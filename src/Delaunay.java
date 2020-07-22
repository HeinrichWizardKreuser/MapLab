//http://www.geom.uiuc.edu/~samuelp/del_project.html
//import java.util.ArrayList;
//import java.util.HashMap;
import java.util.*;

//for library & sorting
import java.util.HashMap;
import java.util.ArrayList;
//for sorting
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import static java.util.stream.Collectors.*;
import static java.util.Map.Entry.*;

public class Delaunay {

    public static void main(String[] args) {
        //Core.off();
        StdDraw.setCanvasSize(900, 900);
        StdDraw.setXscale(0, 12);
        StdDraw.setYscale(0, 12);

        polys = new ArrayList<>(0);
        lines = new ArrayList<>(0);
    //A
        lines.add(new Point[]{
            new Point(1, 4),
            new Point(3, 6),
            new Point(3, 8),
            new Point(1, 10),

            //new Point(3.1, 8),
            //new Point(3.1, 6)
        });
    //B
        lines.add(new Point[]{
            new Point(3, 1),
            new Point(4, 4),
            new Point(6, 5),

            //new Point(4, 4.1)
        });
    //C
        lines.add(new Point[]{
            new Point(6, 7),
            new Point(5, 9),
            new Point(6, 10),
            new Point(9, 10),
            new Point(10, 8),
            new Point(11, 8),

            //new Point(10, 8.1),
            //new Point(9, 10.1),
            //new Point(6, 10.1),
            //new Point(4.9, 9),
        });
    //D
        lines.add(new Point[]{
            new Point(9, 3),
            new Point(9, 5),
            new Point(8, 8),

            //new Point(8.9, 5)
        });
    //E
        lines.add(new Point[]{
            new Point(7, 1),
            new Point(10, 3),
            new Point(11, 6),

            //new Point(10.1, 3)
        });

    //Plane of Existence
        Point[] planeOfExistence = new Point[]{
            new Point(0.1, 0),
                new Point(3, 0),
                new Point(6, 0),
                new Point(9, 0),
            new Point(12, 0),
                new Point(12, 3),
                new Point(12, 6),
                new Point(12, 9),
            new Point(12, 12),
                new Point(9, 12),
                new Point(6, 12),
                new Point(3, 12),
            new Point(0.1, 12),
                new Point(0.1, 9),
                new Point(0.1, 6),
                new Point(0.1, 3),
        };
        polys.add(planeOfExistence);


        //polys = densify(polys, 1);


        ArrayList<Point> all = getAll(polys);
        all.addAll(getAll(lines));

/*
ArrayList<Point> all = new ArrayList<>(0);
        all.add(new Point(0.1, 0.1));
        all.add(new Point(0.1, 4));
        all.add(new Point(1, 2));
        all.add(new Point(2, 4));
        all.add(new Point(3, 3));
        all.add(new Point(3, 0.1));
        all.add(new Point(4, 5));
        all.add(new Point(5, 0.1));
        all.add(new Point(5, 3));
        all.add(new Point(6, 5));
        all.add(new Point(7, 3));
        all.add(new Point(8, 1));
    */

        HashMap<Point, ArrayList<Point>> connectAll = connectAll(all);
        StdDraw.clear();
        draw(connectAll);
        Core.log("we're done now");
/*
        Core.freeze("Are you ready for it?");
        Core.log("*Base* duffff dufffff dufffffff");
        for (Point[] poly : polys) {
            for (int i = 0; i < poly.length; i++) {
                int j = (i == poly.length-1) ? 0 : i+1;
                Point s = poly[i];//start
                Point e = poly[j];//end
                connect(s, e, connectAll);
            }
        }

        for (Point[] line : lines) {
            for (int i = 0; i < line.length-1; i++) {
                Point s = poly[i];//start
                Point e = poly[i+1];//end
                if (connectAll.get(s).contains(e)) {
                    connect(s, e, connectAll);
                }
            }
        }

        StdDraw.clear();
        draw(connectAll);
        Core.log("Okay, now we're done");
*/
    }

    /**
     * Inserts the least amount of points neccesary between s and e in order to
     * connect them
     *
     * @param s is the starting point
     * @param e is the end point
     * @param connectAll is the hashmap hosting all of the points mapped to a list
     * of all points that they are connected to
     */
    /*private static void connect(Point s, Point e, HashMap<Point, ArrayList<Point>> connectAll) {
        // find min & max
        while (!connectAll.get(s).contains(e)) {
            Point min = null;
            Angle min_s = null;
            Point max = null;
            Angle max_s = null;
            for (Point p : connectAll.get(s)) {
                Angle p_s = new Angle(p, s);
                if (min_s == null || (p_s).lessThan(min_s)) {
                    min_s = p_s;
                }
                if (max_s == null || (max_s).lessThan(p_s)) {
                    max_s = p_s;
                }
            }
        }
        assert connectAll.get(min).contains(max);
        // get circumcentre c of min-s-max
        Point c = getCc(min, s, max);
        //create point p to insert
        Angle c_s = new Angle(c, s);
        Angle e_s = new Angle(e, s);
        double diff = c_s.lessThan(e_s) ? e_s.minus(c_s) : c_s.minus(e_s);
        double d = 0.99*2*c.dist(s)*Core.cos(diff);
        if (d > c.dist(e)) d = c.dist(e)/2d;
        Point p = new Point(d*Core.cos(e_s.angle), d*Core.sin(e_s.angle));
        // insert the point
        insert(p, connectAll);
        assert connectAll.get(s).contains(p);
        // set the starting node as p
        s = p;
    }*/

