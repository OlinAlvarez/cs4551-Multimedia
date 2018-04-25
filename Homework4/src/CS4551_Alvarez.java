/*******************************************************
G CS4551 Multimedia Software Systems @ Author: Olin-Mao Alvarez *******************************************************/
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.io.*;
import java.lang.Math;
import java.nio.charset.Charset;
public class CS4551_Alvarez{ 

	static Scanner in = new Scanner(System.in);
	static int Original_Width;
	static int Original_Height;
	static int n,p;
	public static void main(String[] args) throws IOException{
		HashSet<Integer> nSet = new HashSet<>(Arrays.asList(8,16,24));
		HashSet<Integer> pSet = new HashSet<>(Arrays.asList(4,8,12,16));
		
		if(args.length != 2){
		  usage();
		  System.exit(1);
		}
		Image ref_img = new Image(args[0]);
		Image tar_img = new Image(args[1]);
		do {
			System.out.println("Enter n value (8,16,24) : ");
			n = in.nextInt();
		}while(!nSet.contains(n));
		do {
			System.out.println("Enter p value(4,8,12,16) : ");
			p = in.nextInt();
		}while(!pSet.contains(p));
					
		ArrayList<int[][][]> macroblocks = (ArrayList<int[][][]>) getBlocks(tar_img, n);
	}
	public static void usage(){
		System.out.println("\nUsage: java CS4551_Main [input_ppm_file]\n");
	}
	public static float routine(Image current, Image target) {
		float msd = 0;
		int[] currPixel = new int[3];
		int[] tarPixel = new int[3];
		for(int i = 0; i < 16; i++) {
			for (int j = 0; j < 16; j++) {
				current.getPixel(i, j, currPixel);
				target.getPixel(i, j, tarPixel);
				msd += Math.pow(currPixel[1]-tarPixel[1],2);
			}
		}
		msd /= (16 * 16);
		return msd;
	}
	
	public static List<int[][][]> getBlocks(Image img, int n){
		ArrayList<int[][][]> list = new ArrayList<>();
		int blockRow = 0, blockCol = 0;
		
		while(blockRow < img.getH()) {
			int[][][] block =  new int[n][n][3];
			for(int i = 0; i < n; i++) {
				for (int j = 0; j < n; j++) {
					if(blockRow + i < img.getH() && blockCol  + j < img.getW())img.getPixel(blockCol + j, blockRow + i, block[i][j]);;
				}
			}
			list.add(block);
			
			blockCol += n;
			if(blockCol >= img.getW()) {
				blockCol = 0;
				blockRow += n;
			}
		}	
		return list;
	}
	public static int meanSquareDif(int a, int b) {
		return(int) Math.pow(a-b, 2);
	}
	public static int grayValue(int r, int g, int b) {
		return (int) Math.round(0.299 *  r + 0.587 * + 0.114 * b);
	}
}