package com.jiro4989.tkfm.options;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class OptionsStage extends Stage{
	final static String CSS_PATH = "application.css";
	final static String LOGO_PATH = "/application/resources/logo.png";
	private OptionsStageController controller;

	public OptionsStage(){
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("OptionsStage.fxml"));
			BorderPane root = (BorderPane)loader.load();
			controller = (OptionsStageController) loader.getController();
			Scene scene = new Scene(root,900,550);
			scene.getStylesheets().add(getClass().getResource(CSS_PATH).toExternalForm());
			setScene(scene);
			setTitle("設定変更");
			setResizable(false);
			initModality(Modality.APPLICATION_MODAL);
			getIcons().add(new Image(getClass().getResource(LOGO_PATH).toExternalForm()));
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public OptionsStage(Options options) {
		this();
		controller.setOptions(options);
	}

	public OptionsStageController getControlelr() {
		return controller;
	}
}
