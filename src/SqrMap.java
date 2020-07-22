import java.util.*;
//import javax.swing.*;
import java.awt.*;

public class SqrMap extends Map implements Subscriber{

    private Sqr[][] grid;
    public Sqr[][] accessOverride_grid() { return grid; }
    public Sqr grid(double x, double y){
        if (!exists(x,y)) return null;
        if (grid[(int)x][(int)y] == null) return null;
        return grid[(int)x][(int)y].replica();
    }

    private int w, h;
    private double dw, dh;
    private double centreX, centreY;

    public boolean exists(double x, double y){return 0 <= x && x < w && 0 <= y && y < h;}
    public boolean exists(Point p) {return exists(p.x, p.y);}

//Main constructor for generating a new map
    public SqrMap(HashMap<String, String> script){
        super(script);
    //HashMap of all version of this SqrMap for the user to view
        saved = new HashMap<>();
    //Procedurally generates the full map, all indices will be defined
        generate();
        saveAs("First Generation");
    //Apply the symmetry type
        if (applySymmetry()) saveAs("Symmetry Applied");
    //Replace some sqrs
        if (replace()) saveAs("After Replacements");
    //Connect some seperate groups
        if (connect()) saveAs("Post Connections");
    }

//saved is where we save all of the versions of the sqrmap (after changes like via editor or just patchfixing process)
    private HashMap<String, SqrMap> saved;
    public void saveAs(String title){
        saved.put(title, new SqrMap(this));
    //TODO: research a way to show all versions without creating all of their images again
    //      without making changes impossible (Or maybe once it is saved, it should be impossible?)
    //      Only research this if time taken gets too much
        //saved.get(title).setImage();

    //notify saved thumbnails that a change has been made
        //oldThumbnails = null;
    }


//Constructs a SqrMap with all of the data of the previous and avoiding pointers
    public SqrMap(SqrMap that){
        super(that.script);
        setwh(that.w, that.h);
        this.grid = new Sqr[that.w][that.h];
        for (int x = 0; x < that.w; x++){
            for (int y = 0; y < that.h; y++){
                this.grid[x][y] = that.grid(x,y).replica();
            }
        }
    }

    public void syncTo(SqrMap that){
    //constructor stuff
        setwh(that.w, that.h);
        this.script = that.script;
        if (script.containsKey("ProcSet")){
            this.procSet = new ArrayList<Double>(0);
            String[] procs = script.get("ProcSet").substring(1, script.get("ProcSet").length()-1).split(";");
            for (String proc : procs){
                procSet.add(Double.parseDouble(proc));
            }
        }
    //copy stuff
        this.grid = new Sqr[that.w][that.h];
        for (int x = 0; x < that.w; x++){
            for (int y = 0; y < that.h; y++){
                this.grid[x][y] = that.grid(x,y).replica();
            }
        }
    }

    public void setwh(int w, int h){
        this.w = w;
        this.h = h;
        this.dw = (double)w;
        this.dh = (double)h;
        this.centreX = dw/2.0-0.5;
        this.centreY = dh/2.0-0.5;
    }

/* SCRIPT

MapType: SqrMap
Dimensions: 20x20 //###x#
ProcDirection: if(x<w-1?[x+1;y]:[0;y+1]) //some piece of math describing next forloop location
ProcStart: [0;0] //where does the parsed forloop start?
ProcEnd: [w-1;h-1] or "untildone" //where does the parsed forloop end?
ProcAlgorithm: if(adj==0?(if(random>0.83?1:0)):(if(random>0.66?0:1)))
FirstState: 0 or <Function> //what is the first state?
Symmetry: rotate([0;90],90);rotate([0;180],180)
Remove: 0{if(area<0.05*(w*h)?1:0)};1{if(area==1?1:0)};
Connect: 0
ProcSet: {-1;0;1;2;3;4;5} //or (1;9) meaning any number between 1 and 9 disclusive


Symmetry: rotate([0,90],90);reflect([0;180],180)

*/


/*==============================================================================
                                GENERATE
==============================================================================*/

//Generates the map nased on the script
    private void generate(){

    //1) Width and Height
        String[] dimensions = script.get("Dimensions").split("x");
        w = Integer.parseInt(dimensions[0]);
        h = Integer.parseInt(dimensions[1]);
        setwh(w, h);
        //Core.freeze("w="+w+", h="+h);
        grid = new Sqr[w][h];

    //2) ProcDirection
        procDirection = new Function(script.get("ProcDirection"));
        procDirection.addVariable("w", w);
        procDirection.addVariable("h", h);
        //output = nextxy.toArray(nextxy.getOut("x=5;y=3"));

    //3) ProcAlgorithm
        procAlgorithm = new Function(script.get("ProcAlgorithm"));

    //4) Generate the map
        procGen();
    }
//ProcAlgorithm: given the last state, it returns the next state
//(state = some number describing the coordinate e.g. terrain hieght)
    private Function procAlgorithm;
//ProcDirection: given an [x;y] coordinate, it returns the next [x;y] coordinate
    private Function procDirection;

//Procedurally generate the map based on the procDirection and procAlgorithm
    private void procGen() {
    //get starting position
        Function procStart = new Function(script.get("ProcStart"));
        Point curr = new Point(procStart.toArray(procStart.getOut("w="+w+";h="+h)));
    //get end position
        if (!script.get("ProcEnd").equals("untildone")){
            Function procEnd = new Function(script.get("ProcEnd"));
            endPoint = new Point(procEnd.toArray(procEnd.getOut("w="+w+";h="+h)));
        }
        Function nextDir = procDirection;
        Function nextState = procAlgorithm;
    //define first point
        Function firstState = new Function(script.get("FirstState"));
        double last = firstState.f();
        firststate = last;

        Core.log(firstState+"");

        //Core.log("curr:"+curr.ix+", "+curr.iy);
        grid[curr.ix][curr.iy] = new Sqr(curr.ix, curr.iy, nextState.f());
    //psuedo forloop, "iterate until done"
        while(!done(curr)){
            nextState.addVariable("last",last);
            nextState.addVariable("adj",getAdjState(curr.ix,curr.iy,grid));
            nextState.addVariable("hasAdj", hasAdj(curr.ix, curr.iy, grid));
        //get next point
            curr = new Point(nextDir.toArray(nextDir.getOut("x="+curr.ix+";y="+curr.iy)));
        //define new point
            last = nextState.f();
        //if over limit, bring back
            if (last < Collections.min(procSet)) last = Collections.min(procSet);
            else if (Collections.max(procSet) < last) last = Collections.max(procSet);
            grid[curr.ix][curr.iy] = new Sqr(curr.ix, curr.iy, last);
            //Core.log(last+"");
                //grid[curr.ix][curr.iy].grid = grid;

            //Core.log("curr:"+curr.ix+", "+curr.iy);
        }
    }

