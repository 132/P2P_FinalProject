import peersim.config.Configuration;
import peersim.core.Node;
import peersim.edsim.EDSimulator;
import peersim.transport.Transport;

public class weightedTransport implements Transport{
	/*
	 * Parameter
	 */
	private static final String PAR_MINDELAY = "protocol";
	private static final String PAR_MAXDELAY = "protocol";
	
	/*
	 * Field
	 */
	private static long minDelay;
	private static long maxDelay;
	
	public weightedTransport(String prefix) {
		minDelay = Configuration.getLong(prefix + "." + PAR_MINDELAY);
		maxDelay = Configuration.getLong(prefix + "." + PAR_MAXDELAY);
	}
	
	@Override
	//Return a latency estimate from node src to protocol pid of node dst. 
	public long getLatency(Node src, Node dest) {
		return minDelay;
	}

	@Override
	//Sends message msg from node src to protocol pid of node dst.
	public void send(Node src, Node dest, Object msg, int pid) {
		long delay = minDelay;
		if(msg instanceof Block) {
			
			//delay += ((Block) msg).getLatency();
			
		}
		EDSimulator.add( delay, msg, dest, pid);
		
	}

}
