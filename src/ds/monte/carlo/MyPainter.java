package ds.monte.carlo;

import java.awt.Color;
import java.awt.Graphics2D;
import javax.swing.JProgressBar;
import javax.swing.Painter;

/**
 *
 * @author Carmen
 */
class MyPainter implements Painter<JProgressBar>{

    private final Color color;
    
    public MyPainter(Color color) {
        this.color = color;
    }

    @Override
    public void paint(Graphics2D g, JProgressBar object, int width, int height) {
        g.setColor(color);
        g.fillRect(0, 0, width, height);
    }
    
    
    
}
