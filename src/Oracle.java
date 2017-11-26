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
	private static final String PAR_DELAYBLOCKTIME = "delayBlockTime";
	
	/*
	 * Field
	 */
	private static int pid;
	private static int stepTime;
	private static int delayBlockTime;
	int setTime;
	
	// Constructor
	public Oracle(String prefix) {
		pid = Configuration.getPid(prefix + "." + PAR_PROT);
		stepTime = Configuration.getInt(prefix + "." + "step");
		delayBlockTime = Configuration.getInt(prefix + "." + PAR_DELAYBLOCKTIME);
		setTime = delayBlockTime; 
	}
	
	@Override
	public boolean execute() {
		// oracle chooses a miner per cycle
		// random which kind of miners will be chosen
		System.out.println("Block Time: " + delayBlockTime);
		
		if(delayBlockTime == 0) {
			ArrayList<Integer> ArrayOfTheSameKindMiner = new ArrayList<>();
			
			String Power = null;
			Random rn = new Random();
			Integer RandomKindOfMiner = rn.nextInt(10) + 1;
			
			if(RandomKindOfMiner >= 7) Power = "ASIC";
			else if(RandomKindOfMiner >= 4) Power = "FPGA";
			else if(RandomKindOfMiner >= 2) Power = "GPU";
			else if(RandomKindOfMiner == 1) Power = "CPU";
			
			System.out.println("Oracle _ Power: " + Power);
			
			for(int i=0;i<Network.size();i++) {
				Node currNode = Network.get(i);
				protocolTinyCoin currProtocol = (protocolTinyCoin) currNode.getProtocol(pid);
				
				if(currProtocol.kindOfMiner.equals(Power))
					ArrayOfTheSameKindMiner.add(i);
			}
			
			Integer RandMiner = 0;
			if(ArrayOfTheSameKindMiner.size() > 0)
				RandMiner = rn.nextInt(ArrayOfTheSameKindMiner.size());
				
			System.out.println("Rand: " + RandMiner + " size: " + ArrayOfTheSameKindMiner.size());
			Node chosenMiner = Network.get(ArrayOfTheSameKindMiner.get(RandMiner));	// choose the miner in list of miner having the same kind.
			protocolTinyCoin protocolChosenMiner = (protocolTinyCoin) chosenMiner.getProtocol(pid);
			protocolChosenMiner.chosenMinerByOracle = true;
			delayBlockTime = setTime;
		} else
			delayBlockTime -= stepTime;
		
			//delayBlockTime = delayBlockTime - stepTime;
		// return true if we need to stop
		// false for O.W
		return false;
	}

}
