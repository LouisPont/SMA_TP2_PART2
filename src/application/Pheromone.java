package application;

public class Pheromone {

    private final Agent source;
    private float intensite;

    private final int REDUCTION = 5;

    public Pheromone (Agent source, float intensite){
        this.source = source;
        this.intensite = intensite;
    }

    public Agent getSource() {
        return source;
    }

    public float getIntensite() {
        return intensite;
    }

    public void reduireIntensite(){
        intensite -= REDUCTION;
    }

}
