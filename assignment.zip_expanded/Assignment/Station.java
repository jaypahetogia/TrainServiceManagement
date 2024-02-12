// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a SWEN502 assignment.
// You may not distribute it in any other way without permission.

// Code for SWEN502, Assignment W2

import java.util.*;

public class Station{

    private String name;  
    private int zone;       
    private double distance;  
    private Set<TrainLine> trainLines; 

    public Station(String name, int zone, double dist){
        this.name = name;
        this.zone = zone;
        this.distance = dist;
        this.trainLines = new HashSet<>();
    }

    public String getName(){
        return this.name;
    }

    public int getZone(){
        return this.zone;
    }
    public double getDistance() {
    	return this.distance;
    }
    public boolean hasTrainLine(TrainLine trainLine) {
    	return trainLines.contains(trainLine);
    }

    public void addTrainLine(TrainLine line){
        trainLines.add(line);
    }

    public Set<TrainLine> getTrainLines(){
        return Collections.unmodifiableSet(trainLines); //Return an unmodifiable version of the set of train lines.
    }

    public String toString(){
        return name;
    }

}
