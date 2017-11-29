
import java.util.ArrayList;
import java.util.Hashtable;

import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Control;
import peersim.core.Network;
import peersim.core.Node;

public class InitTinyCoin implements Control{
	// Parameters 
	private static final String PAR_PROT = "protocol";
	private static final String PAR_SELFISH = "percentageOfSelfish";
	private static final String PAR_SELFISHPOWER = "selfishPower";
	// Fields
	private static int pid;
	private static double percentageOfSelfish;
	private static int selfishPower;
	
	private Integer NoOfTotalMiner = 0;
	private Integer NoOfSelfishMiner;
	private Hashtable<Integer, String> SetKindMiners = new Hashtable<>();
	
	// should we need a number of miners
	
	// CPU GPU FPGA ASIC
	
	public InitTinyCoin(String prefix) {
		// get the kind of the protocol
		pid = Configuration.getPid(prefix + "." + PAR_PROT);
		// get the percentage of selfish 
		percentageOfSelfish = Configuration.getDouble(prefix + "." + PAR_SELFISH);
		selfishPower = Configuration.getInt(prefix + "." + PAR_SELFISHPOWER);
		
		SetKindMiners.put(0, "CPU");	// power =1 
		SetKindMiners.put(1, "GPU");	// power = 2
		SetKindMiners.put(2, "FPGA");	// power = 3
		SetKindMiners.put(3, "ASIC");	// power = 3
	}
	

