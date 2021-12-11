package sample;

import application.Environnement;
import application.Grille;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.util.Observer;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Grille grille = new Grille(50,50,600_000);
        Environnement environnement = new Environnement(grille, 20, 100);
        GrilleFX grillefx = new GrilleFX(environnement);

        Observer o = (o1, arg) -> grillefx.update();

        environnement.addObserver(o);
        environnement.start();

        primaryStage.setTitle("SMA");
        primaryStage.setResizable(false);
        primaryStage.setScene(new Scene(grillefx, 800, 800));
        primaryStage.show();

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>(){
            public void handle(WindowEvent arg0) {
                Platform.exit();
                System.exit(0);
            }
        });

        grillefx.requestFocus();
    }


    public static void main(String[] args) {
        launch(args);
    }
}


