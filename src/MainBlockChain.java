import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;

public class MainBlockChain {
	
	TreeBlockChain mainTree;
//	ArrayList<Block> headBlock;


	public MainBlockChain() {
		ArrayList<Block> in = new ArrayList<Block>();
		in.add(new Block(0, 0, null));
		this.mainTree = new TreeBlockChain(in);
		this.mainTree.setPreviousSize(0);

	}
	
	public void addBlock(Block newBlock) {
		// find the longest path
		TreeBlockChain addThisTree = getLongestPath();	
		mainTree.checkTheLongestPath(addThisTree, newBlock);		
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

	
/*	public void removeChildrenQueu(ArrayList<TreeBlockChain> in) {
		// remove node which is less than the maximum one and not existing children
		for(TreeBlockChain subQ : in) {
			if(!subQ.existChildren() && subQ.getCurrentSize() < MaxNodeSize) {
				in.remove(subQ);
			}
			
		}
	}
	*/
	

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
	
	
}
