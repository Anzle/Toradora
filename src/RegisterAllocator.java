import java.util.ArrayList;


public class RegisterAllocator {
	protected ArrayList<Instruction> inputInst;
	protected ArrayList<Instruction> outputInst;
	protected RegisterList regList;
	protected int  feasibleSize; 
	protected int realSize;
	
	
	protected void loadFromMemory(Register rx){
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
	
	protected void storeToMemory(Register rx){
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

}
