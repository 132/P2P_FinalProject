import peersim.core.Node;

// only owners of fund can create transactions.
public class Transaction {
	Node InputID;	// ID of user
	Node OutputID;	// ID of target account
	int AmountSpending;	// Amount of spending
	String IDtrans;
	
	public Transaction(Node iID, Node oID, int a, String IDtr) {
		this.InputID = iID;
		this.OutputID = oID;
		this.AmountSpending = a;
		this.IDtrans = IDtr;
	}
	
	public String getID() {
		return IDtrans;
	}
}
