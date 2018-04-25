
public class TestDCT {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		float[][] test = {{139,144,149,153,155,155,155,155,139,144,149,153,155,155,155,155},
						  {144,151,153,156,159,156,156,156,144,151,153,156,159,156,156,156},
						  {150,151,160,163,158,156,156,156,150,151,160,163,158,156,156,156},
						  {159,161,162,160,160,159,159,159,159,161,162,160,160,159,159,159},
						  {159,160,161,162,162,155,155,155,159,160,161,162,162,155,155,155},
						  {161,161,161,161,160,157,157,157,161,161,161,161,160,157,157,157},
						  {162,162,161,163,162,157,157,157,162,162,161,163,162,157,157,157},
						  {162,162,161,161,163,158,158,158,162,162,161,161,163,158,158,158},
						  {139,144,149,153,155,155,155,155,139,144,149,153,155,155,155,155},
						  {144,151,153,156,159,156,156,156,144,151,153,156,159,156,156,156},
						  {150,151,160,163,158,156,156,156,150,151,160,163,158,156,156,156},
						  {159,161,162,160,160,159,159,159,159,161,162,160,160,159,159,159},
						  {159,160,161,162,162,155,155,155,159,160,161,162,162,155,155,155},
						  {161,161,161,161,160,157,157,157,161,161,161,161,160,157,157,157},
						  {162,162,161,163,162,157,157,157,162,162,161,163,162,157,157,157},
						  {162,162,161,161,163,158,158,158,162,162,161,161,163,158,158,158}};
		
		for (int i = 0; i < test.length; i++) {
			for (int j = 0; j < test[0].length; j++) {
				test[i][j] -= 128;
			}
		}
		printMatrix(test);
		float[][] after = CS4551_Alvarez.dct(test);
		printMatrix(after);
		
		int[][] quantTable = CS4551_Alvarez.quantization(after, true);
		printMatrix(quantTable);
	//	CS4551_Alvarez.encode(quantTable);
		float[][] dequantTable = CS4551_Alvarez.dequantization(quantTable, true);
		printMatrix(dequantTable);
		after = CS4551_Alvarez.inverseDct(dequantTable);
		printMatrix(after);
		
		}
	static void printMatrix(float[][] mtx) {
		for (int i = 0; i < mtx.length; i++) {
			for (int j = 0; j < mtx[0].length; j++) {
				System.out.print(mtx[i][j] + " ");
			}
			System.out.println();
		}
		System.out.println();
	}
	static void printMatrix(int[][] mtx) {
		for (int i = 0; i < mtx.length; i++) {
			for (int j = 0; j < mtx[0].length; j++) {
				System.out.print(mtx[i][j] + " ");
			}
			System.out.println();
		}
		System.out.println();
	}
}		
