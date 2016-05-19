package fun.wxy.test;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.DoublePointer;

import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_imgcodecs.*;
import static org.bytedeco.javacpp.opencv_highgui.*;
import static org.bytedeco.javacpp.opencv_imgproc.*;


/**
 * Created by 0_o on 2016/5/16.
 */
public class Test {

    public void cv1() {
        IplImage big = cvLoadImage("e:\\ss1.png");
        IplImage small = cvLoadImage("e:\\blue.png");

        System.out.println(big);
        System.out.println(small);
        IplImage result = cvCreateImage(cvSize(big.width() - small.width() + 1, big.height() - small.height() + 1), IPL_DEPTH_32F, 1);
        cvZero(result);


        cvMatchTemplate(big, small, result, CV_TM_CCORR_NORMED);
        CvPoint maxLoc = new CvPoint();
        CvPoint minLoc = new CvPoint();
        DoublePointer maxDp = new DoublePointer(new double[2]);
        DoublePointer minDp = new DoublePointer(new double[2]);

        // return only max and min match
        cvMinMaxLoc(result, minDp, maxDp, minLoc, maxLoc, null);
        // is "small" exist in "big"
        //boolean matchRes = maxDp.get(0) > 0.99f ? true : false;
        //System.out.println(maxDp.get(0));
        // draw rectangle block
        //cvRectangle(big, cvPoint(maxLoc.x(), maxLoc.y()), cvPoint(maxLoc.x() + small.width(), maxLoc.y() + small.height()), CV_RGB(0, 0, 0), -1, 8, 0);

        //cvNamedWindow("big", 1);
        //cvShowImage("big", big);
        cvWaitKey(0);
    }


    public static void main(String[] args) {

    }
}
