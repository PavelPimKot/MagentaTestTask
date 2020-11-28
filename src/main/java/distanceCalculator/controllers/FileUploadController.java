package distanceCalculator.controllers;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import distanceCalculator.infoClasses.City;
import distanceCalculator.infoClasses.Distance;
import distanceCalculator.repos.CityRepository;
import distanceCalculator.repos.DistanceRepository;
import distanceCalculator.xmlParser.SAXPars;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class FileUploadController {

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private DistanceRepository distanceRepository;


    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public @ResponseBody
    ModelAndView handleFileUpload(@RequestParam("name") String name,
                                  @RequestParam("file") MultipartFile file) {
        ModelAndView model = new ModelAndView();
        model.setViewName("resultPage");
        if (!file.isEmpty()) {
            try {
                byte[] bytes = file.getBytes();
                File inputFile = new File(name + "-uploaded");
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
        ArrayList<City> cities = SAXPars.getCities();
        ArrayList<Distance> distances = SAXPars.getDistances();
        for (City city : cities) {
            cityRepository.save(city);
        }
        for (Distance distance : distances) {
            distanceRepository.save(distance);
        }
    }

}