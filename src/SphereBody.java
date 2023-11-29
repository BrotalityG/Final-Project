/*********************************************************
 * Filename: SphereBody
 * Author: Branden Stahl
 * Created: 10/09/23
 * Modified: 11/27/23
 * 
 * Purpose: 
 * Each instance of this class represents a circular body.
 * 
 * Attributes:
 * 		-mass: double
 * 		-size: int
 * 		-position: int[]
 * 		-velocity: double[]
 * 		-elasticity: double
 * 		-color: Color
 * 		-canCollide: boolean
 * 		-isStatic: boolean
 * 		-prevPos: int[]
 *  
 * Methods: 
 * 		+SphereBody<double, int, double, int[], boolean, boolean, Color>
 * 		+move(int[]): void
 * 	    +getPosition(): int[]
 * 	    +getVelocity(): double[]
 * 	    +setVelocity(double[]): void
 * 	    +getBounds(): int[][]
 * 	    +getEdgeBounds(): int[][]
 * 	    -isRectCollide(RectBody): boolean
 * 	    -isSphereCollide(SphereBody): boolean
 * 	    +isTouching(GenericBody): boolean
 * 	    +getCollidingBodies(ArrayList<GenericBody>): ArrayList<GenericBody>
 * 	    +getSize(): int
 * 	    +getElasticity(): double
 * 	    +getMass(): double
 * 	    +getColor(): Color
 * 	    +canCollide(): boolean
 * 	    +isStatic(): boolean
 * 	    +setPreviousPos(): void
 * 	    +getPreviousPos(): int[]
 * 	    +setExcludeBody(GenericBody): void
 * 	    +getExcludedBodies(): ArrayList<GenericBody>
 * 	    +getID(): int
 *
 */

import java.awt.Color;
import java.util.ArrayList;

public class SphereBody extends GenericBody {
     private double mass;
     private int size;
     private int[] position = new int[2];
     private double[] velocity = new double[2];
     private double elasticity;
     private Color color;
     private boolean canCollide;
     private boolean isStatic;
     private int[] prevPos;
     private ArrayList<GenericBody> excludeBodies = new ArrayList<GenericBody>();
     private int ID;

    public SphereBody(double mass, int size, double elasticity, int[] position, boolean canCollide, boolean isStatic, Color color, int ID) {
        if (mass < 0) throw new IllegalArgumentException("Mass cannot be negative");
        if (size <= 0) throw new IllegalArgumentException("Size cannot be negative or zero");
        if (elasticity < 0 || elasticity > 1) throw new IllegalArgumentException("Elasticity must be between 0 and 1");

        this.mass = mass;
        this.size = size;
        this.position = position;
        this.canCollide = canCollide;
        this.isStatic = isStatic;
        this.velocity = new double[] {0, 0};
        this.elasticity = elasticity;
        this.color = color;
        this.ID = ID;
    }

    @Override
    public void move(int[] position) {
        this.position = position;
    }

    @Override
    public int[] getPosition() {
        return position;
    }

    @Override
    public double[] getVelocity() {
        return velocity;
    }

    @Override
    public void setVelocity(double[] velocity) {
        this.velocity = velocity;
    }

    @Override
    public int[][] getBounds() {
        int radius = (int) size/2;
        double cos = Math.cos(Math.PI/4);
        double sin = Math.sin(Math.PI/4);

        return new int[][] {new int[] {(int) (position[0]-(cos*radius)), (int) (position[1]-(sin*radius))}, new int[] {(int) (position[0]-(cos*radius)), (int) (position[1]+(sin*radius))}, new int[] {(int) (position[0]+(cos*radius)), (int) (position[1]+(sin*radius))}, new int[] {(int) (position[0]+(cos*radius)), (int) (position[1]-(sin*radius))}};
    }

    @Override
    public int[][] getEdgeBounds() {
        int radius = (int) size/2;
        return new int[][] {new int[] {position[0]-radius, position[1]}, new int[] {position[0], position[1]+radius}, new int[] {position[0]+radius, position[1]}, new int[] {position[0], position[1]-radius}};
    }

