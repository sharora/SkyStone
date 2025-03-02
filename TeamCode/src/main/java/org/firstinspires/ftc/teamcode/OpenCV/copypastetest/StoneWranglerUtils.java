package org.firstinspires.ftc.teamcode.OpenCV.copypastetest;

import android.util.Log;

import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class StoneWranglerUtils {

    // ------------------------------ mat cv ops ------------------------------

    static Mat warpPerspective(Mat src, Size size, Mat homography) {
        Mat projected = new Mat(size, CvType.CV_8UC3);
        Imgproc.warpPerspective(src, projected, homography, size);
        return projected;
    }

    static Mat add(Mat a, Mat b) {
        Mat result = new Mat(b.size(), CvType.CV_8UC3);
        Core.add(a, b, result);
        return result;
    }

    static Mat verticalFlip(Mat mat) {
        Core.flip(mat, mat, 0);
        return mat;
    }

    static Mat denoiseMat(Mat src) {
        Mat denoised = new Mat(src.rows(), src.cols(), CvType.CV_8U, new Scalar(3));
        Imgproc.GaussianBlur(src, denoised, StoneWranglerConstants.GAUSSIAN_DENOISE_K, 0);
        return denoised;
    }

    static Mat maskByHSVThreshold(Mat src) {
        Mat hsvFrame = new Mat(src.rows(), src.cols(), CvType.CV_8U, new Scalar(3));
        Imgproc.cvtColor(src, hsvFrame, Imgproc.COLOR_BGR2HSV, 3);
        Mat mask = new Mat(hsvFrame.rows(), hsvFrame.cols(), CvType.CV_8U, new Scalar(3));
        Core.inRange(hsvFrame, StoneWranglerConstants.STONE_HSV_LOWER, StoneWranglerConstants.STONE_HSV_UPPER, mask);
        return mask;
    }

    static Mat convert(Mat src, int conversion) {
        Mat dst = new Mat();
        Imgproc.cvtColor(src, dst, conversion);
        return dst;
    }

    static List<Scalar> houghLinesMatToList(Mat lines) {
        List<Scalar> result = new ArrayList<>();
        for (int x = 0; x < lines.rows(); x++) {
            double theta = lines.get(x, 0)[1];
            double rho   = lines.get(x, 0)[0];
            result.add(new Scalar(theta, rho));
        }
        return result;
    }

    // ------------------------------ mat drawing ops ------------------------------

    static void drawLine(Mat dst, double theta, double rho, Scalar color, int thickness) {
        double a = Math.cos(theta);
        double b = Math.sin(theta);
        double x0 = a * rho, y0 = b * rho;
        Point pt1 = new Point(Math.round(x0 + 1000 * -b), Math.round(y0 + 1000 * a));
        Point pt2 = new Point(Math.round(x0 - 1000 * -b), Math.round(y0 - 1000 * a));
        Imgproc.line(dst, pt1, pt2, color, thickness, Imgproc.LINE_AA, 0);
    }

    // ------------------------------ line stuff ------------------------------

    static List<Scalar> integerPointsAlongLine(double theta, double rho, int width, int height) {
        List<Scalar> result = new ArrayList<>();
        double lineSlopeAngle = theta + Math.PI * 0.5;
        if (Math.abs(Math.tan(lineSlopeAngle)) < 1) {
            double pointX    = Math.cos(theta) * rho;
            double pointY    = Math.sin(theta) * rho;
            double lineSlope = Math.tan(lineSlopeAngle);
            double y = pointY - lineSlope * pointX;  // start at the y intercept
            for (int x = 0; x < width; x++, y += lineSlope)
                if (Math.round(y) >= 0 && Math.round(y) < height)
                    result.add(new Scalar(Math.round(x), Math.round(y)));
        } else {
            double pointX    =     Math.cos(theta) * rho;
            double pointY    =     Math.sin(theta) * rho;
            double lineSlope = 1 / Math.tan(lineSlopeAngle);
            double x = pointX - lineSlope * pointY;  // start at the x intercept
            for (int y = 0; y < height; y++, x += lineSlope)
                if (Math.round(x) >= 0 && Math.round(x) < width)
                    result.add(new Scalar(Math.round(x), Math.round(y)));
        }
        return result;
    }

    static Scalar findLineCenter(Mat stoneMask, double theta, double rho) {
        List<Double>
                xValues = new ArrayList<>(),
                yValues = new ArrayList<>();
        for (int i = -2; i <= 2; i++) {
            List<Scalar> points = StoneWranglerUtils.integerPointsAlongLine(theta, rho + i, stoneMask.width(), stoneMask.height());
            for (Scalar s : points) {
                if (stoneMask.get((int) s.val[1], (int) s.val[0])[0] > 0) {
                    xValues.add(s.val[0]);
                    yValues.add(s.val[1]);
                }
            }
        }
        return new Scalar(StoneWranglerUtils.median(xValues), StoneWranglerUtils.median(yValues));
    }

    // ------------------------------ binning system ------------------------------

    static List<List<Scalar>> binLines(List<Scalar> lines) {
        List<List<Scalar>> bins = new ArrayList<>();
        for (Scalar line : lines) {  // for every line to be sorted
            List<Scalar> assignedBin = null;
            for (List<Scalar> bin : bins)  // for every potential bin
                for (Scalar compare : bin)  // for every scalar in that potential bin
                    if (closeEnough(line.val[0], line.val[1], compare.val[0], compare.val[1])) {  // if they are close
                        if (assignedBin == null) {  // if the line hasn't been binned yet
                            bin.add(line);
                            assignedBin = bin;
                        } else {  // if the line is in a bin already
                            assignedBin.addAll(bin);  // move everything from the examined bin to the line's bin
                            bin.clear();
                        }
                        break;
                    }
            if (assignedBin == null)  // if a matching bin hasn't been found
                bins.add(new ArrayList<>(Collections.singletonList(line)));
        }
        return bins;
    }

    static List<Scalar> binMedians(List<List<Scalar>> bins) {
        List<Scalar> binMedians = new ArrayList<>();
        for (List<Scalar> bin : bins) {
            List<Double> thetas = new ArrayList<>();
            List<Double> rhos = new ArrayList<>();
            for (Scalar s : bin) {
                thetas.add(s.val[0]);
                rhos.add(s.val[1]);
            }
            binMedians.add(new Scalar(StoneWranglerUtils.median(thetas), StoneWranglerUtils.median(rhos)));
        }
        return binMedians;
    }

    static boolean closeEnough(double theta1, double rho1, double theta2, double rho2) {
        return Math.abs(theta1 - theta2) < StoneWranglerConstants.CLOSE_ENOUGH_THETA
                && Math.abs(rho1 - rho2) < StoneWranglerConstants.CLOSE_ENOUGH_RHO;
    }

    // ------------------------------ misc ------------------------------

    static double median(List<Double> list) {
        if (list.size() == 0)
            return -1;
        Collections.sort(list);
        if (list.size() % 2 == 0)
            return (list.get(list.size() / 2) + list.get(list.size() / 2 - 1)) / 2;
        else
            return list.get(list.size() / 2);
    }

    static MatOfPoint2f createMatOfPoint2f(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4) {
        MatOfPoint2f result = new MatOfPoint2f();
        result.fromArray(new org.opencv.core.Point(x1, y1), new org.opencv.core.Point(x2, y2),
                new org.opencv.core.Point(x3, y3), new org.opencv.core.Point(x4, y4));
        return result;
    }
    static void log(String message) {
        Log.w("opencv-sl", message);
    }
}