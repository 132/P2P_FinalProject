import peersim.vector.SingleValueHolder;
import peersim.config.*;
import peersim.core.*;
import peersim.transport.Transport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

import peersim.cdsim.CDProtocol;
import peersim.edsim.EDProtocol;

public class protocolTinyCoin extends SingleValueHolder implements CDProtocol, EDProtocol{
	
	Hashtable<String, Transaction> TransPool;
	Integer Amount;
	Integer ID;
	boolean isSelfish;
	String kindOfMiner  = "";
	boolean chosenMinerByOracle;
	MainBlockChain publicBlockChain;
	//ArrayList<MainBlockChain> tempPublicBlockChain = new ArrayList<>();
	
	ArrayList<Block> tempBlockChain = new ArrayList<>();
	// it should be in case this is a selfish
	MainBlockChain privateBlockChain;
	Integer privateBranchLen;
	//Hashtable<String, Transaction> privateTransPool;
	
	// obverser
	Integer theNumberOfFork = 0;
	Integer theTimeOfSolving;
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
		
System.out.println("Node is sent: " + node.getID());

		if(event instanceof Transaction ) {
			// forward to its neighbors if this transaction does not exist in TransPool, BlockChain (public and private)
			Transaction transactionMessage = (Transaction) event;
			
			// add a new transaction to the TransPool
			
			if(isSelfish) {
				if(!TransPool.containsValue(transactionMessage) && !publicBlockChain.containTransaction(transactionMessage) && !privateBlockChain.containTransaction(transactionMessage) && checkTempBlockChain(transactionMessage) > 0) {
					this.TransPool.put(transactionMessage.IDtrans, transactionMessage);

					Linkable linkable = (Linkable) node.getProtocol(FastConfig.getLinkable(pid));
					if(linkable.degree()>0) {
						Transport transport = ((Transport)node.getProtocol(FastConfig.getTransport(pid)));
						for(int i=0;i<linkable.degree();i++)
							transport.send(node, linkable.getNeighbor(i),event , pid);
					}
				}
			} else {
				if(!TransPool.containsValue(transactionMessage) && !publicBlockChain.containTransaction(transactionMessage) && checkTempBlockChain(transactionMessage) > 0) {//&& !privateBlockChain.containTransaction(transactionMessage)) {	// need to insert the conditions for satisfying
				// if(!public_blockchains.contains(t)&&!containedIntoPrivateChain(t)&&this.UTXO.putIfAbsent(t.identifier,t)==null)
				// check the transaction existing public vs private
					this.TransPool.put(transactionMessage.IDtrans, transactionMessage);

					Linkable linkable = (Linkable) node.getProtocol(FastConfig.getLinkable(pid));
					if(linkable.degree()>0) {
						Transport transport = ((Transport)node.getProtocol(FastConfig.getTransport(pid)));
						for(int i=0;i<linkable.degree();i++)
							transport.send(node, linkable.getNeighbor(i),event , pid);
					}
				}
			}

		// check event. That is the Block
		} else if(event instanceof Block){
			// remove transaction in the Block added from TransPool
			Block BlockMessage = (Block) event;
			Node blockMiner = Network.get(BlockMessage.MinerID);
			protocolTinyCoin protocolBlockMiner = (protocolTinyCoin) blockMiner.getProtocol(pid);
	
			// remove transactions in case of block of private or block of public
//			if(!protocolBlockMiner.isSelfish)
			TransPool.keySet().removeAll(BlockMessage.Trans.keySet());
			//else if(protocolBlockMiner.isSelfish)
			//	privateTransPool.keySet().removeAll(BlockMessage.Trans.keySet());
				
				// need to check the mID of BlockMessage is the selfish or not
			//	Integer MinerIDOfBlock = BlockMessage.MinerID;
			//	Node NodePrevMinerNode = Network.get(MinerIDOfBlock);
			//	protocolTinyCoin protocolPrevMinerBlock = (protocolTinyCoin) NodePrevMinerNode.getProtocol(pid);
			
			// check the receiver is the selfish or honest
			
			//		keep for selfish
			if(isSelfish && !publicBlockChain.containBlock(BlockMessage)) {	//	&& !protocolPrevMinerBlock.isSelfish
				// in case of selfish it will stop the block from the honest and transfer their block
				Integer DeltaPrev = privateBlockChain.getSize() - publicBlockChain.getSize();
				// append new block to public chain
System.out.println("Node is Selfish: " + this.ID);		
				// add the public block later
				//publicBlockChain.addReceviedBlock(BlockMessage);
				// need to check about trans exsiting or not in block will be published
System.out.println("DeltaPrev: " + DeltaPrev);
				if(DeltaPrev==0) {
					// honest win
System.out.println("Deltaprev ==0 ");					
					privateBlockChain = publicBlockChain;
					privateBranchLen = 0;
				} else if(DeltaPrev == 1 ) {
System.out.println("Deltaprev == 1 ");					
					// same length -> try the luck
					publishLastBlock(node, pid);
				} else if(DeltaPrev == 2) {
System.out.println("Deltaprev ==2 ");					
					// pool more than 1 so publish it immediately
					pulishAll2Public(node, pid);
					privateBranchLen = 0;
				} else if(DeltaPrev > 2){
System.out.println("Deltaprev == else > 2 ");					
					// Pool leads more than 1 block
					publishFirstBlock(node, pid);
				} 
			}
			// receiver is the honest nodes and normal nodes
			// check the block is existed before or not
			
System.out.println("Node is received: " + this.ID + " Block ID: " + BlockMessage.ID + " MinerID of Block: " + BlockMessage.MinerID);
System.out.println("======================Before add new Block===============================================================================================");
for(int i=0;i<Network.size();i++) {
	System.out.println("The Node: " + ((protocolTinyCoin) Network.get(i).getProtocol(pid)).ID);
	((protocolTinyCoin) Network.get(i).getProtocol(pid)).publicBlockChain.mainTree.getAllNode();
}
System.out.println("=====================================================================================================================");
for(int i =0;i<publicBlockChain.mainTree.data.size(); i++) {
	System.out.println("Block in publicChain: " + publicBlockChain.mainTree.data.get(i).ID + " MinerIDofBLock: " + publicBlockChain.mainTree.data.get(i).MinerID + " PreviousBlockID: " + publicBlockChain.mainTree.data.get(i).previousID);
}
System.out.println("--------------------------- end Block in publicChain ");
			

			// need to check the exist of Block in 2 chain
System.out.println("Check the condition " + publicBlockChain.containBlock(BlockMessage));
			if(publicBlockChain.containBlock(BlockMessage) == false && !tempBlockChain.contains(BlockMessage)) {
System.out.println("Not existing in the publicChain and tempChain");
				
				// add to publicBlockChain or tempBlockChain
				// check the previous Block
				if(publicBlockChain.containBlock(BlockMessage.previousID)) {
					
					publicBlockChain.addReceviedBlock(BlockMessage);
					// check if there are some Block in buffer
					// add blocks to the new one
				
					if(tempBlockChain.size() > 0) {
System.out.println("The # of tempBlockChain: " + tempBlockChain.size());							
						publishInTempBlockChain(BlockMessage);
					}
					
						//publishTempBlockChain(BlockMessage, node, pid);
						
				} else {
					// public Block Chain does not have the previous Block of the new Block
					// if the temp is empty or 
					//int positionTreefornewBlock = containTheBlockTempBC(BlockMessage);
System.out.println("The previous Block does not exsits");
					//if(tempBlockChain.size() == 0){
System.out.println("Create the new tempPublicChain");						
						tempBlockChain.add(BlockMessage);
				/*		MainBlockChain newMainTree = new MainBlockChain();
						newMainTree.mainTree.data.add(BlockMessage);
						tempPublicBlockChain.add(newMainTree);
					} else {
System.out.println("Add to temp PublicBlock Chain because of pID: " + BlockMessage.previousID);						
						tempPublicBlockChain.get(positionTreefornewBlock).addReceviedBlock(BlockMessage);
					}
				*/		
				}
				Linkable linkable = (Linkable) node.getProtocol(FastConfig.getLinkable(pid));
				if(linkable.degree()>0) {
					Transport transport = ((Transport)node.getProtocol(FastConfig.getTransport(pid)));					
					for(int i=0;i<linkable.degree();i++) {
						transport.send(node, linkable.getNeighbor(i),event , pid);
						System.out.println("Propagation the new Block to Node: " + ((protocolTinyCoin) linkable.getNeighbor(i).getProtocol(pid)).ID + " from: " + this.ID);
					}
				}
								
			} else { System.out.println("The node exists this Block: " + this.ID);}
System.out.println("=====================================================================================================================");
publicBlockChain.mainTree.getAllNode();
System.out.println("=====================================================================================================================");						
for(int i=0;i<tempBlockChain.size();i++)
	System.out.println("Temp Block Chain: " + tempBlockChain.get(i).ID);
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
		int amountOfSendding = CommonState.r.nextInt(10);

		// need to check if this amountOfSendding < Amount if yes we will create a transaction
		if( 0 < amountOfSendding && amountOfSendding < Amount && CommonState.r.nextBoolean()) {
			// create a IDtransaction for each transaction and create a transaction
			String IDtransaction = sender.getIndex() + ":" + amountOfSendding + ":" + receiver.getIndex();
			Transaction newTransaction = new Transaction(sender, receiver, amountOfSendding, IDtransaction);
			
			// Add newTransaction to TransPool
			this.TransPool.put(IDtransaction, newTransaction);

			// Add and minus amountOfSendding
			this.Amount = Amount - amountOfSendding;
			protocolTinyCoin protocolReceiver = (protocolTinyCoin) receiver.getProtocol(pid);
			protocolReceiver.Amount = Amount + amountOfSendding;
			
			// send this transaction to neighbor of the node
			// send2Neighbor(sender, newTransaction, pid);
			Linkable link2NeighborOfSender = (Linkable) sender.getProtocol(FastConfig.getLinkable(pid));
			if(link2NeighborOfSender.degree()>0) {
				Transport transport = ((Transport)sender.getProtocol(FastConfig.getTransport(pid)));
				for(int i=0;i<link2NeighborOfSender.degree();i++) {

					transport.send(node, link2NeighborOfSender.getNeighbor(i),newTransaction, pid);
				}
			}
		}
		// in case, a chosen miner
		if(chosenMinerByOracle) {
			// selfish miner creates a block -> 2 cases: 
			// in case the honest mines 
			// in case the selfish pool mine 
			// that means the selfish miners share the same pool
			
System.out.println("Node is chosen " + ID + "---------------------------------------------------------------------------");
			
			Integer DeltaPrev = 0; 
			if(isSelfish) {
				DeltaPrev = privateBlockChain.getSize() - publicBlockChain.getSize();
System.out.println("Delta Prev and privateLen: " + DeltaPrev + ", " + privateBranchLen);				
				if(DeltaPrev <= 0 && privateBranchLen ==0) {
					privateBlockChain = new MainBlockChain(publicBlockChain);
System.out.println("Assign the Public to Private +++++++++++++++++++");					
				}
				
				// Selfish will do
				// if there is not a block belonging private chain before
				
				//Integer DeltaPrev = privateBlockChain.getSize() - publicBlockChain.getSize();
	
				// create a block and add to the private chain
				// append new block to private chain
				Integer preBlockID = privateBlockChain.getPreviousBlockID();
				Integer minerID = this.ID;
				//Block newBlock = new Block(preBlockID, minerID, privateTransPool);
System.out.println("The # of Transactions for new Block: " + TransPool.size());
System.out.println("The Previous Block: " + preBlockID);				
				Block newBlock = new Block(preBlockID, minerID, new Hashtable<>(TransPool));
				newBlock.privateBlock = true;
				// remove in TransPool transactions belonging in new Block
				removeTransactionBlock(TransPool, newBlock);
				
				// because It should be turned to false for next cycle
				this.chosenMinerByOracle = false;
System.out.println("++++++++++++++++++++++++++ Public Chain Before Private Chain added ++++++++++++++++++");
publicBlockChain.mainTree.getAllNode();
System.out.println("++++++++++++++++++++++++++ ++++ ++++++++++++++++++");				
				// add this new Block to private Chain
System.out.println("++++++++++++++++++++++++++ Private Chain Before Private Chain added ++++++++++++++++++");
privateBlockChain.mainTree.getAllNode();
System.out.println("++++++++++++++++++++++++++ ++++ ++++++++++++++++++");
				
				privateBlockChain.addCreatedBlock(newBlock);
				privateBranchLen++;
				
System.out.println("++++++++++++++++++++++++++ Private Chain After Private Chain added ++++++++++++++++++");
privateBlockChain.mainTree.getAllNode();
System.out.println("++++++++++++++++++++++++++ ++++ ++++++++++++++++++");

System.out.println("++++++++++++++++++++++++++ Public Chain After Private Chain added ++++++++++++++++++");
publicBlockChain.mainTree.getAllNode();
System.out.println("++++++++++++++++++++++++++ ++++ ++++++++++++++++++");
				//  this part belong to the algorithm but we can  ????????????????????
				
				// need to check the condition because of the DeltaPrev == 0 and privateBranchlen == 2
				// that means if the privateChain > publicChain 2 Blocks publish these 2 blocks
				if(DeltaPrev == 0 && privateBranchLen == 2) {
					// publish all the private Chain
					// send to all neighbors 
System.out.println("Publish the all the privateChain");					
					pulishAll2Public(node, pid);
					privateBranchLen = 0;
				}
				
				// mine the new head of private chain
				// only send to other selfish to sync the private chain or pool
				
			} else {
				// this is selfish but the privateChain did not have any other block => behave like the hosnest block
				// honest Miner
				Integer preBlockID = publicBlockChain.getPreviousBlockID(); // find the previous Block	.......................
				Integer minerID = this.ID;
System.out.println("The # of Transactions for new Block: " + TransPool.size());				
				Block newBlock = new Block(preBlockID, minerID, new Hashtable<>(TransPool));

				// add more money to the miner
				this.Amount = Amount + newBlock.getAllReward();
				// add this new Block to BLOCKCHAIN
				publicBlockChain.addCreatedBlock(newBlock);
				removeTransactionBlock(TransPool, newBlock);
				//because It should be turned to false for next cycle
				this.chosenMinerByOracle = false;
				// send the block to its neighbor
				// get the longest chain in block chain and send to its neighbor
				Linkable link2NeighborOfSender = (Linkable) sender.getProtocol(FastConfig.getLinkable(pid));
				if(link2NeighborOfSender.degree()>0) {
					Transport transport = ((Transport)sender.getProtocol(FastConfig.getTransport(pid)));
System.out.println("The number of Neighbors: " + link2NeighborOfSender.degree());					
					for(int i=0;i<link2NeighborOfSender.degree();i++) {
						transport.send(sender, link2NeighborOfSender.getNeighbor(i),newBlock, pid);

System.out.println("A honest Block in NextCycle protocol to Node: " + ((protocolTinyCoin) link2NeighborOfSender.getNeighbor(i).getProtocol(pid)).ID + " From the Node: " + this.ID);
					}
				}
				
			}
			// assign the miner for next cycle
			
			
			// Selfish - has new trans
			// Honest - has old trans ->
			
		}
		// it can be considered that if one block in the longest chain, it can be sent 
	}
	
