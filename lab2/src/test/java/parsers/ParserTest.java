package parsers;

import org.junit.jupiter.api.Test;
import classes.TouristVoucher;
import classes.Hotel;
import classes.TransportType;
import classes.TripType;
import parsers.dom.DOMParser;
import parsers.sax.MySAXParser;
import parsers.stax.StAXParser;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ParserTest {
    public static final String VALID_XML = "src/test/java/data/touristVouchers_valid.xml";
    public static final String INVALID_XML = "src/test/java/data/touristVouchers_invalid.xml";
    public static final String PATH_XSD = "src/test/java/data/touristVouchers.xsd";
    public static final List<TouristVoucher> LIST = List.of(
            new TouristVoucher(
                    "Germany",
                    TripType.WEEKEND,
                    TransportType.PLANE,
                    5,
                    new Hotel(4, 1, true, true),
                    7000
            ),
            new TouristVoucher(
                    "Ukraine",
                    TripType.WEEKEND,
                    TransportType.BUS,
                    3,
                    new Hotel(3, 2, false, false),
                    300
            ));

    void invalidFileTest(Parser Parser) {
        assertThrows(RuntimeException.class, () -> Parser.parseXML(ParserTest.INVALID_XML, ParserTest.PATH_XSD));
    }

    void validFileTest(Parser myParser) {
        List<TouristVoucher> touristVouchers = myParser.parseXML(ParserTest.VALID_XML, ParserTest.PATH_XSD);
        assertEquals(ParserTest.LIST.size(), touristVouchers.size());
        for (int i = 0; i < ParserTest.LIST.size(); i++) {
            assertEquals(ParserTest.LIST.get(i), touristVouchers.get(i));
        }
    }

    @Test
    void domParsTest() {
        invalidFileTest(new DOMParser());
        validFileTest(new DOMParser());
    }

    @Test
    void saxParsTest() {
        invalidFileTest(new MySAXParser());
        validFileTest(new MySAXParser());
    }

    @Test
    void staxParsTest() {
        invalidFileTest(new StAXParser());
        validFileTest(new StAXParser());
    }
}
