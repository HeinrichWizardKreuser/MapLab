public class Angle {

    public static Function reflect = new Function("[x*cos(2*theta)+y*sin(2*theta);x*sin(2*theta)-y*cos(2*theta)]");
    public static Function rotate = new Function("[x*cos(theta)-y*sin(theta);x*sin(theta)+y*cos(theta)]");

//POINT TRANSLATION
    public static Point rotate(Point p, Point origin, double degrees){
        return translate(p, origin, degrees, rotate.replica());
    }

    public static Point reflect(Point p, Point origin, double degrees){
        return translate(p, origin, degrees, reflect.replica());
    }

    public static Point translate(Point p, Point origin, double degrees, Function symF){
        symF.addVariable("theta", degrees);
        Point toReturn = new Point(symF.getOut("x="+(p.x-origin.x)+";y="+(p.y-origin.y)));
        toReturn.setxy(toReturn.x+origin.x, toReturn.y+origin.y);
        return toReturn;
    }

//ANGLE TRANSLATION
    public static Angle rotate(Angle a, double degrees){
        return translate(a, degrees, rotate.replica());
    }

    public static Angle reflect(Angle a, double degrees){
        return translate(a, degrees, reflect.replica());
    }

    public static Angle translate(Angle a, double degrees, Function symF){
    //get a point that is angle a relative to origin
        Point p = new Point(Core.cos(a.angle), Core.sin(a.angle));
    //translate point and get new point
        p = translate(p, new Point(0, 0), degrees, symF);
    //get point angle relative to origin
        Angle toReturn = new Angle(p, new Point(0, 0));
    //return that angle
        return toReturn;
    }

    public String toString(){
        return angle +"";
    }
    public Angle replica(){
        return new Angle(this.angle);
    }

    public final double angle;//[0, 360)
//Constructor
    public Angle(double angle){
    //first regulate
        while (angle < 0) angle+= 360;
        while (360 <= angle) angle -= 360;
        this.angle = regulate(angle);
    }
//construct an angle equal to the angle of point a relative to p
    public Angle(Point a, Point p){
        double angle = 0.0;
        if (a.x == p.x){
            angle = (a.y < p.y) ? 270 : 90;
        } else if (a.y == p.y){
            angle = (a.x < p.x) ? 180 : 0;
        } else {
            angle = getAngle(a.y-p.y, a.x-p.x);
        }
        angle = regulate(angle);
        this.angle = angle;
    }

    public static double getAngle(double ydif, double xdif){
		double m = ydif/xdif;
		boolean negY = ydif < 0;
		boolean negX = xdif < 0;
		double toReturn = Core.toDegrees(Math.atan(m));
        if (!negY && !negX) return toReturn;//both pisitive = normal
        if (negY && !negX){//
            toReturn += 360;
        } else if (!negY && negX){
            toReturn += 180;
        } else if (negY && negX){
            toReturn += 180;
        }
        return regulate(toReturn);
	}

    public static double regulate(double angle){
        while (angle < 0) angle+= 360;
        while (360 <= angle) angle -= 360;
        return angle;
    }
//a < b if sin(b-a)>0
    public boolean lessThan(Angle b){
        return lessThan(b.angle);
    }
    public boolean lessThan(double b){
        return Core.sin(b-angle)>0;
    }

    public boolean greaterThan(Angle b){
        return greaterThan(b.angle);
    }
    public boolean greaterThan(double b){
        return Core.sin(b-angle)<0;
    }

    public boolean isBetween(Angle a, Angle b){
        return isBetween(a.angle, b.angle);
    }
    public boolean isBetween(double a, double b){
        return this.greaterThan(a) && this.lessThan(b);
    }

    public static void main(String[] args) {
        Point a = new Point(-1, 1);
        Point p = new Point(0, 0);
        Angle z = new Angle(a, p);
        Core.println(z.toString());
    }
//return differnece in angle between this angle and the given
//ie what would we have to add or ubtract from this angle to get angle a?
    public double angleDiff(Angle a){
        if (this.greaterThan(a)){
            return -this.minus(a);
        }
        return a.minus(this);
    }

    public boolean equals(Angle theta){
        return theta.angle == this.angle;
    }
    public boolean equals(double theta){
        return angle == theta;
    }

    public double minus(Angle theta){
        return regulate(angle-theta.angle);
    }
    public double minus(double a){
        return minus(new Angle(a));
    }

    public double plus(Angle theta){
        return regulate(angle+theta.angle);
    }

//get the difference between the angle and this one
    public double difference(Angle a){
        if (lessThan(a)) return a.minus(this);
        if (greaterThan(a)) return this.minus(a);
        return 180;
    }
    public double difference(double a){
        return difference(new Angle(a));
    }

    public boolean straightAngle(Angle a){
        return !lessThan(a) && !greaterThan(a);
    }
    public boolean straightAngle(double a){
        return straightAngle(new Angle(a));
    }
}
