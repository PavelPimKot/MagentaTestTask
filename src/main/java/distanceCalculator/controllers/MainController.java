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


@Controller
public class MainController {

    private final static int updateFromDb = 0;
    private static DirectedWeightedMultigraph<City, DefaultWeightedEdge> distanceGraph;
    private static ArrayList<City> cities;
    private static ArrayList<Distance> distances;


    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private DistanceRepository distanceRepository;

    @GetMapping("addCity")
    public String saveBook(@RequestParam(value = "name") String name,
                           @RequestParam(value = "latitude") double latitude,
                           @RequestParam(value = "longitude") double longitude) {
        try {
            City toSave = new City(name, latitude, longitude);
            cities.add(toSave);
            cityRepository.save(toSave);
            return "redirect:/cityTable";
        } catch (LatitudeMeasureException | LongitudeMeasureException e) {
            //Do something
        }
        return "redirect:/cityTable";
    }

    @GetMapping("cityTable")
    public ModelAndView cityTable() {
        ModelAndView model = new ModelAndView();
        model.addObject("cities", cities);
        model.setViewName("cityTable");
        return model;
    }

    @GetMapping("distanceTable")
    public ModelAndView distanceTable() {
        ModelAndView model = new ModelAndView();
        model.addObject("distances", distances);
        model.setViewName("distanceTable");
        return model;
    }

    @GetMapping("mainPage")
    public String mainPage() {
        if (updateFromDb == 0) {
            distanceGraph = new DirectedWeightedMultigraph<>(DefaultWeightedEdge.class);
            cities = new ArrayList<City>(cityRepository.findAll());
            distances = new ArrayList<Distance>(distanceRepository.findAll());
            if (cities.isEmpty()) {

            } else {
                for (City city : cities) {
                    distanceGraph.addVertex(city);
                }
                ArrayList<Distance> distancesToPutIn;
                for (int i = 0; i < cities.size(); i++) {
                    distancesToPutIn = new ArrayList<>(distanceRepository.findAllByFromCity(cities.get(i)));
                    if (distancesToPutIn.isEmpty()) {
//сообщение о том что недостаточно данных
                    } else {
                        for (int j = 0; i < distancesToPutIn.size(); ++i) {
                            Distance currDist = distancesToPutIn.get(i);
                            DefaultWeightedEdge currEdge = distanceGraph.addEdge(currDist.getFromCity(), currDist.getToCity());
                            distanceGraph.setEdgeWeight(currEdge, currDist.getDistance());
                        }
                    }

                }
            }
        }
        return "mainPage";
    }

    @GetMapping("/calculations")
    public String calculationsRet() {
        return "calculations";
    }

    @GetMapping("graphDistanceCalculation")
    public ModelAndView graphDistanceCalculation(@RequestParam(value = "firstName") String firstName,
                                                 @RequestParam(value = "secondName") String secondName) {
        ModelAndView model = new ModelAndView();
        AStarAdmissibleHeuristic<City> heuristic = new AStarAdmissibleHeuristic<City>() {
            @Override
            public double getCostEstimate(City o, City v1) {
                return Distance.getDistanceBetweenStraight(o, v1).getDistance();
            }
        };
        AStarShortestPath<City, DefaultWeightedEdge> aStarShortestPath
                = new AStarShortestPath<City, DefaultWeightedEdge>(distanceGraph, heuristic);
        City sourceVertex = null;
        City destinationVertex = null;
        for (City city : cities) {
            if (city.getName().equals(firstName)) {
                sourceVertex = city;
                continue;
            }
            if (city.getName().equals(secondName)) {
                destinationVertex = city;
                continue;
            }
            if (sourceVertex != null && destinationVertex != null)
                break;
        }
        double resultWeight = aStarShortestPath.getPath(sourceVertex, destinationVertex).getWeight();//ответ
        model.addObject("resultWeight", Double.valueOf(resultWeight));
        model.setViewName("resultPage");
        return model;

    }

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
            Distance result = Distance.getDistanceBetweenStraight(fromCity, toCity);
            cities.add(fromCity);
            cities.add(toCity);
            distances.add(result);
            cityRepository.save(fromCity);
            cityRepository.save(toCity);
            distanceRepository.save(result);
            distanceGraph.addVertex(fromCity);
            distanceGraph.addVertex(toCity);
            DefaultWeightedEdge currEdge = distanceGraph.addEdge(fromCity, toCity);
            distanceGraph.setEdgeWeight(currEdge, result.getDistance());
            model.addObject("resultWeight", Double.valueOf(result.getDistance()));
            model.setViewName("resultPage");
        } catch (LatitudeMeasureException | LongitudeMeasureException e) {
            model.addObject("exception", e.toString());
            return model;
        }
        return model;
    }

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

    private void parseXML(File inputFile) {
        SAXPars.parseXML(inputFile);
        ArrayList<City> citiesUpload = SAXPars.getCities();
        ArrayList<Distance> distancesUpload = SAXPars.getDistances();
        cities.addAll(citiesUpload);
        cityRepository.saveAll(citiesUpload);
        distances.addAll(distancesUpload);
        distanceRepository.saveAll(distancesUpload);
    }
}
