import org.junit.jupiter.api.Test;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SolverTest {

    @Test
    public void solveTest(){
        MatrixSolver solver = new MatrixSolver();
        List<Double> a = new ArrayList<>(Arrays.asList(1.0, 1.0, 1.0, 1.0));
        List<Double> c = new ArrayList<>(Arrays.asList(2.0, 2.0, 2.0, 2.0, 2.0));
        List<Double> b = new ArrayList<>(Arrays.asList(1.0, 1.0, 1.0, 1.0));
        List<Double> d = new ArrayList<>(Arrays.asList(1.0, 1.0, 1.0, 1.0, 1.0));
        Matrix matrix = new Matrix(a, c, b, d);
        assertTrue(Utils.isTridiagonal(matrix));

        List<Double> expectedResult = new ArrayList<>(Arrays.asList(0.5, 0.0, 0.5, 0.0, 0.5));
        List<Double> result = solver.solve(matrix);

        assertEquals(expectedResult, result);
    }
}
