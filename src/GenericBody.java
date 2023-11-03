public abstract class GenericBody {
    protected double mass;
    protected boolean canCollide;
    protected boolean isStatic;

    public abstract void move(int[] position);

    public abstract int[] getPosition();

    public abstract int[] getVelocity();

    public abstract void shiftVelocity(int[] velocity);

    public abstract void setVelocity(int[] velocity);

    public abstract int[][] getBounds();

    public abstract int getSize();
}
