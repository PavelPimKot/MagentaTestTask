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

@Controller
public class FileUploadController {

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private DistanceRepository distanceRepository;

    @RequestMapping(value = "/upload", method = RequestMethod.GET)
    public @ResponseBody
    String provideUploadInfo() {
        return "Вы можете загружать файл с использованием того же URL.";
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public @ResponseBody
    String handleFileUpload(@RequestParam("name") String name,
                            @RequestParam("file") MultipartFile file) {
        if (!file.isEmpty()) {
            try {
                byte[] bytes = file.getBytes();
                File inputFile = new File(name + "-uploaded");
                BufferedOutputStream stream =
                        new BufferedOutputStream(new FileOutputStream(inputFile));
                stream.write(bytes);
                stream.close();
                parseXML(inputFile);
                return "Вы удачно загрузили " + name + " в " + name + "-uploaded !";

            } catch (Exception e) {
                return "Вам не удалось загрузить " + name + " => " + e.getMessage();
            }
        } else {
            return "Вам не удалось загрузить " + name + " потому что файл пустой.";
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