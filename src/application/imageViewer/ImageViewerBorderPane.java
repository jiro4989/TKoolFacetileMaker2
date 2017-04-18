package application.imageViewer;

import java.io.IOException;

import application.MainController;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;

public class ImageViewerBorderPane extends BorderPane{
	private MainController mainController;
	private ImageViewerBorderPaneController controller;

	public ImageViewerBorderPane(MainController aMainController) {
		super();
		mainController = aMainController;

		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("ImageViewerBorderPane.fxml"));
			BorderPane root = (BorderPane) loader.load();
			controller = (ImageViewerBorderPaneController) loader.getController();
			controller.setMainController(mainController);
			setCenter(root);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public ImageViewerBorderPaneController getController() {
		return controller;
	}
}
