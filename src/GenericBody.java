/*********************************************************
 * Filename: GenericBody
 * Author: Branden Stahl
 * Created: 10/09/23
 * Modified: 11/08/23
 * 
 * Purpose: 
 * This is the abstract class representing all bodies.
 * 
 * Attributes:
 * 		
 *  
 * Methods: 
 * 		+move(int[] position): void
 * 	    +getPosition(): int[]
 * 	    +getVelocity(): double[]
 * 	    +setVelocity(double[] velocity): void
 * 	    +getBounds(): int[][]
 * 	    +getEdgeBounds(): int[][]
 * 	    +getCollidingBodies(ArrayList<GenericBody> bodies): ArrayList<GenericBody>
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

public abstract class GenericBody {
    public abstract void move(int[] position);

    public abstract int[] getPosition();

    public abstract double[] getVelocity();

    public abstract void setVelocity(double[] velocity);

    public abstract int[][] getBounds();

    public abstract int[][] getEdgeBounds();

    public ArrayList<GenericBody> getCollidingBodies(ArrayList<GenericBody> bodies) {
        return new ArrayList<GenericBody>();
    }

    public abstract int getSize();

    public abstract double getElasticity();

    public abstract double getMass();

    public abstract Color getColor();

    public abstract boolean canCollide();

    public abstract boolean isStatic();

    public abstract void setPreviousPos();

    public abstract int[] getPreviousPos();

    public abstract void setExcludeBody(GenericBody body);

    public abstract int getID();
}