    /**
     * Inserts the given point into the delaunay triangulation and then updates
     * any triangulations neccesary
     *
     * @param toInsert is the point to insert into the triangulation connectAll
     * @param connectAll is the hashmap hosting all of the points mapped to a list
     * of all points that they are connected to
     */
    /*private static void insert(Point toInsert, HashMap<Point, ArrayList<Point>> connectAll) {
        connectAll.put(toInsert, new ArrayList<Point>(0));
        // determine triangle
        Point[] triangle = getTriangleContaining(toInsert, connectAll);
        // setup connectections from toinsert to all points
        for (Point corner : triangle) {
            connectAll.get(toInsert).add(corner);
            connectAll.get(corner).add(toInsert);
        }
        // get all angles including toInsert
        ArrayList<Point[]> corners = new ArrayList<>(0);
        corners.add(new Point[]{triangle[0], toInsert, triangle[1]});
        corners.add(new Point[]{triangle[1], toInsert, triangle[2]});
        corners.add(new Point[]{triangle[2], toInsert, triangle[0]});
        // update connections as neccesary
        while (!corners.isEmpty()) {
            // create list of next corners we may need to iterate over
            ArrayList<Point[]> nextCorners = new ArrayList<>(0);
            // iterate through all corners
            cornerLoop:
            for (Point[] corner : corners) {
                Point a = corner[0];
                Point b = corner[2];
                // get point c
                Point c = null;
                for (Point p : connectAll.get(a)) {
                    if (connectAll.get(p).contains(a) && p != toInsert) {
                        Angle a_toInsert = new Angle(a, toInsert);
                        Angle b_toInsert = new Angle(b, toInsert);
                        Angle min = (a_toInsert.lessThan(b_toInsert)) ? a_toInsert : b_toInsert;
                        Angle max = (min == a_toInsert) ? b_toInsert : a_toInsert;
                        Anlge p_toInsert = new Angle(p, toInsert);
                        if (min.lessThan(toInsert) && toInsert.lessThan(max)) {
                            if (c != null) c = Math.min(c.dist(toInsert), p.dist(toInsert));
                            else c = p;
                        }
                    }
                }
                if (c == null) continue cornerLoop;
                Point cc = getCc(a, toInsert, b);
                // if a-toInsert-b circle contains c
                if (cc.dist(c) < cc.dist(toInsert)) {
                    // sever connection a-b
                    connectAll.get(a).remove(b);
                    connectAll.get(b).remove(a);
                    // create connection toInsert-c
                    connectAll.get(c).add(toInsert);
                    connectAll.get(toInsert).add(c);
                    // add new corners created to next list
                    nextCorners.add(new Point[]{a, toInsert, c});
                    nextCorners.add(new Point[]{c, toInsert, b});
                }
            }
            // set corners list equal to next
            corners = nextCorners;
        }
    }*/

    /**
     * Returns a length3 Point array representing the triangle which contains the
     * given point
     *
     * @param point is the point we are searching the host triangle for
     * @param connectAll is the hashmap hosting all of the points mapped to a list
     * of all points that they are connected to
     */
    /*private static Point[] getTrianlgeContaining(Point point, HashMap<Point, ArrayList<Point>> connectAll) {
        // get sorted list of all points closest to point
        HashMap<Point, Double> sortMe = new HashMap<>();
        for (Point p : connectAll.keySet()) {
            sortMe.put(p, p.dist(point));
        }
        ArrayList<Point> sorted = sort(sortMe, "min");
        if (sorted.contains(point)) sorted.remove(point);
        // iterate over all collection of 3 points in order of likely connected
        maxLoop:
        for (int max = 2; max < closest.size(); max++) {
            Point c = closest.get(max);
            minLoop:
            for (int min = 0; min < max-1; min++) {
                Point a = closest.get(min);
                if (!connectAll.get(c).contains(a)) continue minLoop;
                jLoop:
                for (int j = min+1; j < max; j++) {
                    Point b = closest.get(j);
                    if (!connectAll.get(b).contains(a) || !connectAll.get(b).contains(c)) continue jLoop;
                    int left = 0;
                    int right = 0;
                    if ((new Angle(a, b)).lessThan(new Angle(p, b))) left++;
                    else right++;
                    if ((new Angle(b, c)).lessThan(new Angle(p, c))) left++;
                    else right++;
                    // if left was incremented only once and those right as well, then not in this triangle
                    if (left == 1) continue jLoop;
                    if ((new Angle(c, a)).lessThan(new Angle(p, a))) left++;
                    else right++;
                    if (left == 3 || right == 3) {
                        return new Point[]{ a, b, c};
                    }
                }
            }
        }
        throw new Exception("Could not find triangle containing " +point.toString());
        return null;
    }*/

