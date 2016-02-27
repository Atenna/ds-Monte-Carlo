package ds.monte.carlo;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import static java.lang.Math.max;
import java.util.Random;
import javax.swing.SwingWorker;

/**
 *
 * @author Carmen
 */
public class Simulation extends SwingWorker<Integer, Integer>{

    private int numberOfReplications;
    private String parameter2;
    private String parameter3;

    private static Random seed;
    private static Random gen01, gen12, gen13, gen26,
            gen65, gen34, gen37, gen78, gen90,
            gen02, gen04, gen05,
            gend04;

    private int time01, time12, time13, time26, time65, 
                time34, time37, time78, time90;

    private double part02, part04, part05;
    private double decision04;

    public Simulation(int replications) {
        this.numberOfReplications = replications;
        Simulation.seed = new Random();
        Simulation.gen01 = new Random(seed.nextInt());
        Simulation.gen12 = new Random(seed.nextInt());
        Simulation.gen13 = new Random(seed.nextInt());
        Simulation.gen26 = new Random(seed.nextInt());
        Simulation.gen65 = new Random(seed.nextInt());
        Simulation.gen34 = new Random(seed.nextInt());
        Simulation.gen37 = new Random(seed.nextInt());
        Simulation.gen78 = new Random(seed.nextInt());
        Simulation.gen90 = new Random(seed.nextInt());
        // branches
        Simulation.gen02 = new Random(seed.nextInt());
        Simulation.gen04 = new Random(seed.nextInt());
        Simulation.gen05 = new Random(seed.nextInt());
        // decisions
        Simulation.gend04 = new Random(seed.nextInt());
    }

    @Override
    protected Integer doInBackground() throws Exception {

        int tp = 140;            // required project length in days
        int tp_mc = 0;           // days needed in one simulation run
        int successful = 0;      // increment when tp_mc <= tp
        long overalTimes = 0;
        int current = 0;
        int progress = 0;
        
        while (current < numberOfReplications) {
            generateTimes();
            tp_mc = sumTimes();
            //System.out.println(tp_mc);
            successful = (tp_mc <= tp) ? (successful + 1) : successful;
            overalTimes += tp_mc;
            tp_mc = 0;
            current++;
            if(current%1000==0) {
                //System.out.println((int)((double)current/numberOfReplications*100));
                progress = (int)((double)current/numberOfReplications*100);
                setProgress(progress);
            }
        }
        System.out.println("SUCC " + (double)successful/numberOfReplications);
        System.out.println("AVG " + (double)overalTimes/numberOfReplications);
        return successful;
    }

    @Override
    protected void done() {
        // TO DO - when done create JFreeChart
    }

    protected long getNumberOfReplications() {
        return numberOfReplications;
    }

    protected void setNumberOfReplications(int numberOfReplications) {
        this.numberOfReplications = numberOfReplications;
    }

    private void generateTimes() {
        time01 = gen01.nextInt((15 - 4) + 1) + 4;
        
        part02 = gen02.nextDouble();
        if (part02 < 0.2) {
            time12 = gen12.nextInt((29 - 10) + 1) + 10;
        } else if (part02 >= 0.2 && part02 < 0.6) {
            time12 = gen12.nextInt((48 - 30) + 1) + 30;
        } else {
            time12 = gen12.nextInt((65 - 49) + 1) + 49;
        }
        time13 = gen13.nextInt((92 - 48) + 1) + 48;
        time26 = gen26.nextInt(16 - 10) + 10;

        decision04 = gend04.nextDouble();
        if (decision04 < 0.32) {
            // nebude sa vykonavat
            time34 = 0;
            time37 = gen37.nextInt(29 - 20) + 20;
            // cas aktivity 7 sa predlzi o 15%
            time37 += time37*0.15;
        } else {
            part04 = gen04.nextDouble();
            if (part04 < 0.2) {
                time34 = gen34.nextInt((27 - 19) + 1) + 19;
            } else {
                time34 = gen34.nextInt((44 - 28) + 1) + 28;
            }
            time37 = gen37.nextInt(29 - 20) + 20;
        }

        part05 = gen05.nextDouble();
        if (part05 < 0.2) {
            time65 = gen65.nextInt((19 - 5) + 1) + 5;
        } else if (part05 >= 0.2 && part05 < 0.7) {
            time65 = gen65.nextInt((39 - 20) + 1) + 20;
        } else {
            time65 = gen65.nextInt((55 - 40) + 1) + 40;
        }

        time78 = gen78.nextInt(17 - 12) + 12;
        time90 = gen90.nextInt(27 - 13) + 13;
        
        //System.out.println("-----------------------------------");
        //System.out.println("1 " + time01);
        //System.out.println("2 branch " + part02);
        //System.out.println("2 " + time12);
        //System.out.println("3 " + time13);
        //System.out.println("4 branch " + part04);
        //System.out.println("4 " + time34);
        //System.out.println("5 branch " + part05);
        //System.out.println("5 " + time65);
        //System.out.println("6 " + time26);
        //System.out.println("7 " + time37);
        //System.out.println("8 " + time78);
        //System.out.println("9 " + time90);
        
    }

    private int sumTimes() {
        int length = time01;
        int path1, path2, path3;
        
        path1 = time12 + time26;
        
        path2 = time13 + time34;
        
        path3 = time13 + time37 + time78;
        
        if(time34 == 0) {
            // aktivita 4 sa nevykona, pouzijeme cesty 1 a 3
            path2 += time65;
            length += (path1 < path3) ? path1 : path3;
            length += time90;
            
        } else {
            // aktivita 4 sa vykona, hladame dlhsiu cestu z (1 a 2) a 3
            int decision1 = max(path1, path2) + time65;
            length += max(decision1, path3) + time90;
        }
        
        return length;
    }
}
