import peersim.vector.SingleValueHolder;
import peersim.config.*;
import peersim.core.*;
import peersim.transport.Transport;

import java.util.HashMap;
import java.util.Hashtable;

import peersim.cdsim.CDProtocol;
import peersim.edsim.EDProtocol;

public class protocolTinyCoin extends SingleValueHolder implements CDProtocol, EDProtocol{
	
	Hashtable<String, Transaction> TransPool;
	
	double Amount;
	
	Integer ID;
	
	boolean isSelfish;
	
	String kindOfMiner;
	
	boolean chosenMinerByOracle;
	
	MainBlockChain publicBlockChain;
	
	// it should be in case this is a selfish
	MainBlockChain privateBlockChain;
	
	Integer privateBranchLen;
	
	/*
	 *  Parameters
	 */
	protected static final String PAR_QUOTA = "quota";
	
	/*
	 * Fields
	 */
	private final double quota_value;
	protected double quota; // current cycle quota
	

	public protocolTinyCoin(String prefix) {
		super(prefix);
		
		this.publicBlockChain = new MainBlockChain();
		this.privateBlockChain = publicBlockChain;
		this.privateBranchLen = 0;
		
		this.TransPool = new Hashtable<String, Transaction>();
		this.isSelfish = false;
		
		/*
		 * Take parameter from config file
		 */
		quota_value = (Configuration.getInt(prefix + "." + PAR_QUOTA,1));
		quota = quota_value;
	}

	
	// send message is the propagration of block and modify of block
	
	// ED protocol
	// Standard method to define a process
	@Override
	public void processEvent(Node node, int pid, Object event) {
		
		//AverageMessage aem = (AverageMessage)event;
		//((Transport)node.getProtocol(FastConfig.getTransport(pid))).send(node, aem.sender, arg2, arg3);
/*		AverageMessage aem = (AverageMessage)event;
		if(aem.sender!= null) {
			((Transport)node.getProtocol(FastConfig.getTransport(pid))).send(node, aem.sender, new AverageMessage(value, null), pid);
			value = (value + aem.value)/2;
		}
		
*/
		// check that if the event is a Transaction or Block
		if(event instanceof Transaction ) {
			// forward to its neighbors if this transaction does not exist in TransPool, BlockChain (public and private)
			if(true) {	// need to insert the conditions for satisfying
				Linkable linkable = (Linkable) node.getProtocol(FastConfig.getLinkable(pid));
				if(linkable.degree()>0) {
					Transport transport = ((Transport)node.getProtocol(FastConfig.getTransport(pid)));
					for(int i=0;i<linkable.degree();i++)
						transport.send(node, linkable.getNeighbor(i),event , pid);
				}
			}
		} else if(event instanceof Block){
			// remove transaction in the Block added from TransPool
			Block BlockMessage = (Block) event;
			TransPool.keySet().removeAll(BlockMessage.Trans.keySet());
			
			
			if(isSelfish) {
				// in case of selfish it will stop the block from the honest and transfer their block
			}
			// do sth
			// need a state flag to check the block is sent before or not 
			// if it is sent dont need to send again
		}
		
	}

	// CD protocol
	public void nextCycle(Node node, int pid) {
		// create a transaction
		Node peerNode = Network.get(CommonState.r.nextInt(Network.size()));
		Node IDsender = node;
		Node IDreceiver = peerNode;
		Double amountOfSendding = CommonState.r.nextDouble();
		
		// need to check if this amountOfSendding < Amount if yes we will create a transaction
		
		String IDtransaction = node.toString() + ":" + amountOfSendding.toString() + ":" + peerNode.toString();
		Transaction trans = new Transaction(IDsender, IDreceiver, amountOfSendding, IDtransaction);
		// Add newTransaction to TransPool
		this.TransPool.put(IDtransaction, trans);
		
		// Add and minus amountOfSendding
		this.Amount = Amount - amountOfSendding;
		
		protocolTinyCoin peerProtocol = (protocolTinyCoin) peerNode.getProtocol(pid);
		peerProtocol.Amount = Amount + amountOfSendding;
		
		// send this transaction to neighbor of the node
		Linkable linkable = (Linkable) node.getProtocol(FastConfig.getLinkable(pid));
		if(linkable.degree()>0) {
			Transport transport = ((Transport)node.getProtocol(FastConfig.getTransport(pid)));
			for(int i=0;i<linkable.degree();i++)
				transport.send(node, linkable.getNeighbor(i),trans , pid);
		}
		
		// in case, a chosen miner
		if(chosenMinerByOracle) {
			// selfish miner creates a block -> 2 cases: 
			// in case the honest mines 
			// in case the selfish pool mine 
			// that means the selfish miners share the same pool
			if(isSelfish) {
				// Selfish will do
				// if there is not a block belonging private chain before
				Integer DeltaPrev = privateBlockChain.getSize() - publicBlockChain.getSize();
				// append new block to private chain
				// ....
				Integer preBlock = 0; // find the previous Block
				Integer mID = this.ID;
				Integer pID = preBlock; 
				Hashtable<String, Transaction> T = TransPool;
				Block newBlock = new Block(pID, mID, T);
				// add more money to the miner
				this.Amount = Amount + newBlock.getAllReward();
				// add this new Block to BLOCKCHAIN
				privateBlockChain.addBlock(newBlock);
				
				if(DeltaPrev == 0 && privateBranchLen == 2) {
					// publish all the private Chain
					// send to all neighbors 
					privateBranchLen = 0;
				}
				// mine the new head of private chain
				privateBlockChain.pulisher2Public();
				
			}else {
				// honest Miner
				Integer preBlock = 0; // find the previous Block
				Integer mID = this.ID;
				Integer pID = preBlock; 
				Hashtable<String, Transaction> T = TransPool;
				Block newBlock = new Block(pID, mID, T);
				// add more money to the miner
				this.Amount = Amount + newBlock.getAllReward();
				// add this new Block to BLOCKCHAIN
				publicBlockChain.addBlock(newBlock);
			}
			// assign the 
			chosenMinerByOracle = false;
		}
		// send the block to its neighbor
		
		this.TransPool = new Hashtable<String, Transaction>();
		
		
	}
	
	public void change2Selfish() {
		this.isSelfish = true;
		this.privateBlockChain = publicBlockChain;
	}
	
/*	
	@Override
	public void nextCycle(Node node, int pid) {
		// TODO Auto-generated method stub
		Linkable linkable = (Linkable) node.getProtocol(FastConfig.getLinkable(pid));
		
		if(linkable.degree()>0) {
			Node peern = linkable.getNeighbor(CommonState.r.nextInt(linkable.degree()));
			if(!peern.isUp())	return;
			
			protocolTinyCoin peer = (protocolTinyCoin) peern.getProtocol(pid);
			
			((Transport)node.getProtocol(FastConfig.getTransport(pid))).
			
		}
			
	}
*/
	
	
}
