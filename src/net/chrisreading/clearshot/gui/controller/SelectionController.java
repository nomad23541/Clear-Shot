package net.chrisreading.clearshot.gui.controller;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import net.chrisreading.clearshot.ClearShot;
import net.chrisreading.clearshot.Vars;
import net.chrisreading.clearshot.io.ImgurUpload;
import net.chrisreading.clearshot.util.CaptureTool;
import net.chrisreading.clearshot.util.ClipboardTool;

public class SelectionController {
	
	private ClearShot main;
	private Stage stage;
	private BufferedImage image;
	private Rectangle clip;
	
	@FXML
	private Button btnSave;
	@FXML
	private Button btnCopy;
	@FXML
	private Button btnUpload;
	@FXML
	private Button btnCancel;
	@FXML
	private GridPane pane;
	
	@FXML
	public void initialize() {}
	
	@FXML
	public void onSave() {
		stage.close();

		try {
			ImageIO.write(SwingFXUtils.fromFXImage(CaptureTool.getImageFromRectangle(image, clip), null), "png", new File(Vars.APP_DIRECTORY + File.separator + System.currentTimeMillis() + ".png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@FXML
	public void onCopy() {
		stage.close();
		ClipboardTool.copyImageToClipboard(SwingFXUtils.fromFXImage(CaptureTool.getImageFromRectangle(image, clip), null));
	}
	
	@FXML
	public void onUpload() {
		stage.close();
		ImgurUpload upload = new ImgurUpload(SwingFXUtils.fromFXImage(CaptureTool.getImageFromRectangle(image, clip), null));
		
		try {
			upload.upload();
			main.showProgress(upload.getLink());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		/*
		TextInputDialog dialog = new TextInputDialog(upload.getLink());
		dialog.setTitle("Upload Result");
		dialog.setHeaderText("Upload Successful");
		dialog.setContentText("Link");
		
		Button btnCopy = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
		Button btnClose = (Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL);
		
		btnCopy.setText("Copy");
		btnClose.setText("Close");
		
		btnCopy.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				ClipboardTool.copyStringToClipboard(dialog.getResult());
			}
		});
		
		dialog.showAndWait();
		*/
	}
	
	@FXML
	private void onCancel() {
		stage.close();
	}
	
	public void setCapture(BufferedImage image) {
		this.image = image;
	}
	
	public void setApplication(ClearShot main) {
		this.main = main;
	}
	
	public void setStage(Stage stage) {
		this.stage = stage;
	}
	
	public void setRectangleClip(Rectangle clip) {
		this.clip = clip;
	}

}
