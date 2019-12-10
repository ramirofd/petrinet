package main;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import static org.junit.Assert.assertTrue;

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
            while(!this.petriNetModel.isReady(t)){
                next_t = this.policyManager.whichTransition(this.petriNetModel.getSensibilizedTransitions());
                this.ConditionQueue[next_t.getIndex()].signal();
                this.ConditionQueue[t.getIndex()].await(this.petriNetModel.getRemainingTime(t), TimeUnit.MILLISECONDS);
            }
            this.petriNetModel.triggerTransition(t);
            assertTrue(this.petriNetModel.checkPlaceInvariants());
            next_t = this.policyManager.whichTransition(this.petriNetModel.getSensibilizedTransitions());
            this.ConditionQueue[next_t.getIndex()].signal();
            this.op.run();
            if(this.count < 0){
                System.out.println
                    (Thread.currentThread().getName()+": Interrumpiendo threads");
                Thread.currentThread().getThreadGroup().interrupt();
                return false;
            }
            return true;
        } catch (InterruptedException e) {
            System.out.println(Thread.currentThread().getName()+": Thread interrumpido");
            this.petriNetModel.printMarking();
            return false;
        } catch (AssertionError e){
            System.out.println
                (Thread.currentThread().getName()+ ": [!] ERROR: Algun invariante no se cumple, terminando");
            System.exit(-1);
            return false;
        }finally {
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
