public class Line{

    private Function function;
    public Function getFunction() {return function;}

    public Line(Function f, double x1, double x2){
        setxy(f, new Point(x1, f.f(x1)), new Point(x2, f.f(x2)));

    }

//Create a line based on inputs
    public Line(Point start, Point end){
        if (start.x == end.x) start.x -= 0.0001;//since we consusize on an upwards angle
        if (start.y == end.y) start.y -= 0.0001;
        double m = (end.y-start.y)/(end.x-start.x);
        Function f = new Function(m+"*x");
        double c = start.y-f.f(start.x);
        f = new Function(f.getFunction() +""+ (c<0?"":"+") +""+ c);
        setxy(f, start, end);
    }

    private void setxy(Function f, Point start, Point end){
        this.function = f;
        this.start = start;
        this.end = end;
        min = new Point(Math.min(start.x, end.x), Math.min(start.y, end.y));
        max = new Point(Math.max(start.x, end.x), Math.max(start.y, end.y));
    }

    public Point start;
    public Point end;
//min and max values
    public Point min;
    public Point max;

    public double f(double x){
        return function.f(x);
    }
    public double f(){
        return function.f();
    }


    public boolean intersects(Line line){
    //check if the two lines intersect on their intervals
        if (this.max.x < line.min.x) return false;
        if (line.max.x < this.min.x) return false;
        if (this.max.y < line.min.y) return false;
        if (line.max.y < this.min.y) return false;

        Point p = intersection(line);
        double minx = Math.min(this.min.x, line.min.x);
        double maxx = Math.max(this.max.x, line.max.x);
        double miny = Math.min(this.min.y, line.min.y);
        double maxy = Math.max(this.max.y, line.max.y);
        return (minx <= p.x && p.x <= maxx && miny <= p.y && p.y <= maxy);
    }

//Return a coordinate where this line and the given line intersect
    public Point intersection(Line line){
        double x = this.function.intersection(line.getFunction());
        return new Point(x, function.f(x));
    }
}
