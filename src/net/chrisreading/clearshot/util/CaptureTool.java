package net.chrisreading.clearshot.util;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.image.BufferedImage;

import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Bounds;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.shape.Rectangle;

/**
 * Tool that allows saving a screenshot
 */
public class CaptureTool {
	
	/**
	 * Creates a BufferedImage from a rectangle
	 * @param rect Selection to create BufferedImage from
	 * @return BufferedImage capture of selection
	 * @throws AWTException
	 */
	public static BufferedImage createCapture(Rectangle rect) {
		Bounds bounds = rect.getBoundsInParent();
		java.awt.Rectangle rectangle = new java.awt.Rectangle((int) bounds.getMinX(), (int) bounds.getMinY(), (int) bounds.getWidth(), (int) bounds.getHeight());
		
		try {
			return new Robot().createScreenCapture(rectangle);	
		} catch (AWTException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * Create a JavaFX image from a rectangle selection
	 * @param image AWT Image to convert
	 * @param clip Rectangle selection
	 * @return JavaFX Image
	 */
	public static Image getImageFromRectangle(BufferedImage image, Rectangle clip) {
		PixelReader reader = SwingFXUtils.toFXImage(image, null).getPixelReader();
		WritableImage wi = new WritableImage(reader, (int) clip.getX(), (int) clip.getY(), (int) clip.getWidth(), (int) clip.getHeight());
		
		return wi;
	}

}
