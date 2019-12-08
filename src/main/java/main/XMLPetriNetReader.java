package main;

import org.apache.commons.math3.linear.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;

@SuppressWarnings("ALL")
public class XMLPetriNetReader {

    private PrintUtils utils;
    private File fXMLFile;

    private NodeList transitionNodesList;
    private NodeList placeNodesList;
    private NodeList arcNodesList;
    private ArrayList<String> t_list;
    private ArrayList<String> p_list;

    public XMLPetriNetReader(){
        utils = new PrintUtils();
    }

    private void separateXMLFileIntoNodeLists(){
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(this.fXMLFile);
            this.transitionNodesList = doc.getElementsByTagName("transition");
            this.placeNodesList = doc.getElementsByTagName("place");
            this.arcNodesList = doc.getElementsByTagName("arc");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setfXMLFile(File fXMLFile) {

        this.fXMLFile = fXMLFile;

        this.separateXMLFileIntoNodeLists();
        this.getTransitionsList(false);
        this.getPlacesList(false);
        System.out.println("*** Found: "+this.transitionNodesList.getLength()+" transitions.");
        System.out.println("*** Found: "+this.placeNodesList.getLength()+" places.");
        System.out.println("*** Found: "+this.arcNodesList.getLength()+" arcs.");

    }

    private int getIndex(NodeList nList, String identifier){
        for (int i = 0; i < nList.getLength(); i++) {

            Node nNode = nList.item(i);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;
                String elementId = eElement.getAttribute("id");
                if (elementId.equals(identifier)) {
                    return i;
                }
            }
        }
        return -1;
    }

