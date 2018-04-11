/*******************************************************
 CS4551 Multimedia Software Systems
 @ Author: Elaine Kang
 *******************************************************/

//
// Template Code - demonstrate how to use Image class

public class onlyRed 
{
  public static void main(String[] args)
  {
	// if there is no commandline argument, exit the program
    if(args.length != 1)
    {
      usage();
      System.exit(1);
    }

    System.out.println("--Welcome to Multimedia Software System--");

    // Create an Image object with the input PPM file name.
    // Display it and write it into another PPM file.
    Image img = new Image(args[0]);
    img.changeChannel(1,0,0);
    img.display();
    img.write2PPM("out.ppm");

    System.out.println("--Good Bye--");
  }

  public static void usage()
  {
    System.out.println("\nUsage: java CS4551_Main [input_ppm_file]\n");
  }
}
