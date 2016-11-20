

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;



public class Bank{
	
	private final int TOTALACCOUNT = 20; //20 accounts
	
	private final static Transaction nullTrans = new Transaction(-1,0,0);
	//list of accounts
	private List<Account> accounts = new ArrayList<>();
	//blocking queue to holds transactions
	private BlockingQueue<Transaction> queue = new ArrayBlockingQueue<Transaction>(200000);
	
	
	public Bank() {
		//initialize 20 account
		for(int i = 0; i< TOTALACCOUNT; i++){
			accounts.add(new Account(i));
			
		}
	}
	public BlockingQueue<Transaction> getQueue(){
		return this.queue;
	}
	/**
	 * for each transfer
	 * 2 main task: withdraw from "from" account and deposit to "to" account with the same amount of money
	 * @param transaction - transaction objects
	 */
	public void Transfer(Transaction transaction){
		withdraw(transaction.getIdFrom(), transaction.getAmount());
		deposit(transaction.getIdTo(), transaction.getAmount());
		
	}
	/**
	 * withdraw process
	 * @param from account
	 * @param Amount
	 */
	public  synchronized void withdraw(int from, double Amount) {
		
			Account acc = searchAccount(from);
			double balance = acc.getBalance() - Amount;
			acc.setBalance(balance);
			acc.setNumOfTransactions(acc.getNumOfTransactions() +1);
			
		
		
	}
	/**
	 * deposit process
	 * @param to account
	 * @param Amount
	 */
	public synchronized  void deposit(int to, double Amount) {
		
			Account acc = searchAccount(to);
			double balance =acc.getBalance() + Amount;
			acc.setBalance(balance);
			acc.setNumOfTransactions(acc.getNumOfTransactions() + 1);
		
		
		
	}
	/**
	 * search account from list of accounts
	 * @param id
	 * @return
	 */
	public Account searchAccount(int id){
		for (int i=0; i<accounts.size(); i++){
			if(accounts.get(i).getId() == id){
				return accounts.get(i);
			}
		}
		
		return null;
	}
	/**
	 * print out the transactions results
	 */
	public void printData(){
		for(int i =0; i< accounts.size(); i++){
			System.out.println(accounts.get(i));
		}
	}
	/**
	 * read transaction files, 
	 * convert each lines into Transaction object and 
	 * put each transactions into blocking queue
	 * @param filename
	 */
	public void readingTransactionFile(String filename){
		String line = "";
		BufferedReader bufferedReader = null;
		InputStream in = getClass().getResourceAsStream(filename); 
		try{
			bufferedReader = new BufferedReader(new InputStreamReader(in));
			
			try {
				while((line = bufferedReader.readLine()) != null){
					line = line.trim();
					int firstBlankToken = line.indexOf(' ');
					int lastBlankToken = line.lastIndexOf(' ');
					int idFrom = Integer.parseInt(line.substring(0, firstBlankToken));
					
					int idTo = Integer.parseInt(line.substring(firstBlankToken+1, lastBlankToken));
					
					double amount = Double.parseDouble(line.substring(lastBlankToken+1,line.length()));
					//put into blocking queue
					queue.put(new Transaction(idFrom,idTo, amount));
					
					
					
				}
				//finally put nullTrans flag 
				queue.put(nullTrans);
			} catch (IOException e) {
				
			}
		}catch(Exception e){
			System.out.println("File not found.");
		}
		
		
		
	}
	/**
	 * worker inner class 
	 * process transactions
	 * using blocking queue 
	 */
	class Worker implements Runnable{
		
		private CountDownLatch workerLatch;
		
		public Worker(CountDownLatch workerLatch) {
			// TODO Auto-generated constructor stub
			this.workerLatch= workerLatch;
		}
		
		@Override
		public void run() {
			
			try{
				//keep pop the first element of the queue, untill nullTrans is hit
				while(!queue.peek().equals(nullTrans)){
						//process each transaction
						Transfer(queue.take());
						
						//count down the latch
						workerLatch.countDown();
				}
			}catch(InterruptedException e){ }
		}
		
	}
	/**
	 * FileWorker inner class 
	 * reading transaction file
	 *
	 */
	class FileWorker implements Runnable{
		private CountDownLatch readingLatch;
		private String filename;
		/**
		 * 
		 * @param filename - transaction file
		 * @param readingLatch 
		 */
		public FileWorker(String filename, CountDownLatch readingLatch) {
			// TODO Auto-generated constructor stub
			this.filename=filename;
			this.readingLatch = readingLatch;
		}
		
		@Override
		public void run() {
			
			//reading transaction file and put transaction to blocking queue
			readingTransactionFile(filename);
			//count down readingLatch
			readingLatch.countDown();
		}
		
	}
	public static void main(String[] args) {
		try{
			if(Integer.parseInt(args[1].toString().trim())<=8 && Integer.parseInt(args[1].toString().trim())>0){
				int numOfThread = Integer.parseInt(args[1].toString().trim());
				//count down latch for reading transaction file
				CountDownLatch readingLatch = new CountDownLatch(1);
				
				//count down latch for process transactions
				CountDownLatch workerLatch = new CountDownLatch(numOfThread);
				
				
				Bank bank = new Bank();
				
				
			    //thread used for reading transaction file, using readingLatch
				Thread readingThread = new Thread(bank.new FileWorker(args[0].trim(), readingLatch));
				readingThread.start();//start 
				readingThread.join();//wait for readingThread to die out
		
				/*
				 * create threads to handle transactions
				 */
				Thread[] threads = new Thread[numOfThread];
				for(int i =0; i<threads.length; i++ )
				{	
					try {
						 //thread used for processing transactions, using workerLatch and blocking queue
						threads[i] = new Thread(bank.new Worker(workerLatch));
						Thread.sleep(50);
						readingLatch.await();//wait until readingLatch hit 0
						
						threads[i].start();//start the thread
						
					
						
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					
				}
				//thread used for print out results of transactions
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						
						try {
							//wait until all transaction have processed
							workerLatch.await();
							bank.printData();
						} catch (InterruptedException e) {
							
							e.printStackTrace();
						}
						
					}
				}).start();				
				
			}else{
				System.out.println("<number of threads>: 1 - 8 only");
			}
		}catch(Exception e){
			System.out.println("Wrong commandline");
			System.out.println("java -jar Bank.jar <filename> <number of threads>");
		}
	
	
	}
}


