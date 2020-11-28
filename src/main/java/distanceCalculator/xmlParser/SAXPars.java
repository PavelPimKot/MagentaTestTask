package distanceCalculator.xmlParser;

import distanceCalculator.exceptionClasses.LatitudeMeasureException;
import distanceCalculator.exceptionClasses.LongitudeMeasureException;
import distanceCalculator.infoClasses.City;
import distanceCalculator.infoClasses.Distance;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.util.ArrayList;

public class SAXPars {

    private static ArrayList<City> cities = new ArrayList<>();
    private static ArrayList<Distance> distances = new ArrayList<>();
    private static int distanceCount = -1;
    private static ArrayList<Integer> mistakePositions;

    private static class XMLHandler extends DefaultHandler {

        private Locator locator;

        @Override
        public void setDocumentLocator(final Locator locator) {
            this.locator = locator; // Save the locator, so that it can be used later for line tracking when traversing nodes.
        }

        @Override
        public void startDocument() throws SAXException {
            mistakePositions = new ArrayList<Integer>();
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            if (qName.equals("city")) {
                String name = attributes.getValue("name");
                double latitude = Double.parseDouble(attributes.getValue("latitude"));
                double longitude = Double.parseDouble(attributes.getValue("longitude"));
                try {
                    City toAdd = new City(name, latitude, longitude);
                    cities.add(toAdd);
                    if (distanceCount == 0) {
                        distances.get(distances.size() - 1).setFromCity(toAdd);
                        ++distanceCount;
                    }
                    if (distanceCount == 1) {
                        distances.get(distances.size() - 1).setToCity(toAdd);
                        distanceCount = -1;
                    }
                } catch (LatitudeMeasureException | LongitudeMeasureException e) {
                    mistakePositions.add(locator.getLineNumber());
                }
            }
            if (qName.equals("distance")) {
                double distance = Double.parseDouble(attributes.getValue("distance"));
                distances.add(new Distance(null, null, distance));
                ++distanceCount;
            }
        }
    }


    public static ArrayList<City> getCities() {
        return cities;
    }

    public static ArrayList<Distance> getDistances() {
        return distances;
    }

    public static ArrayList<Integer> getMistakePositions() {
        return mistakePositions;
    }

    public static void parseXML(File inputFile) {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            XMLHandler xmlHandler = new XMLHandler();
            saxParser.parse(inputFile, xmlHandler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