    private Point endPoint;
    private boolean done(Point curr){
        if (endPoint == null){
            for (int x = 0; x < w; x++)
                for (int y = 0; y < h; y++)
                    if (grid[x][y] == null) return false;
            return true;
        }
        return endPoint.equals(curr);
    }

    private double firststate;
//Get the state of the adjacent square to this one
    private double getAdjState(int ix, int iy, Sqr[][] grid){
        HashMap<Double, Integer> adj = new HashMap<>(0);
        for (int x = ix-1; x <= ix+1; x++)
            for (int y = iy-1; y <= iy+1; y++)
                if((Math.abs(ix-x) + Math.abs(iy-y) == 1) && exists(ix,iy) && grid[ix][iy] != null){
                    if (!adj.containsKey(grid[ix][iy].getState())) adj.put(grid[ix][iy].getState(), 1);
                    else adj.put(grid[ix][iy].getState(), adj.get(grid[ix][iy].getState())+1);
                }
        if (adj.isEmpty()) return firststate;
        Double corporation = null;
        for(double state : adj.keySet()){
            if (corporation == null) corporation = state;
            else if (adj.get(state) > adj.get(corporation)) corporation = state;
            else if (adj.get(state) == adj.get(corporation)) corporation = (Math.random() < 0.5)?corporation:state;
        }
        return corporation;
    }
//returns 1 if it has an adj defined cell and 0 if not
    private double hasAdj(int ix, int iy, Sqr[][] grid) {
        for (int x = ix-1; x <= ix+1; x++)
            for (int y = iy-1; y <= iy+1; y++)
                if((Math.abs(ix-x) + Math.abs(iy-y) == 1) && exists(ix,iy) && grid[ix][iy] != null){
                    return 1d;
                }
        return 0d;
    }

/*==============================================================================
                                SYMMETRY
==============================================================================*/

//applies symmetry
    private boolean applySymmetry(){
        //1) Get Symmetry commands
        if (!script.containsKey("Symmetry")) return false;
        String sym = script.get("Symmetry");
        if (sym.equals("none")) return false;
        char[] arr = sym.toCharArray();
        //2) Extract commands
        int brace = -1;
        int last = 0;
        ArrayList<String> symCommands = new ArrayList<String>(0);
        for(int i = 0; i < arr.length; i++){
            if (arr[i] == '(') brace++;
            else if (arr[i] == ')') brace--;

            if (brace == -1 && arr[i] == ';') {
                symCommands.add(sym.substring(last,i));
                last = i+1;
            }
        }
        symCommands.add(sym.substring(last,sym.length()));

        //Apply symmetry commands "rotate([0,90],90);reflect([0;180],180)"
        symCommandsLoop: for(String s : symCommands){
        //Get pie area to copy and theta line
            //Point twoAngles;//= Point with x representing minimum angle and y representing maximum angle.
            double[] twoAngles;//degrees of the min and max angles
            double theta = 0;//angle at whcih we rotate
            Function symF;
            if (s.startsWith("reflect(")){//reflect([0;180],180)
                //twoAngles = new Point(s.substring(8,s.indexOf(",")));
                twoAngles = (new Point(s.substring(8,s.indexOf(",")))).toArray();
                theta = Double.parseDouble(s.substring(s.indexOf(",")+1,s.length()-1));
                symF = Angle.reflect.replica();//new Function("[i+x*cos(2*theta)+y*sin(2*theta);j+x*sin(2*theta)-y*cos(2*theta)]");
            } else if (s.startsWith("rotate(")){//rotate([0,90],90)
                //twoAngles = new Point(s.substring(7,s.indexOf(",")));
                twoAngles = (new Point(s.substring(7,s.indexOf(",")))).toArray();
                theta = Double.parseDouble(s.substring(s.indexOf(",")+1,s.length()-1));
                symF = Angle.rotate.replica();//new Function("[i+x*cos(theta)-y*sin(theta);j+x*sin(theta)+y*cos(theta)]");
            } else if (s.equals("")){
                continue symCommandsLoop;
            } else throw new IllegalArgumentException("Could not identify symmetry command " +s+ " in " +sym);

        //get original angle arms
            Angle fromMinAngle = new Angle(twoAngles[0]);
            Angle fromMaxAngle = new Angle(twoAngles[1]);
        //find casted over angles
            Angle toMinAngle = Angle.translate(fromMinAngle, theta, symF);
            Angle toMaxAngle = Angle.translate(fromMaxAngle, theta, symF);
        //remove all points in the casted over pie
            Point origin = new Point(centreX, centreY);
/* Technically not neccesary for SqrMap but will be neccesary in other maps
            //Core.log("candidates:");
            for (int y = 0; y < h; y++){
                for (int x = 0; x < w; x++){
                    Angle a = new Angle(new Point(x, y), origin);
                    if (a.isBetween(toMinAngle, toMaxAngle)){
                        grid[x][y] = null;
                        //Core.println((new Point(x, y)).toString());
                    }
                }
            }
*/        //copy over all points from the pie
            for (int y = 0; y < h; y++){
                loop: for (int x = 0; x < w; x++){
                    Point curr = new Point(x, y);
                //check if curr is in zone to be casted over another zone
                    if (!(new Angle(curr, origin)).isBetween(fromMinAngle, fromMaxAngle)) continue loop;
                //translate
                    Point p = Angle.translate(curr, origin, theta, symF);
                //if the new place doesn't exist ~ out of bounds (Would be due to w != h), then don't spawn
                    if(!exists(p)) continue loop;
                //set new point translated
                    grid[p.ix][p.iy] = new Sqr(p.ix, p.iy, grid[curr.ix][curr.iy].getState());
                }
            }
        }
        return true;
    }

/*==============================================================================
                                REPLACE
==============================================================================*/
//Replace parts of map specified by settingstxt
    private boolean replace(){
    //1) Get removal commands
        if (!script.containsKey("Replace")) return false;
        String replace = script.get("Replace");
        if (replace.equals("none")) return false;
        char[] arr = replace.toCharArray();
    //2) Extract commands
        int brace = -1;
        int last = 0;
        ArrayList<String> replaceCommands = new ArrayList<String>(0);
        for(int i = 0; i < arr.length; i++){
            if (arr[i] == '(') brace++;
            else if (arr[i] == ')') brace--;

            if (brace == -1 && arr[i] == ';') {
                replaceCommands.add(replace.substring(last,i));
                last = i+1;
            }
        }
        replaceCommands.add(replace.substring(last,replace.length()));

        System.out.println(replaceCommands);
        //Apply replace commands "replace(0,if(area<0.05*(w*h)?adj:0));replace(1,if(area==1?1:0));"
        replaceCommandsLoop: for(String s : replaceCommands){
        //Get function
            double wantedState;
            Function replaceF;
            if (s.startsWith("replace(")){//replace(0,if(area<0.05*(w*h)?1:0))
                wantedState = Double.parseDouble(s.substring(8,s.indexOf(",")));
                replaceF = new Function(s.substring(s.indexOf(",")+1, s.length()-1));
            } else if (s.equals("")){
                continue replaceCommandsLoop;
            } else throw new IllegalArgumentException("Could not identify replace command '" +s+ "' in " +replace);
            replaceF.addVariable("w", w);
            replaceF.addVariable("h", h);
        //fulfill command
            groupAll();
            ArrayList<Sqr> immuneTrueLeaders = new ArrayList<Sqr>(0);
        //We have been given the command to conform all followers of a trueLeader.
            forAllSqrsLoop: for (Sqr sqr : allSqrs()){
            //if this sqr follows an immune leader, skip it
                if (immuneTrueLeaders.contains(sqr.trueLeader())) continue forAllSqrsLoop;
            //we found a sqr that belongs to the wanted state
                if (sqr.state == wantedState){
            //AREA
                    replaceF.addVariable("area", followersOf(sqr.trueLeader()).size());
            //ADJ: the biggest surrounding group
                //if sqr's state is the highest in procSet, then inradius accessible. Else, displacement
                    ArrayList<Sqr> surroundingSqrs = (sqr.state == Collections.max(procSet)) ? sqr.inRadius(this, 1)
                                                                                             : sqr.inDisplacement(this, 1);
                //list all surroundingTrueLeaders
                    ArrayList<Sqr> surroundingTrueLeaders = new ArrayList<Sqr>(0);
                    for (Sqr surroundingSqr : surroundingSqrs){
                        if (!surroundingTrueLeaders.contains(surroundingSqr.trueLeader()))
                            surroundingTrueLeaders.add(surroundingSqr.trueLeader());
                    }
                //if there are no different surrounding true leaders, then the entire map must be this.
                    if (surroundingTrueLeaders.isEmpty()){
                        drawTopView();
                        Core.freeze("No other groups found, what now?");//--------TODO: Find a solution of what to do here
                        Core.exit("I'm sorry, I can't let you continue like this, Wizard.");
                    }
                //find the trueLeader with the highest area
                    Sqr corporation = surroundingTrueLeaders.get(0);
                    for (Sqr trueLeader : surroundingTrueLeaders){
                        if (followersOf(corporation).size() < followersOf(trueLeader).size()){
                            corporation = trueLeader;
                        }
                    }
                //add variable
                    replaceF.addVariable("adj", corporation.state);
            //NEW STATE
                //get new state
                    double newState = replaceF.f();
                //if the true leader is already what we want it to be, grant it immunity from the new imposed law
                    if (sqr.trueLeader().state == newState){
                        immuneTrueLeaders.add(sqr.trueLeader());
                        continue forAllSqrsLoop;
                    }
                //change state of all followers
                    for (Sqr follower : followersOf(sqr.trueLeader())){
                        follower.state = newState;
                    }
                    groupAll();
                }
            }
        }
        return true;
    }

/*==============================================================================
                                CONNECT
==============================================================================*/
//obsGate drill - Make it so that all states in that map that are of the same state become connected, thus same group
    private boolean connect(){
        return false;
    }

/*==============================================================================
                                GROUPING
==============================================================================*/

//Sorts map into groups of sqrs based on states and accessability between sqrs based on their respective states
//The highest state in the procSet can access all surrounding of same state, all other states can only access up/down/left/right
    private void groupAll(){
        ArrayList<Sqr> allSqrs = allSqrs();
        for (Sqr s : allSqrs)
            s.leader = null;//undo all following
        for (Sqr sqr : allSqrs){
        //Make a list of all surroundingSqrs
            ArrayList<Sqr> surroundingSqrs = (sqr.state == 0) ? sqr.inDisplacement(this, 1) : sqr.inRadius(this, 1);
        //Get list of all different trueLeaders
            ArrayList<Sqr> differentTrueLeaders = new ArrayList<Sqr>(0);
            for (Sqr surroundingSqr : surroundingSqrs){
                if (sqr.state == surroundingSqr.state
                && surroundingSqr.trueLeader() != null
                && !differentTrueLeaders.contains(surroundingSqr.trueLeader())){
                    differentTrueLeaders.add(surroundingSqr.trueLeader());
                }
            }
        //No leaders found, we will become our own leader
            if (differentTrueLeaders.size() == 0){
                sqr.leader = sqr;
            }
        //we found one leader, we will follow them to the end
            else if (differentTrueLeaders.size() == 1){
                for (Sqr surroundingSqr : surroundingSqrs){
                    if (surroundingSqr.trueLeader() == differentTrueLeaders.get(0)){
                        sqr.leader = surroundingSqr;
                    }
                }
            }
        //we found competing leaders but they have the same states, they share the same values,
        //we will be the bridge between their gap
            else if (differentTrueLeaders.size() > 1){
                loop: while(differentTrueLeaders.size() > 1){
                    for (Sqr trueLeader1 : differentTrueLeaders){
                        for (Sqr trueLeader2 : differentTrueLeaders){
                            if (trueLeader1 != trueLeader2){
                            //We will become our own leader
                                sqr.leader = sqr;
                            //and they will follow us
                                trueLeader1.leader = sqr;
                                trueLeader2.leader = sqr;
                            //and thus dethroning them.
                                differentTrueLeaders.remove(trueLeader1);
                                differentTrueLeaders.remove(trueLeader2);
                                continue loop;
                            }
                        }
                    }
                }
                if (!differentTrueLeaders.isEmpty())
                    differentTrueLeaders.get(0).leader = sqr;
            }
        }

        for (Sqr s : allSqrs())
            if (s.leader == null)
                Core.exit("illegal independent sqr! " +s.toString());
    }

//Returns the true followers of the true leader
    public ArrayList<Sqr> followersOf(Sqr trueLeader){
        ArrayList<Sqr> followers = new ArrayList<Sqr>(0);
        for (Sqr sqr : allSqrs()){
            if (sqr.trueLeader() == trueLeader)
                followers.add(sqr);
        }
        return followers;
    }

//return a collection of all different trueLeaders on the map
    private ArrayList<Sqr> allTrueLeaders(){
        ArrayList<Sqr> allTrueLeaders = new ArrayList<Sqr>(0);
        for (Sqr sqr : allSqrs()){
            if (!allTrueLeaders.contains(sqr.trueLeader()))
                allTrueLeaders.add(sqr.trueLeader());
        }
        return allTrueLeaders;
    }

//return a collection of all sqrs in the array
    private ArrayList<Sqr> allSqrs(){
        ArrayList<Sqr> allSqrs = new ArrayList<Sqr>(0);
        for (int y = 0; y < h; y++){
            for (int x = 0; x < w; x++){
                allSqrs.add(grid[x][y]);
            }
        }
        return allSqrs;
    }

/*==============================================================================
                            SUBSCRIPTION
==============================================================================*/


