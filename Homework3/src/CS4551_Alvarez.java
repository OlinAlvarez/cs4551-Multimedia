/*******************************************************
G CS4551 Multimedia Software Systems
 @ Author: Olin-Mao Alvarez 
 *******************************************************/
import java.util.ArrayList;
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
	static int n;

	public static void main(String[] args) throws IOException{
	if(args.length != 1){
	  usage();
	  System.exit(1);
	}
	Image img = new Image(args[0]);

		if(img.getH() % 8 != 0  || img.getW() % 8 != 0) {
			img = resize(img);
		}
		
		ArrayList<float[][]> ycbcr = (ArrayList<float[][]>) RGB2YCbCr(img);
		
		float[][] yChan =  ycbcr.get(0);
		float[][] cbChan =  ycbcr.get(1);
		float[][] crChan =  ycbcr.get(2);
		
		float[][] cbSubsample = subsample(cbChan);
		float[][] crSubsample = subsample(crChan);
		
		if(cbSubsample.length %8 != 0 || cbSubsample[0].length % 8 != 0) cbSubsample = resizeChannel(cbSubsample);
		if(crSubsample.length %8 != 0 || crSubsample[0].length % 8 != 0) crSubsample = resizeChannel(crSubsample);
		if(yChan.length %8 != 0 || yChan[0].length % 8 != 0) yChan = resizeChannel(yChan);
		
		float[][] yDct = dct(yChan);
		float[][] cbDct = dct(cbSubsample);
		float[][] crDct = dct(crSubsample);
		
		do {
			System.out.println(" Enter an n value for compression");
			n = in.nextInt();
		}while( n > 5 || n < 0);

		int[][] yQuant = quantization(yDct, true);
		int[][] cbQuant = quantization(cbDct, false);
		int[][] crQuant = quantization(crDct, false);
		
		int yBits = encodeQuant(yQuant);
		int cbBits = encodeQuant(cbQuant);
		int crBits = encodeQuant(crQuant);
		int totalBits = yBits + cbBits + crBits;
        System.out.println("number of bits needed to encode = + Y Bits " + yBits + " Cb bits = " + cbBits + " Cr bits = " + crBits);
        System.out.println("total bits = " + totalBits );
        System.out.println("image size =" + img.getSize());
        float compRatio = (float) img.getSize() / totalBits;
        System.out.println("Compression Ratio : " + compRatio);
		float[][] yInvertedQuant = dequantization(yQuant, true);
		float[][] cbInvertedQuant = dequantization(cbQuant, false);
		float[][] crInvertedQuant = dequantization(crQuant, false);
	
		float[][] yIdct = inverseDct(yInvertedQuant);
		float[][] cbIdct = inverseDct(cbInvertedQuant);
		float[][] crIdct = inverseDct(crInvertedQuant);
		
		float[][] cbSuper = supersample(cbIdct);
		float[][] crSuper = supersample(crIdct);
		Image orignal = YCbCr2RGB(yChan, cbChan, crChan);
		Image compressed = YCbCr2RGB(yIdct, cbSuper, crSuper);
		compressed.display();
		
		if(Original_Height % 8 != 0  || Original_Width % 8 != 0) {
			img = revertDimensions(img);
			img.display();
		}	
	}
	public static int encodeQuant(int[][] quant) {
		int[][] block = new int[8][8];
		int blockRow = 0, blockCol = 0;
		int bits = 0;
		while(blockRow < quant.length && blockCol < quant[0].length) {
				
			for(int i = 0; i < 8; i++) {
				for (int j = 0; j < 8; j++) {
					block[i][j] =  quant[blockRow + i][blockCol + j];
				}
			}
			bits += encode(block);
			
			blockRow += 8;
			
			if(blockRow >= quant.length) {
				blockRow = 0;
				blockCol += 8;
			}
		}	
		return bits;
	}
	public static void usage(){
		System.out.println("\nUsage: java CS4551_Main [input_ppm_file]\n");
	}
	public static Image resize(Image img) {
		
		int width = img.getW();
		int height = img.getH();
		Original_Width = width;
		Original_Height = height;
		
		width += 8 - width % 8;
		height += 8 - height % 8;
		int[] black = {0,0,0};
		int[] pixelValue = new int[3];
		
		Image resizedImage =  new Image(width,height);
		
		for(int x = 0; x < width; x++) {
			for(int y = 0; y < height; y++) {
				if( x < img.getW() && y < img.getH()) {
					img.getPixel(x, y, pixelValue);
					resizedImage.setPixel(x, y, pixelValue);
				}else {
					resizedImage.setPixel(x, y, black);
				}
			}
		}
		resizedImage.display();
		return resizedImage;
	}

	public static Image revertDimensions(Image img) {
		Image resize = new Image(Original_Width, Original_Height);
		int[] pixel = new int[3];
		for(int x = 0; x < Original_Width; x++) {
			for (int y = 0; y < Original_Height; y++) {
				img.getPixel(x, y, pixel);
				resize.setPixel(x, y, pixel);
			}
		}	
		resize.display();
		return resize;
	}
	//store YCvCr in 2d float array
	public static List<float[][]> RGB2YCbCr(Image img) {
		float Y = 0;
		float Cb = 0;
		float Cr = 0;
		float[][] yChan = new float[img.getW()][img.getH()];
		float[][] cbChan= new float[img.getW()][img.getH()];
		float[][] crChan = new float[img.getW()][img.getH()];	
		int[] rgb = new int[3];
		
		for(int x = 0; x < img.getW(); x++) {
			for(int y = 0; y < img.getH(); y++) {
				img.getPixel(x, y, rgb);
				
				Y  = (float) (0.2990 * rgb[2] + 0.5870 * rgb[1] + 0.1140 * rgb[0]);
				Cb = (float) (-0.1687 * rgb[2] + -0.3313 * rgb[1] + 0.5000 * rgb[0]);
				Cr = (float) (0.5000 * rgb[2] + -0.4187 * rgb[1] + -0.0813 * rgb[0]);
				
				truncate(Y,Cb,Cr);
				Y -= 128;
				Cb -= 0.5;
				Cr -= 0.5;
				yChan[x][y] = Y;
				cbChan[x][y] = Cb;
				crChan[x][y] = Cr;
			}
		}
		
		List<float[][]> list = new ArrayList<>();
		list.add(yChan);
		list.add(cbChan);
		list.add(crChan);
		
		
		return list; 
	}
	
	public static Image YCbCr2RGB(float[][] Y, float[][] Cb, float[][] Cr) {
		Image img = new Image(Y.length, Y[0].length);
		int[] pixel = new int[3];
		int r,g,b;
		for(int x = 0; x < img.getW(); x++) {
			for(int y = 0; y < img.getH(); y++) {
				
				r = (int)((Y[x][y]) + (1.402 * Cr[x][y]));
				g = (int)((Y[x][y]) - (0.344 * Cb[x][y]) - (0.714*Cr[x][y]));
				b = (int)((Y[x][y]) + (1.772 * Cb[x][y]));
				
				truncate(r);
				truncate(g);
				truncate(b);
				
				pixel[0] = b + 128;
				pixel[1] = g + 128;
				pixel[2] = r + 128;
				
				img.setPixel(x, y, pixel);
			}
		}
		img.display();
		return img;
	}
	public static void showImage(float[][] channel) {
		Image img = new Image(channel.length, channel[0].length);
		int[] pixel = new int[3];
		for(int x = 0; x < img.getW(); x++) {
			for(int y = 0; y < img.getH(); y++) {
				
				
				pixel[0] =(int) channel[x][y];
				pixel[1] =(int) channel[x][y];
				pixel[2] =(int) channel[x][y];
				
				img.setPixel(x, y, pixel);
			}
		}
		img.display();
	}
	public static void showImage(int[][] channel) {
		Image img = new Image(channel.length, channel[0].length);
		int[] pixel = new int[3];
		for(int x = 0; x < img.getW(); x++) {
			for(int y = 0; y < img.getH(); y++) {
				
				
				pixel[0] =(int) channel[x][y];
				pixel[1] =(int) channel[x][y];
				pixel[2] =(int) channel[x][y];
				
				img.setPixel(x, y, pixel);
			}
		}
		img.display();
	}
	public static float[][] dct(float[][] channel) {
		
		/*
		 * This is where the actual DCT begins
		 */
		
		float cu, cv, sumx =(float) 0.0, sumy =(float) 0.0;
		float[][] block = new float[8][8];
		float[][] dct =  new float[channel.length][channel[0].length];
		int blockRow = 0, blockCol = 0;
		int counter = 0;
		while(blockRow < channel.length && blockCol < channel[0].length) {
			counter++;	
			for(int i = 0; i < 8; i++) {
				for (int j = 0; j < 8; j++) {
					block[i][j] =  channel[blockRow + i][blockCol + j];
				}
			}
			
 				
			for(int u = 0; u < 8; u++) {
				if(u == 0) cu = (float) Math.sqrt(2) / 2;
				else cu = 1;

				for(int v = 0; v < 8; v++) {
				if(v == 0) cv = (float) Math.sqrt(2) / 2;
				else cv = 1;
					sumx = (float) 0.0;
					for(int x = 0; x < 8; x++) {
						sumy = (float) 0.0;
						for(int y = 0; y < 8; y++) {
							sumy += block[x][y] * Math.cos(((2 * x + 1) * u * Math.PI ) / 16 ) * Math.cos( ((2 * y + 1) * v * Math.PI ) / 16); 
						}
						sumx += sumy;
					}
					dct[blockRow + u][blockCol + v] = (float) (cu * cv * sumx) / 4;
				}
			}
			blockRow += 8;
			if(blockRow >= channel.length) {
				blockRow = 0;
				blockCol += 8;
			}
		
		}
		return dct;
	}
	public static float[][] inverseDct(float[][] channel) {
		float cu, cv, sumx =(float) 0.0, sumy =(float) 0.0;
		float[][] block = new float[8][8];
		float[][] dct =  new float[channel.length][channel[0].length];
		int blockRow = 0, blockCol = 0;
		
		while(blockRow < channel.length && blockCol < channel[0].length) {
				
			for(int i = 0; i < 8; i++) {
				for (int j = 0; j < 8; j++) {
					block[i][j] =  channel[blockRow + i][blockCol + j];
				}
			}	
			//CS4551_Alvarez.printMatrix(block); 			
			for(int u = 0; u < 8; u++) {
				if(u == 0) cu = (float) Math.sqrt(2) / 2;
				else cu = 1;

				for(int v = 0; v < 8; v++) {
				if(v == 0) cv = (float) Math.sqrt(2) / 2;
				else cv = 1;
					sumx = (float) 0.0;
					for(int x = 0; x < 8; x++) {
						sumy = (float) 0.0;
						for(int y = 0; y < 8; y++) {
							sumy += block[x][y] *(cu * cv) / 4 * Math.cos(((2 * x + 1) * u * Math.PI ) / 16 ) * Math.cos( ((2 * y + 1) * v * Math.PI ) / 16);	
						}
						sumx += sumy;
					}
					dct[blockRow + u][blockCol + v] = sumx;
				}
			}
			blockRow += 8;
			if(blockRow >= channel.length) {
				blockRow = 0;
				blockCol += 8;
			}
		
		}
		return dct;
	}
	
	public static int[][] quantization(float[][] dct, boolean isLuma){
		
		
		int[][] lumaTable = {{4,4,4,8,8,16,16,32},
							 {4,4,4,8,8,16,16,32},
							 {4,4,8,8,16,16,32,32},
							 {8,8,8,16,16,32,32,32},
							 {8,8,16,16,32,32,32,32},
							 {16,16,16,32,32,32,32,32},
							 {16,16,32,32,32,32,32,32},
							 {32,32,32,32,32,32,32,32}};
		
		int[][] chromaTable =  {{8,8,8,16,32,32,32,32},
								{8,8,8,16,32,32,32,32},
								{8,8,16,32,32,32,32,32},
								{16,16,32,32,32,32,32,32},
								{32,32,32,32,32,32,32,32},
								{32,32,32,32,32,32,32,32},
								{32,32,32,32,32,32,32,32},
								{32,32,32,32,32,32,32,32}};
		int[][] table;
		if(isLuma)	table = lumaTable;
		else table = chromaTable;
		
		int[][] quantTable = new int[dct.length][dct[0].length];
		float[][] block = new float[8][8];
		
		int blockRow = 0, blockCol = 0;
		int power = (int) Math.pow(2, n);
		int counter = 0;
		while(blockRow < dct.length && blockCol < dct[0].length) {
			
			for(int i = 0; i < 8; i++) {
				for (int j = 0; j < 8; j++) {
					block[i][j] =  dct[blockRow + i][blockCol + j];
				}
			}
			for(int i = 0; i < 8; i++) {
				for (int j = 0; j < 8; j++) {
					quantTable[blockRow + i][blockCol + j] =(int) Math.round( block[i][j] / (table[i][j] * power));
				}
			}
			blockRow += 8;
			if(blockRow >= dct.length) {
				blockRow = 0;
				blockCol += 8;
			}
		}
		return quantTable;
		
	}
	public static float[][] dequantization(int[][] dct, boolean isLuma){
				
		
		int[][] lumaTable = {{4,4,4,8,8,16,16,32},
							 {4,4,4,8,8,16,16,32},
							 {4,4,8,8,16,16,32,32},
							 {8,8,8,16,16,32,32,32},
							 {8,8,16,16,32,32,32,32},
							 {16,16,16,32,32,32,32,32},
							 {16,16,32,32,32,32,32,32},
							 {32,32,32,32,32,32,32,32}};
		
		int[][] chromaTable =  {{8,8,8,16,32,32,32,32},
								{8,8,8,16,32,32,32,32},
								{8,8,16,32,32,32,32,32},
								{16,16,32,32,32,32,32,32},
								{32,32,32,32,32,32,32,32},
								{32,32,32,32,32,32,32,32},
								{32,32,32,32,32,32,32,32},
								{32,32,32,32,32,32,32,32}};
		int[][] table;
		if(isLuma)	table = lumaTable;
		else table = chromaTable;
		
		float[][] dequantTable = new float[dct.length][dct[0].length];
		
		int power = (int) Math.pow(2, n);
		int blockRow = 0, blockCol = 0;
		int[][] block = new int[8][8];
		
		while(blockRow < dct.length && blockCol < dct[0].length) {
				
			for(int i = 0; i < 8; i++) {
				for (int j = 0; j < 8; j++) {
					block[i][j] =  dct[blockRow + i][blockCol + j];
				}
			}
			for(int i = 0; i < 8; i++) {
				for (int j = 0; j < 8; j++) {
					dequantTable[blockRow + i][blockCol + j] = block[i][j] * (table[i][j] * power);
				}
			}
			blockRow += 8;
			if(blockRow >= dct.length) {
				blockRow = 0;
				blockCol += 8;
			}
		}
		return dequantTable;
	}
    
	public static int encode(int[][] block) {

        int index = 0;
        int[] list =  new int[64];
        
			for (int i = 0; i < 16 - 1; i++) {
				if (i % 2 == 1) {
					int x = i < 8 ? 0 : i - 8 + 1;
					int y = i < 8 ? i : 8 - 1;
					while (x < 8 && y >= 0 ) {
						list[index] = block[x][y];
						index++;
						x++;
						y--;
					}
				} else {
					// up right
					int x = i < 8 ? i : 8 - 1;
					int y = i < 8 ? 0 : i - 8 + 1;
					while (x >= 0 && y < 8) {
						list[index] = block[x][y];
						index++;
						x--;
						y++;
					}
				}
			}

        int prev = list[0];
        int runLength = 1;
        ArrayList<Tuple> encoding = new ArrayList<>();
        
        for (int i = 1; i <= list.length; i++) {
        	if(i == list.length) {
        		encoding.add(new Tuple(prev,runLength));
        	}else {
				if(list[i] != prev) {
					encoding.add(new Tuple(prev,runLength));
					prev = list[i];
					runLength = 1;
				}else {
					runLength++;
				}
        	} 
		}/*
        for (int i = 0; i < encoding.size(); i++) {
        	System.out.print(encoding.get(i));
		}
        System.out.println()*/
        int dcbits;
        if(encoding.size() > 1) dcbits = (encoding.size() - 1) * 16;
        else dcbits = 16;
        return dcbits + 10;
    }
	public static float[][] resizeChannel(float[][] channel ){
		
	
		int depth =  channel.length;
		int width =  channel[0].length;
		if(depth % 8 != 0) depth += 8 - (depth  % 8);
		if(width % 8 != 0) width += 8 - (width  % 8);
		
		float[][] res = new float[depth][width];
		
		for(int i = 0; i < depth; i++) {
			for (int j = 0; j < width; j++) {
				if( j >= channel[0].length || i >= channel.length ) {
					res[i][j] = 0;
				}
				else {
					res[i][j] = channel[i][j];
				}
			}
		}
		return res;
		
	}
	public static float[][] subsample(float[][] channel){

		/* 
		 * averages by indexing to every other element of the matrix then
		 * summing that element and it's three neighbours and dividing by 4
		 * */
		float[][] sub = new float[channel.length / 2][channel[0].length / 2];
		int row = 0, col = 0;
		float avg = 0;
		for(int i = 0; i < channel.length - 1; i += 2) {
			col = 0;
			for(int j = 0; j < channel[0].length - 1; j += 2) {
				avg =  channel[i][j] + channel[i+1][j] + channel[i][j + 1] + channel[i + 1][j + 1];
				avg /= 4;
				sub[row][col++] = avg;
			}
			row++;
			
		}
		return sub;
	}	
	public static float[][] supersample(float[][] subsample){
		float[][] supersample = new float[subsample.length * 2][subsample[0].length * 2];
		int k,l;
		for(int i = 0; i < subsample.length; i++) {
			k = 2 * i;
			for (int j = 0; j < subsample[0].length; j++) {
				l = 2 * j;
				supersample[k][l] = subsample[i][j];
				supersample[k + 1][l] = subsample[i][j];
				supersample[k][l + 1] = subsample[i][j];
				supersample[k + 1][l + 1] = subsample[i][j];	
			}
		}
		return supersample;
	}
	public static void truncate(float y,float cb,float cr){
		if(y < 0)
			y = 0;
		if(y > 255)
			y = 255;
		if(cb > 127.5)
			cb = (float) 127.5;
		if(cb < -127.5)
			cb = (float) -127.5;
		if(cr > 127.5)
			cr = (float) 127.5;
		if(cr < -127.5)
			cr = (float) -127.5;
	}
	public static void truncate(int num){
		if(num < 0)
			num = 0;
		if(num > 255)
			num = 255;
	}
}