import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.text.AsyncBoxView.ChildLocator;

import java.util.Set;

public class TreeBlockChain {
	
	ArrayList<Block> data;
	
	Integer previousSize;		// check the children max size
	TreeBlockChain previousBlock;	// it can be just only the ID of the block
	
	ArrayList<TreeBlockChain> children = new ArrayList<>();
	
	//public ArrayList<BranchBlockChain> TreeBC;
	public TreeBlockChain(TreeBlockChain in) {
		this.data = in.data;
		this.children = in.children;
	}
	
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
	
	public void getAllNode() {
		for(int i=0;i<data.size();i++) {
			System.out.println("The BlockID: " + data.get(i).ID + " MinerID: " + data.get(i).MinerID + " PreviousID: " + data.get(i).previousID);
		}
System.out.println("Children: " + children.size());
		if(!children.isEmpty()) {
			for(int i =0; i<children.size();i++) {
				children.get(i).getAllNode();
			}
		}
	}
	
	public void addBlock(Block in) {
		// set the Previous Block to new Block before adding
		//in.setPreviousB(data.get(data.size()-1));
		
		// add the new node at the end of the NODE
		if(data.get(data.size()-1).ID == in.previousID && children.size() == 0) {
			data.add(in);
		// add the node to its children
		} else if(data.get(data.size()-1).ID == in.previousID && children.size() != 0) {
			ArrayList<Block> newListBlock = new ArrayList<>();
			newListBlock.add(in);
			TreeBlockChain newChild = new TreeBlockChain(newListBlock);
			children.add(newChild);
		// add the node to the middle of NODE => the node follow have to be added to one of the children
		} else {
			// split without children
			int positionSplit = 0;
			
			if(children.size() == 0 && data.get(data.size()-1).ID != in.previousID ) {
				for(int i=0; i<data.size();i++) {
					if(data.get(i).ID == in.previousID && data.get(i+1).previousID == data.get(i).ID) {
						splitAndAddChildren(in, data.get(i+1));
						positionSplit = i+1;
					}
				}
				// just only split and add block again
				for(int i=0; i < children.size();i++) {
					for(int j=positionSplit; j<data.size(); j++) {
						if(children.get(i).data.get(children.get(i).data.size()-1).ID == data.get(j).previousID) {
							children.get(i).data.add(data.get(j));
							data.remove(data.get(j));
						}
					}
				}
			
			// in case of adding a new Block to middle => create new Tree
			} else if (children.size() != 0 && data.get(data.size()-1).ID != in.previousID) {
				
System.out.println("Number Of Children: " + children.size());				
				ArrayList<TreeBlockChain> tempChildren = children;
				for(TreeBlockChain child : tempChildren)
					children.remove(child);
				
				for(int i=0; i<data.size();i++) {
					if(data.get(i).ID == in.previousID && data.get(i+1).previousID == data.get(i).ID) {
						splitAndAddChildren(in, data.get(i+1));
						positionSplit = i+1;
					}
				}
				
				for(int i=0; i < children.size();i++) {

					for(int j=0; j<data.size(); j++) {
						if(children.get(i).data.get(children.get(i).data.size()-1).ID == data.get(j).previousID) {
							children.get(i).data.add(data.get(j));
							data.remove(data.get(j));
							positionSplit = i;
						}
					}
					
				}
System.out.println("The position of Children: " + positionSplit);				
				children.get(positionSplit).children = tempChildren;
				// split with childreen
			}
		}

		
		/*else {	
			for(int i=0; i < data.size(); i++) {
				if(previousBlockOfNewBlock == data.get(i).previousID) {
					splitAndAddChildren(in, data.get(i));
					data.remove(data.get(i));
				}
			}				
		} 
		
		else if(children.size() > 0) {
			if(in.previousID == data.get(data.size()-1).ID) {
				ArrayList<Block> listBlockChild = new ArrayList<>();
				listBlockChild.add(in);
				TreeBlockChain newChild = new TreeBlockChain(listBlockChild);
				children.add(newChild);
			} else {
				for(int i=0;i<data.size();i++) {
					if(data.get(i).ID == in.previousID) {
						splitAndAddChildren(in, data.get(i));
					}
				}
			}
		} else {
System.out.println("Add new Block data Size == 1");
			data.add(in);
		}
		*/
	}
	
	// this function split the newBlock and another Block in BlockChain and add both of them to the Children
	public void splitAndAddChildren(Block in, Block curr) {
		for(int i=0; i< data.size();i++) {
			if(data.get(i).ID == in.previousID && data.get(i).ID == curr.previousID) {
				ArrayList<Block> inList = new ArrayList<>();
				inList.add(in);
				TreeBlockChain treeIn = new TreeBlockChain(inList);
				children.add(treeIn);
				
				ArrayList<Block> currList = new ArrayList<>();
				currList.add(curr);
				TreeBlockChain treeCurr = new TreeBlockChain(currList);
				children.add(treeCurr);
				data.remove(curr);
			}
		}
	}
	

