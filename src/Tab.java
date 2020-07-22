import java.awt.*;
import java.io.*;
import java.util.*;

public class Tab extends Clicker {

    public Tab(double x, double y, double hw, double hh, String name){
        super(x, y, hw, hh, name);
    }

    protected void hover(){
        if (!lit) {
            StdDraw.filledRectangle(x, y, hw, hh, StdDraw.LIGHT_ATOM);
            StdDraw.rectangle(x, y, hw, hh, StdDraw.LIGHT_GRAY);
            StdDraw.text(x, y, image, StdDraw.LIGHT_GRAY);
            lit = true;
        }
    }

    protected void unhover(){
        if (lit){
            StdDraw.filledRectangle(x, y, hw, hh, StdDraw.ATOM);
            StdDraw.rectangle(x, y, hw, hh, StdDraw.LIGHT_GRAY);
            StdDraw.text(x, y, image, StdDraw.LIGHT_GRAY);
            lit = false;
        }
    }

    public ArrayList<Tab> family;//Group of panes that cannot individually be active

    public static void setFamily(Clicker[] family){
        Tab[] tabs = new Tab[family.length];
        for (int i = 0; i < tabs.length; i++){
            tabs[i] = (Tab)(family[i]);
        }
        for (Tab b : tabs){
            b.family = new ArrayList<Tab>(0);
            for (Tab fam : tabs){
                if (fam != b){
                    b.family.add(fam);
                }
            }
        }
    }

}
