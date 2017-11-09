import java.util.Random;

public class observerOracle {
	private Miners miner;
	
	/*
	 *  Parameters
	 */
	protected static final String PAR_NumberofNormalNodes = "normalNodes";
	protected static final String PAR_NumberofMiners = "miners";
	
	/*
	 * Fields
	 */
//	private final double normalNodes_values;
//	private final double miners_values;
	
	protected double normalNodes; // current cycle quota
	protected double miners; // current cycle quota
	
	
	
	public void chooseMiner() {
		Integer TheNumberOfNodes = 10;
		String Power;
		Random rn = new Random();
		Integer RandomKindOfMiner = rn.nextInt(10 - 1 + 1) + 1;
		
		if(RandomKindOfMiner >= 7) Power = "ASIC";
		else if(RandomKindOfMiner >= 4) Power = "FPGA";
		else if(RandomKindOfMiner >= 2) Power = "GPU";
		else if(RandomKindOfMiner == 1) Power = "CPU";
	}
}
