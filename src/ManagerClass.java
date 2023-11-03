//***********************************/
//* Formulas:
//*  Force between two gravitational bodies: F= G(m1*m2)/r^2
//*  Gravitational constant: G= 6.67408e-11
//*  Acceleration due to gravity: g=gravity/s^2
//*  Force: mass*acceleration

/** 
**    Format for Constants.DAT:
*        0 = Earth Gravity Acceleration
*        1 = Pixels per Meter
*
**    Format for Settings.DAT:
*        0 = Use Earth Gravity
*        1 = Use Particle Gravity
*        2 = Exaggeration Factor (Basically, by what magnitude is particle gravity multiplied by)
*/

import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class ManagerClass {
    private static ManagerClass manager;
    private static GuiClass gui = new GuiClass();
    private ArrayList<GenericBody> bodies = new ArrayList<GenericBody>();
    private ArrayList<Object> constants = new ArrayList<Object>();
    private ArrayList<Object> settings = new ArrayList<Object>();
    private Timer timer;
    private Instant lastUpdate;

    public static void main(String[] args) {
        manager = new ManagerClass();
        manager.readData();

        gui.createMenu(manager);
    }

    private void readData() {
        try (FileReader read = new FileReader("Constants.DAT")) {
            BufferedReader reader = new BufferedReader(read);
            String line;
            while ((line = reader.readLine()) != null) {
                constants.add(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try (FileReader read = new FileReader("Settings.DAT")) {
            BufferedReader reader = new BufferedReader(read);
            String line;
            while ((line = reader.readLine()) != null) {
                settings.add(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (int i = 0; i < constants.size(); i++) {
            String var = (String) constants.get(i);

            if (var.matches("[0-9]+")) {
                constants.set(i, Integer.parseInt(var));
            } else if (var.matches("[0-9]+.[0-9]+")) {
                constants.set(i, Double.parseDouble(var));
            } else if (var.matches("true") || var.matches("false")) {
                constants.set(i, Boolean.parseBoolean(var));
            }

        }

        for (int i = 0; i < settings.size(); i++) {
            String var = (String) settings.get(i);

            if (var.matches("[0-9]+")) {
                settings.set(i, Integer.parseInt(var));
            } else if (var.matches("[0-9]+.[0-9]+")) {
                settings.set(i, Double.parseDouble(var));
            } else if (var.matches("true") || var.matches("false")) {
                settings.set(i, Boolean.parseBoolean(var));
            }

        }
    }

    public void startRender() {
        timer = new Timer("Framerate Manager", true);

        lastUpdate = Instant.now();

        System.out.println("Simulation Started...");

        timer.scheduleAtFixedRate(new TimerTask(){
            public void run() {
                //! Get elasped time since last frame and reset timer.
                long elapsed = Duration.between(lastUpdate, Instant.now()).toMillis();
                lastUpdate = Instant.now();

                if (elapsed <= 0) {
                    elapsed = 1;
                }

                updateAll(elapsed); //? Update all bodies in the simulation.
                gui.render(bodies, (int) (1000/elapsed)); //? Render the simulation frame.
            }
        }, 0, 1000/60);
    }

    public void stopRender() {
        timer.cancel();
        System.out.println("Simulation Stopped.");
    }

    public void createBody(int type, double mass, int size, double elasticity, int[] position, boolean canCollide, boolean isStatic) {
        if (type == 0) {
            bodies.add(new RectBody(mass, size, elasticity, position, canCollide, isStatic));
        } else if (type == 1) {
            bodies.add(new SphereBody(mass, size, elasticity, position, canCollide, isStatic));
        }
    }

    public void onMouseClick(MouseEvent e) {
        switch (e.getButton()) {
            case MouseEvent.BUTTON1: //! Left Click
                //? This will only move the body if it's being dragged on.
            
                break;
            case MouseEvent.BUTTON3: //! Right Click
                //? Open menu to spawn new body at mouse position.

                gui.openSpawnMenu(e);
                break;
        }
    }

    private void Apply2BodyGravity(GenericBody body1, GenericBody body2, double elapsedTime) { //! Apply gravity to a body with reference to another.
        if ((Boolean) settings.get(1)) { //? If using Particle Gravity
            double exag = (Double) settings.get(2); //? Exaggeration Factor
            double G = 6.67408e-11; //? Gravitational constant
            double m1 = body1.getMass()*(Math.pow(10, exag));
            double m2 = body2.getMass()*(Math.pow(10, exag));
            double r = Math.sqrt(Math.pow(body1.getPosition()[0] - body2.getPosition()[0], 2) + Math.pow(body1.getPosition()[1] - body2.getPosition()[1], 2)); //? Distance between the two bodies
            double F = G * ((m1 * m2) / Math.pow(r, 2)); //? Force between the two bodies
            double a = F / m1; //? Acceleration of body1 due to body2

            //? Calculate the angle between the two bodies
            double angle = Math.atan2(body2.getPosition()[1] - body1.getPosition()[1], body2.getPosition()[0] - body1.getPosition()[0]);

            //? Calculate the acceleration in the x and y directions
            double ax = a * Math.cos(angle);
            double ay = a * Math.sin(angle);

            //? Calculate the new velocity of body1
            double[] newVelocity = new double[] {body1.getVelocity()[0] + ax, body1.getVelocity()[1] + ay};

            //? Set the new velocity of body1
            body1.setVelocity(newVelocity);
        }
    }

    private void ApplyGravity(GenericBody body, double elapsedTime) { //! Apply gravity to a body.
        if ((Boolean) settings.get(0)) { //? If using Earth Gravity
            double g = (Double) constants.get(0); //? Earth Gravity Acceleration

            //? Calculate the new velocity of the body
            double[] newVelocity = new double[] {body.getVelocity()[0], body.getVelocity()[1] + g};

            //? Set the new velocity of the body
            body.setVelocity(newVelocity);
        }
    }

    private void updatePosition(GenericBody body, double elapsedTime) { //! Update the position of a body without gravitational effect.
        int[] newPosition = new int[] {body.getPosition()[0], body.getPosition()[1]};
        int pixels = (int) constants.get(1);

        //? Calculate the new position of the body
        newPosition = new int[] {newPosition[0] + (int) ((body.getVelocity()[0] * elapsedTime)/pixels), newPosition[1] + (int) ((body.getVelocity()[1] * elapsedTime)/pixels)};

        //? Set the new position of the body
        body.move(newPosition);
    }

    private void checkBounds(GenericBody body) {
        //? Get positional info
        int[] newPosition = new int[] {body.getPosition()[0], body.getPosition()[1]};
        Insets insets = gui.getInsets();
        GamePanel mainPanel = gui.getPanel();
        int[][] bounds = body.getEdgeBounds();
        int size = body.getSize();
        double elasticity = body.getElasticity();

        //? Check if the body is out of bounds, and if so, move it back in bounds & invert velocity.
        if (bounds[0][0] < (0+insets.left)) {
            newPosition[0] = 0+insets.left+(size/2);
            body.setVelocity(new double[] {-body.getVelocity()[0]*elasticity, body.getVelocity()[1]});
        } else if (bounds[2][0] > mainPanel.getWidth()-insets.right) {
            newPosition[0] = mainPanel.getWidth()-insets.right-(size/2);
            body.setVelocity(new double[] {-body.getVelocity()[0]*elasticity, body.getVelocity()[1]});
        } else if (bounds[1][1] > mainPanel.getHeight()-insets.bottom) {
            newPosition[1] = mainPanel.getHeight()-insets.bottom-(size/2);
            body.setVelocity(new double[] {body.getVelocity()[0], -body.getVelocity()[1]*elasticity});
        } else if (bounds[3][1] < (0+insets.top)) {
            newPosition[1] = 0+insets.top+(size/2);
            body.setVelocity(new double[] {body.getVelocity()[0], -body.getVelocity()[1]*elasticity});
        }

        //? Move the body to the new position
        body.move(newPosition);
    }

    private void updateAll(double elapsedTime) {
        for (GenericBody body : bodies) {
            //? If the body is static, skip it.
            if (!body.isStatic()) {
                ApplyGravity(body, elapsedTime); //? Apply gravity to a body.

                //? Check gravitational effects of all bodies in existence.
                for (GenericBody body2 : bodies) {
                    if (body != body2) {
                        Apply2BodyGravity(body, body2, elapsedTime); //? Apply gravity to a body with reference to another.
                    }
                }

                updatePosition(body, elapsedTime); //? Update the position of a body.

                checkBounds(body); //? Confirm body is in bounds and fix if not.
            }
        }
    }
}