    @Override
    protected void setupTabs(){
//VIEW
    //show
        mainPane.getSubPane("view").addSubPane("show", new Pane(11.5, 3.5, 2.5, 3.5));
        Pane show = mainPane.getSubPane("view").getSubPane("show");//pointer
            show.setListener(new Button(8.5, 6.5-0, 0.5, "../img/view/show.png"));
            show.listener.setSubscriber(this, "view.show()");
        //Groups - tickbox
            show.addTickBox("groups", new TickBox(13.5, 6.5, 0.5, "Show Groups", "left"));
            show.getTickBox("groups").setSubscriber(this, "view.show.groups()");
            show.addTickBox("chokepoints", new TickBox(13.5, 6.5-1, 0.5, "Show Choke-Points", "left"));
            show.getTickBox("chokepoints").setSubscriber(this, "view.show.chokepoints()");
            show.addTickBox("graphics", new TickBox(13.5, 6.5-2, 0.5, "Topview", "left"));
            show.getTickBox("graphics").setSubscriber(this, "view.show.graphics()");
            TickBox g = show.getTickBox("graphics");//pointer-shortcut
                g.tickBox = "../img/view/show/basic.png";
                g.tickBox_T = "../img/view/show/basic_T.png";
                g.tickedBox = "../img/view/show/thinned.png";
                g.tickedBox_T = "../img/view/show/thinned_T.png";
            //show.addButton("voronoi", new Button(12, 3.5, 1, "../img/view/voronoi.png"));
            //show.getButton("voronoi").setSubscriber(this, "view.show.println(voronoi)");

    //versions
        mainPane.getSubPane("view").addSubPane("versions", new Pane(11.5, 3.5, 2.5, 3.5));
        Pane versions = mainPane.getSubPane("view").getSubPane("versions");//pointer
            versions.setListener(new Button(8.5, 6.5-1, 0.5, "../img/view/versions.png"));
            versions.listener.setSubscriber(this, "view.versions()");
        //thumbnails
            Function f = new Function("if(x==13.5?(if(y==1.5?[9.5;6.5]:[9.5;y-1])):[x+1;y])");
            Point curr = new Point(8.5, 6.5);//supposed to be 9.5;6.5 but this is easier for the loop.
            for (String version : saved.keySet()){
            //determine hw and hh of image
                double max = Math.max(w, h);
                double hw = 0.4*(dw)/max;//0.4 would be 0.5 bt we don't want the thumbnails
                double hh = 0.4*(dh)/max;//directly next to each other
            //determine new x and y via f
                curr = new Point(f.getOut("x="+curr.x+";y="+curr.y));
                versions.addThumbnail(version,
                    new Thumbnail(curr.x, curr.y, hw, hh, saved.get(version).getImage().bufferedImage()));
                Thumbnail v = versions.getThumbnail(version);//pointer-shortcut
                    v.sqrMap = saved.get(version);//map the thumbnail represents
                    v.setSubscriber(this, "view.versions.switchout("+curr.toString()+")");
                    v.setHoverSubscriber("view.versions.hover("+curr.toString()+")");
                    v.setUnhoverSubscriber("view.versions.unhover("+curr.toString()+")");
            }

//3D
        Pane threeD = mainPane.getSubPane("threeD");//shortcut
    //Zone - for swiveling
        threeD.addHoverListener("zone", new HoverListener(4, 4, 4, 4));
            threeD.getHoverListener("zone").setSubscriber(this, "threeD.zone()");

//EDITOR
    //------------TODO
    }

/* IMPORTANT NOTICE
    THIS VOID FUNCTION EXISTS
    - TO HIDE ANY CURRENTLY ACTIVE TABS
    - TO SHOW THE NEW TAB
    - TO HIDE ANY CURRENTLY ACTIVE SUBPANES
    - NOT TO SHOW ANY NEW SUBPANES
    - IT DOES UPDATE THE "lastTab" VARIABLE
*/
//update the currently displaying tab and then subpane
    //public void updateTab(Pane newTab, Pane subPane){//view/editor/3D, sub pane i.ie view.show/view.versions
    public void updateTab(Pane newTab, Pane newPane){//newTab = view/editor/3D
    //pointer-shortcuts
        Pane view = mainPane.getSubPane("view");
        Pane editor = mainPane.getSubPane("editor");
        Pane threeD = mainPane.getSubPane("threeD");
    //remove all stuff from last tab manually
        if (lastTab != newTab){
    //remove old tab
            if (lastTab == null){
                //then we will skip to showing the new tab
            }
        //3D
            else if (lastTab == threeD){
                threeD.hide();
                //threeD.getHoverListener("zone").hide();
            }
        //EDITOR
            else if (lastTab == editor){
                //TODO: remove editor stuff//SHOW
/*            //???
                if (lastPane_editor == mainPane.getSubPane("editor").getSubPane("???")){
                }
            //???
                else if (lastPane_view == mainPane.getSubPane("editor").getSubPane("???")){
                }
*/
                editor.hide();
            }
        //VIEW
            else if (lastTab == view){
            //SHOW
                if (lastPane_view == view.getSubPane("show")){
                //nothing yet
                }
            //VERSIONS
                else if (lastPane_view == view.getSubPane("versions")){
                //nothing yet
                }

                view.hide();
            }

    //show new tab
        //3D
            if (newTab == threeD){
                threeD.show();
                //threeD.getHoverListener("zone").show();
                //Core.freeze("drawing 3D");
                if (currTilt != -1 && currRot != -1){
                    threeDDraw(currTilt, currRot);
                } else threeDDraw(30, 45);//draw threeD version rotated 45 degrees and tilted at 30 degree angle
            }
        //EDITOR
            else if (newTab == editor){
            //show pane
                editor.show();
            //display topView with red frame
                drawTopView();
                StdDraw.square(4, 4, 3, StdDraw.RED);
            //run default pane
                //TODO: editor.getSubPane("??").listener.overridePress();
            }
        //VIEW
            else if (newTab == view){
            //show pane
                view.show();
            //Run default pane
                if (lastPane_view == null)
                    view.getSubPane("show").listener.overridePress();//Run show first
                else
                    lastPane_view.listener.overridePress();
            //Display any images
                drawTopView();
            }
        }

    //show new Pane
        if (newPane == null){
            lastTab = newTab;
            return;//then we were just here to start up tabs
        }


    //VIEW
        if (lastTab == view){
            if (lastPane_view != newPane){
                if (lastPane_view == view.getSubPane("versions")){
                //nothing yet
                } else if (lastPane_view == view.getSubPane("show")){
                //nothing yet
                }
                if (lastPane_view != null)
                    lastPane_view.hide();
            }
        }

    //3D
        else if (lastTab == threeD){
            if (lastPane_threeD != newPane){
/*                if (lastPane_threeD == threeD.getSubPane("???")){
                    //???
                } else if (lastPane_threeD == threeD.getSubPane("???")){
                    //???
                }
*/
            }
            if (lastPane_threeD != null)
                lastPane_threeD.hide();
        }

    //EDITOR
        else if (lastTab == editor){
            if (lastPane_editor != newPane){
/*                if (lastPane_editor == editor.getSubPane("???")){
                    //???
                } else if (lastPane_editor == editor.getSubPane("???")){
                    //???
                }
*/
            }
            if (lastPane_editor != null)
                lastPane_editor.hide();
        }
        lastTab = newTab;
        return;
    }


/*==============================================================================
                                VIEW
==============================================================================*/
/* This pane let's us view certain aspects of the map
It has buttons we can toggle. Once we toggle a button, it will update
*/

//launch the view menu, display topview of map in 2d form. Show buttons
    @Override
    protected void view(){
        Core.log("view()");//Core.println(" SqrMap.view()");
        Pane viewPane = mainPane.getSubPane("view"); //pointer-shortcut
    //remove any other panes and show this one
        updateTab(viewPane, null);
    }

/* VIEW SubTasks:
    show()
    show.groups() - show the map in it's groups
        .chokepoints() - show the chokepoints identified in the map

    versions() - open the versions pane
    versions.hover([x;y]) - display the name of the hovered over thumbnail
            .unhover([x;y]) - remove the name of the unhovered thumbnail
            .switchout([x;y]) - trade the variables of this map and the thumbnail's map with those coordinates

    graphics()
    graphics.thinnedview() - show thinned version TODO
            .basicview() - call on basicTopView
*/

