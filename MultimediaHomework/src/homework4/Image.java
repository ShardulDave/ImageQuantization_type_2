package homework4;
/*******************************************************

 CS4551 Multimedia Software Systems
 @ Author: Elaine Kang

 This image class is for a 24bit RGB image only.
 *******************************************************/

import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.image.*;
import javax.swing.*;
import javax.imageio.stream.FileImageInputStream;

// A wrapper class of BufferedImage
// Provide a couple of utility functions such as reading from and writing to PPM file

public class Image {
	private BufferedImage img;
	private String fileName; // Input file name
	private int pixelDepth = 3; // pixel depth in byte

	public Image(int w, int h)
	// create an empty image with w(idth) and h(eight)
	{
		fileName = "";
		img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		System.out.println("Created an empty image with size " + w + "x" + h);
	}

	public Image(String fn)
	// Create an image and read the data from the file
	{
		fileName = fn;
		readPPM(fileName);
		System.out.println("Created an image from " + fileName + " with size " + getW() + "x" + getH());
	}

	public int getW() {
		return img.getWidth();
	}

	public int getH() {
		return img.getHeight();
	}

	public int getSize()
	// return the image size in byte
	{
		return getW() * getH() * pixelDepth;
	}

	public void setPixel(int x, int y, byte[] rgb)
	// set byte rgb values at (x,y)
	{
		int pix = 0xff000000 | ((rgb[0] & 0xff) << 16) | ((rgb[1] & 0xff) << 8) | (rgb[2] & 0xff);
		img.setRGB(x, y, pix);
	}

	public void setPixel(int x, int y, int[] irgb)
	// set int rgb values at (x,y)
	{
		byte[] rgb = new byte[3];

		for (int i = 0; i < 3; i++)
			rgb[i] = (byte) irgb[i];

		setPixel(x, y, rgb);
	}

	public void getPixel(int x, int y, byte[] rgb)
	// retreive rgb values at (x,y) and store in the byte array
	{
		int pix = img.getRGB(x, y);

		rgb[2] = (byte) pix;
		rgb[1] = (byte) (pix >> 8);
		rgb[0] = (byte) (pix >> 16);
	}

	public void getPixel(int x, int y, int[] rgb)
	// retreive rgb values at (x,y) and store in the int array
	{
		int pix = img.getRGB(x, y);

		byte b = (byte) pix;
		byte g = (byte) (pix >> 8);
		byte r = (byte) (pix >> 16);

		// converts singed byte value (~128-127) to unsigned byte value (0~255)
		rgb[0] = (int) (0xFF & r);
		rgb[1] = (int) (0xFF & g);
		rgb[2] = (int) (0xFF & b);
	}

	public void displayPixelValue(int x, int y)
	// Display rgb pixel in unsigned byte value (0~255)
	{
		int pix = img.getRGB(x, y);

		byte b = (byte) pix;
		byte g = (byte) (pix >> 8);
		byte r = (byte) (pix >> 16);

		System.out.println(
				"RGB Pixel value at (" + x + "," + y + "):" + (0xFF & r) + "," + (0xFF & g) + "," + (0xFF & b));
	}

	public void readPPM(String fileName)
	// read a data from a PPM file
	{
		File fIn = null;
		FileImageInputStream fis = null;

		try {
			fIn = new File(fileName);
			fis = new FileImageInputStream(fIn);

			System.out.println("Reading " + fileName + "...");

			// read Identifier
			if (!fis.readLine().equals("P6")) {
				System.err.println("This is NOT P6 PPM. Wrong Format.");
				System.exit(0);
			}

			// read Comment line
			String commentString = fis.readLine();

			// read width & height
			String[] WidthHeight = fis.readLine().split(" ");
			int width = Integer.parseInt(WidthHeight[0]);
			int height = Integer.parseInt(WidthHeight[1]);

			// read maximum value
			int maxVal = Integer.parseInt(fis.readLine());

			if (maxVal != 255) {
				System.err.println("Max val is not 255");
				System.exit(0);
			}

			// read binary data byte by byte and save it into BufferedImage object
			int x, y;
			byte[] rgb = new byte[3];
			img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

			for (y = 0; y < getH(); y++) {
				for (x = 0; x < getW(); x++) {
					rgb[0] = fis.readByte();
					rgb[1] = fis.readByte();
					rgb[2] = fis.readByte();
					setPixel(x, y, rgb);
				}
			}

			fis.close();

			System.out.println("Read " + fileName + " Successfully.");

		} // try
		catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}

