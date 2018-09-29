/*
 *	This is the Processor for the Image Analyzer program written by
 *	Nicholas Grokhowsky
 *	May 3, 2018
 *	for CSCI E-10b
 *	
 *	This program enables the user to perform bandwidth analysis
 *	on an image(s) in four side by side panes.
 *
 */

/*
 *	Import java packages in order to enable the creation of a GUI -
 *	javax.swing
 *
 *	Import java packages in order to enable the use of BufferedImages -	
 *	java.awt
 *
 *	Import java packages in order to input/output image files - javax.imageio
 *	
 *	Import java packages in order to input/output data files - java.util
 */

import java.util.*;
import java.io.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*; 
import javax.swing.event.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import javax.imageio.ImageIO;


// Processor class processes images passed to it and seperates the individual pixel values
// This allows us to perform mathmatical operations on the individual pixels
class Processor
{
	// Instance Variables
	// BufferedImage image is used as the input image and output is used as the output image
	// the three int arrays are used to store the red, green,and blue pixels
	// the redSum, greenSum, and blueSum store the total pixel values for each band
	// the count variable stores the total number of pixels in an array
	// the width and height store the image width and height
	private	BufferedImage image = null;
	private	BufferedImage output = null;
	private int [][] red;
	private int [][] green;
	private int [][] blue;
	private int redSum;
	private int greenSum;
	private int blueSum;
	private int count;
	private int width;
	private int height;

	//Constructor method with no parameters builds an empty Processor
	public Processor()
	{

	}

	// Constructor method with BufferedImage as a parameter sets all necessary 
	// instance variables to perform calculations
	public Processor(BufferedImage image)
	{

		this.image = image;
		this.output = image;
		this.width = image.getWidth();
		this.height = image.getHeight();
		this.red = new int[this.width][this.height];
		this.green = new int[this.width][this.height];
		this.blue = new int[this.width][this.height];
	
	}

	// Setter method to get the pixel arrays for red, green, blue 
	// Also, sums red, green, and blue pixels and keeps total
	// count of pixels in image.
	public void setPixels()
	{				
		for(int i=0; i<this.width; i++)
		{
			for(int j=0; j<this.height; j++)
			{
				int pixel = image.getRGB(i,j);

				int a = (pixel>>24) & 0xff;
				int r = (pixel>>16) & 0xff;
				int g = (pixel>>8) & 0xff;
				int b = pixel & 0xff;

				this.red[i][j] = r;
				this.green[i][j] = g;
				this.blue[i][j] = b;
												
				this.redSum += r;
				this.greenSum +=g;
				this.blueSum += b;
								
				this.count++;
			}
		}
	}

	// Setter method that is supposed to create a true color image
	// and set the output image as the BufferedImage
	public void createOutputImage()
	{
		for(int i=0; i<this.width; i++)
		{
			for(int j=0; j<this.height; j++)
			{
				int r = this.red[i][j];
				int g = this.green[i][j];
				int b = this.blue[i][j];


				Color c = new Color(r, g, b);

				this.output.setRGB(i, j, c.getRGB());
			}
		}
	}

	// Setter method sets red pixel array
	public void setRed()
	{
		for(int i=0; i<this.width; i++)
		{
			for(int j=0; j<this.height; j++)
			{
				int r = this.red[i][j];

				Color c = new Color(r, greenSum/count, blueSum/count);

				this.output.setRGB(i, j, c.getRGB());
			}
		}

	}

	// Setter method sets green pixel array
	public void setGreen()
	{
		for(int i=0; i<this.width; i++)
		{
			for(int j=0; j<this.height; j++)
			{
				int g = this.green[i][j];

				Color c = new Color(redSum/count, g, blueSum/count);

				this.output.setRGB(i, j, c.getRGB());
			}
		}

	}

	// Setter method sets blue pixel array
	public void setBlue()
	{
		for(int i=0; i<this.width; i++)
		{
			for(int j=0; j<this.height; j++)
			{
				int b = this.blue[i][j];

				Color c = new Color(redSum/count, greenSum/count, b);

				this.output.setRGB(i, j, c.getRGB());
			}
		}

	}

	// Getter method gets the sum value of a pixel array
	public int getSum(int[][] array)
	{
		int sum = 0;

		for(int i=0; i<this.width; i++)
		{
			for(int j=0; j<this.height; j++)
			{
				sum += array[i][j];
			}
		}

		return sum;
	}

	// Getter method returns the output image as a BufferedImage
	public BufferedImage outputToBufferedImage()
	{
		return this.output;
	}

	// Getter method gets the red pixel array
	public int[][] getRedArray()
	{
		return red;
	}

	// Getter method gets the blue pixel array
	public int[][] getBlueArray()
	{
		return blue;
	}

	// Getter method gets the green pixel array
	public int[][] getGreenArray()
	{
		return green;
	}

	// Getter method returns the red array to string value
	public String redToString()
	{
		return Arrays.deepToString(red);
	}

	// Getter method returns the green array to string value
	public String greenToString()
	{
		return Arrays.deepToString(green);
	}

	// Getter method returns the blue array to string value
	public String blueToString()
	{
		return Arrays.deepToString(blue);
	}

	// Getter method that returns the int sum of the red pixel array
	public int redSum()
	{
		return redSum;
	}

	// Getter method that returns the int sum of the green pixel array
	public int greenSum()
	{
		return greenSum;
	}

	// Getter method that returns the int sum of the blue pixel array
	public int blueSum()
	{
		return blueSum;
	}

	// Getter method that returns the int count value of total pixels per image
	public int countToInt()
	{
		return count;
	}

}