	private void removeTransactionBlock(Hashtable<String, Transaction> transP, Block b) {
		transP.keySet().removeAll(b.Trans.keySet());
	}
	public void pulishAll2Public(Node node, int pid) {
		// publish all blocks on private chain
		// calculate again the reward from the miners
		while(privateBranchLen > 0) {
			publishFirstBlock(node, pid);
		}
	}
	
	public void publishLastBlock(Node node, int pid) {
		// take the last block
		Block theLastBlock = privateBlockChain.popTheLastBlock();
		
		// add more money to the miner
		// Integer IDminerOfTheLastBlock = theLastBlock.MinerID;
		if(this.ID == theLastBlock.MinerID)
			this.Amount = Amount + theLastBlock.getAllReward();
		
		publicBlockChain.addReceviedBlock(theLastBlock);
		this.privateBranchLen--;
		
		// propagation the block to neighbors
		Linkable linkNeighbor = (Linkable) node.getProtocol(FastConfig.getLinkable(pid));
		if(linkNeighbor.degree() > 0) {
			Transport transport = (Transport) node.getProtocol(FastConfig.getTransport(pid));
			for(int i=0;i < linkNeighbor.degree();i++) {
				transport.send(node, linkNeighbor.getNeighbor(i), theLastBlock, pid);
System.out.println("A selfish Block in  protocol to Node: " + ((protocolTinyCoin) linkNeighbor.getNeighbor(i).getProtocol(pid)).ID);
			}
		}
		
	}
	
