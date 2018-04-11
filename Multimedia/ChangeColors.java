/*******************************************************
 CS4551 Multimedia Software Systems
 @ Author: Elaine Kang
 *******************************************************/

//
// Template Code - demonstrate how to use Image class

public class ChangeColors 
{
  public static void main(String[] args)
  {
	// if there is no commandline argument, exit the program
    if(args.length != 4)
    {
      usage();
      System.exit(1);
    }

    System.out.println("--Welcome to Multimedia Software System--");

    // Create an Image object with the input PPM file name.
    // Display it and write it into another PPM file.
    Image img = new Image(args[0]);
    int r = Integer.parseInt(args[1]);
    int g = Integer.parseInt(args[2]);
    int b = Integer.parseInt(args[3]);
    img.changeChannel(r,g,b);
    img.display();
    img.write2PPM("out.ppm");

    System.out.println("--Good Bye--");
  }

  public static void usage()
  {
    System.out.println("\nUsage: java CS4551_Main [input_ppm_file]\n");
  }
}
