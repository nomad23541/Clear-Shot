package net.chrisreading.clearshot.gui.controller;

import javax.annotation.PostConstruct;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import net.chrisreading.clearshot.util.ClipboardTool;

public class ProgressController {
	
	private Stage stage;
	private String link;
	
	@FXML
	private Button btnClose;
	@FXML
	private Button btnCopy;
	@FXML
	private ProgressBar progressBar;
	@FXML
	private TextField fieldLink;
	@FXML
	private AnchorPane paneDetails;
	
	@FXML
	public void initialize() {}
	
	@FXML
	public void onCopy() {
		ClipboardTool.copyStringToClipboard(link);
		stage.close();
	}
	
	@FXML
	public void onClose() {
		stage.close();
	}
	
	public void setLink(String link) {
		this.link = link;
		fieldLink.setText(link);
	}
	
	public void setStage(Stage stage) {
		this.stage = stage;
	}

}
