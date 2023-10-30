//***********************************/
//* Formulas:
//*  Force between two gravitational bodies: F= G(m1*m2)/r^2
//*  Gravitational constant: G= 6.67408e-11
//*  Acceleration due to gravity: g=gravity/s^2
//*  Force: mass*acceleration

import java.util.ArrayList;
//import java.time.*;

public class ManagerClass {
    private static ManagerClass manager;
    private static GuiClass gui = new GuiClass();
    private ArrayList<GenericBody> bodies = new ArrayList<GenericBody>();
    private boolean render = true;
    private long lastUpdate;

    public static void main(String[] args) {
        manager = new ManagerClass();

        gui.createMenu(manager);
    }

    public void startRender() {
        Thread runner = new Thread(() -> {
            lastUpdate = System.currentTimeMillis(); //* Define starting time, so that load times can be accounted for in simulation.

            System.out.println("Simulation running...");
            
            while (render) {
                long elapsed = System.currentTimeMillis() - lastUpdate; //! Ensure that all bodies recieve a static frame time;
                //! this is to prevent the simulation from running faster on faster computers, and to prevent certain objects
                //! from moving faster than others.

                updateAll(elapsed); //? Update all bodies in the simulation.
                gui.render(bodies); //? Render the simulation frame.

                lastUpdate = System.currentTimeMillis();
                try {
                    if (elapsed < 1000/60) { //? Skip wait if frame took too long to render.
                        Thread.sleep(1000/60-elapsed);
                    }
                } catch (Exception e) {
                    System.out.println("Error: " + e);
                }; //? Wait until the next frame (1/60th of a second).
            }

            System.out.println("Simulation stopped.");
        });

        runner.start();
    }

    public void stopRender() {
        render = false;
    }

    private void ApplyGravity(GenericBody body1, GenericBody body2) {
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
    }

    private void updateAll(long elapsedTime) {
        for (GenericBody body : bodies) {
            //Check gravitational effects of all bodies in existence.

            for (GenericBody body2 : bodies) {
                if (body != body2) {
                    ApplyGravity(body, body2); //?Apply gravity to 2 bodies.
                }
            }
        }
    }
}
