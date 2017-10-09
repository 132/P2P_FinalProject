import java.util.Random;

/*
 * At each round of the protocol decides at random to generate a transaction 
 * Random target and random the amount
 */
public class Nodes {
	private Integer IdOfNode;
	private Double TheAmountOfNode;
	
	public void generateTransaction() {
	// random Int target 		// need to know the number of nodes in network
	// random Amount to transaction 
		Random AmountOfTransaction = new Random();
		Random TargetNode = new Random();
		
		
		if(TheAmountOfNode < AmountOfTransaction.nextDouble()) {
			Transaction newTrans = new Transaction();
			// implemete
		}else {
			System.out.println("The amount of Transaction is greater than its own");
		}
	}

}
