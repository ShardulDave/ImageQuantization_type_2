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

		switch (n) {
		case 24:
			int[] rgb = new int[3];
			//
			// Dividing a target image into blocks of 24x24
			//
			int[][][][][] blockMatrixT = new int[8][6][24][24][4];
			for (int i = 0, mi = 0; i < target.getW() || mi < 8; i = i + 24, mi++) {
				for (int j = 0, mj = 0; j < target.getH() || mj < 6; j = j + 24, mj++) {
					for (int counti = i, blocki = 0; counti < i + 24 || blocki < 24; counti++, blocki++) {
						for (int countj = j, blockj = 0; countj < j + 24 || blockj < 24; countj++, blockj++) {
							target.getPixel(counti, countj, rgb);
							int gray = (int) (Math.round(0.299 * rgb[0] + 0.587 * rgb[1] + 0.114 * rgb[2]));
							blockMatrixT[mi][mj][blocki][blockj][0] = rgb[0];
							blockMatrixT[mi][mj][blocki][blockj][1] = rgb[1];
							blockMatrixT[mi][mj][blocki][blockj][2] = rgb[2];
							blockMatrixT[mi][mj][blocki][blockj][3] = gray;
						}
					}
				}
			}

			int[][][][][] blockMatrixR = new int[8][6][24][24][4];
			for (int i = 0, mi = 0; i < target.getW() || mi < 8; i = i + 24, mi++) {
				for (int j = 0, mj = 0; j < target.getH() || mj < 6; j = j + 24, mj++) {
					for (int counti = i, blocki = 0; counti < i + 24 || blocki < 24; counti++, blocki++) {
						for (int countj = j, blockj = 0; countj < j + 24 || blockj < 24; countj++, blockj++) {
							target.getPixel(counti, countj, rgb);
							int gray = (int) (Math.round(0.299 * rgb[0] + 0.587 * rgb[1] + 0.114 * rgb[2]));
							blockMatrixR[mi][mj][blocki][blockj][0] = rgb[0];
							blockMatrixR[mi][mj][blocki][blockj][1] = rgb[1];
							blockMatrixR[mi][mj][blocki][blockj][2] = rgb[2];
							blockMatrixR[mi][mj][blocki][blockj][3] = gray;
						}
					}
				}
			}

			switch (p) {
			case 4:
				int sum_sq = 0;
				double[][] msd = new double[4][4];
				double minValue;
				int matchi = 0, matchj = 0;
				int matchtargeti,matchtargetj;
				try {
					PrintWriter writer = new PrintWriter("mv.txt", "UTF-8");
					writer.println("Name: Shardul Dave");
					writer.println("Target image name:"+target.fileName);
					writer.println("Reference image name:"+reference.fileName);
					writer.println("Number of target macro blocks: 8 x 6 (image size is 192 x 144");
					writer.println("\n");

					for (matchtargeti = 0; matchtargeti < 8; matchtargeti++) {
						for (matchtargetj = 0; matchtargetj < 6; matchtargetj++) {
							for (int mi = 0; mi < 4; mi++) {
								for (int mj = 0; mj < 4; mj++) {
									for (int blocki = 0; blocki < 24; blocki++) {
										for (int blockj = 0; blockj < 24; blockj++) {
											int refPixel = blockMatrixR[mi][mj][blocki][blockj][3];
											int targetPixel = blockMatrixT[mi][mj][blocki][blockj][3];
											int err = targetPixel - refPixel;
											sum_sq = (err * err);
										}
									}
									msd[mi][mj] = (double) sum_sq / (24 * 24);
								}
							}

							minValue = msd[0][0];

							for (int mi = 0; mi < 4; mi++) {
								for (int mj = 0; mj < 4; mj++) {
									if (msd[mi][mj] <= minValue) {
										minValue = msd[mi][mj];
										matchi = mi;
										matchj = mj;
									}
								}
							}

							int motionVectori = matchtargeti - matchi;
							int motionVectorj = matchtargetj - matchj;

							writer.print("[" + motionVectori + "," + motionVectorj + "] ");

						}
						writer.println("\n");
					}

					writer.close();
				} catch (Exception e) {
					e.getMessage();
				}
				
				

				break;
			}

			break;
		}

		// end
		return null;
	}

} // Image class