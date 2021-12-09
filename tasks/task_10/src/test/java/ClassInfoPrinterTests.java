import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ClassInfoPrinterTests {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    @BeforeEach
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    @Test
    public void testInfoForExampleInterfaceAnimal() {
        ClassInfoPrinter classInfo = new ClassInfoPrinter("exampleclasses.Animal");
        classInfo.printInfo();
        assertEquals("Class from: exampleclasses.Animal\r\n" +
                        "Class from package:  exampleclasses\r\n" +
                        "public abstract interface Animal\r\n" +
                        "Given class is not an extension of any other class\r\n" +
                        "Given class doesn't implement interfaces.\r\n" +
                        "Is interface: true\r\n" +
                        "Is enum: false\r\n" +
                        "No fields declared in given class.\r\n" +
                        "No subclasses declared in given class.\r\n"
                , outContent.toString());
    }

    @Test
    public void testInfoForExampleClassCat() {
        ClassInfoPrinter classInfo = new ClassInfoPrinter("exampleclasses.Cat");
        classInfo.printInfo();
        assertEquals("Class from: exampleclasses.Cat\r\n" +
                        "Class from package:  exampleclasses\r\n" +
                        "public Cat\r\n" +
                        "Extends class java.lang.Object\r\n" +
                        "Interfaces implemented in given class: \r\n" +
                        "public abstract interface exampleclasses.Animal\r\n" +
                        "public abstract interface java.io.Serializable\r\n" +
                        "Is interface: false\r\n" +
                        "Is enum: false\r\n" +
                        "Fields in given class: \r\n" +
                        "private java.lang.String name\r\n" +
                        "private int age\r\n" +
                        "No subclasses declared in given class.\r\n"
                , outContent.toString());
    }
}
