//***********************************/
//* Formulas:
//*  Force between two gravitational bodies: F= G(m1*m2)/r^2
//*  Gravitational constant: G= 6.67408e-11
//*  Acceleration due to gravity: g=gravity/s^2
//*  Force: mass*acceleration

import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

//import java.time.*;

public class ManagerClass {
    private static ManagerClass manager;
    private static GuiClass gui = new GuiClass();
    private ArrayList<GenericBody> bodies = new ArrayList<GenericBody>();
    private ArrayList<Object> constants = new ArrayList<Object>();
    private ArrayList<Object> settings = new ArrayList<Object>();
    private boolean render = true;
    private long lastUpdate;
    private long previousTime;
    private double preferredTime = 1.000/60;

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
            } else if (var.matches("true") || var.matches("false")) {
                constants.set(i, Boolean.parseBoolean(var));
            }

        }
    }

    public void startRender() {
        Thread runner = new Thread(() -> {
            lastUpdate = System.currentTimeMillis(); //* Define starting time, so that load times can be accounted for in simulation.
            previousTime = System.currentTimeMillis(); //* Previous frame time, for calculating elapsed time.
            int framerate = 0;

            System.out.println("Simulation running...");
            
            while (render) {
                double elapsed = (double) (System.currentTimeMillis() - lastUpdate)/1000; //! Ensure that all bodies recieve a static frame time;
                //! this is to prevent the simulation from running faster on faster computers, and to prevent certain objects
                //! from moving faster than others.

                previousTime = lastUpdate; //? Update the previous frame time.
                lastUpdate = System.currentTimeMillis(); //? Update the last update time.

                updateAll(elapsed); //? Update all bodies in the simulation.
                gui.render(bodies, framerate); //? Render the simulation frame.

                elapsed = (double) (System.currentTimeMillis() - previousTime)/1000; //? Calculate total the elapsed time of the frame.

                if (elapsed < preferredTime) { //? Skip wait if frame took too long to render.
                    int waitTime = (int) ((preferredTime - elapsed)*1000); //? Calculate the time to wait until the next frame.

                    try {
                        Thread.sleep(waitTime);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } //? Wait until the next frame (1/60th of a second).
                }

                elapsed = (double) (System.currentTimeMillis() - previousTime)/1000; //? Calculate total frame time after delay.
                framerate = (int) (1.0/elapsed);
            }

            System.out.println("Simulation stopped.");
        });

        runner.start();
    }

    public void stopRender() {
        render = false;
    }

    public void createBody(int type, double mass, int size, int[] position, boolean canCollide, boolean isStatic) {
        if (type == 0) {
            bodies.add(new RectBody(mass, size, position, canCollide, isStatic));
        } else if (type == 1) {
            bodies.add(new SphereBody(mass, size, position, canCollide, isStatic));
        }
    }

    public void onMouseClick(MouseEvent e) {
        switch (e.getButton()) {
            case 1: { //! Left Click
                //? This will only move the body if it's being dragged on.
            }
                
            case 3: { //! Right Click
                //? Open menu to spawn new body at mouse position.

                gui.openSpawnMenu(e);
            }
        }
    }

    private void ApplyGravity(GenericBody body1, GenericBody body2, double elapsedTime) { //! Not currently operational.
        double G = 6.67408e-11; //? Gravitational constant
        double m1 = body1.mass;
        double m2 = body2.mass;
        double r = Math.sqrt(Math.pow(body1.getPosition()[0] - body2.getPosition()[0], 2) + Math.pow(body1.getPosition()[1] - body2.getPosition()[1], 2)); //? Distance between the two bodies
        double F = G * ((m1 * m2) / Math.pow(r, 2)); //? Force between the two bodies
        double a = F / m1; //? Acceleration of body1 due to body2

        //? Calculate the angle between the two bodies
        double angle = Math.atan2(body2.getPosition()[1] - body1.getPosition()[1], body2.getPosition()[0] - body1.getPosition()[0]);

        //? Calculate the acceleration in the x and y directions
        double ax = a * Math.cos(angle);
        double ay = a * Math.sin(angle);

        //? Calculate the new velocity of body1
        int[] newVelocity = new int[] {body1.getVelocity()[0] + (int) ax, body1.getVelocity()[1] + (int) ay};

        //? Set the new velocity of body1
        body1.setVelocity(newVelocity);

        //? Calculate the new position of body1
        int[] newPosition = new int[] {body1.getPosition()[0] + (int) (newVelocity[0] * elapsedTime), body1.getPosition()[1] + (int) (newVelocity[1] * elapsedTime)};
    }

    private void updateAll(double elapsedTime) {
        for (GenericBody body : bodies) {
            //Check gravitational effects of all bodies in existence.

            for (GenericBody body2 : bodies) {
                if (body != body2) {
                    ApplyGravity(body, body2, elapsedTime); //?Apply gravity to 2 bodies.
                }
            }
        }
    }
}