    @Override
    public void view(String subTask){//show() or show.function(parameter) or show.something.function(parameter)
        //Core.println(" SqrMap.view().subtask:"+subTask);//remove any other panes
        Core.log(subTask);

    //determine pane (before brackets or any dots) //lastPaneName = "versions" or "show" etc
        String lastPaneName = subTask.contains(".") ? subTask.substring(0, subTask.indexOf("."))
                                                    : subTask.substring(0, subTask.indexOf("("));
        Pane view = mainPane.getSubPane("view");//pointer-shortcut
        Pane subPane = view.getSubPane(lastPaneName);//pane = versions or show etc
        if (subPane == null) Core.exit("pane " +lastPaneName+ " DOES NOT EXIST");//doesn't exist
    //remove prev
        updateTab(view, subPane); //updateTab has made the environment safe, we may now display as we please.
    //show new
        lastPane_view = subPane;
        lastPane_view.show();
    //tasks
        String task = subTask;///task = string that we will be thining and thining until we find the task // versions()
        if (task.contains(".")) task = task.substring(task.indexOf(".")+1, task.length());//task = hover(param);

    //VERSIONS
        if (lastPaneName.equals("versions")){
            //Core.log("  versions selected with task " +task);
            Pane versions = lastPane_view;//pointer-shortcut
        //task = versions()
            if (task.equals("versions()")){
            //get updated version of saved map images
            }
        //task = hover([x;y]) - display the name of the hovered over thumbnail
            else if (task.startsWith("hover(")){
                Point p = new Point(task.substring(6, task.length()-1));
                for (String s : versions.thumbnails.keySet()){
                    Thumbnail t = versions.getThumbnail(s);
                    if (t.x == p.x && t.y == p.y){
                        StdDraw.filledRectangle(11.5, 0.5, 1.49, 0.49, StdDraw.ATOM);
                        StdDraw.text(11.5, 0.5, s, StdDraw.WHITE);
                        double x = t.x, y = t.y, hw = t.hw, hh = t.hh;
                        StdDraw.polygon(new double[]{x-hw, x-hw, x+hw-0.01, x+hw-0.01},
                                        new double[]{y-hh+0.01, y+hh, y+hw, y-hh+0.01}, t.hoverColor());
                    }
                }
            }
        //task = unhover([x;y]) - remove the name of the unhovered thumbnail
            else if (task.startsWith("unhover(")){
                Point p = new Point(task.substring(8, task.length()-1));
                for (String s : versions.thumbnails.keySet()){
                    Thumbnail t = versions.getThumbnail(s);
                    if (t.x == p.x && t.y == p.y){
                        StdDraw.filledRectangle(11.5, 0.5, 1.49, 0.49, StdDraw.ATOM);
                    }
                }
            }
        //task = switchout([x;y]) - trade the variables of this map and the thumbnail's map with those coordinates
            else if (task.startsWith("switchout(")){
                Point p = new Point(task.substring(10, task.length()-1));
                Core.log("switchout RECEIVED "+p.toString());
            //find thumbnail with those x y coordinates
                loop: for (String s : versions.thumbnails.keySet()){
                    Thumbnail t = versions.getThumbnail(s);
                    if (t.x == p.x && t.y == p.y){
                        this.syncTo(t.sqrMap);
                        drawTopView();
                        break loop;
                    }
                }
            }


        //do task
            if (task.startsWith("println(")){
                Core.println(task.substring(task.indexOf("(")+1, task.length()-1));
            }
        }

    //SHOW
      //display checkboxes to show extra content ie groups etc
        else if (lastPaneName.equals("show")){
            //Core.log("  show selected with task " +task);
            Pane show = mainPane.getSubPane("view").getSubPane("show");
        //display tickboxes on side
            //groups
                if (task.equals("groups()")){
                    show.getTickBox("groups").tick();
                }
            //chokepoints
                else if (task.equals("chokepoints()")){
                    show.getTickBox("chokepoints").tick();
                }
            //graphics
                else if (task.equals("graphics()")){
                    show.getTickBox("graphics").tick();
                    if (show.getTickBox("graphics").ticked())
                        topView = "thinned";
                    else
                        topView = "basic";
                }
                drawTopView();
        //do task
            if (task.startsWith("println(")){
                Core.println(task.substring(task.indexOf("(")+1, task.length()-1));
            }
        //do any subtasks
            if (task.contains(".")){
                Core.exit("show subtask requested!");
                task = task.substring(task.indexOf(".")+1, task.length());
                if (task.startsWith("println(")){
                    Core.println(task.substring(task.indexOf("("), task.length()-1));
                }
            }
        }
    }
    private Pane lastPane_view = null;

/*==============================================================================
                                3D
==============================================================================*/
//launch the view menu, display topview of map in 2d form. Show buttons
    @Override
    protected void threeD(){
        Core.println(" SqrMap.threeD()");
        Pane threeDPane = mainPane.getSubPane("threeD"); //pointer-shortcut
    //remove any other panes
        updateTab(threeDPane, null);
    }



