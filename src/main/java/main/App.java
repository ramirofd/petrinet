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
//        (([a-z_0-9]*,)*?)(?=(pagar_por_a(([a-z_0-9]*,)*?)(?=(avanzar_salida_a(([a-z_0-9]*,)*?)(?=salir_de_playa_a)))))
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
        monitor.stopAfterTransitionsFired(5000);

        ArrayList<Task> taskList = new ArrayList<>();

        taskList.add(new Task(new String[] {"entrar_autos_en_a", "pasar_a_sacar_ticket_a", "subir_de_a_a_piso_1"}, monitor, petriNetModel, "Entrada A", true));
        taskList.add(new Task(new String[] {"estacionar_piso_1"}, monitor, petriNetModel, "Entrar Piso 1", true));
        taskList.add(new Task(new String[] {"salir_del_piso_1"}, monitor, petriNetModel, "Salir Piso 1", true));
        taskList.add(new Task(new String[] {"subir_rampa", "estacionar_piso_2"}, monitor, petriNetModel, "Subir Piso 2", true));
        taskList.add(new Task(new String[] {"salir_del_piso_2", "bajar_rampa"}, monitor, petriNetModel, "Bajar Piso 2", true));
        taskList.add(new Task(new String[] {"pagar_por_a", "avanzar_salida_a", "salir_de_playa_a"}, monitor, petriNetModel, "Salida A:", true));

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
