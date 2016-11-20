
/**
 * model of account
 * @author Nghi Nguyen
 *
 */
public class Account {
	//fields
	private int id;//id of account
	private double balance;//balance of account
	private int numOfTransactions; // number of transactions have been made
	/**
	 * constructor
	 * @param id
	 */
	public Account(int id) {
		this.id = id;
		this.balance = 1000;//each account initialized with $1000
		this.numOfTransactions= 0;
	}
	/**
	 * getter
	 * @return id
	 */
	public int getId() {
		return this.id;
	}
	/**
	 * getter
	 * @return balance
	 */
	public double getBalance() {
		return this.balance;
	}
	/**
	 * setter
	 * @param amount - the balance
	 */
	public void setBalance(double amount){
		this.balance = amount;
	}
	/**
	 * setter
	 * @param numOfTransactions
	 */
	public void setNumOfTransactions(int numOfTransactions) {
		this.numOfTransactions=numOfTransactions;
	}
	/**
	 * getter
	 * @return numOfTransactions
	 */
	public int getNumOfTransactions() {
		return this.numOfTransactions;
	}
	/**
	 * display information of the account
	 */
	public String toString() {
		String str = "";
		str += "acct:"+getId()+" bal:"+getBalance()+" trans:" + getNumOfTransactions();
		return str;
	}
}
