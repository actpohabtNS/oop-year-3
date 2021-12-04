package validators;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ValidatorXMLTest {
    public static final String PATH_XSD = "src/test/java/data/touristVouchers.xsd";
    public static final String VALID_XML = "src/test/java/data/touristVouchers_valid.xml";
    public static final String INVALID_XML = "src/test/java/data/touristVouchers_invalid.xml";

    @Test
    void invalidFileTest() {
        Assertions.assertThrows(RuntimeException.class, () -> XMLValidator.validateAgainstXSD(INVALID_XML, PATH_XSD));
    }

    @Test
    void validFileTest() {
        XMLValidator.validateAgainstXSD(VALID_XML, PATH_XSD);
    }
}
