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
    public void start(Stage primaryStage) {
        Grille grille = new Grille(50,50,200);
        Environnement environnement = new Environnement(grille, 20, 250_000);
        GrilleFX grillefx = new GrilleFX(environnement);

        Observer o = (o1, arg) -> grillefx.update();

        environnement.addObserver(o);


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
        environnement.start();
    }


    public static void main(String[] args) {
        launch(args);
    }
}


