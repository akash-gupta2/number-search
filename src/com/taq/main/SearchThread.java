package com.taq.main;

import java.math.BigInteger;

/**
 * @author akashgupta
 * A thread class that accepts all input data.
 * search of key is done from start to end numbers
 * once found, master object is alerted by invoking master's reachedGoal()
 *
 */
public class SearchThread extends Thread {
	
	private Master master;
	private BigInteger start;
	private BigInteger end;
	private BigInteger key;
	
	private BigInteger i;  // run thru start to end

	public SearchThread(Master master, ThreadGroup tg, String name, BigInteger start, BigInteger end, BigInteger key) {
		super(tg, name);
		
		this.master = master;
		this.start = start;
		this.end = end;
		this.key = key;
		
		this.i = start;
	}
	

	@Override
	public void run() {
		
		super.run();
		
		for( i = start; i.compareTo(end) < 0; i = i.add(BigInteger.ONE)) {
			if(i.compareTo(key) == 0) {
				master.reachedGoal(this);  // found the key, call the master's success fn
				break;
			}
		}
	}


	public BigInteger getI() {
		return i;
	}

	public void setI(BigInteger i) {
		this.i = i;
	}
}
