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

    SphereBody(double mass, int size, double elasticity, int[] position, boolean canCollide, boolean isStatic, Color color) {
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
    public ArrayList<GenericBody> getCollidingBodies(ArrayList<GenericBody> bodies) { //! Collision detection. At least one of these bodies will always be a sphere. This is pain.
        ArrayList<GenericBody> collidingBodies = new ArrayList<GenericBody>();

        for (GenericBody body : bodies) { //? Iterate through all bodies in the list.
            boolean added = false;

            if (body.canCollide() && !body.equals(this)) {
                if (body instanceof RectBody) { //? If body is a rectbody, check to see if it's inside the body's bounds by comparing bounds.
                    double radius = getSize()/2.00;
                    int[] bodyPosition = getPosition();
                    double magnitude = Math.sqrt(Math.pow(position[0]-bodyPosition[0], 2) + Math.pow(position[1]-bodyPosition[1], 2));

                    if (magnitude <= radius) {
                        collidingBodies.add(body);
                        added = true;
                    }

                    if (added) { continue; } //? If the body has already been added, skip the rest of the loop.

                    int[][] edgeBounds = body.getEdgeBounds();
                    for (int[] bound : edgeBounds) {
                        magnitude = Math.sqrt(Math.pow(bound[0]-bodyPosition[0], 2) + Math.pow(bound[1]-bodyPosition[1], 2));
                        if (magnitude <= radius) {
                            collidingBodies.add(body);
                            added = true;
                            break;
                        }
                    }

                    if (added) { continue; } //? If the body has already been added, skip the rest of the loop.

                    int[][] bodyBounds = body.getBounds();
                    for (int[] bound : bodyBounds) {
                        magnitude = Math.sqrt(Math.pow(bound[0]-bodyPosition[0], 2) + Math.pow(bound[1]-bodyPosition[1], 2));
                        if (magnitude <= radius) {
                            collidingBodies.add(body);
                            added = true;
                            break;
                        }
                    }
                } else if (body instanceof SphereBody) { //? If body is a spherebody, check to see if the distance between the two bodies is less than the sum of their radii.
                    double radius = getSize()/2.00;
                    double radii = radius + body.getSize()/2.00;
                    int[] bodyPosition = body.getPosition();
                    double magnitude = Math.sqrt(Math.pow(position[0]-bodyPosition[0], 2) + Math.pow(position[1]-bodyPosition[1], 2));

                    if (magnitude <= radii) {
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
