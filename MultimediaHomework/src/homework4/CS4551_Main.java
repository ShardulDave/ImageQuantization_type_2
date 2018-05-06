package homework4;
/*******************************************************
 CS4551 Multimedia Software Systems
 @ Author: Elaine Kang
 *******************************************************/

//
// Template Code - demonstrate how to use Image class

public class CS4551_Main
{
  public static void main(String[] args)
  {
	// if there is no commandline argument, exit the program

    System.out.println("--Welcome to Multimedia Software System--");

    // Create an Image object with the input PPM file name.
    // Display it and write it into another PPM file.
    Image reference = new Image("/Users/shardul/Desktop/4551/IDB/Walk_001.ppm");
    Image target = new Image("/Users/shardul/Desktop/4551/IDB/Walk_002.ppm");
    
    Image[] blocks=new Image[432];
    blocks=Image.routine(reference, target, 24, 4);
    /*for(Image e:blocks) {
    	e.display();
    }*/
    //block.write2PPM("out.ppm");

    System.out.println("--Good Bye--");
  }

  public static void usage()
  {
    System.out.println("\nUsage: java CS4551_Main [input_ppm_file]\n");
  }
}