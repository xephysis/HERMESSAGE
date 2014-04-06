package ac.ajou.hermessageServerTest;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;


public class Transcoding {
	
	public static final int RATIO = 0;
	public static final int SAME = -1;
	
	void ChangeImageType(String messageID,String srcFilename,String destType)
	{
		
		int index = srcFilename.indexOf('.');
		String temp = srcFilename.substring(0,index);
		String destFileName = temp +"."+ destType;
		
		File srcFp = new File("/HERMESSAGE/content/"+ messageID + "/" + srcFilename);  
		File desFp = new File("/HERMESSAGE/content/"+ messageID + "/" + destFileName);
		
		Image srcImg = null;
		
		String suffix = srcFp.getName().substring(srcFp.getName().lastIndexOf('.') + 1).toLowerCase();
		
		if (suffix.equals("jpeg") || suffix.equals("png")|| suffix.equals("gif") || suffix.equals("bmp") || suffix.equals("jpg")) {
			try {
				srcImg = ImageIO.read(srcFp);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			System.out.println("TransCoding Error");
		}

		int srcWidth = srcImg.getWidth(null);
		int srcHeight = srcImg.getHeight(null);
		

		Image imgTarget = srcImg.getScaledInstance(srcWidth, srcHeight,Image.SCALE_SMOOTH);
		
		int pixels[] = new int[srcWidth * srcHeight];
		
		PixelGrabber pg = new PixelGrabber(imgTarget, 0, 0, srcWidth,srcHeight, pixels, 0, srcWidth);
		try {
			pg.grabPixels();
		} catch (InterruptedException e) {
			try {
				throw new IOException(e.getMessage());
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		BufferedImage destImg = new BufferedImage(srcWidth, srcHeight,BufferedImage.TYPE_INT_RGB);
		
		destImg.setRGB(0, 0, srcWidth, srcHeight, pixels, 0, srcWidth);

		try {
			ImageIO.write(destImg, destType, desFp);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		System.out.println("ImageTransCoding Success !!");
		
	}
	
	void ChangImageTypeWithSize(String messageID, String srcFilename,String destType,int width, int height) throws IOException{
		
		int index = srcFilename.indexOf('.');
		String temp = srcFilename.substring(0,index);
		String destFileName = temp +"."+ destType;
		
		File srcFp = new File("/HERMESSAGE/content/"+ messageID + "/" + srcFilename);  
		File desFp = new File("/HERMESSAGE/content/"+ messageID + "/" + destFileName);
		
		Image srcImg = null;
		
		String suffix = srcFp.getName().substring(srcFp.getName().lastIndexOf('.') + 1).toLowerCase();
		
		if (suffix.equals("jpeg") || suffix.equals("png")|| suffix.equals("gif") || suffix.equals("bmp") || suffix.equals("jpg")) {
			srcImg = ImageIO.read(srcFp);
		} else {
			System.out.println("TransCoding Error");
		}

		int srcWidth = srcImg.getWidth(null);
		int srcHeight = srcImg.getHeight(null);

		int destWidth = -1, destHeight = -1;

		if (width == SAME) {
			destWidth = srcWidth;
		} else if (width > 0) {
			destWidth = width;
		}

		if (height == SAME) {
			destHeight = srcHeight;
		} else if (height > 0) {
			destHeight = height;
		}

		if (width == RATIO && height == RATIO) {
			destWidth = srcWidth;
			destHeight = srcHeight;
		} else if (width == RATIO) {
			double ratio = ((double) destHeight) / ((double) srcHeight);
			destWidth = (int) ((double) srcWidth * ratio);
		} else if (height == RATIO) {
			double ratio = ((double) destWidth) / ((double) srcWidth);
			destHeight = (int) ((double) srcHeight * ratio);
		}

		Image imgTarget = srcImg.getScaledInstance(destWidth, destHeight,Image.SCALE_SMOOTH);
		
		int pixels[] = new int[destWidth * destHeight];
		
		PixelGrabber pg = new PixelGrabber(imgTarget, 0, 0, destWidth,destHeight, pixels, 0, destWidth);
		try {
			pg.grabPixels();
		} catch (InterruptedException e) {
			throw new IOException(e.getMessage());
		}
		BufferedImage destImg = new BufferedImage(destWidth, destHeight,BufferedImage.TYPE_INT_RGB);
		
		destImg.setRGB(0, 0, destWidth, destHeight, pixels, 0, destWidth);

		ImageIO.write(destImg, destType, desFp);
		System.out.println("ImageTransCoding Success !!");
	}
	
}