    private static ArrayList<Point[]> polys;
    private static ArrayList<Point[]> lines;
/*
//check if there is an edge containing these guys
    private static boolean adj(Point a, Point b) {
        for (Point[] poly : polys) {
            for (int i = 0; i < poly.length; i++) {
                Point p = poly[i];
                if (p == a) {
                //check left and right
                    return (poly[(i!=0?i-1:poly.length-1)] == b || poly[(i!=poly.length-1?i+1:0)] == b);
                } else if (p == b) {
                    return (poly[(i!=0?i-1:poly.length-1)] == a || poly[(i!=poly.length-1?i+1:0)] == a);
                }
            }
        }
        return false;
    }

//return all points connected to p
    private static ArrayList<Point> adj(Point p) {
        ArrayList<Point> adj = new ArrayList<>(0);
        for (Point[] poly : polys) {
            for (int i = 0; i < poly.length; i++) {
                if (p == poly[i]) {
                //left
                    Point left = poly[(i!=0?i-1:poly.length-1)];
                    if (left != p) adj.add(left);
                //right
                    Point right = poly[(i!=0?i-1:poly.length-1)];
                    if (right != p) adj.add(right);
                //return list
                    return adj;
                }
            }
        }
        return adj;
    }

    private static Point[] findPoly(Point p) {
        for (Point[] poly : polys) {
            for (Point vertex : poly) {
                if (p == vertex) return poly;
            }
        }
        return new Point[]{p};
    }
*/
/*
//Makes all of the polygons' edges very dense
    private static ArrayList<Point[]> densify(ArrayList<Point[]> allPolys, double reqDensity){
        ArrayList<Point[]> densified = new ArrayList<>(0);
        for (Point[] poly : allPolys){
            //Core.log("original length = " +poly.length);
            ArrayList<Point> newPoly = new ArrayList<>(0);
        //now we must densify each edge
            for (int i = 0; i < poly.length-1; i++){
                //int j = (i+1 == poly.length) ? 0 : i+1;
                int j = i+1;
                Point[] e = new Point[]{poly[i], poly[j]};//e = edge
            //Measure density. density = distance between all points on edge
                double density = e[0].dist(e[1]);
                if (density > reqDensity){
                //add points to make density
                    for (double w = 0.0; w < density; w += reqDensity){
                        //Core.freeze("adding");
                        newPoly.add(new Point(
                            ((density-w)/density)*e[0].x + (w/density)*e[1].x,
                            ((density-w)/density)*e[0].y + (w/density)*e[1].y));
                    }
                }
            }
            //Core.exit("newPoly.size()="+newPoly.size());
            densified.add(new Point[newPoly.size()]);
            for (int i = 0; i < newPoly.size(); i++){
                densified.get(densified.size()-1)[i] = newPoly.get(i);
            }
            //Core.log("new length = " +poly.length);
        }
        return densified;
    }
*/

  private static ArrayList<Point> getAll(ArrayList<Point[]> allPolys){
		ArrayList<Point> all = new ArrayList<>(0);
		for (Point[] poly : allPolys){
			for (Point p : poly){
				all.add(p);
			}
		}
		return all;
	}

    private static void draw(HashMap<Point, ArrayList<Point>> connectAll){
        for (Point p : connectAll.keySet()){
            StdDraw.filledSquare(p.x, p.y, 0.1, StdDraw.BLUE);
            for (Point connected : connectAll.get(p)){
                StdDraw.line(p, connected, StdDraw.GREEN);
            }
        }
    }


    public static HashMap<Point, ArrayList<Point>> connectAll(ArrayList<Point> all) {
    //get sorted list based on x position (if same x, take y)
        Point[] sortedX = new Point[all.size()];
        for (int i = 0; i < all.size(); i++){
            sortedX[i] = all.get(i);
        }
        sortedX = mergesort(sortedX);
    //divide into different sets
        return triangulate(sortedX);
    }

/*
    Takes in a set of points. If the set's size > 3, it divides it in half and
    recursively feeeds itself both halves. If the size <= 3, it sets up connections
    between all points in the set. When it gets two halves back, it will send them
    through merge() before returning it

*/
    private static void drawSet(Point[] set){
        for (Point p : set){
            StdDraw.filledSquare(p.x, p.y, 0.1, StdDraw.BLUE);
        }
        Core.freeze();
        for (Point p : set){
            StdDraw.filledSquare(p.x, p.y, 0.2, StdDraw.WHITE);
        }
    }

