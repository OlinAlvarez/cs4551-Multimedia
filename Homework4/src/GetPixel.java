import java.util.Scanner;
public class GetPixel{
    
    public static void main(String[] args){
        Image img = new Image("IDB/Walk_001.ppm");
        Scanner in = new Scanner(System.in);
        int x, y, n = 0;
        int[] pixel = new int[3];
        do{
            System.out.print("\nenter x: "); 
            x = in.nextInt();
            System.out.print("\nenter x: "); 
            y = in.nextInt();
            img.getPixel(x,y,pixel); 
            printarr(pixel);
            System.out.print("\nagain?"); 
            n = in.nextInt();
            
        }while(n != 1);
    }
    public static void printarr(int[] nums){
        for(int n = 0; n < nums.length; n++){
            System.out.print(nums[n] + " "); 
        }
            System.out.println(); 
    }
}
