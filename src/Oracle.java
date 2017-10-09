import java.util.Random;

public class Oracle {
	private Miners miner;
	//private double
	public void chooseNextMiner() {
		Integer TheNumberOfNodes = 10;
		String Power;
		Random rn = new Random();
		Integer RandomKindOfMiner = rn.nextInt(10 - 1 + 1) + 1;
		if(RandomKindOfMiner >= 7) Power = "ASIC";
		else if(RandomKindOfMiner >= 4) Power = "FPGA";
		else if(RandomKindOfMiner >= 2) Power = "GPU";
		else if(RandomKindOfMiner == 1) Power = "CPU";
	}
}
