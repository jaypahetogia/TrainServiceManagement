import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import ecs100.UI;

public class Queries {
	private Map<String, Station> stations = new HashMap<>();
	private Map<String, TrainLine>trainLines = new HashMap<>();
	private Map<Integer, Double> farePrices = new HashMap<>();
	private double zoomLevel = 1.0;
	private String currentImage;
	private static final String geoImage1 = "geo";
	private static final String sysImage1 = "system";
	
public Queries() {
	readStationsData();
	readAllTrainLines();
	actions(); //where all our execution takes place 
	setupSlider();
}

//--------------------->>stations.data = station name, zone, distance<<-------------------------------------------
	public void readStationsData() {
			try {
				Scanner scan = new Scanner(new File("/Users/admin/Downloads/Assignment/Train network data/stations.data"));
				while (scan.hasNext()) {
					String stationName = scan.next();
					int zone = scan.nextInt();
					double distance = scan.nextDouble();
					Station station = new Station(stationName, zone, distance);
					stations.put(stationName, station);
					}
			scan.close();
				
			} catch (FileNotFoundException e) {
				UI.println("Error reading data: " + e.getMessage());
				e.printStackTrace();
			}
}
	//---------------------->>>method for reading all train line -stations.data<<<-----------------------------------
	private void readStationsForTrainline(TrainLine trainLine) {
		String fileName = "/Users/admin/Downloads/Assignment/Train network data/"+trainLine.getName()+"-stations.data";
	try (Scanner scan = new Scanner(new File(fileName))) {
		while(scan.hasNextLine()) {
			String stationName = scan.nextLine().trim();
			Station station = stations.get(stationName);
			if(station != null) {
				trainLine.addStation(station);
			}
		}
	} catch (FileNotFoundException e) {
		UI.println("File not found: " + e.getMessage());
	}
	}
	//---------------------->>>method for reading all train line -services.data<<<------------------------------------
	private void readTrainServicesForTrainLine(TrainLine trainLine) {
		String fileName = "/Users/admin/Downloads/Assignment/Train network data/"+trainLine.getName()+"-services.data";
		try(Scanner scan = new Scanner(new File(fileName))) {
			while(scan.hasNextLine()) {
				String[] services = scan.nextLine().split(" ");
				TrainService service = new TrainService(trainLine);
				for(int i=0; i<services.length; i++) {
					int time = Integer.parseInt(services[i]);
					boolean firstStop = (i==0);
					service.addTime(time, firstStop);
				}
				trainLine.addTrainService(service); 
			}
		} catch(FileNotFoundException e) {
			UI.println("File not found: " + e.getMessage());
		}
	}
	//----------->>>Looping through each train line name as input for -station.data and -services.data<<<-----------
	private void readAllTrainLines() {
		String[] lineNames = {
				"Johnsonville_Wellington",
				"Masterton_Wellington", 
				"Melling_Wellington", 
				"Upper-Hutt_Wellington", 
				"Waikanae_Wellington", 
				"Wellington_Johnsonville", 
				"Wellington_Masterton", 
				"Wellington_Melling", 
				"Wellington_Upper-Hutt", 
				"Wellington_Waikanae"
		};
		for(String lineName:lineNames) {
			TrainLine trainLine = new TrainLine(lineName);
			readStationsForTrainline(trainLine);
			readTrainServicesForTrainLine(trainLine);
			trainLines.put(lineName, trainLine);
		}
	}
	//--------------->>>method for reading zone and fare prices in "fares.data"<<<---------------------------
	private void readFare() {
	    Scanner scan;
	    try {
	        scan = new Scanner(new File("/Users/admin/Downloads/Assignment/Train network data/fares.data"));

	        while (scan.hasNext()) {
	            int zone = scan.nextInt();
	            double fare = scan.nextDouble();
	            farePrices.put(zone, fare);
	        }
	        scan.close();
	    } catch (FileNotFoundException e) {
	        UI.println("Failed to read fare prices. " + e);
	    }
	}
	
	
//--------------------------------->>>>ACTIONS TO DO<<<<-------------------------------------------------
	private void actions() {
		UI.initialise();
		UI.addButton("List Stations", this::listStations);
		UI.addButton("List Train Lines", this::listTrainLines);
		UI.addButton("List Train Service", this::listTrainService);
		UI.addButton("List Stations in Train Line", this::listStTrainLine);
		UI.addButton("Train Lines through Station", this::listTrainLinesThruStation);
		UI.addButton("TrainLineBetweenStations", this::findTrainLineBetweenStations);
		UI.addButton("Find next service", this::findNextTrainService);
		UI.addButton("Show Train by Time", this::showTrainByTime);
		UI.addButton("Find Trip Between Stations", this::findTripBetweenStations);
		UI.addButton("Open System Map", this::drawSystemMap);
		UI.addButton("Open Geographical Map", this::drawGeoMap);
        		
		UI.addButton("Quit", UI::quit);
	}
	private void setupSlider() {
		UI.addSlider("Zoom", 0.6, 3, 1, (double z) -> {
			zoomLevel = z;
			redrawImage();
		});
	}
	