    @Override
    public void threeD(String subTask){
        Core.log(" "+subTask);

        //determine pane (before brackets or any dots) //lastPaneName = "versions" or "show" etc
        String lastPaneName = subTask.contains(".") ? subTask.substring(0, subTask.indexOf("."))
                                                    : subTask.substring(0, subTask.indexOf("("));
                                                    Core.log(subTask);
        Pane threeD = mainPane.getSubPane("threeD");//pointer-shortcut
        Pane subPane = threeD.getSubPane(lastPaneName);//pane = versions or show etc
        //REMOVE PREV
        updateTab(threeD, subPane);

    //SHOW NEW
        lastPane_threeD = subPane;
        if (subPane != null) lastPane_threeD.show();

    //TASKS
        String task = subTask;///task = string that we will be thining and thining until we find the task // versions()
        if (task.contains(".")) task = task.substring(task.indexOf(".")+1, task.length());//task = hover(param);
    //ZONE
        if (lastPaneName.equals("zone")){
            Core.log("  " +task);
        //we must track the mouse's movements and rotate accordingly
            long time = System.currentTimeMillis();
            while (threeD.getHoverListener("zone").hover(StdDraw.mouseX(), StdDraw.mouseY())){
                Point curr = StdDraw.mouseLoc();
                if (currTilt == -1 || currRot == -1){
                    Core.exit("3D hasn't been drawn yet");
                }
                while (System.currentTimeMillis()-time > 3000){
                    threeDDraw(currTilt, currRot+0.1);
                    Point next = StdDraw.mouseLoc();
                    if (StdDraw.mousePressed() || !curr.equals(next))
                        time = System.currentTimeMillis();
                }
                while (StdDraw.mousePressed()){
                //record the original click
                //get the new click
                    Point next = StdDraw.mouseLoc();
                //if old and new different, show difference
                    if (!curr.equals(next)){//ADD CAPS TODO
                    //determine tilt - compare y change
                        double yDiff = next.y-curr.y;//positive = upwards
                    //6 units = 90 degrees, thus 1:15degrees
                        double tiltDiff = yDiff*15;
                        double tilt = currTilt+tiltDiff;
                    //determine rot - compare x change
                        double xDiff = next.x-curr.x;//positive = rightwards
                    //change of 6 units on screen = change of 180 degrees = 1:30degrees
                        double rotDiff = xDiff*30;//degrees change
                        double rot = currRot+rotDiff;
                    //draw new
                        threeDDraw(tilt, rot);
                    }
                    curr = next;//update curr
                    time = System.currentTimeMillis();
                }
            }
            Core.log("  exiting zone");
        }
    }
    private Pane lastPane_threeD = null;

