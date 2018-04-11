/*******************************************************
 CS4551 Multimedia Software Systems
 @ Author: Olin-Mao Alvarez 
 *******************************************************/
import java.util.Scanner;
public class CS4551_Alvarez{
  static Scanner in = new Scanner(System.in);
  public static void main(String[] args){
    if(args.length != 1){
      usage();
      System.exit(1);
    }
    ConvertibleImage img = new ConvertibleImage(args[0]);

    int choice;
    do{
        choice = displayMenu();
        if(choice > 5 || choice < 0){
            System.out.println("error invalid choice!");
        }
        switch(choice){
            case 1: img.cvtGrayScale(img);
                break;
            case 2: cvtNLevel(img);
                break;
            case 3: cvt8bitIndexedUCQ(img);
                break;
            case 4: cvt8bitIndexed(img);
                break;
            case 5: System.exit(0);
                break;
        }
    }while(choice != 5);
  }

  public static void usage(){
    System.out.println("\nUsage: java CS4551_Main [input_ppm_file]\n");
  }
  public static int displayMenu(){
    
    System.out.println("Main Menu------------------------");
    System.out.println("1. Conversion to Gray-Scale image(24bits -> 8bits)");
    System.out.println("2. Conversion to N-Level Image");
    System.out.println("3. Conversion to 8bit Indexed Color using" +
                        "\nUniform COlor Quantization(24bits -> 8bits");
    System.out.println("4. Conversion to 8bit inexed color image using [!!change!!]");
    System.out.println("5. Quit");
    return in.nextInt(); 
  }
}
