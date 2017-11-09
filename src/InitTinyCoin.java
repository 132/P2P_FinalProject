
import java.util.ArrayList;
import java.util.Hashtable;

import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Control;
import peersim.core.Network;
import peersim.core.Node;

public class InitTinyCoin implements Control{
	// Parameters 
	private static final String PAR_PROT = "protocol";
	private static final String PAR_SELFISH = "percentageOfSelfish";
	// Fields
	private static int pid;
	private static double percentageOfSelfish;
	private Integer NoOfTotalMiner = 0;
	private Integer NoOfSelfishMiner;
	private Hashtable<Integer, String> SetKindMiners = new Hashtable<>();
	
	// should we need a number of miners
	
	// CPU GPU FPGA ASIC
	
	public InitTinyCoin(String prefix) {
		// get the kind of the protocol
		pid = Configuration.getPid(prefix + "." + PAR_PROT);
		// get the percentage of selfish 
		percentageOfSelfish = Configuration.getDouble(prefix + "." + PAR_SELFISH);
		SetKindMiners.put(0, "CPU");
		SetKindMiners.put(1, "GPU");
		SetKindMiners.put(2, "FPGA");
		SetKindMiners.put(3, "ASIC");
	}
	

	@Override
	public boolean execute() {
		ArrayList<Integer> arrayMiner = new ArrayList<>();
		// run for every node in the network
		for(int i=0;i<Network.size();i++) {
			Node curNode = Network.get(i);
			protocolTinyCoin protocolOfCurrentNode = (protocolTinyCoin) curNode.getProtocol(pid);
			protocolOfCurrentNode.ID = i;
			protocolOfCurrentNode.Amount = CommonState.r.nextDouble();
			//protocolOfCurrentNode.isSelfish = false;
			// protocolOfCurrentNode.kindOfMiner = null;
			protocolOfCurrentNode.chosenMinerByOracle = false;
			protocolOfCurrentNode.publicBlockChain = new MainBlockChain();
			
			// random node is the miner and random kinds of miner
			if(CommonState.r.nextBoolean()) {
				// random type of Miner
				// greater than or equal 0 and less than 4
				protocolOfCurrentNode.kindOfMiner = SetKindMiners.get(CommonState.r.nextInt(4));
				// If need we can choose the distribution of kinds Miners
				// count the number of miners
				arrayMiner.add(i);
				NoOfTotalMiner++;
			}
		}
		
		// divide the number of selfish miners and honest miners
		NoOfSelfishMiner = (int) Math.round(NoOfTotalMiner*percentageOfSelfish);
		// chosing some miners to become selfish miner
		for(int i=0;i<NoOfSelfishMiner;i++) {
			// random in miner set to choose the selfish miner
			Node selfCurrNode = Network.get(arrayMiner.get(CommonState.r.nextInt(arrayMiner.size()-1)));
			protocolTinyCoin protocolSelfCurrNode = (protocolTinyCoin) selfCurrNode.getProtocol(pid);
			// set the miner is iseflish
			protocolSelfCurrNode.isSelfish = true;		
			protocolSelfCurrNode.privateBlockChain = protocolSelfCurrNode.publicBlockChain;
		}
		
		return false;
	}
	

}
