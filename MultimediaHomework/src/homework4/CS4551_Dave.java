

import java.util.Scanner;

/*******************************************************
 CS4551 Multimedia Software Systems
 @ Author: Elaine Kang
 *******************************************************/

//
// Template Code - demonstrate how to use Image class

public class CS4551_Dave
{
  public static void main(String[] args)
  {
	// if there is no commandline argument, exit the program
	  
	  /*if(args.length != 1)
	    {
	      usage();
	      System.exit(1);
	    }*/



    System.out.println("--Welcome to Multimedia Software System--");
   
    System.out.println("--Welcome to Multimedia Software System--");
    System.out.println("Main Menu------------------------------------");
    System.out.println("1.Block-Bases Motion Compensation");
    System.out.println("2.Removing Moving Objects");
    System.out.println("3.Quit");
    System.out.println("Please enter the task number [1-3]:");
    
    Scanner input=new Scanner(System.in);
    int m=input.nextInt();
    switch (m){
    case 1:
    	System.out.println("Enter the url for the reference image:");
    	String r=input.next();
    	
    	System.out.println("Enter the url for the target image:");
    	String t=input.next();
    	
    	System.out.println("Enter the size of the each block:");
    	int n=input.nextInt();
    	
    	System.out.println("Enter the search window size");
    	int p=input.nextInt();
    	
    	Image reference = new Image(r);
        Image target = new Image(t);
        Image.routine(reference, target, n, p);
        break;
 
    case 2:
    	System.out.println("Not implemented");
    
    case 3:
    	System.exit(1);
    }

    System.out.println("--Good Bye--");
  }

  public static void usage()
  {
    System.out.println("\nUsage: java CS4551_Main [input_ppm_file]\n");
  }
}