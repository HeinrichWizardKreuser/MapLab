/* TODO

ACTUAL TODOs:
 - Import from .map file


 Types:
 1) 2D Sqrs
 2) 2D Hexagons
 3) 3D Globe

 forEach:
 1) VIEW
 2) EDITOR
 3) 3D

Names
 Map generation (procedural)
 Map EDITOR
 map viewer

 mapGeditor ~Pro
 MG6
 MapLab
*/

import java.util.*;

public abstract class Map implements Subscriber{

    public HashMap<String, String> script = null;
    public ArrayList<Double> procSet;

//Abstract Constructor, sets up data Script
    public Map(HashMap<String, String> script){
        this.script = script;

    //unpack procSet
        if (script.containsKey("ProcSet")){
            procSet = new ArrayList<Double>(0);
            String[] procs = script.get("ProcSet").substring(1, script.get("ProcSet").length()-1).split(";");
            for (String proc : procs){
                procSet.add(Double.parseDouble(proc));
            }
        }
    }



//The pane that holds the tabs and buttons at top left applcable to all map types
    public Pane mainPane;

//Setup mainPane and all underlying buttons
    public void setupPanes(){
        mainPane = new Pane(Run.width-3, Run.height-0.5, 3, 0.5);

//Buttons
    //exit
        mainPane.addButton("exit", new Button(mainPane.right().x-0.5, mainPane.right().y, 0.5, "../img/exit.png"));
        mainPane.getButton("exit").setSubscriber(this, "exit()");
    //newMap
        mainPane.addButton("newMap", new Button(mainPane.left().x+0.5, mainPane.left().y, 0.5, "../img/newMap.png"));
        mainPane.getButton("newMap").setSubscriber(this, "newMap()");
    //save as file or image
        mainPane.addSubPane("save", new Pane(13, 7.5, 1, 0.5));
        Pane save = mainPane.getSubPane("save");//pointer-shortcut
            save.listener = new Button(12.5, 8.5, 0.5, "../img/save.png");
            save.listener.setSubscriber(this, "save()");
            save.addButton("image", new Button(13.5, 7.5, 0.5, "../img/save/image.png"));
            save.addButton("file", new Button(13.5-1, 7.5, 0.5, "../img/save/file.png"));
            save.getButton("image").setSubscriber(this, "save.image()");
            save.getButton("file").setSubscriber(this, "save.file()");

//SubPanes
    //EDITOR
        mainPane.addSubPane("editor", new Pane(4, 4, 4, 4));
        mainPane.getSubPane("editor").listener = new Button(1.33, 8.25, 1.3, 0.25, "EDITOR");
    //VIEW
        mainPane.addSubPane("view", new Pane(4, 4, 4, 4));
        mainPane.getSubPane("view").listener = new Button(4, 8.25, 1.3, 0.25, "VIEW");
    //3D
        mainPane.addSubPane("threeD", new Pane(4, 4, 4, 4));
        mainPane.getSubPane("threeD").listener = new Button(6.66, 8.25, 1.3, 0.25, "3D");

    //Set subscribers
        for (String tabName : mainPane.subPanes.keySet()){
            mainPane.getSubPane(tabName).listener.setSubscriber(this, tabName+"()");//set subscriber
        }
        setupTabs();

    //Make that all buttons and panes show
        mainPane.show();
        mainPane.getSubPane("view").listener.overridePress();
    }

/*
*/

/*==============================================================================
                            PANES
==============================================================================*/

    //public abstract void syncTo(Map map);
//EDITOR
    protected abstract void editor();
    public abstract void editor(String subTask);
//VIEW
    protected abstract void view();
    public abstract void view(String subTask);
    protected abstract void drawTopView();
//3D
    protected abstract void threeD();
    public abstract void threeD(String subTask);
    protected abstract void threeDDraw(double degrees, double rotation);

    protected abstract void setupTabs();

/*==============================================================================
                            SAVE & IMAGE
==============================================================================*/
//called when we want to save
    public void saveMenu(){
        Pane save = mainPane.getSubPane("save");//pointer-shortcut
        if (saveMenu){
            save.hide();
            saveMenu = false;
        } else if (!saveMenu) {
            save.show();
            saveMenu = true;
            save.listener.show();
        }
    }
    private boolean saveMenu = false;
//save as image or file
    public void save(String subTask){//subTask = image() or file() or image(name) or file(name);
        if (!subTask.contains(".")){//then is final task
            String name = null;
            if (subTask.indexOf("(")+1 != subTask.indexOf(")")){//if there is input
                name = subTask.substring(subTask.indexOf("(")+1, subTask.indexOf(")"));
            }
            if (subTask.contains("image")){
                Picture image = getImage();
                if (name == null){//do selection dialog
                    image.saveDialog();
                } else {//just save with file name
                    if (!name.endsWith(".png") && !name.endsWith(".jpg")) name += ".png";
                    image.save(name);
                }
            } else if (subTask.contains("file")){
                Core.log("WARNING: PREMATURE REQUEST TO SAVE AS FILE");
                //saveFile();
                return;
            }
        }
    }
// Save map as a file
    public void saveFile(){
        Core.log("saveFile");
        Core.exit("premature request of saveFile()");
    }

    public abstract Picture getImage();

/*==============================================================================
                            ACTIONS
==============================================================================*/
//Any notification we my get will come here
    @Override
    public void notify(String notification){
        Core.log(this.getClass()+ " notified of " +notification);
        String s = notification;
    //basic
        if (s.equals("exit()")) System.exit(0);
        if (s.equals("newMap()")) Run.newMap();
        if (s.equals("save()")) saveMenu();
        if (s.startsWith("println(")) Core.println(s.substring(8, s.length()-1));
    //Tabs
        if (s.equals("view()")){
            view();
        } else if (s.equals("editor()")){
            editor();
        } else if (s.equals("threeD()")){
            threeD();
        }
    //sub tab tasks
        String subTask = s.substring(s.indexOf(".")+1, s.length());
        if (s.startsWith("view.")){
            view(subTask);
        } else if (s.startsWith("editor.")){
            editor(subTask);
        } else if (s.startsWith("threeD.")){
            threeD(subTask);
        } else if (s.startsWith("save.")){
            save(subTask);
        }
    }
    protected Pane lastTab;

/*==============================================================================
                            VORONOI
==============================================================================*/
}
