package fr.ibaraki.libs;

import java.util.concurrent.TimeUnit;

public abstract class Run implements Runnable {

	private Thread thread;
	private Run run;
	private long taskId = -1;
	
	/**
	 * Create a Runnable in a new thread
	 * @author Yugo
	 */
	public Run() {
		this.run = this;
	}
	
	/**
	 * 
	 * Run task without ending
	 * 
	 * @param delay : interval between two execution
	 * @param delayUnit : unit of delay
	 * @param before : time before the first execution
	 * @param beforeUnit : unit of before
	 * @param daemon : can be killed if executing
	 */
	public void runTaskDelay(long delay, TimeUnit delayUnit, long before, TimeUnit beforeUnit, boolean daemon) {
		this.thread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					Thread.sleep(TimeUnit.MILLISECONDS.convert(before, beforeUnit));
					
					while (true) {
						run.run();
						Thread.sleep(TimeUnit.MILLISECONDS.convert(delay, delayUnit));
					}
				} catch (InterruptedException e) {}
				
			}
		});
		taskId = thread.getId();
		thread.setDaemon(daemon);
		thread.start();
	}
	
	/**
	 * 
	 * Run task after a delay
	 * 
	 * @param before : time before the first execution
	 * @param beforeUnit : unit of before
	 * @param daemon : can be killed if executing
	 */
	public void runTaskLater(long before, TimeUnit unit, boolean daemon) {
		this.thread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					Thread.sleep(TimeUnit.MILLISECONDS.convert(before, unit));
					run.run();
					thread.interrupt();
				} catch (InterruptedException e) {}
				
			}
		});
		taskId = thread.getId();
		thread.setDaemon(daemon);
		thread.start();
	}
	
	
	/**
	 * 
	 * Run the task
	 * 
	 * @param daemon : can be killed if executing
	 */
	public void runTask(boolean daemon) {
		this.thread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				run.run();
				thread.interrupt();
			}
		});
		taskId = thread.getId();
		thread.setPriority(Thread.MAX_PRIORITY);
		thread.setDaemon(daemon);
		thread.start();
	}
	
	
	/**
	 * 
	 * @return the ID of the task
	 * @throws IllegalStateException
	 */
	public synchronized long getTaskId() throws IllegalStateException {
	    long id = this.taskId;
	    if (id == -1)
	      throw new IllegalStateException("Not scheduled yet"); 
	    return id;
	  }
	
	/**
	 * Cancel the task by the ID
	 * 
	 * @param id : ID of the task
	 * @throws IllegalArgumentException
	 */
	public static void cancelTask(long id) throws IllegalArgumentException {
		for (Thread thr : Thread.getAllStackTraces().keySet()) {
			if (thr.getId() == id) {
				thr.interrupt();
			}
		}
		new IllegalArgumentException("No matching Thread With ID :" + id);
	}
	
	/**
	 * 
	 * Cancel current task
	 * 
	 * @throws IllegalArgumentException
	 */
	public void cancelTask() throws IllegalArgumentException {
		for (Thread thr : Thread.getAllStackTraces().keySet()) {
			if (thr.getId() == this.getTaskId()) {
				thr.interrupt();
			}
		}
		new IllegalArgumentException("No matching Thread With ID :" + this.getTaskId());
	}
	
	/**
	 * 
	 * @param id : ID of the task
	 * @return true if the task is alive else is false
	 */
	public static boolean isAlive(long id) {
		for (Thread thr : Thread.getAllStackTraces().keySet()) {
			if (thr.getId() == id) {
				return thr.isAlive();
			}
		}
		new IllegalArgumentException("No matching Thread With ID :" + id);
		return false;
	}
	
	/**
	 * 
	 * @return true if the current task is alive else is false
	 */
	public boolean isAlive() {
		return thread.isAlive();
	}
	
	/**
	 * 
	 * @param id : ID of the task
	 * @return true if the task is daemon else is false
	 */
	public static boolean isDaemon(long id) {
		for (Thread thr : Thread.getAllStackTraces().keySet()) {
			if (thr.getId() == id) {
				return thr.isDaemon();
			}
		}
		new IllegalArgumentException("No matching Thread With ID :" + id);
		return false;
	}
	
	/**
	 * 
	 * @return true if the current task is daemon else is false
	 */
	public boolean isDaemon() {
		return thread.isDaemon();
	}
	
	/**
	 * 
	 * @return the current executable
	 */
	public Runnable getRunnable() {
		return run;
	}
	
	
	
}
