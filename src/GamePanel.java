import java.awt.Graphics;
import java.awt.Insets;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class GamePanel extends JPanel {
    private ArrayList<GenericBody> bodies = new ArrayList<GenericBody>();
    private int framerate;

    GamePanel(JFrame parent) {
        super();
    }

    public void updateArgs(ArrayList<GenericBody> bodies, int framerate) {
        this.bodies = bodies;
        this.framerate = framerate;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Insets insets = this.getInsets();
        g.drawString("FPS: " + (framerate), insets.left+5, insets.top+15);

        for (int i = 0; i < bodies.size(); i++) {
            GenericBody body = bodies.get(i);
            
            //! Detect what body is what and render it.
            if (body instanceof RectBody) {
                g.drawRect(body.getPosition()[0]-(body.getSize()/2), body.getPosition()[1]-(body.getSize()/2), body.getSize(), body.getSize());
            } else if (body instanceof SphereBody) {
                g.drawOval(body.getPosition()[0]-(body.getSize()/2), body.getPosition()[1]-(body.getSize()/2), body.getSize(), body.getSize());
                int[][] bounds = body.getBounds();

                for (int j = 0; j < bounds.length; j++) {
                    g.drawLine(bounds[j][0], bounds[j][1], bounds[(j+1)%bounds.length][0], bounds[(j+1)%bounds.length][1]);
                };
            }
        }
    }
}