    private static HashMap<Point, ArrayList<Point>> triangulate(Point[] set){
        drawSet(set);
    //divide if more than three
        if (set.length > 3){
        //divide into 2 sets
            Point[] left = new Point[set.length/2];
			Point[] right = new Point[set.length/2];
			if (set.length%2 == 0){//if even
				for (int i = 0; i < left.length; i++){
					left[i] = set[i];
					right[i] = set[left.length+i];
				}
			} else {//if odd
				right = new Point[set.length/2 +1];
				for (int i = 0; i < left.length; i++) left[i] = set[i];
				for (int i = 0; i < right.length; i++) right[i] = set[left.length+i];
			}
			return merge(triangulate(left), triangulate(right));
        }
    //setup triangulation in small scale
        HashMap<Point, ArrayList<Point>> triangulated = new HashMap<>();
    //check for colinear points if 3
        if (set.length == 3 && area(set[0], set[1], set[2]) == 0) {//if colinear
            Point ave = new Point((set[0].x+set[1].x+set[2].x)/3, (set[0].y+set[1].y+set[2].y)/3);
            Point closest = null;
            for (Point p : set) {
                triangulated.put(p, new ArrayList<Point>(0));
                if (closest == null || p.dist(ave) < closest.dist(ave)) {
                    closest = p;
                }
            }
            for (Point p : set) {
                if (p != closest) {
                    triangulated.get(closest).add(p);
                    triangulated.get(p).add(closest);
                }
            }
        } else {//regular setup if non colinear points
            for (Point p : set){
                triangulated.put(p, new ArrayList<Point>(0));
                for (Point o : set){
                    if (p != o){
                        triangulated.get(p).add(o);
                    }
                }
            }
        }


                for (Point p : triangulated.keySet()){
                    StdDraw.filledSquare(p.x, p.y, 0.1, StdDraw.BLUE);
                    for (Point connected : triangulated.get(p)){
                        StdDraw.line(p, connected, StdDraw.GREEN);
                    }
                }
                Core.freeze();
                StdDraw.clear();

        return triangulated;
    }

//evaluates the current l and r and makes sure that they are the best
    private static Point[] getLR(HashMap<Point, ArrayList<Point>> left, HashMap<Point, ArrayList<Point>> right, Point l, Point r) {

        StdDraw.line(l, r, StdDraw.WHITE);

        ArrayList<Point> usedLs = new ArrayList<>(0);
        ArrayList<Point> usedRs = new ArrayList<>(0);
    //assume l and r are lowest. Test the cases
        loop:
        while (true) {
            StdDraw.line(l, r, StdDraw.RED);
            Core.freeze("loop: " +usedLs.size()+ " " +usedRs.size());
        //2) If the current l is not visible by the current r, discard edge and look for any new one
            if (!isVisible(l, left, r)) {
            //get new l
                StdDraw.line(l, r, StdDraw.WHITE);
                usedLs.add(l);
                l = getNewLR(usedLs, left, "left");
                continue loop;
            }
        //3) If the current r is not visible by the current l, discard edge and look for any new one
            if (!isVisible(r, right, l)) {
            //get new r
                StdDraw.line(l, r, StdDraw.WHITE);
                usedRs.add(r);
                r = getNewLR(usedRs, right, "right");
                continue loop;
            }

        //4) If there is an r that has an angle less than current r (relative to l), take that and discard previous
            Angle r_l = new Angle(r, l);
            loop4:
            for (Point p : right.keySet()) {
                if (!usedRs.contains(p)) {
                //check if this point has less degrees while being visible.
                    Angle p_l = new Angle(p, l);
                    if (p_l.lessThan(r_l) && isVisible(p, right, l)) {
                    //however if this new one is basically the same angle as previous
                        if (r_l.minus(p_l) < 0.25) {
                        //but the distance to the new one is further, rather skip
                            if (r.dist(l) < p.dist(l)) continue loop4;
                        }
                        //then we will only take it if it is closer
                        usedRs.add(r);
                            StdDraw.line(l, r, StdDraw.WHITE);
                        r = p;
                        continue loop;
                    }
                }
            }
        //5) If there is an l that has an angle more than current l (relative to r), take that and discard previous
            Angle l_r = new Angle(l, r);
            loop5:
            for (Point p : left.keySet()) {
                if (!usedLs.contains(p)) {
                //check if this point has less degrees while being visible.
                    Angle p_r = new Angle(p, r);
                    if (l_r.lessThan(p_r) && isVisible(p, left, r)) {
                    //however if this new one is basically the same angle as previous
                        if (p_r.minus(l_r) < 0.25) {
                        //but the distance to the new one is further, rather skip
                            if (l.dist(r) < p.dist(r)) continue loop5;
                        }
                        usedLs.add(l);
                            StdDraw.line(l, r, StdDraw.WHITE);
                        l = p;
                        continue loop;
                    }
                }
            }
            return new Point[]{l, r};
        }
    }

    private static boolean isVisible(Point corner, Point[] shape, Point eye) {
        Angle eye_corner = new Angle(eye, corner);
    //determine min and max
        Angle min = null;
        Angle max = null;

        HashMap<Point, Double> sortMe = new HashMap<>();
        for (Point p : shape) {
            if (p != corner) sortMe.put(p, corner.dist(p));
        }
        sortMe.put(eye, corner.dist(eye));
        ArrayList<Point> sorted = sort(sortMe, "min");

        for (Point p : sorted) {
            if (p == eye) {
                if (min == null || max == null) {
                    return true;
                } else {
                    return !(min.lessThan(eye_corner) && eye_corner.lessThan(max));
                }
            }
            Angle p_corner = new Angle(p, corner);
            if (min == null || (p_corner).lessThan(min)) min = p_corner;
            if (max == null || (max).lessThan(p_corner)) max = p_corner;
        }

        return !(min.lessThan(eye_corner) && eye_corner.lessThan(max));
    }
//check if the point p existing as a corner on the shape is visible by the eye
    private static boolean isVisible(Point corner, HashMap<Point, ArrayList<Point>> shape, Point eye) {
        Point[] arr = new Point[shape.size()];
        int i = 0;
        for (Point p : shape.keySet()) {
            arr[i] = p;
            i++;
        }
        return isVisible(corner, arr, eye);
/*
        for (Point p : shape.keySet()) {
            if (p != corner) {
                Angle p_corner = new Angle(p, corner);
                if (min == null || (p_corner).lessThan(min)) min = p_corner;
                if (max == null || (max).lessThan(p_corner)) max = p_corner;
            }
        }
        return !(min.lessThan(eye_corner) && eye_corner.lessThan(max));
*/
    }

//get next lowest
    private static Point getNewLR(ArrayList<Point> used, HashMap<Point, ArrayList<Point>> list, String lr) {
        Point newP = null;
        for (Point p : list.keySet()) {
            if (!used.contains(p))
                if (newP == null || p.y < newP.y) {
                    newP = p;
                } else if (p.y == newP.y && ((lr.equals("left") && newP.x < p.x) || (lr.equals("right") && p.x < newP.x))) {
                    newP = p;
                }
        }
        return newP;
    }




