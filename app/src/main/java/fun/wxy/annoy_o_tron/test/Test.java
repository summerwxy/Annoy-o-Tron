package fun.wxy.annoy_o_tron.test;

//import org.bytedeco.javacpp.opencv_core;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by 0_o on 2016/5/16.
 */
public class Test {

    public static void main(String[] args) {
        System.out.println("123");
        //opencv_core.IplImage big = org.bytedeco.javacpp.opencv_imgcodecs.cvLoadImage("d:/ss1.png");
        //opencv_core.IplImage small = org.bytedeco.javacpp.opencv_imgcodecs.cvLoadImage("d:/blue.png");

        //System.out.println(big);
        //System.out.println(small);
        // opencv_core.IplImage result = org.bytedeco.javacpp.opencv_core.cvCreateImage(, 1, 1);

        Test test = new Test();
        List<Integer> list = test.getList();
    }



    public List getList() {
        List list = new ArrayList();
        list.add("foo");
        return list;
    }

}
