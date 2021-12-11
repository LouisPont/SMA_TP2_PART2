package application;

import java.awt.*;
import java.util.*;
import java.util.logging.Logger;

public class Environnement extends Observable implements Runnable{

    private int sizeX;
    private int sizeY;
    private Grille grille;
    private Objet[][] monde;
    private Agent[][] emplacements;
    private ArrayList<Agent> agents;
    private Random rand;
    private int nbIteration;

    public Environnement(Grille grille, int nbAgents, int nbIteration) {
        this.nbIteration = nbIteration;
        agents = new ArrayList<>();
        this.grille = grille;
        this.monde = this.grille.getMonde();
        this.sizeX = grille.getN();
        this.sizeY = grille.getM();
        emplacements = new Agent[sizeX][sizeY];
        rand = new Random();
        int nb, i, j;
        nb = 0;
        while (nb < nbAgents) {
            i = rand.nextInt(sizeX);
            j = rand.nextInt(sizeY);
            if (emplacements[i][j] == null) {
                nb++;
                Agent a = new Agent(this);
                emplacements[i][j] = a;
                agents.add(a);
            }
        }
    }

    public Perception getPerception(Agent a){
        Point pos = getPos(a);
        return new Perception(monde[pos.x][pos.y]);
    }

    public Grille getGrille() {
        return grille;
    }

    public void deplacerAgent(Agent a){
        int newi, newj;

        Point pos = getPos(a);

        do {
            if (pos.x == 0){
                newi = rand.nextInt(2);
            } else if (pos.x == sizeX-1) {
                newi = rand.nextInt(2)-1;
            } else {
                newi = rand.nextInt(3)-1;
            }
            if (pos.y == 0){
                newj = rand.nextInt(2);
            } else if (pos.y == sizeX-1) {
                newj = rand.nextInt(2)-1;
            } else {
                newj = rand.nextInt(3)-1;
            }
        } while ((newi == 0 && newj ==0) || emplacements[pos.x+newi][pos.y+newj] != null);

        emplacements[pos.x][pos.y] = null;
        pos.x += newi;
        pos.y += newj;

        emplacements[pos.x][pos.y] = a;
    }

    public void removeObject(Agent a){
        Point pos = getPos(a);
        monde[pos.x][pos.y] = null;
    }

    public void deposerObjet(Agent a, Objet obj){
        Point pos = getPos(a);
        monde[pos.x][pos.y] = obj;
    }

    public void run(int nbIterations){
        System.out.println(grille);
        for (int i = 0; i < nbIterations; i++) {
            for (Agent a : agents){
                a.update();
            }
            setChanged();
            notifyObservers();

            try {
                synchronized(this){
                    Thread.sleep(40);
                }
            } catch (InterruptedException ex) {
                System.out.printf(ex.getMessage());
            }

        }

        System.out.println(grille);
        check();
    }

    public Point getPos(Agent a) {
        for (int i = 0; i < sizeX; i++) {
            for (int j = 0; j < sizeX; j++) {
                if (emplacements[i][j] == a) {
                    return new Point(i,j);
                }
            }
        }
        return null;
    }

    public void check() {
        int nbA, nbB;
        nbA =nbB = 0;
        for (int i =0 ; i< sizeX; i++){
            for (int j = 0; j< sizeY; j++) {
                if (monde[i][j] != null){
                    if (monde[i][j].getType().equals(Objet.Type.A)) {
                        nbA++;
                    }
                    if (monde[i][j].getType().equals(Objet.Type.B)) {
                        nbB++;
                    }
                }
            }
        }
        System.out.println("Nb A : " + nbA + " | nbB : " + nbB);
    }

    public String toString() {
        StringBuilder grille = new StringBuilder();
        for (int i = 0; i < sizeX; i++) {
            grille.append("|");
            for (int j = 0; j < sizeY; j++) {
                if (emplacements[i][j] == null) {
                    grille.append("   |");
                } else {
                    grille.append(" ").append(emplacements[i][j].getIndex()).append(" |");
                }
            }
            grille.append("\n");
        }
        return grille.toString();
    }


    @Override
    public void run() {
        this.run(this.nbIteration);
    }
    public void start() {
        new Thread(this).start();
    }
}
