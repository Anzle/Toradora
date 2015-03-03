import java.util.ArrayList;


public class TopDownLR extends RegisterAllocator {

	
	public TopDownLR(ArrayList<Instruction> iI,ArrayList<Instruction> oI, RegisterList rL, int Kreg){
		inputInst = iI;
		outputInst = oI;
		regList = rL;
		feasibleSize = 3;
		realSize =  Kreg - feasibleSize;
		
		//Prepare the registers for allocation
		regList.setFeasRealSize(feasibleSize, realSize);
		
		/*
		 * Any remaining initializations
		 */
		String[] str = {"loadI", "1020", " ","r0" };
		outputInst.add(new Instruction(str, 0));
		
	}
	
	/** Perform Register allocation taking the Live Range of Registers into account*/
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
					
					if(regList.inRealRegister(rx) && !rx.isSpilled()){
						tok[j] = rx.getRealName(); //It's in a register, use it
					}
					else{
						if(rx.isSpilled()){//it has already been spilled, so load it
							regList.addToFeasible(rx, j);
							loadFromMemory(rx); //Generate load code
							tok[j] = rx.getRealName();
							//System.out.println(tok[j]);
						}
						else if(realSize == 0){ //we only have the feasible set
							
							rx.setSpilled();
							regList.addToFeasible(rx, j);
							//generate store code -> shouldn't have to do here
							tok[j] = rx.getRealName();
						}
						else{//This is our first time seeing this value
							//allocate it to a real register and spill if needed
							storeReal(rx, linenum);
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
				else if(realSize == 0){ //we only have the feasible set
					rx.setSpilled(); //if it is already true, this won't really matter
					regList.addToFeasible(rx, 1); //Use F1 to perform store operation
					tok[3] = rx.getRealName();
				}
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
	private void storeReal(Register rx, int linenumber){
		for(int i = 0; i < realSize; i++){
			if(regList.getfromReal(i) == null ){//we found an empty spot
				regList.addToReal(rx, i);
				return;
			}
		}
		//
		for(int i = 0; i < realSize; i++){
			if(regList.getfromReal(i) != null && regList.getfromReal(i).getLastLine() < linenumber){//we found an empty spot
				regList.addToReal(rx, i);
				return;
			}
		}
		//We need to spill something. Find the live range ending furthest away
		int realreg = regList.findLongestLive();
		Register spill = regList.getfromReal(realreg); //get the register we will spill
		storeToMemory(spill); //generate spill code. 
		
		regList.addToReal(rx, realreg); //add rx into the real set
		
	}
	
	/* Take a virtual register rx and allocate it to a real register
	 * Update the real set and generate spill code if needed
	 * */
	private void storeOutput(Register rx, int linenumber){
		for(int i = 0; i < realSize; i++){
			if(regList.getfromReal(i) == null ){//find an empty spot
				regList.addToReal(rx, i);
				return;
			}
		}
		
		int realreg = regList.findNextOutput(linenumber);
		
		//check if the register we got isn't dead and needs to be spilled
		if(regList.getfromReal(realreg).getLastLine() > linenumber){
			Register spill = regList.getfromReal(realreg); //get the register we will spill
			storeToMemory(spill); //generate spill code. 
		}
		
		regList.addToReal(rx, realreg); //add rx into the real set
		
	}
	
}//End of Class






















