import org.junit.jupiter.api.Test;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
public class MatrixTest {
    @Test
    public void createMatrixFromFile(){
        Matrix matrix = new Matrix(new File("src/test/resources/matrix.txt"));
        List<Double> expectedA = new ArrayList<>(Arrays.asList(1.0, 3.0, 1.0, 1.0));
        List<Double> expectedC = new ArrayList<>(Arrays.asList(2.0, 2.0, 4.0, 2.0, 3.0));
        List<Double> expectedB = new ArrayList<>(Arrays.asList(1.0, 1.0, 1.0, 1.0));
        List<Double> expectedF = new ArrayList<>(Arrays.asList(1.0, 2.0, 1.0, 1.0, 1.0));
        assertEquals(expectedA, matrix.getA());
        assertEquals(expectedC, matrix.getC());
        assertEquals(expectedB, matrix.getB());
        assertEquals(expectedF, matrix.getD());
    }
}
