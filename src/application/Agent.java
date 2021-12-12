package application;

import java.util.LinkedList;
import java.util.Random;

public class Agent {

    private static int count;
    private final Environnement env;
    private int index=0;
    private boolean porte;
    private Objet obj;
    private final LinkedList<String> memoire;
    private static final float K_PLUS = 0.9f;
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
                env.deplacerAgentAleatoirement(this);
            }
        } else if (!porte && objSol != null){
            float proba = rand.nextFloat();
            if (proba <= probaPrendre) {
                ramasserObjet(objSol);
                env.removeObject(this);
            } else {
                env.deplacerAgentAleatoirement(this);
            }
        } else {
            env.deplacerAgentAleatoirement(this);
        }
    }

    public void update(){
        Perception p = env.getPerception(this);
        action(p);
        if (p.getObj() != null) {
            updateMemoire(p.getObj().getType());
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

    public int getFrequency(Objet obj, LinkedList<String> list) {
        int counter = 0;
        for (String elem : list) {
            if (elem.equals(obj.getType().name())) {
                counter ++;
            }
        }
        return counter;
    }

    public int getFrequencyInverse(Objet obj, LinkedList<String> list) {
        int counter = 0;
        for (String elem : list) {
            if (!elem.equals(obj.getType().name()) && !elem.equals("0") ) {
                counter ++;
            }
        }
        return  counter;
    }

    public double getFERROR(Objet obj) {
        int ft = getFrequency(obj, memoire);
        int ftB = getFrequencyInverse(obj, memoire);
        return ( ft + ftB * ERROR) / memoire.size();
    }

    public double getProbabilitePrendre(Objet obj){
        double f = getFERROR(obj);
        return Math.pow((K_PLUS / (K_PLUS + f)),2);
    }

    public double getProbabiliteLaisse(){
        if (this.obj != null) {
            double f = getFERROR(obj);
            return Math.pow((f/(K_MOINS+f)),2);
        }
        return 0.d;
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


}
