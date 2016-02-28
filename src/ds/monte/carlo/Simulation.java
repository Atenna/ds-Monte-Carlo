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

    private final HashMap<Long, Integer> dictionary; // uklada pocetnost vyslednych casov kvoli barchartu

    private static Random seed;
    private static Random gen1, gen2, gen3, gen6,
            gen5, gen4, gen7, gen8, gen9,
            gen02, gen04, gen05, // generatory pre diskretne empiricke rozdelenie
            gend04;              // modeluje decision, ci sa aktivita 4 vykona alebo nie

    private double time1, time2, time3, time6, time5, 
                time4, time7, time8, time9;

    private double part02, part04, part05;
    private double decision04;
    private double[] growth;
    int successful;

    public Simulation(int replications, long initSeed) {
        this.numberOfReplications = replications;
        
        if(initSeed == 0) {
            // nepouzije sa seed 
            Simulation.seed = new Random();
        } else {
            Simulation.seed = new Random(initSeed);
        }
        
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
        growth = new double [replications/100];
        successful = 0; // increment when tp_mc <= tp
    }

    @Override
    protected Integer doInBackground() throws Exception {

        double tp = 140;            // trvanie projektu
        double tp_mc = 0;           // trvanie projektu v jednom behu     
        long sumOfTimes = 0;     // suma vyslednych casov kvoli priemeru
        int current = 0;         // iterator
        int growthIterator = 0;
        double tempAvg = 0;
        int progress = 0;        // <0, 100> vstup pre progress bar
        
        while (current < numberOfReplications) {
            // setup of one run
            generateTimes();
            tp_mc = sumTimes();
            //System.out.println(Math.round(tp_mc));
            // map of values
            Integer count = dictionary.get(Math.round(tp_mc));
            if (count == null) {
                dictionary.put(Math.round(tp_mc), 1);
            }
            else {
                //System.out.println(Math.round(tp_mc) + ": " + count);
                dictionary.put(Math.round(tp_mc), count + 1);
            }
            // progress bar
            if(current%100==0) {
                progress = (int)((double)current/numberOfReplications*100);
                setProgress(progress);
            }
            // calculations
            successful = (tp_mc <= tp) ? (successful + 1) : successful;
            /*if(tp_mc > 118 && tp_mc < 178) {
                successful++;
            }*/
            sumOfTimes += tp_mc;
            tp_mc = 0;
            current++;
            // parcialne priemery
            if(current%100==0) {
                tempAvg = (double)sumOfTimes/current;
                //System.out.println(tempAvg);
                growth[growthIterator] = (tempAvg);
                growthIterator++;
            }
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
        
        //  Pravdepodobnosť, že sa aktivita 4 nebude vykonávať je 32%.  
        decision04 = gend04.nextDouble();
        if (decision04 < 0.32) {
            // 4 sa nebude vykonavat
            time4 = 0;
        } // diskretne empiricke 
        else {
            part04 = gen04.nextDouble();
            if (part04 < 0.2) {
                time4 = gen4.nextInt((27 - 19) + 1) + 19;
            } else {
                time4 = gen4.nextInt((44 - 28) + 1) + 28;
            }
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
        
        // spojite rovnomerne, Tmin = 10, Tmax = 16
        time6 = gen6.nextDouble()*(16 - 10) + 10;
        
        if(decision04 < 0.32) {
            time7 = (gen7.nextDouble()*(29 - 20) + 20)*1.15;
        } else {
            time7 = gen7.nextDouble()*(29 - 20) + 20;
        }
        // spojite rovnomerne, Tmin = 12, Tmax = 17
        time8 = gen8.nextDouble()*(17 - 12) + 12;
        // spojite rovnomerne, Tmin = 13, Tmax = 27
        time9 = gen9.nextDouble()*(27 - 13) + 13;
    }

    private double sumTimes() {
        double length = time1;
        double path1, path2, path3;
        
        path1 = time2 + time6;
        
        path2 = time3 + time4;
        
        path3 = time3 + time7 + time8;
        
        if(time4 == 0) {
            // aktivita 4 sa nevykona, pouzijeme cesty 1 a 3
            path1 += time5;
            length += (path1 > path3) ? path1 : path3;
            length += time9;
            
        } else {
            // aktivita 4 sa vykona, hladame dlhsiu cestu z (1 a 2) a 3
            double decision1 = max(path1, path2) + time5;
            length += max(decision1, path3) + time9;
        }
        
        return length;
    }
    
    public HashMap<Long, Integer> getHashMap() {
        return dictionary;
    }
    
    public double[] getGrowthMap() {
        return growth;
    }
    
    public int getSuccessful() {
        return successful;
    }
    
    public int findInterval(int min, int max) {
        double tp = 140;            // trvanie projektu
        double tp_mc = 0;           // trvanie projektu v jednom behu     
        int current = 0;         // iterator

        while (current < numberOfReplications) {
            // setup of one run
            generateTimes();
            tp_mc = sumTimes();

            if(tp_mc > min && tp_mc < max) {
                successful++;
            }
            tp_mc = 0;
            current++;
        }
        
        return successful;
    }
}
