import peersim.config.Configuration;
import peersim.core.Node;
import peersim.edsim.EDSimulator;
import peersim.transport.Transport;

public class weightedTransportTinyCoin implements Transport{
	/*
	 * Parameter
	 */
	private static final String PAR_MINDELAY = "mindelay";		// parameters of latencies between nodes in network
	private static final String PAR_MAXDELAY = "maxdelay";
	private static final String PAR_DELAYNODES = "deplayNodes";
	
	/*
	 * Field
	 */
	private static long minDelay;
	private static long maxDelay;
	private static long delayNodes;
	
	public weightedTransportTinyCoin(String prefix) {
		minDelay = Configuration.getLong(prefix + "." + PAR_MINDELAY);
		maxDelay = Configuration.getLong(prefix + "." + PAR_MAXDELAY);
		delayNodes = Configuration.getLong(prefix + "." + PAR_DELAYNODES);
	}
	
	@Override
	//Return a latency estimate from node src to protocol pid of node dst. 
	public long getLatency(Node src, Node dest) {
		return delayNodes;
	}

	@Override
	//Sends message msg from node src to protocol pid of node dst.
	public void send(Node src, Node dest, Object msg, int pid) {
		long delay = minDelay;
		if(msg instanceof Block) {
			delay = (long) ((delay + ((Block) msg).getLatency() > maxDelay)? maxDelay : delay + ((Block) msg).getLatency());
			//delay += ((Block) msg).getLatency();
			
		}
		EDSimulator.add( delay, msg, dest, pid);
	}
	
	public Object clone() {
		return this;
	}

}