	public void publishFirstBlock(Node node, int pid) {
System.out.println("============================ private Block Chain before Take first ======================");
privateBlockChain.mainTree.getAllNode();
System.out.println("=========================================================");
		// take the first block
System.out.println("The len of privateBlockChain " + privateBranchLen);
		Block theFirstBlock = privateBlockChain.popTheFirstBlock(privateBranchLen);
System.out.println("The first Block Miner ID: " + theFirstBlock.MinerID + " Block ID: " + theFirstBlock.ID);		

System.out.println("============================ private Block Chain after Take first ======================");
privateBlockChain.mainTree.getAllNode();
System.out.println("=========================================================");
		// add more money to the miner
		if(this.ID == theFirstBlock.MinerID)
			this.Amount = Amount + theFirstBlock.getAllReward();
		
		publicBlockChain.addReceviedBlock(theFirstBlock);
		this.privateBranchLen--;
		
		// propagation the block to neighbors
		Linkable linkNeighbor = (Linkable) node.getProtocol(FastConfig.getLinkable(pid));
		if(linkNeighbor.degree() > 0) {
			Transport transport = (Transport) node.getProtocol(FastConfig.getTransport(pid));
			for(int i=0;i < linkNeighbor.degree();i++) {
				transport.send(node, linkNeighbor.getNeighbor(i), theFirstBlock, pid);
System.out.println("A selfish Block in protocol to Node: " + ((protocolTinyCoin) linkNeighbor.getNeighbor(i).getProtocol(pid)).ID);
			}
		}
	}
/*	
	public void addBlock(Block in) {
		// we only add the block into public Block Chain in case of the previousBlock is equal to the last longest Block
		Block previousBlock = publicBlockChain.getLongestPath().data.get(publicBlockChain.getLongestPath().data.size()-1);
		if(in.previousID == previousBlock.ID)
			publicBlockChain.addCreatedBlock(in);
		else
			tempPublicBlockChain.addCreatedBlock(in);
	}
*/	
	public void TheNumeberOfFork() {
		theNumberOfFork = publicBlockChain.getTheNumberOfFork();
	}

