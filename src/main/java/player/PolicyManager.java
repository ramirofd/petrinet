package player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Callable;

public class PolicyManager {

    private HashMap<String, Policy> policies;
    private String currentPolicy;

    public PolicyManager(){
        this.policies = new HashMap<String, Policy>();
    }

    public void addPolicy(String name, Policy p){
        this.policies.put(name, p);
    }

    public void setCurrentPolicy(String name){
        this.currentPolicy = name;
    }

    public Transition whichTransition(ArrayList<Transition> t_list) {
        return policies.get(this.currentPolicy).nextTransition(t_list);
    }
}
