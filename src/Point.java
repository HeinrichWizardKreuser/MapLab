import java.util.*;

public class Point{

	public double x;
	public double y;
	public int ix;
	public int iy;

//from [x;y] format
	public Point(String function){
		Function f = new Function(function);
        double[] arr = f.toArray(function);
		setxy(arr[0],arr[1]);
	}

//From array format {x, y}
	public Point(double[] arr){
		setxy(arr[0],arr[1]);
	}

	public Point(double x, double y){
		setxy(x,y);
	}

	public Point(double x, double y, String info){
		setxy(x,y);
		this.info = info;
	}

	public void setxy(double x, double y){
		this.x = x;
		this.y = y;
		this.ix = (int)x;
		this.iy = (int)y;
	}

	public Point plus(Point a){
		return new Point(x+a.x, y+a.y);
	}

	public Point minus(Point a){
		return new Point(x-a.x, y-a.y);
	}

	// Signed area / determinant thing
	public double cross(Point p) {
		return x*p.y-y*p.x;
	}

	public String info;//we can store any valuable information about this point here

	public String toString(){return "["+Core.round(x,3)+";"+Core.round(y,3)+"]";}
//	public String toString(){
		//String sx = x+"";
		//if (sx.length > 3 && sx.contains(".")) sx = substring(0, sx.indexOf(".")+4);
	//	return "["+x+";"+y+"]";}
	public String toStringi(){return "["+ix+";"+iy+"]";}

	public double[] toArray(){
		return new double[]{x, y};
	}

	public boolean equals(Point p) {
		return (this.x == p.x && this.y == p.y);
	}

	public boolean equals(double x, double y){
		return (this.x == x && this.y == y);
	}

	public double distanceTo(Point p){
		return Math.sqrt( (x-p.x)*(x-p.x) + (y-p.y)*(y-p.y) );
	}

	public double dist(Point p){
		return distanceTo(p);
	}

//Return a coordinate on the line closest to Point p
    public Point closestPointOnLine(Line line){
    //create a function to solve: f(x) = this.x-x-this.y
		double x = line.getFunction().intersection(new Function("-x+"+(this.x-this.y)));
		double y = line.getFunction().f(x);
		return new Point(x, y);
    }
/*
//Relevant
    public HashMap<String, ArrayList<Point>> relevant;
    public ArrayList<Point> get(String s){ return relevant.get(s); }
    public void addNewList(String s){
        if (!relevant.containsKey(s))
            relevant.put(s, new ArrayList<>(0));
    }
*/
}
