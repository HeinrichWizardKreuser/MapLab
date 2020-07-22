public class TickBox extends Clicker{

    private String titlePos;//"left"/"right"/"up"/"down"
    private String title;
    public String tickBox = "../img/tickBox.png";
    public String tickBox_T = "../img/tickBox_T.png";
    public String tickedBox = "../img/tickedBox.png";
    public String tickedBox_T = "../img/tickedBox_T.png";
    public TickBox(double x, double y, double r, String title, String titlePos){
        super(x, y, r, title);
        this.title = title;
        this.titlePos = titlePos;
    }

    private boolean ticked = false;//is this tickbox ticked?
    public boolean ticked() { return ticked; }
    public void tick(){
        ticked = !ticked;
        StdDraw.picture(x, y, (ticked?tickedBox:tickBox), hw*2, hh*2);
    }

/*==============================================================================
                                SHOW
==============================================================================*/
//draw tickbox in current state
    @Override
    public void show(){
    //Core.log(">>"+image + " showing");
        StdDraw.picture(x, y, (ticked?tickedBox:tickBox), hw*2, hh*2);
        double w = StdDraw.getTextWidth(title);
        double h = StdDraw.getTextHeight(title);
        switch(titlePos){
            case "left" :
                StdDraw.filledRectangle(x-0.5-(w/2), y, w/2, h/2, Run.background);
                StdDraw.textRight(x-(0.5), y, title, StdDraw.WHITE);
                break;
            case "right" :
                StdDraw.filledRectangle(x+0.5+(w/2), y, w/2, h/2, Run.background);
                StdDraw.textLeft(x+(0.5), y, title, StdDraw.WHITE);
                break;
            case "up" :
                StdDraw.filledRectangle(x, y+1, w/2, h/2, Run.background);
                StdDraw.text(x, y+(1), title, StdDraw.WHITE);
                break;
            case "down" :
                StdDraw.filledRectangle(x, y-1, w/2, h/2, Run.background);
                StdDraw.text(x, y-(1), title, StdDraw.WHITE);
                break;
        }
        Run.addTask(this);//add self to active listeners to check for
        StdDraw.show();
    }

//cover itself and title
    public void hide(){
        StdDraw.filledRectangle(x, y, hw, hh, background);
        Run.removeTask(this);//remove self from active listeners to check for
        StdDraw.show();
    }


/*==============================================================================
                                HOVERING
==============================================================================*/

    @Override
    protected void hover(){
        //Core.log(title +" hover");
        if (!lit) {
            StdDraw.picture(x, y, (ticked?tickedBox_T:tickBox_T), hw*2, hh*2);
            lit = true;
        }
    }

    @Override
    protected void unhover(){
        //Core.log(title +" unhover");
        if (lit){
            StdDraw.rectangle(x, y, hw, hh, background);
            StdDraw.picture(x, y, (ticked?tickedBox:tickBox), hw*2, hh*2);
            lit = false;
        }
    }
}
