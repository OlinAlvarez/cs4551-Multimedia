Olin Alvarez Homework 3
DCT image compression

So the way this lab works is that you read in an input image from the command line

1. The first thing the program does is check if the image that is read has a size that is
divisble by 8 in both width and height if not it will resize the image and fill in the extra with
black pixels. I display this image right away. Also I resize the image to the original image.
This is done using the "resize" method in the CS4551_Alvarez.java file

2. The next step is to convert the image from RGB to ycbcr which I do via the
RGB2YCbCr method in the CS4551_Alvarez.java file this returns a list of float matrices
that so that I can easily access the Y,Cb,Cr channels all.

3.After this I subsample the cb, cr channels using the 4:2:0 schema, average the value of the nearest
neighbor pixels.

4. after that all the channels get resized accordingly.

5. The next step is the dct method which each of the channels, and the subsamples get sent to the
    dct method.
6. then the resulting matrices are sent to a quantization method to quantize them
7. Then the next method is the "encodeQuant" method which stores all the runs of quantization
channels and stores them I have included a class called "Tuple" because it stored the values of the runs more nicely. This method also returns the number of bits needed per channel by adding up
the number of bits each 8x8 window needs

8. after that the main method adds up all the total bits for the channels and gets the compression
ratio using the size of the original image. it displays it to the console.

9. After that the channels are dequantized, then we apply inverseDct and the cb, and cr
are supersampled. I did not know which methoed we were suppsoed to use so i just distributed them evenly.

10.then we convert back the image into RBG using the YCbCr2RGB method in the CS4551_Alvarez file
unfortunately i could not get all these steps to work toegether however I show that this method works
by converting the original y,cb,cr channels back into the image and displaying that.

NOTE: Overall i had trouble making all the steps work together I got terrible results compressing
the image however I did not have enough time to fix all my mistakes. I did however include a
"TestDCT.java" file which tests all the methods used in a small matrix that was used as an example
in lecture slide 10 from the "9. Jpeg Algorithm" lecture notes. I took that matrix and appened it to
itself 3 times to make it 4 times larger and the results seemed to be similar to those on the
notes I don't really understand where it went wrong.


