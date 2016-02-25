package ds.monte.carlo;

import java.util.Random;
import javax.swing.SwingWorker;

/**
 *
 * @author Carmen
 */
public abstract class Simulation extends SwingWorker {

    private String numberOfReplications;
    private String parameter2;
    private String parameter3;

    private static final int d = 10;
    private static final int l = 9;
    private static Random rnd1;
    private static int n = 0;
    private static int m = 0;

    @Override
    protected int[] doInBackground() throws Exception {
        // TO DO - implement Monte Carlo simulation
        int[] results = new int[5];

        rnd1 = new Random();
        double a, y, alfa;

        while (n < 10000) {
            alfa = rnd1.nextDouble() * 180;

            y = rnd1.nextDouble() * d;
            a = Math.sin(Math.toRadians(alfa)) * l;

            if (a + y >= d) {
                m++;
            }
            n++;
        }
        double vysledok = (double)m/n;

        System.out.println(vysledok + ", m: " + m + ", n: " + n);
        return results;
    }

    @Override
    protected void done() {
        // TO DO - when done create JFreeChart
    }

    protected String getNumberOfReplications() {
        return numberOfReplications;
    }

    protected void setNumberOfReplications(String numberOfReplications) {
        this.numberOfReplications = numberOfReplications;
    }

}
