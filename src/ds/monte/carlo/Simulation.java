package ds.monte.carlo;

import static java.lang.Math.max;
import java.util.HashMap;
import java.util.Random;
import javax.swing.SwingWorker;

/**
 *
 * @author Carmen
 */
public class Simulation extends SwingWorker<Integer, Integer>{

    private int numberOfReplications;

    private final HashMap<Integer, Integer> dictionary; // uklada pocetnost vyslednych casov kvoli barchartu

    private static Random seed;
    private static Random gen1, gen2, gen3, gen6,
            gen5, gen4, gen7, gen8, gen9,
            gen02, gen04, gen05, // generatory pre diskretne empiricke rozdelenie
            gend04;              // modeluje decision, ci sa aktivita 4 vykona alebo nie

    private int time1, time2, time3, time6, time5, 
                time4, time7, time8, time9;

    private double part02, part04, part05;
    private double decision04;

    public Simulation(int replications) {
        this.numberOfReplications = replications;
        Simulation.seed = new Random();
        // diskretne rovnomerne rozdelenie
        Simulation.gen1 = new Random(seed.nextInt());
        Simulation.gen2 = new Random(seed.nextInt());
        Simulation.gen3 = new Random(seed.nextInt());
        Simulation.gen6 = new Random(seed.nextInt());
        Simulation.gen5 = new Random(seed.nextInt());
        Simulation.gen4 = new Random(seed.nextInt());
        Simulation.gen7 = new Random(seed.nextInt());
        Simulation.gen8 = new Random(seed.nextInt());
        Simulation.gen9 = new Random(seed.nextInt());
        // empiricke rozdelenia
        Simulation.gen02 = new Random(seed.nextInt());
        Simulation.gen04 = new Random(seed.nextInt());
        Simulation.gen05 = new Random(seed.nextInt());
        // decision
        Simulation.gend04 = new Random(seed.nextInt());
        dictionary = new HashMap<>();
    }

    @Override
    protected Integer doInBackground() throws Exception {

        int tp = 140;            // trvanie projektu
        int tp_mc = 0;           // trvanie projektu v jednom behu
        int successful = 0;      // increment when tp_mc <= tp
        long sumOfTimes = 0;     // suma vyslednych casov kvoli priemeru
        int current = 0;         // iterator
        int progress = 0;        // <0, 100> vstup pre progress bar
        
        while (current < numberOfReplications) {
            // setup of one run
            generateTimes();
            tp_mc = sumTimes();
            // map of values
            Integer count = dictionary.get(tp_mc);
            if (count == null) {
                dictionary.put(tp_mc, 1);
            }
            else {
                dictionary.put(tp_mc, count + 1);
            }
            // progress bar
            if(current%1000==0) {
                progress = (int)((double)current/numberOfReplications*100);
                setProgress(progress);
            }
            // calculations
            successful = (tp_mc <= tp) ? (successful + 1) : successful;
            sumOfTimes += tp_mc;
            tp_mc = 0;
            current++;
        }
        System.out.println("SUCC " + (double)successful/numberOfReplications);
        System.out.println("AVG " + (double)sumOfTimes/numberOfReplications);
        
        return successful;
    }

    @Override
    protected void done() {
        setProgress(100);
    }

    protected long getNumberOfReplications() {
        return numberOfReplications;
    }

    protected void setNumberOfReplications(int numberOfReplications) {
        this.numberOfReplications = numberOfReplications;
    }

    private void generateTimes() {
        // diskretne rovnomerne, Tmin = 4, Tmax = 15
        time1 = gen1.nextInt((15 - 4) + 1) + 4;
        // diskretne empiricke 
        part02 = gen02.nextDouble();
        if (part02 < 0.2) {
            time2 = gen2.nextInt((29 - 10) + 1) + 10;
        } else if (part02 >= 0.2 && part02 < 0.6) {
            time2 = gen2.nextInt((48 - 30) + 1) + 30;
        } else {
            time2 = gen2.nextInt((65 - 49) + 1) + 49;
        }
        // diskretne rovnomerne, Tmin = 48, Tmax = 92
        time3 = gen3.nextInt((92 - 48) + 1) + 48;
        // diskretne rovnomerne, Tmin = 10, Tmax = 16
        time6 = gen6.nextInt(16 - 10) + 10;
        
        //  Pravdepodobnosť, že sa aktivita 4 nebude vykonávať je 32%.  
        decision04 = gend04.nextDouble();
        if (decision04 < 0.32) {
            // 4 sa nebude vykonavat
            time4 = 0;
            // diskretne rovnomerne, Tmin = 20, Tmax = 29
            time7 = gen7.nextInt(29 - 20) + 20;
            // // V prípade, že sa aktivita 4 z projektu vypustí predĺži sa čas trvania aktivity 7 o 15%.
            time7 += time7*0.15;
        } // diskretne empiricke 
        else {
            part04 = gen04.nextDouble();
            if (part04 < 0.2) {
                time4 = gen4.nextInt((27 - 19) + 1) + 19;
            } else {
                time4 = gen4.nextInt((44 - 28) + 1) + 28;
            }
            time7 = gen7.nextInt(29 - 20) + 20;
        }
        // diskretne empiricke 
        part05 = gen05.nextDouble();
        if (part05 < 0.2) {
            time5 = gen5.nextInt((19 - 5) + 1) + 5;
        } else if (part05 >= 0.2 && part05 < 0.7) {
            time5 = gen5.nextInt((39 - 20) + 1) + 20;
        } else {
            time5 = gen5.nextInt((55 - 40) + 1) + 40;
        }
        // diskretne rovnomerne, Tmin = 12, Tmax = 17
        time8 = gen8.nextInt(17 - 12) + 12;
        // diskretne rovnomerne, Tmin = 13, Tmax = 27
        time9 = gen9.nextInt(27 - 13) + 13;
    }

    private int sumTimes() {
        int length = time1;
        int path1, path2, path3;
        
        path1 = time2 + time6;
        
        path2 = time3 + time4;
        
        path3 = time3 + time7 + time8;
        
        if(time4 == 0) {
            // aktivita 4 sa nevykona, pouzijeme cesty 1 a 3
            path2 += time5;
            length += (path1 < path3) ? path1 : path3;
            length += time9;
            
        } else {
            // aktivita 4 sa vykona, hladame dlhsiu cestu z (1 a 2) a 3
            int decision1 = max(path1, path2) + time5;
            length += max(decision1, path3) + time9;
        }
        
        return length;
    }
    
    public HashMap<Integer, Integer> getHashMap() {
        return dictionary;
    }
}
