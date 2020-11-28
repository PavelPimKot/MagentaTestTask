package distanceCalculator.controllers;


import distanceCalculator.exceptionClasses.LatitudeMeasureException;
import distanceCalculator.exceptionClasses.LongitudeMeasureException;
import distanceCalculator.infoClasses.City;
import distanceCalculator.infoClasses.Distance;
import distanceCalculator.xmlParser.SAXPars;
import org.jgrapht.alg.interfaces.*;
import org.jgrapht.alg.shortestpath.AStarShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedWeightedMultigraph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import distanceCalculator.repos.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

/**
 * @author PavelPimkot
 * @version 1.0
 * The main controller class in project
 * Handles all events on the web
 */
@Controller
public class MainController {

    private final static int updateFromDb = 0;
    /**
     * This field is used to solve the distance Matrix calculation
     */
    private static DirectedWeightedMultigraph<City, DefaultWeightedEdge> distanceGraph;
    /**
     * This field is used to keep all cities while program is working
     */
    private static ArrayList<City> cities;
    /**
     * This field is used to keep all distances while program is working
     */
    private static ArrayList<Distance> distances;


    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private DistanceRepository distanceRepository;

    /**
     * This method is used to work with addCity response
     * If we have not got such city in our cities
     * we add it into ArrayList, save to database and add into graph as edge
     * If we already have got such city, or if city is incorrect
     * we drop a message about exception
     */
    @GetMapping("addCity")
    public ModelAndView saveCity(@RequestParam(value = "name") String name,
                                 @RequestParam(value = "latitude") double latitude,
                                 @RequestParam(value = "longitude") double longitude) {
        ModelAndView model = new ModelAndView();
        try {
            City toSave = new City(name, latitude, longitude);
            for (City city : cities) {
                if (city.equals(toSave)) {
                    model.addObject("resultWeight", "Such city is already exists");
                    model.setViewName("resultPage");
                    return model;
                }
            }
            cities.add(toSave);
            distanceGraph.addVertex(toSave);
            cityRepository.save(toSave);
            model.setViewName("redirect:/cityTable");
            return model;
        } catch (LatitudeMeasureException | LongitudeMeasureException e) {
            model.addObject("resultWeight", e.toString());
            model.setViewName("resultPage");
            return model;
        }
    }

    /**
     * This method is used to show cities as table at view
     * We put it into the model, then show it using thymeleaf
     */
    @GetMapping("cityTable")
    public ModelAndView cityTable() {
        ModelAndView model = new ModelAndView();
        model.addObject("cities", cities);
        model.setViewName("cityTable");
        return model;
    }

    /**
     * This method is used to show distances as table at view
     * We put it into the model, then show it using thymeleaf
     */
    @GetMapping("distanceTable")
    public ModelAndView distanceTable() {
        ModelAndView model = new ModelAndView();
        model.addObject("distances", distances);
        model.setViewName("distanceTable");
        return model;
    }

    /**
     * This method is used to download information from database when the program starts
     * It download info into graph and Lists of cities and distances
     * Also it used to show mainPage which has links to other pages
     */
    @GetMapping("mainPage")
    public String mainPage() {
        if (updateFromDb == 0) {
            distanceGraph = new DirectedWeightedMultigraph<>(DefaultWeightedEdge.class);
            cities = new ArrayList<City>(cityRepository.findAll());
            distances = new ArrayList<Distance>(distanceRepository.findAll());
            for (City city : cities) {
                distanceGraph.addVertex(city);
            }
            ArrayList<Distance> distancesToPutIn;
            for (int i = 0; i < cities.size(); i++) {
                distancesToPutIn = new ArrayList<>(distanceRepository.findAllByFromCity(cities.get(i)));
                for (int j = 0; i < distancesToPutIn.size(); ++i) {
                    Distance currDist = distancesToPutIn.get(i);
                    DefaultWeightedEdge currEdge = distanceGraph.addEdge(currDist.getFromCity(), currDist.getToCity());
                    distanceGraph.setEdgeWeight(currEdge, currDist.getDistance());
                }
            }
        }
        return "mainPage";
    }

    @GetMapping("/calculations")
    public String calculationsRet() {
        return "calculations";
    }

    /**
     * This method calculates the distance the distance between two cities using the AStar graph algorithm
     * It doesnt do it, if it is no such cities in database
     */
    @GetMapping("graphDistanceCalculation")
    public ModelAndView graphDistanceCalculation(@RequestParam(value = "firstName") String firstName,
                                                 @RequestParam(value = "firstLat") double firstLat,
                                                 @RequestParam(value = "firstLong") double firstLong,
                                                 @RequestParam(value = "secondName") String secondName,
                                                 @RequestParam(value = "secondLat") double secondLat,
                                                 @RequestParam(value = "secondLong") double secondLong) {
        ModelAndView model = new ModelAndView();
        int found = 0;
        AStarAdmissibleHeuristic<City> heuristic = new AStarAdmissibleHeuristic<City>() {
            @Override
            public double getCostEstimate(City o, City v1) {
                return Distance.getDistanceBetweenStraight(o, v1).getDistance();
            }
        };
        AStarShortestPath<City, DefaultWeightedEdge> aStarShortestPath
                = new AStarShortestPath<City, DefaultWeightedEdge>(distanceGraph, heuristic);
        try {
            City sourceVertex = new City(firstName, firstLat, firstLong);
            City destinationVertex = new City(secondName, secondLat, secondLong);
            for (City city : cities) {
                if (city.equals(sourceVertex)) {
                    sourceVertex = city;
                    ++found;
                }
                if (city.equals(destinationVertex)) {
                    destinationVertex = city;
                    ++found;
                }
                if (found == 2)
                    break;
            }
            if (found < 2) {
                model.addObject("resultWeight", "No such information in DB Exception");
                model.setViewName("resultPage");
                return model;
            } else {
                Double resultWeight = aStarShortestPath.getPath(sourceVertex, destinationVertex).getWeight();//ответ
                model.addObject("resultWeight", resultWeight);
            }
        } catch (LatitudeMeasureException | LongitudeMeasureException e) {
            model.addObject("resultWeight", e.toString());
            model.setViewName("resultPage");
            return model;
        }
        model.setViewName("resultPage");
        return model;

    }

