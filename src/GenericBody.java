public abstract class GenericBody {
    public abstract void move(int[] position);

    public abstract int[] getPosition();

    public abstract double[] getVelocity();

    public abstract void setVelocity(double[] velocity);

    public abstract int[][] getBounds();

    public abstract int[][] getEdgeBounds();

    public abstract int getSize();

    public abstract double getElasticity();

    public abstract double getMass();

    public abstract boolean canCollide();

    public abstract boolean isStatic();
}
