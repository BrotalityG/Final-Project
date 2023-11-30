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

 //* Do note that this file is never going to meet the line limit per method simply due to how swing
 //* requires the code to be written. I will still try though with the actions.


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
    private JFrame pauseMenu;
    private GamePanel panel;
    private Graphics g;
    private ManagerClass manager;
    private ArrayList<Object> settings;
    private Insets insets;
    private boolean paused = false;
    private boolean inSaveMenu = false;

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

        Toolkit tk = Toolkit.getDefaultToolkit();

        //? Populate screen
        //* Title
        JTextField title = new JTextField("2-Dimensional Physics Sandbox");
        title.setEditable(false);
        title.setBounds(tk.getScreenSize().width/2-tk.getScreenSize().width/4, tk.getScreenSize().height/2-tk.getScreenSize().height/4, tk.getScreenSize().width/2, tk.getScreenSize().height/24);
        title.setHorizontalAlignment(JTextField.CENTER);
        title.setFont(title.getFont().deriveFont((float) tk.getScreenSize().height/32));
        title.setBorder(null);
        panel1.add(title);

        //* Subtitle
        JTextField subtitle = new JTextField("An Experiment By Branden Stahl");
        subtitle.setEditable(false);
        subtitle.setBounds(tk.getScreenSize().width/2-tk.getScreenSize().width/4, tk.getScreenSize().height/2-tk.getScreenSize().height/4+tk.getScreenSize().height/24, tk.getScreenSize().width/2, tk.getScreenSize().height/48);
        subtitle.setHorizontalAlignment(JTextField.CENTER);
        subtitle.setFont(subtitle.getFont().deriveFont((float) tk.getScreenSize().height/64));
        subtitle.setBorder(null);
        panel1.add(subtitle);

        //* Launch Sandbox
        JButton start = new JButton("Launch Sandbox");
        start.addActionListener(e -> {
            createWindow(wireframe);
            menu.dispose();
        });
        start.setBounds(tk.getScreenSize().width/2-tk.getScreenSize().width/8, tk.getScreenSize().height/2-tk.getScreenSize().height/16, tk.getScreenSize().width/4, tk.getScreenSize().height/8);
        panel1.add(start);

        //* Load from file
        JButton loadFromFile = new JButton("Load From File (Currently Not Implemented)");
        loadFromFile.addActionListener(e -> {
            //! Currently not implemented
            // menu.dispose();
        });
        loadFromFile.setBounds(tk.getScreenSize().width/2-tk.getScreenSize().width/8, tk.getScreenSize().height/2+tk.getScreenSize().height/16+5, tk.getScreenSize().width/4, tk.getScreenSize().height/8);
        panel1.add(loadFromFile);

        //* Settings
        JButton set = new JButton("Settings");
        set.addActionListener(e -> {
            openSettings();
            menu.dispose();
        });
        set.setBounds(tk.getScreenSize().width/2-tk.getScreenSize().width/8, tk.getScreenSize().height/2+(tk.getScreenSize().height/16)*3+10, tk.getScreenSize().width/4, tk.getScreenSize().height/16);
        panel1.add(set);

        //* Exit
        JButton exit = new JButton("Exit");
        exit.addActionListener(e -> {
            menu.dispose();
        });
        exit.setBounds(tk.getScreenSize().width/2-tk.getScreenSize().width/8, tk.getScreenSize().height/2+(tk.getScreenSize().height/16)*4+15, tk.getScreenSize().width/4, tk.getScreenSize().height/16);
        panel1.add(exit);
        
        menu.setVisible(true);
    }

    private void createWindow(boolean wireframe) { //? Create window
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

    public void overwriteMenu(String filename) {
        JFrame overwriteMenu = new JFrame("Overwrite Menu");
        JPanel panel = new JPanel();
        panel.setOpaque(true);
        panel.setLayout(null);
        overwriteMenu.setContentPane(panel);

        overwriteMenu.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        overwriteMenu.pack();
        overwriteMenu.setSize(300, 200);
        overwriteMenu.setLocationRelativeTo(mainFrame);

        overwriteMenu.setAlwaysOnTop(true);
        
        JTextField TitleText = new JTextField(filename + " already exists! Overwrite?");
        TitleText.setEditable(false);
        TitleText.setHorizontalAlignment(JTextField.CENTER);
        TitleText.setBorder(null);
        TitleText.setForeground(Color.RED);
        TitleText.setBounds(5, 5, 300-overwriteMenu.getInsets().right-overwriteMenu.getInsets().left-10, 30);
        overwriteMenu.add(TitleText);

        JButton yes = new JButton("Yes");
        yes.addActionListener(e -> { //? If save button is clicked, save to file.
            manager.saveToFile(filename, true);
            overwriteMenu.dispose();
            inSaveMenu = false;
        });
        yes.setLocation(5, 40);
        yes.setSize(300-overwriteMenu.getInsets().right-overwriteMenu.getInsets().left-10, 30);
        overwriteMenu.add(yes);

        JButton no = new JButton("No");
        no.addActionListener(e -> {
            overwriteMenu.dispose();
            inSaveMenu = false;
        });
        no.setLocation(5, overwriteMenu.getHeight()-overwriteMenu.getInsets().bottom-overwriteMenu.getInsets().top-35);
        no.setSize(300-overwriteMenu.getInsets().right-overwriteMenu.getInsets().left-10, 30);
        overwriteMenu.add(no);

        overwriteMenu.setVisible(true);

        inSaveMenu = true;
    }

    private void saveMenu() {
        JFrame saveMenu = new JFrame("Save Menu");
        JPanel panel = new JPanel();
        panel.setOpaque(true);
        panel.setLayout(null);
        saveMenu.setContentPane(panel);

        saveMenu.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        saveMenu.pack();
        saveMenu.setSize(300, 200);
        saveMenu.setLocationRelativeTo(mainFrame);

        saveMenu.setAlwaysOnTop(true);
        
        JTextField errorText = new JTextField("");
        errorText.setEditable(false);
        errorText.setHorizontalAlignment(JTextField.CENTER);
        errorText.setBorder(null);
        errorText.setForeground(Color.RED);
        errorText.setBounds(5, 75, 300-saveMenu.getInsets().right-saveMenu.getInsets().left-10, 30);
        saveMenu.add(errorText);

        JTextField saveName = new JTextField("Save Name");
        saveName.setLocation(5, 5);
        saveName.setSize(300-saveMenu.getInsets().right-saveMenu.getInsets().left-10, 30);
        saveMenu.add(saveName);

        JButton save = new JButton("Save");
        save.addActionListener(e -> { //? If save button is clicked, save to file.
            //? Make sure file name is a valid file name
            if (saveName.getText().matches("[abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890_-]+") && saveName.getText().length() > 0) {
                manager.saveToFile(saveName.getText(), false);
                saveMenu.dispose();
                inSaveMenu = false;
            } else {
                errorText.setText("Invalid File Name!");
            }
        });
        save.setLocation(5, 40);
        save.setSize(300-saveMenu.getInsets().right-saveMenu.getInsets().left-10, 30);
        saveMenu.add(save);

        JButton exit = new JButton("Exit");
        exit.addActionListener(e -> {
            saveMenu.dispose();
            inSaveMenu = false;
        });
        exit.setLocation(5, saveMenu.getHeight()-saveMenu.getInsets().bottom-saveMenu.getInsets().top-35);
        exit.setSize(300-pauseMenu.getInsets().right-pauseMenu.getInsets().left-10, 30);
        saveMenu.add(exit);

        saveMenu.setVisible(true);

        inSaveMenu = true;
    }

    public void openPauseMenu() {
        if (paused) {
            pauseMenu.dispose();
            manager.startRender();
            paused = false;
            return;
        }

        manager.stopRender();

        pauseMenu = new JFrame("Pause Menu");
        JPanel panel = new JPanel();
        panel.setOpaque(true);
        panel.setLayout(null);
        pauseMenu.setContentPane(panel);

        pauseMenu.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        pauseMenu.pack();
        pauseMenu.setSize(300, 300);
        pauseMenu.setLocationRelativeTo(mainFrame);

        pauseMenu.setAlwaysOnTop(true);

        JButton resume = new JButton("Resume");
        resume.addActionListener(e -> {
            pauseMenu.dispose();
            manager.startRender();
            paused = false;
        });
        resume.setLocation(5, 5);
        resume.setSize(300-pauseMenu.getInsets().right-pauseMenu.getInsets().left-10, 30);
        pauseMenu.add(resume);

        JButton save = new JButton("Save To File");
        save.addActionListener(e -> {
            if (!inSaveMenu) {
                saveMenu();
            }
        });
        save.setLocation(5, 40);
        save.setSize(300-pauseMenu.getInsets().right-pauseMenu.getInsets().left-10, 30);
        pauseMenu.add(save);

        JButton exit = new JButton("Exit To Menu");
        exit.addActionListener(e -> {
            pauseMenu.dispose();
            mainFrame.dispose();
            createMenu();
        });
        exit.setLocation(5, (300-pauseMenu.getInsets().bottom-pauseMenu.getInsets().top)-70);
        exit.setSize(300-pauseMenu.getInsets().right-pauseMenu.getInsets().left-10, 30);
        pauseMenu.add(exit);

        JButton exit1 = new JButton("Exit To Desktop");
        exit1.addActionListener(e -> {
            System.exit(0);
        });
        exit1.setLocation(5, (300-pauseMenu.getInsets().bottom-pauseMenu.getInsets().top)-35);
        exit1.setSize(300-pauseMenu.getInsets().right-pauseMenu.getInsets().left-10, 30);
        pauseMenu.add(exit1);

        pauseMenu.setVisible(true);

        paused = true;
    }

/** 
**    Format for Constants.DAT:
*        0 = Earth Gravity Acceleration
*        1 = Pixels per Meter
*
**    Format for Settings.DAT:
*        0 = Use Earth Gravity
*        1 = Use Particle Gravity
*        2 = Exaggeration Factor (Basically, by what magnitude is particle gravity multiplied by)
*        3 = Wireframe Rendering
*        4 = Debug Mode
*/

    private boolean setEF(String ef) { //? Set EF
        boolean isSet = false;

        if (ef.matches("[0-9]+.[0-9]+")) {
            settings.set(2, Double.parseDouble(ef));
            isSet = true;
        } else if (ef.matches("[0-9]+")) {
            settings.set(2, (double) Integer.parseInt(ef));
            isSet = true;
        }

        return isSet;
    }

    private void openSettings() {
        JFrame menu = new JFrame("2DPSE Menu - Settings");
        JPanel panel1 = new JPanel();
        panel1.setOpaque(true);
        panel1.setLayout(null);
        menu.setContentPane(panel1);

        menu.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        menu.pack();
        menu.setExtendedState(JFrame.MAXIMIZED_BOTH);

        Toolkit tk = Toolkit.getDefaultToolkit();

        //? Populate screen
        //* Title
        JTextField title = new JTextField("Settings");
        title.setEditable(false);
        title.setBounds(tk.getScreenSize().width/2-tk.getScreenSize().width/4, tk.getScreenSize().height/4, tk.getScreenSize().width/2, tk.getScreenSize().height/24);
        title.setHorizontalAlignment(JTextField.CENTER);
        title.setFont(title.getFont().deriveFont((float) tk.getScreenSize().height/32));
        title.setBorder(null);
        panel1.add(title);

        //* Subtitle
        JTextField subtitle = new JTextField("Edit and change settings here!");
        subtitle.setEditable(false);
        subtitle.setBounds(tk.getScreenSize().width/2-tk.getScreenSize().width/4, tk.getScreenSize().height/4+tk.getScreenSize().height/24, tk.getScreenSize().width/2, tk.getScreenSize().height/48);
        subtitle.setHorizontalAlignment(JTextField.CENTER);
        subtitle.setFont(subtitle.getFont().deriveFont((float) tk.getScreenSize().height/64));
        subtitle.setBorder(null);
        panel1.add(subtitle);

        //* Selection Box
        JTextField selection = new JTextField("Toggleable Settings:");
        selection.setEditable(false);
        selection.setBorder(null);
        selection.setHorizontalAlignment(JTextField.CENTER);
        selection.setFont(selection.getFont().deriveFont((float) tk.getScreenSize().height/64));
        selection.setBounds(tk.getScreenSize().width/4, tk.getScreenSize().height/4+tk.getScreenSize().height/12, tk.getScreenSize().width/2, tk.getScreenSize().height/48);
        panel1.add(selection);

        //* Earth Gravity
        JCheckBox earthGravity = new JCheckBox("Use Earth Gravity");
        earthGravity.setSelected((boolean) settings.get(0));
        earthGravity.setBounds(tk.getScreenSize().width/2-tk.getScreenSize().width/10, tk.getScreenSize().height/2-tk.getScreenSize().height/8, tk.getScreenSize().width/16, tk.getScreenSize().height/64);
        panel1.add(earthGravity);

        //* Particle Gravity
        JCheckBox particleGravity = new JCheckBox("Use Particle Gravity");
        particleGravity.setSelected((boolean) settings.get(1));
        particleGravity.setBounds(tk.getScreenSize().width/2-tk.getScreenSize().width/10, tk.getScreenSize().height/2-tk.getScreenSize().height/8+tk.getScreenSize().height/24, tk.getScreenSize().width/16, tk.getScreenSize().height/64);
        panel1.add(particleGravity);

        //* Wireframe Rendering
        JCheckBox wireframe = new JCheckBox("Wireframe Rendering");
        wireframe.setSelected((boolean) settings.get(3));
        wireframe.setBounds(tk.getScreenSize().width/2+tk.getScreenSize().width/20, tk.getScreenSize().height/2-tk.getScreenSize().height/8, tk.getScreenSize().width/16, tk.getScreenSize().height/64);
        panel1.add(wireframe);

        //* Debug Mode
        JCheckBox debugMode = new JCheckBox("Debug Mode");
        debugMode.setSelected((boolean) settings.get(4));
        debugMode.setBounds(tk.getScreenSize().width/2+tk.getScreenSize().width/20, tk.getScreenSize().height/2-tk.getScreenSize().height/8+tk.getScreenSize().height/24, tk.getScreenSize().width/16, tk.getScreenSize().height/64);
        panel1.add(debugMode);

        //* Exaggeration Factor
        JTextField efBox = new JTextField(Double.toString((double) settings.get(2)));
        efBox.setBounds(tk.getScreenSize().width/2-tk.getScreenSize().width/16, tk.getScreenSize().height/2, tk.getScreenSize().width/8, tk.getScreenSize().height/48);
        panel1.add(efBox);
        JTextField efText = new JTextField("Exaggeration Factor:");
        efText.setEditable(false);
        efText.setBorder(null);
        efText.setHorizontalAlignment(JTextField.CENTER);
        efText.setFont(selection.getFont().deriveFont((float) tk.getScreenSize().height/64));
        efText.setBounds(tk.getScreenSize().width/4, tk.getScreenSize().height/2-tk.getScreenSize().height/32, tk.getScreenSize().width/2, tk.getScreenSize().height/48);
        panel1.add(efText);

        //* Error Text
        JTextField errorText = new JTextField("");
        errorText.setEditable(false);
        errorText.setHorizontalAlignment(JTextField.CENTER);
        errorText.setBorder(null);
        errorText.setForeground(Color.RED);
        errorText.setBounds(tk.getScreenSize().width/2-tk.getScreenSize().width/8, tk.getScreenSize().height/2+tk.getScreenSize().height/32, tk.getScreenSize().width/4, tk.getScreenSize().height/48);
        panel1.add(errorText);

        //* Save
        JButton save = new JButton("Save & Exit");
        save.addActionListener(e -> {
            if (!setEF(efBox.getText())) {
                errorText.setText("Please ensure that the Exaggeration Factor is a decimal number!");
                return;
            }

            settings.set(0, earthGravity.isSelected());
            settings.set(1, particleGravity.isSelected());
            settings.set(3, wireframe.isSelected());
            settings.set(4, debugMode.isSelected());

            FileAccessor files = new FileAccessor();

            files.writeSettings(settings);
            manager.readData();

            createMenu();
            menu.dispose();
        });
        save.setBounds(tk.getScreenSize().width/2-tk.getScreenSize().width/8, tk.getScreenSize().height/2+tk.getScreenSize().height/16, tk.getScreenSize().width/4, tk.getScreenSize().height/16);
        panel1.add(save);

        //* Exit
        JButton exit = new JButton("Exit Without Saving");
        exit.addActionListener(e -> {
            createMenu();
            menu.dispose();
        });
        exit.setBounds(tk.getScreenSize().width/2-tk.getScreenSize().width/8, tk.getScreenSize().height/2+tk.getScreenSize().height/8+5, tk.getScreenSize().width/4, tk.getScreenSize().height/16);
        panel1.add(exit);

        menu.setVisible(true);
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
