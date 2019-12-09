package main;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import java.util.ArrayList;

public class PetriNetModel {

    private final boolean verbose;
    private RealMatrix incidence;
    private RealMatrix inhibition;
    private RealVector marking;
    private RealVector sens_transitions;
    private ArrayList<Transition> t_list;
    private ArrayList<Place> p_list;
    private PrintUtils utils;
    private ArrayList<PInvariant> pInvariants;

    PetriNetModel(XMLPetriNetReader pnetreader, boolean verbose) {
        this.incidence = pnetreader.readIncidenceMatrix(verbose);
        this.inhibition = pnetreader.readInhibitions(verbose);
        this.marking = pnetreader.readMarking(verbose);
        this.t_list = pnetreader.getTransitionsList(verbose);
        this.p_list = pnetreader.getPlacesList(verbose);
        this.calculateSensibilizedTransitions();
        this.utils = new PrintUtils();
        this.verbose = verbose;
        this.pInvariants = new ArrayList<PInvariant>();
    }

    private void calculateSensibilizedTransitions(){
        this.sens_transitions = new ArrayRealVector(this.incidence.getColumnDimension());
        for(int i=0; i<this.incidence.getColumnDimension(); i++) {
            RealVector aux = new ArrayRealVector(this.marking.add(this.incidence.getColumnVector(i)).toArray());
            if(aux.getMinValue()<0){
                sens_transitions.setEntry(i,0);
            }else{
                sens_transitions.setEntry(i,1);
            }
        }
    }

    private int getPlaceMark(String name) {
        int i;
        for (i=0; i<this.p_list.size(); i++) {
            Place p = this.p_list.get(i);
            if(p.getName().equals(name)){
                break;
            }
        }
        return (int)this.marking.getEntry(i);
    }

    public boolean isSensibilized(Transition t)
    {
        return this.getSensibilizedTransitions().contains(t);
    }

    public boolean triggerTransition(Transition t) {
        if(this.isSensibilized(t))
        {
            RealVector triggeredTransition = new ArrayRealVector(this.sens_transitions.getDimension());
            triggeredTransition.setEntry(t.getIndex(), 1.);
            this.marking = this.marking.add(this.incidence.operate(triggeredTransition));
            System.out.print(t.toString()+",");
            return true;
        }
        else
        {
            return false;
        }
    }

    public ArrayList<Transition> getSensibilizedTransitions(){
        this.calculateSensibilizedTransitions();
        ArrayList<Transition> st_list = new ArrayList<>();
        for(int i=0; i<this.sens_transitions.getDimension(); i++)
        {
            if(this.sens_transitions.getEntry(i)>0)
            {
                st_list.add(this.t_list.get(i));
            }
        }
        return st_list;
    }

    public Transition getTransition(String name) {
        for (Transition t: this.t_list) {
            if(t.getName().equals(name)){
                return t;
            }
        }
        return null;
    }

    public Transition getTransition(int index) {
        for (Transition t: this.t_list) {
            if(t.getIndex()==index){
                return t;
            }
        }
        return null;
    }

    public void printTransitions() {
        for (Transition t : this.t_list) {
            System.out.println(t.toString());
        }
    }

    public void printMarking() {
        utils.printVector(this.marking);
    }


    public int getAmountTransitions() {
        return t_list.size();
    }

    public void addPlaceInvariant(PInvariant pInvariant) {
        this.pInvariants.add(pInvariant);
    }

    public boolean checkPlaceInvariants(){
        for (PInvariant pInv : pInvariants) {
            int sum=0;
            for (String placeName : pInv.getPlaceArray()) {
                sum += this.getPlaceMark(placeName);
            }
            if (sum!=pInv.getValue()) {
                return false;
            }
        }
        return true;
    }
}
