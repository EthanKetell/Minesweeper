package imageManipulator;

import java.awt.image.BufferedImage;

public class NamedImage {
	
	private final BufferedImage image;
	private final String name;

	public NamedImage(BufferedImage image, String name) {
		this.image = image;
		this.name = name;
	}
	
	public static NamedImage[] assignNames(BufferedImage[] images, String[] names){
		NamedImage[] out = new NamedImage[images.length];
		String name = "";
		int spot = 0;
		for(BufferedImage image: images){
			name = "";
			try{
				name = names[spot];
			} catch (IndexOutOfBoundsException e){}
			out[spot] = (new NamedImage(image, name));
			spot++;
		}
		return out;
	}
	
	public BufferedImage getImage(){
		return image;
	}
	
	public String getName(){
		return name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		NamedImage other = (NamedImage) obj;
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		return true;
	}
}
