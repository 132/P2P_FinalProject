import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;

public class MainBlockChain {
	
	TreeBlockChain mainTree;
	Integer theNumberOfFork =0;
	Integer theSumOfTime=0;
//	ArrayList<Block> headBlock;


	public MainBlockChain() {
		ArrayList<Block> in = new ArrayList<Block>();
		//in.add(new Block(0, 0, null));
		this.mainTree = new TreeBlockChain(in);
		this.mainTree.setPreviousSize(0);
	}
	
	// when a NODE creates a new Block which will be added to the Longest => find the Longest tree => get the last BlockID
	public void addCreatedBlock(Block newBlock) {
		// find the longest path
		TreeBlockChain addThisTree = getLongestPath();
		// the function will check the existing of the tree and add the new Block to that tree
		mainTree.check_add2Tree(addThisTree, newBlock);		
	}
	
	// when a NODE receives a new Block which will find a node in BlockChain or branch where it can be added
	public void addReceviedBlock(Block newBlock) {
		TreeBlockChain addThisTree = mainTree.getTheTreeContainPreBlock(newBlock);
System.out.println("-----------------Check the Tree before adding ----------------------------------------------------");
addThisTree.getAllNode();
System.out.println("--------------------------------------------------------------------------------------------------");
	// the function will check the existing of the tree and add the new Block to that tree
		mainTree.check_add2Tree(addThisTree, newBlock);
	}
	
	public TreeBlockChain getLongestPath() {
		TreeBlockChain LastMaxNode = mainTree;
		Integer MaxNodeSize = mainTree.getCurrentSize();	// 0 + mainTree.size();
		// add node to Queue
		ArrayList<TreeBlockChain> checkedNode = new ArrayList<>();
		
		mainTree.addChildrenQueue(checkedNode, MaxNodeSize, LastMaxNode);
		
		// calculate the max one
		while(checkedNode.size()>0) {
			TreeBlockChain currentNode = checkedNode.get(0);
			checkedNode.remove(currentNode);
			currentNode.addChildrenQueue(checkedNode, MaxNodeSize, LastMaxNode);
		}
		return LastMaxNode;
	}	

	public boolean isEmpty() {
		return (mainTree.data.size() > 0) ? false : true;
	}
	
	public Block popTheFirstBlock() {
		Block out = mainTree.data.get(0);
		mainTree.data.remove(0);
		return out;
	}
	public Block popTheLastBlock() {
		Block out = mainTree.data.get(mainTree.data.size()-1);
		mainTree.data.remove(mainTree.data.size()-1);
		return out;
	}
	
	public boolean containBlock(Block in) {
		// check the whole chain -> traverse the tree
		return mainTree.containBlock(in);
	}
	
	
	public boolean containTransaction(Transaction t) {
		return mainTree.containTransaction(t);
	}	
	
	public Integer getPreviousBlockID() {
		TreeBlockChain theLastLongestBlockList = getLongestPath(); 	// function return the last node in tree of TreeBlockChain
		Block thePreviousBlock = theLastLongestBlockList.data.get(theLastLongestBlockList.data.size()-1);
		return thePreviousBlock.ID;
	}
	
	public int getSize() {
		return mainTree.getCurrentSize();
	}


	// ---------------------------------------------------------------
	// how to make a fork???
	public void add2Block(Block b1, Block b2) {
		
	}

	public Integer getTheNumberOfFork() {
		theNumberOfFork += mainTree.getTheNumberOfFork(theNumberOfFork);
		return theNumberOfFork;
	}

	public Integer getTimeOfSolvingFork() {
		theSumOfTime += mainTree.getTheTimeOfForkSolving(theSumOfTime);
		return theSumOfTime;
	}
	
	
}
