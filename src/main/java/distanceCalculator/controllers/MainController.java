package distanceCalculator.controllers;


import distanceCalculator.exceptionClasses.LatitudeMeasureException;
import distanceCalculator.exceptionClasses.LongitudeMeasureException;
import distanceCalculator.infoClasses.City;
import distanceCalculator.infoClasses.Distance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import distanceCalculator.repos.*;
import org.springframework.web.servlet.ModelAndView;


@Controller
public class MainController {


    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private DistanceRepository distanceRepository;


    @GetMapping("/mainPage")
    public String method (){
        return "mainPage";
    }

    @GetMapping("/calculations")
    public String calculationsRet (){
        return "calculations";
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
        City fromCity = new City(firstName, Double.parseDouble(firstLat),Double.parseDouble(firstLong));
        City toCity = new City(firstName, Double.parseDouble(secondLat),Double.parseDouble(secondLong));
       Distance result =  Distance.getDistanceBetweenStraight(fromCity, toCity);
       cityRepository.save(fromCity);
       cityRepository.save(toCity);
       distanceRepository.save(result);
       model.addObject("resultObject", result);
       model.setViewName("resultPage");
       return model;
    }
}
