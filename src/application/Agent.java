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
    private static final float K_PLUS = 0.1f;
    private static final float K_MOINS= 0.3f;
    private static final int SIZE_MEMORY= 10;
    private static final float ERROR= 0.0f;
    private final Random rand ;
    private boolean enAttente;
    private int compteurAttente;
    private Agent camarade;


    public Agent (Environnement env){
        camarade = null;
        enAttente = false;
        compteurAttente = 0;
        rand = new Random();
        this.env = env;
        index = (++count);
        porte = false;
        memoire = new LinkedList<>();
    }

    public void resetCompteurAttente(){
        compteurAttente = 0;
    }

    public void augmenteAttente(){
        if (compteurAttente < 10) {
            compteurAttente ++;
        } else {
            enAttente = false;
            env.retirerPheromones(this);
            env.deposerObjet(this, getObjet());
            deposerObjet();
            env.libererAgents(this);
            compteurAttente = 0;
            env.deplacerAgentAleatoirement(this);
        }
    }

    public Agent getCamarade() {
        return camarade;
    }

    public void removeCamarade() {
        this.camarade = null;
    }

    public void setCamarade(Agent a ){
        this.camarade = a;
    }

    public void action(Perception p){

        Objet objSol = p.getObj();
        Pheromone phe = p.getPheromone();

        // Si l'agent aide un autre agent, il le suit
        if (camarade != null){
            env.suivreCamarade(this);
        } else {
            int probaPheromone = rand.nextInt(100);
            // Si l'agent ne porte rien et que des phéromones l'attirent
            if (!porte && phe != null && probaPheromone < phe.getIntensite() ){
                // ils gardent un pointeur l'un sur l'autre
                env.assignLink(this, phe);
                //l'agent 1 suit l'agent 2
                env.suivreCamarade(this);
            // si l'agent porte un objet et que rien n'est au sol
            } else if (porte && objSol == null){
                double probaPoser = getProbabiliteLaisse();
                Objet objTenu = getObjet();
                float proba = rand.nextFloat();
                if (proba <= probaPoser) {
                    deposerObjet();
                    env.deposerObjet(this, objTenu);
                } else {
                    env.deplacerAgentAleatoirement(this);
                }
            // Si l'agent ne porte rien que qu'un objet est au sol
            } else if (!porte && objSol != null){
                double probaPrendre = getProbabilitePrendre(objSol);
                float proba = rand.nextFloat();
                if (proba <= probaPrendre) {
                    // Si c'est un objet C, il se met en attente et relache des pheromones
                    if (objSol.getType().equals(Objet.Type.C)){
                        this.setEnAttente(true);
                        env.relacherPheromones(this);
                    }
                    ramasserObjet(objSol);
                    env.removeObject(this);
                } else {
                    env.deplacerAgentAleatoirement(this);
                }
            } else {
                env.deplacerAgentAleatoirement(this);
            }
        }
    }

    public boolean isEnAttente() {
        return enAttente;
    }

    public void setEnAttente(boolean enAttente) {
        this.enAttente = enAttente;
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

    public boolean isPorte() {
        return porte;
    }


}
