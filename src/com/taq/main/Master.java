package com.taq.main;

import java.math.BigInteger;
import java.time.Duration;
import java.time.Instant;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author akashgupta
 *
 */
public class Master {

	private final static int SUGGESTED_NUM_OF_THREADS = 1000;
	private final static BigInteger MAX_ALLOWED_VALUE = new BigInteger("999999999999999");

	private ThreadGroup threadGroup;
	private CopyOnWriteArrayList<SearchThread> searchThreadList = new CopyOnWriteArrayList<>();

	private BigInteger key;

	private Instant startInstance;
	private Instant finishInstance;

	/**
	 * starting program that invokes all the thread
	 */
	public void perform() {
		key = acceptKey();
		threadGroup = new ThreadGroup("search");

		// numbers per thread
		BigInteger slice = MAX_ALLOWED_VALUE.divide(BigInteger.valueOf(SUGGESTED_NUM_OF_THREADS));

		// the number of threads may have to be adjusted for the last portion of
		// reminding numbers
		int numOfThreads = SUGGESTED_NUM_OF_THREADS
				+ MAX_ALLOWED_VALUE.remainder(BigInteger.valueOf(SUGGESTED_NUM_OF_THREADS)).compareTo(BigInteger.ZERO);

		System.out.println("Starting the threads.  Num of threads=" + numOfThreads + ". numbers per thread="
				+ slice.toString() + "\n");

		startInstance = Instant.now();

		BigInteger start = BigInteger.ZERO;
		for (int i = 0; i < numOfThreads; i++) {
			SearchThread searchThread = new SearchThread(this, threadGroup, i + "", start, start.add(slice), key);
			searchThreadList.add(searchThread);
			searchThread.start();

			start = start.add(slice);
		}

	}

	/**
	 * get a valid number
	 * 
	 * @return
	 */
	private BigInteger acceptKey() {
		System.out.println("Please enter value from 0 to " + MAX_ALLOWED_VALUE);
		Scanner sc = new Scanner(System.in);
		while (true) {
			String str = sc.nextLine();
			try {
				BigInteger val = new BigInteger(str);
				if (val.compareTo(BigInteger.ZERO) < 0 || val.compareTo(MAX_ALLOWED_VALUE) > 0) {
					throw new Exception("Entered value out of bound");
				}

				sc.close();
				return val;
			} catch (Exception ex) {
				System.out.println(ex.getMessage());
				System.out.println("Invalid entry. Please enter valid number between 0 to " + MAX_ALLOWED_VALUE);
			}
		}

	}

	/**
	 * one of the thread found the key and called this method
	 */
	public void reachedGoal(SearchThread winnerThread) {

		// stop all the threads
		threadGroup.interrupt();
		finishInstance = Instant.now();

		System.out.println("Hurray!! Thread " + winnerThread.getName() + " found the key in " + findTime());

		try {
			searchThreadList.stream().forEach(searchThread -> System.out
					.println("Thread " + decorateName(searchThread.getName()) + " looped till " + searchThread.getI()));
		} catch (Exception ex) {
			System.out.println("Got an error. " + ex.getMessage());
		}

	}

	/**
	 * calculate time taken for the search
	 * 
	 * @return
	 */
	private String findTime() {

		int[] measures = { 1000, 60, 60, 24, Integer.MAX_VALUE };
		String[] units = { "ms", "sec", "min", "hours", "days" };

		long timeElapsed = Duration.between(startInstance, finishInstance).toMillis();

		for (int i = 0; i < measures.length; i++) {
			if (timeElapsed < measures[i]) {
				return timeElapsed + " " + units[i];
			}

			timeElapsed /= measures[i];
		}

		return timeElapsed + " " + units[units.length - 1];
	}

	private String decorateName(String name) {
		StringBuffer sb = new StringBuffer("");
		for (int i = 0; i < 5 - name.length(); i++) {
			sb.append(" ");
		}
		return sb.append(name).toString();
	}

	public static void main(String[] args) {
		System.out.println("Master initialize");
		Master master = new Master();
		master.perform();
	}

}
