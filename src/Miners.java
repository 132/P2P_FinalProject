// choosing kind of miners => the probability of Power

public class Miners extends Nodes {
	public String KindOfMiners;
	public double PowerOfMiners;
	public Miners() {
		switch (KindOfMiners) {
		case "CPU": PowerOfMiners=0.2;
			break;
		case "GPU": PowerOfMiners=0.4;
		break;
		case "FPGA": PowerOfMiners=0.6;
		break;
		case "ASIC": PowerOfMiners=0.8;
		break;
		}
		
	}
}
