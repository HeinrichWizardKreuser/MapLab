public class HoverListener extends Clicker{

    public HoverListener(double x, double y, double hw, double hh){
        super(x, y, hw, hh, "hover-listener");
    }

    public HoverListener(double x, double y, double r){
        super(x, y, r, "hover-listener");
    }

/*==============================================================================
                                SHOW
==============================================================================*/
//draw tickbox in current state
    @Override
    public void show(){
    //Core.log(">>"+image + " showing");
        Run.addTask(this);//add self to active listeners to check for
    }

//cover itself and title
    @Override
    public void hide(){
        Run.removeTask(this);//remove self from active listeners to check for
    }

    @Override
    public boolean isPressed(){
        //Core.log(image +" isPressed()");
        if (overridePress){
            hover();
            overridePress = false;
            return true;
        }
        if (StdDraw.mousePressed() && Math.abs(x-StdDraw.mouseX()) < hw && Math.abs(y-StdDraw.mouseY()) < hh){
            return true;
            //if (Math.abs(x-StdDraw.mouseX()) < hw && Math.abs(y-StdDraw.mouseY()) < hh) //maybe add
        }
        return false;
    }

/*==============================================================================
                                HOVERING
==============================================================================*/
//Hovering
    @Override
    protected void hover(){
        if (!lit) {
            lit = true;
            StdDraw.square(4, 4, 3, StdDraw.RED);
        }
        //subscriber.notify(notification +"hover")
        //String s = notification;
        //notification = s.substring(0, s.indexOf("[x")) +mouseLoc.toString()+ s.substring(s.indexOf("y]")+2, s.length());
        //subscriber.notify();
    }
    //private Point mouseLoc;

    @Override
    protected void unhover(){
        if (lit){
            //mouseLoc = null;
            lit = false;
            StdDraw.square(4, 4, 3, Run.background);
        }
    }
}
