
public class Block {
	public Integer ID;	// block header
	private Integer PreviousID; // previous block header
	private double Reward; // Constant reward (doesnot decrease over time)
	private double AmountOfAddition; // values is rewarded to the miner for each transaction
	private boolean Chosen;
	
	public void modifyBlock() {
		if(!Chosen)
			return;
		
		
	}
	
	// latency increase due to increasing block size, the block propagation time is increased 
	//by a constant amount for each transaction included in a block
		
	// reward and latency increase per transaction set by simulation
}
