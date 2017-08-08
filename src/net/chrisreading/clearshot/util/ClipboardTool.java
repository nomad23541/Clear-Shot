package net.chrisreading.clearshot.util;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;

import net.chrisreading.clearshot.io.ImageSelection;

/**
 * Tool for clipboard manipulation
 */
public class ClipboardTool {
	
	public static void copyImageToClipboard(Image image) {
		ImageSelection is = new ImageSelection(image);
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(is, null);
	}
	
	public static void copyStringToClipboard(String str) {
		StringSelection ss = new StringSelection(str);
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, ss);
	}

}
