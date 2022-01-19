package application;

import java.awt.*;
import java.util.*;

public class Environnement extends Observable implements Runnable{

    private final int sizeX;
    private final int sizeY;
    private final Grille grille;
    private final Objet[][] monde;
    private final Case[][] emplacements;
    private final Pheromone[][] pheromones;
    private final ArrayList<Agent> agents;
    private final Random rand;
    private final int nbIteration;

    private final int DISTANCE = 3;

    public Environnement(Grille grille, int nbAgents, int nbIteration) {
        this.nbIteration = nbIteration;
        agents = new ArrayList<>();
        this.grille = grille;
        this.monde = this.grille.getMonde();
        this.sizeX = grille.getN();
        this.sizeY = grille.getM();
        pheromones = new Pheromone[sizeX][sizeY];
        emplacements = new Case[sizeX][sizeY];
        for (int k = 0; k<sizeX; k++){
            for (int l = 0 ; l< sizeY; l++){
                emplacements[k][l] = new Case();
            }
        }
        rand = new Random();
        int nb, i, j;
        nb = 0;
        while (nb < nbAgents) {
            i = rand.nextInt(sizeX);
            j = rand.nextInt(sizeY);
            if (emplacements[i][j].isEmpty()) {
                nb++;
                Agent a = new Agent(this);
                emplacements[i][j].addAgent(a);
                agents.add(a);
            }
        }
    }

    public Perception getPerception(Agent a){
        Point pos = getPos(a);
        return new Perception(monde[pos.x][pos.y], pheromones[pos.x][pos.y]);
    }

    public void suivreCamarade(Agent a){
        Agent cible = a.getCamarade();
        Point pos = getPos(a);
        Point posCible = getPos(cible);
        emplacements[pos.x][pos.y].removeAgent(a);
        if (pos.x > posCible.x){
            pos.x -= 1;
        } else if (pos.x < posCible.x){
            pos.x += 1;
        }
        if (pos.y > posCible.y){
            pos.y -= 1;
        } else if (pos.y < posCible.y) {
            pos.y += 1;
        }
        emplacements[pos.x][pos.y].addAgent(a);
        checkPosition(a);
    }

    public void assignLink(Agent a, Pheromone phe){
        a.setCamarade(phe.getSource());
    }


    public void checkPosition(Agent suiveur){
        Agent cible = suiveur.getCamarade();
        Point pos = getPos(suiveur);
        Point pointCible = getPos(cible);
        // L'agent 2 arrive à la position de l'agent 1
        if (pos.x == pointCible.x && pos.y == pointCible.y){
            // Pour l'aide a porter
            if (cible.isPorte() && cible.isEnAttente()){
                cible.setEnAttente(false);
                cible.resetCompteurAttente();
                //Si plusieurs agents sont attirés, on libère tout les agents attirés par la cible
                //et on assigne de nouveau le premier arrivé
                libererAgents(cible);
                suiveur.setCamarade(cible);
            // Pour déposer l'objet
            } else if (!cible.isPorte()) {
                suiveur.removeCamarade();
            }

        }
    }

    public Grille getGrille() {
        return grille;
    }

    public String afficherAgents(){
        StringBuilder grille = new StringBuilder();
        for (int i = 0; i < sizeX; i++) {
            grille.append("|");
            for (int j = 0; j < sizeY; j++) {
                if (emplacements[i][j].isEmpty()) {
                    grille.append("   |");
                } else {
                    grille.append(" ").append("A").append(" |");
                }
            }
            grille.append("\n");
        }
        return grille.toString();
    }

    public void deplacerAgentAleatoirement(Agent a){
        int newi, newj;

        Point pos = getPos(a);
        int loop = 0;

        do {
            loop ++;
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
        } while (((newi == 0 && newj ==0) || !emplacements[pos.x+newi][pos.y+newj].isEmpty()) && loop != 10);

            emplacements[pos.x][pos.y].removeAgent(a);
            pos.x += newi;
            pos.y += newj;

            emplacements[pos.x][pos.y].addAgent(a);

    }

