package player;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Hello world!
 *
 */
public class App 
{
    private static File file;
    private static XMLPetriNetReader petriNetReader;
    private static PetriNetModel petriNetModel;

    public static void main( String[] args )
    {
        petriNetReader = new XMLPetriNetReader();
        file = new File("./files/mutex.xml");
        System.out.println("*** Selected Petri Net: " + file.getName());
        System.out.println("2) Click Start Simulation.");
        petriNetReader.setfXMLFile(file);
        petriNetModel = new PetriNetModel(petriNetReader, true);

        MonitorManager monitor = new MonitorManager(petriNetModel);
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