    private boolean isRectCollide(RectBody body) {
        boolean isCollide = false;
        int[] bodyPosition = body.getPosition();
        double magnitude = Math.sqrt(Math.pow(position[0]-bodyPosition[0], 2) + Math.pow(position[1]-bodyPosition[1], 2));

        if (magnitude <= getSize()/2.00) {
            isCollide = true;
        }

        for (int[] bound : body.getEdgeBounds()) {
            magnitude = Math.sqrt(Math.pow(bound[0]-bodyPosition[0], 2) + Math.pow(bound[1]-bodyPosition[1], 2));
            if (magnitude <= getSize()/2.00) {
                isCollide = true;
            }
        }

        for (int[] bound : body.getBounds()) {
            magnitude = Math.sqrt(Math.pow(bound[0]-bodyPosition[0], 2) + Math.pow(bound[1]-bodyPosition[1], 2));
            if (magnitude <= getSize()/2.00) {
                isCollide = true;
            }
        }

        return isCollide;
    }

    private boolean isSphereCollide(SphereBody body) {
        boolean isCollide = false;
        int[] bodyPosition = body.getPosition();
        double magnitude = Math.sqrt(Math.pow(position[0]-bodyPosition[0], 2) + Math.pow(position[1]-bodyPosition[1], 2));

        if (magnitude <= body.getSize()/2.00) {
            isCollide = true;
        }

        for (int[] bound : getEdgeBounds()) {
            magnitude = Math.sqrt(Math.pow(bound[0]-bodyPosition[0], 2) + Math.pow(bound[1]-bodyPosition[1], 2));
            if (magnitude <= body.getSize()/2.00) {
                isCollide = true;
            }
        }

        for (int[] bound : getBounds()) {
            magnitude = Math.sqrt(Math.pow(bound[0]-bodyPosition[0], 2) + Math.pow(bound[1]-bodyPosition[1], 2));
            if (magnitude <= body.getSize()/2.00) {
                isCollide = true;
            }
        }

        return isCollide;
    }

    @Override
    public boolean isTouching(GenericBody body) {
        boolean isTouching = false;

        if (body instanceof RectBody) {
            isTouching = isRectCollide((RectBody) body);
        } else if (body instanceof SphereBody) {
            isTouching = isSphereCollide((SphereBody) body);
        }

        return isTouching;
    }

    @Override
    public ArrayList<GenericBody> getCollidingBodies(ArrayList<GenericBody> bodies) { //! Collision detection. At least one of these bodies will always be a sphere. This is pain.
        ArrayList<GenericBody> collidingBodies = new ArrayList<GenericBody>();

        for (GenericBody body : bodies) { //? Iterate through all bodies in the list.
            if (body.canCollide() && !body.equals(this)) {
                boolean match = false;
                for (int i = 0; i < excludeBodies.size(); i++) {
                    if (body.equals(excludeBodies.get(i))) {
                        match = true;
                    } if (!excludeBodies.get(i).isTouching(this)) {
                        excludeBodies.remove(i);
                    }
                }

                if (match) {
                    continue;
                }

                if (body.isTouching(this)) {
                    collidingBodies.add(body);
                }
            }
        }

        return collidingBodies;
    }

    @Override
    public void setPreviousPos() {
        prevPos = position;
    }

    @Override
    public int[] getPreviousPos() {
        return prevPos;
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public double getElasticity() {
        return elasticity;
    }

    @Override
    public double getMass() {
        return mass;
    }

    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public boolean canCollide() {
        return canCollide;
    }

    @Override
    public boolean isStatic() {
        return isStatic;
    }

    @Override
    public void setExcludeBody(GenericBody body) {
        excludeBodies.add(body);
    }

    @Override
    public ArrayList<GenericBody> getExcludedBodies() {
        return excludeBodies;
    }

    @Override
    public int getID() {
        return ID;
    }
}