    private static class Angle {
    //construct an angle equal to the angle of point a relative to p
        public Angle(Point a, Point p){
            double angle = 0.0;
            if (a.x == p.x) angle = (a.y < p.y) ? 270 : 90;
            else if (a.y == p.y) angle = (a.x < p.x) ? 180 : 0;
            else angle = getAngle(a.y-p.y, a.x-p.x);
            angle = regulate(angle);
            this.angle = angle;
        }
        public final double angle;
        private double getAngle(double ydif, double xdif){
    		double m = ydif/xdif;
    		boolean negY = ydif < 0;
    		boolean negX = xdif < 0;
    		double toReturn = Math.atan(m)*(180/Math.PI);
            if (!negY && !negX) return toReturn;//both pisitive = normal
            if (negY && !negX) toReturn += 360;
            else if (!negY && negX) toReturn += 180;
            else if (negY && negX) toReturn += 180;
            return regulate(toReturn);
    	}
        private double regulate(double angle){
            while (angle < 0) angle+= 360;
            while (360 <= angle) angle -= 360;
            return angle;
        }
        public double minus(Angle theta){
            return regulate(angle-theta.angle);
        }
        public boolean lessThan(Angle b){
            return lessThan(b.angle);
        }
        public boolean lessThan(double b){
            return Math.sin((b-angle)*(Math.PI/180)) > 0;
        }
    }


/*
    Takes in two sets that are next to each other (left and right) and merges them. Steps:
    1) Create an edge at the bottom using the lowest(y) of the left and right subsets' points
    2) For each side repsectively, set up a list of candidates and sort the list based on
        smallest angle difference with the bottom edge
    3) Each side selects a candidate from the sorted list and determines which
        criterions the candidate meets:
            C1: Criterion1) Angle less than 180
            C2: Criterion2) Circumcircle with that candidate via triangle with left,
            right and candidate points does not contain next candidate on that side

        Process followed based on critera met:
        if (C1 && C2){
            Set this candidate as the Final candidate for this side
        } else if (!C1){
            Then no candidates will be chosen from this side (break candidate search loop)
        } else if (C1 && !C2){
            The line drawn from the endpoint (on the side of the candidate) to
            the candidate is deleted
        }
        Unless the candidate search loop is broken, this process is repeated until
        a final candidate for this side is chosen, or all candidates have been exhuasted
        (In which case, there is no final candidate)

        After both sides have confirmed their final candidates (if any), the
        following process is followed:

        - If neither side has submitted a candidate, the merge is complete
        - If only one candidate is submited, we setup a connection between the opposite
        side's bottom edge endpoint and the final candidate. the new LR lines endpoint
        on the candidate's side becomes the candidate
        - If both sides have submitted a candidate, the previous if will be followed but
        with the final candidate being the point not contained by the circumcircle drawn
        via the bottom Edge's endpoints and the opposite side's final candidate
*/
    private static HashMap<Point, ArrayList<Point>> merge(HashMap<Point, ArrayList<Point>> left, HashMap<Point, ArrayList<Point>> right) {
/*
//ensure that pre-existing connections are there if any
        for (Point l : left.keySet()) {
        //check for all points in adj that are in left
            for (Point a : adj(l)) { //get list of adj points to l
                if (!left.get(l).contains(a) && left.containsKey(a)) {
                //if we don't have this adj point in our connected list, but it does exist in the realm, setup connection
                    left.get(l).add(a);
                    if (!left.get(a).contains(l)) left.get(a).add(l);
                }
            }
        }

        for (Point r : right.keySet()) {
        //check for all points in adj that are in left
            for (Point a : adj(r)) { //get list of adj points to l
                if (!right.get(r).contains(a) && right.containsKey(a)) {
                //if we don't have this adj point in our connected list, but it does exist in the realm, setup connection
                    right.get(r).add(a);
                    if (!right.get(a).contains(r)) right.get(a).add(r);
                }
            }
        }
*/

    //create an edge at the bottom using the bottom of the left and right subsets
        HashMap<Point, ArrayList<Point>> merged = new HashMap<>();
    //find bottom of each of left and right
        Point l = null;
        for (Point p : left.keySet()){
            merged.put(p, left.get(p));
            if (l == null || p.y < l.y || (p.y == l.y && p.x > l.x)) l = p;
        }
        Point r = null;
        for (Point p : right.keySet()){
            merged.put(p, right.get(p));
            if (r == null || p.y < r.y || (p.y == r.y && p.x < p.y)) r = p;
        }
        if (left.isEmpty() || right.isEmpty()) return merged;


//check that r and l are in sight
    //ensure that there are no points under the line between r-l from l & r list
        Angle r_l = new Angle(r, l);
        Angle l_r = new Angle(l, r);





                        for (Point p : merged.keySet()){
                            StdDraw.filledSquare(p.x, p.y, 0.1, StdDraw.BLUE);
                            for (Point connected : merged.get(p)){
                                StdDraw.line(p, connected, StdDraw.GREEN);
                            }
                        }
                        StdDraw.line(l, r, StdDraw.RED);
                        Core.freeze("Base LR set");



    //evaluate l

    //theorem: the best line is that of which the centre lies the lowest
        //theorem: a pair both higher than current pair will not be a better candidate

        Point[] getLR = getLR(left, right, l, r);
        l = getLR[0];
        r = getLR[1];
        StdDraw.line(l, r, StdDraw.PURPLE);
        Core.freeze("new LR set");
        StdDraw.line(l, r, StdDraw.RED);

    //loop
        loop: while (true) {

//candidate selection
    //LEFT
        //create sorted list of all points connected to l based on smallest angle
            HashMap<Point, Double> leftAngles = new HashMap<>();
            r_l = new Angle(r, l);//base line from l's perspective
            for (Point p : merged.get(l)){
                Angle p_l = new Angle(p, l);//angle of possible candidate relative to l
                double diff = p_l.minus(r_l);
                leftAngles.put(p, diff);
            }
            ArrayList<Point> leftCandidates = sort(leftAngles, "min");

    //RIGHT
        //create sorted list of all points connected to r based on smallest angle
            HashMap<Point, Double> rightAngles = new HashMap<>();
            l_r = new Angle(l, r);//base line from r's perspective
            for (Point p : merged.get(r)){
                Angle p_r = new Angle(p, r);//angle of possible candidate relative to r
                double diff = l_r.minus(p_r);
                rightAngles.put(p, diff);
            }
            ArrayList<Point> rightCandidates = sort(rightAngles, "min");

    //now we have a sorted list of all possible left and right candidates.
/* <180 VS <=180

    There are good reasons not to allow the ==180s to get through, it simply does'nt work.
    But if we could allow them through, we can act accordingly to special cases.
    The question is just that if we do let ==180 through, will this not cuase a loop where we always let ==180s through?
        And then furthermore, is it possible that this has happened before and is the reason for the previous loop?
        Likely not since the original did not allow ==180s loops.
    Concerning the possible infinite ==180s loop, it would likely happen since we never disregard the ==180s permanently.
        However, we need only disregard the ==180s when there is a next candidate, since that candidate can possible be <180.
        However, if there is no nextCandidate, then we let the ==180 through.
        And thus, later down in the code, when we ee that the angle is ==180, we can assume there is no nextCandidate
        which would further help us act accordingly, like finishing the merge and such.
    After testing cases, we've found a nice if-else table of sorts with special cases.
    We further suggest allowing ==180s to pass, but to fix infinite loops, we immediately disregard
    candidate with y value less than the current l.
    Q: What of those with equal of l that may have been previous l? Those are susceptible to infinite loop?
    A: Those are part of a special cases. We really actually don't know what to do

    We really actually don't know what to do

    Hazaa...it seems the problem fixed itself over night
*/

    //Now to get the next candidate
        //LEFT
            Point leftFinalCandidate = null;
            leftCandidateSearch:
            for (int i = 0; i < leftCandidates.size(); i++){
                if (leftFinalCandidate != null) break leftCandidateSearch;
                Point leftCandidate = leftCandidates.get(i);
                if (leftCandidate == r) continue leftCandidateSearch;
                //if (leftCandidate.y < l.y) continue leftCandidateSearch;
                                                            StdDraw.line(l, leftCandidate, StdDraw.ORANGE);Core.freeze("possible left candidate");StdDraw.line(l, leftCandidate, StdDraw.GREEN);

//if this candidate is not supposed to be visible based on shape, we will not add it as a candidate
                //if (!isVisible(leftCandidate, findPoly(leftCandidate), r)) continue leftCandidateSearch;



                Point nextCandidate = (i+1 < leftCandidates.size()) ? leftCandidates.get(i+1) : null;
            //first criterion: angle < 180
                double angle = leftAngles.get(leftCandidate);
                boolean firstCriterion = angle < 179.99;//180.0;
                //if (nextCandidate == null) firstCriterion = angle <= 180;//if no next candidate, allow ==180 to get through
            //second criterion: circumcircle with that candidate may not contain next candidate on that side
                boolean secondCriterion = true;
                if (nextCandidate != null && angle != 180.0){
                    Point cc = getCc(l, r, leftCandidate);
                    if (cc.dist(nextCandidate) < cc.dist(l)){//if dist to next candidate is outside of radius
                        secondCriterion = false;
                    }
                }
            //act accordingly to criteria:
                if (firstCriterion && secondCriterion){
                    leftFinalCandidate = leftCandidate;//our final candidate for that side
                                                            StdDraw.line(l, leftCandidate, StdDraw.BLACK);Core.freeze("found left candidate: " +angle);StdDraw.line(l, leftCandidate, StdDraw.GREEN);
                } else if (!firstCriterion){
                    Core.freeze("left "+angle+" > 180");StdDraw.line(l, leftCandidate, StdDraw.WHITE);
                    break leftCandidateSearch; //Then no candidates will be chosen from that side
                } else if (firstCriterion && !secondCriterion){
                //the line drawn from the endpoint (on the side of the candidate) to the candidate is deleted
                    //if (!adj(l, leftCandidate)) {
                        StdDraw.line(l, leftCandidate, StdDraw.WHITE);
                        Core.freeze("connection severed");
                        merged.get(leftCandidate).remove(l);
                        merged.get(l).remove(leftCandidate);
                    //}
                }
            }
        //RIGHT
            Point rightFinalCandidate = null;
            rightCandidateSearch:
            for (int i = 0; i < rightCandidates.size(); i++){
                if (rightFinalCandidate != null) break rightCandidateSearch;
                Point rightCandidate = rightCandidates.get(i);
                if (rightCandidate == l) continue rightCandidateSearch;
                //if (rightCandidate.y < r.y) continue rightCandidateSearch;
                                                            StdDraw.line(r, rightCandidate, StdDraw.ORANGE);Core.freeze("possible right candidate");StdDraw.line(r, rightCandidate, StdDraw.GREEN);

//if this candidate is not supposed to be visible based on shape, we will not add it as a candidate
                //if (!isVisible(rightCandidate, findPoly(rightCandidate), l)) continue rightCandidateSearch;


                Point nextCandidate = (i+1 < rightCandidates.size()) ? rightCandidates.get(i+1) : null;
            //first criterion: angle < 180
                double angle = rightAngles.get(rightCandidate);
                boolean firstCriterion = angle < 179.99;//180.0;
                //if (nextCandidate == null) firstCriterion = angle <= 180;//if no next candidate, allow ==180 to get through
            //second criterion: circumcircle with that candidate may not contain next candidate on that side
                boolean secondCriterion = true;
                if (nextCandidate != null && angle != 180.0){
                    Point cc = getCc(l, r, rightCandidate);
                    if (cc.dist(nextCandidate) < cc.dist(r)){//if dist to next candidate is outside of radius
                        secondCriterion = false;
                    }
                }
            //act accordingly to criteria:
                if (firstCriterion && secondCriterion){
                    rightFinalCandidate = rightCandidate;//our final candidate for that side
                                                            StdDraw.line(r, rightCandidate, StdDraw.BLACK);Core.freeze("found right candidate: " +angle);StdDraw.line(r, rightCandidate, StdDraw.GREEN);
                } else if (!firstCriterion){
                    Core.freeze("right "+angle+" > 180");StdDraw.line(r, rightCandidate, StdDraw.WHITE);
                    break rightCandidateSearch; //Then no candidates will be chosen from that side
                } else if (firstCriterion && !secondCriterion){
                //the line drawn from the endpoint (on the side of the candidate) to the candidate is deleted
                    //if (!adj(r, rightCandidate)) {
                        StdDraw.line(r, rightCandidate, StdDraw.WHITE);
                        Core.freeze("connection severed");
                        merged.get(rightCandidate).remove(r);
                        merged.get(r).remove(rightCandidate);
                    //}
                }
            }

    //both sides have finished selecting candidates


                    if (leftFinalCandidate == null){
                        Core.log("no left candidate");
                    } else {
                        StdDraw.square(leftFinalCandidate.x, leftFinalCandidate.y, 0.1, StdDraw.CRIMSON);
                    }
                    if (rightFinalCandidate == null){
                        Core.log("no right candidate");
                    } else {
                        StdDraw.square(rightFinalCandidate.x, rightFinalCandidate.y, 0.1, StdDraw.CRIMSON);
                    }
                    //if (leftCandidate != null) StdDraw.square(leftCandidate.x, leftCandidate.y, 0.1, StdDraw.CRIMSON);


        //If neither side has submitted a candidate
            if (leftFinalCandidate == null && rightFinalCandidate == null){
            //the merge is complete
                if (!merged.get(l).contains(r)) merged.get(l).add(r);
                if (!merged.get(r).contains(l)) merged.get(r).add(l);
                break loop;
            }
        //only left candidate was chosen
            else if (leftFinalCandidate != null && rightFinalCandidate == null){
            //check for colinearcy
                double lAngle = leftAngles.get(leftFinalCandidate);
                if (lAngle == 0) {
                //l = lfc
                    l = leftFinalCandidate;
                } else {
                //the line that goes from l to final candidate becomes the new LR edge.
                    if (!merged.get(l).contains(r)) merged.get(l).add(r);
                    if (!merged.get(r).contains(l)) merged.get(r).add(l);
                    l = leftFinalCandidate;
                }
            }
        //only right candidate was chosen
            else if (leftFinalCandidate == null && rightFinalCandidate != null) {
            //check for colinearcy
                double rAngle = rightAngles.get(rightFinalCandidate);
                if (rAngle == 0) {
                //r = rfc
                    r = rightFinalCandidate;
                } else {
                //the line that goes from l to final candidate becomes the new LR edge.
                    if (!merged.get(l).contains(r)) merged.get(l).add(r);
                    if (!merged.get(r).contains(l)) merged.get(r).add(l);
                    r = rightFinalCandidate;
                }
            }
        //If both sides have submitted a candidate:
            else if (leftFinalCandidate != null && rightFinalCandidate != null) {
                double lAngle = leftAngles.get(leftFinalCandidate);
                double rAngle = rightAngles.get(rightFinalCandidate);
                if (lAngle == 0 && rAngle == 0) {
                    if (leftFinalCandidate == rightFinalCandidate) {
                        Core.exit("leftFinalCandidate == rightFinalCandidate");
                    }
                    //StdDraw.setPenRadius(1);
                    Core.println("ETERNAL SLEEP!!!");
                    StdDraw.line(r, l, StdDraw.PURPLE);
                    StdDraw.filledPolygon(new Point[]{r, l, rightFinalCandidate, leftFinalCandidate}, StdDraw.BLUE);
                    StdDraw.filledSquare(r.x, r.y, 0.1, StdDraw.PURPLE); Core.freeze("r");
                    StdDraw.filledSquare(l.x, l.y, 0.2, StdDraw.PURPLE); Core.freeze("l");
                    StdDraw.filledSquare(rightFinalCandidate.x, rightFinalCandidate.y, 0.3, StdDraw.PURPLE); Core.freeze("rfc");
                    StdDraw.filledSquare(leftFinalCandidate.x, leftFinalCandidate.y, 0.4, StdDraw.PURPLE);  Core.freeze("lfc");

                    while (true) {}
                }
                if (lAngle == 0) {
                //l = lfc
                    l = leftFinalCandidate;
                }
                else if (rAngle == 0) {
                    r = rightFinalCandidate;
                }
                else {
                //get the circum circle of both triangles formed with the candidates
                    Point lcc = getCc(l, r, leftFinalCandidate);
                    Point rcc = getCc(l, r, rightFinalCandidate);//we need only test once
                //the circumcircle that does not contain the other candidate will be the final candidate
                    if (lcc.dist(l) > lcc.dist(rightFinalCandidate)){
                    //then rightFinalCandidate is the successor
                        if (!merged.get(l).contains(r)) merged.get(l).add(r);
                        if (!merged.get(r).contains(l)) merged.get(r).add(l);
                        r = rightFinalCandidate;
                    } else {
                        //then leftFinalCandidate is the successor
                        if (!merged.get(l).contains(r)) merged.get(l).add(r);
                        if (!merged.get(r).contains(l)) merged.get(r).add(l);
                        l = leftFinalCandidate;
                    }
                }
            }
            StdDraw.line(l, r, StdDraw.RED);
            Core.freeze("new base LR set");
        }
        StdDraw.clear();
        return merged;
    }

