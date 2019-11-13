package player;

public class PetriNetElement {
    String name;
    int index;

    public PetriNetElement(String name, int index) {
        this.name = name;
        this.index = index;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String toString(){
        return String.format("%d: %s", this.index, this.name);
    }


}