	public void TheTimeOfSolving() {
		theTimeOfSolving = publicBlockChain.getTimeOfSolvingFork();	
	}

	public void change2Selfish() {
		this.isSelfish = true;
		this.privateBlockChain = new MainBlockChain();
		this.privateBranchLen = 0;
	}

	public void publishInTempBlockChain(Block in) {
		for(int i=0;i<tempBlockChain.size();i++) {
			if(tempBlockChain.get(i).previousID == in.ID) {
				Block b = tempBlockChain.get(i);
				publicBlockChain.addReceviedBlock(b);
				tempBlockChain.remove(i);
				i--;
				publishInTempBlockChain(b);
				
			}
		}
	}
	
	public int checkTempBlockChain(Transaction in) {
		for(int i=0;i < tempBlockChain.size();i++)
				if(tempBlockChain.get(i).checkTrans(in))
					return 1;
		return 0;
	}
/*
	public int containTheBlockTempBC(Block in) {
		for(int i=0;i<tempPublicBlockChain.size();i++) {
			if(tempPublicBlockChain.get(i).containBlock(in))
				return i;
		}
		return -1;
	}
	
	private void publishTempBlockChain(Block blockMessage, Node node, int pid) {
		
		for(int i=0;i<tempPublicBlockChain.size();i++) {
			if(blockMessage.ID == tempPublicBlockChain.get(i).mainTree.data.get(0).previousID) {
				addTempTree2PublicChain(i);
				
			}
		}
		
	}
	
	public void addTempTree2PublicChain(int position) {
		for(int i=0;i<tempPublicBlockChain.get(position); i++) {
			
		}
	}
*/
	public void send2Neighbor(Block b, Node node, int pid) {
		Linkable link2NeighborOfSender = (Linkable) node.getProtocol(FastConfig.getLinkable(pid));
		if(link2NeighborOfSender.degree()>0) {
			Transport transport = ((Transport)node.getProtocol(FastConfig.getTransport(pid)));
			for(int i=0;i<link2NeighborOfSender.degree();i++) {
				transport.send(node, link2NeighborOfSender.getNeighbor(i),b, pid);
			}
	
		}
	}
	
}