    public static double area(Point d, Point e, Point f){
        return Math.abs(d.x*(e.y-f.y)+e.x*(f.y-d.y)+f.x*(d.y-e.y))/2.0;
    }

//calculates the circumcentre of a collection of points making up a triangle. Used by (2)
    private static Point getCc(Point a, Point b, Point c){
        double x1 = a.x, y1 = a.y, x2 = b.x, y2 = b.y, x3 = c.x, y3 = c.y;
        double y2_MINUS_y1 = (y2-y1==0) ? 0.001 : y2-y1;
        double y3_MINUS_y1 = (y3-y1==0) ? 0.001 : y3-y1;

        double x = 0.5* ( y3 - y2 + (x3*x3-x1*x1)/(y3_MINUS_y1) + (x1*x1-x2*x2)/(y2_MINUS_y1) )/
                        ( (x1-x2)/(y2_MINUS_y1) + (x3-x1)/(y3_MINUS_y1) );
        double y = ((x1-x2)/(y2_MINUS_y1))*x + 0.5*(y1 + y2 + (x2*x2-x1*x1)/(y2_MINUS_y1));

        return new Point(x, y);
    }

/*==============================================================================
                            SORTING
==============================================================================*/
/**
 * Given a hashMap of keys and values, returns an arraylist of the keys sorted
 * based on the values in the orde specified by "minOrMax"
 * It uses mergesort
 *
 * @param  list HashMap with key of any data type and value as Double
 * @param  minOrMax string describing order (max for descending, min for ascending)
 * @throws IllegalArgumentException if {@code minOrMax} is not "min" nor "max"
 */
	public static <T> ArrayList<T> sort(HashMap<T, Double> toSort, String minOrMax){
		if (minOrMax.equals("max")){
			HashMap<T, Double> sorted = toSort
				.entrySet()
				.stream()
				.sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
				.collect(
					toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,LinkedHashMap::new));
			ArrayList<T> toReturn = new ArrayList<>(0);
			for (T t : sorted.keySet()) toReturn.add(t);
			return toReturn;
		}

