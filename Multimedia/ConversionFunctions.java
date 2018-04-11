public class ConvertableImage extends Image{
    
    public static void cv2GrayScale(Image img){
        for(int y = 0; y < img.getH(); y++){
            for(int x = 0; x < img.getW(); x++){
                int pix = img.getRGB(x,y);
            }
        }
    }
}