    public void removeObject(Agent a){
        Point pos = getPos(a);
        monde[pos.x][pos.y] = null;
    }

    public void deposerObjet(Agent a, Objet obj){
        Point pos = getPos(a);
        monde[pos.x][pos.y] = obj;
    }

    public void libererAgents(Agent a){
        for (Agent ag : agents){
            if (ag.getCamarade() == a){
                ag.removeCamarade();
            }
        }
    }

    public void run(int nbIterations) throws InterruptedException {
        int updateIter = 100;
        for (int i = 0; i < nbIterations; i++) {
            for (Agent a : agents){
                if (!a.isEnAttente()){
                    a.update();
                } else {
                    a.augmenteAttente();
                }
            }
            for (int k= 0; k<sizeX; k++) {
                for (int l = 0; l<sizeY; l++){
                    if (pheromones[k][l] != null){
                        if (pheromones[k][l].getIntensite() > 10) {
                            pheromones[k][l].reduireIntensite();
                        } else {
                            pheromones[k][l] = null;
                        }
                    }
                }
            }
            if (i%updateIter == 0) {
                setChanged();
                notifyObservers();
                Thread.sleep(1);
            }
        }
    }

    public int[] getBornes(Point pos){
        int[] bornes = new int[4];
        int borneInfX =  pos.x - DISTANCE; if(borneInfX < 0 ) { borneInfX = 0;}
        int borneSupX =  pos.x + DISTANCE; if(borneSupX > sizeX ) { borneSupX = sizeX;}
        int borneInfY =  pos.y - DISTANCE; if(borneInfY < 0 ) { borneInfY = 0;}
        int borneSupY =  pos.y + DISTANCE; if(borneSupY > sizeY ) { borneSupY = sizeY;}
        bornes[0] = borneInfX;
        bornes[1] = borneSupX;
        bornes[2] = borneInfY;
        bornes[3] = borneSupY;
        return bornes;
    }

    public void retirerPheromones(Agent a) {
        Point pos = getPos(a);
        int[] bornes = getBornes(pos);
        for (int i = bornes[0]; i < bornes[1]; i++) {
            for (int j = bornes[2]; j < bornes[3]; j++) {
                if (pheromones[i][j] != null) {
                    if (pheromones[i][j].getSource() == a){
                        pheromones[i][j] = null;
                    }
                }
            }
        }
    }

    public void relacherPheromones(Agent a) {
        Point pos = getPos(a);
        int disX, disY, distance, intensite ;
        int[] bornes = getBornes(pos);

        for (int i = bornes[0]; i < bornes[1]; i++) {
            for (int j = bornes[2]; j < bornes[3]; j++) {
                disX = Math.abs(pos.x - i);
                disY = Math.abs(pos.y - j);
                distance = Math.max(disX, disY);
                intensite = (int) calculIntensite(distance);
                pheromones[i][j] = new Pheromone(a,intensite);
            }
        }
    }

    public double calculIntensite(int distance){
        if (distance == 0 || distance == 1){
            return 100;
        } else  {
            return calculIntensite(distance-1)- ((double)100 / DISTANCE);
        }
    }

    public Point getPos(Agent a) {
        for (int i = 0; i < sizeX; i++) {
            for (int j = 0; j < sizeX; j++) {
                if (emplacements[i][j].containsAgent(a)) {
                    return new Point(i,j);
                }
            }
        }
        return null;
    }

    public String toString() {
        StringBuilder grille = new StringBuilder();
        for (int i = 0; i < sizeX; i++) {
            grille.append("|");
            for (int j = 0; j < sizeY; j++) {
                if (monde[i][j] == null) {
                    grille.append("   |");
                } else {
                    grille.append(" ").append(monde[i][j].getType()).append(" |");
                }
            }
            grille.append("\n");
        }
        return grille.toString();
    }

    @Override
    public void run() {
        try {
            this.run(this.nbIteration);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public void start() {
        new Thread(this).start();
    }
}