	private void listStations() {
		UI.clearText();
		for(Station station : stations.values()) {
			UI.println(station.toString());
		}
	}
	
	private void listTrainLines() {
		UI.clearText();
		for(TrainLine trainLine : trainLines.values()) {
			UI.println(trainLine.getName());
		}
	}
	
	private void listStTrainLine() {
		UI.clearText();
		String trainLineName = UI.askString("Enter train line name: ");
		TrainLine trainLine = trainLines.get(trainLineName);
		if(trainLine ==null) {
			UI.println("Error: No stations found at: "+trainLineName);
			return;
		}
		UI.println("Stations in " + trainLineName + ": ");
		for(Station station:trainLine.getStations()) {
			UI.println(station.getName());
		}
	}
	
	private void listTrainLinesThruStation() {
		UI.clearText();
		String stationName = UI.askString("Enter station name: ");
		List<TrainLine>match = new ArrayList<>();
		for(TrainLine trainLine:trainLines.values()) {
		for(Station station:trainLine.getStations()) {
				if(station.getName().equalsIgnoreCase(stationName)) {
					match.add(trainLine);
					break;
				}
			}
		}
		if(match.isEmpty()) {
			UI.println("Error: No train lines for "+stationName);
			return;
		}
		UI.println("Train lines passing through " +stationName + ":");
		for(TrainLine trainLine:match) {
			UI.println(trainLine.getName());
		}
	}
	
	private void findTrainLineBetweenStations() {
	    UI.clearText();

	    String startStationName = UI.askString("Enter starting station name:");
	    String endStationName = UI.askString("Enter destination station name:");

	    Station startStation = stations.get(startStationName);
	    Station endStation = stations.get(endStationName);

	    if (startStation == null || endStation == null) {
	        UI.println("Invalid station name provided.");
	        return;
	    }
	    TrainLine connectingLine = null;
	    for (TrainLine trainLine : startStation.getTrainLines()) {
	        if (trainLine.getStations().contains(endStation)) {
	            connectingLine = trainLine;
	            break;
	        }
	    }
	    if (connectingLine == null) {
	        UI.println("No train line found connecting " + startStationName + " and " + endStationName);
	    } else {
	        UI.println("Train line connecting " + startStationName + " and " + endStationName + " is: " + connectingLine.getName());
	    }
	}
	
	private void findNextTrainService() {
	    UI.clearText();
	    String stationName = UI.askString("Enter station name:");
	    int specifiedTime = UI.askInt("Enter the time (in the format HHMM):");

	    Station station = stations.get(stationName);

	    if (station == null) {
	        UI.println("Invalid station name provided.");
	        return;
	    }
	    boolean foundService = false;
	    for (TrainLine trainLine : station.getTrainLines()) {
	        for (TrainService service : trainLine.getTrainServices()) {
	            int timeForStation = service.getTimeForStation(station);
	            
	            if (timeForStation > specifiedTime) {
	                UI.println("Next train service on " + trainLine.getName() + " after " + specifiedTime + " is at " + timeForStation);
	                foundService = true;
	                break; 
	            }
	        	}
	    }

	    if (!foundService) {
	        UI.println("No train service found after the specified time for " + stationName);
	    }
	}
	
