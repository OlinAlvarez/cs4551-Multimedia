import java.lang.Math;
public class ConvertibleImage extends Image{
    
    public ConvertibleImage(){}  
    
    public static void cv2GrayScale(Image img){
    	Image grayImg = new Image();
        for(int y = 0; y < img.getH(); y++){
           for(int x = 0; x < img.getW(); x++){
                int pix = img.getRGB(x,y);
                int b = (byte) pix;
                int g = (byte) (pix >> 8);
                int r = (byte) (pix >> 16);
                int gray =  Math.round(0.299 * r + 0.587 * g + 0.114 * b);
                System.out.println( x + ',' y + gray);
            }
        }
    } asdflj.
}
