/*********************************************************
 * Filename: GuiClass
 * Author: Branden Stahl
 * Created: 10/09/23
 * Modified: 11/27/23
 * 
 * Purpose: 
 * Gui for game, handles all graphical display.
 * 
 * Attributes:
 *      -mainFrame: GameFrame
 *      -panel: GamePanel
 *      -g: Graphics
 *      -manager: ManagerClass
 *      -settings: ArrayList<Object>
 *      -insets: Insets
 *
 * 
 * Methods: 
 * 		+GuiClass<ManagerClass, ArrayList<Object>>
 *      +createMenu(): void
 *      +createWindow(boolean): void
 *      +openSpawnMenu(MouseEvent): void
 *      +render(ArrayList<GenericBody>, int): void
 *      +getInsets(): Insets
 *      +getFrame(): GameFrame
 *      +getPanel(): GamePanel
 * 
 */

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
    private GamePanel panel;
    private Graphics g;
    private ManagerClass manager;
    private ArrayList<Object> settings;
    private Insets insets;

    public GuiClass(ManagerClass manager, ArrayList<Object> settings) {
        this.manager = manager;
        this.settings = settings;
    }

    public void createMenu() { //? Create menu
        //* Create blank window
        boolean wireframe = (boolean) settings.get(3);
        JFrame menu = new JFrame("2DPSE Menu");
        JPanel panel1 = new JPanel();
        panel1.setOpaque(true);
        panel1.setLayout(null);
        menu.setContentPane(panel1);

        menu.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        menu.pack();
        menu.setExtendedState(JFrame.MAXIMIZED_BOTH);

        //* Populate screen
        JButton start = new JButton("Launch Sandbox");
        start.addActionListener(e -> {
            createWindow(wireframe);
            menu.dispose();
        });
        start.setHorizontalTextPosition(AbstractButton.CENTER);
        start.setVerticalTextPosition(AbstractButton.CENTER);
        start.setAlignmentX(AbstractButton.CENTER);
        start.setAlignmentY(AbstractButton.CENTER);
        start.setBounds(Toolkit.getDefaultToolkit().getScreenSize().width/2, Toolkit.getDefaultToolkit().getScreenSize().height/2, Toolkit.getDefaultToolkit().getScreenSize().width/4, Toolkit.getDefaultToolkit().getScreenSize().height/8);
        panel1.add(start);
        
        menu.setVisible(true);
    }

    public void createWindow(boolean wireframe) { //? Create window
        mainFrame = new GameFrame(manager);
        panel = new GamePanel(mainFrame, wireframe, manager);
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
        canCollide.setSelected(true);
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

        JTextField elas = new JTextField("0.75");
        elas.setLocation(5, 110);
        elas.setSize(135, 30);
        spawnMenu.add(elas);
        JTextComponent elasText = new JTextField("Elasticity");
        elasText.setEditable(false);
        elasText.setLocation(155-spawnMenu.getInsets().right, 110);
        elasText.setSize(135, 30);
        spawnMenu.add(elasText);

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
            String elasT = elas.getText();

            //? Tell if mass and size are numbers
            if ((!massT.matches("[0-9]+") && !massT.matches("[0-9]+.[0-9]+")) || !sizeT.matches("[0-9]+") || !elasT.matches("[0-9]+.[0-9]+")) {
                errorText.setText("Check Mass, Size, or Elasticity!");
                return;
            }

            manager.createBody(0, Double.parseDouble(massT), Integer.parseInt(sizeT), Double.parseDouble(elasT), location, canCollide.isSelected(), isStatic.isSelected());
            spawnMenu.dispose();
            
            if ((boolean) settings.get(4)) {
                System.out.println("Spawned rectangle with mass " + Double.parseDouble(massT) + " and size " + Integer.parseInt(sizeT));
            }
        });
        rect.setLocation(5, 200);
        rect.setSize(135, 30);
        spawnMenu.add(rect);

        JButton sphere = new JButton("Spawn Ball");
        sphere.addActionListener(e2 -> { //? If spawn ball button is clicked, spawn a ball.
            String massT = mass.getText();
            String sizeT = size.getText();
            String elasT = elas.getText();

            //? Tell if mass and size are numbers
            if ((!massT.matches("[0-9]+") && !massT.matches("[0-9]+.[0-9]+")) || !sizeT.matches("[0-9]+") || !elasT.matches("[0-9]+.[0-9]+")) {
                errorText.setText("Check Mass, Size, or Elasticity!");
                return;
            }
            manager.createBody(1, Double.parseDouble(massT), Integer.parseInt(sizeT), Double.parseDouble(elasT), location, canCollide.isSelected(), isStatic.isSelected());
            spawnMenu.dispose();
            
            if ((boolean) settings.get(4)) {
                System.out.println("Spawned ball with mass " + Double.parseDouble(massT) + " and size " + Integer.parseInt(sizeT));
            }
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

        if ((boolean) settings.get(4)) {
            System.out.println("Opened Spawn Menu");
        }
    }

    public void render(ArrayList<GenericBody> bodies, int framerate) { //! On render, refresh the screen.
        if (g == null) {
           return;
        }

        panel.updateArgs(bodies, framerate);
        mainFrame.repaint();
    }

    public Insets getInsets() {
        if (insets == null) {
            insets = mainFrame.getInsets();
        }
        return insets;
    }

    public GameFrame getFrame() {
        return mainFrame;
    }

    public GamePanel getPanel() {
        return panel;
    }
}
