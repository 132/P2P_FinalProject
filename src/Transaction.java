import peersim.core.Node;

// only owners of fund can create transactions.
public class Transaction {
	Node InputID;	// ID of user
	Node OutputID;	// ID of target account
	Double AmountSpending;	// Amount of spending
	
	String IDtrans;
	
	boolean sent = false;
	
	public Transaction(Node iID, Node oID, Double a, String IDtr) {
		this.InputID = iID;
		this.OutputID = oID;
		this.AmountSpending = a;
		this.IDtrans = IDtr;
	}
	
	public String getID() {
		return IDtrans;
	}
}
