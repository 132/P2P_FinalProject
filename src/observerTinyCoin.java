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
	private static final String PAR_PERCENTSELFISH = "percentageOfSelfish_";
	private static final String PAR_COMPUTATION = "ComputationalPower_";						
	private static final String PAR_TIMEBETWEEN2BLOCK = "TimeBlocks_";
	private static final String PAR_LATENCYNODE = "TheLatenciesNodes_";
	private static final String PAR_DEGREE = "degree_";
	
	//private static final String PAR_STEP = "step";
	/*
	 * Field
	 */
	private static int pid;
	private static int percentSelfish;
	private static int powerSelfish;
	private static int time2Blocks;
	private static int latencyNodes;
	private static int degree;
	//private static int noStep;
	
	Integer theNumberOfFork = 0;
	Integer theTimeOfSolving = 0;
	Integer currStep = 0;
	
	
	// Constructor
	public observerTinyCoin(String prefix) {
		pid = Configuration.getPid(prefix + "." + PAR_PROT);
		percentSelfish = Configuration.getInt(prefix + "." + PAR_PERCENTSELFISH);
		powerSelfish = Configuration.getInt(prefix + "." + PAR_COMPUTATION);						
		time2Blocks = Configuration.getInt(prefix + "." + PAR_TIMEBETWEEN2BLOCK);
		latencyNodes = Configuration.getInt(prefix + "." + PAR_LATENCYNODE);
		degree = Configuration.getInt(prefix + "." + PAR_DEGREE);
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
		
		lines.add("\nThe size of Network: " + Network.size());
		lines.add("\nThe percentage of selfish: " + percentSelfish);
		lines.add("\nThe power of selfish: " + powerSelfish);
		lines.add("\nThe time of 2 created blocks: " + time2Blocks);
		lines.add("\nThe latency of 2 nodes: " + latencyNodes);
		lines.add("\nThe number of degree in node: " + degree);
		
		lines.add("\nThe number of Fork in BlockChain: " + theNumberOfFork + " and the Avg: " + (float) theNumberOfFork/Network.size());
		lines.add("\nThe time to solve the Fork in BlockChain: " + theTimeOfSolving + " and the Avg: " + (float) theTimeOfSolving/Network.size());
  		try {
  			File file = new File(NameFile);
  			file.createNewFile();  				
  		     // creates a FileWriter Object
  			FileWriter writer = new FileWriter(file,true); 
  		      
  		      // Writes the content to the file
  			for(String f: lines)	writer.write(f);
  			writer.write("\n");
  			writer.flush();
  			writer.close();
  		} catch (IOException e) {
  			// TODO Auto-generated catch block
  			e.printStackTrace();
  		}
		return false;
	}

}
