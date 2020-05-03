package com.jiro4989.tkfm.outputViewer;

import java.io.IOException;

import com.jiro4989.tkfm.MainController;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;

public class OutputViewerAnchorPane extends AnchorPane{
	private MainController mainController;

	private OutputViewerAnchorPaneController controller;

	public OutputViewerAnchorPane(MainController aMainController) {
		super();
		mainController = aMainController;

		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("OutputViewerAnchorPane.fxml"));
			AnchorPane root = (AnchorPane) loader.load();
			controller = (OutputViewerAnchorPaneController) loader.getController();
			controller.setMainController(mainController);
			getChildren().add(root);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public OutputViewerAnchorPaneController getController() {
		return controller;
	}

}
