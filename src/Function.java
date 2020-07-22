import java.util.*;

public class Function {

//for testing
    public static void main(String[] args){
        double x = Double.parseDouble(args[0]);

        Function f = new Function("1/(1+e^|-x|)");
        Core.println((1.0/(1.0+Math.pow(Math.E, Math.abs(-x)))) +"=="+ f.f(x));

        f = new Function("((4)^x)+3*(x^2)");
        Core.println((Math.pow(4, x) + 3*Math.pow(x, 2)) +"=="+ f.f(x));

        f = new Function("x^e^x");
        Core.println((Math.pow(x, Math.pow(Math.E, x))) +"=="+ f.f(x));

        f = new Function("|x-3|");
        Core.println((Math.abs(x-3)) +"=="+ f.f(x));

        f = new Function("max(|-1|,x)");
        Core.println((Math.max(0,x)) +"=="+ f.f(x));

        f = new Function("if(x==2?e:3)");
        Core.println((x == 2 ? Math.E : 3) +"=="+ f.f(x));

        f = new Function("min(if(x==2?e:3),|1/(x^2)|)");
        Core.println(Math.min( (x == 2 ? Math.E : 3), Math.abs(1/(Math.pow(x, 2)))) +"=="+ f.f(x));

        f = new Function("((x+2)*(x+3))");
        Core.println(((x+2)*(x+3)) +"=="+ f.f(x));

        f = new Function("2+3+4");
        Core.println((2+3+4) + "==" + f.f(x));

        x = 2;
        f = new Function("2*3*4");
        Core.println("24=="+f.f(x));

        f = new Function("h");
        f.addVariable("h", 3);
        Core.println("3=="+f.f(0));

        x = 2;
        f = new Function("if(x!=2?e:7)");
        Core.println("7=="+f.f(x));

        f = new Function("if((x!=2&5<x)?x:1)");
        Core.println( ((x!=2&&5<x)?x:1) +"=="+ f.f(x));

        f = new Function("if((x!=2&(2<x|x<2))?1:x)");
        Core.println( ((x!=2&&(2<x||x<2))?1:x) +"=="+ f.f(x));
//trig
        f = new Function("sin(30)");
        Core.println("sin(30) == 0.5 == "+f.f(30));
//non-x variables
        double last = 3;
        f = new Function("((4)^last)+3*(last^2)");
        Core.println((Math.pow(4, last) + 3*Math.pow(last, 2)) +"=="+ f.f(last));
//multidimensional functions
        Function nextxy = new Function("[y;x-1]");
        double[] output = nextxy.toArray(nextxy.getOut("x=2;y=3"));
        Core.println("3.0=="+output[0]+";1.0=="+output[1]);

        nextxy = new Function("if(x<w-1?[x+1;y]:[0;y+1])");
        nextxy.addVariable("w", 6);
        output = nextxy.toArray(nextxy.getOut("x=5;y=3"));
        Core.println("0.0=="+output[0]+";4.0=="+output[1]);
//solving for x
        Function f1 = new Function("2*x");
        Function f2 = new Function("x+1");
        Core.println("1.0=="+f1.intersection(f2));

        f1 = new Function("-2*x");
        f2 = new Function("1");
        Core.println("-0.5=="+f1.intersection(f2));

        f1 = new Function("-2*x-3");
        f2 = new Function("x+3");
        Core.println("-2.0=="+f1.intersection(f2));

        f1 = new Function("0.5*x");
        f2 = new Function("100000*x-800000");
        Core.println("8.0=="+f1.intersection(f2));

//Lines
        Line line = new Line(new Point(0,1), new Point(2,5));
        Core.println(line.getFunction().getFunction());

        Line l1 = new Line(new Point(0,0), new Point(0,5));
        Line l2 = new Line(new Point(-1,0), new Point(2,5));
        Point p = l1.intersection(l2);
        Core.println(l1.intersects(l2) +": "+ p.toString());

//Rotation formula
        Function rotation = new Function("[x*cos(theta)-y*sin(theta);x*sin(theta)+y*cos(theta)]");
        output = rotation.toArray(rotation.getOut("x=4;y=5;theta=90"));
        Core.println("-5.0=="+output[0]+";4.0=="+output[1]);


//Reflection formula
        Function reflection = new Function("[x*cos(2*theta)+y*sin(2*theta);x*sin(2*theta)-y*cos(2*theta)]");
        output = reflection.toArray(reflection.getOut("x=4;y=5;theta=90"));
        Core.println("-4.0=="+output[0]+";5.0=="+output[1]);
    }

/*Consensus with interactions:
- Given functions are linear functions
- Given functions are at most simple level: #*x+# (with x if any on left and constant if any on right)
*/
//given "x+1" and know that itself is "2*x", solve for x in 2x = x+1
    public double intersection(Function function){
    //Send all those that contains variables to the left
        String left = this.function;
        String right = function.getFunction();

        String xsContainer = "";//contains all xs
        String constantsContainer = "";//contains all constants

    //Left evaluation
        leftSideLoop: for (int i = 0; i < left.length(); i++){
            if (left.charAt(i) == 'x'){
                xsContainer+= left.substring(0,i+1);
                if (i+1 < left.length()){//if there are constants, add to constantsContainer
                    constantsContainer += left.substring(i+1, left.length());
                } else break leftSideLoop;//there is no constants
            }
            if (i+1 == left.length()){//we did not find an x
                constantsContainer += left.substring(i+1, left.length());
            }
        }

    //Right evaluation
        for (int i = 0; i < right.length(); i++){
            if (right.charAt(i) == 'x'){
            //create and solve function
                Function xs = new Function(xsContainer + "-1*(" +right.substring(0,i+1)+")" );
            //if there are still constants after finding the x
                if (i+1 < right.length()){
                    if (constantsContainer.equals("")) constantsContainer = "0";
                    Function constants = new Function(right.substring(i+1, right.length()) +"-1*(" +constantsContainer+ ")");
                    double solved = xs.f(1);
                    if (solved == 0) return constants.f();
                    return constants.f()/solved;
                } else {//there are no more constants
                //there cannot be no constants from left in rightside, else the above would've resolved. constantsContainer contains all constants for thsis algebra
                    Function constants = new Function("0-1*(" +constantsContainer+ ")");
                    double solved = xs.f(1);
                    if (solved == 0) return constants.f();
                    return constants.f()/solved;
                }
            }
            if (i+1 == right.length()){//we did not find an x, the entire right must be a constant
                Function xs = new Function(xsContainer);
                Function constants = new Function(right);
                double solved = xs.f(1);
                if (solved == 0) return constants.f();
                return constants.f()/solved;
            }
        }
        throw new IllegalArgumentException("Could not solve for x in " +left+ " = " +right);
    }


