class test{
    public static void main(String[] args){
        Image refImg = new Image("IDB/Walk_001.ppm"); 
        Image tarImg = new Image("IDB/Walk_101.ppm"); 
        
        PixelMatrix[][] blocks =  getBlocks(tarImg);
    }
}
