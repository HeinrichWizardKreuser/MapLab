//Class mainly for the versions thing
import java.awt.*;
import java.awt.image.BufferedImage;

public class Thumbnail extends Clicker{

//Constructors
    public Thumbnail(double x, double y, double hw, double hh, String image){
        super(x, y, hw, hh, image);
        setHoverColor(StdDraw.RED);
    }

    public Thumbnail(double x, double y, double r, String image){
        super(x, y, r, image);
        setHoverColor(StdDraw.RED);
    }

    public Thumbnail(double x, double y, double hw, double hh, BufferedImage image){
        super(x, y, hw, hh, "non-image thumbnail");
        setHoverColor(StdDraw.RED);
        this.bufferedImage = image;
    }

    public Thumbnail(double x, double y, double r, BufferedImage image){
        super(x, y, r, "non-image thumbnail");
        setHoverColor(StdDraw.RED);
        this.bufferedImage = image;
    }

    private BufferedImage bufferedImage;
    public SqrMap sqrMap;

/*==============================================================================
                                SHOW
==============================================================================*/
//draw tickbox in current state
    @Override
    public void show(){
    //Core.log(">>"+image + " showing");
        StdDraw.picture(x, y, bufferedImage, hw*2, hh*2);
        Run.addTask(this);//add self to active listeners to check for
        StdDraw.show();
    }

//cover itself and title
    @Override
    public void hide(){
        StdDraw.filledRectangle(x, y, hw, hh, background);
        Run.removeTask(this);//remove self from active listeners to check for
        StdDraw.show();
    }


/*==============================================================================
                                HOVERING
==============================================================================*/

//Hovering
    public void setHoverColor(Color hoverColor){
        this.hoverColor = hoverColor;
    }
    public Color hoverColor(){return hoverColor;}
    private Color hoverColor;

    @Override
    protected void hover(){
        if (!lit) {
            //StdDraw.rectangle(x, y, hw-0.01, hh-0.01, hoverColor);
            StdDraw.polygon(new double[]{x-hw, x-hw, x+hw-0.01, x+hw-0.01},
                            new double[]{y-hh+0.01, y+hh, y+hw, y-hh+0.01}, hoverColor);
            lit = true;
            if (hoverSubscription != null){
                subscriber.notify(hoverSubscription);
            }
        }
    }

    @Override
    protected void unhover(){
        if (lit){
            StdDraw.picture(x, y, bufferedImage, hw*2, hh*2);
            lit = false;
            if (unhoverSubscription != null){
                subscriber.notify(unhoverSubscription);
            }
        }
    }
}
