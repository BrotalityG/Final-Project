/*********************************************************
 * Filename: GameFrame
 * Author: Branden Stahl
 * Created: 10/25/23
 * Modified: 12/11/23
 * 
 * Purpose: 
 * Frame for game, handles mouse events and window closing.
 * 
 * Attributes:
 * 
 * 
 * Methods: 
 * 		+GameFrame<ManagerClass>
 * 
 */

import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.Instant;

import javax.swing.JFrame;

public class GameFrame extends JFrame {
    public GameFrame(ManagerClass manager) {
        super("2-Dimensional Physics Simulation Environment (Sandbox)");

        manager.startRender();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                manager.stopRender();
            }
        });

        addMouseListener(new MouseAdapter() {
            int[] lastPos;
            int[] offset;
            Instant start;
            Boolean active = false;
            GenericBody body;
            int[] bodyPos;
            Thread thread;

            @Override
            public void mouseClicked(MouseEvent e) {
                manager.onMouseClick(e);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    body = manager.clickingBody(e);

                    if (body != null) {
                        lastPos = new int[] {e.getX(), e.getY()};
                        active = true;
                        start = Instant.now();
                        bodyPos = body.getPosition();

                        offset = new int[] {e.getX() - bodyPos[0], e.getY() - bodyPos[1]};

                        thread = new Thread() {
                            @Override
                            public void run() {
                                while (active) {
                                    manager.onMouseDrag(new int[] {lastPos[0], lastPos[1]}, new int[] {getMousePosition().x, getMousePosition().y}, (int) (Instant.now().toEpochMilli() - start.toEpochMilli()), body, bodyPos, offset);
                                    lastPos = new int[] {getMousePosition().x, getMousePosition().y};
                                    body.setVelocity(new double[] {0, 0});
                                    start = Instant.now();

                                    try {
                                        Thread.sleep(10);
                                    } catch (InterruptedException e) {
                                        //! Do nothing
                                    }
                                }
                            }
                        };

                        thread.start();
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (!active) return;
                active = false;
                thread.interrupt();

                int time = (int) (Instant.now().toEpochMilli() - start.toEpochMilli());

                manager.onMouseDrag(new int[] {lastPos[0], lastPos[1]}, new int[] {e.getX(), e.getY()}, time, body, bodyPos, offset);
            }
        });

        addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                manager.onKeyPress(e);
            }

            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                //! Do nothing
            }

            @Override
            public void keyTyped(java.awt.event.KeyEvent e) {
                //! Do nothing
            }
        });
    }
}