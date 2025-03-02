package org.firstinspires.ftc.teamcode.OpenCV.copypastetest;

import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Scalar;
import org.opencv.core.Size;

@SuppressWarnings("WeakerAccess")
class StoneWranglerConstants {
    final static double  // frame space calibration points
            CALX1F = 629,
            CALY1F = 413,
            CALX2F = 935,
            CALY2F = 412,
            CALX3F = 1067,
            CALY3F = 228,
            CALX4F = 635,
            CALY4F = 228;
    final static double  // world space calibration points
            CALX1W = 0,
            CALY1W = 11 * 3,
            CALX2W = 8.5,
            CALY2W = 11 * 3,
            CALX3W = 8.5,
            CALY3W = 11 * 2,
            CALX4W = 0,
            CALY4W = 11 * 2;
    final static double
            PIXEL_SIZE = 0.1;  // world size of bird's eye view pixel (e.g. .1 inch -> 1 px == .1)
    final static double
            AREA_X_DIMENSION = 24,  // surveyed area's dimensions in world space
            AREA_Y_DIMENSION = 24;
    final static Size
            GAUSSIAN_DENOISE_K = new Size(3, 3);
    final static Scalar
            STONE_HSV_LOWER = new Scalar(16, 177, 25),
            STONE_HSV_UPPER = new Scalar(26, 255, 255);
    final static double
            HOUGH_LINES_RHO_STEP = 2,
            HOUGH_LINES_THETA_STEP = Math.PI / 180;
    final static int
            HOUGH_LINES_THRESHOLD = 10;
    final static Scalar
            RED_SCALAR   = new Scalar(0, 0, 255),
            GREEN_SCALAR = new Scalar(0, 255, 0),
            BLUE_SCALAR  = new Scalar(255, 0, 0);
    final static double
            CLOSE_ENOUGH_THETA = Math.PI * 15 / 180,
            CLOSE_ENOUGH_RHO = 40;

    static MatOfPoint2f getCalF() {
        return StoneWranglerUtils.createMatOfPoint2f(CALX1F, CALY1F, CALX2F, CALY2F, CALX3F, CALY3F, CALX4F, CALY4F);
    }

    static MatOfPoint2f getCalW() {
        double halfXDimensionInPixels = AREA_X_DIMENSION / 2 / PIXEL_SIZE;
        double yDimensionInPixels  = AREA_Y_DIMENSION / PIXEL_SIZE;

        double  calx1w = CALX1W / PIXEL_SIZE + halfXDimensionInPixels,
                calx2w = CALX2W / PIXEL_SIZE + halfXDimensionInPixels,
                calx3w = CALX3W / PIXEL_SIZE + halfXDimensionInPixels,
                calx4w = CALX4W / PIXEL_SIZE + halfXDimensionInPixels;
        double  caly1w = yDimensionInPixels - CALY1W / PIXEL_SIZE,
                caly2w = yDimensionInPixels - CALY2W / PIXEL_SIZE,
                caly3w = yDimensionInPixels - CALY3W / PIXEL_SIZE,
                caly4w = yDimensionInPixels - CALY4W / PIXEL_SIZE;
        return StoneWranglerUtils.createMatOfPoint2f(calx1w, caly1w, calx2w, caly2w, calx3w, caly3w, calx4w, caly4w);
    }
}