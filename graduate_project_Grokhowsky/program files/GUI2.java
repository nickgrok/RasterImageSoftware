/*
 *	This is the GUI for the Image Analyzer program written by
 *	Nicholas Grokhowsky
 *	May 3, 2018
 *	for CSCI E-10b
 *	
 *	This program enables the user to perform bandwidth analysis
 *	on an image(s) in four side by side panes.
 *
 *	All analysis functions are on the left pane of the GUI including
 * 	basic statistics and a save feature which will save the last image
 * 	that was loaded and/or altered
 *
 * 	The right pane enables the user to take notes and then save them. 
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



/*
 *	Class GUI2 is the primary class for bulding the GUI
 *	It references super() to build the JFrame in which all
 *	components are affixed
 *	
 *	For the most part colors have not been changed in the GUI in
 * 	order make viewing the images less obstructed.  Only the empty ImageIcon
 *	have had their colors changed to white to represent no image present.
 */


class GUI2 extends JFrame
{
	/*
	 *	Create instance variables for data that is input to the GUI
	 *	Variable sizeCoeff is the string value typed for the zoom feature.
	 *	It is later converted to a double for processing.
	 *
	 *	Variable notes stores the string value of the notepad on the right margin.
	 *
	 *	Variable sliderCoeff stores the slider value which is used to set the pixel 
	 *	value of pixels that are within the range of one standard deviation from 
	 *	the pixel array. All pixels outside of one standard deviation are considered
	 * 	extreme values and assigned a 0 or 255.
	 */
	private String sizeCoeff = "1";
	private String notes;
	private double sliderCoeff = 1;
	