    private double currTilt = -1;
    private double currRot = -1;

//draw the map into 3D form
    protected void threeDDraw(double tilt, double rot){//tilt degrees, rotation degrees
        long start = System.currentTimeMillis();
        StdDraw.filledRectangle(4, 4, 3.95, 3.95, Run.background);//clear pane
//1) get/regulate parameters
        ArrayList<Sqr> allSqrs = allSqrs();
        if (rot < 0) rot += 360;
        if (360 <= rot) rot -= 360;
        currRot = rot;
        tilt = Math.min(tilt, 90);
        tilt = Math.max(tilt, 0);
        currTilt = tilt;
//2) Get o & l
    //O = origin//absolute centre
        Point o = new Point(centreX, centreY);
    //l = multiplier by which we can multiply point distances to get their display coordinates
        double l = 3.0/o.dist(new Point(w, h));
//3) Get corner points list (set as "poly")
        ArrayList<Point[]> polys = new ArrayList<>(0);
        HashMap<Point[], Sqr> OG = new HashMap<>();// new polygon, original Sqr
        for (Sqr s : allSqrs){//clockwise order from bottom left
            polys.add(new Point[]{  new Point(s.x-0.5, s.y-0.5),//SW
                                    new Point(s.x-0.5, s.y+0.5),//NW
                                    new Point(s.x+0.5, s.y+0.5),//NE
                                    new Point(s.x+0.5, s.y-0.5)});//SE
            OG.put(polys.get(polys.size()-1), s);
        }
    //sortMe = map of all sqrs with their position (part of 8)
        HashMap<Point[], Double> sortMe = new HashMap<>();
        HashMap<Point[], Point[]> oGCorners = new HashMap<>();//array mapping of display corner locations and order locations
        HashMap<Point[], Sqr> originalSqrs = new HashMap<>();
        double cosTilt = Core.cos(tilt);
//4) Define list points as relative to o and then multiply with l
        for (Point[] poly : polys){
            ArrayList<Point> sqr = new ArrayList<>(0);
            ArrayList<Point> list = new ArrayList<>(0);//the sqr's corners based on height
            double max = 0;
            for (Point p : poly){
            //Define relative to o
                p.setxy(p.x-o.x, p.y-o.y);//now they are relative to origin(0, 0)
            //multiply with l
                p.setxy(p.x*l, p.y*l);//now when we multiply them, we are just scaling them
//5) Rotate
                p = Angle.rotate(p, new Point(0, 0), rot);//rotate about origin

            //before tilting, add the height to list
                list.add(new Point(p.x, p.y));
                max += p.y;
//6) Tilt
                if (tilt != 0) p.setxy(p.x, p.y*cosTilt);
//7) Get display height
            //get the display positions
                p.setxy(p.x+4, p.y+4);//origin
            //in case something happens
                if (p.x < 1 || 7 < p.x || p.y < 1 || 7 < p.y) Core.exit(p.toString());

                sqr.add(p);
            }
        //get corners into array
            Point[] corners = new Point[4];
            for (int i = 0; i < 4; i++) corners[i] = sqr.get(i);
        //convert sqrOGCorners as well
            //sqrOGCorners.put(sqr, list);
            Point[] arr = new Point[4];
            for (int i = 0; i < 4; i++) arr[i] = list.get(i);
            oGCorners.put(corners, arr);

        //add to hashMap to be sorted
            max /= 4;
            sortMe.put(corners, max);
        //remember sqr
            originalSqrs.put(corners, OG.get(poly));
        }

//8) Sort
        ArrayList<Point[]> sortedSqrs = Core.sort(sortMe, "max");

//9) TerrainHeights
    //get highest terrainHeight
        double maxHeight = allSqrs.get(0).state;//allSqrs is not a mistake
        for (Sqr s : allSqrs){
            maxHeight = Math.max(maxHeight, s.state);
            //Core.println(s.state+"");
        }
    //if that terrainHeight exceeds sqrt(7), then cap all terrain heights at Sqrt(7) ~ 2.645
        double terrainHeight = (maxHeight > 2.645) ? 2.645/maxHeight : 1;
        //double terrainHeight = 1;
        //Core.println(maxHeight+"");

        terrainHeight *= l*Core.sin(tilt);
//10) Setup polygons to draw
    //Add all polygons to list of what must be drawn
        Sqr lowestSqr = originalSqrs.get(sortedSqrs.get(sortedSqrs.size()-1));
        loop:
        for (Point[] sqr : sortedSqrs) {
            Sqr originalSqr = originalSqrs.get(sqr);
        //TOP
            Point[] top = new Point[4];
            for (int i = 0; i < 4; i++){
                top[i] = new Point(sqr[i].x, sqr[i].y+terrainHeight*originalSqrs.get(sqr).state);
            }
            Color color = originalSqr.getColor();
            StdDraw.filledPolygon(top, color);
            StdDraw.polygon(top, StdDraw.ATOM);
            if (originalSqr.getState() == 0.0) continue loop;
        //draw front sides
            Point[] bottom = sqr;//for clearance
        //find most front corner, draw all sides that are connected to that corner
            int front = 0;//front = index of the front corner
            for (int i = 0; i < 4; i++){
                front = oGCorners.get(sqr)[i].y > oGCorners.get(sqr)[front].y ? front : i;
            }
        //draw all sides that are connected to front

            boolean optimization = false;//optimization rate ~330, unoptimized ~230
            rightSide:
            if (optimization) {
            //RIGHT
                int right = (front == 0) ? 3 : front-1; //determine right index
                boolean bool = false;
                boolIf:
                if (originalSqr.y == lowestSqr.y) bool = true;
                //^  originalSqr.y == lowestSqr.y) bool = true;
                else {
                //get list of sqrs adj to originalSqr
                    ArrayList<Sqr> adj = originalSqr.inDisplacement(this, 1);
                //get thos sqr's point[] and iterate over
                    for (Point[] poly : originalSqrs.keySet()) {
                        // if this poly's original sqr is adj to this sqr
                        Sqr s = originalSqrs.get(poly);
                        if (adj.contains(s)) {
                            // then we must evaluate if the adj sqr's poly has the corners we are looking for
                            int sharedCorners = 0;
                            for (Point p : poly) {
                                if (p.equals(bottom[front]) || p.equals(bottom[right])) {
                                    sharedCorners++;
                                }
                                if (sharedCorners == 2) {
                                    // then this sqr is next to us, we need only draw the right side if we are higher
                                    if (s.getState() < originalSqr.getState()) bool = true;
                                    break boolIf;
                                }
                            }
                        }
                    }
                }
                if (bool) {
                    Point[] rightSide = new Point[]{
                        bottom[front],
                        top[front],
                        top[right],
                        bottom[right]};
                    StdDraw.filledPolygon(rightSide, color);
                    StdDraw.polygon(rightSide, StdDraw.ATOM);
                    // we are done with settling this side.
                    //break rightSide;
                }
            }
            else {
            //RIGHT
                int right = (front == 0) ? 3 : front-1; //determine right index
            //construct polygon using corners
                Point[] rightSide = new Point[]{
                    bottom[front],
                    top[front],
                    top[right],
                    bottom[right]};
                StdDraw.filledPolygon(rightSide, color);
                StdDraw.polygon(rightSide, StdDraw.ATOM);

            }



            leftSide:
            if (optimization) {
            //RIGHT
                int left = (front == 3) ? 0 : front+1;//determine left index
            //get list of sqrs adj to originalSqr
                ArrayList<Sqr> adj = originalSqr.inDisplacement(this, 1);
            //get thos sqr's point[] and iterate over
                for (Point[] poly : originalSqrs.keySet()) {
                    Sqr s = originalSqrs.get(poly);
                    // if this poly's original sqr is adj to this sqr
                    if (adj.contains(s)) {
                        // then we must evaluate if the adj sqr's poly has the corners we are looking for
                        int sharedCorners = 0;
                        for (Point p : poly) {
                            if (p.equals(bottom[front]) || p.equals(bottom[left])) {
                                sharedCorners++;
                            }
                            if (sharedCorners == 2) {
                                // then this sqr is next to us, we need only draw the right side if we are higher
                                if (s.getState() < originalSqr.getState()) {
                                    // construct polygon using corners
                                    Point[] leftSide = new Point[]{
                                        bottom[front],
                                        top[front],
                                        top[left],
                                        bottom[left]};
                                    StdDraw.filledPolygon(leftSide, color);
                                    StdDraw.polygon(leftSide, StdDraw.ATOM);
                                    // we are done with settling this side.
                                    break leftSide;
                                }
                            }
                        }
                    }
                }
            }
            else {
            //LEFT
                int left = (front == 3) ? 0 : front+1;//determine left index
            //construct polygon using corners
                Point[] leftSide = new Point[]{
                    bottom[front],
                    top[front],
                    top[left],
                    bottom[left]};
                StdDraw.filledPolygon(leftSide, color);
                StdDraw.polygon(leftSide, StdDraw.ATOM);
            }

        }
        double frameRate = System.currentTimeMillis() - start;
        frameRates.add(frameRate);
        double frameRate_ave = 0;
        int frameRate_n = 0;
        for (int i = frameRates.size()-1; i >= 0 && i >= frameRates.size()-50; i--, frameRate_n++) {
            frameRate_ave += frameRates.get(i);
        }
        frameRate_ave /= frameRate_n;
        StdDraw.textLeft(1, 0.2, frameRate+" ~" +frameRate_ave, StdDraw.WHITE);
        StdDraw.textLeft(4, 0.2, "fps"+(1000d/frameRate), StdDraw.WHITE);
        StdDraw.show();
    }
    //private double frameRate_ave = 0;
    //private double frameRate_n = 0;
    private ArrayList<Double> frameRates = new ArrayList<>(0);


