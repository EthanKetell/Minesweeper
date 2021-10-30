package imageManipulator;


import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;  
import java.io.File;  
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
   
public class ImageManipulator {
	
	private static ArrayList<String> names = new ArrayList<String>();
	private static ArrayList<BufferedImage> images = new ArrayList<BufferedImage>();
	private static ArrayList<String> iconNames = new ArrayList<String>();
	private static ArrayList<Icon> icons = new ArrayList<Icon>();
	
	public static BufferedImage get(String target){
		if(names.indexOf(target) != -1){
			return images.get(names.indexOf(target));
		}
		File f = new File("resources/"+target);
		try {
			BufferedImage temp = ImageIO.read(f);
			names.add(target);
			images.add(temp);
			return temp;
		} catch (IOException e) {
			return null;
		}	
	}
	
	public static Icon getIcon(String target){
		if(iconNames.indexOf(target) != -1){
			return icons.get(iconNames.indexOf(target));
		}
		try {
			ImageIcon icon = new ImageIcon(target);
			iconNames.add(target);
			icons.add(icon);
			return icon;
		} catch (Exception e) {
			return null;
		}
	}
	
	public static BufferedImage scale(BufferedImage input, int percentScale){
		if(input == null || percentScale >= 100 || percentScale < 0){
			return input;
		}
        BufferedImage output = new BufferedImage((input.getWidth()*percentScale/100),(input.getHeight()*percentScale/100),BufferedImage.TYPE_INT_ARGB );
        Graphics2D g2d = output.createGraphics();
        AffineTransform at = new AffineTransform();
        at.translate(input.getWidth() / 2, input.getHeight() / 2);
        at.scale(percentScale/100.0, percentScale/100.0);
        at.translate(-input.getWidth()/2, -input.getHeight()/2);
        g2d.drawImage(input, at, null);
        g2d.dispose();
        return output;
	}
	
	public static BufferedImage scale(BufferedImage input, int percentScale, boolean aroundCenter){
		if(input == null || percentScale >= 100 || percentScale < 0){
			return input;
		}
        BufferedImage output = new BufferedImage((input.getWidth()*percentScale/100),(input.getHeight()*percentScale/100),BufferedImage.TYPE_INT_ARGB );
        Graphics2D g2d = output.createGraphics();
        AffineTransform at = new AffineTransform();
        if(aroundCenter){
	        at.translate(input.getWidth() / 2, input.getHeight() / 2);
	        at.scale(percentScale/100.0, percentScale/100.0);
	        at.translate(-input.getWidth()/2, -input.getHeight()/2);
        } else {
        	at.scale(percentScale/100.0, percentScale/100.0);
        }
        g2d.drawImage(input, at, null);
        g2d.dispose();
        return output;
	}
	
	public static BufferedImage colorMultiply(BufferedImage input, Color color){
		if(input == null || color == null){
			return input;
		}
		int tintARGB = color.getRGB();
		int tintRed = ((tintARGB >> 16) & 0xff);
    	int tintGreen = ((tintARGB >> 8) & 0xff);
    	int tintBlue = (tintARGB & 0xff);
		for (int x = 0; x < input.getWidth(); x++){
	        for (int y = 0; y < input.getHeight(); y++){
	        	int argb = input.getRGB(x, y);
	        	int alpha = ((argb >> 24) & 0xff);
	        	int red = ((argb >> 16) & 0xff);
	        	int green = ((argb >> 8) & 0xff);
	        	int blue = (argb & 0xff);
	        	red = ((red * tintRed)/255);
	        	green = ((green * tintGreen)/255);
	        	blue = ((blue * tintBlue)/255);
	        	argb = (alpha << 24) + (red << 16) + (green << 8) + blue;
	        	input.setRGB(x, y, argb);
	        }
	    }
		return input;
	}
	
