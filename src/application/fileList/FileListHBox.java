package application.fileList;

import java.io.IOException;

import application.MainController;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.HBox;

public class FileListHBox extends HBox{
	private MainController mainController;
	private FileListHBoxController controller;

	public FileListHBox(MainController aMainController) {
		super();
		mainController = aMainController;

		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("FileListHBox.fxml"));
			HBox root = (HBox) loader.load();
			controller = (FileListHBoxController) loader.getController();
			controller.setMainController(mainController);
			getChildren().add(root);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public FileListHBoxController getController() {
		return controller;
	}
}