    /**
     * This method does straight distance calculation
     */
    @GetMapping("/calculateDistance")
    public ModelAndView result(
            @RequestParam(value = "firstName") String firstName,
            @RequestParam(value = "firstLat") double firstLat,
            @RequestParam(value = "firstLong") double firstLong,
            @RequestParam(value = "secondName") String secondName,
            @RequestParam(value = "secondLat") double secondLat,
            @RequestParam(value = "secondLong") double secondLong
    ) {
        ModelAndView model = new ModelAndView();
        try {
            City fromCity = new City(firstName, firstLat, firstLong);
            City toCity = new City(secondName, secondLat, secondLong);
            boolean firstCityFound = false;
            boolean secondCityFound = false;
            for (City city : cities) {
                if (city.equals(fromCity)) {
                    fromCity = city;
                    firstCityFound = true;
                }
                if (city.equals(toCity)) {
                    toCity = city;
                    secondCityFound = true;
                }
                if (firstCityFound && secondCityFound) {
                    break;
                }
            }
            Distance result = Distance.getDistanceBetweenStraight(fromCity, toCity);
            model.addObject("resultWeight", Double.valueOf(result.getDistance()));
            model.setViewName("resultPage");
            for (Distance distance : distances) {
                if (distance.equals(result)) {
                    return model;
                }
            }
            if (!firstCityFound && !secondCityFound) {
                cities.add(fromCity);
                cities.add(toCity);
                cityRepository.save(fromCity);
                cityRepository.save(toCity);
                distanceGraph.addVertex(fromCity);
                distanceGraph.addVertex(toCity);
            } else if (firstCityFound && !secondCityFound) {
                cities.add(toCity);
                cityRepository.save(toCity);
                distanceGraph.addVertex(toCity);
            } else if (!firstCityFound) {
                cities.add(fromCity);
                cityRepository.save(fromCity);
                distanceGraph.addVertex(fromCity);
            }
            distances.add(result);
            distanceRepository.save(result);
            DefaultWeightedEdge currEdge = distanceGraph.addEdge(fromCity, toCity);
            distanceGraph.setEdgeWeight(currEdge, result.getDistance());

        } catch (LatitudeMeasureException | LongitudeMeasureException e) {
            model.addObject("resultWeight", e.toString());
            model.setViewName("resultPage");
            return model;
        }
        return model;
    }

    /**
     * This method downloads the file and parses it
     */
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public @ResponseBody
    ModelAndView handleFileUpload(@RequestParam("name") String name,
                                  @RequestParam("file") MultipartFile file) {
        ModelAndView model = new ModelAndView();
        model.setViewName("resultPage");
        if (!file.isEmpty()) {
            try {
                byte[] bytes = file.getBytes();
                File inputFile = new File(name);
                BufferedOutputStream stream =
                        new BufferedOutputStream(new FileOutputStream(inputFile));
                stream.write(bytes);
                stream.close();
                parseXML(inputFile);
                if (SAXPars.getMistakePositions().isEmpty()) {
                    model.addObject("resultWeight",
                            "Вы удачно загрузили " + name + " в " + name + "-uploaded !");
                } else {
                    model.addObject("resultWeight",
                            "Вы удачно загрузили " + name + " в " + name + "-uploaded !" + " количество ошибок : "
                                    + SAXPars.getMistakePositions().size());
                }
                return model;

            } catch (Exception e) {
                model.addObject("resultWeight",
                        "Вам не удалось загрузить " + name + " => " + e.getMessage());
                return model;
            }
        } else {
            model.addObject("resultWeight",
                    "Вам не удалось загрузить " + name + " потому что файл пустой.");
            return model;
        }
    }

    /**
     * This method parses info from file and add the info into the graph and lists
     */
    private void parseXML(File inputFile) {
        SAXPars.parseXML(inputFile);
        ArrayList<City> citiesUpload = SAXPars.getCities();
        ArrayList<Distance> distancesUpload = SAXPars.getDistances();
        cities.addAll(citiesUpload);
        cityRepository.saveAll(citiesUpload);
        distances.addAll(distancesUpload);
        distanceRepository.saveAll(distancesUpload);
        for (City city : citiesUpload) {
            distanceGraph.addVertex(city);
        }
        for (Distance distance : distancesUpload) {
            DefaultWeightedEdge currEdge = distanceGraph.addEdge(distance.getFromCity(), distance.getToCity());
            distanceGraph.setEdgeWeight(currEdge, distance.getDistance());
        }
    }
}