	private void listTrainService() {
		    UI.clearText();
		    String trainLineName = UI.askString("Enter train line name:");
		    TrainLine trainLine = trainLines.get(trainLineName);

		    if (trainLine == null) {
		        UI.println("Train line not found.");
		        return;
		    }
		    List<Station> stationsOnLine = trainLine.getStations();
		    for (Station station : stationsOnLine) {
		        UI.print("|"+station.getName()); 
		    }
		    UI.println();
		    for (TrainService service : trainLine.getTrainServices()) {
		        for (Station station : stationsOnLine) {
		            int time = service.getTimeForStation(station);
		            if (time != -1) {
		                UI.print("    " + formatTime(time)); 
		            } else {
		                UI.print("    " + " --- "); //trying to align the station name headers and the time data
		            }
		        }
		        UI.println();
		    }
			}

		private String formatTime(int time) {
		    int hours = time / 100; 
		    int minutes = time % 100;
		    return String.format("%02d:%02d", hours, minutes);
		}
	
	private void showTrainByTime() {
		UI.clearText();
		int time = UI.askInt("Enter time as HHMM: ");
		for(TrainLine trainLine:trainLines.values() ) {
			for(TrainService service:trainLine.getTrainServices()) {
				if(service.getStart() == time) {
					UI.println(trainLine.getName()+": "+ service.toString());
					
				}
			}
			}
			}
	
	
	
	private void findTripBetweenStations() {
	    UI.clearText();

	    String startStationName = UI.askString("Enter starting station name:");
	    String endStationName = UI.askString("Enter destination station name:");
	    int specifiedTime = UI.askInt("Enter the time (in the format HHMM):");

	    Station startStation = stations.get(startStationName);
	    Station endStation = stations.get(endStationName);

	    if (startStation == null || endStation == null) {
	        UI.println("Invalid station names provided.");
	        return;
	    }

	    boolean foundService = false;
	    for (TrainLine trainLine : startStation.getTrainLines()) {
	    if (!trainLine.hasStation(endStation)) {
	            continue;
	        }

	    for (TrainService service : trainLine.getTrainServices()) {
	            int startTime = service.getTimeForStation(startStation);
	            int endTime = service.getTimeForStation(endStation);
	            
	        if (startTime > specifiedTime && endTime != -1) {
	                UI.println("Next trip on " + trainLine.getName() + " after " + specifiedTime + ":");
	                UI.println("Departs " + startStationName + " at " + startTime);
	                UI.println("Arrives at " + endStationName + " at " + endTime);
	                int zoneDifference = Math.abs(startStation.getZone() - endStation.getZone()) + 1;
	                Double fare = farePrices.get(zoneDifference);
	                UI.println("Fare for the trip: $" + fare);

	                foundService = true;
	                break;
	            }
	        }
	        if (foundService) {
	            break;
	        }
	    	 }

	    if (!foundService) {
	        UI.println("No train service found after the specified time between " + startStationName + " and " + endStationName);
	    }
		}

	    private void drawSystemMap() {
	    	currentImage = sysImage1;
	    	UI.clearGraphics();
	    	String imagePath = "/Users/admin/Downloads/assignment.zip_expanded/Assignment/Train network data/system-map.png";
	    	UI.drawImage(imagePath, 0, 0, UI.getCanvasWidth()*zoomLevel, UI.getCanvasHeight()*zoomLevel);
	    }
	    private void drawGeoMap() {
	    	currentImage = geoImage1;
	    	UI.clearGraphics();
	    	String imagePath = "/Users/admin/Downloads/assignment.zip_expanded/Assignment/Train network data/geographic-map.png";
	    	UI.drawImage(imagePath, 0, 0, UI.getCanvasWidth()*zoomLevel, UI.getCanvasHeight()*zoomLevel);
	    }
	    private void redrawImage() {
	    	if(geoImage1.equals(currentImage)) {
	    		drawGeoMap();
	    	}
	    	else if(sysImage1.equals(currentImage)) {
	    		drawSystemMap();
	    	}
	    }

	   
	
	public static void main(String[] args) {
		new Queries();
		
	}
	}


		
		

	


