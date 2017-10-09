import peersim.vector.SingleValueHolder;
import peersim.config.*;
import peersim.core.*;
import peersim.transport.Transport;
import peersim.cdsim.CDProtocol;
import peersim.edsim.EDProtocol;

public class TinyCoin extends SingleValueHolder
implements CDProtocol, EDProtocol{
	/*
	 *  Parameters
	 */
	protected static final String PAR_QUOTA = "quota";
	
	/*
	 * Fields
	 */
	private final double quota_value;
	protected double quota; // current cycle quota
	

	public TinyCoin(String prefix) {
		super(prefix);
		// TODO Auto-generated constructor stub
		
		/*
		 * Take parameter from config file
		 */
		quota_value = (Configuration.getInt(prefix + "." + PAR_QUOTA,1));
		quota = quota_value;
	}

	
	
	@Override
	public void processEvent(Node node, int pid, Object event) {
		// TODO Auto-generated method stub
		//AverageMessage aem = (AverageMessage)event;
		//((Transport)node.getProtocol(FastConfig.getTransport(pid))).send(node, aem.sender, arg2, arg3);
	}

	@Override
	public void nextCycle(Node node, int pid) {
		// TODO Auto-generated method stub
		Linkable linkable = (Linkable) node.getProtocol(FastConfig.getLinkable(pid));
		
		if(linkable.degree()>0) {
			Node peern = linkable.getNeighbor(CommonState.r.nextInt(linkable.degree()));
			
		}
			
	}
	
}
