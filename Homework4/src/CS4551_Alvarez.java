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
    public static void main(String[] args){
        int choice;
		do{
			choice = displayMenu();		
		}while(choice != 1 && choice != 2 && choice != 3); 
		
		try{
			if(choice  == 1) motionCompensation();
			if(choice  == 2) removeMovingObjects();
		}catch(Exception e){
			e.printStackTrace();
		}
    }
	 public static int displayMenu(){
     
     System.out.println("--------------------Main Men--------------------");
     System.out.println("1. Blocked Based Motion Compensation");
     System.out.println("2. Remove Moving Objects");
     System.out.println("3. Quit");
     return in.nextInt(); 
   }

	public static void motionCompensation() throws IOException{
            
            HashSet<Integer> nSet = new HashSet<>(Arrays.asList(8,16,24));
            HashSet<Integer> pSet = new HashSet<>(Arrays.asList(4,8,12,16));
            
			do {
                 System.out.println("Enter n value (8,16,24) : ");
                 n = in.nextInt();
             }while(!nSet.contains(n));
             do {
                 System.out.println("Enter p value(4,8,12,16) : ");
                 p = in.nextInt();
             }while(!pSet.contains(p)); 
			in = new Scanner(System.in);
			String file1;
            String file2;
            System.out.print("Enter First Image: ");
            file1 = in.nextLine();
            
            System.out.print("Enter Second Image: ");
            file2 = in.nextLine();
			
            Image refImg = new Image(file1);
			Image tarImg = new Image(file2);

			PixelMatrix[][] macroblocks = getBlocks(tarImg);
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
            int xOffset;
            int yOffset;
            for(int y = 0; y < res.getH(); y++){
                for(int x = 0; x < res.getW(); x++){
                    pixel = new int[3];
                    pixel2 = new int[3];
                    pixel3 = new int[3];
                    xOffset =  mv[x / n][y / n][0];
                    yOffset =  mv[x / n][y / n][1];
                    reference.getPixel(x + xOffset, y + yOffset,pixel);
                    reference.getPixel(x, y, pixel2);
                    pixel3 = subtractPixel( getGrayPixel(pixel), getGrayPixel(pixel2));
                    pixel3 =  trimError(pixel3);
                    res.setPixel(x, y, pixel3);
                }
            }	
			res.display();
			res.write2PPM("test_resRow.ppm");
		}
        
        public static int[] trimError(int[] pixel){
            int ret = Math.max(pixel[0],0);
            ret = Math.min(ret,255);
            int[] r = {ret,ret,ret};
            return r;
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
            
            while(yOffset + n <= endY) {	

                total = 0;
                    
                for(int i = 0; i < n; i++) {
                    for(int j = 0; j < n; j++) {
                        pixel = new int[3];
                        reference.getPixel(xOffset + i, yOffset + j, pixel);
                        temp.setPixel(i,j,pixel);
                    }
                }
                total = blockComparison(block,temp);
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
            return motionVector;
    } 
	public static float blockComparison(PixelMatrix target, PixelMatrix reference) {
		int total = 0;
        for(int j = 0; j < target.cols; j++) {
            for(int i = 0; i < target.rows; i++) {
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
        for(int j = 0; j < mtx[0].length; j++){
            for(int i = 0; i < mtx.length; i++){
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
	public static void rebuildImage(PixelMatrix[][] mtx) {
        Image img = new Image( n * mtx.length, n * mtx[0].length);
		int blockRow = 0, blockCol = 0;

        for(int j = 0; j < mtx[0].length; j++){
            for(int i = 0; i < mtx.length; i++){
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
    /*
     *Beginning of section 2
     * */
   public static void removeMovingObjects() throws IOException{
        n  = 8;
        p = 8;
        int tarNum;
        do{
            System.out.println("\n Image Loaded\n select number in range 19 - 179: ");
            tarNum = in.nextInt();
        }while(tarNum > 179 || tarNum < 19);
        int refNum = tarNum - 2; 
        String pic =  "" + tarNum;
        String pic2 =  "" + refNum;
        if(tarNum < 100){
            pic = "0" + pic; 
        }
        if(refNum < 100){
            pic2 = "0" + pic2;
        }
        String tarFilePath = "IDB/Walk_" + pic + ".ppm";
        String refFilePath = "IDB/Walk_" + pic2 + ".ppm";
        Image target = new Image(tarFilePath);
        Image reference = new Image(refFilePath);
          
        PixelMatrix[][] macroblocks = getBlocks(target);
        rebuildImage(macroblocks);
        int[][][] motionVectors =  getMotionVectors(macroblocks, reference);
        showRedBlocks(macroblocks,motionVectors);
        fifthFrame(macroblocks,motionVectors);
        staticFrame(macroblocks,motionVectors,target);
        writeMotionVectors(motionVectors,tarFilePath,refFilePath,"rmo_mv.txt");
   } 
   public static void showRedBlocks(PixelMatrix[][] blocks, int[][][] mv){
        int[]  redPixel = {255,40,0} ; // I didn't want the image to be just red wanted to add a little more of a complex color.
        Image img = new Image( n * blocks.length, n * blocks[0].length);
		int blockRow = 0, blockCol = 0;
        int xOffset, yOffset;
        for(int j = 0; j < blocks[0].length; j++){
            for(int i = 0; i < blocks.length; i++){
                xOffset =  mv[i][j][0];
                yOffset =  mv[i][j][1];
                for(int x = 0; x < n; x++){
					for(int y = 0; y < n; y++){
						    if( xOffset != 0 || yOffset != 0) img.setPixel(blockCol + x, blockRow + y,redPixel);
                            else img.setPixel(blockCol + x, blockRow + y,blocks[i][j].matrix[x][y]);
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
		img.write2PPM("redBlocks.ppm");
   }
   public static void fifthFrame(PixelMatrix[][] blocks, int[][][] mv){
        Image fifth = new Image("IDB/Walk_005.ppm");
        Image img = new Image( n * blocks.length, n * blocks[0].length);
		int blockRow = 0, blockCol = 0;
        int xOffset, yOffset;
        int[] pixel;
        for(int j = 0; j < blocks[0].length; j++){
            for(int i = 0; i < blocks.length; i++){
                xOffset =  mv[i][j][0];
                yOffset =  mv[i][j][1];
                for(int x = 0; x < n; x++){
					for(int y = 0; y < n; y++){
						    if( xOffset != 0 || yOffset != 0){
                                pixel = new int[3];
                                fifth.getPixel(blockCol + x, blockRow + y,pixel);
                                img.setPixel(blockCol + x, blockRow + y,pixel);
                            }
                            else img.setPixel(blockCol + x, blockRow + y,blocks[i][j].matrix[x][y]);
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
		img.write2PPM("fifthFrameReplacement.ppm");
   }

   public static void staticFrame(PixelMatrix[][] blocks, int[][][] mv, Image reference){
        Image img = new Image( n * blocks.length, n * blocks[0].length);
		int blockRow = 0, blockCol = 0;
        int xOffset, yOffset;
        int[] pixel;
        int[] bc;
        PixelMatrix block;
        for(int j = 0; j < blocks[0].length; j++){
            for(int i = 0; i < blocks.length; i++){
                xOffset =  mv[i][j][0];
                yOffset =  mv[i][j][1];
                for(int x = 0; x < n; x++){
					for(int y = 0; y < n; y++){
						    if( xOffset != 0 || yOffset != 0){
                                pixel = new int[3];
                                bc = getNearestStaticMotionVector(i,j,mv);//bc for Block Coordinates;
                                block =  blocks[bc[0]][bc[1]];
                                pixel = block.getPixel(x,y);
                                img.setPixel(blockCol + x, blockRow + y,pixel);
                            }
                            else img.setPixel(blockCol + x, blockRow + y,blocks[i][j].matrix[x][y]);
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
		img.write2PPM("staticFrameReplacement.ppm");
   
   }

   public static int[] getNearestStaticMotionVector(int row, int col, int[][][] mv){
        int[] ret = new int[2]; 
    
        int num1, num2;
        for(int j = col; j < mv[0].length; j++){
            for(int i = row; i < mv.length; i++){
               num1 = mv[i][j][0]; 
               num2 = mv[i][j][1]; 
               if(num1 == 0 && num2 == 0){
                    ret[0] = i;
                    ret[1] = j;
                    return ret;
               }
            }
        }
        ret[0] = row;
        ret[1] = col;
        return ret;
   }
}
