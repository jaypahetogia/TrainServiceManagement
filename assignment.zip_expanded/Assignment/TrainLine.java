// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a SWEN502 assignment.
// You may not distribute it in any other way without permission.

// Code for SWEN502, Assignment W2

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TrainLine{
   
    private String name;
    private List<Station> stations = new ArrayList<>();           
    private List<TrainService> services = new ArrayList<>();

    public TrainLine(String name){
        this.name = name;
    }
public boolean connectsStations(Station start, Station end) {
	return stations.contains(start) && stations.contains(end);
}

    public void addTrainService(TrainService service){
        this.services.add(service);
    }
    
  public TrainService getNextService(Station station, int time) {
    	TrainService nextService = null;
    	for(TrainService service : services) {
    		int serviceTime = service.getTimeForStation(station);
    		if(serviceTime > time) {
    			if(nextService == null || serviceTime < nextService.getTimeForStation(station)) {
    				nextService = service;
    			}
    		}
    	}
    	return nextService;
    }
  
    public void addStation(Station station){
        if(!stations.contains(station)) {
        stations.add(station);
        station.addTrainLine(this);
    }
    }
    public boolean hasStation(Station station) {
    	return this.stations.contains(station);
    }

    public String getName(){
        return this.name;
    }

    public List<Station> getStations(){
        return Collections.unmodifiableList(stations); // an unmodifiable version of the list of stations
    }

    public List<TrainService> getTrainServices(){
        return Collections.unmodifiableList(services); // an unmodifiable version of the list of trainServices
    }

    public String toString(){
        return (name+" ("+stations.size()+" stations, "+services.size()+" services)");
    }

}