    private String function;
    public String getFunction(){return function;}

    public Function(String function){
        this.function = function;
    }

    public Function replica(){
        Function f = new Function(this.function);
        return f;
    }

    private double ram;//random access memory. In the case that the user gives us a variable e.g. "h" and asks us to solve, we will take ram.
    public double f(double x){
        ram = x;
        return d(solve(this.function));
    }
    public double f(){
        return d(solve(this.function));
    }

    private HashMap<String, Double> variables;

    public void addVariable(String s, double val){
        if (variables == null) variables = new HashMap<>();
        variables.put(s, val);
    }


//gets an input described as a string "x=#;y=#;z=#", spits out an output array
    public String getOut(String input){
        String[] inputs = input.split(";");
        for(String i : inputs){
            addVariable(i.substring(0,i.indexOf("=")),d(solve(i.substring(i.indexOf("=")+1, i.length()))));
        }
        String solved = solve(function);
        //Core.println(solved);
        return solved;
    }
/* Have not found a need for this yet
//When no input parameters are needed
    public String getOut(){
        String solved = solve(function);
        //Core.println(solved);
        return solved;
    }
*/
//takes a solved vector string i.e. [2,3,-0.234] etc and returns it as a double array i.e. {2,3,-0.234} etc
    public double[] toArray(String input){
        String[] inputs = input.substring(1, input.length()-1).split(";");
        double[] arr = new double[inputs.length];
        for(int i = 0; i < inputs.length; i++){
            arr[i] = d(inputs[i]);
        }
        return arr;
    }


