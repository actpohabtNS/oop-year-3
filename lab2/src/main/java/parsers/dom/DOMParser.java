package parsers.dom;

import lombok.SneakyThrows;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import classes.Hotel;
import classes.TouristVoucher;
import classes.TransportType;
import classes.TripType;
import parsers.Parser;
import validators.XMLValidator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DOMParser implements Parser {
    @SneakyThrows
    public List<TouristVoucher> parseXML(String xml_path, String xsd_path) {
        XMLValidator.validateAgainstXSD(xml_path, xsd_path);
        List<TouristVoucher> vouchers = new ArrayList<>();
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = builderFactory.newDocumentBuilder();
        Document document = builder.parse(new File(xml_path));
        Element rootElement = document.getDocumentElement();
        NodeList nodes = rootElement.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node instanceof Element element) {
                vouchers.add(
                        new TouristVoucher(
                                getString(element, "country"),
                                TripType.valueOf(getString(element, "type")),
                                TransportType.valueOf(getString(element, "transport")),
                                Integer.valueOf(getString(element, "days")),
                                new Hotel(
                                    Integer.valueOf(getString(element, "stars")),
                                    Integer.valueOf(getString(element, "persons")),
                                    Boolean.valueOf(getString(element, "payedBreakfast")),
                                    Boolean.valueOf(getString(element, "hasPool"))
                                ),
                                Integer.valueOf(getString(element, "price"))
                        )
                );
            }
        }
        return vouchers;
    }

    private String getString(Element element, String key) {
        return element.getElementsByTagName(key).item(0).getTextContent();
    }
}