    @Override
    protected void editor(){
        Core.println(" SqrMap.editor()");
        Pane editorPane = mainPane.getSubPane("editor"); //pointer-shortcut
    //remove any other panes
        updateTab(editorPane, null);
/*
    //display topView with red frame
        drawTopView();
        StdDraw.square(4, 4, 3, StdDraw.RED);
    //show pane
        editorPane.show();
    //run default pane
        //TODO: viewPane.getSubPane("??").listener.overridePress();
*/
    }

    @Override
    public void editor(String subTask){
        Core.println(" SqrMap.editor().subtask:"+subTask);
/*    //get task name / task without brackets at the end
        String taskName = subTask;
        if (subTask.contains(".")) taskName = subTask.substring(0, subTask.indexOf("."));
        taskName = taskName.substring(0, subTask.length()-2);
        Pane pane = mainPane.getSubPane("editor").getSubPane(taskName);
        if (pane == null) return;
    //remove any previous subTasks
        if (lastPane_editor != null && lastPane_editor != pane) lastPane_editor.hide();
        lastPane_editor = pane;
    //TODO: find some actions
        if (subTask.equals("")){
            mainPane.getSubPane("editor").getSubPane("").show();
        }

    //make sure to de activate any other subpanes of view
        for (String s : mainPane.getSubPane("editor").subPanes.keySet()){
            if (!s.equals(taskName)){//subTask.substring(0, subTask.length()-2)
                mainPane.getSubPane("editor").getSubPane(s).listener.show();
            }
        }
*/    }
    private Pane lastPane_editor = null;

/*==============================================================================
                                TOP VIEW
==============================================================================*/
    private String topView = "basic";
    @Override
    protected void drawTopView(){
    //Basic top view
        if (topView.equals("basic")){
            if (lastTab != null
            && lastTab == mainPane.getSubPane("view")
            && mainPane.getSubPane("view").getSubPane("show").tickBoxes.containsKey("groups")
            && mainPane.getSubPane("view").getSubPane("show").getTickBox("groups").ticked()){ //show groups
            //get all groups
                groupAll();
                HashMap<Sqr, ArrayList<Sqr>> allGroups = new HashMap<>();
                for (Sqr s : allSqrs()){
                    Sqr trueLeader = s.trueLeader();
                    if (allGroups.containsKey(trueLeader)){
                        allGroups.get(trueLeader).add(s);
                    } else {
                        allGroups.put(trueLeader, new ArrayList<>(0));
                        allGroups.get(trueLeader).add(s);
                    }
                }
            //paint map based on group
                for (ArrayList<Sqr> followers : allGroups.values()){
                    Color color = StdDraw.randomColor();
                    for (Sqr follower : followers){
                        Point p = getDisplayLoc(new Point(follower.x, follower.y));
                        StdDraw.filledSquare(p.x, p.y, 3.0/Math.max(w,h), color);
                    }
                }
                StdDraw.show();
                return;
            }
        //draw top view in box
            double max = Math.max(w,h);
            double xOffset = 1.0, yOffset = 1.0;
            if (w != h){
                if (w < h) xOffset = 4-3*(w/max);
                else yOffset = 4-3*(h/max);
            }
            double multiplier = 6.0/max, halfLength = 3.0/max;

            for (int x = 0; x < w; x++){
                for (int y = 0; y < h; y++){
                    if (grid[x][y] == null) throw new NullPointerException(x +", "+y+ " undefined");
                    StdDraw.filledSquare(xOffset + multiplier*x + halfLength,
                                         yOffset + multiplier*y + halfLength, halfLength, grid[x][y].getColor());
                }
            }
            StdDraw.show();
        }
    //Thinned top view
        else if (topView.equals("thinned")){
        //draw top view in box
            double max = Math.max(w,h);
            double xOffset = 1.0, yOffset = 1.0;
            if (w != h){
                if (w < h) xOffset = 4-3*(w/max);
                else yOffset = 4-3*(h/max);
            }
            double multiplier = 6.0/max, halfLength = 3.0/max;
            Color plain = (new Sqr(0, 0, 0)).getColor();
            for (int x = 0; x < w; x++){
                for (int y = 0; y < h; y++){
                    if (grid[x][y] == null) throw new NullPointerException(x +", "+y+ " undefined");
                    double dx = xOffset + multiplier*x + halfLength;
                    double dy = yOffset + multiplier*y + halfLength;
                    StdDraw.filledSquare(dx, dy, halfLength, plain);
                    if (grid[x][y].state == 1){
                        for (Sqr s : grid[x][y].inRadius(this, 1)){
                            if (s.state == 1){
                                StdDraw.line(new Point(dx, dy), getDisplayLoc(new Point(s.x, s.y)), StdDraw.BLACK);
                            }
                        }
                    }
                }
            }
            StdDraw.show();
        }
    }

//highlight the given sqr's position on the map
    private void highlightSqr(Sqr s){
        Point p = getDisplayLoc(new Point(s.x, s.y));
        StdDraw.square(p.x, p.y, 3.0/Math.max(w,h), StdDraw.RED);
    }