    public String removeE(String function){
        //Core.log(function);
        int indexE = function.indexOf("E");
        char[] f = function.toCharArray();
        int pow = (f[indexE+1] == '-') ? -1 : +1;
        for (int i = indexE-1; 0 <= i; i--){
            if (!isConstant(f[i]+"") || i == 0){
                String beginning = (!isConstant(f[i]+"")) ? function.substring(0, i+1) : "";
                for (int j = indexE+((pow < 0)?2:1); j < f.length; j++){
                    if (!isConstant(f[j]+"") || j == f.length-1){
                        String end = (!isConstant(f[j]+"")) ? function.substring(j, f.length) : "";
                    //i == first index, j = last index
                        String middle = ((pow < 0) ? "0.0000001" : "10000000");
                        String toReturn = beginning + middle + end;
                        //Core.println("begin = " +beginning);
                        //Core.println("middle= " +middle);
                        //Core.println("end   = " +end);
                        //Core.log(toReturn);
                        //Core.freeze("");
                        return toReturn;
                    }
                }
            }
        }
        Core.exit("no E found");
        return function;
    }


/*==============================================================================
                                SOLVE
==============================================================================*/
//Solve the given equation recursively
    private String solve(String function){

        //Core.log(function);

        while (function.contains("E") || function.contains("E")){
            function = removeE(function);
        }

        char[] f = function.toCharArray();
        ArrayList<String> solvable = new ArrayList<String>(0);
        int last = 0;//the latest index we last added
        int brace = -1;
        boolean abs = false;
        //-1.2246467991473532E-16
        //-1.2246467991473532E16
//find 1.2345E-5 and convert to rounded numbers


/*
        for (int i = 0; i < f.length-2; i++){
            if (isConstant(f[i]+"") && f[i+1] == 'E' && (f[i+2] == '-' || isConstant(f[i+2]+""))){
                //Core.log("picked up " +function);
            //trace back to where it started
                int k = i;
                kLoop: for (; 0 <= k; k--){
                    if (!isConstant(f[k]+"") && f[k] != '.') break;
                }
                if (k == -1) k = 0;
            //trace until it ends
                for (int j = i+3; j < f.length-2; j++){
                    if (!isConstant(f[j]+"") || j+1 == f.length-2){
                        int e = 0;
                        if (!isConstant(f[j]+"")) e =  Integer.parseInt(function.substring(i+2,j)); //this is the number after E
                        if (j+1 == f.length-2) e =  Integer.parseInt(function.substring(i+2,j+1));
                        //Core.freeze("e = " +e);
                        String added = "";
                        eLoop: if (f[i+2] == '-'){//E is a negative power
                            e = Math.abs(e);
                            if (e > 4) {
                                added = "0.00001";
                                break eLoop;
                            }
                            added = "0.";
                            for (int p = 1; p < e; p++) added += "0";
                            added += "1";
                        } else {//E is a positive Power. We want to add the right amount of zeroes
                            added = "1";
                            for (int p = 0; p < e; p++) added += "0";
                        }
                        //Core.log(function +" == "+ function.substring(0, k) +added+ function.substring(j, f.length));
                        return solve(function.substring(0, k) +added+ function.substring(j, f.length));
                    }
                }
            }
        }
*/

//check for multiple dimensions
        vectorSolver: if (function.startsWith("[") && function.endsWith("]")){
            String[] indices = function.substring(1, function.length()-1).split(";");
            for(String s : indices){
                solvable.add(solve(s));
            }
            String solved = "[";
            for (int i = 0; i < solvable.size(); i++){
                solved += solvable.get(i);
                if (i != solvable.size()-1) solved += ";";
            }
            return solved +"]";
        }


//(Brackets)
        bracketMinimizer: if (function.startsWith("(") && function.endsWith(")")){
            String alternate = function.substring(1, function.length()-1);
            char[] alt = alternate.toCharArray();
            int leftBraces = 0, rightBraces = 0;
            for (int i = 0; i < alt.length; i++){
                if (alt[i] == '(') leftBraces++;
                else if (alt[i] == ')'){
                    rightBraces++;
                    if (rightBraces > leftBraces) break bracketMinimizer;
                }
            }
            return solve(function.substring(1, function.length()-1));
        }

//|Absolute|
        if (function.startsWith("|")){
            brace = -1;
            f = function.toCharArray();
            for (int i = 1; i < f.length; i++){
                if (f[i] == '(') brace++;
                if (f[i] == ')') brace--;
                if (f[i] == '|' && brace == -1){
                    return solve(Math.abs( d(solve(function.substring(1, i))) ) +function.substring(i+1, function.length()));
                }
            }
        }


//Sqrt
        if (function.startsWith("sqrt(")){
            brace = 0;//since sqrt( has a brace that need be closed
            abs = false;
            f = function.toCharArray();
            for (int i = 5; i < f.length; i++){
                if (f[i] == '(') brace++;
                else if (f[i] == '|') abs = !abs;
                else if (f[i] == ')' && brace == -1 && !abs) {
                    return solve(Math.sqrt( d(solve(function, 5, i-1))) + function.substring(i+1, function.length()));
                }
            }
        }
//rounding
        if (function.startsWith("int(") || function.startsWith("floor(") || function.startsWith("ceil(") || function.startsWith("round(")){
            brace = -1;
            abs = false;
            f = function.toCharArray();
            for (int i = 0; i < f.length; i++){
                if (f[i] == '(') brace++;
                else if (f[i] == '|') abs = !abs;
                else if (f[i] == ')' && brace == 0 && !abs) {
                    //Core.log(function);
                    if (function.startsWith("int(") || function.startsWith("floor("))
                        return solve( Math.floor( d(solve(function.substring(function.indexOf("(")+1, i-1))) ) + function.substring(i+1, function.length()));
                    if (function.startsWith("ceil("))
                        return solve( Math.ceil( d(solve(function.substring(function.indexOf("(")+1, i-1))) ) + function.substring(i+1, function.length()));
                    if (function.startsWith("round("))
                        return solve( Math.round( d(solve(function.substring(function.indexOf("(")+1, i-1))) ) + function.substring(i+1, function.length()));
                }
            }
        }


//if(x==5?4:6)
        if (function.startsWith("if(")){

            brace = 0;//since if( has a brace that need be closed
            abs = false;
            f = function.toCharArray();
            for (int i = 3; i < f.length; i++){
                if (f[i] == '(') brace++;
                else if (f[i] == ')') brace--;

                if (brace == -1) {
                    int start = 3, end = i;

                    Boolean bool = null;
                    for (i = start; i < end; i++){//through string (...)==#?(...):#

                        if (f[i] == '(') brace++;
                        else if (f[i] == ')') brace--;
                        //else if (f[i] == '|') abs = !abs;//absolute values shouldn't exist inbetween brackets e.g (|)| etc

                        if (brace == -1){
                        //split based on ?. everything before must be solved as a boolean.
                            if (bool == null && f[i] == '?'){
                                bool = new Boolean(bool(function.substring(start, i)));//(...)==#
                                start = i+1;
                        //split based on :
                            } else if (f[i] == ':' && !abs){ //bool is defined
                                String left = solve(function.substring(start, i));
                                String right = solve(function.substring(i+1, end));
                                return solve( ((bool)?left:right) + function.substring(end+1, function.length()) );
                            }
                        }
                    }
                }
            }
        }


//max & min
        last = 0;
        brace = -1;
        abs = false;
        if ((function.startsWith("max(") || function.startsWith("min(")) && function.endsWith(")")){
            boolean max = function.startsWith("max(");
            function = function.substring(4, function.length()-1);
            f = function.toCharArray();
        //search for comma
            for (int i = 0; i < f.length; i++){
                if (brace == -1 && !abs){ //if we are not inside a brace
                    if (f[i] == ','){ //this splits stuff in half
                        double left = d(solve(function.substring(0,i)));
                        double right = d(solve(function.substring(i+1,function.length())));
                        if (max) return Math.max(left, right) +"";
                        else return Math.min(left, right) +"";
                    } else if (f[i] == '('){
                        brace++;
                        if (i == 0) last = i;
                        else if (f[i-1] == ',') last = i;
                    } else if (f[i] == '|'){
                        abs = true;
                        if (i == 0) last = i;
                        else if (f[i-1] == ',') last = i;
                    }
                } else if (brace > -1 || abs){
                    if (f[i] == '(') brace++;
                    else if (f[i] == ')') brace--;

                    if (f[i] == '|') abs = !abs;
                }
            }
            throw new IllegalArgumentException("No comma found in " +function);
        }

//Trigonometry
        if ((function.startsWith("sin(") || function.startsWith("cos(") || function.startsWith("tan(")) && function.endsWith(")")){
            brace = 0;//since sqrt( has a brace that need be closed
            abs = false;
            f = function.toCharArray();
            for (int i = 4; i < f.length; i++){
                if (f[i] == '(') brace++;
                else if (f[i] == ')') brace--;
                else if (f[i] == '|') abs = !abs;

                if (f[i] == ')' && brace == -1 && !abs) {
                    double degrees = d(solve(function.substring(4, i)));
                    String trigF = function.substring(0,4);
                    double trig = 0;
                    if(trigF.equals("sin(")) trig = Core.sin(degrees);
                    else if(trigF.equals("cos(")) trig = Core.cos(degrees);
                    else if(trigF.equals("tan(")) trig = Core.tan(degrees);
                    return solve( round(trig, 3)+""+ function.substring(i+1, function.length()));
                }
            }
        }

//NOTE: keep this here, else there will be errors (trust me)
//Return if single value like an integer or x
        if (!hasOperator(function)){
            double total = solveFor(function);
            return total +"";
        }

/* I did not see the purpose of this function at the moment, x+1 should suffice it's use and I cannot see where
    a permanent increase in a variable's value for the rest of the sum will e of use. In any case, we can return
    x+1 to the user and they would feed us x+1 again which we will see as the new x, effectively incrementing x.

//Incremention
        if (function.length == 3 && function.endsWith("++")){
            double total = solveFor(function.charAt(0)+"");
            total++;
            return total +"";
        }
        if (function.startsWith("x+=")){
            double incrementWith = d(solve(function.substring(3, function.length())));
            x += incrementWith;
            return solveFor("x") +"";
        }
//Decremention
        if (function.equals("x--")){
            x--;
            return solveFor("x") +"";
        }
        if (function.startsWith("x-=")){
            double decrementWith = d(solve(function.substring(3, function.length())));
            x -= decrementWith;
            return solveFor("x") +"";
        }
*/


//ADDITION
        last = 0;
        brace = -1;
        abs = false;
        for (int i = 0; i < f.length; i++){
            if (brace == -1 && !abs){ //if we are not inside a brace
                if (f[i] == '+' || f[i] == '-'){
                //add all up until this
                    if (function.substring(last, i).equals("")) solvable.add("0");
                    else solvable.add(solve(function.substring(last, i)));
                    solvable.add(f[i] +"");
                    last = i+1;
                } else if (f[i] == '('){
                    brace++;
                    if (i == 0) last = i;
                    else if (f[i-1] == '+' || f[i-1] == '-') last = i;
                } else if (f[i] == '|'){
                    abs = true;
                    if (i == 0) last = i;
                    else if (f[i-1] == '+' || f[i-1] == '-') last = i;
                }
            } else if (brace > -1 || abs){

                if (f[i] == '(') brace++;
                else if (f[i] == ')') brace--;

                if (f[i] == '|') abs = !abs;
            }
        //if we've reached the end and already added a few stuff
            if (i == f.length-1 && !solvable.isEmpty()){
                solvable.add(solve(function.substring(last, f.length)));
            }
        }

    //we've added everything and it is ready to solve, solve according to + and -
        if (!solvable.isEmpty()){ //solvable looks like x+2-4-e etc
        //    System.out.println(solvable);
            String total = solvable.get(0);
        //solve the first three of solvable until only 1 left
            while (solvable.size() >= 3){
                total = solvable.get(0);
                String operator = solvable.get(1);
                String b = solvable.get(2);

                solvable.set(0, (solveFor(total) + (( operator.equals("+") ? +1 : -1 )*solveFor(b)) ) +"" );


                solvable.remove(2);
                solvable.remove(1);
            }
            return solvable.get(0) + "";
        }


//MULTIPLICATION
    //if we are not dealing with a + and - sum, move onto solving based on / and *
        if (solvable.isEmpty()){
            last = 0;//the latest index we last added
            brace = -1;
            abs = false;
            for (int i = 0; i < f.length; i++){
                if (brace == -1 && !abs){ //if we are not inside a brace
                    if (f[i] == '*' || f[i] == '/'){
                    //add all up until this
                        if (function.substring(last, i).equals("")) solvable.add("0");
                        else solvable.add(solve(function.substring(last, i)));
                        solvable.add(f[i] +"");
                        last = i+1;
                    } else if (f[i] == '('){
                        brace++;
                        if (i == 0) last = i;
                        else if (f[i-1] == '*' || f[i-1] == '/') last = i;
                    } else if (f[i] == '|'){
                        abs = true;
                        if (i == 0) last = i;
                        else if (f[i-1] == '*' || f[i-1] == '/') last = i;
                    }
                } else if (brace > -1 || abs){

                    if (f[i] == '(') brace++;
                    else if (f[i] == ')') brace--;

                    if (f[i] == '|') abs = !abs;
                }
                //if we've reached the end and already added a few stuff
                if (i == f.length-1 && !solvable.isEmpty()){
                    solvable.add(solve(function.substring(last, f.length)));
                }
            }
        }

    //we've added everything and it is ready to solve, solve according to + and -
        if (!solvable.isEmpty()){ //solvable looks like x*2/4*e etc
            double total = 0.0;
        //solve the first three of solvable until only 1 left
            while (solvable.size() >= 3){
                int l = solvable.size();

                String a = solvable.get(l-3);
                String operator = solvable.get(l-2);
                String b = solvable.get(l-1);

                if (operator.equals("*")) total = solveFor(a)*solveFor(b);
                else if (operator.equals("/")) total = solveFor(a)/solveFor(b);
                else {
                    System.out.println(a + operator + b);
                    System.out.printf("[%" +a.length()+ "s]%n^", "");
                    throw new IllegalArgumentException("Cannot identify operator");
                }

                solvable.remove(l-1);
                solvable.remove(l-2);
                solvable.set(l-3, total+"");
            }
            return total + "";
        }


//EXPONENTIAL
    //Explonential ^
        if (solvable.isEmpty()){
            last = 0;//the latest index we last added
            brace = -1;
            abs = false;
            for (int i = 0; i < f.length; i++){
                if (brace == -1 && !abs){ //if we are not inside a brace
                    if (f[i] == '^'){
                        //add all up until this
                        solvable.add(solve(function.substring(last, i)));
                        solvable.add(f[i] +"");
                        last = i+1;
                    } else if (f[i] == '('){
                        brace++;
                        if (i == 0) last = i;
                        else if (f[i-1] == '^') last = i;
                    } else if (f[i] == '|'){
                        abs = true;
                        if (i == 0) last = i;
                        else if (f[i-1] == '^') last = i;
                    }
                } else if (brace > -1 || abs){

                    if (f[i] == '(') brace++;
                    else if (f[i] == ')') brace--;

                    if (f[i] == '|') abs = !abs;
                }
                //if we've reached the end and already added a few stuff
                if (i == f.length-1 && !solvable.isEmpty()){
                    solvable.add(solve(function.substring(last, f.length)));
                }
            }
        }

    //we've added everything and it is ready to solve, solve according to ^
        if (!solvable.isEmpty()){ //solvable looks like x^2^e etc
            double total = 0.0;
        //solve the first three of solvable until only 1 left
            while (solvable.size() >= 3){
                int l = solvable.size();

                String a = solvable.get(l-3);
                String operator = solvable.get(l-2);
                String b = solvable.get(l-1);

                if (operator.equals("^")) total = Math.pow(solveFor(a), solveFor(b));
                else {
                    System.out.println(a + operator + b);
                    System.out.printf("[%" +a.length()+ "s]%n^", "");
                    throw new IllegalArgumentException("Cannot identify operator");
                }

                solvable.remove(l-1);
                solvable.remove(l-2);
                solvable.set(l-3, total+"");
            }
            return total + "";
        }

        Core.crash("Couldn't solve for " +function);
        return null;
    }

//solve a piece of the function
    private String solve(String function, int i, int j){
        return solve(function.substring(i, j));
    }


/*==============================================================================
                                BOOL
==============================================================================*/
//Returns the parsed boolean value
    private boolean bool(String function){//(value) >= value // (...)==# // ((...)==#)&(#==(...)) // (|...|==#)&&((x==5&&)#==(...))

        if (function.equals("true")) return true;
        if (function.equals("false")) return false;

        char[] f = function.toCharArray();
        int brace = -1;
        ArrayList<String> solvable = new ArrayList<String>(0);
        int last = 0;

        bracketMinimizer: if (function.startsWith("(") && function.endsWith(")")){
            String alternate = function.substring(1, function.length()-1);
            char[] alt = alternate.toCharArray();
            int leftBraces = 0, rightBraces = 0;
            for (int i = 0; i < alt.length; i++){
                if (alt[i] == '(') leftBraces++;
                else if (alt[i] == ')'){
                    rightBraces++;
                    if (rightBraces > leftBraces) break bracketMinimizer;
                }
            }
            return bool(function.substring(1, function.length()-1));
        }



    //first look for any BOOLEAN operators & |
        for (int i = 0; i < f.length; i++){
            if (f[i] == '(') brace++;
            else if (f[i] == ')') brace--;

            if (brace == -1){
                if (f[i] == '&' || f[i] == '|'){
                    solvable.add(function.substring(last, i));
                    solvable.add(f[i]+"");
                    last = i;
                }
            }
        }

        if (!solvable.isEmpty()){//if we've already added stuff, thus there are the operators & |
            solvable.add(function.substring(last, f.length));
        //now we have solvable: "x!=2"; "&"; "5<x" OR "x!=2"; "|"; "5<x"; "|"; "(...)"; (never both at the same time, may expect mutliple of each & | comparators)
            boolean endBool = bool(solvable.get(0));
        //solve the first three of solvable until only 1 left
            while (solvable.size() >= 3){
                int l = solvable.size();

                String a = solvable.get(l-3);
                String operator = solvable.get(l-2);
                String b = solvable.get(l-1);

                if (operator.equals("&")) endBool = bool(a)&&bool(b);
                else if (operator.equals("|")) endBool = bool(a)||bool(b);
                else {
                    System.out.println(a + operator + b);
                    System.out.printf("[%" +a.length()+ "s]%n^", "");
                    throw new IllegalArgumentException("Cannot identify operator '" +operator+ "'");
                }

                solvable.remove(l-1);
                solvable.remove(l-2);
                solvable.set(l-3, endBool+"");
            }
            return endBool;
        }

    //we didn't add anything, we have something like "x!=5" or "(...)<(...)"
    //find comparators ==; <=; >=; !=; <; >;
        brace = -1;
        for (int i = 0; i < f.length; i++){
            if (f[i] == '(') brace++;
            else if (f[i] == ')') brace--;

            if (brace == -1){
                if (f[i] == '=' || f[i] == '<' || f[i] == '>' || f[i] == '!'){
                    double left = d(solve(function.substring(0, i)));
                    if (f[i+1] == '='){
                        double right = d(solve(function.substring(i+2, function.length())));
                        if (f[i] == '=') return left == right;
                        if (f[i] == '<') return left <= right;
                        if (f[i] == '>') return left >= right;
                        if (f[i] == '!') return left != right;
                        Core.crash("Couldn't solve " +function);
                    } else {
                        double right = d(solve(function.substring(i+1, function.length())));
                        if (f[i] == '<') return left < right;
                        if (f[i] == '>') return left > right;
                        Core.crash("Couldn't solve " +function);
                    }
                }
            }
        }

        Core.crash("Could not solve boolean value of " +function);
        return false;
    }


