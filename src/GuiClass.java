// import java.awt.*;
import java.awt.Toolkit;
import java.util.ArrayList;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class GuiClass {
    public void createMenu(ManagerClass manager) {
        //* Create blank window
        JFrame menu = new JFrame("2DPSE Menu");
        JPanel panel = new JPanel();
        panel.setOpaque(true);
        panel.setLayout(null);
        menu.setContentPane(panel);

        menu.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        menu.pack();
        menu.setExtendedState(JFrame.MAXIMIZED_BOTH);

        //* Populate screen
        JButton start = new JButton("Launch Sandbox");
        start.addActionListener(e -> {
            menu.dispose();
            createWindow(manager);
        });
        start.setHorizontalTextPosition(AbstractButton.CENTER);
        start.setVerticalTextPosition(AbstractButton.CENTER);
        start.setAlignmentX(AbstractButton.CENTER);
        start.setAlignmentY(AbstractButton.CENTER);
        start.setBounds(Toolkit.getDefaultToolkit().getScreenSize().width/2, Toolkit.getDefaultToolkit().getScreenSize().height/2, Toolkit.getDefaultToolkit().getScreenSize().width/4, Toolkit.getDefaultToolkit().getScreenSize().height/8);
        panel.add(start);
        
        menu.setVisible(true);
    }

    public void createWindow(ManagerClass manager) {
        GameFrame mainFrame = new GameFrame(manager);

        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);

        mainFrame.setVisible(true);
    }



    public void render(ArrayList<GenericBody> bodies) {
        //Currently null
    }
}
