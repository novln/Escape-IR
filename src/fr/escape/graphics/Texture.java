package fr.escape.graphics;

import java.awt.Image;
import java.awt.image.BufferedImage;

import java.io.File;
import java.io.IOException;

import java.util.Objects;

import javax.imageio.ImageIO;

import fr.escape.app.Disposable;

public final class Texture implements Disposable {

	private final BufferedImage image;
	
	public Texture(File file) throws IOException {
		Objects.requireNonNull(file);
		image = ImageIO.read(file);
	}
	
	public int getWidth() {
		return image.getWidth();
	}
	
	public int getHeight() {
		return image.getHeight();
	}
	
	public void dispose() {
		image.flush();
	}
	
	public Image getImage() {
		return image;
	}
	
}
