package net.listcode.commons.batch;

import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author leo
 *
 */
@Slf4j
public abstract class BaseTimeTask implements Runnable {
	/*
	 * 1. 周期性执行业务方法 （固定执行时间点（时分），执行间隔（秒），首次是否执行【首次执行的时间要特殊处理】）
	 * 2. 优雅关闭，带超时
	 * 3. 是否关闭
	 */
	
	/**
	 * 周期性守护线程运行状态枚举值
	 */
	private enum TaskState {
		notStart, init, running, sleeping, stoping, stoped;
	}
	
	/**
	 * 
	 * @param fixRunPoint 固定执行时间点（时分）
	 * @param runIntervalOfSecond 执行间隔（秒）
	 * @param isRunAtStart 首次是否执行【首次执行的时间要特殊处理】
	 */
	public BaseTimeTask(Date fixRunPoint, int runIntervalOfSecond, boolean isRunAtStart) {
		long last = toRecntRunPoint(fixRunPoint, runIntervalOfSecond);
		this.fixRunPoint = new Date(last);
		this.runIntervalOfSecond = runIntervalOfSecond;
		this.isRunAtStart = isRunAtStart;
	}

	
	//设置到已经过去的最近的一个执行时间点
	private final Date fixRunPoint;
	private final int runIntervalOfSecond;
	private final boolean isRunAtStart;
	/*
	 * 这两个变量只被一个线程访问
	 */
	private int runNum = 0;
	private long lastRunAt = 0;
	
	/*
	 * 被多个线程读写
	 */
	private volatile TaskState taskState = TaskState.notStart;
	/**
	 * 检查是否需要sleep，需要的话，sleep到需要执行的时间点
	 * @throws InterruptedException
	 */
	private void checkAndMaySleep() throws InterruptedException {
		if (runNum == 0) {
			if (isRunAtStart) {
				//  虚拟到上一次整点运行时间
				this.lastRunAt = fixRunPoint.getTime();
				this.runNum ++;
				return;
			} else {
				// sleep 合适的时间
				long sleetTime = this.lastRunAt + runIntervalOfSecond * 1000L - System.currentTimeMillis();
				sleetTime = Math.min(runIntervalOfSecond * 1000L, sleetTime);
				sleetTime = Math.max(1, sleetTime);
				Thread.sleep(sleetTime);
				this.lastRunAt = System.currentTimeMillis();
				this.runNum ++;
				return;
			}
		}
		long now = System.currentTimeMillis();
		long distance = now - lastRunAt;
		if (distance >= (runIntervalOfSecond * 1000L)) {
			this.lastRunAt = System.currentTimeMillis();
			this.runNum ++;
			//return;
		} else {
			//必然大于0, 保险起见，额外检查
			long sleetTime = runIntervalOfSecond * 1000L - distance;
			sleetTime = Math.min(runIntervalOfSecond * 1000L, sleetTime);
			sleetTime = Math.max(1, sleetTime);
			Thread.sleep(sleetTime);
			this.lastRunAt = System.currentTimeMillis();
			this.runNum ++;
			//return;
		}
	}
	
	
	@Override
	public final void run() {
		this.taskState = (TaskState.init);
		while(this.taskState != TaskState.stoped && this.taskState != TaskState.stoping) {
			try {
				this.taskState = TaskState.sleeping;
				checkAndMaySleep();
				this.taskState = TaskState.running;
				doit();
			} catch(InterruptedException inter) {
				// 中断的发起者，已经改变了状态，不用管，会退出循环
				//inter.printStackTrace();
				log.warn(inter.getMessage(), inter);
				Thread.currentThread().interrupt();
			} catch(Exception e) {
				// 记录日志，不用管，进入下次循环就好
				//e.printStackTrace();
				log.warn(e.getMessage(), e);
			}
		}
		
		if (this.taskState == TaskState.stoping) {
			//收尾处理
			try {
				clearUp();
			} catch (InterruptedException e) {
				//立即 return 
				//e.printStackTrace();
				log.warn(e.getMessage(), e);
				Thread.currentThread().interrupt();
			} catch(Exception inter) {
				// 记录日志，不用管，return
				log.warn(inter.getMessage(), inter);
			}
		}
		synchronized (this) {
			this.taskState = TaskState.stoped;
			this.notifyAll();
		}
	}


	/**
	 * 要求可以被中断，最后一次运行时逻辑上要允许
	 * 
	 * @throws InterruptedException
	 */
	protected abstract void doit() throws InterruptedException;
	
	/**
	 * 收尾方法，stopSafly调用后会执行该方法，这是最后清理，保存等工作的机会
	 * @throws InterruptedException
	 */
	protected abstract void clearUp() throws InterruptedException;
	/**
	 * 返回停止线程的异步任务
	 * @param handle
	 * @param timeout
	 * @return
	 */
	public final synchronized FutureTask<Boolean> stopSafly(final Thread handle, final long timeout) {
		//使用异步任务来停止，不会阻塞调度线程
		this.taskState = TaskState.stoping;
		handle.interrupt();
		//newTask(this, timeout, handle);
		
		final BaseTimeTask waitObj = this;
		FutureTask<Boolean> task = new FutureTask<>(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				synchronized (waitObj) {
					if (!waitObj.isStoped()) {
						try {
							waitObj.wait(timeout);
						} catch (InterruptedException e) {
							log.warn(e.getMessage(), e);
							Thread.currentThread().interrupt();
						}
					}
				}
				handle.interrupt();
				return true;
			}
		});
		task.run();
		return task;
	}
	
	public final boolean isStoped() {
		return this.taskState == TaskState.stoped;
	}
	
//	public synchronized DeamonStat getState() {
//		return this.deamonStat;
//	}
//	
//	private synchronized void setState(DeamonStat stat) {
//		this.deamonStat = stat;
//	}
	
//	private void newTask(BaseDeamon waitObj, long timeout, Thread handle) {
//		synchronized (waitObj) {
//			if (!waitObj.isStoped()) {
//				try {
//					waitObj.wait(timeout);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//			}
//		}
//		handle.interrupt();
//	}

	
	
	/*
	 * 获取已经过去的最近的一个执行点
	 * @param fixRunPoint 一个任意的执行点，跟当前时间无关，可能大于，可能小于
	 * @param runIntervalOfSecond
	 * @return
	 */
	private long toRecntRunPoint(Date fixRunPoint, int runIntervalOfSecond) {
		long now = System.currentTimeMillis();
		long last = 0;
		
		final long FIX_RUN_AT = fixRunPoint.getTime();
		final long INTER = runIntervalOfSecond * 1000L;
		final long OFFSET = now - fixRunPoint.getTime();
		
		if (OFFSET > 0) {
			last = FIX_RUN_AT + OFFSET / INTER * INTER;
		} else if (OFFSET < 0) {
			last = FIX_RUN_AT + OFFSET / INTER * INTER - INTER;
		} else {
			last = now;
		}
		return last;
	}
	
//	public static void main(String[] args) {
	/*Fri Sep 21 14:33:53 CST 2018
	Fri Sep 21 14:33:53 CST 2018
	Fri Sep 21 14:33:23 CST 2018
	Fri Sep 21 14:29:23 CST 2018
	*/
//		long now = System.currentTimeMillis() ;
//		System.out.println(new Date(now));
//		System.out.println(new Date(toRecntRunPoint(new Date(now), 300)));
//		System.out.println(new Date(toRecntRunPoint(new Date(now - 30 * 1000L), 300)));
//		System.out.println(new Date(toRecntRunPoint(new Date(now + 30 * 1000L), 300)));
//	}
}