	public void write2PPM(String fileName)
	// wrrite the image data in img to a PPM file
	{
		FileOutputStream fos = null;
		PrintWriter dos = null;

		try {
			fos = new FileOutputStream(fileName);
			dos = new PrintWriter(fos);

			System.out.println("Writing the Image buffer into " + fileName + "...");

			// write header
			dos.print("P6" + "\n");
			dos.print("#CS451" + "\n");
			dos.print(getW() + " " + getH() + "\n");
			dos.print(255 + "\n");
			dos.flush();

			// write data
			int x, y;
			byte[] rgb = new byte[3];
			for (y = 0; y < getH(); y++) {
				for (x = 0; x < getW(); x++) {
					getPixel(x, y, rgb);
					fos.write(rgb[0]);
					fos.write(rgb[1]);
					fos.write(rgb[2]);

				}
				fos.flush();
			}
			dos.close();
			fos.close();

			System.out.println("Wrote into " + fileName + " Successfully.");

		} // try
		catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}

	public void display()
	// display the image on the screen
	{
		// Use a label to display the image
		// String title = "Image Name - " + fileName;
		String title = fileName;
		JFrame frame = new JFrame(title);
		JLabel label = new JLabel(new ImageIcon(img));
		frame.add(label, BorderLayout.CENTER);
		frame.pack();
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}

	//
	//
	// Task 1-Block based Motion Compensation
	//
	//
	//
	public static Image[] routine(Image reference, Image target, int n, int p) {

		if (!(n == 8 || n == 16 || n == 24)) {
			System.out.println("Value of block size can be 8,16 or 24");
			System.out.println("Try again..");
			System.exit(1);
		}

		if (!(p == 4 || p == 8 || p == 12 || p == 16)) {
			System.out.println("Values of search window can be 8,16 or 24");
			System.out.println("Try again..");
			System.exit(1);
		}

		Image[] blocksofTarget = new Image[432];
		Image blockofTarget;
		switch (n) {
		case 24:
			int[] rgbTarget = new int[3];
			//
			//
			// Dividing a target image and reference image into blocks of 24x24
			//
			//
			int count = 0;
			for (int i = 0; i < target.getW(); i = i + 24) {
				for (int j = 0; j < target.getH(); j = j + 24) {
					blockofTarget = new Image(24, 24);
					for (int counti = i, blocki = 0; counti < i + 24 || blocki < 24; counti++, blocki++) {
						for (int countj = j, blockj = 0; countj < j + 24 || blockj < 24; countj++, blockj++) {
							target.getPixel(counti, countj, rgbTarget);
							blockofTarget.setPixel(blocki, blockj, rgbTarget);
						}
					}
					blockofTarget.display();
					blocksofTarget[count++] = blockofTarget;
				}
			}
			//
			// End
			//

			/*switch (p) {
			case 4:
				int[] rgbref = new int[3];
				int[] rgbtarget = new int[3];
				double mad = 0, mad1 = 0;
				int[][][] MotionVector=new int[8][6][2];
				int count1 = 0;
				
				double grayref = 0, graytarget = 0;
				for (int i = 0; i < target.getW(); i = i + 24) {
					for (int j = 0; j < target.getH(); j = j + 24) {
						for (int refi = i - p; refi < i + 24 + p; refi++) {
							for (int refj = j - p; refj < j + 24 + p; refj++) {
								for (int blocki = refi, targeti = 0; blocki < refi + 24|| targeti < 24; blocki++, targeti++) {
									for (int blockj = refj, targetj = 0; blockj < refj + 24|| targetj < 24; blockj++, targetj++) {
										reference.getPixel(blocki, blockj, rgbref);
										grayref = Math.round(0.299 * rgbref[0] + 0.587 * rgbref[1] + 0.114 * rgbref[2]);
										blocksofTarget[count1++].getPixel(targeti, targetj, rgbtarget);
										graytarget = Math.round(
												0.299 * rgbtarget[0] + 0.587 * rgbtarget[1] + 0.114 * rgbtarget[2]);
										mad = mad + ((graytarget - grayref) / 576);
									}
								}
								if (mad1 < mad) {
									mad = mad1;
									MotionVector[i][j][0]=i-refi;
									MotionVector[i][j][1]=j-refj;
								}
							}
						}
					}
				}
				
				for(int i=0;i<8;i++) {
					for(int j=0;j<6;j++) {
						for(int k=0;k<2;k++) {
							System.out.println(MotionVector[i][j][k]);
						}
					}
				}

			}*/

		}

		return null;
	}

} // Image class