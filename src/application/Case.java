package application;

import java.util.LinkedList;

public class Case {

    private LinkedList<Agent> emplacements;

    public Case (){
        emplacements = new LinkedList<>();
    }

    public void addAgent(Agent a){
        emplacements.add(a);
    }

    public void removeAgent(Agent a){
        emplacements.remove(a);
    }

    public boolean isEmpty(){
        return emplacements.isEmpty();
    }

    public boolean containsAgent(Agent a){
        return emplacements.contains(a);
    }

    public Agent getTop() { return emplacements.get(0);}
}
