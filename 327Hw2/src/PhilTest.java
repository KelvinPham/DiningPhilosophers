import java.util.concurrent.locks.*;

/**
 * runs the dining philosophers problem
 * @author Kelvin
 *
 */
public class PhilTest {
	//three different states of philosophers
	private static int WAITING = 0, EATING = 1, THINKING = 2;
	//number of philosophers
	private static final int NUM_PHILS = 100;
	//lock used for synchronization purposes
	private static Lock lock = new ReentrantLock();
	//philosophers in the arraylist
	private static Condition phil[] = new Condition[NUM_PHILS];
	//array of the states of each philosopher
	private static int states[] = new int[NUM_PHILS];
	//an array of eat counts for that holds one value for each philosopher
	private static int[] eatCount		= new int[NUM_PHILS];
	/**
	 * initializes the philosophers
	 */
	public static void init() {
		for (int k = 0; k < NUM_PHILS; k++) {
			phil[k] = lock.newCondition();
			states[k] = THINKING;
		}
	}


	public static void main(String a[]) {
		init();
		//array of philosophers
		Philosopher p[] = new Philosopher[NUM_PHILS];
		for (int k = 0; k < p.length; k++) {
			p[k] = new Philosopher(lock, phil, states, NUM_PHILS,eatCount);
			p[k].start();
		}
		
		for(int i = 0; i < p.length; i++){
			try {
				p[i].join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		for(int j = 0; j < p.length; j++){
			p[j].output();
		}
		
	}
}