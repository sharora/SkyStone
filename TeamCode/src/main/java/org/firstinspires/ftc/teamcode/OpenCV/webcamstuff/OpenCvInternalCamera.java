package org.firstinspires.ftc.teamcode.OpenCV.webcamstuff;

import android.hardware.Camera;

public interface OpenCvInternalCamera extends OpenCvCamera
{
    enum CameraDirection
    {
        FRONT(Camera.CameraInfo.CAMERA_FACING_FRONT),
        BACK(Camera.CameraInfo.CAMERA_FACING_BACK);

        public int id;

        CameraDirection(int id)
        {
            this.id = id;
        }
    }

    enum BufferMethod
    {
        /*
         * Acquires a frame from the camera, processes it, and then waits for the
         * next frame. Causes time to be wasted waiting for the next frame if the
         * pipeline compute time is greater than the frame timing interval.
         */
        SINGLE,

        /*
         * Acquires a frame from the camera, processes it, and then either waits
         * for the next frame from the camera OR reads the next frame from a second
         * buffer which was filled while the first frame was still being processed.
         * This can increase FPS if the pipeline time is greater than the frame
         * timing interval.
         */
        DOUBLE,
    }

    /***
     * Same as {@link #startStreaming(int, int, OpenCvCameraRotation)} except for:
     *
     * @param bufferMethod the method by which frames from the camera hardware are buffered.
     *                     Using single buffering acquires a frame from the camera, processes
     *                     it, and then waits for the next frame from the camera. This works
     *                     fine if the total compute time per frame is less than the camera's
     *                     frame timing interval. However, consider a camera which is able to
     *                     provide frames every 33ms (30FPS) but the pipeline and overhead compute
     *                     time is 40ms. In this case, if we are processing, say, frame 150, then
     *                     frame 151 will be missed because frame 150 was still being processed.
     *                     This would cause ~26ms to be wasted waiting for the next frame, which
     *                     will reduce FPS by a considerable margin. When using double buffering,
     *                     a second buffer is available for the next frame to be dumped into while
     *                     the previous frame is still being processed. Then after that frame is done,
     *                     the next frame can simply be read from the buffer and started through the
     *                     pipeline immediately, rather than wasting time waiting for yet another
     *                     frame.
     */
    void startStreaming(int width, int height, OpenCvCameraRotation rotation, BufferMethod bufferMethod);

    /***
     * Set whether or not the camera's flash should be
     * put into flashlight ("torch") mode, i.e. where
     * it is always on.
     *
     * Note that the camera must be opened before calling
     * this method. Also, if the camera does not support
     * "torch" mode (or it does not even have a flash at all
     * as in the case of a front camera) an exception will
     * be thrown.
     *
     * @param flashlightEnabled whether or not the camera's
     *        flash should be put into flashlight ("torch")
     *        mode, i.e. where it is always on.
     */
    void setFlashlightEnabled(boolean flashlightEnabled);

    /***
     * Set the exposure compensation value of the auto exposure
     * routine. This can allow for (limited) relative exposure
     * adjustment from the automatically determined value.
     *
     * This method cannot be called until the camera has been opened.
     *
     * @param exposureCompensation the exposure compensation value that the auto exposure routine
     *                             should use. Must be in the range returned by {@link #getMaxSupportedExposureCompensation()}
     *                             and {@link #getMinSupportedExposureCompensation()}
     */
    void setExposureCompensation(int exposureCompensation);

    /***
     * Get the maximum exposure compensation value
     * supported by the auto exposure routine.
     *
     * This method cannot be called until the camera
     * has been opened.
     *
     * @return the maximum exposure compensation value
     *         supported by the auto exposure routine.
     */
    int getMaxSupportedExposureCompensation();

    /***
     * Get the minimum exposure compensation value
     * supported by the auto exposure routine.
     *
     * This method cannot be called until the camera
     * has been opened.
     *
     * @return the minimum exposure compensation value
     *         supported by the auto exposure routine.
     */
    int getMinSupportedExposureCompensation();

    /***
     * Locks / unlocks the auto exposure routine. Changes
     * to exposure compensation will still take effect if
     * the exposure has been locked.
     *
     * This method cannot be called until the camera has
     * been opened.
     *
     * @param lock whether the auto exposure routine should be
     *             locked
     */
    void setExposureLocked(boolean lock);

    /***
     * Set the zoom level of the camera. Must be >= 0
     * and <= {@link #getMaxSupportedZoom()}
     *
     * Note that this method cannot be called until
     * streaming is active.
     *
     * @param zoom the zoom level to set the camera to
     */
    void setZoom(int zoom);

    /***
     * Gets the maximum supported zoom level of the camera
     * in the current streaming mode.
     *
     * Note that the value returned *may* be different
     * based on streaming resolution. Because of this,
     * streaming must be active before you call this
     * method.
     *
     * @return the maximum supported zoom level of the camera
     *         in the current streaming mode.
     */
    int getMaxSupportedZoom();

    /***
     * Sets the recording hint parameter of the camera.
     * This tells the camera API that the intent of the
     * application is to record a video. While this is
     * not true for OpenCV image processing, it does seem
     * to make the camera choose to boost ISO before lowering
     * the frame rate.
     *
     * @param hint the recording hint parameter of the camera
     */
    void setRecordingHint(boolean hint);

    /***
     * Set the FPS range the camera hardware should send
     * frames at. Note that only a few ranges are supported.
     * Usually, a device will support (30,30), which will allow
     * you to "lock" the camera into sending 30FPS. This will,
     * however, have the potential to cause the stream to be
     * dark in low light.
     *
     * @param frameTiming the frame timing range the hardware
     *                    should send frames at
     */
    void setHardwareFrameTimingRange(FrameTimingRange frameTiming);

    /***
     * Ask the camera hardware what frame timing ranges it supports.
     *
     * @return an array of FrameTimingRange objects which represents
     *         the frame timing ranges supported by the camera hardware.
     */
    FrameTimingRange[] getFrameTimingRangesSupportedByHardware();

    class FrameTimingRange
    {
        public int min;
        public int max;

        public FrameTimingRange(int min, int max)
        {
            this.min = min;
            this.max = max;
        }

        @Override
        public boolean equals(Object o)
        {
            if(o == null || o.getClass() != this.getClass())
            {
                return false;
            }

            FrameTimingRange objToCompare = (FrameTimingRange)o;

            return min == objToCompare.min && max == objToCompare.max;
        }
    }
}