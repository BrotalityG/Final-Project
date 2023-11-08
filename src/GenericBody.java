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
}
