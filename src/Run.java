// Runs the program from start to finish. Main operator

/* Software PsuedoThread Structure

- setup StdDraw GUI
- create new map from script
    (create 3panes <view, 3D, editor> + 2buttons <exit, newMap> )
    add the 3panes' listeners and the 2buttons' as tasks to the tasks list
- handles each task individually until newMap() is notified

*/

import java.awt.*;
import java.io.*;
import java.util.*;

public class Run {
/*Green color of icons :
HEX: #91DC5A
*/

//UI
    public static final Color background = StdDraw.ATOM;
    public static final int height = 9;
    public static final int width = 14;
    public static final int pixels = 100;

    public static void main(String[] args){
    /*
        ArrayList<String> list = new ArrayList<>(0);
        list.add("st1");
        list.add("st2");
        for (String s : list){
            list.remove(s);
        }
    */
        StdDraw.setTitle("MapLab Pro v6");
        StdDraw.enableDoubleBuffering();
        StdDraw.setCanvasSize(pixels*width, pixels*height);
        StdDraw.setXscale(0, width);
        StdDraw.setYscale(0, height);
    //Run program until done
        while (true){
            Core.println("newMap");
            //subscriber = new Run();
            StdDraw.clear(StdDraw.ATOM);
            tasks = new ArrayList<>(0);
        //Create new map
            HashMap<String, String> script = script();
            String s = script.get("MapType");
            if (s.equals("SqrMap")) map = new SqrMap(script);
            //else if (s.equals("HexMap")) map = new HexMap(script);
            //else if (s.equals("LineMap")) map = new LineMap(script);
            //else if (s.equals("VoronoiMap")) map = new VoronoiMap(script);
            //else if (s.equals("GlobeMap")) map = new GlobeMap(script);
        //Setup tasks
            map.setupPanes();
        //Run tasks until next new map
            //listTasks();
            while (!tasksDone()){
                for (Clicker task : tasks()){
                    task.updateHovering();
                    if (task.isPressed()) task.ping();
                }
                //listTasks();
            }
        }
    }

    private static boolean newMap = false;

/*==============================================================================
                            RUNNING TASKS
==============================================================================*/

    public static void newMap(){
        newMap = true;
    }

//all Clickers that need be checked for updates
    private static ArrayList<Clicker> tasks;
//returns an updated version of tasks
    private static ArrayList<Clicker> tasks(){
        ArrayList<Clicker> updatedTasks = new ArrayList<>(0);
        for (Clicker task : tasks) {
            updatedTasks.add(task);
        }
        return updatedTasks;
    }

    public static boolean tasksDone(){
        if (newMap) {
            newMap = false;
            return Core.True("newMap == true");
        }
        return false;
    }

    public static void addTask(Clicker task){
        if (tasks.contains(task)){
            Core.log("task overflow request for " +task.image);
            return;
        }
        //Core.log("adding "+ task.image);
        tasks.add(task);
    }
    public static void removeTask(Clicker task){
        //Core.freeze("removing "+ task.image);
        tasks.remove(task);
    }
    public static boolean contains(Clicker task){
        return tasks.contains(task);
    }

//lists all of the current tasks
    public static void listTasks(){
        Core.println("tasks:");
        for (Clicker task : Run.tasks){
            Core.println(task.image);
        }
        Core.println("");
    }

    public static Map map = null;


/*==============================================================================
                                SCRIPT
==============================================================================*/
//Returns the settings for this session
    public static HashMap<String, String> script(){
    //extract settings
        ArrayList<String> settingstxt = new ArrayList<>(0);
        try {
            Scanner reader = new Scanner(new File("../settings/settings.txt"));
            while(reader.hasNextLine()){
                settingstxt.add(reader.nextLine());
            }
        } catch (Exception e){
            PrintWriter writer = null;
            try {
                writer = new PrintWriter(new File("../settings/settings.txt"));
            } catch (Exception e2){}
            writer.println("MapType: SqrMap"); //SqrMap, HexMap, GlobeMap
            writer.println("Dimensions: 20x20");//SqrMap: 0 - infinity; HexMap
            writer.println("ProcDirection: if(x<w-1?[x+1;y]:[0;y+1])");
            writer.println("ProcStart: [0;0]");
            writer.println("FirstState: 0");
            writer.println("ProcEnd: [w-1;h-1]");
            writer.println("ProcAlgorithm: if(adj==0?(if(random>0.83?1:0)):(if(random>0.66?0:1)))");
            writer.close();
        }

    //setup script mapping
        HashMap<String, String> script = new HashMap<>();
        settingstxtLoop: for (String line : settingstxt){
            if (line.startsWith("//")) continue settingstxtLoop;
            split: for (int i = 0; i < line.length(); i++){
                if(line.charAt(i) == ':' && line.charAt(i+1) == ' '){
                    script.put(line.substring(0,i), line.substring(i+2,line.length()));
                    break split;
                }
            }
        }
        return script;
    }

/*==============================================================================
                            SUBSCRIPTION
==============================================================================*/
    //public static Run subscriber;
/*
    @Override
    public void notify(String notification){
        Core.log(this.getClass()+ " notified of " +notification);
        String s = notification;
        if (s.equals("exit()")) System.exit(0);
        if (s.equals("newMap()")) Run.newMap();
        if (s.equals("save()")) map.saveMenu();
    //sub tasks
        String subTask = s.substring(s.indexOf(".")+1, s.length());
        if (s.startsWith("save.")){
            map.save(subTask);
        }
    }
*/
}
