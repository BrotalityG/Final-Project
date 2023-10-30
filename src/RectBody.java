public class RectBody extends GenericBody {
     protected double mass;
     private double size;
     private int[] position = new int[2];
     private int[] velocity = new int[2];
     protected boolean canCollide;
     protected boolean isStatic;

    @Override
    public void create(double mass, double size, int[] position, boolean canCollide, boolean isStatic) {
        if (mass < 0) throw new IllegalArgumentException("Mass cannot be negative");
        if (size <= 0) throw new IllegalArgumentException("Size cannot be negative or zero");

        this.mass = mass;
        this.size = size;
        this.position = position;
        this.canCollide = canCollide;
        this.isStatic = isStatic;
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
    public int[] getVelocity() {
        return velocity;
    }

    @Override
    public void shiftVelocity(int[] velocity) {
        this.velocity = new int[] {this.velocity[0] + velocity[0], this.velocity[1] + velocity[1]};
    }

    @Override
    public void setVelocity(int[] velocity) {
        this.velocity = velocity;
    }

    @Override
    public double getArea() {
        return Math.pow(size, 2);
    }

    @Override
    public double getSize() {
        return size;
    }
}
