package application;

import java.util.ArrayList;
import java.util.Random;

public class Grille {

    private int N;
    private int M;
    private Objet[][] monde;

    public Grille(int N, int M, int nombreObjet){
        this.N = N;
        this.M = M;
        this.monde = new Objet[N][M];
        Random rand = new Random();
        for (int k=0 ; k < Objet.Type.values().length; k++){
            int nb, i, j;
            nb = 0;
            while (nb < nombreObjet) {
                i = rand.nextInt(N);
                j = rand.nextInt(M);
                if (monde[i][j] == null){
                    nb ++;
                    monde[i][j] = new Objet(Objet.Type.values()[k]);
                }
            }
        }
    }

    public int getN() {
        return N;
    }

    public int getM() {
        return M;
    }

    public Objet[][] getMonde() {
        return monde;
    }

    public String toString(){
        StringBuilder grille = new StringBuilder();
        for (int i=0; i<N; i++){
            grille.append("|");
            for (int j=0; j<M; j++){
                if (monde[i][j] == null) {
                    grille.append("   |");
                } else {
                    switch (monde[i][j].getType()) {
                        case A:
                            grille.append(" A |");
                            break;
                        case B:
                            grille.append(" B |");
                            break;
                    }
                }

            }
            grille.append("\n");
        }

        return grille.toString();
    }

}
