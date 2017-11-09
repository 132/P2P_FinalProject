//package example.edaggregation;

import peersim.vector.SingleValueHolder;
import peersim.config.*;
import peersim.core.*;
import peersim.transport.Transport;

import java.util.ArrayList;
import java.util.LinkedList;

import peersim.cdsim.CDProtocol;
import peersim.edsim.EDProtocol;

public class BranchBlockChain {
	
	public ArrayList<Block> Chain = new ArrayList<Block>();
	
/*	public BranchBlockChain() {
		System.out.print("Create a new Block Chain.");
		Chain.add(new Block(0));
	}
*/
	public void addBlock(Block newB) {
		
		//Chain.add(Chain.indexOf(currB),newB);
		newB.setPreviousB(Chain.get(Chain.size()-1));
		Chain.add(newB);
		
	}
	public Integer getLongestPath() {
		
		return 1;
	}

	public Integer countBlock() {
		return Chain.size();
	}

}
