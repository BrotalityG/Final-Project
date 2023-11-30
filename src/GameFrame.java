/*********************************************************
 * Filename: GameFrame
 * Author: Branden Stahl
 * Created: 10/25/23
 * Modified: 11/08/23
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
            @Override
            public void mouseClicked(MouseEvent e) {
                manager.onMouseClick(e);
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