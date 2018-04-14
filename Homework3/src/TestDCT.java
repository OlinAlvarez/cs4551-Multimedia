
public class TestDCT {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		float[][] test = {{139,144,149,153,155,155,155,155},
						  {144,151,153,156,159,156,156,156},
						  {150,151,160,163,158,156,156,156},
						  {159,161,162,160,160,159,159,159},
						  {159,160,161,162,162,155,155,155},
						  {161,161,161,161,160,157,157,157},
						  {162,162,161,163,162,157,157,157},
						  {162,162,161,161,163,158,158,158}};
		
		for (int i = 0; i < test.length; i++) {
			for (int j = 0; j < test.length; j++) {
				test[i][j] -= 128;
			}
		}
		int[][] testTable = {{12,1,0,0,0,0,0,0},
							 {1,1,0,0,0,0,0,0},
							 {1,0,0,0,0,0,0,0},
							 {0,0,0,0,0,0,0,0},
							 {0,0,0,0,0,0,0,0},
							 {0,0,0,0,0,0,0,0},
							 {0,0,0,0,0,0,0,0},
							 {0,0,0,0,0,0,0,0}};
		CS4551_Alvarez.encode(testTable);
		CS4551_Alvarez.printMatrix(test);
		float[][] after = CS4551_Alvarez.dct(test);
		CS4551_Alvarez.printMatrix(after);
		//after = CS4551_Alvarez.inverseDct(test,8,8);
		//after = idct(after);
		CS4551_Alvarez.printMatrix(after);
		int[][] quantTable = CS4551_Alvarez.quantization(after, true);
		CS4551_Alvarez.printMatrix(quantTable);
		CS4551_Alvarez.encode(quantTable);
		float[][] dequantTable = CS4551_Alvarez.dequantization(quantTable, true);
		CS4551_Alvarez.printMatrix(dequantTable);
		after = idct(dequantTable);
		CS4551_Alvarez.printMatrix(after);
		
		}
		
	public static float[][] idct(float[][] channel) {
		
		/*
		 * This is where the actual DCT begins
		 */
		
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
			if(blockRow > channel.length) {
				blockRow = 0;
				blockCol += 8;
			}
		
		}
		return dct;
	}
}
