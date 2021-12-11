package application;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;

public class Agent {

    private static int count;
    private final Environnement env;
    private int index=0;
    private boolean porte;
    private Objet obj;
    private final LinkedList<String> memoire;
    private static final float K_PLUS = 0.1f;
    private static final float K_MOINS= 0.3f;
    private static final int SIZE_MEMORY= 10;
    private static final float ERROR= 0.1f;
    private final Random rand ;

    public int getIndex() {
        return index;
    }

    public Agent (Environnement env){
        rand = new Random();
        this.env = env;
        index = (++count);
        porte = false;
        memoire = new LinkedList<>();
    }

    public void action(Perception p){
        Objet objSol = p.getObj();
        double probaPrendre = 0.d;
        if (objSol != null){
            probaPrendre = getProbabilitePrendre(objSol);
        }

        Objet objTenu = getObjet();
        double probaPoser = getProbabiliteLaisse();


        if (porte && objSol == null){
            float proba = rand.nextFloat();
            if (proba <= probaPoser) {
                deposerObjet();
                env.deposerObjet(this, objTenu);
            } else {
                env.deplacerAgent(this);
            }
        } else if (!porte && objSol != null){
            float proba = rand.nextFloat();
            if (proba <= probaPrendre) {
                ramasserObjet(objSol);
                env.removeObject(this);
            } else {
                env.deplacerAgent(this);
            }
        } else {
            env.deplacerAgent(this);
        }
    }

    public void update(){
        Perception p = env.getPerception(this);
        action(p);
        if (p.getObj() != null) {
            float erreur = rand.nextFloat();
            if (erreur <= ERROR) {
                updateMemoire(Objet.Type.swap(p.getObj().getType()));
            } else {
                updateMemoire(p.getObj().getType());
            }
        } else {
            updateMemoire(null);
        }
    }

    public void updateMemoire(Objet.Type objet){
        if (memoire.size() == SIZE_MEMORY){
            memoire.removeFirst();
        }
        memoire.add( objet == null ? "0" : objet.name());
    }

    public double getProbabilitePrendre(Objet obj){
        double f = (double) Collections.frequency(memoire, obj.getType().name()) / memoire.size();
        return Math.pow((K_PLUS / (K_PLUS + f)),2);
    }

    public void ramasserObjet(Objet obj){
        this.obj = obj;
        this.porte = true;
    }

    public void deposerObjet() {
        this.obj = null;
        this.porte = false;
    }

    public Objet getObjet(){
        return this.obj;
    }

    public double getProbabiliteLaisse(){
        if (this.obj != null) {
            double f = (double) Collections.frequency(memoire, this.obj.getType().name()) / memoire.size();
            return Math.pow((f/(K_MOINS+f)),2);
        }
        return 0.d;
    }
}
