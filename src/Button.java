import java.awt.*;
import java.io.*;
import java.util.*;

public class Button extends Clicker {

    public Button(double x, double y, double hw, double hh, String image){
        super(x, y, hw, hh, image);
    }

    public Button(double x, double y, double r, String image){
        super(x, y, r, image);
    }

    protected void hover(){
        if (!lit) {
            try {
                StdDraw.picture(x, y, hover, hw*2, hh*2);
            } catch (IllegalArgumentException e){
                StdDraw.filledRectangle(x, y, hw, hh, StdDraw.LIGHT_ATOM);
                StdDraw.rectangle(x, y, hw, hh, StdDraw.LIGHT_GRAY);
                StdDraw.text(x, y, image, StdDraw.LIGHT_GRAY);
            }
            lit = true;
        }
    }

    protected void unhover(){
        if (lit){
            //StdDraw.rectangle(x, y, hw, hh, background);
            try {
                StdDraw.picture(x, y, image, hw*2, hh*2);
            } catch (IllegalArgumentException e){
                StdDraw.filledRectangle(x, y, hw, hh, StdDraw.ATOM);
                StdDraw.rectangle(x, y, hw, hh, StdDraw.LIGHT_GRAY);
                StdDraw.text(x, y, image, StdDraw.LIGHT_GRAY);
            }
            lit = false;
        }
    }
}
