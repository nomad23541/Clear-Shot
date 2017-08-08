package net.chrisreading.clearshot;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import net.chrisreading.clearshot.gui.component.DraggableRectangle;
import net.chrisreading.clearshot.gui.controller.ProgressController;
import net.chrisreading.clearshot.gui.controller.SelectionController;
import net.chrisreading.clearshot.util.CaptureTool;

/**
 * Initializes gui app
 */
public class ClearShot extends Application {
	
	private Stage primaryStage;
	private TrayIcon trayIcon;
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		this.primaryStage = primaryStage;
		createTrayIcon(primaryStage);
		this.primaryStage.setTitle(Vars.APP_TITLE);
		this.primaryStage.setResizable(false);
		
		// make sure the main directory exists
		File directory = new File(Vars.APP_DIRECTORY);
		if(!directory.exists()) {
			directory.mkdirs();
		}
		
		// don't close program if all windows are closed
		Platform.setImplicitExit(false);
		//initRootLayout();
		
		GlobalScreen.registerNativeHook();
		
		Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
		logger.setLevel(Level.OFF);
		
		NativeKeyListener listener = new NativeKeyListener() {
			@Override
			public void nativeKeyPressed(NativeKeyEvent e) {
				if (e.getKeyCode() == NativeKeyEvent.VC_PAUSE) {
					if(true) {
						Platform.runLater(() -> {
							try {
								showSelection();	
							} catch (IOException e1) {
								e1.printStackTrace();
							}
						});	
					}
				}
			}
			
			@Override
			public void nativeKeyReleased(NativeKeyEvent e) {}

			@Override
  			public void nativeKeyTyped(NativeKeyEvent e) {}
		};
		
