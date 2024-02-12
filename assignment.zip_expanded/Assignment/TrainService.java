// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a SWEN502 assignment.
// You may not distribute it in any other way without permission.

// Code for SWEN502, Assignment W2

import java.util.*;

public class TrainService{
   
    private TrainLine trainLine;  
    private String trainID;    
    private List<Integer> times = new ArrayList<Integer>();

    public TrainService(TrainLine line){
        trainLine = line;
    }
    
    public int getTimeForStation(Station station) {
    	int index = trainLine.getStations().indexOf(station);
    	if(index != -1) {
    		return times.get(index);
    	}
    	return -1;
    }
  
    public TrainLine getTrainLine(){
        return this.trainLine;
    }

    public String getTrainID(){
        return this.trainID;
    }

    public List<Integer> getTimes(){
        return Collections.unmodifiableList(times);  // unmodifiable version of the list of times.
    }

	public void addTime(int time, boolean firstStop){
        times.add(time);
        if (trainID==null && time != -1){
            if (firstStop) {
                trainID = trainLine.getName()+"-"+time;
            }
            else {
                time += 10000;
                trainID = trainLine.getName()+"-"+time;
            }           
        }
    }

    public int getStart(){
        for (int time : times){
            if (time!=-1){
            	return time;
            }
        }
        return -1;
    }
    
    public boolean hasStation(Station station) {
    	int index = trainLine.getStations().indexOf(station);
    	return index != -1 && times.get(index) != -1;
    }
    
     @Override
    public String toString() {
        if (trainID==null){
        	return trainLine.getName()+"-unknownStart";
        }
        int count = 0;
        for (int time : times) {
        	if (time!=-1) {
        		count++;
        }
        }
        return trainID+" ("+count+" stops)";
    }

}
