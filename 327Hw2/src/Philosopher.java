import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.*;

/**
 * Philosopher class used as an thread to the dining philosopher problem
 * @author Kelvin
 *
 */
public class Philosopher extends Thread {
	//Different states
	private static int WAITING = 0, EATING = 1, THINKING = 2;
	private Lock lock;
	private Condition phil[];
	//Array of states for the philosophers
	private int states[];
	private int NUM_PHILS;
	private int id;
	private final int TURNS = 20;
	//Number of times a philosopher has eaten
	private int eatCount[];
	//How long they had to wait
	private long waitTime;
	//Number of times they waited
	private int stallCount;
	//How long to finish
	private long executionTime;
	//Checks if philosopher is in queue
	boolean firstInQueue;
	//Queue for the philosophers
	private static ConcurrentLinkedQueue<Philosopher> queue;

	/**
	 * constructor used to initialize philosopher
	 * @param l lock
	 * @param p philosopher
	 * @param st state
	 * @param num number of philosophers
	 */
	public Philosopher(Lock l, Condition p[], int st[], int num, int[] ec) {
		lock = l;
		phil = p;
		states = st;
		NUM_PHILS = num;
		eatCount = ec;
		waitTime = 0;
		stallCount = 0;
		executionTime = 0;
		queue = new ConcurrentLinkedQueue<Philosopher>();
		firstInQueue = true;
	}

	/**
	 * causes the thread to run
	 */
	public void run() {
		//keep track of the start of the running time
		long start = System.nanoTime();
		id = ThreadID.get();
		for (int k = 0; k < TURNS; k++) {
			try {
				sleep(100);
			} catch (Exception ex) { /* lazy */
			}
			takeSticks(id);
			try {
				sleep(20);
			} catch (Exception ex) {
			}
			putSticks(id);
		}
		//get execution time of the thread
		executionTime = System.nanoTime() - start;
	}

	/**
	 * determines if a philosopher takes the sticks to eat
	 * @param id id of the philosopher thread
	 */
	public void takeSticks(int id) {
		long currentTime = System.nanoTime();
		lock.lock();
		//keep track of current wait time
		waitTime += System.nanoTime() - currentTime;
		try {
			firstInQueue = true;
			if (!queue.isEmpty()) {
				firstInQueue = false;
			}
			//if neither side is eating and the philosopher is first in queue they can eat
			if (states[leftof(id)] != EATING && states[rightof(id)] != EATING
					&& firstInQueue) {
				states[id] = EATING;
				eatCount[id]++;
			} 
			//if philosopher can't eat they get added to the queue if they are not on it 
			else {
					states[id] = WAITING;
					queue.add(this);
					phil[id].await();
					stallCount++;
				}
			
			//track wait time of philosopher thread
			waitTime += System.nanoTime() - currentTime;
		} catch (InterruptedException e) {
			System.exit(-1);
		} finally {
			lock.unlock();
		}
		
	}

	/**
	 * output the information about this thread
	 */
	public void output() {
		lock.lock();
			System.out.print("Thread: " + id);
			System.out.print("\t Eatcount: " + eatCount[id] );
			System.out.print("\t ExecTime: " +getExecTime());
			System.out.print("\t Stall Counts: " +getStallCount());
			System.out.print("\t waitTime: " +getWaitTime());


			System.out.println("");
		lock.unlock();
		System.out.println();
		System.out.println();
	}

	/**
	 * have the philosopher put down his stick and signal the next person in queue to eat
	 * @param id philosopher id
	 */
	public void putSticks(int id) {
		//current time
		long currentTime = System.nanoTime();
		lock.lock();
		//track the wait time
		waitTime += System.nanoTime() - currentTime;
		try {
			//set the philosopher that finished eating to thinking
			states[id] = THINKING;
			//check the queue list
			if(!queue.isEmpty()){
				//first philosopher in queue
				Philosopher p = queue.peek();
				//check that no one beside him is eating
				if(states[(leftof(p.getID()))] != EATING && states[(rightof(p.getID()))] != EATING){
					//signal the philosopher to eat
					phil[p.getID()].signal();// Signal an awaiting phil
					//change status to eat
					states[p.getID()] = EATING;
					//increment eat counter
					eatCount[p.getID()]++;
					//remove the eating philosopher from queue
					queue.remove();
				}
			}
			
		} finally {
			lock.unlock();
		}
	}

	private int leftof(int id) { // clockwise
		int retval = id - 1;
		if (retval < 0) // not valid id
			retval = NUM_PHILS - 1;
		return retval;
	}

	private int rightof(int id) {
		int retval = id + 1;
		if (retval == NUM_PHILS) // not valid id
			retval = 0;
		return retval;
	}

	/**
	 * accessor to execution time of philosopher
	 * @return execution time
	 */
	public long getExecTime() {
		return executionTime;
	}
	public long getStallCount(){
		return stallCount;
	}
	public long getWaitTime(){
		return waitTime;
	}

	/**
	 * returns the id of philosopher
	 * @return id
	 */
	public int getID() {
		return id;
	}
}