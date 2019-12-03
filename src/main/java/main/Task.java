package main;

import java.util.ArrayList;

public class Task implements Runnable{
    private MonitorManager mon;
    private Transition[] tarray;
    private PetriNetModel pnet;
    private boolean verbose;
    private String name;

    public Task(String[] names, MonitorManager mon, PetriNetModel pnet, String taskName, boolean verbose){
        ArrayList<Transition> auxTlist = new ArrayList<>();
        for(String name: names){
            auxTlist.add(pnet.getTransition(name));
        }
        this.tarray = auxTlist.toArray(new Transition[0]);
        this.mon = mon;
        this.name = taskName;
        this.pnet = pnet;
        this.verbose = verbose;
    }

    @Override
    public void run() {
        boolean keepGoing = true;
        while(keepGoing){
            for(Transition t: this.tarray){
                keepGoing = this.mon.exec(t);
                if(!keepGoing) break;
            }
        }
        if(this.verbose)
            System.out.println
                (
                "Msg From:"+ Thread.currentThread().getName()+
                        "\nThread terminado"
                );
    }

    public String getName() {
        return name;
    }
}
