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
		String filename = "block4.i"; //args[1];
		Path inputFile = FileSystems.getDefault().getPath("testdata", filename);
		
		int K = 10; //input registers
		RegisterList regList = new RegisterList();
		ArrayList<Instruction> inputInstructions= new ArrayList<Instruction>();
		ArrayList<Instruction> outputInstructions= new ArrayList<Instruction>();
		alloc.parseInstructions(inputFile, inputInstructions, regList); //parse iLOC code
		
		TopDownEaC eac = new TopDownEaC(inputInstructions, outputInstructions, regList, K);
		eac.allocateRegisters();
		
		//Input Instruction Test
		/*for(int i=0; i < inputInstructions.size(); i++)
			System.out.println(inputInstructions.get(i));
		
		System.out.println(regList.printVirtual()); //Registers input into the program
		*/
		for(int i=0; i< outputInstructions.size();i++)
			System.out.println(outputInstructions.get(i));
		
		
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
		        		reglist.addToVirtual(new Register(tmp, Integer.toString(offset), linenumber));
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
