/*********************************************************
 * Filename: GamePanel
 * Author: Branden Stahl
 * Created: 10/25/23
 * Modified: 11/08/23
 * 
 * Purpose: 
 * Panel for GameFrame, handles all rendering.
 * 
 * Attributes:
 *      -bodies: ArrayList<GenericBody>
 *      -framerate: int
 *      -wireframe: boolean
 *      -debugLines: boolean
 *      -manager: ManagerClass
 * 
 * Methods: 
 * 		+GamePanel<JFrame, boolean, ManagerClass>
 *      +updateArgs(ArrayList<GenericBody>, int): void
 *      -wireframeRender(Graphics): void
 *      -render(Graphics): void
 *      +paintComponent(Graphics): void
 * 
 */

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Insets;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class GamePanel extends JPanel {
    private ArrayList<GenericBody> bodies = new ArrayList<GenericBody>();
    private int framerate;
    private boolean wireframe;
    private boolean debugLines = true;
    private ManagerClass manager;

    GamePanel(JFrame parent, boolean wireframe, ManagerClass manager) {
        super();
        this.wireframe = wireframe;
        this.manager = manager;
    }

    public void updateArgs(ArrayList<GenericBody> bodies, int framerate) {
        this.bodies = bodies;
        this.framerate = framerate;
    }

    private void wireframeRender(Graphics g) { //* Render as wireframe
        for (GenericBody body : bodies) {
            manager.checkCollisions(body);
            //! Detect what body is what and render it.
            if (body instanceof RectBody) {
                g.drawRect(body.getPosition()[0]-(body.getSize()/2), body.getPosition()[1]-(body.getSize()/2), body.getSize(), body.getSize());
                g.drawString(Integer.toString(body.getID()), body.getPosition()[0], body.getPosition()[1]);
            } else if (body instanceof SphereBody) {
                g.drawOval(body.getPosition()[0]-(body.getSize()/2), body.getPosition()[1]-(body.getSize()/2), body.getSize(), body.getSize());
                g.drawString(Integer.toString(body.getID()), body.getPosition()[0], body.getPosition()[1]);
                int[][] bounds = body.getBounds();

                for (int j = 0; j < bounds.length; j++) {
                    g.drawLine(bounds[j][0], bounds[j][1], bounds[(j+1)%bounds.length][0], bounds[(j+1)%bounds.length][1]);
                };
            }

            if (debugLines) {
                g.drawLine(body.getPosition()[0], body.getPosition()[1], (int) (body.getVelocity()[0] + body.getPosition()[0]), (int) (body.getVelocity()[1] + body.getPosition()[1]));
            }
        }
    }

    private void render(Graphics g) {
        for (GenericBody body : bodies) {
            manager.checkCollisions(body);
            //! Detect what body is what and render it.
            g.setColor(body.getColor());
            if (body instanceof RectBody) {
                g.fillRect(body.getPosition()[0]-(body.getSize()/2), body.getPosition()[1]-(body.getSize()/2), body.getSize(), body.getSize());
            } else if (body instanceof SphereBody) {
                g.fillOval(body.getPosition()[0]-(body.getSize()/2), body.getPosition()[1]-(body.getSize()/2), body.getSize(), body.getSize());
            }

            g.setColor(Color.BLACK);
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Insets insets = this.getInsets();
        g.drawString("FPS: " + (framerate), insets.left+5, insets.top+15);

        if (wireframe) {
            wireframeRender(g);
        } else {
            render(g);
        }
    }
}
