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
//        File file = new File("./files/productor_consumidor.xml");
        File file = new File("./files/4_RdP_Estacionamiento_1_entrada_1_piso_1_salida.xml");
        System.out.println("*** Selected Petri Net: " + file.getName());
        System.out.println("2) Click Start Simulation.");
        petriNetReader.setfXMLFile(file);
        PetriNetModel petriNetModel = new PetriNetModel(petriNetReader, true);

        petriNetModel.addPlaceInvariant(new PInvariant(new String[] {"espacio_para_ticket_a", "sacando_ticket_a"},  1));
        petriNetModel.addPlaceInvariant(new PInvariant(new String[] {"esperando_ticket_a", "espacio_cola_para_ticket_a"},  2));
        petriNetModel.addPlaceInvariant(new PInvariant(new String[] {"cajero", "pagando_a"},  1));
        petriNetModel.addPlaceInvariant(new PInvariant(new String[] {"ocupado_piso_1", "vacio_piso_1"},  30));
        petriNetModel.addPlaceInvariant(new PInvariant(new String[] {"subiendo_por_rampa", "rampa_disponible", "bajando_por_rampa"},  1));
        petriNetModel.addPlaceInvariant(new PInvariant(new String[] {"subiendo_por_rampa", "ocupado_piso_2", "vacio_piso_2"},  30));

        PolicyManager policyManager = new PolicyManager();
        RandomPolicy randomPolicy = new RandomPolicy();

        policyManager.addPolicy("random", randomPolicy);
        policyManager.setCurrentPolicy("random");

        MonitorManager monitor = new MonitorManager(petriNetModel, policyManager);
        monitor.stopAfterTransitionsFired(100);

        ArrayList<Task> taskList = new ArrayList<>();

        taskList.add(new Task(new String[] {"T0", "T1", "T2"}, monitor, petriNetModel, "Entrada A", true));
        taskList.add(new Task(new String[] {"T3", "T4"}, monitor, petriNetModel, "Piso 1", true));
        taskList.add(new Task(new String[] {"T8", "T9", "T10", "T11"}, monitor, petriNetModel, "Piso 2", true));
        taskList.add(new Task(new String[] {"T5", "T6"}, monitor, petriNetModel, "Salida A", true));

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