		/*
		Integer previousBlock = in.previousID;
		for(int i =0;i < data.size();i++) {
			if(data.get(i).ID == previousBlock) {
				
				ArrayList<Block> dataOfChildTree = new ArrayList<>();
				dataOfChildTree.add(in);
				TreeBlockChain newChild = new TreeBlockChain(dataOfChildTree);
				children.add(newChild);
				
				dataOfChildTree = new ArrayList<>();
				dataOfChildTree.add(curr);
				newChild = new TreeBlockChain(dataOfChildTree);
				children.add(newChild);
				// in case of the Block has previous Block 
			}
		}
		// add back other block into the children
		for(int i=0;i<data.size();i++) {
			// find the previous Block to add
			for(int j=0;j<children.size();j++) {
				if(children.get(j).data.get(children.get(j).data.size()-1).ID == data.get(i).previousID) {
					TreeBlockChain temp = children.get(j);
					temp.data.add(data.get(i));
					children.set(j, temp);
				}
					
			}
			/*
			TreeBlockChain findTree2Add = null;
			findTree2Add =getTreeBlockChain(data.get(i).previousID);
				if(findTree2Add != null) {
System.out.println("Found the tree 2 add with ID: " + findTree2Add.data.get(findTree2Add.data.size()-1).ID);
				
				// add the newBlock to the Tree
				for(int j=0;j < findTree2Add.data.size();j++) {
					if(findTree2Add.data.get(j).ID == data.get(i).previousID) {
						findTree2Add.addBlock(data.get(i));
					}
				}
				
			}
			*/

	
	
	
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

	public void check_add2Tree(TreeBlockChain addThisTree, Block newBlock) {
		if(this.equals(addThisTree)) {
			this.addBlock(newBlock);
			return;
		} else {
			for(int i=0;i<children.size();i++) {
				children.get(i).check_add2Tree(addThisTree, newBlock);
			}
		}
	}

	public boolean containBlock(Block in) {
		if(data.contains(in)) {
System.out.println("Exist Block True => false" + data.get(0).ID);			
			return true;
		} else { 
			if(children.size() > 0) {
				for(int i=0;i<children.size();i++) {
					if(children.get(i).containBlock(in))
						return true;
				}
			}
		}
		return false;
	}
	
	public boolean containTransaction(Transaction in) {
		if(data.size() > 0) {
			for(Block blockInTree : data)
				if(blockInTree.containTransaction(in))
					return true;
		} else { 
			if(existChildren()) {
				for(TreeBlockChain chil : children)
					if(chil.containTransaction(in))
						return true;
			}
		}
		return false;
	}

	public Integer getTheNumberOfFork(Integer in) {
		if(children.size() == 0) {
			return in;
		} else {
			in++;
			for(TreeBlockChain child : children) {
				in += child.getTheNumberOfFork(in);
			}
		}
		return in;
	}

	public Integer getTheTimeOfForkSolving(Integer theSumOfTime) {
		if(children.size() == 0) {
			return theSumOfTime;
		} else {
			Integer minBlockList = data.size();
			for(TreeBlockChain child : children) {
				if(child.data.size() < minBlockList)
					minBlockList = child.data.size();
			}
			theSumOfTime += minBlockList;
			for(TreeBlockChain child : children) {
				theSumOfTime += child.getTheTimeOfForkSolving(theSumOfTime);
			}
		}
		return theSumOfTime;
	}
	
	public TreeBlockChain getTreeBlockChain(int preID) {
		TreeBlockChain out = null;
		for(int i=0;i<data.size();i++)
			if(data.get(i).ID == preID)
				return this;
		for(int j=0;j<children.size();j++) {
			out = children.get(j).getTreeBlockChain(preID);
		}
		return out;
	}
	
	public boolean containBlockID(int pID) {
		for(int i=0;i<data.size();i++) {
			if(data.get(i).ID == pID) {
				return true;
			}
		}
		return false;
	}
	
	// return the Tree which contain the Block
	public TreeBlockChain getTheTreeContainPreBlock(Block newBlock) {
		if(containBlockID(newBlock.previousID)){
			return this;
		} else {
			for(int i=0;i<children.size();i++) {
				TreeBlockChain temp = children.get(i).getTheTreeContainPreBlock(newBlock);
				if(temp!=null)
					return temp;
			}
		}
		return null;
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
