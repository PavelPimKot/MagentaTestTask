package distanceCalculator.controllers;


import distanceCalculator.exceptionClasses.LatitudeMeasureException;
import distanceCalculator.exceptionClasses.LongitudeMeasureException;
import distanceCalculator.infoClasses.City;
import distanceCalculator.infoClasses.Distance;
import org.jgrapht.alg.interfaces.AStarAdmissibleHeuristic;
import org.jgrapht.alg.shortestpath.AStarShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedWeightedMultigraph;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import distanceCalculator.repos.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;


@Controller
public class MainController {

    private final static int updateFromDb = 0;
    private static DirectedWeightedMultigraph<City, DefaultWeightedEdge> distanceGraph;


    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private DistanceRepository distanceRepository;


    @GetMapping("/mainPage")
    public String method() {
        return "mainPage";
    }

    @GetMapping("/calculations")
    public String calculationsRet() {
        if (updateFromDb == 0) {//заполнение графа(нужно загрузить данные из базы данных в граф)
            distanceGraph = new DirectedWeightedMultigraph<>(DefaultWeightedEdge.class);
            ArrayList<City> cities = new ArrayList<City>(cityRepository.findAll());
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
        City sourceVertex = cityRepository.findByName(firstName);
        City destinationVertex = cityRepository.findByName(secondName);
        double resultWeight = aStarShortestPath.getPath(sourceVertex, destinationVertex).getWeight();//ответ
        model.addObject("resultWeight", resultWeight);
        model.setViewName("resultWeightPage");
        return model;

    }

    @GetMapping("/calculateDistance")
    public ModelAndView result(
            @RequestParam(value = "firstName") String firstName,
            @RequestParam(value = "firstLat") String firstLat,
            @RequestParam(value = "firstLong") String firstLong,
            @RequestParam(value = "secondName") String secondName,
            @RequestParam(value = "secondLat") String secondLat,
            @RequestParam(value = "secondLong") String secondLong
    ) throws LatitudeMeasureException, LongitudeMeasureException {
        ModelAndView model = new ModelAndView();
        City fromCity = new City(firstName, Double.parseDouble(firstLat), Double.parseDouble(firstLong));
        City toCity = new City(secondName, Double.parseDouble(secondLat), Double.parseDouble(secondLong));
        Distance result = Distance.getDistanceBetweenStraight(fromCity, toCity);
        cityRepository.save(fromCity);
        cityRepository.save(toCity);
        distanceRepository.save(result);
        model.addObject("resultObject", result);
        model.setViewName("resultPage");
        return model;
    }
}
