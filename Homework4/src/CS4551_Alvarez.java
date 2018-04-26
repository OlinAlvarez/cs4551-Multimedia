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
		Image refImg = new Image(args[0]);
		Image tarImg = new Image(args[1]);
		do {
			System.out.println("Enter n value (8,16,24) : ");
			n = in.nextInt();
		}while(!nSet.contains(n));
		do {
			System.out.println("Enter p value(4,8,12,16) : ");
			p = in.nextInt();
		}while(!pSet.contains(p));

	 	PixelMatrix[][] macroblocks = getBlocks(tarImg);
	 	//testBlocks(macroblocks);
	 	rebuildImage(macroblocks);
	 	getMotionVectors(macroblocks, refImg);
	}
	public static void getMotionVectors(PixelMatrix[][] macroblocks, Image refImage) {
		for (int i = 0; i < macroblocks.length; i++) {
			for (int j = 0; j < macroblocks[i].length; j++) {
				for(int y = 0; y < n; y++) {
					for(int x = 0; x < n; x++) {
						
					}
				}
			}
		}	
	}
	public static List<PixelMatrix> getPSearchBlocks(int x, int y, Image reference){
		List<PixelMatrix> matrices = new ArrayList<>();
		int startX, endX, startY, endY, runningTotal = 0;
		
		if(x - p < 0) startX = 0;
		else startX = x - p;
		if(y - p < 0) startY = 0;
		else startY = y - p;
		
		if(x + p > reference.getW()) endX = reference.getW();
		else endX = x + p;
		if(y + p > reference.getW()) endY = reference.getH();
		else endY = y + p;
		
		PixelMatrix tempMatrix;
		int mRow = 0, mCol = 0;
		int[] pixel;
		while(startX + n < endX && startY + n < endY) {	
			tempMatrix = new PixelMatrix(n, n);
			mRow = 0;
			mCol = 0;
		
			for(int j = startY; j < startY + p; j++) {
				pixel = new int[3];
				for(int i = startX; i < startX + p; i++) {
					reference.getPixel(i, j, pixel);
					tempMatrix.setPixel(mRow, mCol++,pixel);
				}
				mRow++;
				mCol = 0;
			}
			matrices.add(tempMatrix);
		}
		return matrices;
	}
	public static float blockComparison(PixelMatrix target, PixelMatrix reference) {
		int total = 0;
		for(int i = 0; i < target.rows; i++) {
			for(int j = 0; j < target.cols; j++) {
				total += meanSquareDif(target.getPixel(i, j), reference.getPixel(i, j));
			}
		}
		return total / (target.rows * target.cols);
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
		msd /= n * n;
		return msd;
	}
	public static PixelMatrix[][] getBlocks(Image img){
		img.display();
		PixelMatrix[][] mtx =  new PixelMatrix[img.getW() / n][img.getH() / n];
		for(int i = 0; i < mtx.length; i++) {
			for (int j = 0; j < mtx[0].length; j++) {
				mtx[i][j] = new PixelMatrix(n,n);
			}
		}
		int blockRow = 0, blockCol = 0;
		for(int i = 0; i < mtx.length; i++){
			for(int j = 0; j < mtx[0].length; j++){
					for(int y = 0; y < n; y++){
						for(int x = 0; x < n; x++){
							int[] pixel = new int[3];
							img.getPixel(blockCol + x, blockRow + y, pixel);
							mtx[i][j].setPixel(x, y, pixel);
						}
					}
					blockCol += n;
					if(blockCol >= img.getW()) {
						blockCol = 0;
						blockRow += n;
					}
			}
		}
		return mtx;
	}
	public static int meanSquareDif(int[] a, int[] b) {
		return(int) Math.pow(grayValue(a)-grayValue(b), 2);
	}
	public static int grayValue(int[]  pixel) {
		int r = pixel[2];
		int g = pixel[1];
		int b = pixel[0];
		return (int) Math.round(0.299 *  r + 0.587 * g + 0.114 * b);
	}
	public static void testBlocks(PixelMatrix[][] mtx){
			Image test = new Image(n,n);
			System.out.printf("number of blocks %d\n", mtx.length * mtx[0].length);
			
			for(int i = 0; i < mtx.length; i++){
				for(int j = 0; j < mtx[0].length; j++){
					if(i == j){
						for(int y = 0; y < n; y++){
							for(int x = 0; x < n; x++){
								test.setPixel(x, y, mtx[i][j].matrix[x][y]);
							}
						}
						test.display();
					}
				}
			}
	}
	public static void rebuildImage(PixelMatrix[][] mtx) {
		Image img = new Image( n * mtx.length, n * mtx[0].length);
		int blockRow = 0, blockCol = 0;

		for(int i = 0; i < mtx.length; i++){
			for(int j = 0; j < mtx[0].length; j++){
					for(int y = 0; y < n; y++){
						for(int x = 0; x < n; x++){
							img.setPixel(blockCol + x, blockRow + y,mtx[i][j].matrix[x][y]);
						}
					}
					blockCol += n;
					if(blockCol >= img.getW()) {
						blockCol = 0;
						blockRow += n;
					}
			}
		}
		img.display();
		img.write2PPM("output.ppm");
	}
}
