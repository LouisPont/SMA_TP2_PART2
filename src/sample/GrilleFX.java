package sample;

import application.*;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;

public class GrilleFX extends GridPane {

    private final Grille grille;
    private final Objet[][] monde;
    private final Rectangle[][] recs;
    private final int N;
    private final int M;
    private final int sizeRecN;
    private final int sizeRecM;

    public GrilleFX(Environnement env){
        super();
        this.grille = env.getGrille();
        this.monde = this.grille.getMonde();
        this.N = grille.getN();
        this.M = grille.getM();
        this.recs = new Rectangle[N][M];
        this.sizeRecN = Math.round(800/N);
        this.sizeRecM = Math.round(800/M);

        this.init();
    }

    private void init(){
        for (int i=0; i<N; i++) {
            for (int j=0; j<M; j++){
                Rectangle rec = new Rectangle();
                this.recs[i][j] = rec;
                rec.setHeight(this.sizeRecM);
                rec.setWidth(this.sizeRecN);
                if (monde[i][j] != null){
                    if (this.monde[i][j].getType().equals(Objet.Type.A)) {
                        rec.setFill(Color.BLUE);
                    } else if (this.monde[i][j].getType().equals(Objet.Type.B)) {
                        rec.setFill(Color.RED);
                    } else if (this.monde[i][j].getType().equals(Objet.Type.C)) {
                        rec.setFill(Color.PURPLE);
                    }
                } else {
                    rec.setFill(Color.BEIGE);
                }
                this.add(rec, i, j);
            }
        }
    }

    public void update(){
        for (int i=0; i<N; i++) {
            for (int j = 0; j < M; j++) {
                recs[i][j].setStrokeType(StrokeType.INSIDE);
                if (monde[i][j] != null){
                    if (this.monde[i][j].getType().equals(Objet.Type.A)) {
                        recs[i][j].setFill(Color.BLUE);
                    } else if (this.monde[i][j].getType().equals(Objet.Type.B)) {
                        recs[i][j].setFill(Color.RED);
                    } else if (this.monde[i][j].getType().equals(Objet.Type.C)) {
                        recs[i][j].setFill(Color.PURPLE);
                    }
                } else {
                    recs[i][j].setFill(Color.BEIGE);
                }
            }
        }
    }

}
