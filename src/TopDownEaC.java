import java.util.ArrayList;


public class TopDownEaC extends RegisterAllocator{

	/*
	private ArrayList<Instruction> inputInst;
	private ArrayList<Instruction> outputInst;
	private RegisterList regList;
	public static int  FEASIBLE_SIZE = 3; 
	private int kreg;
	*/
	
	public TopDownEaC(ArrayList<Instruction> iI,ArrayList<Instruction> oI, RegisterList rL, int Kreg){
		inputInst = iI;
		outputInst = oI;
		regList = rL;
		feasibleSize = 3;
		realSize =  Kreg - feasibleSize;
		
		//Prepare the registers for allocation
		regList.setFeasRealSize(feasibleSize, realSize);
		regList.sortByFrequency();
		
		//Fill the real registers with the highest frequency registers
		for(int i=0; (i < realSize) && (i < regList.getVirtualSize() ); i++){
			Register rx = regList.getfromVirtual(i);
			regList.addToReal(rx, i);
		}
		/*
		 * Any remaining initializations
		 */
		String[] str = {"loadI", "1020", " ","r0" };
		outputInst.add(new Instruction(str, 0));
		
	}
	
	
	/**Perform Register Allocation with no live range consideration
	 * real registers filled by things with the highest frequency
	 * */
	public void allocateRegisters(){
		int size = inputInst.size();
		
		//Iterate over all instructions
		for(int i = 0; i < size; i++){ 
			String[] tok = {" ", " ", " ", " "}; //use for new instruction creation
			Instruction old = inputInst.get(i), notOld;
			int linenum = inputInst.get(i).getLineNumber();
			int j = 1;
			String regname;
			Register rx = null;
			tok[0] = old.getInstToken(0); //Set the command token
			 
			//Loop over the two input arguments
			for(j = 1; j < 3; j++){
				regname = old.getInstToken(j);
				if(regname.startsWith("r")){//then regname is a register
					rx = regList.getFromVirtual(regname);
					
					if(regList.inRealRegister(rx)&& !rx.isSpilled()){
						tok[j] = rx.getRealName(); //It's in a register, use it
					}
					else{
						if(rx.isSpilled()){//it has already been spilled, so load it
							regList.addToFeasible(rx, j);
							loadFromMemory(rx); //Generate load code
							tok[j] = rx.getRealName();
							//System.out.println(tok[j]);
						}
						else{//This is our first time seeing this value, store it, then use it
							rx.setSpilled();
							regList.addToFeasible(rx, j);
							//generate store code -> shouldn't have to do here
							tok[j] = rx.getRealName();
						}
					}
				}
				
				//rx was an immediate value
				else{
					tok[j] = old.getInstToken(j);
				}	
			}//End For j
			
			regname = old.getInstToken(3); //load the output/result register
			
			if(regname.startsWith("r")){//Integerity check, shouldn't actually need
				rx = regList.getFromVirtual(regname);
				if(regList.inRealRegister(rx))
					tok[3] = rx.getRealName();
				else if(tok[0].equals("store")){
					regList.addToFeasible(rx, ((j==2)?1:2));
					loadFromMemory(rx); //Generate load code
					tok[3] = rx.getRealName();
				}
				else{
					rx.setSpilled(); //if it is already true, this won't really matter
					regList.addToFeasible(rx, (j%2+1)); //Use F1 to perform store operation
					tok[3] = rx.getRealName();
				}
			}
			
			//Add out new instruction to the list
			notOld = new Instruction(tok, linenum);
			//System.out.println(notOld);
			outputInst.add(notOld);
			
			if(!regList.inRealRegister(rx) && rx != null){
				storeToMemory(rx);//generate store code 
			}
	
		}//End for i
	}//End of AllocateRegisters
	
/*	private void loadFromMemory(Register rx){
		//loadI offset => fx
		//add  r0. fx
		//load fx => fx
		Instruction notOld;
		
		String tok[] = new String[4];
		tok[0] = "\nloadI";
		tok[1] = rx.getOffset();
		tok[2] = " ";
		tok[3] = rx.getRealName();
		
		notOld = new Instruction(tok, -1);
		outputInst.add(notOld);
		
		tok[0] = "add";
		tok[1] = regList.getfromFeasible(0).getRegName(); //The value r0
		tok[2] = rx.getRealName();
		tok[3] = rx.getRealName();
		
		notOld = new Instruction(tok, -1);
		outputInst.add(notOld);
		
		tok[0] = "load";
		tok[1] = rx.getRealName();
		tok[2] = " ";
		tok[3] = rx.getRealName() + "\n";
		
		notOld = new Instruction(tok, -1);
		outputInst.add(notOld);
	}
	
	private void storeToMemory(Register rx){
		//loadI offset => fy
		//add r0, fy =>fy
		//store fx => fy
		Instruction notOld;
		String ry = (rx.getRealName().equals("r1"))? "r2": "r1"; //we need two variable to store without storeAI
		//I trust the assumption that we use r0, r1, r2 for our feasible set
		
		String tok[] = new String[4];
		tok[0] = "\nloadI";
		tok[1] = rx.getOffset();
		tok[2] = " ";
		tok[3] = ry; 
		
		notOld = new Instruction(tok, -1);
		outputInst.add(notOld);
		
		tok[0] = "add";
		tok[1] = regList.getfromFeasible(0).getRegName(); //The value r0
		tok[2] = ry;
		tok[3] = ry;
		
		notOld = new Instruction(tok, -1);
		outputInst.add(notOld);
		
		tok[0] = "store";
		tok[1] = rx.getRealName();
		tok[2] = " ";
		tok[3] = ry + "\n";
		
		notOld = new Instruction(tok, -1);
		outputInst.add(notOld);
	}
	*/
	
}//End of Class
