/*******************************************************
G CS4551 Multimedia Software Systems @ Author: Olin-Mao Alvarez 
*******************************************************/
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import javax.management.modelmbean.ModelMBeanOperationInfo;

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
			//rebuildImage(macroblocks);
			int[][][] motionVectors =  getMotionVectors(macroblocks, refImg);
//			writeMotionVectors(motionVectors,args[0], args[1],"mv.txt");	 	
			getResidualImage(macroblocks,motionVectors,refImg);
		}
		public static void getResidualImage(PixelMatrix[][] macroblocks, int[][][] motionVectors, Image reference) {
			Image res = new Image(reference.getW(),reference.getH());
				
			for (int y = 0; y <res.getH(); y++) {
				for (int x = 0; x < macroblocks.length; x++) {
				}
			}
				
			res.display();
			res.write2PPM("res.ppm");
		}
		public static int[][][] getMotionVectors(PixelMatrix[][] macroblocks, Image refImage) {
			int[][][] motionVectors =  new int[macroblocks.length][macroblocks[0].length][2];
			for (int y = 0; y < macroblocks[0].length; y++) {
				for (int x = 0; x < macroblocks.length; x++) {
					motionVectors[x][y] = motionVector(x * n, y * n,macroblocks[x][y],refImage);
				}
			}
			return motionVectors;
		}	
	    public static int[] motionVector(int x, int y, PixelMatrix block, Image reference){
			int xOffset, endX, yOffset, endY;
			
			if(x - p < 0) xOffset = 0;
			else xOffset = x - p;
			if(y - p < 0) yOffset = 0;
			else yOffset = y - p;
			int input;
		if(x + p  + n> reference.getW()) endX = reference.getW();
		else endX = x + p + n;
		if(y + p + n > reference.getW()) endY = reference.getH();
		else endY = y + p + n;
		//System.out.printf("(%d,%d) motion vector from (%d,%d) -> (%d,%d)\n",x,y,xOffset,yOffset,endX,endY);	
		int[] pixel;
        int[] motionVector = new int[2];
        int total = 0, min = 2147483647;
		while(xOffset + n < endX && yOffset + n < endY) {	
		
			for(int j = 0; j < n; j++) {
				pixel = new int[3];
				for(int i = 0; i < n; i++) {
				    reference.getPixel(i + xOffset, j + yOffset, pixel);
				    total += meanSquareDif(block.getPixel(i, j), pixel);
				}
			}
			if(total < min) {
				min = total;
				motionVector[0] = x - xOffset;
				motionVector[0] = y - yOffset;	
			}
			xOffset++;
			if(xOffset > endX - n) {
				xOffset = 0;
				yOffset++;
			}
        }    
	int v1  =  motionVector[0];	
	int v2  =  motionVector[1];	
	System.out.printf("msd %d motion vector (%d,%d)\n",min,v1,v2);
	//System.out.printf("motion vector (%d, %d) : %d,%d\n",x,y,v1,v2);
        return motionVector;
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
	public static void writeMotionVectors(int[][][] motionvectors,String target, String reference, String filename) throws IOException{
		System.out.println("Writting mv.txt file");
		int rows = motionvectors.length;
		int cols = motionvectors[0].length;
		File encodedText = new File(filename);
		FileWriter fw = new FileWriter(encodedText);
		BufferedWriter writer = new BufferedWriter(fw);
//		writer.write("");
		writer.write("#Name: Olin-Mao Alvarez\n");
		writer.write("#Target image name: " + target + '\n');
		writer.write("#Reference image name: " + reference + '\n');
		writer.write("#Number of target macro blocks: "
			+ rows + " x  " + cols + " (image size is " 
			+ rows * n + " x " + cols * n  + ")\n");
		
		for(int row = 0; row < rows; row++){
			for(int col = 0; col < cols; cols++){
				writer.write("[ " + motionvectors[row][col][0]
						+ ", " + motionvectors[row][col][1] + "] ");
			}
			writer.write('\n');
		}

		writer.close();
		System.out.println(filename + " written");
	}
}