	public static BufferedImage colorScreen(BufferedImage input, Color color){
		if(input == null || color == null){
			return input;
		}
		int tintARGB = color.getRGB();
		int tintRed = ((tintARGB >> 16) & 0xff);
    	int tintGreen = ((tintARGB >> 8) & 0xff);
    	int tintBlue = (tintARGB & 0xff);
		for (int x = 0; x < input.getWidth(); x++){
	        for (int y = 0; y < input.getHeight(); y++){
	        	int argb = input.getRGB(x, y);
	        	int alpha = ((argb >> 24) & 0xff);
	        	int red = ((argb >> 16) & 0xff);
	        	int green = ((argb >> 8) & 0xff);
	        	int blue = (argb & 0xff);
	        	red = 255 - (((255-red) * (255-tintRed))/255);
	        	green = 255 - (((255-green) * (255-tintGreen))/255);
	        	blue = 255 - (((255-blue) * (255-tintBlue))/255);
	        	argb = (alpha << 24) + (red << 16) + (green << 8) + blue;
	        	input.setRGB(x, y, argb);
	        }
	    }
		return input;
	}
	
	public static BufferedImage colorOverlay(BufferedImage input, Color color, int percentOpacity, boolean grayscaleTopLayer){
		if(input == null || color == null || percentOpacity > 100 || percentOpacity < 0){
			return input;
		}
		BufferedImage output = new BufferedImage(input.getWidth(),input.getHeight(),BufferedImage.TYPE_INT_ARGB );
		int tintARGB = color.getRGB();
		int tintRed = ((tintARGB >> 16) & 0xff);
    	int tintGreen = ((tintARGB >> 8) & 0xff);
    	int tintBlue = (tintARGB & 0xff);
    	double tRed = tintRed/255;
    	double tGreen = tintGreen/255;
    	double tBlue = tintBlue/255;
    	double dRed;
    	double dGreen;
    	double dBlue;
		for (int x = 0; x < input.getWidth(); x++){
	        for (int y = 0; y < input.getHeight(); y++){
	        	int argb = input.getRGB(x, y);
	        	int alpha = (((argb >> 24) & 0xff)*percentOpacity)/100;
	        	int red = ((argb >> 16) & 0xff);
	        	int green = ((argb >> 8) & 0xff);
	        	int blue = (argb & 0xff);
	        	if(grayscaleTopLayer){
	        		red = (int)(0.3*red + 0.6*green + .1*blue);
	        		green = red;
	        		blue = green;
	        	}
	        	dRed = red/255.0;
	        	dGreen = green/255.0;
	        	dBlue = blue/255.0;
	        	if(alpha != 0){
		        	if(dRed > 0.5){
		        		dRed = (1 - (1-2*(dRed-0.5)) * (1-tRed));
		        	} else {
		        		dRed = ((2*dRed) * tRed);
		        	}
		        	if(dGreen > 0.5){
		        		dGreen = (1 - (1-2*(dGreen-0.5)) * (1-tGreen));
		        	} else {
		        		dGreen = ((2*dGreen) * tGreen);
		        	}
		        	if(dBlue > 0.5){
		        		dBlue = (1 - (1-2*(dBlue-0.5)) * (1-tBlue));
		        	} else {
		        		dBlue = ((2*dBlue) * tBlue);
		        	}
		        	red = (int)(dRed * 255);
		        	green = (int)(dGreen * 255);
		        	blue = (int)(dBlue * 255);
		        	argb = (alpha << 24) | ((red & 0xff) << 16) | ((green & 0xff) << 8) | (blue & 0xff);
		        	output.setRGB(x, y, argb);
	        	}
	        }
	    }
		BufferedImage[] out = new BufferedImage[2];
		out[0] = input;
		out[1] = output;
		BufferedImage returnImage = merge(out);
		return returnImage;
	}
	
