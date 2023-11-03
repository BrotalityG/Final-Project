// import java.awt.*;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

public class GuiClass {
    private GameFrame mainFrame;
    private Graphics g;
    private ManagerClass manager;
    private Insets insets;

    public void createMenu(ManagerClass manager) {
        this.manager = manager;

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
            createWindow();
            menu.dispose();
        });
        start.setHorizontalTextPosition(AbstractButton.CENTER);
        start.setVerticalTextPosition(AbstractButton.CENTER);
        start.setAlignmentX(AbstractButton.CENTER);
        start.setAlignmentY(AbstractButton.CENTER);
        start.setBounds(Toolkit.getDefaultToolkit().getScreenSize().width/2, Toolkit.getDefaultToolkit().getScreenSize().height/2, Toolkit.getDefaultToolkit().getScreenSize().width/4, Toolkit.getDefaultToolkit().getScreenSize().height/8);
        panel.add(start);
        
        menu.setVisible(true);
    }

    public void createWindow() {
        mainFrame = new GameFrame(manager);
        JPanel panel = new JPanel();
        panel.setOpaque(true);
        panel.setLayout(null);

        mainFrame.setContentPane(panel);
        mainFrame.pack();

        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);

        mainFrame.setVisible(true);

        g = mainFrame.getGraphics();
    }

    public void openSpawnMenu(MouseEvent e) { //? Spawn menu. This will be a big ole wall of swing code.
        JFrame spawnMenu = new JFrame("Spawn Menu");
        JPanel panel = new JPanel();
        panel.setOpaque(true);
        panel.setLayout(null);
        spawnMenu.setContentPane(panel);

        spawnMenu.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        spawnMenu.pack();
        spawnMenu.setSize(300, 300);
        spawnMenu.setLocation(e.getXOnScreen(), e.getYOnScreen());

        spawnMenu.setAlwaysOnTop(true);

        JCheckBox canCollide = new JCheckBox("Can Collide");
        canCollide.setLocation(5, 5);
        canCollide.setSize(135, 30);
        spawnMenu.add(canCollide);

        JCheckBox isStatic = new JCheckBox("Is Static");
        isStatic.setLocation(155-spawnMenu.getInsets().right, 5);
        isStatic.setSize(135, 30);
        spawnMenu.add(isStatic);

        JTextField mass = new JTextField("");
        mass.setLocation(5, 40);
        mass.setSize(135, 30);
        spawnMenu.add(mass);
        JTextComponent massText = new JTextField("Mass");
        massText.setEditable(false);
        massText.setLocation(5, 75);
        massText.setSize(135, 30);
        spawnMenu.add(massText); 

        JTextField size = new JTextField("");
        size.setLocation(155-spawnMenu.getInsets().right, 40);
        size.setSize(135, 30);
        spawnMenu.add(size);
        JTextComponent sizeText = new JTextField("Size");
        sizeText.setEditable(false);
        sizeText.setLocation(155-spawnMenu.getInsets().right, 75);
        sizeText.setSize(135, 30);
        spawnMenu.add(sizeText);

        int totalFrameSize = 300-(spawnMenu.getInsets().left+spawnMenu.getInsets().right);

        JTextComponent errorText = new JTextField("");
        errorText.setEditable(false);
        errorText.setForeground(Color.RED);
        errorText.setLocation(25, 160);
        errorText.setSize(totalFrameSize-50, 30);
        spawnMenu.add(errorText);

        int[] location = new int[] {e.getXOnScreen(), e.getYOnScreen()};

        JButton rect = new JButton("Spawn Rectangle");
        rect.addActionListener(e2 -> { //? If spawn rectangle button is clicked, spawn a rectangle.
            String massT = mass.getText();
            String sizeT = size.getText();

            //? Tell if mass and size are numbers
            if (!massT.matches("[0-9]+") || !sizeT.matches("[0-9]+")) {
                errorText.setText("Mass &/or Size must be a number!");
                return;
            }

            manager.createBody(0, Integer.parseInt(massT), Integer.parseInt(sizeT), location, canCollide.isEnabled(), isStatic.isEnabled());
            spawnMenu.dispose();
        });
        rect.setLocation(5, 200);
        rect.setSize(135, 30);
        spawnMenu.add(rect);

        JButton sphere = new JButton("Spawn Ball");
        sphere.addActionListener(e2 -> { //? If spawn ball button is clicked, spawn a ball.
            String massT = mass.getText();
            String sizeT = size.getText();

            //? Tell if mass and size are numbers
            if (!massT.matches("[0-9]+") || !sizeT.matches("[0-9]+")) {
                spawnMenu.getGraphics().drawString("Mass &/or Size must be a number!", 180, 5);
                return;
            }

            manager.createBody(1, Integer.parseInt(massT), Integer.parseInt(sizeT), location, canCollide.isEnabled(), isStatic.isEnabled());
            spawnMenu.dispose();
        });
        sphere.setLocation(155-spawnMenu.getInsets().right, 200);
        sphere.setSize(135, 30);
        spawnMenu.add(sphere);

        rect.setHorizontalTextPosition(AbstractButton.CENTER);
        rect.setVerticalTextPosition(AbstractButton.CENTER);
        rect.setAlignmentX(AbstractButton.CENTER);
        rect.setAlignmentY(AbstractButton.CENTER);

        sphere.setHorizontalTextPosition(AbstractButton.CENTER);
        sphere.setVerticalTextPosition(AbstractButton.CENTER);
        sphere.setAlignmentX(AbstractButton.CENTER);
        sphere.setAlignmentY(AbstractButton.CENTER);

        spawnMenu.setVisible(true);
    }

    public void render(ArrayList<GenericBody> bodies, int framerate) { //! On render, refresh the screen.
        if (g == null) {
            return;
        }

        insets = mainFrame.getInsets();
        
        g.clearRect(0, 0, Toolkit.getDefaultToolkit().getScreenSize().width, Toolkit.getDefaultToolkit().getScreenSize().height);
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
