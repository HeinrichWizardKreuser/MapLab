import java.util.*;
import java.awt.*;

public class Pane {

    public double  x;
    public double  y;
    public final double hw;
    public final double hh;

    public Point top(){return new Point(x, y+hh); }
    public Point bottom(){return new Point(x, y-hh); }
    public Point left(){return new Point(x-hw, y); }
    public Point right(){return new Point(x+hw, y); }
    public Point middle(){return new Point(x, y); }

    public Point SE(){return new Point(x+hw, y-hh);}
    public Point NE(){return new Point(x+hw, y+hh);}
    public Point SW(){return new Point(x-hw, y-hh);}
    public Point NW(){return new Point(x-hw, y+hh);}

    public Color background = StdDraw.ATOM;

    public Pane(double x, double  y, double  hw, double  hh){
        this.x = x;
        this.y = y;
        this.hw = hw;
        this.hh = hh;
        subPanes = new HashMap<>();
        buttons = new HashMap<>();
        tickBoxes = new HashMap<>();
        hoverListeners = new HashMap<>();
        thumbnails = new HashMap<>();
        showing = false;
    }


/*==============================================================================
                            SHOW AND HIDE
==============================================================================*/

    public boolean showing = false;

//Show all buttons and subpane listeners
    public void show(){
    //show the pane listener and path
        if (listener != null){
            drawOutline(StdDraw.LIGHT_GRAY);
            Run.removeTask(listener);//don't wait for press if pane active
        }
    //show all subPane listeners and own buttons
        for (Pane subPane : subPanes.values()) subPane.listener.show();
        for (Button b : buttons.values()) b.show();
        for (TickBox t : tickBoxes.values()) t.show();
        for (HoverListener hl : hoverListeners.values()) hl.show();
        for (Thumbnail t : thumbnails.values()) t.show();
        if (showSubscription != null) subscriber.notify(showSubscription);
    //set as showing
        showing = true;
        StdDraw.show();
    }

//Hide all of this pane's sub panes and b
    public void hide(){
    //close all sub panes and buttons
        drawOutline(background);
        listener.show();
        for (Pane subPane : subPanes.values()){
            subPane.dissapear();
        }
        for (Button b : buttons.values()) b.hide();
        for (TickBox t : tickBoxes.values()) t.hide();
        for (HoverListener hl : hoverListeners.values()) hl.hide();
        for (Thumbnail t : thumbnails.values()) t.hide();
        if (hideSubscription != null) subscriber.notify(hideSubscription);
    //hide self, set as not showing
        StdDraw.filledRectangle(x, y, hw, hh, background);
        showing = false;
        StdDraw.show();
    }

    public void dissapear(){
        listener.hide();
        drawOutline(background);
        for (Pane subPane : subPanes.values()){
            subPane.dissapear();
        }
        for (Button b : buttons.values()) b.hide();
        for (TickBox t : tickBoxes.values()) t.hide();
        for (HoverListener hl : hoverListeners.values()) hl.hide();
        for (Thumbnail t : thumbnails.values()) t.hide();
    //hide self, set as not showing
        StdDraw.filledRectangle(x, y, hw, hh, background);
        showing = false;
    }

/*==============================================================================
                                SUBSCRIPTION
==============================================================================*/

