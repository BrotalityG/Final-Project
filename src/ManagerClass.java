/*********************************************************
 * Filename: ManagerClass
 * Author: Branden Stahl
 * Created: 10/09/23
 * Modified: 11/27/23
 * 
 * Purpose: 
 * Main Manager class, handles all physics, timing, and controls of the simulation.
 * 
 * Attributes:
 * 		-manager: ManagerClass
 * 		-gui: GuiClass
 *      -files: FileAccessor
 * 		-bodies: ArrayList<GenericBody>
 * 		-constants: ArrayList<Object>
 * 		-settings: ArrayList<Object>
 * 		-timer: Timer
 * 		-lastUpdate: Instant
 * 
 * Methods: 
 * 		+main(String[]): void
 * 		-readData(): void
 * 		+startRender(): void
 * 		+stopRender(): void
 * 		+generateNewID(): int
 * 		+createBody(int, double, int, double, int[], boolean, boolean): void
 * 		+onMouseClick(MouseEvent): void
 * 		-checkToApply(GenericBody, GenericBody): boolean
 * 		-Apply2BodyGravity(GenericBody, GenericBody, double): void
 * 		-ApplyGravity(GenericBody, double): void
 * 		-updatePosition(GenericBody, double): void
 * 		-checkBounds(GenericBody): void
 * 		-getBodiesAngle(GenericBody, GenericBody): double
 * 		-getAngledOffset(GenericBody, GenericBody): double
 * 		-getAoA(GenericBody, GenericBody): double
 * 		-offsetNonstatic(GenericBody, GenericBody): void
 * 		-calculateStaticAngle(GenericBody, GenericBody): double
 * 		-offsetBody(GenericBody, GenericBody): void
 * 		-getVelocity(GenericBody, GenericBody): double
 * 		-setCollisionVelocity(GenericBody, GenericBody): void
 * 		-setStaticCollisionVelocity(GenericBody, GenericBody): void
 * 		-checkCollisions(GenericBody): void
 * 		-checkStuck(GenericBody): void
 * 		-updateAll(double): void
 * 
 */

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
*        3 = Wireframe Rendering
*        4 = Debug Mode
*/