		HashMap<T, Double> sorted = toSort
			.entrySet()
			.stream()
			.sorted(comparingByValue())
			.collect(toMap(e -> e.getKey(), e -> e.getValue(), (e1, e2) -> e2, LinkedHashMap::new));
		ArrayList<T> toReturn = new ArrayList<>(0);
		for (T t : sorted.keySet()) toReturn.add(t);
		return toReturn;
	}




//Mergesort algorithm adapted for special case where two points have equal x values
// and then gives preference to point with lower y value
	private static Point[] merge(Point[] a, Point[] b){
		Point[] c = new Point[a.length + b.length];
		for (int i = 0, j = 0, k = 0; k < c.length; ){
			if (j == b.length){
				c[k] = a[i];
				k++;
				i++;
			} else if (i == a.length){
				c[k] = b[j];
				k++;
				j++;
			} else if (a[i].x < b[j].x){
				c[k] = a[i];
				k++;
				i++;
			} else if (a[i].x > b[j].x){
				c[k] = b[j];
				k++;
				j++;
			} else if (a[i].x == b[j].x){
                if (a[i].y < b[j].y){
                    c[k] = a[i];
    				k++;
    				i++;
                } else {
                    c[k] = b[j];
    				k++;
    				j++;
                }
            }
		}
		return c;
	}
	private static Point[] mergesort(Point[] a){
		if (2 < a.length){
			Point[] b = new Point[a.length/2];
			Point[] c = new Point[a.length/2];
			if (a.length%2 == 0){//if even
				for (int i = 0; i < b.length; i++){
					b[i] = a[i];
					c[i] = a[b.length+i];
				}
			} else {//if odd
				c = new Point[a.length/2 +1];
				for (int i = 0; i < b.length; i++) b[i] = a[i];
				for (int i = 0; i < c.length; i++) c[i] = a[b.length+i];
			}
			return merge(mergesort(b), mergesort(c));
		}
		if (a.length == 1) return a;
		else if (a[0].x > a[1].x){
			return new Point[]{a[1], a[0]};
		} else if (a[0].x == a[1].x){
            if (a[0].y > a[1].y){
    			return new Point[]{a[1], a[0]};
            }
        }
		return a;
	}

}