	// Constructor method that builds the Graphical User Interface
	public GUI2()
	{

		// Instantiate the JFrame and set its title, size, and other features
		super();
		setLayout(new BorderLayout());
		String title = "Image Analyzer";
		setTitle(title);
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setResizable(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);


		/*
		 *	This section sets fonts and colors initially.
		 *	They will be changed as the program progresses.
		 *	
		 *  Also, the Backend object is instantiated here
		 */
		Font f = new Font("Helvetica", Font.BOLD, 14);
		Color c = new Color(108, 114, 124);

		Backend action = new Backend();
		/*
		 *	This section instantiates a JPanel that is used for the file input.
		 *	It nests a JPanel within a JPanel where the sub-class JPanel creates
		 *	a 'Choose File' button next to a JLabel that captures the file directory.
		 *
		 *	The JButton is attached to an ActionListener below in order to create the
		 *	action of choosing a file from a file directory and then inputing the string
		 *	into the JLabel
		 *
		 */
		JPanel inputLabel = new JPanel();
		JLabel inputFile = new JLabel("CHOOSE AN IMAGE FILE: ");
		inputLabel.add(inputFile);

		JPanel inputPanel = new JPanel(new GridLayout(2,1));
		inputFile.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		JPanel fileInputPanel = new JPanel(new FlowLayout());
		JButton fileButton = new JButton("Choose File");
		fileButton.setPreferredSize(new Dimension(100, 25));
		JLabel imgLoc = new JLabel();
		imgLoc.setPreferredSize(new Dimension(200, 25));
		imgLoc.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		fileInputPanel.add(fileButton);
		fileInputPanel.add(imgLoc);
		inputPanel.add(fileInputPanel);
		JPanel loadImagePanel = new JPanel(new FlowLayout());	
		JButton loadImage = new JButton("Load Image");
		loadImage.setPreferredSize(new Dimension(150, 50));


		/*
		 *	This section creates the parameter panel that allows the user to
		 *	choose the width and height coefficient in order to adjust the size 
		 *	of the image.
		 *
		 */
		JPanel paramPanel = new JPanel(new GridLayout(3,1));
		JLabel paramInstructions = new JLabel("ENTER ZOOM: ");
		paramInstructions.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		paramPanel.add(paramInstructions);
		JPanel sizeParamPanel = new JPanel(new BorderLayout());;
		JLabel sizeParam = new JLabel("Zoom Coefficient:  ");
		sizeParamPanel.add(sizeParam, BorderLayout.WEST);
		JTextArea sizeInput = new JTextArea();
		sizeInput.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		sizeParamPanel.add(sizeInput, BorderLayout.CENTER);
		paramPanel.add(sizeParamPanel);
		JPanel zoomPanel = new JPanel();
		JButton zoom = new JButton("Zoom");
		zoomPanel.add(zoom, BorderLayout.SOUTH);

		/*
		 *	This section builds the radio buttons that are used to choose the image, and labels them.
		 *	The ability to choose an image will allow changes to be made to each image while 
		 *	leaving other images unchanged
		 *
		 */
		JPanel buttonGroupLabelPanel = new JPanel(new GridLayout(3,1));
		JLabel buttonGroupLabel = new JLabel("CHOOSE IMAGE(S) VIEW: ");
		buttonGroupLabel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		buttonGroupLabelPanel.add(buttonGroupLabel);

		JPanel buttonGroupPanel = new JPanel(new GridLayout(3,1));
		ButtonGroup bg = new ButtonGroup();
		JRadioButton img1 = new JRadioButton("QUADRANT I");
		JRadioButton img2 = new JRadioButton("QUADRANT II");
		JRadioButton img3 = new JRadioButton("QUADRANT III");
		JRadioButton img4 = new JRadioButton("QUADRANT IV");
		buttonGroupPanel.add(img1);
		buttonGroupPanel.add(img2);
		buttonGroupPanel.add(img3);
		buttonGroupPanel.add(img4);


		/*
		 *	This sections adds the dropdown menu in order isolate the bandwidths viewed.
		 *	The functions will be applied to the image selected from the radio buttons. 
		 *	
		 *	The truecolor feature reloads the image.
		 */

		JPanel dropDownPanel = new JPanel();
		dropDownPanel.setLayout(new BorderLayout());
		JLabel mathLabel = new JLabel("CHOOSE BANDWIDTH: ");
		mathLabel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		dropDownPanel.add(mathLabel, BorderLayout.PAGE_START);
		JComboBox<String> cb = new JComboBox<String>();
			cb.addItem("True Color");
			cb.addItem("Red");
			cb.addItem("Green");
			cb.addItem("Blue");
		dropDownPanel.add(cb, BorderLayout.CENTER);	

		JPanel mathButtonPanel = new JPanel(new FlowLayout());
		JButton mathButton = new JButton("Process");
		mathButtonPanel.add(mathButton);

		/*
		 *	This block of code adds the four image panes to the center of the program
		 *	JPanel component.  The images are attached to JLabels as ImageIcons.
		 * 
		 *	An etched border is included to show seperation between the images.
		 *
		 *	Also, the background is set to white to show an empty location.  
		 */
		JPanel sliderPanel = new JPanel(new GridLayout(3,1));
		JLabel sliderCoeffLabel = new JLabel("Color Value:  ");
		JLabel detail = new JLabel("(within one standard deviation around the mean)");
		f = new Font("Helvetica", Font.PLAIN, 10);
		detail.setFont(f);
		JTextArea sliderCoeffText = new JTextArea("0");
		sliderPanel.add(sliderCoeffLabel);
		sliderPanel.add(detail);
		sliderPanel.add(sliderCoeffText);
		
		Backend slider = new Backend();

		JSlider adjustMean = new JSlider(SwingConstants.HORIZONTAL, 0, 255, 0);
		adjustMean.addChangeListener ( new ChangeListener()
					{
						public void stateChanged(ChangeEvent e)
						{
							JSlider source = (JSlider)e.getSource();
							if (!source.getValueIsAdjusting()) 
							{
            					int value = (int)source.getValue();
            					sliderCoeff = value;
            					sliderCoeffText.setText(String.valueOf(sliderCoeff));          						
							}						
						}
					}); 
				
		adjustMean.setMajorTickSpacing(50);
		adjustMean.setMinorTickSpacing(10);
		adjustMean.setPaintTicks(true);

		JPanel adjustBandwidth = new JPanel(new FlowLayout());
		JButton adjustRed = new JButton("Red Shift");
		JButton adjustGreen = new JButton("Green Shift");
		JButton adjustBlue = new JButton("Blue Shift");
		adjustBandwidth.add(adjustRed);
		adjustBandwidth.add(adjustGreen);
		adjustBandwidth.add(adjustBlue);

		
		/*
		 *	This section of code adds an output box to the bottom left pane that 
		 *	shows the calculated mean, median, and standard deviation for the pixel 
		 *	array most recently loaded.  (ig. upon loading an image and isolating 
		 *	the bandwidth the statistics will be shown for the bandwidth chosen)
		 *
		 */
		JPanel stats = new JPanel(new GridLayout(3,1));
		stats.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		JTextField mean = new JTextField("Mean: ");
		JTextField median = new JTextField("Median: ");
		JTextField sd = new JTextField("Standard Deviation: ");
		stats.add(mean);
		stats.add(median);
		stats.add(sd);

		/*
		 *	This sectoin adds a JPanel to the left JPanel and instantiates a
		 *	button within it.  The button is referenced to an ActionListener 
		 *	that will enact the save image function
		 *
		 */

		JPanel bottom = new JPanel();
		JButton visible = new JButton("Save Image");
		bottom.add(visible);

		/*
		 *	This section of code adds an output panel on the right side of the program.
		 *	The panel is a JTextField that can be used to take notes. 
		 *	
		 *  A 'save notes' button is added to save the notes taken about the images in .dat format.
		 */

		c = new Color(108, 114, 124);
		f = new Font("Helvetica", Font.BOLD, 26);

		JLabel mathOutputLabel = new JLabel("NOTES: ");
		mathOutputLabel.setFont(f);
		
		JPanel mathOutputPanel = new JPanel();
		mathOutputPanel.setBackground(c);
		mathOutputPanel.setLayout(new BoxLayout(mathOutputPanel, BoxLayout.Y_AXIS));
		JTextArea mathOutput = new JTextArea();
		mathOutput.setLineWrap(true);
		mathOutput.setWrapStyleWord(true);
		mathOutput.setPreferredSize(new Dimension(200, 750));
		mathOutput.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		mathOutputPanel.add(mathOutput);

		JPanel noteButtonPanel = new JPanel();
		JButton noteButton = new JButton("Save Notes");
		noteButtonPanel.add(noteButton);
		mathOutputPanel.add(noteButtonPanel);

		
		/*
		 *	This block of code adds the four image panes to the center of the program
		 *	JPanel component.  The images are attached to JLabels as ImageIcons.
		 * 
		 *	An etched border is included to show seperation between the images.
		 *
		 *	Also, the background is set to white to show an empty location.  
		 */

		c = new Color(255, 255, 255);

		JPanel panel = new JPanel(new GridLayout(2,2));
		panel.setBackground(c);
		add(panel, BorderLayout.CENTER);

		JLabel imgLabelOne = new JLabel(new ImageIcon());
		imgLabelOne.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panel.add(imgLabelOne);

		JLabel imgLabelTwo = new JLabel(new ImageIcon());
		imgLabelTwo.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panel.add(imgLabelTwo);

		JLabel imgLabelThree = new JLabel(new ImageIcon());
		imgLabelThree.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panel.add(imgLabelThree);

		JLabel imgLabelFour = new JLabel(new ImageIcon());
		imgLabelFour.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panel.add(imgLabelFour);


		/*
		 *	This section of code builds the structure of the GUI by attaching 
		 *	components to the JFame.  
		 *
		 *  The left JPanel is created and added in the
		 *	WEST position and most functions are attached to this left panel.
		 *  
		 *	The right JPanel is created and added in the East position and 
		 *  the notes functionality is attached to it.    
		 */
		JPanel left = new JPanel(new GridLayout(17,1));
		left.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		left.add(inputLabel);
		left.add(inputPanel);	
		left.add(loadImagePanel);	
		left.add(new JPanel());
		left.add(buttonGroupLabelPanel);
		left.add(buttonGroupPanel);
		loadImagePanel.add(loadImage);
		left.add(paramPanel);
		left.add(zoomPanel);		
		left.add(new JPanel());
		left.add(dropDownPanel);
		left.add(mathButtonPanel);
		left.add(sliderPanel);
		left.add(adjustMean);
		left.add(adjustBandwidth);		
		left.add(stats);
		left.add(new JPanel());
		left.add(bottom);
		
		// Add left JPanel to the JFrame in the WEST position
		add(left,BorderLayout.WEST);
		setVisible(true);	

		// Add right JPanel to the JFrame in the EAST position
		// Create the right JPanel and add it to the right JFrame
		JPanel right = new JPanel(new BorderLayout());
		right.add(mathOutputLabel, BorderLayout.NORTH);
		right.add(mathOutputPanel);
		add(right, BorderLayout.EAST);


		
		/*
		 *	This sectoin builds an anonymous interal classes for ActionListeners and 
		 *	DocumentListeners
		 *	
		 *  The ActionListener creates 5 variables (4 are ImageIcons 
		 *  that will be used to store the ImageIcons that are attached
		 *  to the 4 JLabels that make the 2X2 grid view of images used
		 *	to compare the images
		 *
		 *	The String s variable stores the value of the button that is 
		 *	activated and then passes it to the Backend objec that was 
		 *	created at the beginning of this GUI2 class.
		 *
		 *  The length of the ActionListener is caused by the switch
		 *  statement.  Methods were created below that have been used
		 *  inside the switch statement to reduce the length of this
		 *  code.
		 *
		 *  This is one place I woudld have liked to have been able to spend
		 *  more time to reduce the size of this method,.
		 */
		ActionListener listener = new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				String s = e.getActionCommand();
				ImageIcon image1 = new ImageIcon();
				ImageIcon image2 = new ImageIcon();
				ImageIcon image3 = new ImageIcon();
				ImageIcon image4 = new ImageIcon();
				
				switch(s)
				{
					case("Choose File"):	action.feedString(s, 0);	
					break;

					case("Load Image"):		if(imgLoc.getText().equals(""))
											{
												return;
											}	
											else
											{
												image1 = new ImageIcon();
												image2 = new ImageIcon();
												image3 = new ImageIcon();
												image4 = new ImageIcon();

												if(img1.isSelected())
												{
													loadImage(action, s, sizeCoeff, 1, imgLabelOne, image1);													
												}
												if(img2.isSelected())
												{
													loadImage(action, s, sizeCoeff, 2, imgLabelTwo, image2);
												}													
												if(img3.isSelected())
												{
													loadImage(action, s, sizeCoeff, 3, imgLabelThree, image3);													
												}
												if(img4.isSelected())
												{
													loadImage(action, s, sizeCoeff, 4, imgLabelFour, image4);
												}
											}											
					break;

				    case("Process"):		if(cb.getSelectedItem().equals("True Color"))
											{
												if(img1.isSelected())
												{
													processImage(action, imgLabelOne, s, 1, 0, image1);
												}
												if(img2.isSelected())
												{
													processImage(action, imgLabelTwo, s, 2, 0, image2);
												}
												if(img3.isSelected())
												{
													processImage(action, imgLabelThree, s, 3, 0, image3);	
												}
												if(img4.isSelected())
												{
													processImage(action, imgLabelFour, s, 4, 0, image4);	
												}
											}

											if(cb.getSelectedItem().equals("Red"))
											{
												if(img1.isSelected())
												{
													processImage(action, imgLabelOne, s, 1, 1, image1);	
												}

												if(img2.isSelected())
												{
													processImage(action, imgLabelTwo, s, 2, 1, image2);	
												}

												if(img3.isSelected())
												{
													processImage(action, imgLabelThree, s, 3, 1, image3);	
												}

												if(img4.isSelected())
												{
													processImage(action, imgLabelFour, s, 4, 1, image4);	
												}
											}
											if(cb.getSelectedItem().equals("Green"))
											{
												if(img1.isSelected())
												{
													processImage(action, imgLabelOne, s, 1, 2, image1);	
												}

												if(img2.isSelected())
												{
													processImage(action, imgLabelTwo, s, 2, 2, image2);	
												}

												if(img3.isSelected())
												{
													processImage(action, imgLabelThree, s, 3, 2, image3);	
												}

												if(img4.isSelected())
												{
													processImage(action, imgLabelFour, s, 4, 2, image4);	
												}
											}

											if(cb.getSelectedItem().equals("Blue"))
											{
												if(img1.isSelected())
												{
													processImage(action, imgLabelOne, s, 1, 3, image1);
												}

												if(img2.isSelected())
												{
													processImage(action, imgLabelTwo, s, 2, 3, image2);
												}

												if(img3.isSelected())
												{
													processImage(action, imgLabelThree, s, 3, 3, image3);
												}

												if(img4.isSelected())
												{
													processImage(action, imgLabelFour, s, 4, 3, image4);
												}
											}
					break;

					case("Zoom"):			if(img1.isSelected())
											{
												action.feedString(s, 1, Double.parseDouble(sizeCoeff));
											}
											if(img2.isSelected())
											{
												action.feedString(s, 2, Double.parseDouble(sizeCoeff));
											}
											if(img3.isSelected())
											{
												action.feedString(s, 3, Double.parseDouble(sizeCoeff));
											}
											if(img4.isSelected())
											{
												action.feedString(s, 4, Double.parseDouble(sizeCoeff));
											}

					case("Red Shift"):		if(img1.isSelected())
											{
												shiftImage(action, imgLabelOne, s, 1, sliderCoeff, image1);
											}
											if(img2.isSelected())
											{
												shiftImage(action, imgLabelTwo, s, 2, sliderCoeff, image2);
											}
											if(img3.isSelected())
											{
												shiftImage(action, imgLabelThree, s, 3, sliderCoeff, image3);
											}
											if(img4.isSelected())
											{
												shiftImage(action, imgLabelFour, s, 4, sliderCoeff, image4);	
											}
					break;

					case("Green Shift"):	if(img1.isSelected())
											{
												shiftImage(action, imgLabelOne, s, 1, sliderCoeff, image1);
											}
											if(img2.isSelected())
											{
												shiftImage(action, imgLabelTwo, s, 2, sliderCoeff, image2);	
											}
											if(img3.isSelected())
											{
												shiftImage(action, imgLabelThree, s, 3, sliderCoeff, image3);
											}
											if(img4.isSelected())
											{
												shiftImage(action, imgLabelFour, s, 4, sliderCoeff, image4);	
											}
					break;

					case("Blue Shift"):		if(img1.isSelected())
											{
												shiftImage(action, imgLabelOne, s, 1, sliderCoeff, image1);
											}
											if(img2.isSelected())
											{
												shiftImage(action, imgLabelTwo, s, 2, sliderCoeff, image2);	
											}
											if(img3.isSelected())
											{
												shiftImage(action, imgLabelThree, s, 3, sliderCoeff, image3);
											}
											if(img4.isSelected())
											{
												shiftImage(action, imgLabelFour, s, 4, sliderCoeff, image4);	
											}									
					break;

					case("Save Image"):		saveImage(action, s, img1, img2, img3, img4);
					break;

					case("Save Notes"):		action.feedString(s, notes);
					break;
				}
				
				imgLoc.setText(action.toString());
				mean.setText("Mean pixel value: " + action.meanToString());
				median.setText("Median pixel value: " + action.medianToString());
				sd.setText("SD: " + action.sdToString());
				
			}
		};

		/*
		 *	
		 *  This section adds ActionListeners to the buttons, sliders and radio buttons
		 *  
		 *  
		 */

		fileButton.addActionListener(listener);
		loadImage.addActionListener(listener);
		visible.addActionListener(listener);
		img1.addActionListener(listener);
		img2.addActionListener(listener);
		img3.addActionListener(listener);
		img4.addActionListener(listener);
		mathButton.addActionListener(listener);
		noteButton.addActionListener(listener);
		adjustRed.addActionListener(listener);
		adjustGreen.addActionListener(listener);
		adjustBlue.addActionListener(listener);
		zoom.addActionListener(listener);


		DocumentListener dListener = new DocumentListener()
		{
			public void removeUpdate(DocumentEvent e)
			{
				sizeCoeff = sizeInput.getText();
				notes = mathOutput.getText();
			}
			public void insertUpdate(DocumentEvent e)
			{
				sizeCoeff = sizeInput.getText();
				notes = mathOutput.getText();
			}
			public void changedUpdate(DocumentEvent e)
			{
				sizeCoeff = sizeInput.getText();
				notes = mathOutput.getText();
			}
		};

		/*
		 *	
		 *  This section adds DocumentListeners to text fields
		 *  
		 *  
		 */

		sizeInput.getDocument().addDocumentListener(dListener);
		mathOutput.getDocument().addDocumentListener(dListener);
	}


	/*
	 *	
	 *  This method is used to load an image into the Backend object through the action listener
	 *  Depending on the size of the sizeCoeff variable the method passes one of two feedString method
	 *	
	 *	The first feedString method takes two parameters, the string and the image quadrant and is used
	 *  for images that are not zoomed in or out
	 *
	 *  The second feedString method takes three parameters where the third is the sizeCoeff greater 0,
	 *  and is meant for images that are zoomed in or out of
	 */

	public void loadImage(Backend action, String s, String size, int quadrant, JLabel image, ImageIcon icon)
	{
		if(Double.parseDouble(sizeCoeff)<=0)
		{
			action.feedString(s, quadrant);
			icon = action.toLabel(quadrant);
			image.setIcon(icon);
		}
		else
		{
			action.feedString(s, quadrant, Double.parseDouble(size));
			icon = action.toLabel(quadrant);
			image.setIcon(icon);
		}
	}

	/*
	 *  This method is used pass a string to the Backend object that processes a single bandwidth image
	 *  (ig red pixels only)
	 *	
	 *	The parameters this method takes are the Backend object, the JLabel the images is stored on
	 *  in the GUI2 class, the string passed from the button, the image quadrant, the image function
	 *  which represents which band is being shown (red, green, or blue), and the ImageIcon that stores
	 *	the image, and is attached to the JLabel.
	 * 
	 */
	public void processImage(Backend action, JLabel image, String s, int quadrant, int function, ImageIcon icon)
	{
		action.feedString(s, quadrant, function);
		icon = action.toLabel(quadrant);
		image.setIcon(icon);
	}

	/*
	 *  This method is used pass a string to the Backend object that processes a shift in bandwidth based
	 *  on the pixels mean, median, and standard deviation in the pixel array for the selected bandwidth.
	 *	A shift variable is used (coeff), and is selected by the JSlider.  This only affects the pixels
	 *  that are within one standard deviation of the pixel array.  
	 *
	 *	The parameters this method takes are the Backend object, the JLabel the images is stored on
	 *  in the GUI2 class, the string passed from the button, the image quadrant, the shift value
	 *  and the ImageIcon that stores the image, and is attached to the JLabel.
	 * 
	 */

	public void shiftImage(Backend action, JLabel image, String s, int quadrant, double coeff, ImageIcon icon)
	{
		action.feedString(s, quadrant, coeff);
		icon = action.toLabel(quadrant);
		image.setIcon(icon);
	}

	/*
	 *  This method is used to pass a string to the Backend object that saves the image as a .jpg file.
	 *  The backend method will allow the user to choose which image quadrant to save and which file 
	 *	directory and the file name to save it as.   
	 *
	 *	The parameters this method takes are the Backend object, the string passed from the button, and one
	 *	of four image selected in the program.
	 * 
	 */


	public void saveImage(Backend action, String s, JRadioButton img1, JRadioButton img2, JRadioButton img3, JRadioButton img4)
	{
		if(img1.isSelected())
		{
			action.feedString(s, 1); 	
		}
		if(img2.isSelected())
		{
			action.feedString(s, 2);	
		}
		if(img3.isSelected())
		{
			action.feedString(s, 3);	
		}
		if(img4.isSelected())
		{
			action.feedString(s, 4);	
		}
	}

	/*
	 *  This method is used round a double value to 2 decimal places.   
	 *
	 *	The parameters this method takes are a double value and it returns a rounded double value.
	 * 
	 */

	private double round(double z)
    {
    	String zee = String.format("%2.2f", z);
        return Double.valueOf(zee);
    }

}



	