import java.awt.Color;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class ManagerClass {
    private static ManagerClass manager;
    private static GuiClass gui;
    private FileAccessor files;
    private ArrayList<GenericBody> bodies = new ArrayList<GenericBody>();
    private ArrayList<Object> constants;
    private ArrayList<Object> settings;
    private Timer timer;
    private boolean running = false;
    private Instant lastUpdate;

    public static void main(String[] args) {
        manager = new ManagerClass();
        
        manager.readData();

        gui = new GuiClass(manager, manager.settings);

        gui.createMenu();
    }

    public void readData() {
        files = new FileAccessor();

        constants = files.getConstants();
        settings = files.getSettings();
    }

    public void startRender() {
        timer = new Timer();

        lastUpdate = Instant.now();

        System.out.println("Simulation Started...");

        timer.scheduleAtFixedRate(new TimerTask(){
            public void run() {
                //! Get elasped time since last frame and reset timer.

                if (gui.getFrame() == null) {
                    return;
                }

                long elapsed = Duration.between(lastUpdate, Instant.now()).toMillis();
                lastUpdate = Instant.now();

                if (elapsed <= 0) {
                    elapsed = 1;
                }

                updateAll(elapsed); //? Update all bodies in the simulation.
                gui.render(bodies, (int) (1000/elapsed)); //? Render the simulation frame.
            }
        }, 0, 1000/120);

        running = true;
    }

    public void stopRender() {
        timer.cancel();
        running = false;
        System.out.println("Simulation Stopped.");
    }

    private int generateNewID() {
        int id = 0;
        boolean isUnique = false;

        while (!isUnique) {
            isUnique = true;
            for (GenericBody body : bodies) {
                if (body.getID() == id) {
                    isUnique = false;
                    id++;
                }
            }
        }

        return id;
    }

    public void createBody(int type, double mass, int size, double elasticity, int[] position, boolean canCollide, boolean isStatic) {
        if (type == 0) {
            bodies.add(new RectBody(mass, size, elasticity, position, canCollide, isStatic, Color.RED, generateNewID()));
        } else if (type == 1) {
            bodies.add(new SphereBody(mass, size, elasticity, position, canCollide, isStatic, Color.GREEN, generateNewID()));
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

    public void onKeyPress(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_SPACE: //! Spacebar
                //? Pause/Unpause the simulation.
                if (running) {
                    stopRender();
                } else {
                    startRender();
                }
                break;
            case KeyEvent.VK_ESCAPE: //! Escape
                //? Pause the simulation and open pause menu.
                gui.openPauseMenu();
        }
    }

    public void saveToFile(String filename, boolean overwrite) {
        files.saveToFile(filename, bodies, gui, overwrite);
    }

    public void loadFromFile(String filename) {
        bodies = files.loadFromFile(filename, gui);
    }

    private boolean checkToApply(GenericBody body1, GenericBody body2) {
        boolean canApply = true;

        if (body1.getExcludedBodies() != null) {
            for (GenericBody body : body1.getExcludedBodies()) {
                if (body == body2) {
                    canApply = false;
                }
            }
        } else if (body1.isTouching(body2)) {
            canApply = false;
        }

        return canApply;
    }

    private void Apply2BodyGravity(GenericBody body1, GenericBody body2, double elapsedTime) { //! Apply gravity to a body with reference to another.
        if ((Boolean) settings.get(1) && checkToApply(body1, body2)) { //? If using Particle Gravity
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
            double[] newVelocity = new double[] {body.getVelocity()[0], body.getVelocity()[1] + (g)};

            //? Set the new velocity of the body
            body.setVelocity(newVelocity);
        }
    }

    private void updatePosition(GenericBody body, double elapsedTime) { //! Update the position of a body.
        int[] newPosition = new int[] {body.getPosition()[0], body.getPosition()[1]};
        int pixels = (int) constants.get(1);

        //? Calculate the new position of the body
        newPosition = new int[] {newPosition[0] + (int) ((body.getVelocity()[0] * elapsedTime)/(double) pixels), newPosition[1] + (int) ((body.getVelocity()[1] * elapsedTime)/(double) pixels)};

        //? Set the new position of the body
        body.setPreviousPos();
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

        //? Check if the body is out of bounds and that it's moving away from bounds, and if so, move it back in bounds & invert velocity.
        if (bounds[0][0] < (0+insets.left) && body.getVelocity()[0] < 0) { //? Left
            newPosition[0] = 0+insets.left+(size/2);
            body.setVelocity(new double[] {-body.getVelocity()[0]*elasticity, body.getVelocity()[1]});
        } else if (bounds[2][0] > mainPanel.getWidth()-insets.right && body.getVelocity()[0] > 0) { //? Right
            newPosition[0] = mainPanel.getWidth()-insets.right-(size/2);
            body.setVelocity(new double[] {-body.getVelocity()[0]*elasticity, body.getVelocity()[1]});
        } else if (bounds[1][1] > mainPanel.getHeight()-insets.bottom && body.getVelocity()[1] > 0) { //? Bottom
            newPosition[1] = mainPanel.getHeight()-insets.bottom-(size/2);
            body.setVelocity(new double[] {body.getVelocity()[0], -body.getVelocity()[1]*elasticity});
        } else if (bounds[3][1] < (0+insets.top) && body.getVelocity()[1] < 0) { //? Top
            newPosition[1] = 0+insets.top+(size/2);
            body.setVelocity(new double[] {body.getVelocity()[0], -body.getVelocity()[1]*elasticity});
        }

        //? Move the body to the new position
        body.move(newPosition);
    }

    // private double getBodiesAngle(GenericBody body1, GenericBody body2) {
    //     //? Get positional info
    //     int[] position = body1.getPosition();
    //     int[] position2 = body2.getPosition();

    //     //? Calculate the angle between the two bodies
    //     double angle = Math.atan2(position2[1] - position[1], position2[0] - position[0]);

    //     return angle;
    // }

    // private double getAngledOffset(GenericBody body1, GenericBody body2) { //! Get the relative angle between two bodies
    //     double velAngle = getAoA(body1, body2);
    //     double bodAngle = getBodiesAngle(body1, body2);

    //     double offset = velAngle+bodAngle;

    //     return offset;
    // }

    private double getAoA(GenericBody body1, GenericBody body2) { //! Get the angle of the collision between two bodies
        double a = body1.getSize()/2.00;
        double b = body2.getSize()/2.00;
        double c = Math.sqrt(Math.pow(body1.getPreviousPos()[0] - body2.getPosition()[0], 2) + Math.pow(body1.getPreviousPos()[1] - body2.getPosition()[1], 2));

        double angle = Math.acos((Math.pow(b, 2) + Math.pow(c, 2) - Math.pow(a, 2)) / (2 * c * b));

        if (Double.isNaN(angle)) {
            angle = Math.acos(((Math.pow(b, 2) + Math.pow(c, 2) - Math.pow(a, 2)) / (2 * c * b))-1);

            if (Double.isNaN(angle)) {
                angle = 0;
            }
        }

        return angle;
    }

    private void offsetNonstatic(GenericBody body1, GenericBody body2) { //! If both bodies are not static, do this instead
        //? Get positional info
        int[] position = body1.getPosition();
        int[] position2 = body2.getPosition();

        //? Get angle of the other body relative to the body's trajectory
        double angle = getAoA(body1, body2);
        double angle2 = Math.PI-angle;

        //System.out.println("Angle: " + angle);

        //? Offset bodies so they are not clipping
        position = new int[] {(int) ((Math.cos(angle2)*((body1.getSize()/2)+(body2.getSize()/2)))/2+position2[0]), (int) ((Math.sin(angle2)*((body1.getSize()/2)+(body2.getSize()/2)))/2+position2[1])};
        position2 = new int[] {(int) ((Math.cos(angle)*((body1.getSize()/2)+(body2.getSize()/2)))/2+position[0]), (int) ((Math.sin(angle)*((body1.getSize()/2)+(body2.getSize()/2)))/2+position[1])};
    }

    private double calculateStaticAngle(GenericBody body1, GenericBody body2) { //! Get angle of collision between a static body and a non-static body
        double initAngle = getAoA(body1, body2);
        double velAngle = Math.atan2(body1.getVelocity()[1], body1.getVelocity()[0]);
        double angle = initAngle;

        double velocityTotal = Math.sqrt(Math.pow(body1.getVelocity()[0], 2) + Math.pow(body1.getVelocity()[1], 2));
        //System.out.println("Velocity: " + velocityTotal);

        if (velocityTotal > 1) {
            angle = Math.sqrt(Math.pow(initAngle, 2) + Math.pow(velAngle, 2));
        }

        return angle;
    }

    private void offsetBody(GenericBody body1, GenericBody body2) { //! If one body is static, do this instead
        //? Get positional info
        int[] position = body2.getPosition();

        //? Get angle of the other body relative to the body's trajectory
        double angle = calculateStaticAngle(body1, body2);

        //System.out.println("Angle: " + angle);

        //? Offset bodies so they are not clipping
        position = new int[] {(int) ((Math.cos(angle)*((body1.getSize()/2)+(body2.getSize()/2)))+position[0]), (int) ((Math.sin(angle)*((body1.getSize()/2)+(body2.getSize()/2)))+position[1])};

        //? Set the new position of the body
        body1.move(position);
    }

    private double getVelocity(GenericBody body1, GenericBody body2) {
        //? Calculate the 2 velocity vectors for both bodies relative to each other
        double[] velocity1 = body1.getVelocity();
        double[] velocity2 = body2.getVelocity();

        //? Convert the velocity vectors to a singular value
        double velocity1Total = Math.sqrt(Math.pow(velocity1[0], 2) + Math.pow(velocity1[1], 2));
        double velocity2Total = Math.sqrt(Math.pow(velocity2[0], 2) + Math.pow(velocity2[1], 2));

        double upperVal1 = body1.getMass()-body2.getMass();
        double upperVal2 = 2*body2.getMass();
        double lowerVal = body1.getMass()+body2.getMass();

        double velocity = ((upperVal1/lowerVal)*velocity1Total)+((upperVal2/lowerVal)*velocity2Total);

        return velocity;
    }

    public void setCollisionVelocity(GenericBody body, GenericBody body2) {
        offsetNonstatic(body, body2);
        double angle1 = Math.PI-getAoA(body, body2);
        double angle2 = getAoA(body, body2);

        //? Calculate the 2 velocity vectors for both bodies relative to each other
        double velocity1 = getVelocity(body, body2);
        double velocity2 = getVelocity(body2, body);

        double totalElasticity = body.getElasticity()*body2.getElasticity();

        //? Calculate the new velocities
        double[] newVelocity1 = new double[] {Math.cos(angle1)*velocity1*totalElasticity, Math.sin(angle1)*velocity1*totalElasticity};
        double[] newVelocity2 = new double[] {Math.cos(angle2)*velocity2*totalElasticity, Math.sin(angle2)*velocity2*totalElasticity};

        //? Apply the new velocities
        body.setVelocity(newVelocity1);
        body2.setVelocity(newVelocity2);
    }

    public void setStaticCollisionVelocity(GenericBody body, GenericBody body2) { //! Exclude body 2 since body 2 is static
        offsetBody(body, body2);
        double angle1 = Math.PI-getAoA(body, body2);

        double totalElasticity = body.getElasticity()*body2.getElasticity();

        //? Calculate the 2 velocity vectors for both bodies relative to each other
        double velocity1 = -getVelocity(body, body2);

        //? Calculate the new velocities
        double[] newVelocity1 = new double[] {Math.cos(angle1)*velocity1*totalElasticity, Math.sin(angle1)*velocity1*totalElasticity};

        //? Apply the new velocities
        body.setVelocity(newVelocity1);
        
        body.setExcludeBody(body2);
    }

    public void checkCollisions(GenericBody body) { //! Check if a body is colliding with another.
        if (body.canCollide() && !body.isStatic()) { //! If this body cannot collide, DO NOT UPDATE COLLISIONS.
            ArrayList<GenericBody> colliding = body.getCollidingBodies(bodies); //? Get a list of all colliding bodies.

            for (GenericBody body2 : colliding) { //? Iterate through all colliding bodies
                //System.out.println("Body (" + body2.getClass().getName() + ": " + body2.getID() + ") is colliding with (" + body.getClass().getName() + ": " + body.getID() + ")!");

                if (body2.isStatic() && body.getPreviousPos() != body.getPosition()) {
                    setStaticCollisionVelocity(body, body2);
                } else if (body.getPreviousPos() != body.getPosition()) {
                    setCollisionVelocity(body, body2);
                }
            }
        }
    }

    private void checkStuck(GenericBody body) {
        if (body.getPreviousPos() == null) {
            return;
        }

        if (body.getPreviousPos()[0] == body.getPosition()[0] && body.getPreviousPos()[1] == body.getPosition()[1] && (Double.isNaN(body.getVelocity()[0]) || Double.isNaN(body.getVelocity()[1]))) {
            System.out.println("Body " + body.getClass().getName() + " is stuck! Resetting Velocity...");
            body.setVelocity(new double[] {0, 0});
        }
    }

    private void updateAll(double elapsedTime) {
        for (GenericBody body : bodies) {
            //? If the body is static, skip it.
            if (!body.isStatic()) {
                checkStuck(body); //? Check if a body is stuck.

                ApplyGravity(body, elapsedTime); //? Apply gravity to a body.

                //? Check gravitational effects of all bodies in existence.
                for (GenericBody body2 : bodies) {
                    if (body != body2) {
                        Apply2BodyGravity(body, body2, elapsedTime); //? Apply gravity to a body with reference to another.
                    }
                }

                if (body.isStatic()) {
                    body.setVelocity(new double[] {0, 0});
                }

                updatePosition(body, elapsedTime); //? Update the position of a body.

                checkCollisions(body); //? Check if a body is colliding with another.
            } else {
                body.setPreviousPos();
            }

            checkBounds(body); //? Confirm body is in bounds and fix if not.
        }
    }
}
