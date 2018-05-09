Olin-Mao Alvarez
Homework 4 

I included a PixelMatrix class that I created in order to break down the amount of typing that
I had to do, also it contains methods that helped me during debugging.

First the menu is displayed and you select either:
Blocked Based Motion Compensation
Remove moving objects.

Block Based Motion Compensation
-------------------------------

The user is prompted to pick an n value and a p value and given the ranges.
The two inputs are done in while loops to assure that they will be in the given range.

next the users is prompted to input two images, i assumed the user would have perfect input
and that the images would be divisble by all the values.

After that the macroblocks are stored in a 2D PixelMatrix array which is returned by the
"getBlocks" method. 

the getBlocks method simply splits each section of the image into its own PixelMatrix and then stores 
it into a 2d array.

the we get the motion vectors using msd which is a 3d array of integers.
then we write the motion vectors and we save the residual Image.

saves motion vectors as mv.txt
Remove Moving Objects
---------------------

This one works similarly, n and p are both set to 8 for default
then we get the image number from the user

then after that we get the nth-2nd image and load it as a reference image

then we got macroblocks and motion vectors.

then we send it to showredblocks which saves and displays the dynamic blocks in red.

then we send it to two different removal methods

fifthFrame gets the fifth image of the set
staticFrame finds the nearest static block and replaces it

staticFrame saves image StaticReplacement.ppm
fifthFrame saves image fifthFrameReplacement.ppm
the motion vectors are saved as rmo_mv.txt
