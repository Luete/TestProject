package de.luete.gamebrowser.main;

import java.io.IOException;

import datatypes.Server;
import de.luete.gamebrowser.gui.MainView;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainFX extends Application {

	@Override
	public void start(Stage primaryStage) throws IOException, InterruptedException {

        ApplicationSettings.load();
        Server.loadFavs();
        
		FXMLLoader fxmlLoader = new FXMLLoader(MainView.class.getResource("MainView.fxml"));
		Parent mainView = fxmlLoader.load();
		MainView mainViewController = fxmlLoader.getController();
		mainViewController.initialize();
		
		Scene scene = new Scene(mainView);
		primaryStage.setScene(scene);
		
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
