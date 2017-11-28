import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import peersim.config.Configuration;
import peersim.core.Control;
import peersim.core.Network;
import peersim.core.Node;

public class observerTinyCoin implements Control{
	

	/*
	 * Parameter
	 */
	private static final String PAR_PROT = "protocol";
	//private static final String PAR_STEP = "step";
	/*
	 * Field
	 */
	private static int pid;
	//private static int noStep;
	
	Integer theNumberOfFork = 0;
	Integer theTimeOfSolving = 0;
	Integer currStep = 0;
	
	// Constructor
	public observerTinyCoin(String prefix) {
		pid = Configuration.getPid(prefix + "." + PAR_PROT);
	//	noStep = Configuration.getInt(prefix + "." + PAR_STEP);
	}
	
	
	
	// compue_distribution
	
	
	@Override
	public boolean execute() {
		
		String NameFile = "Data.txt";
		ArrayList<String> lines = new ArrayList<>();
		
		theNumberOfFork = 0;
		theTimeOfSolving = 0;
		
		for(int i=0;i<Network.size();i++) {
			Node currNode = Network.get(i);
			protocolTinyCoin protocolCurrNode = (protocolTinyCoin) currNode.getProtocol(pid);
			protocolCurrNode.TheNumeberOfFork();
			theNumberOfFork += protocolCurrNode.theNumberOfFork;
		
			protocolCurrNode.TheTimeOfSolving();
			theTimeOfSolving += protocolCurrNode.theTimeOfSolving;
		}
		
		//System.out.println("The Number of Fork: " + theNumberOfFork / Network.size() + " The number of Time: " + theTimeOfSolving/ Network.size());
		System.out.println("The Number of Fork: " + theNumberOfFork + " The number of Time: " + theTimeOfSolving);
		lines.add("\nThe number of Fork in BlockChain: " + theNumberOfFork);
		lines.add("\nThe time to solve the Fork in BlockChain: " + theTimeOfSolving);
  		try {
  			File file = new File(NameFile);
  			file.createNewFile();  				
  		     // creates a FileWriter Object
  			FileWriter writer = new FileWriter(file); 
  		      
  		      // Writes the content to the file
  			for(String f: lines)	writer.write(f+"\n");
  			writer.flush();
  			writer.close();
  		} catch (IOException e) {
  			// TODO Auto-generated catch block
  			e.printStackTrace();
  		}
	  	//	currStep = 0;
	//	} else
	//		currStep++;
		return false;
	}

}