    private Point getDisplayLoc(Point p){
        double max = Math.max(w,h);
        double xOffset = 1.0, yOffset = 1.0;
        if (w != h){
            if (w < h) xOffset = 4-3*(w/max);
            else yOffset = 4-3*(h/max);
        }
        double multiplier = 6.0/max, halfLength = 3.0/max;
        return new Point(xOffset + multiplier*p.x + halfLength, yOffset + multiplier*p.y + halfLength);
    }

//returns the current map as a picture
    public Picture getImage(){
        double max = Math.max(w,h);
        Picture image = new Picture((int)((dw/max)*600), (int)((dh/max)*600));
        int pixlPerX = (int)((double)image.width()/dw);
        int pixlPerY = (int)((double)image.height()/dh);
        for (int x = 0; x < w; x++){
            for (int y = 0; y < h; y++){
                int minX = x*pixlPerX, maxX = (x+1)*pixlPerX, minY = y*pixlPerY, maxY = (y+1)*pixlPerY;
                Color color = grid[x][y].getColor();
                for (int pixlX = minX; pixlX < maxX; pixlX++){
                    for (int pixlY = minY; pixlY < maxY; pixlY++){
                        image.set(pixlX, pixlY, color);
                    }
                }
            }
        }
        return image;
    }
}
