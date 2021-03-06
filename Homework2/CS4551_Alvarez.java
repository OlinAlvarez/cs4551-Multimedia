/*******************************************************
 CS4551 Multimedia Software Systems
 @ Author: Olin-Mao Alvarez 
 *******************************************************/
import java.util.LinkedHashMap;
import java.util.Scanner;
import java.io.*;
import java.lang.Math;
import java.nio.charset.Charset;
public class CS4551_Alvarez{ static Scanner in = new Scanner(System.in);
public static void main(String[] args) throws IOException{

    int choice;
    do{
        choice = displayMenu();
        if(choice > 3 || choice < 0){
            System.out.println("error invalid choice!");
        }
        switch(choice){
            case 1: aliasing();
                break;
            case 2: dictionaryCoding();
                break;
            case 3: System.exit(0);
                break;
        }
    }while(choice != 3);
  }

  public static void usage(){
    System.out.println("\nUsage: java CS4551_Main [input_ppm_file]\n");
  }
  public static int displayMenu(){
    
    System.out.println("-------Main Menu-------");
    System.out.println("1. Aliasing");
    System.out.println("2. Dictionary Coding");
    System.out.println("3. Quit");
    return in.nextInt(); 
  }
  
  public static void aliasing(){
    Image img = new Image(512,512);
    img.setBlank();
    int center =  512 / 2;
    int defaultRadius =  5;
    
    System.out.print("Enter thickness of circles: ");
    int thickness = in.nextInt();
        
    System.out.print("\nEnter radii distance of circles: ");
    int radius = in.nextInt();
    radius += defaultRadius; 
    int innerRadius = radius;
    
    int x, y, outerRadius, distance;
    int[] blackPixel = {0,0,0};
    double theta = 0.0;
    while(center + radius + thickness < 512) {
    	
    	outerRadius = radius + thickness;
    	
    	while(radius < outerRadius ) {
    		theta = 0.0;
    		while(theta <= 90) {
				x =  (int) ((int) radius * Math.cos(theta));
				y =  (int) ((int) radius * Math.sin(theta));
				
				if(x < 512 && y < 512) {
					img.setPixel(center + x, center + y, blackPixel);    		
					img.setPixel(center + x, center - y, blackPixel);
					img.setPixel(center - x, center + y, blackPixel);
					img.setPixel(center - x, center - y, blackPixel);
					
				}
				theta += 0.01;
			}
			radius++;
    	}
    	
    	radius +=  innerRadius;
    }
    
    img.write2PPM("circles");
    img.display();
    int k;
   
    do {
		System.out.println("Enter K: ");
		k = in.nextInt();
    }while(k < 2 ||  k > 16  || (k & (k - 1)) != 0);
    
    reduceImageNoFilter(img,k);
    reduceImageFilter1(img,k);
    reduceImageFilter2(img,k);
    
 }
  public static void reduceImageNoFilter(Image img, int k) {
	  Image reducedImage = new Image(512/k, 512/k);
	  int[] pixel =  new int[3];
	  for(int y = 0; y < reducedImage.getH(); y++) { 
		 for(int x = 0; x < reducedImage.getW(); x++) {
			 img.getPixel(x * k, y * k, pixel);
			 reducedImage.setPixel(x,y,pixel);
		 } 
	  }
	  reducedImage.display();
	  reducedImage.write2PPM("reduced_circles_nofilter");
  }
  //Helper Function for y position 
  public static int getYPlacement(int x, int r) {
	 return (int) Math.sin(Math.acos( x / r));
  } 
  
