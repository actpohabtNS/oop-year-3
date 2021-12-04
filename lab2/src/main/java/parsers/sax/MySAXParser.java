package parsers.sax;

import lombok.SneakyThrows;
import classes.TouristVoucher;
import parsers.Parser;
import validators.XMLValidator;
import classes.Hotel;
import classes.TransportType;
import classes.TripType;

import org.xml.sax.helpers.DefaultHandler;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import java.io.File;
import java.util.List;
import java.util.ArrayList;

public class MySAXParser extends DefaultHandler implements Parser {
    @SneakyThrows
    public List<TouristVoucher> parseXML(String xml_path, String xsd_path) {
        XMLValidator.validateAgainstXSD(xml_path, xsd_path);
        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        SAXParser saxParser = saxParserFactory.newSAXParser();
        MyHandler handler = new MyHandler();
        saxParser.parse(new File(xml_path), handler);
        return handler.getResult();
    }
}

class MyHandler extends DefaultHandler {
    private final List<TouristVoucher> result = new ArrayList<>();
    private StringBuilder value = null;
    private TouristVoucher current = null;
    private String tag = "";

    public List<TouristVoucher> getResult() {
        return result;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        tag = qName;
        value = new StringBuilder();
        if (qName.equalsIgnoreCase("touristVoucher")) {
            current = new TouristVoucher();
            current.setHotel(new Hotel());
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        switch (tag) {
            case "type" -> current.setType(TripType.valueOf(value.toString().trim()));
            case "country" -> current.setCountry(value.toString().trim());
            case "days" -> current.setDays(Integer.valueOf(value.toString().trim()));
            case "transport" -> current.setTransport(TransportType.valueOf(value.toString().trim()));
            case "stars" -> current.getHotel().setStars(Integer.valueOf(value.toString().trim()));
            case "persons" -> current.getHotel().setPersons(Integer.valueOf(value.toString().trim()));
            case "payedBreakfast" -> current.getHotel().setPayedBreakfast(Boolean.valueOf(value.toString().trim()));
            case "hasPool" -> current.getHotel().setHasPool(Boolean.valueOf(value.toString().trim()));
            case "price" -> current.setPrice(Integer.valueOf(value.toString().trim()));
        }

        if (qName.equalsIgnoreCase("touristVoucher")) {
            result.add(current);
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        value.append(new String(ch, start, length));
    }
}
