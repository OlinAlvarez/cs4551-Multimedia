import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.image.*;
import javax.imageio.stream.FileImageInputStream;

public class ConvertibleImage extends Image{
    private String filename;
    private BufferedImage img;

    public ConvertibleImage(){} 
    public ConvertibleImage(String fn){
        this.filename = fn;
        super.readPPM(fn);
    }    
    public static void cvtGrayScale(Image img){
        Image gray = new Image(this.fn + "_gray");
        System.out.println("new image created");
        for(int y = 0; y < img.getH(); y++){
            for(int x = 0; x < img.getW(); x++){
                int pix = img.getRGB(x,y);
                int b = (byte) pix;
                int g = (byte) (pix >> 8);
                int r = (byte) (pix >> 16);
                int gray =  Math.round(0.299 * r + 0.587 * g + 0.114 * b);
                System.out.println( x + ',' + y + gray);
            }
        }
    }
    public static void cvtNLeve(Image img){
    
    }
    public static void cvt8bitIndexedUCQ(Image img){
    
    }
    public static void cv28bitIndexed(Image img){
    
    }
}
