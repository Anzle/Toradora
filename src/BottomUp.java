import java.util.ArrayList;


public class BottomUp extends RegisterAllocator {

	public BottomUp(ArrayList<Instruction> iI,ArrayList<Instruction> oI, RegisterList rL, int Kreg){
		inputInst = iI;
		outputInst = oI;
		regList = rL;
		feasibleSize = 2;
		realSize =  Kreg - feasibleSize;
		
		//Prepare the registers for allocation
		regList.setFeasRealSize(feasibleSize, realSize);
		
		/*
		 * Any remaining initializations
		 */
		String[] str = {"loadI", "1020", " ","r0" };
		outputInst.add(new Instruction(str, 0));
	}
	
	public void allocateRegisters(){
		int size = inputInst.size();
		
		//Iterate over all instructions
		for(int i = 0; i < size; i++){ 
			String[] tok = {" ", " ", " ", " "}; //use for new instruction creation
			Instruction old = inputInst.get(i), notOld;
			int linenum = inputInst.get(i).getLineNumber();
			String regname;
			Register rx = null;
			tok[0] = old.getInstToken(0); //Set the command token
			 
			

			//Loop over the two input arguments
			for(int j = 1; j < 3; j++){
				regname = old.getInstToken(j);
				if(regname.startsWith("r")){//then regname is a register
					rx = regList.getFromVirtual(regname);
					
					if(regList.inRealRegister(rx)){
						tok[j] = rx.getRealName(); //It's in a register, use it
					}
					else if(regList.getCurrentRealSize() < realSize){
							regList.addToReal(rx, regList.getCurrentRealSize());
							tok[j] = rx.getRealName(); 
					}
					/*else if(rx.equals(dying)){ //is this register being taken over by the output?
						tok[j] = tok[3];
					}
					*/
					else if(rx.isSpilled()){
						int spill = regList.findFurthestUse(linenum);
						regList.addToReal(rx, spill);
						loadFromMemory(rx, ""); //Generate load code
						tok[j] = rx.getRealName();
					}
					else{
						int spill = regList.findFurthestUse(linenum);
						regList.getfromReal(spill).setSpilled();
						storeToMemory(regList.getfromReal(spill));
						regList.addToReal(rx, spill);
						tok[j] = rx.getRealName();
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
				else{
					storeOutput(rx, linenum);
					tok[3] = rx.getRealName();
					
				}
			}
			
			//Add out new instruction to the list
			notOld = new Instruction(tok, linenum);
			//System.out.println(notOld);
			outputInst.add(notOld);
			
			//we need to store the output in the even there are only feasible registers. 
			if(realSize == 0 && rx != null){
				storeToMemory(rx);//generate store code 
			}
	
		}//End for i
	}//End of AllocateRegisters
	

	/* Take a virtual register rx and allocate it to a real register
	 * Update the real set and generate spill code if needed
	 * */
	private Register storeOutput(Register rx, int linenumber){
		Register spill = null;
		if(regList.getCurrentRealSize() < realSize){
			regList.addToReal(rx, regList.getCurrentRealSize());
			return spill;
		}
		int realreg = regList.findFurthestUse(linenumber);
		//check if the register we got isn't dead and needs to be spilled
		if(regList.getfromReal(realreg).getLastLine() > linenumber){
			spill = regList.getfromReal(realreg); //get the register we will spill
			spill.setSpilled();
			storeToMemory(spill); //generate spill code. 
		}
		
		regList.addToReal(rx, realreg); //add rx into the real set
		
		return spill;
	}

}
