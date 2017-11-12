import java.util.ArrayList;
import java.util.Random;

import peersim.config.Configuration;
import peersim.core.Control;
import peersim.core.Network;
import peersim.core.Node;

public class Oracle implements Control{
	// choosing a miner per cycle with probability based on the the calculating power
	
	/*
	 * Parameter
	 */
	private static final String PAR_PROT = "protocol";
	
	/*
	 * Field
	 */
	private static int pid;
	
	// Constructor
	public Oracle(String prefix) {
		pid = Configuration.getPid(prefix + "." + PAR_PROT);

	}
	
	@Override
	public boolean execute() {
		// oracle chooses a miner per cycle
		// random which kind of miners will be chosen
		ArrayList<Integer> ArrayOfTheSameKindMiner = new ArrayList<>();
		
		String Power = null;
		Random rn = new Random();
		Integer RandomKindOfMiner = rn.nextInt(10) + 1;
		
		if(RandomKindOfMiner >= 7) Power = "ASIC";
		else if(RandomKindOfMiner >= 4) Power = "FPGA";
		else if(RandomKindOfMiner >= 2) Power = "GPU";
		else if(RandomKindOfMiner == 1) Power = "CPU";
		
		for(int i=0;i<Network.size();i++) {
			Node currNode = Network.get(i);
			protocolTinyCoin currProtocol = (protocolTinyCoin) currNode.getProtocol(pid);
			if(currProtocol.kindOfMiner.equals(Power))
				ArrayOfTheSameKindMiner.add(i);
		}
		
		Integer RandMiner = rn.nextInt(ArrayOfTheSameKindMiner.size()-1);
		
		Node chosenMiner = Network.get(ArrayOfTheSameKindMiner.get(RandMiner));	// choose the miner in list of miner having the same kind.
		protocolTinyCoin protocolChosenMiner = (protocolTinyCoin) chosenMiner.getProtocol(pid);
		protocolChosenMiner.chosenMinerByOracle = true;
		
		return false;
	}

}
