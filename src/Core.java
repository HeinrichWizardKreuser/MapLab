/*Welcome to the KreuserCore Engine v3.

	This Class provides us with some shortcuts to widely used methods for efficiency
	When executing the main, we may create a new .java file.
*/


import java.util.*;
import java.io.*;
import java.text.*;
//for sorting
import java.util.Map;
import static java.util.stream.Collectors.*;
import static java.util.Map.Entry.*;

public class Core {
	private static boolean active = true;
	public static boolean active() { return active; }
	public static void on() {active = true;}
	public static void off(){active = false;}

	public static void Log(String message) { log(message); }
	public static void log(String message) { if (active) System.out.println(now()+ " " + message); }

	public static void Exit(String message) { exit(message); }
	public static void exit(String message){
		System.out.println(now()+ " ERROR: " + message);
		try{Thread.sleep(2000);}catch(Exception e){}
		System.exit(0);
	}
	public static void exit() { exit(""); }

//Crashes the program for some stack traces when error finding becomes difficult
	public static void crash(){
		int[] i = { 0 };
		if (i[1] == 0) i[1]++;
	}
	public static void crash(String message){
		System.out.println("Crashing: " +message);
		crash();
	}

	public static void track(){
		try {
			int[] i = { 0 };
			if (i[1] == 0) i[1]++;
		} catch (IndexOutOfBoundsException e){
			e. printStackTrace();
		}
	}
	public static void trace(){
		track();
	}

//Prints the given message returns false
	public static boolean False(String message){
		if (active) System.out.println(now()+ " " + message);
		return false;
	}
//Prints the given message then returns true
	public static boolean True(String message){
		if (active) System.out.println(now()+ " " + message);
		return true;
	}

	public static void err(String message) {
		System.err.println(message);
	}

	public static boolean errFalse(String message) {
		System.err.println(message);
		return false;
	}

	public static boolean errTrue(String message) {
		System.err.println(message);
		return true;
	}

	public static void print(String message) {
		System.out.print(message);
	}

	public static void print(int message) {
		System.out.print(message);
	}

	public static void println(String message) {
		System.out.println(message);
	}
	public static void println(int message) {
		System.out.println(message);
	}

	public static final String date = "[HH:mm:ss.SSS]";
	public static String now(){
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat dateFormat = new SimpleDateFormat(date);
		return dateFormat.format(calendar.getTime());
	}

	public static final String today = "MM-dd|";
	public static String today(){
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat dateFormat = new SimpleDateFormat(today);
		return dateFormat.format(calendar.getTime());
	}

//converts Strings and chars to their numbers
	public static int Int(String x) {
		return Integer.parseInt(x);
	}

	public static int Int(char x) {
		return (int)(x)-48;
	}

//easily edits the index of a string
	public static String editString(String string, int i, char val){
        char[] array = string.toCharArray();
        array[i] = val;
        return String.valueOf(array);
	}

//Call this function when you want the program to just pause until you press enter and then continue
	public static void freeze(){
		if (!active) return;
		System.out.println("Froze program, press enter to continue");
		Scanner scanner = new Scanner(System.in);
		String s = scanner.nextLine();
	}
	public static void Freeze() {
		freeze();
	}
//This version of freeze will print the given message first
	public static void freeze(String message){
		if (!active) return;
		log("Frozen: " +message+ " (Press enter to continue)");
		Scanner scanner = new Scanner(System.in);
		String s = scanner.nextLine();
	}
	public static void Freeze(String message) {
		freeze(message);
	}

	public static void wait(int mil){
		try {
			Thread.sleep(mil);
		} catch(Exception e){

		}
	}

//Slight shortcut to the err and out streams, mainly to track down the last message printed
	public static Stream err = new Stream("err");
	public static Stream out = new Stream("out");

/*==============================================================================
                            MATH
==============================================================================*/
	public static  int displacement(double fx, double fy, double tx, double ty){
		return (int)Math.abs(fx-tx) + (int)Math.abs(fy-ty);
	}

	public static double round(double d, int places){
		double p = Math.pow(10,places);
		return Math.round(d*p)/p;
	}

	public static double sin(double theta){
		return Math.sin(toRadians(theta));
	}
	public static double tan(double theta){
		return Math.tan(toRadians(theta));
	}
	public static double cos(double theta){
		return Math.cos(toRadians(theta));
	}
	public static double sin(Angle theta){
		return Math.sin(toRadians(theta.angle));
	}
	public static double tan(Angle theta){
		return Math.tan(toRadians(theta.angle));
	}
	public static double cos(Angle theta){
		return Math.cos(toRadians(theta.angle));
	}

