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
public class Test{

	static Scanner in = new Scanner(System.in);
	static int Original_Width;
	static int Original_Height;
	static int n = 24 ,p = 4;
    public static void main(String[] args){
        HashSet<Integer> nSet = new HashSet<>(Arrays.asList(8,16,24));
		HashSet<Integer> pSet = new HashSet<>(Arrays.asList(4,8,12,16));
        /*
        for(Integer i: nSet){
            n = i;
            for(Integer j: pSet){
             p = j;
             try{
                 runner();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
        */
         try{
             runner();
            }catch(Exception e){
                e.printStackTrace();
        }
        
    }
	public static void runner() throws IOException{
            String file1 = "IDB/Walk_001.ppm";
            String file2 = "IDB/Walk_001.ppm";
			Image refImg = new Image(file1);
			Image tarImg = new Image(file2);

			PixelMatrix[][] macroblocks = getBlocks(tarImg);
            System.out.printf(" dims: %d, %d\n",macroblocks.length,macroblocks[0].length);
			rebuildImage(macroblocks);
			int[][][] motionVectors =  getMotionVectors(macroblocks, refImg);
			writeMotionVectors(motionVectors,file1, file2,"test_mv.txt");	 	
			getResidualImage(macroblocks,motionVectors,refImg);
		}
		public static void getResidualImage(PixelMatrix[][] macroblocks, int[][][] mv, Image reference) {
			Image res = new Image(reference.getW(),reference.getH());
		    int[] pixel;
            int[] pixel2;
            int[] pixel3;
            /*
            int rows = macroblocks.length;
            int cols = macroblocks[0].length;

            for(int col = 0; col < cols; col++){
                for(int row = 0; row < rows; row++){
				
                }
			}
            */
            int xOffset;
            int yOffset;
            for(int y = 0; y < res.getH(); y++){
                for(int x = 0; x < res.getW(); x++){
                    pixel = new int[3];
                    pixel2 = new int[3];
                    pixel3 = new int[3];
                    xOffset =  mv[x / n][y / n][0];
                    yOffset =  mv[x / n][y / n][1];
                    //if(x/n == 3 && y/n == 2) System.out.printf("(%d,%d)->(%d,%d)\n",x,y, xOffset, yOffset);
                    reference.getPixel(x + xOffset, y + yOffset,pixel);
                    reference.getPixel(x, y, pixel2);
                    pixel3 = subtractPixel( getGrayPixel(pixel), getGrayPixel(pixel2));
                    res.setPixel(x, y, pixel3);
                }
            }	
			res.display();
			res.write2PPM("test_res.ppm");
		}
        public static int[] subtractPixel(int[] pix1, int[] pix2){
            int[] res = new int[3];
            res[0] = Math.abs(pix1[0]-pix2[0]);
            res[1] = Math.abs(pix1[1]-pix2[1]);
            res[2] = Math.abs(pix1[2]-pix2[2]);
            return res;
        }
		public static int[][][] getMotionVectors(PixelMatrix[][] macroblocks, Image refImage) {
            int rows = macroblocks.length;
            int cols = macroblocks[0].length;
			int[][][] motionVectors =  new int[rows][cols][2];

            for(int col = 0; col < cols; col++){
                for(int row = 0; row < rows; row++){
			        motionVectors[row][col] = motionVector(row * n,col * n, macroblocks[row][col],refImage);
				}
			}
			return motionVectors;
        }
        /*
	    public static int[] motionVector(int x, int y, PixelMatrix block, Image reference){
			int xOffset, endX, yOffset, endY;
			
			if(x - p < 0) xOffset = 0;
			else xOffset = x - p;
            int xStart = xOffset;
			
            if(y - p < 0) yOffset = 0;
			else yOffset = y - p;
			int input;
            
            if(x + p  + n> reference.getW()) endX = reference.getW();
            else endX = x + p + n;
            if(y + p + n > reference.getH()) endY = reference.getH();
            else endY = y + p + n;
            int[] pixel;
            int[] motionVector = new int[2];
            float total = 0;
            float min = 2147483647; // max possible value for java int
            int ctr = 0;
            float boxTotal= 0; 
            while(xOffset + n <= endX && yOffset + n <= endY) {	
                total = 0;
                for(int j = 0; j < n; j++) {
                    for(int i = 0; i < n; i++) {
                    pixel = new int[3];
                        reference.getPixel(i + xOffset, j + yOffset, pixel);
                        total += meanSquareDif(block.getPixel(i, j), pixel);
                    }
                }
                if(xOffset == x && yOffset == y) {
                    boxTotal = total;
                    System.out.println("boxtotal updated");
                }
                if(total < min) {
                    min = total;
                    motionVector[0] = xOffset - x;
                    motionVector[1] = yOffset - y;	
                }
                xOffset++;
                if(xOffset >= endX - n) {
                    xOffset = xStart;
                    yOffset++;
                }
            } 
            System.out.printf("(%d,%d) -> (%d,%d):: boxtotal : %.2f min : %.2f\n",x,y,xOffset,yOffset,boxTotal,min);
            return motionVector;
    } 
    */
	public static int[] motionVector(int x, int y, PixelMatrix block, Image reference){
			int xOffset, endX, yOffset, endY;
			
			if(x - p < 0) xOffset = 0;
			else xOffset = x - p;
            int xStart = xOffset;
			
            if(y - p < 0) yOffset = 0;
			else yOffset = y - p;
			int input;
            
            if(x + p  + n> reference.getW()) endX = reference.getW();
            else endX = x + p + n;
            if(y + p + n > reference.getH()) endY = reference.getH();
            else endY = y + p + n;
            int[] pixel;
            int[] motionVector = new int[2];
            float total = 0;
            float min = 2147483647; // max possible value for java int
            int ctr = 0;
            float boxTotal= 0;
            PixelMatrix temp = new PixelMatrix(n,n);
            while(xOffset + n <= endX && yOffset + n <= endY) {	
                total = 0;
                for(int j = 0; j < n; j++) {
                    for(int i = 0; i < n; i++) {
                    
                        pixel = new int[3];
                        reference.getPixel(i + xOffset, j + yOffset, pixel);
                        temp.setPixel(i,j,pixel);
                    }
                }
                total = blockComparison(block,temp);
                if(xOffset == x && yOffset == y) {
                    boxTotal = total;
                    System.out.println("boxtotal updated");
                }
                if(total < min) {
                    min = total;
                    motionVector[0] = xOffset - x;
                    motionVector[1] = yOffset - y;	
                }
                xOffset++;
                if(xOffset >= endX - n) {
                    xOffset = xStart;
                    yOffset++;
                }
            } 
            System.out.printf("(%d,%d) -> (%d,%d):: boxtotal : %.2f min : %.2f\n",x,y,xOffset,yOffset,boxTotal,min);
            return motionVector;
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
                for(int x = 0; x < n; x++){
					for(int y = 0; y < n; y++){
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
    public static int[] getGrayPixel(int[] pixel){
        int val = grayValue(pixel);
        int[] ret = {val,val,val};
        return ret;
    }
	public static int grayValue(int[]  pixel) {
		int r = pixel[2];
		int g = pixel[1];
		int b = pixel[0];
		int gray = (int) Math.round(0.299 *  r + 0.587 * g + 0.114 * b);
        gray = Math.min(255,gray);
        gray = Math.max(0,gray);
        return gray;
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
                for(int x = 0; x < n; x++){
					for(int y = 0; y < n; y++){
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
		System.out.println("Writing mv.txt file");
		int rows = motionvectors.length;
		int cols = motionvectors[0].length;
		File encodedText = new File(filename);
		FileWriter fw = new FileWriter(encodedText);
		BufferedWriter writer = new BufferedWriter(fw);
		writer.write("");
		writer.write("#Name: Olin-Mao Alvarez\n");
		writer.write("#Target image name: " + target + '\n');
		writer.write("#Reference image name: " + reference + '\n');
		writer.write("#Number of target macro blocks: "
			+ rows + " x  " + cols + " (image size is " 
			+ rows * n + " x " + cols * n  + ")\n");
	    int ctr = 0;	
        for(int col = 0; col < cols; col++){
            for(int row = 0; row < rows; row++){
				writer.write("[" + motionvectors[row][col][0]
						+ "," + motionvectors[row][col][1] + "] ");
                ctr++;
			}
			writer.write('\n');
		}
		writer.close();
		System.out.println(filename + " written");
	}
}
