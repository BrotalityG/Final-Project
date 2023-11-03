public class SphereBody extends GenericBody {
     private double mass;
     private int size;
     private int[] position = new int[2];
     private double[] velocity = new double[2];
     private double elasticity;
     private boolean canCollide;
     private boolean isStatic;

    SphereBody(double mass, int size, double elasticity, int[] position, boolean canCollide, boolean isStatic) {
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
    public boolean canCollide() {
        return canCollide;
    }

    @Override
    public boolean isStatic() {
        return isStatic;
    }
}
