import java.util.ArrayList;
import java.util.Collections;


public class RegisterList {

	private ArrayList<Register> virtualRegisters;
	private ArrayList<Register> realRegisters;
	private ArrayList<Register> feasibleSet;
	private int feasibleSize;
	private int realSize;
	private int virtualSize;
	
	/**The bare bones constructor, real and veasible are not set up*/
	public RegisterList(){
		virtualRegisters = new ArrayList<Register>(255);
		Register r0 = new Register(); //use the empty constructor for r0 
		virtualRegisters.add(r0);
		virtualSize = 1;
	}
	
	/**The robust constructor. Real and Feasible sets are crafted*/
	public RegisterList(int feasible, int real){
		feasibleSize = feasible;
		realSize = real;
		
		virtualRegisters = new ArrayList<Register>(255);
		realRegisters  = new ArrayList<Register>(realSize);
		feasibleSet = new ArrayList<Register>(feasibleSize);
		
		Register r0 = new Register(); //use the empty constructor for r0 
		virtualRegisters.add(r0);
		virtualSize = 1;
		feasibleSet.add(r0);
	}
	
	/**This method is used to initialize the feasible and real sets after
	 *  entering an allocator */
	public void setFeasRealSize(int feasible, int real){
		feasibleSize = feasible;
		realSize = real;
		
		realRegisters  = new ArrayList<Register>(realSize);
		feasibleSet = new ArrayList<Register>(feasibleSize);
		
		Register r0 = new Register(); //use the empty constructor for r0
		feasibleSet.add(r0);
		
	}
	
	
	/** Place register rx into the virtual register set if the register is 
	 * already in the set, increment the frequency and update the last line*/
	public void addToVirtual(Register rx){
		if(virtualRegisters.contains(rx)){
			int pos = virtualRegisters.indexOf(rx);
			virtualRegisters.get(pos).incrementFrequency();
			virtualRegisters.get(pos).setLastLine(rx.getLastLine());
		}
		else{
			virtualRegisters.add(rx);
			virtualSize++;
		}
	}
	
	/**Obrain the register located at position X*/
	public Register getfromVirtual(int regnum){
		return virtualRegisters.get(regnum);
	}
	
	/**Find a virtual register with name X*/
	public Register getFromVirtual(String regname){
		for(int i=0; i<virtualSize;i++){
			if(virtualRegisters.get(i).getRegName().equals(regname))
				return virtualRegisters.get(i);
		}
		return null;
	}
	
	/** get the number of Virtual Registers*/
	public int getVirtualSize(){return virtualSize;}
	
	
	/**Replace/add a register to the Feasible set. 
	 * Update the virtualRegisters entries for all elements involved*/
	public void addToFeasible(Register rx, int regnum){
		int pos = virtualRegisters.indexOf(rx);
		virtualRegisters.get(pos).setRealName("r"+regnum);
		
		if(feasibleSet.size() == feasibleSize){ //things don't index chronologically in the arraylist, so find the position based off the name reference
			pos = virtualRegisters.indexOf(feasibleSet.get(regnum));
			virtualRegisters.get(pos).setRealName("");
			rx.setRealName("r"+regnum);
			feasibleSet.set(regnum, rx); //replace the previous register
		}
		else
			feasibleSet.add(rx);
	}

	/**Obrain the register located at position X*/
	public Register getfromFeasible(int regnum){
		return feasibleSet.get(regnum);
	}
	
	/**Check if the register rx is presently in the feasible set*/
	public boolean inFeasibleSet(Register rx){
		return feasibleSet.contains(rx);
	}
	
	/** get the number of Feasible Registers*/
	public int getFeasibleSize(){return feasibleSize;}
	
	/**Replace/add a register to the Real set. 
	 * Update the virtualRegisters entries for all elements involved*/
	public void addToReal(Register rx, int regnum){
		int pos = virtualRegisters.indexOf(rx);
		virtualRegisters.get(pos).setRealName("r"+ (regnum+feasibleSize));
		
		if(realRegisters.size() == realSize){
			pos = virtualRegisters.indexOf(feasibleSet.get(regnum));
			virtualRegisters.get(pos).setRealName("");
			rx.setRealName("r"+regnum);
			realRegisters.set(regnum, rx); //replace the previous register
		}
		else
			realRegisters.add(rx);
	}
	
	/**Obrain the register located at position X*/
	public Register getfromReal(int regnum){
		return realRegisters.get(regnum);
	}
	
	/** get the number of Real Registers*/
	public int getRealSize(){return realSize;}
	
	/**Check if the register rx is presently in a real register*/
	public boolean inRealRegister(Register rx){
		return realRegisters.contains(rx);
	}
	
	public String printVirtual(){
		String out = "";
		for(int i = 0; i < virtualRegisters.size(); i++){
			if(virtualRegisters.get(i) != null)
				out += virtualRegisters.get(i) + "\n";
		}
		return out;
	}
	
	/**Compare two registers based off register number*/
	public int compare(Register rx, Register ry){
			if(rx.getNumber() > ry.getNumber())
				return 1;
			else if(rx.getNumber() == ry.getNumber())
				return 0;
			else
				return -1;
	}
	
	/**Sort the Virtual Registers by Frequency*/
	public void sortByFrequency(){
		Collections.sort(virtualRegisters);
	}

}
