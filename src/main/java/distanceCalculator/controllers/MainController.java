package distanceCalculator.controllers;


import distanceCalculator.xmlParser.SAXPars;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import distanceCalculator.repos.*;


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

    private void parseXML(){
        SAXPars.parseXML();
    }
}
