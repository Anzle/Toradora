import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.StringTokenizer;


public class alloc {

	public static void main(String[] args) {
		
	//Create the path to the input file
		int K = Integer.parseInt(args[0]);
		String allocator = args[1];
		String filename = args[2]; 
		
		//There aren't enough registers for this
		if(K < 3){
			System.err.println( "You have input to few registers. Allocators, must have atlease 3 registers. ");
			System.exit(0);
		}
		
		Path inputFile = FileSystems.getDefault().getPath(filename);
		
		RegisterList regList = new RegisterList();
		ArrayList<Instruction> inputInstructions= new ArrayList<Instruction>();
		ArrayList<Instruction> outputInstructions= new ArrayList<Instruction>();
		
		alloc.parseInstructions(inputFile, inputInstructions, regList); //parse iLOC code
		
		switch(allocator){
			case "s": //the simple allocator
				TopDownEaC eac = new TopDownEaC(inputInstructions, outputInstructions, regList, K);
				eac.allocateRegisters();
				break;
		
			case "t": //the top down with live range
				TopDownLR lr = new TopDownLR(inputInstructions, outputInstructions, regList, K);
				lr.allocateRegisters();
				break;
		
			case "b": //the bottom up
				BottomUp bu = new BottomUp(inputInstructions, outputInstructions, regList, K);
				bu.allocateRegisters();
				break;
		}
		
		for(int i=0; i< outputInstructions.size();i++)
			System.out.println(outputInstructions.get(i));
		
		//Input Instruction Test
		/*for(int i=0; i < inputInstructions.size(); i++)
			System.out.println(inputInstructions.get(i));
		
		System.out.println(regList.printVirtual()); //Registers input into the program
		*/

		
		
	}
	
	public static void parseInstructions(Path file, ArrayList<Instruction> inst, RegisterList reglist){
		StringTokenizer tok;
		String delim = " ,'\t'";
		int linenumber = 1;
		int offset = -4;
		
		Charset charset = Charset.forName("US-ASCII");
		try (BufferedReader reader = Files.newBufferedReader(file, charset)) {
		    String line = null;
		    while ((line = reader.readLine()) != null) {
		        //System.out.println(line);
		        
		        if(line.startsWith("//")) //Filter out comments
		        	continue;
		        
		        String[] input = {" "," "," "," "};
		        tok=new StringTokenizer(line, delim);
		        int i = 0;
		        while(tok.hasMoreTokens()){
		        	String tmp = tok.nextToken();
		        	
		        	if(tmp.equals("=>")){
		        		tmp =  tok.nextToken();
		        		input[3] = tmp;
		        	}
		        	else
		        		input[i] = tmp;
		        	
		        	if(i > 0 && tmp.startsWith("r")){//If i = 0, we are on the instruction name token
		        		reglist.addToVirtual(new Register(tmp, Integer.toString(offset), linenumber), linenumber);
		        		offset-=4;
		        	}
		        	i++;
		        }
		        
		        inst.add(new Instruction(input,linenumber++));
		        
		    }
		} catch (IOException x) {
		    System.err.format("IOException: %s%n", x);
		}
		
	}
	

}
