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
	Hashtable<String, Transaction> privateTransPool;
	
	
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
		this.privateTransPool = TransPool;
		
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
	
	
	// sth like receivers
	@Override
	public void processEvent(Node node, int pid, Object event) {
		// check that if the event is a Transaction or Block
		if(event instanceof Transaction ) {
			// forward to its neighbors if this transaction does not exist in TransPool, BlockChain (public and private)
			Transaction transactionMessage = (Transaction) event;
			
			// add a new transaction to the TransPool
			
			if(!TransPool.containsValue(transactionMessage) && !publicBlockChain.containTransaction(transactionMessage) && !privateBlockChain.containTransaction(transactionMessage)) {	// need to insert the conditions for satisfying
			// if(!public_blockchains.contains(t)&&!containedIntoPrivateChain(t)&&this.UTXO.putIfAbsent(t.identifier,t)==null)
			// check the transaction existing public vs private
				this.TransPool.put(transactionMessage.IDtrans, transactionMessage);
				
				if(this.isSelfish) this.privateTransPool.put(transactionMessage.IDtrans, transactionMessage);
				
				Linkable linkable = (Linkable) node.getProtocol(FastConfig.getLinkable(pid));
				if(linkable.degree()>0) {
					Transport transport = ((Transport)node.getProtocol(FastConfig.getTransport(pid)));
					for(int i=0;i<linkable.degree();i++)
						transport.send(node, linkable.getNeighbor(i),event , pid);
				}
				//TransPool.remove(TransactionMessage.IDtrans);
			}
			
		// check event. That is the Block
		} else if(event instanceof Block){
			// remove transaction in the Block added from TransPool
			Block BlockMessage = (Block) event;
			Node blockMiner = Network.get(BlockMessage.MinerID);
			protocolTinyCoin protocolBlockMiner = (protocolTinyCoin) blockMiner.getProtocol(pid);
			
			// remove transactions in case of block of private or block of public
			if(!protocolBlockMiner.isSelfish)
				TransPool.keySet().removeAll(BlockMessage.Trans.keySet());
			else if(protocolBlockMiner.isSelfish)
				privateTransPool.keySet().removeAll(BlockMessage.Trans.keySet());
				
				// need to check the mID of BlockMessage is the selfish or not
			//	Integer MinerIDOfBlock = BlockMessage.MinerID;
			//	Node NodePrevMinerNode = Network.get(MinerIDOfBlock);
			//	protocolTinyCoin protocolPrevMinerBlock = (protocolTinyCoin) NodePrevMinerNode.getProtocol(pid);
			
			// check the receiver is the selfish or honest
			if(isSelfish) {	//	&& !protocolPrevMinerBlock.isSelfish
				// in case of selfish it will stop the block from the honest and transfer their block
				Integer DeltaPrev = privateBlockChain.getSize() - publicBlockChain.getSize();
				// append new block to public chain
				publicBlockChain.addBlock(BlockMessage);
				
				// need to check about trans exsiting or not in block will be published
				if(DeltaPrev==0) {
					// honest win
					privateBlockChain = publicBlockChain;
					privateBranchLen = 0;
				} else if(DeltaPrev == 1 ) {
					// same length -> try the luck
					publishLastBlock(node, pid);
				} else if(DeltaPrev == 2) {
					// pool more than 1 so publish it immediately
					pulishAll2Public(node, pid);
					privateBranchLen = 0;
				} else {
					// Pool leads more than 1 block
					publishFirstBlock(node, pid);
				} 
			}
			// receiver is the honest nodes and normal nodes
			// check the block is existed before or not
			if(!publicBlockChain.containBlock(BlockMessage)) {	
				Linkable linkable = (Linkable) node.getProtocol(FastConfig.getLinkable(pid));
				if(linkable.degree()>0) {
					Transport transport = ((Transport)node.getProtocol(FastConfig.getTransport(pid)));
					for(int i=0;i<linkable.degree();i++)
						transport.send(node, linkable.getNeighbor(i),event , pid);
				}
			}
			
			// // it can be considered that if one block in the longest chain, it can be sent
			
			// forward the block to its neighbor
			
			// do sth
			// need a state flag to check the block is sent before or not 
			// if it is sent dont need to send again
		}
		
	}

	// CD protocol
	public void nextCycle(Node node, int pid) {
		// create a transaction 
		Node sender = node;
		Node receiver = Network.get(CommonState.r.nextInt(Network.size()-1));;
		Double amountOfSendding = CommonState.r.nextDouble();
		
		// need to check if this amountOfSendding < Amount if yes we will create a transaction
		if(amountOfSendding < Amount) {
			// create a IDtransaction for each transaction and create a transaction
			String IDtransaction = sender.toString() + ":" + amountOfSendding.toString() + ":" + receiver.toString();
			Transaction newTransaction = new Transaction(sender, receiver, amountOfSendding, IDtransaction);
			
			// Add newTransaction to TransPool
			this.TransPool.put(IDtransaction, newTransaction);
			
			if(this.isSelfish) this.privateTransPool.put(IDtransaction, newTransaction);
			
			// Add and minus amountOfSendding
			this.Amount = Amount - amountOfSendding;
			protocolTinyCoin protocolReceiver = (protocolTinyCoin) receiver.getProtocol(pid);
			protocolReceiver.Amount = Amount + amountOfSendding;
			
			// send this transaction to neighbor of the node
			// send2Neighbor(sender, newTransaction, pid);
			Linkable link2NeighborOfSender = (Linkable) sender.getProtocol(FastConfig.getLinkable(pid));
			if(link2NeighborOfSender.degree()>0) {
				Transport transport = ((Transport)sender.getProtocol(FastConfig.getTransport(pid)));
				for(int i=0;i<link2NeighborOfSender.degree();i++)
					transport.send(node, link2NeighborOfSender.getNeighbor(i),newTransaction, pid);
			}
			//newTransaction.sent = true;
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
				
				// create a block and add to the private chain
				// append new block to private chain
				Integer preBlockID = publicBlockChain.getPreviousBlockID(); // find the previous Block	.......................
				Integer minerID = this.ID;
				Block newBlock = new Block(preBlockID, minerID, privateTransPool);

				// add this new Block to private Chain
				privateBlockChain.addBlock(newBlock);
				privateBranchLen++;
				
				if(DeltaPrev == 0 && privateBranchLen == 2) {
					// publish all the private Chain
					// send to all neighbors 
					pulishAll2Public(node, pid);
					privateBranchLen = 0;
				}
				// mine the new head of private chain
				// only send to other selfish to sync the private chain or pool
				Linkable link2NeighborOfSender = (Linkable) sender.getProtocol(FastConfig.getLinkable(pid));
				if(link2NeighborOfSender.degree()>0) {
					Transport transport = ((Transport)sender.getProtocol(FastConfig.getTransport(pid)));
					for(int i=0;i<link2NeighborOfSender.degree();i++) {
						// check for all neighbor if it is the selfish this node will send the newBlock
						Node neighborSelfishMiner = link2NeighborOfSender.getNeighbor(i);
						protocolTinyCoin protocolNeighborSelfishMiner = (protocolTinyCoin) neighborSelfishMiner.getProtocol(pid);
						if(protocolNeighborSelfishMiner.isSelfish)
							transport.send(sender, link2NeighborOfSender.getNeighbor(i),newBlock, pid);
					} 
				}	
				privateTransPool = new Hashtable<String, Transaction>();
			} else {
				// honest Miner
				Integer preBlockID = publicBlockChain.getPreviousBlockID(); // find the previous Block	.......................
				Integer minerID = this.ID;
				Block newBlock = new Block(preBlockID, minerID, TransPool);

				// add more money to the miner
				this.Amount = Amount + newBlock.getAllReward();
				
				// add this new Block to BLOCKCHAIN
				publicBlockChain.addBlock(newBlock);
				
				// send the block to its neighbor
				// get the longest chain in block chain and send to its neighbor
				Linkable link2NeighborOfSender = (Linkable) sender.getProtocol(FastConfig.getLinkable(pid));
				if(link2NeighborOfSender.degree()>0) {
					Transport transport = ((Transport)sender.getProtocol(FastConfig.getTransport(pid)));
					for(int i=0;i<link2NeighborOfSender.degree();i++)
						transport.send(sender, link2NeighborOfSender.getNeighbor(i),newBlock, pid);
				}
				TransPool = new Hashtable<String, Transaction>(); 
			}
			// assign the miner for next cycle
			this.chosenMinerByOracle = false;
			
			// Selfish - has new trans
			// Honest - has old trans ->
			this.TransPool = new Hashtable<String, Transaction>();
		}
		
		// it can be considered that if one block in the longest chain, it can be sent 
		
	}
	
	public void pulishAll2Public(Node node, int pid) {
		// publish all blocks on private chain
		// calculate again the reward from the miners
		while(!privateBlockChain.isEmpty())
			publishFirstBlock(node, pid);
	}
	
	public void publishLastBlock(Node node, int pid) {
		// take the last block
		Block theLastBlock = privateBlockChain.popTheLastBlock();
		
		// add more money to the miner
		// Integer IDminerOfTheLastBlock = theLastBlock.MinerID;
		if(this.ID == theLastBlock.MinerID)
			this.Amount = Amount + theLastBlock.getAllReward();
		
		// propagation the block to neighbors
		Linkable linkNeighbor = (Linkable) node.getProtocol(FastConfig.getLinkable(pid));
		if(linkNeighbor.degree() > 0) {
			Transport transport = (Transport) node.getProtocol(FastConfig.getTransport(pid));
			for(int i=0;i < linkNeighbor.degree();i++)
				transport.send(node, linkNeighbor.getNeighbor(i), theLastBlock, pid);
		}
		
	}
	
	public void publishFirstBlock(Node node, int pid) {
		// take the first block
		Block theFirstBlock = privateBlockChain.popTheFirstBlock();
		
		// add more money to the miner
		if(this.ID == theFirstBlock.MinerID)
			this.Amount = Amount + theFirstBlock.getAllReward();
		
		
		// propagation the block to neighbors
		Linkable linkNeighbor = (Linkable) node.getProtocol(FastConfig.getLinkable(pid));
		if(linkNeighbor.degree() > 0) {
			Transport transport = (Transport) node.getProtocol(FastConfig.getTransport(pid));
			for(int i=0;i < linkNeighbor.degree();i++)
				transport.send(node, linkNeighbor.getNeighbor(i), theFirstBlock, pid);
		}
		
	}
}
