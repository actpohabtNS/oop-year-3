package parsers;

import classes.TouristVoucher;

import java.util.List;

public interface Parser {
    List<TouristVoucher> parseXML(String xml_path, String xsd_path);
}
