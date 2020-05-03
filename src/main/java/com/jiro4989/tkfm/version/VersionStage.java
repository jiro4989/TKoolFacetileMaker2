package com.jiro4989.tkfm.version;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class VersionStage extends Stage{
	final static String CSS_PATH = "/application/application.css";
	final static String LOGO_PATH = "/application/resources/logo.png";

	public VersionStage(){
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("VersionStage.fxml"));
			GridPane root = (GridPane)loader.load();
			Scene scene = new Scene(root,502,131);
			scene.getStylesheets().add(getClass().getResource(CSS_PATH).toExternalForm());
			setScene(scene);
			setTitle("バージョン情報");
			setResizable(false);
			initModality(Modality.APPLICATION_MODAL);
			getIcons().add(new Image(getClass().getResource(LOGO_PATH).toExternalForm()));
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
