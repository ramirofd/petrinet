package main;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

/**
 * Hello world!
 *
 */
public class App 
{

    public static void main( String[] args )
    {
        XMLPetriNetReader petriNetReader = new XMLPetriNetReader();
        File file = new File("./files/productor_consumidor.xml");
        System.out.println("*** Selected Petri Net: " + file.getName());
        System.out.println("2) Click Start Simulation.");
        petriNetReader.setfXMLFile(file);
        PetriNetModel petriNetModel = new PetriNetModel(petriNetReader, true);

        petriNetModel.addPlaceInvariant(new PInvariant(new String[] {"P0", "P1"},  2));
        petriNetModel.addPlaceInvariant(new PInvariant(new String[] {"P2", "P3"},  15));

        PolicyManager policyManager = new PolicyManager();
        RandomPolicy randomPolicy = new RandomPolicy();

        policyManager.addPolicy("random", randomPolicy);
        policyManager.setCurrentPolicy("random");

        MonitorManager monitor = new MonitorManager(petriNetModel, policyManager);
        monitor.stopAfterTransitionsFired(5000);

        ArrayList<Task> taskList = new ArrayList<>();
        taskList.add(new Task(new String[] {"T0", "T1"}, monitor, petriNetModel, "Producer", true));
        for(int i=0; i<7; i++)
            taskList.add(new Task(new String[] {"T2", "T3"}, monitor, petriNetModel, "Consumer-"+i, true));

        ThreadGroup tg = new ThreadGroup("Task Threads");

        ArrayList<Thread> ThreadList = new ArrayList<>();
        Thread TaskThread;
        for(Task task: taskList){
            TaskThread = new Thread(tg, task, "Th-"+task.getName());
            TaskThread.start();
            ThreadList.add(TaskThread);
        }

        try{
            for(Thread th: ThreadList)
                th.join();
        } catch (InterruptedException e){
            e.printStackTrace();
        }
    }
}

class RandomPolicy implements Policy{
    private Random rand;

    public RandomPolicy(){
        this.rand = new Random();
    }
    @Override
    public Transition nextTransition(ArrayList<Transition> t_list) {
        return t_list.get(rand.nextInt(t_list.size()));
    }
}
