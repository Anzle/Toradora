
public class Instruction {

	/*Class members*/
	private String[] instruction;
	private int linenumber; 
	
	public Instruction(String[] tokens, int ln){
		instruction = new String[4];
		for(int i=0; i< 4; i++)
			instruction[i] = tokens[i];
		linenumber = ln;
	}
	
	/*Get the 0 based Instruction Token*/
	public String getInstToken(int i){
		return instruction[i];
	}
	
	public int getLineNumber(){
		return linenumber;
	}
	
	public String toString(){
		String out = "";
		out += instruction[0];
		out += " " + instruction[1];
		
		if(!instruction[2].equals(" "))
			out += ", " + instruction[2];
		if(!instruction[3].equals(" "))
			out += " => " + instruction[3];
		
		return out;
	}
	
}
