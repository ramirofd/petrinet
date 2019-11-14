package player;

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
        this.lock.lock();
        try{
            System.out.println("Fired: "+this.count+" transition: "+this.policyManager.whichTransition(this.petriNetModel.getSensibilizedTransitions()).toString());
            this.op.run();
            return (this.count > 0);
        } finally {
            this.lock.unlock();
        }
    }
}
