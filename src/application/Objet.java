package application;

import java.util.Random;

public class Objet {

    private Type label;
    private static Random rand = new Random();;

    public Objet(Type label){
        this.label = label;
    }

    public Type getType() {
        return label;
    }

    public enum Type {
        A,
        B,
        C;

        public static Type swap(Type initial) {
            Type label;
            do {
                label = values()[rand.nextInt(values().length)];
            } while (label == initial);
            return label;
        }
    }

}
