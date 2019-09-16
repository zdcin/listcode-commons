package net.listcode.commons.batch;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * 批量保存某些数据的通用类，适用于单个添加低效，多个添加高效的场景
 * @author leo
 *
 * @param <T>
 */
public class LazyBatchSaver<T> implements AutoCloseable {

	private final int flashSize;
	private final Consumer<List<T>> consumer;
	private List<T> list;

	public LazyBatchSaver(int flashSize, Consumer<List<T>> consumer) {
		this.flashSize = flashSize;
		this.consumer = consumer;
		clearTempCache();
	}

	public synchronized void addAndMayFlush(T item) {
		this.list.add(item);
		if (this.list.size() >= this.flashSize) {
			List<T> temp = this.list;
			clearTempCache();
			this.consumer.accept(temp);
		}
	}
	
	private void clearTempCache() {
		this.list = new ArrayList<>(this.flashSize);
	}
	
	/**
	 * 强制写入
	 */
	public synchronized void flush() {
		if (!this.list.isEmpty()) {
			List<T> temp = this.list;
			clearTempCache();
			this.consumer.accept(temp);
		}
	}

	/**
	 * 使用 try() {}语法可以自动关闭，特殊情况也可以手动关闭
	 */
	@Override
	public synchronized void close() {
		this.flush();
	}
}
