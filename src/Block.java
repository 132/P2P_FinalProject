import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;

public class Block {
	static Integer NoBlock =0;
	public Integer MinerID;
	
	public Integer ID;									 // block header
	public Integer previousID;							 // previous block header
	
	static Integer Reward = 10; 								 // Constant reward (doesnot decrease over time)
	static Integer RewardTrans = 1;					 // values is rewarded to the miner for each transaction
	static Integer LatencyTrans = 1;							// the latency of the block
	
	Hashtable<String, Transaction> Trans;			// ListOfTrans
	
	boolean privateBlock = false;
	
	public Block(Integer currID, Integer pID, Integer mID, Hashtable<String, Transaction> T) {
		this.MinerID = mID;
		this.Trans = T;
		this.previousID = pID;
		this.ID = currID;
	}
	
	public Block(Integer pID, Integer mID, Hashtable<String, Transaction> T) {
		NoBlock++;
		this.MinerID = mID;
		this.Trans = T;
		this.previousID = pID;
		this.ID = NoBlock;

	}
	public Block(Integer mID, Hashtable<String, Transaction> T) {
		NoBlock++;
		this.MinerID = mID;
		this.Trans = T;
		this.ID = NoBlock;
		
	}

	public void addpreBlock(Integer pID) {
		this.previousID = pID;
	}

	public boolean checkTrans(Transaction in) {	// return true if existing the trans
		return Trans.containsKey(in.IDtrans);
	}
	public void setPreviousB(Block in) {
		this.previousID = in.ID;
	}
	
	public Integer getLatency() {
		return Trans.size()*LatencyTrans;
	}
	
	public Integer getRewardFromTransactions() {
		return Trans.size()*RewardTrans;
	}
	
	public Integer getAllReward() {
		return Reward + getRewardFromTransactions();
	}
	
	public boolean containTransaction(Transaction in) {
		if(Trans.isEmpty())
			return false;		
		else if(Trans.contains(in))
			return true;
		else 
			return false;
	}
	
	// latency increase due to increasing block size, the block propagation time is increased 
	//by a constant amount for each transaction included in a block
		
	// reward and latency increase per transaction set by simulation
}