    private double d(String a){
        return Double.parseDouble(a);
    }

//Middle man for tracking down errors
    private double solveFor(String a){
        //Core.log(a);
        if (a.equals("")) return 1;
        double solved = solveFor2(a);
        return solved;
    }

//assumes given a correct number, no operators
    private double solveFor2(String a){
        double symbol = a.startsWith("-") ? -1.0 : +1.0;
        if (symbol == -1.0) a = a.substring(1, a.length());
        if (a.equals("e")) return symbol*Math.E;
        if (a.equals("random")) return symbol*Math.random();
        if (variables != null){
            for (String var : variables.keySet()){
                if (var.equals(a)) return symbol*variables.get(var);
            }
        }
        if (isConstant(a)) return symbol*d(a);
        return symbol*ram;//if we could not identify the variable, we will have it be whatever was given at the start
    }

//x, h, y, z etc are variables, 1, 2, 4.5, -0,923434 are constants
    private boolean isConstant(String a){
        for (char c : a.toCharArray()){
            if (!(48 <= (int)c && (int)c <= 57) && c != '.') return false;

            //if (48<=(int)c && (int)c<=57) return true;
        }
        return true;


    }

    private boolean hasOperator(String function){
        char[] f = function.toCharArray();
        for (char c : f){
            if (c == '+' || c == '-' || c == '/' || c == '*' || c == '^')
                return true;
        }
        return false;
    }
    private boolean isOperator(char c){
        return (c == '+' || c == '-' || c == '/' || c == '*' || c == '^');
    }

    private boolean containsVariable(String a){
        for (char c : a.toCharArray()){
            if (!(48<=(int)c && (int)c<=57) && !isOperator(c) && c != ' ') return true;
        }
        return false;
    }

    public static double round(double d, int places){
        double p = Math.pow(10,places);
        return Math.round(d*p)/p;
    }
}
