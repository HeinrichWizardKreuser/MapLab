import java.awt.*;
import java.io.*;
import java.util.*;

public abstract class Clicker {

    public Color background = StdDraw.ATOM;

    public double x;
    public double y;
    public double hw;
    public double hh;
    public Point SE(){return new Point(x+hw, y-hh);}
    public Point NE(){return new Point(x+hw, y+hh);}
    public Point SW(){return new Point(x-hw, y-hh);}
    public Point NW(){return new Point(x-hw, y+hh);}
    public String image;
    public String hover;

    public Clicker(double x, double y, double hw, double hh, String image){
        set(x, y, hw, hh, image);
    }

    public Clicker(double x, double y, double r, String image){
        set(x, y, r, r, image);
    }

    private void set(double x, double y, double hw, double hh, String image){
        this.x = x;
        this.y = y;
        this.hw = hw;
        this.hh = hh;
        this.image = image;
        try {
            hover = image.replace(".png", "_T.png");
            StdDraw.picture(Run.height+1, Run.width+1, hover, 0.5, 0.5);
        } catch (IllegalArgumentException e) {
            hover = null;
        }
    }

/*==============================================================================
                                SHOW
==============================================================================*/
    public void show(){
        //Core.log(">>"+image + " showing");
        try {
            StdDraw.picture(x, y, image, hw*2, hh*2);
        } catch (IllegalArgumentException e){
            StdDraw.filledRectangle(x, y, hw, hh, background);
            StdDraw.rectangle(x, y, hw, hh, StdDraw.LIGHT_GRAY);
            StdDraw.text(x, y, image, StdDraw.LIGHT_GRAY);
        }
        if (showSubscription != null) subscriber.notify(showSubscription);
        Run.addTask(this);//add self to active listeners to check for
        StdDraw.show();
    }

    public void hide(){
        StdDraw.filledRectangle(x, y, hw, hh, background);
        StdDraw.rectangle(x, y, hw, hh, background);//patchfix
        if (hideSubscription != null) subscriber.notify(hideSubscription);
        Run.removeTask(this);//remove self from active listeners to check for
        StdDraw.show();
    }

    public void overridePress(){ overridePress = true; }
    protected boolean overridePress = false;//If we wish to force press the button.
    public boolean isPressed(){
        //Core.log(image +" isPressed()");
        if (overridePress){
            hover();
            overridePress = false;
            return true;
        }
        if (StdDraw.mousePressed() && Math.abs(x-StdDraw.mouseX()) < hw && Math.abs(y-StdDraw.mouseY()) < hh){
            while (StdDraw.mousePressed()){}
            //if (Math.abs(x-StdDraw.mouseX()) < hw && Math.abs(y-StdDraw.mouseY()) < hh) //maybe add
                return true;
        }
        return false;
    }

/*==============================================================================
                                HOVERING
==============================================================================*/
    public void updateHovering(){
        //if (overridePress) hover();
        if (hover(StdDraw.mouseX(), StdDraw.mouseY())){
            hover();
        } else unhover();
        StdDraw.show();
    }

    public boolean hover(double mouseX, double mouseY){
        if (Math.abs(x-mouseX) < hw && Math.abs(y-mouseY) < hh){
            //sound
            return true;
        }
        return false;
    }

    public boolean lit = false;
    protected abstract void hover();
    protected abstract void unhover();


/*==============================================================================
                                SUBSCRIPTION
==============================================================================*/

    public Subscriber subscriber;//will notify this class when pressed
    public String notification;//will send this message to subscriber when pressed
    public void setSubscriber(Subscriber subscriber, String notification){
        this.subscriber = subscriber;
        this.notification = notification;
    }
    public void ping(){
        subscriber.notify(notification);
    }

//hovering subscription lets us notify the subscriber once it is hovered over
    public void setHoverSubscriber(String s){ hoverSubscription = s; }
    protected String hoverSubscription;
//UNHOVER
    public void setUnhoverSubscriber(String s){ unhoverSubscription = s; }
    protected String unhoverSubscription;
//SHOW
    public void setShowSubscriber(String s){ showSubscription = s; }
    protected String showSubscription;
//HIDE
    public void setHideSubscriber(String s){ hideSubscription = s; }
    protected String hideSubscription;
}
