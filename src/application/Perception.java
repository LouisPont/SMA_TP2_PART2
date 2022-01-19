package application;

public class Perception {

    private Objet obj;
    private Pheromone phe;

    public Perception(Objet obj, Pheromone phe){
        this.phe = phe;
        this.obj = obj;
    }

    public Objet getObj() {
        return obj;
    }

    public Pheromone getPheromone(){
        return phe;
    }
}