	public static BufferedImage colorOverlay(BufferedImage input, Color color){
		if(input == null || color == null){
			return input;
		}
		int tintARGB = color.getRGB();
		int tintRed = ((tintARGB >> 16) & 0xff);
    	int tintGreen = ((tintARGB >> 8) & 0xff);
    	int tintBlue = (tintARGB & 0xff);
    	double tRed = tintRed/255.0;
    	double tGreen = tintGreen/255.0;
    	double tBlue = tintBlue/255.0;
    	double dRed;
    	double dGreen;
    	double dBlue;
		for (int x = 0; x < input.getWidth(); x++){
	        for (int y = 0; y < input.getHeight(); y++){
	        	int argb = input.getRGB(x, y);
	        	int alpha = ((argb >> 24) & 0xff);
	        	int red = ((argb >> 16) & 0xff);
	        	int green = ((argb >> 8) & 0xff);
	        	int blue = (argb & 0xff);
	        	dRed = red/255.0;
	        	dGreen = green/255.0;
	        	dBlue = blue/255.0;
	        	if(alpha != 0){
		        	if(dRed > 0.5){
		        		dRed = (1 - (1-2*(dRed-0.5)) * (1-tRed));
		        	} else {
		        		dRed = ((2*dRed) * tRed);
		        	}
		        	if(dGreen > 0.5){
		        		dGreen = (1 - (1-2*(dGreen-0.5)) * (1-tGreen));
		        	} else {
		        		dGreen = ((2*dGreen) * tGreen);
		        	}
		        	if(dBlue > 0.5){
		        		dBlue = (1 - (1-2*(dBlue-0.5)) * (1-tBlue));
		        	} else {
		        		dBlue = ((2*dBlue) * tBlue);
		        	}
		        	red = (int)(dRed * 255);
		        	green = (int)(dGreen * 255);
		        	blue = (int)(dBlue * 255);
		        	argb = (alpha << 24) | ((red & 0xff) << 16) | ((green & 0xff) << 8) | (blue & 0xff);
		        	input.setRGB(x, y, argb);
	        	}
	        }
	    }
		return input;
	}
	
	public static BufferedImage grayscale(BufferedImage input){
		if(input == null){
			return null;
		}
		for (int x = 0; x < input.getWidth(); x++){
	        for (int y = 0; y < input.getHeight(); y++){
	        	int argb = input.getRGB(x, y);
	        	int alpha = ((argb >> 24) & 0xff);
	        	int red = ((argb >> 16) & 0xff);
	        	int green = ((argb >> 8) & 0xff);
	        	int blue = (argb & 0xff);
	        	int grayscale = (int) (0.3*red + 0.6*green + .1*blue);
	        	argb = (alpha << 24) + (grayscale << 16) + (grayscale << 8) + grayscale;
	        	input.setRGB(x, y, argb);
	        }
	    }
		return input;
	}
	
	public static BufferedImage[] getImages(String[] args){
		BufferedImage[] input = new BufferedImage[args.length];
		for ( int i = 0; i < input.length; i++ ) {  
			input[i] = get(args[i]);
        }
		return input;
	}
	
	public static BufferedImage rotate(BufferedImage input, int degrees){
		if(input == null){
			return input;
		}
		while(degrees < 0){
			degrees += 360;
		}
		while(degrees >= 360){
			degrees -= 360;
		}
		BufferedImage output = new BufferedImage(input.getWidth(),input.getHeight(),BufferedImage.TYPE_INT_ARGB );
		Graphics2D g2d = output.createGraphics();
		AffineTransform at = new AffineTransform();
        at.translate(input.getWidth() / 2, input.getHeight() / 2);
        at.rotate(Math.toRadians(degrees));
        at.translate(-input.getWidth()/2, -input.getHeight()/2);
        g2d.drawImage(input, at, null);
        g2d.dispose();
		return output;
	}
	
	public static BufferedImage flip(BufferedImage input, boolean horizontal){
		BufferedImage output = new BufferedImage(input.getWidth(),input.getHeight(),BufferedImage.TYPE_INT_ARGB );
		Graphics2D g2d = output.createGraphics();
		AffineTransform at = new AffineTransform();
		if(horizontal){
			at.scale(-1, 1);
			at.translate(-input.getWidth(), 0);
		} else {
			at.scale(1, -1);
			at.translate(0, -input.getHeight());
		}
        g2d.drawImage(input, at, null);
        g2d.dispose();
		return output;
	}
   
    public static BufferedImage merge(BufferedImage[] args) {
        BufferedImage output = new BufferedImage(args[0].getWidth(),args[0].getHeight(),BufferedImage.TYPE_INT_ARGB );  
        Graphics g = output.getGraphics();  
        for ( int i = 0; i < args.length; i++ ) {  
            while(!g.drawImage( args[i], 0, 0, null ));  
        }
        g.dispose();
        return output;
    }  
}
