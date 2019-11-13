package player;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class MonitorManager {
    private int count;
    private PetriNetModel pnet;
    private Runnable op;
    private Condition[] ConditionQueue;
    private ReentrantLock lock;

    public MonitorManager(PetriNetModel pnet){
        this.pnet = pnet;
        this.count = 0;
        this.op = () -> {};
        this.lock = new ReentrantLock();

        this.ConditionQueue = new Condition[this.pnet.getAmountTransitions()];
        for(int i = 0; i < this.pnet.getAmountTransitions(); i++)
            this.ConditionQueue[i] = lock.newCondition();
    }

    public void stopAfterTransitionsFired(int number){
        this.count = number;
        this.op = () -> this.count--;
    }

    public boolean exec(Transition t){
        this.lock.lock();
        try{

        } finally {
            this.lock.unlock();
        }
        return true;
    }
}
