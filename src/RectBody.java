/*********************************************************
 * Filename: RectBody
 * Author: Branden Stahl
 * Created: 10/09/23
 * Modified: 11/08/23
 * 
 * Purpose: 
 * Each instance of this class represents a rectangular body.
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
 *      +RectBody<double, int, double, int[], boolean, boolean, Color>
 * 		+move(int[]): void
 * 	    +getPosition(): int[]
 * 	    +getVelocity(): double[]
 * 	    +setVelocity(double[]): void
 * 	    +getBounds(): int[][]
 * 	    +getEdgeBounds(): int[][]
 * 	    +getCollidingBodies(ArrayList<GenericBody>): ArrayList<GenericBody>
 * 	    +getSize(): int
 * 	    +getElasticity(): double
 * 	    +getMass(): double
 * 	    +getColor(): Color
 * 	    +canCollide(): boolean
 * 	    +isStatic(): boolean
 * 	    +setPreviousPos(): void
 * 	    +getPreviousPos(): int[]
 *
 */

import java.awt.Color;
import java.util.ArrayList;

public class RectBody extends GenericBody {
     private double mass;
     private int size;
     private int[] position = new int[2];
     private double[] velocity = new double[2];
     private double elasticity;
     private Color color;
     private boolean canCollide;
     private boolean isStatic;
     private int[] prevPos;

    RectBody(double mass, int size, double elasticity, int[] position, boolean canCollide, boolean isStatic, Color color) {
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
        return new int[][] {new int[] {position[0]-radius, position[1]-radius}, new int[] {position[0]+radius, position[1]-radius}, new int[] {position[0]+radius, position[1]+radius}, new int[] {position[0]-radius, position[1]+radius}};
    }

    @Override
    public int[][] getEdgeBounds() {
        int radius = (int) size/2;
        return new int[][] {new int[] {position[0]-radius, position[1]}, new int[] {position[0], position[1]+radius}, new int[] {position[0]+radius, position[1]}, new int[] {position[0], position[1]-radius}};
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
    public ArrayList<GenericBody> getCollidingBodies(ArrayList<GenericBody> bodies) { //! Collision detection. Rects are easy, spheres not so much.
        int[][] bounds = getBounds();
        ArrayList<GenericBody> collidingBodies = new ArrayList<GenericBody>();

        for (GenericBody body : bodies) { //? Iterate through all bodies in the list.
            if (body.canCollide() && !body.equals(this)) {
                if (body instanceof RectBody) { //? If body is a rectbody, check to see if it's inside the body's bounds by comparing bounds.
                    int[][] bodyBounds = body.getBounds();
                    if (bounds[0][0] <= bodyBounds[2][0] && bounds[2][0] >= bodyBounds[0][0] && bounds[0][1] <= bodyBounds[2][1] && bounds[2][1] >= bodyBounds[0][1]) {
                        collidingBodies.add(body);
                    }
                } else if (body instanceof SphereBody) { //? If body is a spherebody, check to see if it's inside the body's bounds.
                    if (isSphereCollide((SphereBody) body)) {
                        collidingBodies.add(body);
                    }
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
}