	public static double toRadians(double theta){
	//180 = 1 pi
		return theta*(Math.PI/180);
	}
	public static double toDegrees(double theta){
	//1 pi = 180
		return theta*(180/Math.PI);
	}
	public static double area(Point[] poly) {
/*
		double area = 0;
		for (int i = 0; i < poly.length; i++){
			int j = (i+1 == poly.length) ? 0 : i+1;
			double ix = poly[i].x;
			double jx = poly[j].x;
			double xDiff = Math.max(ix, jx) - Math.min(ix, jx);
			double xDiff = Math.abs(jx-ix);
			double volume = (xDiff)*((poly[i].y+poly[j].y)/2.0);
			if (jx > ix)
		}
		return Math.abs(area);
*/

		int i, j, n = poly.length;
		double area = 0;
		for (i = 0; i < n; i++) {
			j = (i + 1) % n;
			area += poly[i].x * poly[j].y;
			area -= poly[j].x * poly[i].y;
		}
		return (area)/2.0;

	}


	//personal Math
	public static final double phi = (1.0+Math.sqrt(5))/2.0;
	public static final double e = 2.71828182845904;
	public static double pyth(double fX, double fY, double tX, double tY) {
		return Math.sqrt( (fX-tX)*(fX-tX) + (fY-tY)*(fY-tY) );
	}

/*==============================================================================
                    	CREATE CLASS FROM TERMINAL
==============================================================================*/
//Call this function if you want to create a standard java program
	public static void main(String[] args){

/*Testing mergesort

	if (true){
		HashMap<Integer, Double> hashMap = new HashMap<>();
		hashMap.put(5, 5.0);
		hashMap.put(3, 3.0);
		hashMap.put(1, 1.0);
		hashMap.put(4, 4.0);
		Core.println("before");
		for (Integer i : hashMap.keySet()){
			Core.println(i +"="+ hashMap.get(i));
		}
		ArrayList<Integer> sorted = Core.sort(hashMap, "max");
		Core.println("after");
		for (int i = 0; i < sorted.size(); i++){
			Core.println(sorted.get(i) +"="+ hashMap.get(sorted.get(i)));
		}
		Core.println("-------");
	}

	if (true){
		Point[] array = new Point[]{
			new Point(0, 0),
			new Point(1, 1),
			new Point(1, 2),
			new Point(1, 3),
			new Point(2, 2)};
		HashMap<Point, Double> hashMap = new HashMap<>();
		Core.println("before");
		for (Point p : array){
			hashMap.put(p, p.dist(new Point(1, 3)));
			Core.println(p.toString() +"="+ hashMap.get(p));
		}
		Core.println("after");
		ArrayList<Point> sorted = Core.sort(hashMap, "max");
		for (int i = 0; i < sorted.size(); i++){
			Core.println(sorted.get(i).toString() +"="+ hashMap.get(sorted.get(i)));
		}
	}
*/

		if (args.length == 0) {
			System.out.println("Please enter a filename");
			return;
		}
		String fileName = args[0] + ".java";
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(fileName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		writer.println("public class " +args[0] +"{");
		writer.println("	public static void main(String[] args){");
		writer.println("		");
		writer.println("	}");
		writer.println("}");
		writer.close();
	}

/*==============================================================================
                            SORTING
==============================================================================*/
/**
 * Given a hashMap of keys and values, returns an arraylist of the keys sorted
 * based on the values in the orde specified by "minOrMax"
 * It uses mergesort
 *
 * @param  list HashMap with key of any data type and value as Double
 * @param  minOrMax string describing order (max for descending, min for ascending)
 * @throws IllegalArgumentException if {@code minOrMax} is not "min" nor "max"
 */
	public static <T> ArrayList<T> sort(HashMap<T, Double> toSort, String minOrMax){
		if (minOrMax.equals("max")){
			HashMap<T, Double> sorted = toSort
				.entrySet()
				.stream()
				.sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
				.collect(
					toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,LinkedHashMap::new));
			ArrayList<T> toReturn = new ArrayList<>(0);
			for (T t : sorted.keySet()) toReturn.add(t);
			return toReturn;
		}

		HashMap<T, Double> sorted = toSort
			.entrySet()
			.stream()
			.sorted(comparingByValue())
			.collect(toMap(e -> e.getKey(), e -> e.getValue(), (e1, e2) -> e2, LinkedHashMap::new));
		ArrayList<T> toReturn = new ArrayList<>(0);
		for (T t : sorted.keySet()) toReturn.add(t);
		return toReturn;
	}

