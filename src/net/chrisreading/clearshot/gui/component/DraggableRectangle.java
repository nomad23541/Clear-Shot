package net.chrisreading.clearshot.gui.component;

import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

public class DraggableRectangle extends Rectangle {
	
	public DraggableRectangle(double x, double y, double width, double height) {
		final double handleRadius = 10;
		
		Rectangle resizeHandleNW = new Rectangle(); // top left handle
		Rectangle resizeHandleNE = new Rectangle(); // top right handle
		Rectangle resizeHandleSE = new Rectangle(); // bottom right handle
		Rectangle resizeHandleSW = new Rectangle(); // bottom left handle
		Rectangle resizeHandleN = new Rectangle(); // top handle
		Rectangle resizeHandleS = new Rectangle(); // bottom handle
		Rectangle resizeHandleW = new Rectangle(); // left handle
		Rectangle resizeHandleE = new Rectangle(); // right handle
		Circle moveHandle = new Circle(handleRadius); // move handle
		moveHandle.setFill(new Color(0, 0, 0, 0.5));
		moveHandle.setStroke(Color.WHITE);
		
		Rectangle[] handles = {resizeHandleNW, resizeHandleNE, resizeHandleN, resizeHandleSE, resizeHandleSW, resizeHandleS, resizeHandleE, resizeHandleW};
		
		this.visibleProperty().addListener((args, oldValue, newValue) -> {
			for(Rectangle r : handles) {
				r.setVisible(this.visibleProperty().get());
			}
			
			moveHandle.setVisible(this.visibleProperty().get());
		});
		
		// set look for all circles
		for(Rectangle r : handles) {
			r.setWidth(6);
			r.setHeight(6);
			r.setFill(Color.BLACK);
			r.setStroke(Color.WHITE);
		}
        
        // bind circle to their designated spots
        resizeHandleNW.xProperty().bind(xProperty().subtract(resizeHandleNW.getWidth()));
        resizeHandleNW.yProperty().bind(yProperty().subtract(resizeHandleNW.getHeight()));
        
        resizeHandleNE.xProperty().bind(xProperty().add(widthProperty()));
        resizeHandleNE.yProperty().bind(yProperty().subtract(resizeHandleNE.getHeight()));
        
        resizeHandleN.xProperty().bind(xProperty().add(widthProperty().divide(2)));
        resizeHandleN.yProperty().bind(yProperty().subtract(resizeHandleN.getHeight()));
        
        resizeHandleSW.xProperty().bind(xProperty().subtract(resizeHandleSE.getWidth()));
        resizeHandleSW.yProperty().bind(yProperty().add(heightProperty()));
        
        resizeHandleSE.xProperty().bind(xProperty().add(widthProperty()));
        resizeHandleSE.yProperty().bind(yProperty().add(heightProperty()));
        
        resizeHandleS.xProperty().bind(xProperty().add(widthProperty().divide(2)));
        resizeHandleS.yProperty().bind(yProperty().add(heightProperty()));
        
        resizeHandleE.xProperty().bind(xProperty().add(widthProperty()));
        resizeHandleE.yProperty().bind(yProperty().add(heightProperty().divide(2)));
        
        resizeHandleW.xProperty().bind(xProperty().subtract(resizeHandleW.getWidth()));
        resizeHandleW.yProperty().bind(yProperty().add(heightProperty().divide(2)));
        
        moveHandle.centerXProperty().bind(xProperty().add(widthProperty().divide(2)));
        moveHandle.centerYProperty().bind(yProperty().add(heightProperty().divide(2)));

        // set the circles into the same parent as the rectangle
        parentProperty().addListener((obs, oldParent, newParent) -> {
        	for(Rectangle r : handles) {
            	Group currentParent = (Group) r.getParent();
                if (currentParent != null)
                    currentParent.getChildren().remove(r);
                ((Group) newParent).getChildren().add(r);
            }
        	
        	// add the move handle
        	Group currentParent = (Group) moveHandle.getParent();
        	if(currentParent != null)
        		currentParent.getChildren().remove(moveHandle);
        	((Group) newParent).getChildren().add(moveHandle);
        });

        Wrapper<Point2D> mouseLocation = new Wrapper<>();
        
        // set hover cursor for each handle
        resizeHandleNW.setOnMouseEntered(event -> {
        	resizeHandleNW.getParent().setCursor(Cursor.NW_RESIZE);
        });
        
        resizeHandleNE.setOnMouseEntered(event -> {
        	resizeHandleNE.getParent().setCursor(Cursor.NE_RESIZE);
        });
        
        resizeHandleSW.setOnMouseEntered(event -> {
        	resizeHandleSW.getParent().setCursor(Cursor.SW_RESIZE);
        });
        
        resizeHandleSE.setOnMouseEntered(event -> {
        	resizeHandleSE.getParent().setCursor(Cursor.SE_RESIZE);
        });
        
        resizeHandleN.setOnMouseEntered(event -> {
        	resizeHandleN.getParent().setCursor(Cursor.N_RESIZE);
        });
        
        resizeHandleE.setOnMouseEntered(event -> {
        	resizeHandleSE.getParent().setCursor(Cursor.E_RESIZE);
        });
        
        resizeHandleS.setOnMouseEntered(event -> {
        	resizeHandleSE.getParent().setCursor(Cursor.S_RESIZE);
        });
        
        resizeHandleW.setOnMouseEntered(event -> {
        	resizeHandleSE.getParent().setCursor(Cursor.W_RESIZE);
        });
        
        moveHandle.setOnMouseEntered(event -> {
        	moveHandle.getParent().setCursor(Cursor.MOVE);
        });

        for(Rectangle r : handles) {
        	setUpDragging(r, mouseLocation);
        }
        
        // set up dragging for the move handle
        setUpDragging(moveHandle, mouseLocation);

		resizeHandleNW.setOnMouseDragged(event -> {
			if(mouseLocation.value != null) {
				double deltaX = event.getSceneX() - mouseLocation.value.getX();
				double deltaY = event.getSceneY() - mouseLocation.value.getY();
				double newX = getX() + deltaX;
				
				if(newX >= handleRadius && newX <= getX() + getWidth() - handleRadius) {
					setX(newX);
					setWidth(getWidth() - deltaX);
				}
				
				double newY = getY() + deltaY;
				
				if(newY >= handleRadius && newY <= getY() + getHeight() - handleRadius) {
					setY(newY);
					setHeight(getHeight() - deltaY);
				}
				
				mouseLocation.value = new Point2D(event.getSceneX(), event.getSceneY());
			}
		});
		
		//resizeHandleSW.centerXProperty().bind(xProperty());
       // resizeHandleSW.centerYProperty().bind(yProperty().add(heightProperty()));
		
		resizeHandleNE.setOnMouseDragged(event -> {
			if(mouseLocation.value != null) {
				double deltaX = event.getSceneX() - mouseLocation.value.getX();
				double deltaY = event.getSceneY() - mouseLocation.value.getY();
				double newX = getWidth() + deltaX;
				
				if(newX >= handleRadius && newX <= getX() + getWidth() - handleRadius) {
					setWidth(getWidth() + deltaX);
				}
				
				double newY = getY() + deltaY;
				
				if(newY >= handleRadius && newY <= getY() + getHeight() - handleRadius) {
					setY(newY);
					setHeight(getHeight() - deltaY);
				}
				
				mouseLocation.value = new Point2D(event.getSceneX(), event.getSceneY());
			}
		});
		
		resizeHandleSE.setOnMouseDragged(event -> {
			if(mouseLocation.value != null) {
				double deltaX = event.getSceneX() - mouseLocation.value.getX();
				double deltaY = event.getSceneY() - mouseLocation.value.getY();
				double newMaxX = getX() + getWidth() + deltaX;
				
				if(newMaxX >= getX() && newMaxX <= getParent().getBoundsInLocal().getWidth() - handleRadius) {
					setWidth(getWidth() + deltaX);
				}
				
				double newMaxY = getY() + getHeight() + deltaY;
				
				if(newMaxY >= getY() && newMaxY <= getParent().getBoundsInLocal().getHeight() - handleRadius) {
					setHeight(getHeight() + deltaY);
				}
				
				mouseLocation.value = new Point2D(event.getSceneX(), event.getSceneY());
			}
		});
		
		resizeHandleSW.setOnMouseDragged(event -> {
			if(mouseLocation.value != null) {
				double deltaX = event.getSceneX() - mouseLocation.value.getX();
				double deltaY = event.getSceneY() - mouseLocation.value.getY();
				double newMaxX = getX() + deltaX;
				
				if(newMaxX >= handleRadius && newMaxX <= getX() + getWidth() - handleRadius) {
					setX(newMaxX);
					setWidth(getWidth() - deltaX);
				}
				
				double newMaxY = getY() + getHeight() + deltaY;
				
				if(newMaxY >= getY() && newMaxY <= getParent().getBoundsInLocal().getHeight() - handleRadius) {
					setHeight(getHeight() + deltaY);
				}
				
				mouseLocation.value = new Point2D(event.getSceneX(), event.getSceneY());
			}
		});
		
		resizeHandleE.setOnMouseDragged(event -> {
			if(mouseLocation.value != null) {
				double deltaX = event.getSceneX() - mouseLocation.value.getX();
				double newMaxX = getX() + getWidth() + deltaX;
				
				if(newMaxX >= getX() && newMaxX <= getParent().getBoundsInLocal().getWidth() - handleRadius) {
					setWidth(getWidth() + deltaX);
				}
				
				mouseLocation.value = new Point2D(event.getSceneX(), event.getSceneY());
			}
		});
		
		resizeHandleW.setOnMouseDragged(event -> {
			if(mouseLocation.value != null) {
				double deltaX = event.getSceneX() - mouseLocation.value.getX();
				double newX = getX() + deltaX;
				
				if(newX >= handleRadius && newX <= getX() + getWidth() - handleRadius) {
					setX(newX);
					setWidth(getWidth() - deltaX);
				}
				
				mouseLocation.value = new Point2D(event.getSceneX(), event.getSceneY());
			}
		});
		
		resizeHandleN.setOnMouseDragged(event -> {
			if(mouseLocation.value != null) {
				double deltaY = event.getSceneY() - mouseLocation.value.getY();
				double newY = getY() + deltaY;
				
				if(newY >= handleRadius && newY <= getY() + getHeight() - handleRadius) {
					setY(newY);
					setHeight(getHeight() - deltaY);
				}
				
				mouseLocation.value = new Point2D(event.getSceneX(), event.getSceneY());
			}
		});
		
		resizeHandleS.setOnMouseDragged(event -> {
			if(mouseLocation.value != null) {
				double deltaY = event.getSceneY() - mouseLocation.value.getY();
				double newMaxY = getY() + getHeight() + deltaY;
				
				if(newMaxY >= getY() && newMaxY <= getParent().getBoundsInLocal().getHeight() - handleRadius) {
					setHeight(getHeight() + deltaY);
				}
				
				mouseLocation.value = new Point2D(event.getSceneX(), event.getSceneY());
			}
		});

		moveHandle.setOnMouseDragged(event -> {
			if(mouseLocation.value != null) {
				double deltaX = event.getSceneX() - mouseLocation.value.getX();
				double deltaY = event.getSceneY() - mouseLocation.value.getY();
				double newX = getX() + deltaX;
				double newMaxX = newX + getWidth();
				
				if(newX >= handleRadius && newMaxX <= getParent().getBoundsInLocal().getWidth() - handleRadius) {
					setX(newX);
				}
				
				double newY = getY() + deltaY;
				double newMaxY = newY + getHeight();
				
				if(newY >= handleRadius && newMaxY <= getParent().getBoundsInLocal().getHeight() - handleRadius) {
					setY(newY);
				}
				
				mouseLocation.value = new Point2D(event.getSceneX(), event.getSceneY());
			}

		});
    }

	private void setUpDragging(Shape circle, Wrapper<Point2D> mouseLocation) {
		circle.setOnDragDetected(event -> {
			mouseLocation.value = new Point2D(event.getSceneX(), event.getSceneY());
		});

		circle.setOnMouseReleased(event -> {
			circle.getParent().setCursor(Cursor.DEFAULT);
			mouseLocation.value = null;
		});
		
		circle.setOnMouseExited(event -> {
			circle.getParent().setCursor(Cursor.DEFAULT);
		});
	}

    static class Wrapper<T> { T value ; }

}
