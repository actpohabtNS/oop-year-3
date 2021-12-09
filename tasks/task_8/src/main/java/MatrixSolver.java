import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MatrixSolver {
    private static final Logger log = Logger.getLogger(MatrixSolver.class.getName());
    private Matrix matrix;


    private List<Double> a;
    private List<Double> b;
    private List<Double> c;
    private List<Double> d;

    private Double[] leftAlpha;
    private Double[] rightAlpha;
    private Double[] leftBeta;
    private Double[] rightBeta;

    private Coefficients rightCoefficients;
    private Coefficients leftCoefficients;

    private final List<Double> result = new ArrayList<>();

    public MatrixSolver(){ }

    public List<Double> solve(Matrix matrix){
        this.matrix = matrix;
        return solve();
    }

    public List<Double> solve(){
        if (matrix == null) {
            log.info("Matrix is null, please set a matrix.txt");
            return null;
        }
        if (!Utils.isTridiagonal(matrix)) {
            log.info("Matrix is not a diagonally dominant matrix.txt");
            return null;
        }
        findCoefficients();
        for (int i = 0; i < c.size(); i++) {
            result.add(0.0);
        }
        findMiddleResult();
        findAllResults();
        return result;
    }

    private void findCoefficients() {
        a = matrix.getA();
        c = matrix.getC();
        b = matrix.getB();
        d = matrix.getD();

        leftAlpha = new Double[c.size()];
        leftBeta = new Double[c.size()];
        rightAlpha = new Double[c.size()];
        rightBeta = new Double[c.size()];
        Arrays.fill(leftAlpha, 0.0);
        Arrays.fill(leftBeta, 0.0);
        Arrays.fill(rightAlpha, 0.0);
        Arrays.fill(rightBeta, 0.0);

        Thread left = new Thread(() -> {
            leftAlpha[0] = c.get(0);
            leftBeta[0] = b.get(0);
            leftAlpha[1] = -b.get(0) / c.get(0);
            leftBeta[1] = d.get(0) / c.get(0);
            for(int i = 2; i <= Math.floor(c.size()/2); i++){
                leftAlpha[i] = calculateLeftAlpha(i);
                leftBeta[i] = calculateLeftBeta(i);
            }
        });

        Thread right = new Thread(() -> {
            int n = c.size() - 1;
            rightAlpha[n] = -a.get(a.size() - 1) / c.get(c.size() - 1);
            rightBeta[n] = d.get(d.size() - 1) / c.get(c.size() - 1);
            for (int i = c.size() - 2; i >= Math.floor(c.size() / 2) - 1; i--) {
                rightAlpha[i] = calculateRightAlpha(i);
                rightBeta[i] = calculateRightBeta(i);
            }
        });

        left.start();
        right.start();
        try {
            left.join();
            right.join();
        } catch (InterruptedException e) {
            log.log(Level.SEVERE, "Exception: ", e);
        }

        leftCoefficients = new Coefficients(leftAlpha, leftBeta);
        rightCoefficients = new Coefficients(rightAlpha, rightBeta);
    }

    private Double calculateLeftAlpha(int i) {
        return -b.get(i - 1) / (c.get(i - 1) + a.get(i - 2) * leftAlpha[i - 1]);
    }

    private Double calculateLeftBeta(int i) {
        return (d.get(i - 1) - a.get(i - 2) * leftBeta[i - 1])
                / (c.get(i - 1) + a.get(i - 2) * leftAlpha[i - 1]);
    }

    private Double calculateRightAlpha(int i) {
        return -a.get(i - 1) / (c.get(i) + b.get(i) * rightAlpha[i + 1]);
    }

    private Double calculateRightBeta(int i) {
        return (d.get(i) - b.get(i) * rightBeta[i + 1])
                / (c.get(i) + b.get(i) * rightAlpha[i + 1]);
    }

    private void findMiddleResult() {
        int p = (int) Math.floor((c.size()) / 2) - 1;
        Double middleX = (leftCoefficients.getAlpha()[p + 1] * rightCoefficients.getBeta()[p + 1]
                + leftCoefficients.getBeta()[p + 1]) / (1 - leftCoefficients.getAlpha()[p + 1]
                * rightCoefficients.getAlpha()[p + 1]);
        result.set((int) (Math.floor((c.size()) / 2) - 1), middleX);
    }

    public void findAllResults() {
        Thread left = new Thread(() -> {
            for (int i = (int) Math.floor((c.size()) / 2) - 2; i >= 0; i--) {
                result.set(i, result.get(i + 1) * leftCoefficients.getAlpha()[i + 1]
                        + leftCoefficients.getBeta()[i + 1]);
            }
        });
        Thread right = new Thread(() -> {
            for (int i = (int) Math.floor((c.size()) / 2); i <= c.size() - 1; i++) {
                result.set(i, result.get(i - 1) * rightCoefficients.getAlpha()[i]
                        + rightCoefficients.getBeta()[i]);
            }
        });
        left.start();
        right.start();
        try {
            left.join();
            right.join();
        } catch (InterruptedException e) {
            log.log(Level.SEVERE, "Exception: ", e);
        }
    }
}
