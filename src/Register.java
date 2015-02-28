
public class Register implements Comparable<Register>{
	
	/* Class members*/ 
	private	String regname;
	private String realname;
	private String offset;
	
	private boolean spilled;
	
	private int firstline;
	private int lastline;
	private int liverange;
	private int frequency;
	
	
	/*Setter Methods*/
	public void setName(String s) { regname = s;}
	public void setRealName(String s){realname = s;}
	public void setSpilled(){ spilled = true;}
	public void setFirstLine(int i){firstline = i;}
	public void setLastLine(int i){lastline = i; calculateLiveRange();}
	public void setOffset(String s){offset = s;}
	
	/*Getter Methods*/
	public String getRegName(){return regname;}
	public String getRealName(){return realname;}
	public String getOffset(){return offset;}
	public int getFirstLine(){return firstline;}
	public int getLastLine() {return lastline;}
	public int getLiveRange(){return liverange;}
	public int getFrequency() {return frequency;}
	
	/*Utility Methods*/
	public boolean isSpilled(){return spilled;}
	public void incrementFrequency(){frequency++;}
	private void calculateLiveRange(){liverange = lastline - firstline;}

	public Register(){
		regname = realname = "r0"; offset = "0";
		firstline = lastline = 0;
		calculateLiveRange();
		spilled = false; frequency = -1;
	}
	public Register(String vr, String off, int ln){
		regname = vr; realname = null; offset = off;
		firstline = lastline = ln;
		calculateLiveRange();
		spilled = false; frequency = 1;
				
	}
	/** Compare two registers based off frequency, this is reversed to the normal
	 * sorting routine, meaning that it sorts based off of the input register. 
	 * i.e. if the input register is larger, return 1 */
	public int compareTo(Register o){
			if(frequency < o.frequency)
				return 1;
			else if (frequency > o.frequency)
				return -1;
			else 
				return 0;

	}

	/** Compare two registers for equality*/
	public boolean equals(Object o){
		if (o==null)
			return false;
		if(o instanceof Register)
			return regname.equals( ((Register)o).regname);
		return false;
	}
	
	public String toString(){
		String out = "";
		out += "\nVR: " + regname;
		out += "\nRR: " + realname;
		out += "\n FL/LL: " + firstline + "/" + lastline;
		out += "\nFrequency: " + frequency;
		out += "\nSpilled: ";out += (spilled==true)?"true":"false";
		out += "\nOffset: " + offset;
		return out;
	}
	
	/** Return the numerical representation of the register name. AKA the name without the 'r' */
	public int getNumber(){
		return Integer.parseInt(regname.substring(1));
	}
	
}
