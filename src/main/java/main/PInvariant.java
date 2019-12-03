package main;

public class PInvariant{
    private String[] placeArray;
    private int value;

    public PInvariant(String[] placeArray, int value){
        this.placeArray = placeArray;
        this.value = value;
    }

    public String[] getPlaceArray(){
        return this.placeArray;
    }

    public int getValue(){
        return this.value;
    }
}
