package parsers.stax;

import lombok.SneakyThrows;
import classes.Hotel;
import classes.TouristVoucher;
import classes.TransportType;
import classes.TripType;
import parsers.Parser;
import validators.XMLValidator;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.events.XMLEvent;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class StAXParser implements Parser {
    @SneakyThrows
    public List<TouristVoucher> parseXML(String xml_path, String xsd_path) {
        XMLValidator.validateAgainstXSD(xml_path, xsd_path);
        List<TouristVoucher> result = new ArrayList<>();
        TouristVoucher current = new TouristVoucher();
        String tag = "";

        XMLEventReader eventReader =
                XMLInputFactory.newInstance().createXMLEventReader(new FileReader(xml_path));

        while (eventReader.hasNext()) {
            XMLEvent event = eventReader.nextEvent();

            switch (event.getEventType()) {
                case XMLStreamConstants.START_ELEMENT -> {
                    String qName = event.asStartElement().getName().getLocalPart();
                    tag = qName;
                    if (qName.equalsIgnoreCase("touristVoucher")) {
                        current = new TouristVoucher();
                        current.setHotel(new Hotel());
                    }
                }
                case XMLStreamConstants.CHARACTERS -> {
                    String value = event.asCharacters().toString().trim();
                    if ("".equals(value)) {
                        continue;
                    }
                    switch (tag) {
                        case "type" -> current.setType(TripType.valueOf(value));
                        case "country" -> current.setCountry(value);
                        case "days" -> current.setDays(Integer.valueOf(value));
                        case "transport" -> current.setTransport(TransportType.valueOf(value));
                        case "stars" -> current.getHotel().setStars(Integer.valueOf(value));
                        case "persons" -> current.getHotel().setPersons(Integer.valueOf(value));
                        case "payedBreakfast" -> current.getHotel().setPayedBreakfast(Boolean.valueOf(value));
                        case "hasPool" -> current.getHotel().setHasPool(Boolean.valueOf(value));
                        case "price" -> current.setPrice(Integer.valueOf(value));
                    }
                }
                case XMLStreamConstants.END_ELEMENT -> {
                    String qName = event.asEndElement().getName().getLocalPart();
                    if (qName.equalsIgnoreCase("touristVoucher")) {
                        result.add(current);
                    }
                }
            }
        }
        return result;
    }
}