    public Subscriber subscriber;//will notify this class of notifications
    public void setSubscriber(Subscriber subscriber){ this.subscriber = subscriber; }
//SHOW
    public void setShowSubscriber(String s){ showSubscription = s; }
    protected String showSubscription;
//HIDE
    public void setHideSubscriber(String s){ hideSubscription = s; }
    protected String hideSubscription;

//listener
    public Button listener;//assigned button that listens=
    public void setListener(Button b){ listener = b; }
//subPanes
    public HashMap<String, Pane> subPanes;
    public Pane getSubPane(String name){ return subPanes.get(name); }
    public void addSubPane(String name, Pane newPane){ subPanes.put(name, newPane); }
//buttons
    public HashMap<String, Button> buttons;
    public Button getButton(String name){ return buttons.get(name); }
    public void addButton(String name, Button b){ buttons.put(name, b); }
//tickBoxes
    public HashMap<String, TickBox> tickBoxes;
    public void addTickBox(String name, TickBox t){ tickBoxes.put(name, t); }
    public TickBox getTickBox(String name){ return tickBoxes.get(name); }
//hoverListeners
    public HashMap<String, HoverListener> hoverListeners;
    public void addHoverListener(String name, HoverListener hl){ hoverListeners.put(name, hl); }
    public HoverListener getHoverListener(String name){ return hoverListeners.get(name); }
//thumbnail
    public HashMap<String, Thumbnail> thumbnails;
    public void addThumbnail(String name, Thumbnail t){ thumbnails.put(name, t); }
    public Thumbnail getThumbnail(String name){ return thumbnails.get(name); }

//draw lines to closest two corners of listener
    private void drawOutline(Color color){
        //Core.log("drawing outline for pane with listener " +listener.image);
        Point[] poly = new Point[1 + 2 + 2 + 3];//1 of listener, 2 from listener to box, 2 from those to corners of box, 3 remaining sides of box
        double d = 0.01;
        Button l = listener;//pointer-shortcut
    //check if within x bounds or within y bounds
        if (left().x <= l.x && l.x <= right().x){
        //then is on top or bottom
            if (l.y < this.y){//then under
            //two bottom corners
                poly[0] = new Point(l.x-l.hw+d, l.y-l.hh+d);
                poly[7] = new Point(l.x+l.hw-d, l.y-l.hh+d);
                StdDraw.line(l.NW(), l.NE(), background);
            //corners joining l and box
                poly[1] = new Point(l.x-l.hw+d, y-hh+d);
                poly[6] = new Point(l.x+l.hw-d, y-hh+d);
            //rest of box corners
                poly[2] = new Point(x-hw+d, y-hh+d);
                poly[3] = new Point(x-hw+d, y+hh-d);
                poly[4] = new Point(x+hw-d, y+hh-d);
                poly[5] = new Point(x+hw-d, y-hh+d);
            } else {//then top
            //two top corners
                poly[0] = new Point(l.x-l.hw+d, l.y+l.hh-d);
                poly[7] = new Point(l.x+l.hw-d, l.y+l.hh-d);
                StdDraw.line(l.SW(), l.SE(), background);
            //corners joining l and box
                poly[1] = new Point(l.x-l.hw+d, y+hh-d);
                poly[6] = new Point(l.x+l.hw-d, y+hh-d);
            //rest of box corners
                poly[2] = new Point(x-hw+d, y+hh-d);
                poly[3] = new Point(x-hw+d, y-hh+d);
                poly[4] = new Point(x+hw-d, y-hh+d);
                poly[5] = new Point(x+hw-d, y+hh-d);
            }
        } else {
        //then is on left or right
            if (l.x < this.x){//then left
            //two left corners
                poly[0] = new Point(l.x-l.hw+d, l.y+l.hh-d);
                poly[7] = new Point(l.x-l.hw+d, l.y-l.hh+d);
                StdDraw.line(l.SE(), l.NE(), background);
            //corners joining l and box
                poly[1] = new Point(x-hw+d, l.y+l.hh-d);
                poly[6] = new Point(x-hw+d, l.y-l.hh+d);
            //rest of box corners
                poly[2] = new Point(x-hw+d, y+hh-d);
                poly[3] = new Point(x+hw-d, y+hh-d);
                poly[4] = new Point(x+hw-d, y-hh+d);
                poly[5] = new Point(x-hw+d, y-hh+d);
            } else {//then right
            //two right corners
                poly[0] = new Point(l.x+l.hw-d, l.y+l.hh-d);
                poly[7] = new Point(l.x+l.hw-d, l.y-l.hh+d);
                StdDraw.line(l.NW(), l.NE(), background);
            //corners joining l and box
                poly[1] = new Point(x+hw-d, l.y+l.hh-d);
                poly[6] = new Point(x+hw-d, l.y-l.hh+d);
            //rest of box corners
                poly[2] = new Point(x+hw-d, y+hh-d);
                poly[3] = new Point(x-hw+d, y+hh-d);
                poly[4] = new Point(x-hw+d, y-hh+d);
                poly[5] = new Point(x+hw-d, y-hh+d);
            }
        }
        StdDraw.polygon(poly, color);
    }
}
