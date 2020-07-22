import java.awt.*;
import java.util.*;

public class Sqr {

    public double x;
    public double y;
    public int ix;
    public int iy;
    public double state;

    public Sqr(double x, double y, double state){
        setxy(x,y);
        this.state = state;
    }

    public void setxy(double x, double y){
		this.x = x;
		this.y = y;
		this.ix = (int)x;
		this.iy = (int)y;
	}

    public Color getColor(){
        if (state == -1) return new Color(0, 95, 120);
        return new Color( 120, (int)(220-25*state), 120);
    }

    public double getState(){return state;}

    public Sqr replica(){
        Sqr replica = new Sqr(this.x, this.y, state);
        //if (this.leader != null) replica.leader = this.trueLeader().replica();
        return replica;
    }
//return a copy of this sqr but at different place. same state tho
    public Sqr replica(double x, double y){
        Sqr replica = new Sqr(x, y, state);
        return replica;
    }

//Returns the true leader
    public Sqr trueLeader(){
    //if we are own leader, reveal ourselves
        if (leader == null) return null;
        if (leader == this) return this;
    //else, show whom we follow
        return leader.trueLeader();
    }
    public Sqr leader = null;//the square to whom this one points


    public String toString(){return "[" +x+ ";" +y+ "] state " +state;}
/*==============================================================================
                            ITERABLE LISTS
==============================================================================*/
//These return ArrayList<Sqr> subject to pointers

//Return a list of all sqrs within a certain displacement discluding self
    public ArrayList<Sqr> inDisplacement(SqrMap map, int displacement){
        ArrayList<Sqr> inDisplacement = new ArrayList<Sqr>(0);
        for (double dx = x-displacement; dx <= x+displacement; dx++){
            for (double dy = y-displacement; dy <= y+displacement; dy++){
                int ix = (int)dx, iy = (int)dy;
                if (map.exists(dx, dy) && Core.displacement(x, y, dx, dy) == 1)
                    if(map.accessOverride_grid()[ix][iy] != this)
                        inDisplacement.add(map.accessOverride_grid()[ix][iy]);
            }
        }
        return inDisplacement;
    }

//returns a list of all sqrs in radius of this sqr discluding self
    public ArrayList<Sqr> inRadius(SqrMap map, int radius){
        ArrayList<Sqr> inRadius = new ArrayList<Sqr>(0);
        for (double dx = x-radius; dx <= x+radius; dx++){
            for (double dy = y-radius; dy <= y+radius; dy++){
                int ix = (int)dx, iy = (int)dy;
                if (map.exists(dx, dy))
                    if(map.accessOverride_grid()[ix][iy] != this)
                        inRadius.add(map.accessOverride_grid()[ix][iy]);
            }
        }
        return inRadius;
    }



   /* We are not sure what we want this function to do yet, we will work on it once we've
       determined that we need it.

       public ArrayList<Block> adjSameStates(Block[][] map){
           ArrayList<Block> adjSameStates = new ArrayList<Block>(0);
           for (int dx = x-1; dx < x+2; dx++){
               for (int dy = y-1; dy < y+2; dy++){
                   if (Core.displacement(x, y, dx, dy) != 0
                   && TestField.exists(dx, dy)
                   && map[dx][dy] != null
                   && map[dx][dy].state == state)
                       adjacentBlocks.add(map[dx][dy]);
               }
           }

           return adjSameStates;
       }
   */
   /*


   //return a list of all of the different types of groups adjacent to this block atm
       public ArrayList<Group> adjGroups(Block[][] map){
           ArrayList<Group> adjGroups = new ArrayList<Group>(0);
           for (int dx = x-1; dx < x+2; dx++){
               for (int dy = y-1; dy < y+2; dy++){
                   if (Core.displacement(x, y, dx, dy) == 1
                   && TestField.exists(dx, dy)
                   && map[dx][dy] != null
                   && !adjGroups.contains(map[dx][dy].group)
                   && map[dx][dy].group != null){
                       adjGroups.add(map[dx][dy].group);
                       if (map[dx][dy].group == null){
                           Core.trace();
                           Core.freeze("map[" +x+ "][" +y+ "].group == null");
                       }
                   }
               }
           }

           return adjGroups;
       }

       public Group smallestAdjGroup(Block[][] map){
           Group smallest = null;
           if (adjGroups(map).isEmpty()) return null;
           for (Group g : adjGroups(map)){
               if (smallest == null) smallest = g;

                   if (g.size() <
                   smallest.size()) smallest = g;


           }
           return smallest;
       }

       public Group largestAdjGroup(Block[][] map){
           Group largest = null;
           for (Group g : adjGroups(map)){
               if (largest == null) largest = g;
               if (g.size() > largest.size()) largest = g;
           }
           return largest;
       }
*/
}
