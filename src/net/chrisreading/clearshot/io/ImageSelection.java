package net.chrisreading.clearshot.io;

import java.awt.Image;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

/**
 * Allows attatching an image to the
 * system's clipboard
 */
public class ImageSelection implements Transferable {
	
	private Image image;
	
	public ImageSelection(Image image) {
		this.image = image;
	}

	@Override
	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
		if(!DataFlavor.imageFlavor.equals(flavor))
			throw new UnsupportedFlavorException(flavor);
		return image;
	}

	@Override
	public DataFlavor[] getTransferDataFlavors() {
		return new DataFlavor[] { DataFlavor.imageFlavor };
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return DataFlavor.imageFlavor.equals(flavor);
	}

}