    public ArrayList<Transition> getTransitionsList(boolean verbose)
    {
        ArrayList<Transition> t_list = new ArrayList<>();
        this.t_list = new ArrayList<>();
        for (int i = 0; i < this.transitionNodesList.getLength(); i++) {

            Node nNode = this.transitionNodesList.item(i);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;
                String name = eElement.getAttribute("id");
                this.t_list.add(name);
                t_list.add(new Transition(name, i));
                if(verbose)
                {
                    System.out.println(String.format("%d: %s", i, name));
                }
            }
        }
        return t_list;
    }

    public ArrayList<Place> getPlacesList(boolean verbose)
    {
        ArrayList<Place> p_list = new ArrayList<>();
        this.p_list = new ArrayList<>();
        for (int i = 0; i < this.placeNodesList.getLength(); i++) {

            Node nNode = this.placeNodesList.item(i);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;
                String name = eElement.getAttribute("id");
                this.p_list.add(name);
                p_list.add(new Place(name, i));
                if(verbose)
                {
                    System.out.println(String.format("%d: %s", i, name));
                }
            }
        }
        return p_list;
    }

    public RealMatrix readIncidenceMatrix(boolean verbose){
        RealMatrix incidence = null;
        try {
            RealMatrix prevIncidence = new Array2DRowRealMatrix(this.placeNodesList.getLength(), this.transitionNodesList.getLength());
            RealMatrix postIncidence = new Array2DRowRealMatrix(this.placeNodesList.getLength(), this.transitionNodesList.getLength());

            for (int i = 0; i < this.arcNodesList.getLength(); i++) {

                Node nNode = this.arcNodesList.item(i);

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;

                    String elementId = eElement.getAttribute("id");
                    String sourceId = eElement.getAttribute("source");
                    String targetId = eElement.getAttribute("target");
                    String arc_type = eElement.getElementsByTagName("type").item(0).getAttributes().getNamedItem("value").getTextContent();

                    if(this.p_list.contains(sourceId) && arc_type.equals("normal")){
                        String[] values_content = eElement.getElementsByTagName("value").item(0).getTextContent().split(",");
                        int arcWeight = Integer.parseInt(values_content[values_content.length-1]);
                        int placeIdx = getIndex(this.placeNodesList,sourceId);
                        int transitionIdx = getIndex(this.transitionNodesList,targetId);
                        prevIncidence.setEntry(placeIdx,transitionIdx,arcWeight);
                    }

                    if(this.t_list.contains(sourceId) && arc_type.equals("normal")){
                        String[] values_content = eElement.getElementsByTagName("value").item(0).getTextContent().split(",");
                        int arcWeight = Integer.parseInt(values_content[values_content.length-1]);
                        int placeIdx = getIndex(this.placeNodesList,targetId);
                        int transitionIdx = getIndex(this.transitionNodesList,sourceId);
                        postIncidence.setEntry(placeIdx,transitionIdx,arcWeight);
                    }
                }
            }
            incidence = postIncidence.subtract(prevIncidence);
            if(verbose) {
                System.out.println("*** Backwards incidence matrix I-");
                utils.printMatrix(prevIncidence);
                System.out.println();

                System.out.println("*** Forwards incidence matrix I+");
                utils.printMatrix(postIncidence);
                System.out.println();

                System.out.println("*** Inncidence matrix I");
                utils.printMatrix(incidence);
                System.out.println();
            }else {
                System.out.println("*** Incidence Matrix succefully read.");
            }

        } catch (NullPointerException e) {
            System.out.println("!!! Could not read Incidence Matrix.");
//            e.printStackTrace();
        }
        return incidence;
    }



    public RealVector readMarking(boolean verbose){
        RealVector marking = null;
        try {
            marking = new ArrayRealVector(this.placeNodesList.getLength());
            for (int i = 0; i < this.placeNodesList.getLength(); i++) {

                Node nNode = this.placeNodesList.item(i);

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    String[] values_content = eElement.getElementsByTagName("value").item(1).getTextContent().split(",");
                    int mark = Integer.parseInt(values_content[values_content.length-1]);
                    marking.setEntry(i,mark);
                }
            }
            if(verbose) {
                System.out.println("*** Initial Marking M0");
                utils.printVector(marking);
                System.out.println();
            }else{
                System.out.println("*** Initial Marking succefully read.");
            }


        }catch (NullPointerException e){
            System.out.println("!!! Could not read Initial Marking.");
        }
        return marking;
    }

    public RealMatrix readInhibitions(boolean verbose){
        RealMatrix inhibition =  null;
        try {

            inhibition = new Array2DRowRealMatrix(this.placeNodesList.getLength(), this.transitionNodesList.getLength());
            for (int i = 0; i < this.arcNodesList.getLength(); i++) {

                Node nNode = this.arcNodesList.item(i);

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;

                    String elementId = eElement.getAttribute("id");
                    String sourceId = eElement.getAttribute("source");
                    String targetId = eElement.getAttribute("target");
                    String arc_type = eElement.getElementsByTagName("type").item(0).getAttributes().getNamedItem("value").getTextContent();

                    if(elementId.startsWith("P") && arc_type.equals("inhibitor")){
                        int placeIdx = getIndex(this.placeNodesList,sourceId);
                        int transitionIdx = getIndex(this.transitionNodesList,targetId);
                        inhibition.setEntry(placeIdx,transitionIdx,1);
                    }

                    if(elementId.startsWith("T") && arc_type.equals("inhibitor")){
                        int placeIdx = getIndex(this.placeNodesList,targetId);
                        int transitionIdx = getIndex(this.transitionNodesList,sourceId);
                        inhibition.setEntry(placeIdx,transitionIdx,1);
                    }
                }
            }
            if(verbose) {
                System.out.println("*** Inhibition matrix H");
                utils.printMatrix(inhibition);
                System.out.println();
            }else{
                System.out.println("*** Inhibition Matrix succefully read.");
            }

        }catch (NullPointerException e) {
            System.out.println("!!! Could not read Inhibition Matrix.");
        }
        return inhibition;
    }

    public void readTimeIntervals(){

    }//ToDo: TimeIntervals:  Implementar, reemplazar void por lo que corresponda.


}

