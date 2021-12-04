package validators;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.FileInputStream;
import java.io.InputStream;

public interface XMLValidator {
    static void validateAgainstXSD(String path_xml, String path_xsd) {
        try {
            InputStream xml = new FileInputStream(path_xml);
            InputStream xsd = new FileInputStream(path_xsd);
            SchemaFactory factory =
                    SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(new StreamSource(xsd));
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(xml));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
