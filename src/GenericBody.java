public abstract class GenericBody {
    protected double mass;
    protected boolean canCollide;
    protected boolean isStatic;

    public abstract void create(double mass, double size, int[] position, boolean canCollide, boolean isStatic);

    public abstract void move(int[] position);

    public abstract int[] getPosition();

    public abstract int[] getVelocity();

    public abstract void shiftVelocity(int[] velocity);

    public abstract void setVelocity(int[] velocity);

    public abstract double getArea();

    public abstract double getSize();
}
