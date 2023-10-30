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
    }
}