import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class TreeBlockChain {
	
	ArrayList<Block> data;
	
	Integer previousSize;		// check the children max size
	TreeBlockChain previousBlock;	// it can be just only the ID of the block
	
	ArrayList<TreeBlockChain> children;
	
	//public ArrayList<BranchBlockChain> TreeBC;
	
	public TreeBlockChain(ArrayList<Block> in) {
		this.data = in;
		this.children = new ArrayList<TreeBlockChain>();
	}
	
	public Integer getCurrentSize() {	
		return this.previousSize + this.data.size();
	}
	
	public void setPreviousSize(Integer in) {
		this.previousSize = in;
	}
	
	public boolean existChildren() {
		return (children.size()>0) ? true : false;
	}
	
	public void addBlock(Block in) {
		// set the Previous Block to new Block before adding
		in.setPreviousB(data.get(data.size()-1));
		data.add(in);
	}
	
	public void addChildrenQueue(ArrayList<TreeBlockChain> in, Integer MaxNodeSize, TreeBlockChain LastMaxNode) {
		for(TreeBlockChain chil : children) {
			// set the previous size to children before adding
			chil.setPreviousSize(getCurrentSize());
			in.add(chil);
			// check the size of children to assign again the max
			if(chil.getCurrentSize() > MaxNodeSize) {
				MaxNodeSize = chil.getCurrentSize();
				LastMaxNode = chil;
			}
		}
	}
	
	public boolean equals(TreeBlockChain in) {
		return (this.data.equals(in.data) && this.children.equals(in.children)) ? true : false;
		
	}

	public void checkTheLongestPath(TreeBlockChain addThisTree, Block newBlock) {
		if(this.equals(addThisTree)) {
			this.addBlock(newBlock);
			return;
		} else {
			for(int i=0;i<children.size();i++) {
				children.get(i).checkTheLongestPath(addThisTree, newBlock);
			}
		}
	}
	
	
	public void traverse(TreeBlockChain in) {
		for(TreeBlockChain chil : children) {
			
		}
	}

	public boolean containBlock(Block in) {
		if(data.contains(in)) {
			return true;
		} else { 
			if(existChildren()) {
				for(TreeBlockChain chil : children)
					return chil.containBlock(in);
			}
			else
				return false;
		}
		return false;
	}
	
	public boolean containTransaction(Transaction in) {
		if(data.size() > 0) {
			for(Block blockInTree : data)
				return blockInTree.containTransaction(in);
		} else { 
			if(existChildren()) {
				for(TreeBlockChain chil : children)
					return chil.containTransaction(in);
			} else
				return false;
		}
		return false;
	}
	
	
	
/*	public void removeChildrenQueue(ArrayList<TreeBlockChain> in, Integer MaxNodeSize) {
		// remove node which is less than the maximum one and not existing children
		for(TreeBlockChain subQ : in) {
			if(!subQ.existChildren() && subQ.getCurrentSize() < MaxNodeSize) {
				in.remove(subQ);
			}
			
		}
	}
*/
	
/*
	public TreeBlockChain() {
		TreeBC = new ArrayList<>();
		
		//TreeBC.add(e);
	}
	public void addBlock(Block newB) {
		longestPath().addBlock(newB);
	}
	
	public BranchBlockChain longestPath() {
		Integer maxBranch = TreeBC.get(0).Chain.size();
		BranchBlockChain out = TreeBC.get(0);
		for(int i=0; i<TreeBC.size();i++) {
			if(maxBranch<TreeBC.get(i).Chain.size()) {
				maxBranch = TreeBC.get(i).Chain.size();
				out = TreeBC.get(i);
			}
		}
		return out;
	}
*/
}
