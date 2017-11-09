import peersim.config.Configuration;
import peersim.core.Control;

public class observerTinyCoin implements Control{
	

	/*
	 * Parameter
	 */
	private static final String PAR_PROT = "protocol";
	
	/*
	 * Field
	 */
	private static int pid;
	
	// Constructor
	public observerTinyCoin(String prefix) {
		pid = Configuration.getPid(prefix + "." + PAR_PROT);
	}
	
	
	
	// compue_distribution
	
	
	@Override
	public boolean execute() {
		
		return false;
	}

}