		GlobalScreen.addNativeKeyListener(listener);
	}
	
	public void showProgress(String link) throws IOException {
		FXMLLoader loader = new FXMLLoader(ClearShot.class.getResource("gui/view/ResultLayout.fxml"));
		AnchorPane pane = (AnchorPane) loader.load();
		
		Scene scene = new Scene(pane);
		scene.getStylesheets().add(ClearShot.class.getResource("gui/view/style.css").toExternalForm());
		
		Stage stage = new Stage();
		stage.setScene(scene);
		stage.getIcons().add(new javafx.scene.image.Image(ClearShot.class.getResourceAsStream("gui/view/res/icon.png")));
		stage.initStyle(StageStyle.UNIFIED);
		stage.setResizable(false);
		stage.setAlwaysOnTop(true);
		stage.show();
		
		ProgressController controller = (ProgressController) loader.getController();
		controller.setStage(stage);
		controller.setLink(link);
	}
	
	public void showSelection() throws IOException {
		FXMLLoader loader = new FXMLLoader(ClearShot.class.getResource("gui/view/SelectionLayout.fxml"));
		GridPane pane = (GridPane) loader.load();
		
		Group group = new Group();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		
		// create rectangles
		Rectangle fillBox = new Rectangle(0, 0, screenSize.getWidth(), screenSize.getHeight());
		Rectangle dragBox = new DraggableRectangle(0, 0, 0, 0);
		Rectangle clip = new Rectangle();
		
		// create screenshot of the desktop
		BufferedImage bufImage = CaptureTool.createCapture(fillBox);
		javafx.scene.image.Image image = SwingFXUtils.toFXImage(bufImage, null);
		
		// make background image darker
		ImageView backgroundImage = new ImageView(image);
		ColorAdjust colorAdjust = new ColorAdjust();
		colorAdjust.setBrightness(-0.5);
		backgroundImage.setEffect(colorAdjust);
		
		ImageView foregroundImage = new ImageView(image);
		foregroundImage.setId("foreground");
		foregroundImage.setClip(clip);
		
		// setup rectangles
		dragBox.setStroke(Color.WHITE);
		dragBox.setFill(Color.TRANSPARENT);
		dragBox.setStrokeType(StrokeType.OUTSIDE);
		dragBox.setStrokeWidth(1);
		dragBox.getStrokeDashArray().addAll(3.0, 7.0, 3.0, 7.0);
		dragBox.setVisible(false);
		
		Label lblWidth = new Label();
		Label lblSplit = new Label("x");
		Label lblHeight = new Label();
		
		GridPane labelPane = new GridPane();
		labelPane.add(lblWidth, 1, 1);
		labelPane.add(lblSplit, 2, 1);
		labelPane.add(lblHeight, 3, 1);
		labelPane.setId("labelPane");
		
		group.getChildren().addAll(backgroundImage, foregroundImage, dragBox, labelPane, pane);
		
		Scene scene = new Scene(group);
		scene.setFill(Color.TRANSPARENT);
		scene.getStylesheets().add(ClearShot.class.getResource("gui/view/style.css").toExternalForm());
		
		primaryStage = new Stage();
		primaryStage.setScene(scene);
		primaryStage.getIcons().add(new javafx.scene.image.Image(ClearShot.class.getResourceAsStream("gui/view/res/icon.png")));
		primaryStage.initStyle(StageStyle.TRANSPARENT);
		primaryStage.setMaximized(true);
		primaryStage.setAlwaysOnTop(true);
		primaryStage.show();
		
		// bind pane to dragbox
		pane.layoutXProperty().bind(dragBox.widthProperty().add(dragBox.xProperty()).subtract(pane.getWidth()));
		pane.layoutYProperty().bind(dragBox.heightProperty().add(dragBox.yProperty()));
		
		// bind clips coordinates and size to dragbox's
		clip.xProperty().bind(dragBox.xProperty());
		clip.yProperty().bind(dragBox.yProperty());
		clip.widthProperty().bind(dragBox.widthProperty());
		clip.heightProperty().bind(dragBox.heightProperty());
		
		// bind the labelpane and labels to their respective properties
		labelPane.layoutXProperty().bind(dragBox.xProperty().add(labelPane.getWidth()));
		labelPane.layoutYProperty().bind(dragBox.yProperty().subtract(labelPane.getHeight() + 5));
		lblWidth.textProperty().bind(Bindings.convert(dragBox.widthProperty()));
		lblHeight.textProperty().bind(Bindings.convert(dragBox.heightProperty()));
		
		pane.setVisible(false);
		
		SimpleDoubleProperty initX = new SimpleDoubleProperty();
		SimpleDoubleProperty initY = new SimpleDoubleProperty();
		
		primaryStage.getScene().addEventFilter(MouseEvent.ANY, new EventHandler<MouseEvent>() {
		    @Override
		    public void handle(MouseEvent mouseEvent) {
		    	if(mouseEvent.getEventType() == MouseEvent.MOUSE_PRESSED) {
		    		dragBox.setVisible(true);
		    		initX.set(mouseEvent.getX());
		    		initY.set(mouseEvent.getY());
		    		
		    	}
		    	
		    	if(mouseEvent.getEventType() == MouseEvent.MOUSE_DRAGGED) {
		    		double minX = Math.min(mouseEvent.getX(), initX.get());
		    		double minY = Math.min(mouseEvent.getY(), initY.get());
		    		double maxX = Math.max(mouseEvent.getX(), initX.get());
		    		double maxY = Math.max(mouseEvent.getY(), initY.get());
		    		
		    		dragBox.setX(minX);
		    		dragBox.setY(minY);
		    		dragBox.setWidth(maxX - minX);
		    		dragBox.setHeight(maxY - minY);
		    	}
		    	
		    	if(mouseEvent.getEventType() == MouseEvent.MOUSE_RELEASED) {
		    		primaryStage.getScene().removeEventFilter(MouseEvent.ANY, this);
		    		pane.setVisible(true);
		    	}
		    	
		    }	
		});

		SelectionController controller = (SelectionController) loader.getController();
		controller.setStage(primaryStage);
		controller.setRectangleClip(clip);
		controller.setCapture(bufImage);
		controller.setApplication(this);
	}
	
	/**
	 * Creates a system tray for a specified stage
	 * using AWT
	 * @param stage Stage to use
	 */
	public void createTrayIcon(Stage stage) {
		if(SystemTray.isSupported()) {
			SystemTray tray = SystemTray.getSystemTray();
			Image image = Toolkit.getDefaultToolkit().getImage(ClearShot.class.getResource("gui/view/res/icon.png")).getScaledInstance(16, 16, Image.SCALE_DEFAULT);
			
			stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
				@Override
				public void handle(WindowEvent event) {
					hide(stage);
				}
			});
			
			ActionListener closeListener = new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						GlobalScreen.unregisterNativeHook();
					} catch (NativeHookException e1) {
						e1.printStackTrace();
					}
					
					System.exit(0);
				}
			};
			
			ActionListener showListener = new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Platform.runLater(() -> {
						try {
							showSelection();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					});
				}
			};
			
			PopupMenu popup = new PopupMenu();
			
			MenuItem itemShow = new MenuItem("Take Screenshot");
			itemShow.addActionListener(showListener);
			popup.add(itemShow);
			
			MenuItem itemClose = new MenuItem("Close");
			itemClose.addActionListener(closeListener);
			popup.add(itemClose);
			
			trayIcon = new TrayIcon(image, Vars.APP_TITLE, popup);
			trayIcon.addActionListener(showListener);
			
			try {
				tray.add(trayIcon);	
			} catch (AWTException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void hide(Stage stage) {
		Platform.runLater(() -> {
			if(SystemTray.isSupported()) {
				stage.hide();
			} else {
				System.exit(0);
			}
		});
	}
	
	public static void main(String[] args) throws InterruptedException, IOException {
		Application.launch(args);
	}

}