	@Override
	public boolean execute() {
		ArrayList<Integer> arrayMiner = new ArrayList<>();
		ArrayList<Integer> arrayCPUMiner = new ArrayList<>();
		ArrayList<Integer> arrayGPUMiner = new ArrayList<>();
		ArrayList<Integer> arrayFPGA_ASICMiner = new ArrayList<>();
		
		// run for every node in the network
		for(int i=0;i<Network.size();i++) {
			Node curNode = Network.get(i);
			protocolTinyCoin protocolOfCurrentNode = (protocolTinyCoin) curNode.getProtocol(pid);
			protocolOfCurrentNode.ID = i;
			protocolOfCurrentNode.Amount = CommonState.r.nextInt(100);
			//protocolOfCurrentNode.isSelfish = false;
			// protocolOfCurrentNode.kindOfMiner = null;
			protocolOfCurrentNode.chosenMinerByOracle = false;
			protocolOfCurrentNode.publicBlockChain = new MainBlockChain();
			protocolOfCurrentNode.tempBlockChain = new ArrayList<>();
			//protocolOfCurrentNode.publicBlockChain.addBlock(new Block(0, 0, null));
			protocolOfCurrentNode.publicBlockChain.mainTree.data.add(new Block(0,-1,-1,new Hashtable<String, Transaction>()));
			
//System.out.println("Init publicBlockChain ID: " + protocolOfCurrentNode.publicBlockChain.mainTree.data.get(0).ID + " Init Miner: " + protocolOfCurrentNode.publicBlockChain.mainTree.data.get(0).MinerID + " Init previous: " + protocolOfCurrentNode.publicBlockChain.mainTree.data.get(0).previousID);			
			
			// random node is the miner and random kinds of miner
			if(CommonState.r.nextBoolean()) {
				// random type of Miner
				// greater than or equal 0 and less than 4
				int randomMiner = CommonState.r.nextInt(4);
				if(i > 0 && !((protocolTinyCoin) Network.get(i-1).getProtocol(pid)).kindOfMiner.equals("FPGA"))
					randomMiner = 2;
				protocolOfCurrentNode.kindOfMiner = SetKindMiners.get(randomMiner);
				
				System.out.println("Node: " + protocolOfCurrentNode.ID + " KindOfMiner: " + protocolOfCurrentNode.kindOfMiner);
				
				// If need we can choose the distribution of kinds Miners
				// count the number of miners
				arrayMiner.add(i);
				
				switch(protocolOfCurrentNode.kindOfMiner) {
					case "CPU": arrayCPUMiner.add(i); break;
					case "GPU": arrayGPUMiner.add(i); break;
					case "FPGA": arrayFPGA_ASICMiner.add(i); break;
					case "ASIC": arrayFPGA_ASICMiner.add(i); break;
					default: break;
				}
				NoOfTotalMiner++;
			}
		}		
		// divide the number of selfish miners and honest miners
		NoOfSelfishMiner = (int) Math.round(NoOfTotalMiner*percentageOfSelfish/100);
		
		System.out.println("The number of Miner: " + NoOfTotalMiner);
		System.out.println("The number of self Miner: " + NoOfSelfishMiner);
		System.out.println("The Power of self Miner: " + selfishPower);
		
		
		System.out.println("# of arrayFPGA: " + arrayFPGA_ASICMiner.size());
		System.out.println("# of arrayGPU: " + arrayGPUMiner.size());
		System.out.println("# of arrayCPU: " + arrayCPUMiner.size());
		
		
		// calculation the number of each kind of miner for selfish miners
		int noOfCPUSelfish = 0;
		int noOfGPUSelfish = 0;
		int noOfFPGA_ASICSelfish = 0;
		Integer currSelfishPower = 0;
		Integer currNoOfSelfishMiner = 0;
		// range of power should be from NoOfSelfishMiner to 3*NoOfSelfishMiner
		if(NoOfSelfishMiner <= selfishPower && selfishPower <= 3*NoOfSelfishMiner) {
System.out.println("Check the size of each kind of miners");			
			do {
				
				/*
				noOfFPGA_ASICSelfish = currSelfishPower/3;
				if(noOfFPGA_ASICSelfish > arrayFPGA_ASICMiner.size()) {
					noOfFPGA_ASICSelfish = arrayFPGA_ASICMiner.size(); 
				}
				currSelfishPower = currSelfishPower - noOfFPGA_ASICSelfish*3;
				currNoOfSelfishMiner = currNoOfSelfishMiner - noOfFPGA_ASICSelfish;
				
				noOfGPUSelfish = currSelfishPower/2;
				if(noOfGPUSelfish > arrayGPUMiner.size()) {
					noOfGPUSelfish = arrayGPUMiner.size();
				}
				currSelfishPower = currSelfishPower - noOfGPUSelfish*2;
				currNoOfSelfishMiner = currNoOfSelfishMiner - noOfGPUSelfish;
				
				noOfCPUSelfish = currSelfishPower;
				if(noOfCPUSelfish > arrayCPUMiner.size())
					noOfCPUSelfish = arrayCPUMiner.size();
				currSelfishPower = currSelfishPower - noOfCPUSelfish*1;
				currNoOfSelfishMiner = currNoOfSelfishMiner - noOfCPUSelfish;
				*/
				

				noOfCPUSelfish = CommonState.r.nextInt(arrayCPUMiner.size()+1);
				noOfGPUSelfish = CommonState.r.nextInt(arrayGPUMiner.size()+1);
				noOfFPGA_ASICSelfish = CommonState.r.nextInt(arrayFPGA_ASICMiner.size()+1);
				
				currNoOfSelfishMiner = noOfCPUSelfish + noOfGPUSelfish + noOfFPGA_ASICSelfish;
				
				currSelfishPower = noOfCPUSelfish + noOfGPUSelfish*2 + noOfFPGA_ASICSelfish*3;
				
			}while(currSelfishPower != selfishPower || currNoOfSelfishMiner != NoOfSelfishMiner);
			
		} else {
			System.out.println("The number of selfish miner is not satisfied the power of the selfish");
			return true;
		}
		//} while(noOfFPGA_ASICSelfish > arrayFPGA_ASICMiner.size() || noOfGPUSelfish > arrayGPUMiner.size() || noOfCPUSelfish > arrayCPUMiner.size());
		//while(noOfFPGA_ASICSelfish <= arrayFPGA_ASICMiner.size() && noOfGPUSelfish <= arrayGPUMiner.size() && noOfCPUSelfish <= arrayCPUMiner.size());
		
		// chosing some miners to become selfish miner
		/*
		for(int i=0;i<NoOfSelfishMiner;i++) {
			// random in miner set to choose the selfish miner
			Node selfCurrNode = Network.get(arrayMiner.get(CommonState.r.nextInt(arrayMiner.size()-1)));
			protocolTinyCoin protocolSelfCurrNode = (protocolTinyCoin) selfCurrNode.getProtocol(pid);
			
			System.out.println("The kind of Miner: " + protocolSelfCurrNode.kindOfMiner);
			System.out.println("No FPGA: " + noOfFPGA_ASICSelfish + " No GPU: " + noOfGPUSelfish + " No CPU: " + noOfCPUSelfish);
			
			// check the kind of miner
			if(noOfFPGA_ASICSelfish > 0 && (protocolSelfCurrNode.kindOfMiner.equals("FPGA") || protocolSelfCurrNode.kindOfMiner.equals("ASIC"))) {
				noOfFPGA_ASICSelfish--;
			} else if(noOfGPUSelfish > 0 && protocolSelfCurrNode.kindOfMiner.equals("GPU")) {
				noOfGPUSelfish--;
			} else if(noOfCPUSelfish > 0 && protocolSelfCurrNode.kindOfMiner.equals("CPU")) {
				noOfCPUSelfish--;
			} else {
				i--;
				continue;
			}
			
			System.out.println("The kind of Miner: " + protocolSelfCurrNode.kindOfMiner);
			// set the miner is iseflish
			protocolSelfCurrNode.isSelfish = true;		
			protocolSelfCurrNode.privateBlockChain = protocolSelfCurrNode.publicBlockChain;
		}
		*/
		
		System.out.println("# selfish of FPGA: " + noOfFPGA_ASICSelfish);
		System.out.println("# selfish of GPU: " + noOfGPUSelfish);
		System.out.println("# selfish of CPU: " + noOfCPUSelfish);
		
//		noOfFPGA_ASICSelfish = 1;
//		noOfGPUSelfish = 0;
//		noOfCPUSelfish = 0;
		
		for(int i=0;i<noOfFPGA_ASICSelfish;i++) {
			int position = CommonState.r.nextInt(arrayFPGA_ASICMiner.size());
			Node selfishNode = Network.get(arrayFPGA_ASICMiner.get(position));
			protocolTinyCoin selfishProtocol = (protocolTinyCoin) selfishNode.getProtocol(pid);
			if(selfishProtocol.kindOfMiner.equals("FPGA") || selfishProtocol.kindOfMiner.equals("ASIC"))
				selfishProtocol.change2Selfish();
		}
		
		for(int i=0;i<noOfGPUSelfish;i++) {
			int position = CommonState.r.nextInt(arrayGPUMiner.size());
			Node selfishNode = Network.get(arrayGPUMiner.get(position));
			protocolTinyCoin selfishProtocol = (protocolTinyCoin) selfishNode.getProtocol(pid);
			if(selfishProtocol.kindOfMiner.equals("GPU"))
				selfishProtocol.change2Selfish();
		}
		
		for(int i=0;i<noOfCPUSelfish;i++) {
			int position = CommonState.r.nextInt(arrayCPUMiner.size());
			Node selfishNode = Network.get(arrayCPUMiner.get(position));
			protocolTinyCoin selfishProtocol = (protocolTinyCoin) selfishNode.getProtocol(pid);
			if(selfishProtocol.kindOfMiner.equals("CPU"))
				selfishProtocol.change2Selfish();
		}
		
		return false;
	}
	

}
