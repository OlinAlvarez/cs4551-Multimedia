/*******************************************************
 CS4551 Multimedia Software Systems
 @ Author: Olin-Mao Alvarez 
 *******************************************************/
import java.util.Scanner;
class CS4551_Alvarez{
	
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
        if(choice > 4 || choice < 0){
            System.out.println("error invalid choice!");
        }
        switch(choice){
            case 1: img.cvtGrayScale();
                break;
            case 2: cvtNLevel(img);
                break;
            case 3: img.cvt8bitIndexedUCQ();
                break;
            case 4: System.exit(0);
                break;
        }
    }while(choice != 4);
  }

  public static void usage(){
    System.out.println("\nUsage: java CS4551_Main [input_ppm_file]\n");
  }
  public static int displayMenu(){
    
    System.out.println("Main Menu------------------------");
    System.out.println("1. Conversion to Gray-Scale image(24bits -> 8bits)");
    System.out.println("2. Conversion to N-Level Image");
    System.out.println("3. Conversion to 8bit Indexed Color using" +
                        "\nUniform Color Quantization(24bits -> 8bits");
    System.out.println("4. Quit");
    return in.nextInt(); 
  }
  public static void cvtGrayScale(Image img){}
  public static void cvtNLevel(ConvertibleImage img){
  	int levels;
  	do {
  		System.out.println("\n Enter desired N level: ");
  		levels = in.nextInt();
  		if((levels & (levels - 1)) != 0 || levels < 2 || levels > 16)
  			System.out.println("Error invalid N level");
  	}while((levels & (levels - 1)) != 0|| levels < 2 || levels > 16);
  	img.cvtNLevel(levels);
  }
}
