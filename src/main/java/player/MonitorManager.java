package player;

import java.util.ArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class MonitorManager {
    private int count;
    private PetriNetModel petriNetModel;
    private PolicyManager policyManager;
    private Runnable op;
    private Condition[] ConditionQueue;
    private ReentrantLock lock;

    public MonitorManager(PetriNetModel petriNetModel, PolicyManager policyManager){
        this.petriNetModel = petriNetModel;
        this.policyManager = policyManager;
        this.count = 0;
        this.op = () -> {};
        this.lock = new ReentrantLock();

        this.ConditionQueue = new Condition[this.petriNetModel.getAmountTransitions()];
        for(int i = 0; i < this.petriNetModel.getAmountTransitions(); i++)
            this.ConditionQueue[i] = lock.newCondition();
    }

    public void stopAfterTransitionsFired(int number){
        this.count = number;
        this.op = () -> this.count--;
    }

    public boolean exec(Transition t){
        Transition next_t;

        this.lock.lock();

        try{
            while(!this.petriNetModel.isSensibilized(t)){
                next_t = this.policyManager.whichTransition(this.petriNetModel.getSensibilizedTransitions());
                this.ConditionQueue[next_t.getIndex()].signal();
                try {
                    this.ConditionQueue[t.getIndex()].await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            this.petriNetModel.triggerTransition(t);
            next_t = this.policyManager.whichTransition(this.petriNetModel.getSensibilizedTransitions());
            this.ConditionQueue[next_t.getIndex()].signal();
            this.op.run();
            return (this.count > 0);
        } finally {
            this.lock.unlock();
        }
    }

    private ArrayList<Transition> and(ArrayList<Transition> tList1, ArrayList<Transition> tList2) {
        ArrayList<Transition> result = new ArrayList<>();
        for (Transition t: tList1) {
            if(tList2.contains(t))
                result.add(t);
        }
        return result;
    }
}
