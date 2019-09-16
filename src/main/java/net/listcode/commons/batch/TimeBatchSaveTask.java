package net.listcode.commons.batch;

import java.util.Date;

//import net.listcode.context.ContextTool;

/**
 *  批量保存的数据，如果长时间没有读，或者没有新数据进来，后有一些一直留在内存中， 这个任务是定时将积攒的数据flush
 * @author leo
 *
 */
public class TimeBatchSaveTask extends BaseTimeTask{

	public TimeBatchSaveTask(int runIntervalOfSecond) {
		//第一次运行在初始化10s以后， 运行间隔来源于参数， true代表关闭此task时是否最后运行一次
		super(new Date(System.currentTimeMillis() + 10 * 1000L), runIntervalOfSecond, true);
	}

	@Override
	protected void doit() throws InterruptedException {
		/*
		一个BaseTimeTask的实现例子，注释掉的代码意思是 定期执行注册的写入方法，使用时只需要简单注册即可
		 */
//		for (Class<?> key :ContextTool.getFlushkeys()) {
//			ContextTool.tryFlush(key);
//		}
	}

	@Override
	protected void clearUp() throws InterruptedException {
		//冗余，理解起来简单
		this.doit();
	}

}