  public static void reduceImageFilter1(Image img, int k) {
	  Image reducedImage = new Image(512/k, 512/k);
	  int[] pixel =  new int[3];
	  
	  int[] f1 =  new int[3];
	  int[] f2 =  new int[3];
	  int[] f3 =  new int[3];
	  int[] f4 =  new int[3];
	  int[] f5 =  new int[3];
	  int[] f6 =  new int[3];
	  int[] f7 =  new int[3];
	  int[] f8 =  new int[3];
	  int[] c =  new int[3];
	  for(int y = 1; y < reducedImage.getH()-1; y++) { 
		 for(int x = 1; x < reducedImage.getW()-1; x++) {
			 int oldX = x * k, oldY = y * k;
			 
			 img.getPixel(oldX,oldY, c);
			 img.getPixel(oldX - 1,oldY - 1, f1);
			 img.getPixel(oldX,oldY - 1, f2);
			 img.getPixel(oldX + 1,oldY - 1, f3);
			 img.getPixel(oldX - 1,oldY, f4);
			 img.getPixel(oldX + 1,oldY, f5);
			 img.getPixel(oldX -1,oldY + 1, f6);
			 img.getPixel(oldX,oldY + 1, f7);
			 img.getPixel(oldX + 1,oldY + 1, f8);
			 
			 for (int l = 0; l < c.length; l++) {
				 pixel[l] = c[l] + f1[l] + f2[l] + f3[l] + f4[l] + f5[l] + f6[l] + f7[l] + f8[l];
				 pixel[l] /= 9;
			}
			 reducedImage.setPixel(x,y,pixel);
		 } 
	  }
	  
	  reducedImage.display();
	  reducedImage.write2PPM("reduced_circles_filter1"); 
  }
  public static void reduceImageFilter2(Image img, int k) {
	  Image reducedImage = new Image(512/k, 512/k);
	  double[][] kernel2 = {{1/16,2/16,1/16},{2/16,4/16,2/16},{1/16,2/16,1/16}};
	  int[] f1 =  new int[3];
	  int[] f2 =  new int[3];
	  int[] f3 =  new int[3];
	  int[] f4 =  new int[3];
	  int[] f5 =  new int[3];
	  int[] f6 =  new int[3];
	  int[] f7 =  new int[3];
	  int[] f8 =  new int[3];
	  int[] c =  new int[3];
	  int[] pixel = new int[3];
	  
	  for(int y = 1; y < reducedImage.getH()-1; y++) { 
		 for(int x = 1; x < reducedImage.getW()-1; x++) {
			 int oldX = x * k, oldY = y * k;
			 
			 img.getPixel(oldX,oldY, c);
			 img.getPixel(oldX - 1,oldY - 1, f1);
			 img.getPixel(oldX,oldY - 1, f2);
			 img.getPixel(oldX + 1,oldY - 1, f3);
			 img.getPixel(oldX - 1,oldY, f4);
			 img.getPixel(oldX + 1,oldY, f5);
			 img.getPixel(oldX -1,oldY + 1, f6);
			 img.getPixel(oldX,oldY + 1, f7);
			 img.getPixel(oldX + 1,oldY + 1, f8);
			 
			 for (int l = 0; l < c.length; l++) {
				 pixel[l] =(int) ((int) c[l]* (0.25) + f1[l] * (0.0625) + f2[l] * (0.125) + f3[l] * (0.0625) + 
						 f4[l] * (0.125) + f5[l] * (0.125) + f6[l] * (0.0625) + f7[l] * (0.125) + f8[l] * (0.0625));
			}
			 reducedImage.setPixel(x,y,pixel);
		 } 
	  }
	  reducedImage.display();
	  reducedImage.write2PPM("reduced_circles_filter2");  
	  
  }
  public static void dictionaryCoding() throws IOException{
	  	in = new Scanner(System.in);
		System.out.println("Enter Filename: ");
		String filename = in.nextLine();
		
		InputStream is = new FileInputStream(new File(filename));
		Reader reader = new InputStreamReader(is, Charset.defaultCharset());
		BufferedReader filereader = new BufferedReader(reader);
		
		int mapsize = 256;
		
		
		LinkedHashMap<String,Integer> map =  new LinkedHashMap<>();
		StringBuilder fileContent = new StringBuilder();
		StringBuilder encodedString =  new StringBuilder();
	
		//initializing the dictionary
		int c, index = 0;
		String curr;
		while( (c = filereader.read()) != -1) {
			curr = "" + (char) c;
			if(!map.containsKey(curr)) {
				map.put(curr, index);
				index++;
			}
			fileContent.append(curr);
		}
		
		String p = ""; 
		double dataSizeBeforeCompression = 1;
		for(Character ch: fileContent.toString().toCharArray()) {
				
				dataSizeBeforeCompression++;
				String currString = p + ch;
			
				if(map.containsKey(currString)) {
					p =  currString;
				} else {
					encodedString.append(map.get(p) + " ");
					if(index < 256)	map.put(currString, index++);
					p = "" + ch;
				}
			
		}
		
		
		if(!p.equals(""))
			encodedString.append(map.get(p));
		StringBuilder decodedString = decode(map,encodedString.toString());
		dataSizeBeforeCompression *= 8;
		double dataSizeAfterCompression = encodedString.toString().toCharArray().length * 8;
		double ratio = dataSizeAfterCompression / dataSizeBeforeCompression;
		writeFile(filename,fileContent,map,encodedString,decodedString,ratio);
		
		System.out.println("encoded contents \n" + encodedString.toString());
		printMap(map);
		System.out.println("compression ratio: " + ratio);
		System.out.println("decoded contents \n" + decodedString.toString());
		
		
  }
  public static void printMap(LinkedHashMap<String,Integer> map) {
	System.out.println("\n\n--Dictionary Contents: \n\n");
	System.out.println("Index-----Value \n");

	for(String s: map.keySet()) {
		System.out.println(" " + map.get(s) + "\t   " + s + '\n');
	}
  }
  
  /*
   * The way the decoder works is by creating an array of strings
   * containing the values of the dictionary and saving them to their indices
   * the when the encoded string is read the value of the index is appened to a string
   * */
  public static StringBuilder  decode(LinkedHashMap<String,Integer> map, String encoded) {
	  String[] dictionary =  new String[map.size()];
	  
	  int i = 0;
	  for(String st: map.keySet()) {
		 dictionary[i++] = st;
	  }
	  StringBuilder decodedString = new StringBuilder();
	  String[] numbers = encoded.split(" ");
	  
	  for(i = 0; i < numbers.length; i++) {
		 decodedString.append(dictionary[Integer.parseInt(numbers[i])]) ;
	  }
	  return decodedString;
  }
  public static void writeFile(String filename, StringBuilder fileContent, LinkedHashMap<String,Integer> dict, StringBuilder encoded, StringBuilder decoded,double ratio) throws IOException{
		File encodedText = new File("Dict-Coding-"+filename);
		FileWriter fw = new FileWriter(encodedText);
		BufferedWriter writer = new BufferedWriter(fw);
		
		writer.write(filename + "\n");
		
		writer.write("\n Original Content:");
		writer.write("\n--------------------\n");		
		writer.write(fileContent.toString());
		
		writer.write("\n\n--Dictionary Contents: \n\n");
		writer.write("Index-----Value \n");
		
		for(String s: dict.keySet()) {
			writer.write(" " + dict.get(s) + "\t   " + s + '\n');
		}
		
		writer.write("\n Encoded text:");
		writer.write("\n--------------------\n");		
		writer.write(encoded.toString());
		writer.write("\n Decoded text:");
		writer.write("\n--------------------\n");		
		writer.write(decoded.toString());
		
		writer.write("\n\n Compression ratio: " + ratio);
		writer.close();
		
		
		
		
  }
}