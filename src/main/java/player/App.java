package player;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

/**
 * Hello world!
 *
 */
public class App 
{

    public static void main( String[] args )
    {
        XMLPetriNetReader petriNetReader = new XMLPetriNetReader();
        File file = new File("./files/mutex.xml");
        System.out.println("*** Selected Petri Net: " + file.getName());
        System.out.println("2) Click Start Simulation.");
        petriNetReader.setfXMLFile(file);
        PetriNetModel petriNetModel = new PetriNetModel(petriNetReader, true);

        PolicyManager policyManager = new PolicyManager();
        RandomPolicy randomPolicy = new RandomPolicy();

        policyManager.addPolicy("random", randomPolicy);
        policyManager.setCurrentPolicy("random");

        MonitorManager monitor = new MonitorManager(petriNetModel, policyManager);
        monitor.stopAfterTransitionsFired(50);

        ArrayList<Task> taskList = new ArrayList<>();
        taskList.add(new Task(new String[] {"T0"}, monitor, petriNetModel, "worker1", true));
        taskList.add(new Task(new String[] {"T1"}, monitor, petriNetModel, "worker1", true));
        taskList.add(new Task(new String[] {"T2"}, monitor, petriNetModel, "worker2", true));
        taskList.add(new Task(new String[] {"T3"}, monitor, petriNetModel, "worker2", true));

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
