
public class Stream{
	public Stream(String type) { this.type = type; }
	public String type;//"err" or "out" or "in"

	private void printType(String message){
		if (type.equals("err")) System.err.print(message);
		else if (type.equals("out")) System.out.print(message);
	}

	public void println(String message){
		if (buffer != null){
			stack = buffer + message;
			printType(buffer + message +"\n");
			buffer = null;
		} else {
			stack = message;
			printType(message +"\n");
		}
	}

	public void print(String message){
		if (buffer == null) buffer = "";
		buffer += message;
		printType(message);
	}
	public String buffer = null;
	private String stack;//the last message

	public String nextLine(){
		if (stack == null) return null;
		String nextLine = stack;
		stack = null;
		return nextLine;
	}

	public boolean hasNextLine() { return stack != null; }
	public String viewNextLine() { return stack; }
	public void clear() { stack = null; }
}
