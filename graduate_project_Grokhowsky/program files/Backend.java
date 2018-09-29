/*
 *	This is the Backend for the Image Analyzer program written by
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
import javax.swing.border.*;
import java.awt.event.*; 
import javax.swing.event.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import javax.imageio.ImageIO;

// Backend processes ActionListner actions from GUI2 class
// This does so by implementing the feedString method and
// from there references other setter and getter methods
// within the class
class Backend
{

	// Instance variables are created to store the String location of a file, the File location, the ImageIcons,
	// BufferedImage being processed or passed to the GUI, and the true color array and statistics
	private String file;
	private File f;
	private File f1;
	private File f2;
	private File f3;
	private File f4;
	private ImageIcon iconOne;
	private ImageIcon iconTwo;
	private ImageIcon iconThree;
	private ImageIcon iconFour;
	private BufferedImage image;
	private BufferedImage output;
	private int [][] tc;
	private int counter = 0;
	private	int tcSum = 0;
	private int mean;
	private double median;
	private double sd;
	
	/**************************************CONSTRUCTOR METHOD********************************************/

	// Constructor method builds a Backend() with no parameters passed. 
	// This in essence does nothing but builds an empty Backend();
	public Backend()
	{
	
	}

	/****************************************SETTER METHODS********************************************/

	// Setter method used to accept a string input from a button pressed in the GUI and then process the 
	// correct method based on the button pressed/string passed as a parameter 
	// This method has been overloaded 4 times

	// First version accepts 2 parameters: the string from the button and the quadrant the image is located
	public void feedString(String s, int quadrant)
	{
		// Picks file chooser through chooseFile() method
		// Prints the file directory location in the JTextArea for the file directory
		if(s.equals("Choose File"))
		{
			chooseFile();
		}

		// Loads the image onto the JLabel in the quadrant chose by the user
		if(s.equals("Load Image"))
		{
			loadImage(quadrant);
		}

		// Saves the image in the quadrant chosen in the directory and file chosen by the user
		if(s.equals("Save Image"))
		{
			saveImage(quadrant);
		}


	}

	// Second feedString() method that accepts three parameters, and is soley used to isolate bandwidth
	// Parameters are the string value of the button, the quadrant the image is located, and the function
	// which chooses which bandwidth to isolate (0 for TrueColor, 1 for Red, 2 for Green, and 3 for Blue)
	public void feedString(String s, int quadrant, int function)
	{
		if(s.equals("Process"))
		{
			if(quadrant == 1) processImageOne(function);
			if(quadrant == 2) processImageTwo(function);
			if(quadrant == 3) processImageThree(function);
			if(quadrant == 4) processImageFour(function);
			
		}
	}
	
	// Third feedString() method that accepts three parameters and processes the reSize() method
	// and color shift methods
	// The parameters accepted are the string value of the button, the quadrant value, and a double that is 
	// used as a size coefficient for the reSize() method and used as a shift value for the shift functions
	public void feedString(String s, int quadrant, double size)
	{
		if(s.equals("Load Image"))
		{
			loadImage(quadrant);
		}

		if(s.equals("Zoom"))
		{	
			if(quadrant==1)
			{
				zoomImage(iconOne, size, quadrant);
			}
			if(quadrant==2)
			{
				zoomImage(iconTwo, size, quadrant);
			}
			if(quadrant==3)
			{
				zoomImage(iconThree, size, quadrant);
			}
			if(quadrant==4) 
			{
				zoomImage(iconFour, size, quadrant);
			}
		}
		
		if(s.equals("Red Shift"))
		{
			redSeperation(quadrant, size);
		}
		if(s.equals("Green Shift"))
		{
			greenSeperation(quadrant, size);
		}
		if(s.equals("Blue Shift"))
		{
			blueSeperation(quadrant, size);
		}		
	}

	// Fourth feedString method that takes two strings as parameters
	// This is used soley for saving the notes taken in the note field
	// The first string is the button string and the second string are
	// the notes taken to be saved
	public void feedString(String s, String notes)
	{
		if(s.equals("Save Notes"))
		{
			saveNotes(notes);
		}
	}

	// chooseFile method chooses a file from the file chooser and sets 
	// the directory as a string in the instance variable file
	public void chooseFile()
	{
		JFileChooser chooser = new JFileChooser();
		chooser.showOpenDialog(null);
		file = chooser.getSelectedFile().toString();
		this.f = new File(file);	
	}

	// loadImage method instantiates the BufferedImage instance variable image and
	// sets the four ImageIcon instance variables to the same image as the image variable
	// The ImageIcon variable is chosen based on which quadrant is passed as the parameter
	public void loadImage(int quadrant)
	{				
		try
		{
			this.image = ImageIO.read(f);
			this.output = ImageIO.read(f);
		}
		catch (Exception e)
		{
			System.out.print(e);
		}

		this.tc = new int[image.getWidth()][image.getHeight()];
		
		if(quadrant == 1)
		{
			this.iconOne = new ImageIcon(this.image);
			this.f1 = this.f;
		}

		if(quadrant == 2)
		{
			this.iconTwo = new ImageIcon(this.image);
			this.f2 = this.f;
		}

		if(quadrant == 3)
		{
			this.iconThree = new ImageIcon(this.image);
			this.f3 = this.f;
		}

		if(quadrant == 4)
		{
			this.iconFour = new ImageIcon(this.image);
			this.f4 = this.f;
		}
	}

	// loadImage method instantiates the BufferedImage instance variable image and
	// sets the four ImageIcon instance variables to the same image as the image variable
	// The ImageIcon variable is chosen based on which quadrant is passed as the parameter
	public void loadImage(int quadrant, File file)
	{				
		try
		{
			this.image = ImageIO.read(file);
			this.output = ImageIO.read(file);
		}
		catch (Exception e)
		{
			System.out.print(e);
		}

		this.tc = new int[image.getWidth()][image.getHeight()];
		
		if(quadrant == 1)
		{
			this.iconOne = new ImageIcon(this.image);
		}

		if(quadrant == 2)
		{
			this.iconTwo = new ImageIcon(this.image);
		}

		if(quadrant == 3)
		{
			this.iconThree = new ImageIcon(this.image);
		}

		if(quadrant == 4)
		{
			this.iconFour = new ImageIcon(this.image);
		}
	}

	// The zoomImage method changes the size of the ImageIcon passed as
	// a parameter.  It uses the double size as the coefficient.  The
	// quadrant integer is used to determine which ImageIcon to overwrite.
	public void zoomImage(ImageIcon icon, double size, int quadrant)
	{
		if(size > 0)
		{	
			this.image = toBufferedImage(icon);
			int width = (int)((image.getWidth()*size));
			int height = (int)((image.getHeight()*size));
			reSize(toBufferedImage(icon), width, height, quadrant);
		}
		else
		{
			this.image = toBufferedImage(icon);
			int width = (int)((image.getWidth()*1));
			int height = (int)((image.getHeight()*1));
			reSize(toBufferedImage(icon), width, height, quadrant);	
		}
	}

	// processImageOne method is used to isolate the bandwidth in the ImageIcon instance variable
	// iconOne.  A Processor object is created to process the pixels in the image, and then return
	// them to this class.  It takes one parameter which is the function used to determine whether truecolor
	// red, green, or blue will be isolated.
	public void processImageOne(int function)
	{
		Processor pixels = new Processor();

		if(function == 0)
		{
			loadImage(1, f1);
			toArray();
			this.mean = mean(); 
			this.median = getMedian(tc);
			this.sd = round(getSD(tc));
		}

		if(function == 1)
		{
			pixels = new Processor(toBufferedImage(this.iconOne));
			pixels.setPixels();
			pixels.setRed();
			this.iconOne = new ImageIcon(pixels.outputToBufferedImage());
			this.mean = pixels.redSum()/pixels.countToInt();
			this.median = getMedian(pixels.getRedArray());
			this.sd = round(getSD(pixels.getRedArray()));
		}

		if(function == 2)
		{
			pixels = new Processor(toBufferedImage(this.iconOne));
			pixels.setPixels();
			pixels.setGreen();
			this.iconOne = new ImageIcon(pixels.outputToBufferedImage());
			this.mean = pixels.greenSum()/pixels.countToInt();
			this.median = getMedian(pixels.getGreenArray());
			this.sd = round(getSD(pixels.getGreenArray()));
		}

		if(function == 3)
		{
			pixels = new Processor(toBufferedImage(this.iconOne));
			pixels.setPixels();
			pixels.setBlue();
			this.iconOne = new ImageIcon(pixels.outputToBufferedImage());
			this.mean = pixels.blueSum()/pixels.countToInt();
			this.median = getMedian(pixels.getBlueArray());
			this.sd = round(getSD(pixels.getBlueArray()));
		}	
	}

	// processImageTwo method is used to isolate the bandwidth in the ImageIcon instance variable
	// iconTwo.  A Processor object is created to process the pixels in the image, and then return
	// them to this class.  It takes one parameter which is the function used to determine whether truecolor
	// red, green, or blue will be isolated.
	public void processImageTwo(int function)
	{
		Processor pixels = new Processor();

		if(function == 0)
		{
			loadImage(2, f2);
			toArray();
			this.mean = mean(); 
			this.median = getMedian(tc);
			this.sd = round(getSD(tc));
		}

		if(function == 1)
		{
			pixels = new Processor(toBufferedImage(iconTwo));
			pixels.setPixels();
			pixels.setRed();
			this.iconTwo = new ImageIcon(pixels.outputToBufferedImage());
			this.mean = pixels.redSum()/pixels.countToInt();
			this.median = getMedian(pixels.getRedArray());
			this.sd = round(getSD(pixels.getRedArray()));
		}

		if(function == 2)
		{
			pixels = new Processor(toBufferedImage(iconTwo));
			pixels.setPixels();
			pixels.setGreen();
			this.iconTwo = new ImageIcon(pixels.outputToBufferedImage());
			this.mean = pixels.greenSum()/pixels.countToInt();
			this.median = getMedian(pixels.getGreenArray());
			this.sd = round(getSD(pixels.getGreenArray()));
		}

		if(function == 3)
		{
			pixels = new Processor(toBufferedImage(iconTwo));
			pixels.setPixels();
			pixels.setBlue();
			this.iconTwo = new ImageIcon(pixels.outputToBufferedImage());
			this.mean = pixels.blueSum()/pixels.countToInt();
			this.median = getMedian(pixels.getBlueArray());
			this.sd = round(getSD(pixels.getBlueArray()));
		}		
	}

	// processImageThree method is used to isolate the bandwidth in the ImageIcon instance variable
	// iconThree.  A Processor object is created to process the pixels in the image, and then return
	// them to this class.  It takes one parameter which is the function used to determine whether truecolor
	// red, green, or blue will be isolated.
	public void processImageThree(int function)
	{
		Processor pixels = new Processor();

		if(function == 0)
		{
			loadImage(3, f3);
			toArray();
			this.mean = mean(); 
			this.median = getMedian(tc);
			this.sd = round(getSD(tc));
		}

		if(function == 1)
		{
			pixels = new Processor(toBufferedImage(iconThree));
			pixels.setPixels();
			pixels.setRed();
			this.iconThree = new ImageIcon(pixels.outputToBufferedImage());
			this.mean = pixels.redSum()/pixels.countToInt();
			this.median = getMedian(pixels.getRedArray());
			this.sd = round(getSD(pixels.getRedArray()));
		}

		if(function == 2)
		{
			pixels = new Processor(toBufferedImage(iconThree));
			pixels.setPixels();
			pixels.setGreen();
			this.iconThree = new ImageIcon(pixels.outputToBufferedImage());
			this.mean = pixels.greenSum()/pixels.countToInt();
			this.median = getMedian(pixels.getGreenArray());
			this.sd = round(getSD(pixels.getGreenArray()));
		}

		if(function == 3)
		{
			pixels = new Processor(toBufferedImage(iconThree));
			pixels.setPixels();
			pixels.setBlue();
			this.iconThree = new ImageIcon(pixels.outputToBufferedImage());
			this.mean = pixels.blueSum()/pixels.countToInt();
			this.median = getMedian(pixels.getBlueArray());
			this.sd = round(getSD(pixels.getBlueArray()));
		}		
	}

	// processImageFour method is used to isolate the bandwidth in the ImageIcon instance variable
	// iconFour.  A Processor object is created to process the pixels in the image, and then return
	// them to this class.  It takes one parameter which is the function used to determine whether truecolor
	// red, green, or blue will be isolated.
	public void processImageFour(int function)
	{
		Processor pixels = new Processor();

		if(function == 0)
		{
			loadImage(4, f4);
			toArray();
			this.mean = mean(); 
			this.median = getMedian(tc);
			this.sd = round(getSD(tc));
		}

		if(function == 1)
		{
			pixels = new Processor(toBufferedImage(iconFour));
			pixels.setPixels();
			pixels.setRed();
			this.iconFour = new ImageIcon(pixels.outputToBufferedImage());
			this.mean = pixels.redSum()/pixels.countToInt();
			this.median = getMedian(pixels.getRedArray());
			this.sd = round(getSD(pixels.getRedArray()));
		}

		if(function == 2)
		{
			pixels = new Processor(toBufferedImage(iconFour));
			pixels.setPixels();
			pixels.setGreen();
			this.iconFour = new ImageIcon(pixels.outputToBufferedImage());
			this.mean = pixels.greenSum()/pixels.countToInt();
			this.median = getMedian(pixels.getGreenArray());
			this.sd = round(getSD(pixels.getGreenArray()));
		}

		if(function == 3)
		{
			pixels = new Processor(toBufferedImage(iconFour));
			pixels.setPixels();
			pixels.setBlue();
			this.iconFour = new ImageIcon(pixels.outputToBufferedImage());
			this.mean = pixels.blueSum()/pixels.countToInt();
			this.median = getMedian(pixels.getBlueArray());
			this.sd = round(getSD(pixels.getBlueArray()));
		}		
	}

	// redSeperation determines the image quadrant and passes its double value to the redMeanSeparation
	// method. The parameters passed are an int for the image quadrant and a double for the seperation value. 

	public void redSeperation(int quadrant, double value)
	{
		if(quadrant == 1)
		{
			//loadImage(1);
			this.iconOne = new ImageIcon(redMeanSeperation(this.iconOne, value));
		}
		if(quadrant == 2)
		{
			//loadImage(2);
			this.iconTwo = new ImageIcon(redMeanSeperation(this.iconTwo, value));
		}
		if(quadrant == 3)
		{
			//loadImage(3);
			this.iconThree = new ImageIcon(redMeanSeperation(this.iconThree, value));
		}
		if(quadrant == 4)
		{
			//loadImage(4);
			this.iconFour = new ImageIcon(redMeanSeperation(this.iconFour, value));
		}
	}

	// greenSeperation determines the image quadrant and passes its double value to the greenMeanSeparation
	// method. The parameters passed are an int for the image quadrant and a double for the seperation value. 
	public void greenSeperation(int quadrant, double value)
	{
		if(quadrant == 1)
		{
			//loadImage(1);
			this.iconOne = new ImageIcon(greenMeanSeperation(this.iconOne, value));
		}
		if(quadrant == 2)
		{
			//loadImage(2);
			this.iconTwo = new ImageIcon(greenMeanSeperation(this.iconTwo, value));
		}
		if(quadrant == 3)
		{
			//loadImage(3);
			this.iconThree = new ImageIcon(greenMeanSeperation(this.iconThree, value));
		}
		if(quadrant == 4)
		{
			//loadImage(4);
			this.iconFour = new ImageIcon(greenMeanSeperation(this.iconFour, value));
		}
	}

	// blueSeperation determines the image quadrant and passes its double value to the blueMeanSeparation
	// method. The parameters passed are an int for the image quadrant and a double for the seperation value. 
	public void blueSeperation(int quadrant, double value)
	{
		if(quadrant == 1)
		{
			//loadImage(1);
			this.iconOne = new ImageIcon(blueMeanSeperation(this.iconOne, value));
		}
		if(quadrant == 2)
		{
			//loadImage(2);
			this.iconTwo = new ImageIcon(blueMeanSeperation(this.iconTwo, value));
		}
		if(quadrant == 3)
		{
			//loadImage(3);
			this.iconThree = new ImageIcon(blueMeanSeperation(this.iconThree, value));
		}
		if(quadrant == 4)
		{
			//loadImage(4);
			this.iconFour = new ImageIcon(blueMeanSeperation(this.iconFour, value));
		}
	}

	//saveImage opens a file chooser and writes a new .jpg file within a try-catch block
	// An int parameter is taken to represent the quadrant location of the image to be saved.

	public void saveImage(int quadrant)
	{
		try
			{
				JFileChooser chooser = new JFileChooser();
				chooser.showOpenDialog(null);
				this.file = chooser.getSelectedFile().toString();
				f = new File(file);

				if(quadrant == 1)
				{
					ImageIO.write(toBufferedImage(iconOne), "jpg", f);
				}

				if(quadrant == 2)
				{
					ImageIO.write(toBufferedImage(iconTwo), "jpg", f);
				}

				if(quadrant == 3)
				{
					ImageIO.write(toBufferedImage(iconThree), "jpg", f);
				}

				if(quadrant == 4)
				{
					ImageIO.write(toBufferedImage(iconFour), "jpg", f);
				}
			
			}
			catch (IOException e)
			{
				System.out.println(e);
			}
	}

	// saveNotes opens the file chooser and saves the notes taken as a .dat file inside a try-catch.
	// It takes a string parameter which are the notes to be saved.
	public void saveNotes(String s)
	{
		try
		{
			JFileChooser chooser = new JFileChooser();
			chooser.showOpenDialog(null);
			this.file = chooser.getSelectedFile().toString();
			f = new File(file);

			BufferedWriter writer = new BufferedWriter(new FileWriter(f));
			writer.write(s);
			writer.close();
		}
		catch (Exception exception)
		{
			System.out.println(exception);
		}
	}

	// reSize re-sizes a buffered image and sets the ImageIcon instance variables to the image
	// based on the quadrant the image is located.  
	// The method takes the BufferedImage to be resized, the new width as an int and height as an int
	// and the quadrant to pick which ImageIcon to store set.
	public void reSize(BufferedImage image, int width, int height, int quadrant)
	{
		int w = image.getWidth();
		int h = image.getHeight();

		BufferedImage output = new BufferedImage(width, height, image.getType());

		Graphics2D graphic = output.createGraphics();
		graphic.drawImage(image, 0, 0, width, height, 0, 0, w, h, null);
		graphic.dispose();
		
		if(quadrant == 1)
		{
			this.iconOne = new ImageIcon(output);
		}

		if(quadrant == 2)
		{
			this.iconTwo = new ImageIcon(output);
		}

		if(quadrant == 3)
		{
			this.iconThree = new ImageIcon(output);
		}

		if(quadrant == 4)
		{
			this.iconFour = new ImageIcon(output);
		}
	}

	// toArray iterates the pixels inside the BufferedImage instance variable image
	// and instantiates the true color array (tc), the sum of values for the true
	// color array, and the counter which counts how many pixels there are in the
	// array. These are necessary to calculate the statistics of the true color
	// image.
	public void toArray()
	{	
		for(int i=0; i<this.image.getWidth(); i++)
		{
			for(int j=0; j<this.image.getHeight(); j++)
			{
				int pixel = image.getRGB(i,j);
				int a = (pixel>>24) & 0xff;
				int r = (pixel>>16) & 0xff;
				int g = (pixel>>8) & 0xff;
				int b = pixel & 0xff;

				int alpha = a << 24;
				int rbw = (r << 16);
				int gbw = (g << 8);
				int bbw = (b);
				
				int trueColor = r | g | b;
				this.tc[i][j] = trueColor;
				
				this.tcSum += trueColor;
				this.counter++;
			}
		}
	}

	/***************************************GETTER METHODS*****************************************/

	// redMeanSeperation gets the BufferedImage of the original image with the extreme pixel
	// values for red flooded or removed.  In this method, if a red pixel value is greater than
	// one magnitude of the standard deviation it is flooded with a maximum value.  A pixel value
	// that is less than one magnitude of the standard deviation is stripped to 0.  The remaining
	// pixels are then controled by the slider.  This gives the user the option to flood the image
	// with red pixels, to show only extreme values, or to show red anywhere in between.
	public BufferedImage redMeanSeperation(ImageIcon img, double value)
	{

		BufferedImage buff = toBufferedImage(img);

		for(int i=0; i<buff.getWidth(); i++)
		{
			for(int j=0; j<buff.getHeight(); j++)
			{
				int pixel = buff.getRGB(i,j);
				int a = (pixel>>24) & 0xff;
				int r = (pixel>>16) & 0xff;
				int g = (pixel>>8) & 0xff;
				int b = pixel & 0xff;

				if((r > this.mean + (this.sd/2)))
				{
					r = 255;
				}
				else if ((r < this.mean - (this.sd/2)))
				{
					r = 0;
				}

				else 
				{
					r = (int)value;
				}
				
				Color c = new Color(r, g, b);

				buff.setRGB(i, j, c.getRGB());	
			}
		}

		return buff;
	}

	// greenMeanSeperation gets the BufferedImage of the original image with the extreme pixel
	// values for green flooded or removed.  In this method, if a green pixel value is greater than
	// one magnitude of the standard deviation it is flooded with a maximum value.  A pixel value
	// that is less than one magnitude of the standard deviation is stripped to 0.  The remaining
	// pixels are then controled by the slider.  This gives the user the option to flood the image
	// with green pixels, to show only extreme values, or to show green anywhere in between.
	public BufferedImage greenMeanSeperation(ImageIcon img, double value)
	{

		BufferedImage buff = toBufferedImage(img);

		for(int i=0; i<buff.getWidth(); i++)
		{
			for(int j=0; j<buff.getHeight(); j++)
			{
				int pixel = buff.getRGB(i,j);
				int a = (pixel>>24) & 0xff;
				int r = (pixel>>16) & 0xff;
				int g = (pixel>>8) & 0xff;
				int b = pixel & 0xff;

				if(( g > this.mean + (this.sd/2)))
				{
					g = 255;
				}
				else if ((g < this.mean - (this.sd/2)))
				{
					g = 0;
				}

				else 
				{
					g = (int)value;
				}
				
				Color c = new Color(r, g, b);

				buff.setRGB(i, j, c.getRGB());	
			}
		}

		return buff;
	}

	// blueMeanSeperation gets the BufferedImage of the original image with the extreme pixel
	// values for blue flooded or removed.  In this method, if a blue pixel value is greater than
	// one magnitude of the standard deviation it is flooded with a maximum value.  A pixel value
	// that is less than one magnitude of the standard deviation is stripped to 0.  The remaining
	// pixels are then controled by the slider.  This gives the user the option to flood the image
	// with blue pixels, to show only extreme values, or to show blue anywhere in between.
	public BufferedImage blueMeanSeperation(ImageIcon img, double value)
	{

		BufferedImage buff = toBufferedImage(img);

		for(int i=0; i<buff.getWidth(); i++)
		{
			for(int j=0; j<buff.getHeight(); j++)
			{
				int pixel = buff.getRGB(i,j);
				int a = (pixel>>24) & 0xff;
				int r = (pixel>>16) & 0xff;
				int g = (pixel>>8) & 0xff;
				int b = pixel & 0xff;

				if(( b > this.mean + (this.sd/2)))
				{
					b = 255;
				}
				else if ((b < this.mean - (this.sd/2)))
				{
					b = 0;
				}

				else 
				{
					b = (int)value;
				}
				
				Color c = new Color(r, g, b);

				buff.setRGB(i, j, c.getRGB());	
			}
		}

		return buff;
	}

	// toBufferedImage converts an ImageIcon to a BufferedImage and returns the BufferedImage
	// The parameter taken is the IconImage to be converted to a BufferedImage	
	public BufferedImage toBufferedImage(ImageIcon img)
	{
		BufferedImage buff = new BufferedImage(img.getIconWidth(), img.getIconHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics g = buff.createGraphics();
		// paint the Icon to the BufferedImage.
		img.paintIcon(null, g, 0,0);
		g.dispose();
		return buff;
	}
	
	//mean returns the mean value of the true color pixel array
	public int mean()
	{
		return this.tcSum/this.counter;
	}

	// getMedian returns the median value of the array passed as a parameter. 
	// it does this by iterating the array passed and converting it into a one
	// dimensional array. From there it the array is divided by two and it is 
	// checked to see if the division was even or not.
	public double getMedian(int[][] array)
	{
		double median;

		int [] oneDim = new int[this.image.getWidth()*this.image.getHeight()];

		int position = 0;

		for(int i=0; i<this.image.getWidth(); i++)
		{
			for(int j=0; j<this.image.getHeight(); j++)
			{
				oneDim[position] = array[i][j];
				position++;
			}
		}

		int middle = oneDim.length/2;
		
		if(oneDim.length%2==1)
		{
			median = oneDim[middle];
		}
		else
		{
			median = (oneDim[middle-1] + oneDim[middle])/2.0;
		}

		return median;
	}

	// getSD returns the standard deviation of the array parameter passed.
	// This is done by iterating through the array and making the calculations
	// for variance.  Next it returns the square root of the variance.
	public double getSD(int [][] array)
	{

		double variance = 0;
		int mean = this.mean;
		int n = (this.image.getWidth()*this.image.getHeight())-1;

		int [] oneDim = new int[this.image.getWidth()*this.image.getHeight()];

		int position = 0;

		for(int i=0; i<this.image.getWidth(); i++)
		{
			for(int j=0; j<this.image.getHeight(); j++)
			{

				variance += ((array[i][j]-mean)*(array[i][j]-mean));
				
			}
		}
	
		return Math.sqrt(variance/n);
	}

	// round rounds a double value to two decimal places
	private double round(double z)
    {
    	String zee = String.format("%2.2f", z);
        return Double.valueOf(zee);
    }

    // toString returns the file instance variable to a string
    // This is primarily used in the fileLoading methods
	public String toString()
	{
		return file;
	}

	//toLabel returns an ImageIcon at the specified quadrant
	// This is used in the GUI method to get the ImageIcon and
	// set it to the JLabel
	public ImageIcon toLabel(int quadrant)
	{
		if(quadrant == 1)
		{
			return iconOne;
		}

		if(quadrant == 2)
		{
			return iconTwo;
		}

		if(quadrant == 3)
		{
			return iconThree;
		}

		if(quadrant == 4)
		{
			return iconFour;
		}

		return null;
	}

	// meanToString returns the mean value as a string
	public String meanToString()
	{
		return String.valueOf(mean);
	}

	// medianToString returns the median value as a string
	public String medianToString()
	{
		return String.valueOf(median);
	}

	// sdToString returns the standard deviation to String
	public String sdToString()
	{
		return String.valueOf(sd);
	}
}