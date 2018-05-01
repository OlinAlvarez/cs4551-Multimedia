public class PixelMatrix{
    protected int rows;
    protected int cols;
    protected int[][][] matrix;
    protected Image img;
    public PixelMatrix(int rows, int cols){
        this.rows = rows;
        this.cols = cols;
        matrix = new int[rows][cols][3];
        int[] arr = {0,0,0};
        for(int i = 0; i < rows; i++) {
        	for (int j = 0; j < cols; j++) {
				matrix[i][j] = arr;
			}
        }
    }
    public void setPixel(int x, int y, int[] pixel){
        matrix[x][y] = pixel;
    }

    public int[] getPixel(int x, int y){
      return matrix[x][y];
    }
    public void createImage() {
    	img = new Image(this.rows, this.cols);
    	
    	for(int y = 0; y < this.cols; y++) {
    		for (int x = 0; x < this.rows; x++) {
				this.img.setPixel(x, y, getPixel(x,y));
			}
    	}
    }
    public void showImage() {
    	this.img.display();
    }
    public void showMatrix() {
    	for(int y = 0; y < this.rows; y++) {
    		for (int x = 0; x < this.cols; x++) {
    			System.out.print(showVal(x,y));
			}
    		System.out.println();
    	}
    	System.out.println();
    }
    public String showVal(int x, int y) {
    	return "(" +  matrix[x][y][0] + ","+  matrix[x][y][1] + ","+  matrix[x][y][2] + ") ";
    }
}
