/**
 * immutable class 
 * 
 * @author Nghi Nguyen
 *
 */

public final class Transaction {
	
	//fields
	
	private final int idFrom; //"from" account id
	private final int idTo;//"to" account id
	private final double amount;//amount being transfered
	
	
	/**
	 * constructor
	 * @param idFrom
	 * @param idTo
	 * @param amount
	 */
	public Transaction(int idFrom, int idTo, double amount) {
		this.idFrom=idFrom;
		this.idTo = idTo;
		this.amount = amount;
	}
	

	/**
	 * getter
	 * @return idTo
	 */
	public int getIdTo() {
		return this.idTo;
	}

	/**
	 * getter
	 * @return idFrom
	 */
	public int getIdFrom() {
		return this.idFrom;
	}
	/**
	 * getter
	 * @return amount
	 */
	public double getAmount() {
		return this.amount;
	}
	
	
}