 /* Old version, uses mergesort. Good to have to adapt to sort with special rules
	@SuppressWarnings("unchecked")
	public static <T> ArrayList<T> sort(HashMap<T, Double> list, String minOrMax){
	 	if (!minOrMax.equals("max") && !minOrMax.equals("min")){
	 		throw new IllegalArgumentException("Please insert \"min\" or \"max\" as an argument");
	 	}
	//create array of keys
		T[] array = (T[])new Object[list.size()];
	 	int ti = 0;
	 	for (T t : list.keySet()){
	 		array[ti] = t;
	 		ti++;
	 	}
	 	array = mergesort(array, list);
	 //reverse the array
	 	if (minOrMax.equals("max")){
			T[] newArray = (T[])new Object[array.length];
	 		for (int i = 0; i < array.length; i++){
				newArray[i] = array[array.length-1-i];
	 		}
	 		array = newArray;
	 	}
	 //create arraylist
	 	ArrayList<T> toReturn = new ArrayList<>(0);
		for (int i = 0; i < array.length; i++){
			toReturn.add(array[i]);
		}
	 	return toReturn;
 	}
	//return an array which is a combination of the two given arrays
	@SuppressWarnings("unchecked")
	private static <T> T[] merge(T[] a, T[] b, HashMap<T, Double> list) {
		T[] c = (T[])new Object[a.length + b.length];
		for (int i = 0, j = 0, k = 0; k < c.length; ){
			if (j == b.length){
				c[k] = a[i];
				k++;
				i++;
			} else if (i == a.length){
				c[k] = b[j];
				k++;
				j++;
			} else if (list.get(a[i]) <= list.get(b[j])){
				c[k] = a[i];
				k++;
				i++;
			} else if (list.get(a[i]) > list.get(b[j])){
				c[k] = b[j];
				k++;
				j++;
			}
		}
		return c;
	}
	@SuppressWarnings("unchecked")
	private static <T> T[] mergesort(T[] a, HashMap<T, Double> list){
		if (2 < a.length) {
			T[] b = (T[])new Object[a.length/2];
			T[] c = (T[])new Object[a.length/2];
			if (a.length%2 == 0){//if even
				for (int i = 0; i < b.length; i++){
					b[i] = a[i];
					c[i] = a[b.length+i];
				}
			} else {//if odd
				c = (T[])new Object[a.length/2 +1];
				for (int i = 0; i < b.length; i++) b[i] = a[i];
				for (int i = 0; i < c.length; i++) c[i] = a[b.length+i];
			}
			return merge(mergesort(b, list), mergesort(c, list), list);
		}
		//else if length <= 2
		if (a.length == 1) return a;
		else if (list.get(a[0]) > list.get(a[1])){
			return (T[])new Object[]{a[1], a[0]};
		}
		return a;
	}
*/
}

/*==============================================================================
                            SUMMARY
==============================================================================*/
/*Summary of correct syntax uses
Hashmaps
	HashMap<String, Boolean> map = new HashMap<>();
	map.put("string1", true);//'"string 1"' is the key, and 'true' is the value
	map.put("string2", false)
	map.get("string1") //will print true
	map.containsKey("string1") //will print true
	map.containsValue(Boolean.FALSE) //will print true
	for (String s : map.keySet()) System.out.println(s);//will print all keys in the set
	map.remove("string1");//removes this key from the hashmap
RegEx:
	\/ checks for / literally
Elseif summarized
	int val = (boolean) ? 1 : 0;
Scanner
	Scanner scanner = new Scanner(System.in);
	String move = scanner.next();
Split string
	String[] parts = string.split("-");
String to char array
	char[] array = string.toCharArray();
char array to string
	String str = String.valueOf(array);
List current folder files
	File currentFolder = new File("SomeFolder/");//this will check all files in this folder
	ArrayList<String> savedFiles = new ArrayList<String>(0);//List of all files in curr folder
	String currTxtName;
	File[] filesList = currentFolder.listFiles(); //create array of type File
	for (File fileNumber : filesList)//advanced forloop -> checks entire array
		if( fileNumber.isDirectory() || fileNumber.isFile() )//if in curr folder
		{
			currTxtName = fileNumber.getName();//gets the name of the file
			if ( currTxtName.endsWith(".txt") )//makes sure it's ".txt" file
					savedFiles.add( fileNumber.getName() );//add the name to the list
		}
print into new file
	String fileName = "SomeFolder/" + args[0] + ".txt";//this will create a file int "SomeFolder" folder
	PrintWriter writer = null;//Declare as null, else errors will occur
	try { writer = new PrintWriter(fileName); } catch (FileNotFoundException e) { e.printStackTrace(); }
	for (int i = 0; i < savedFiles.size(); i++) writer.println( savedFiles.get(i) );
	writer.close();
read from a file
	Scanner reader = null;//Declare as null, else errors will occur
	try { reader = new Scanner( new File(fileName) ); } catch (FileNotFoundException e) { e.printStackTrace(); }
	while ( reader.hasNextLine() ) System.out.println( reader.nextLine() );
integer values : chars
 48 '0'
 57 '9'
 65 'A'
 90 'Z'
 97 'a'
122 'z'
*/
