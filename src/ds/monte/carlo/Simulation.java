package ds.monte.carlo;

import javax.swing.SwingWorker;

/**
 *
 * @author Carmen
 */
public abstract class Simulation extends SwingWorker{

    private String numberOfReplications;
    private String parameter2;
    private String parameter3;
    
    @Override
    protected int[] doInBackground() throws Exception {
        // TO DO - implement Monte Carlo simulation
        int[] results = new int[5];
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
