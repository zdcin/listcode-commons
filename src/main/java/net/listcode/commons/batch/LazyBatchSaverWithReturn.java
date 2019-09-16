package net.listcode.commons.batch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * 批量保存某些数据的通用类，适用于单个添加低效，多个添加高效的场景
 * 延迟求值的问题是可能内存占满，这里的策略是最多保存若干返回值，超出的不提供缓存
 * 针对超出的值，可以提供一个从低速存储（可能是db）中拿到值得suplier 函数
 * 使用这个工具需要注意，不能老add不get，否则可能会被自动删除，
 * 如果不需要get的场景，务必使用单参数的add，get尽量保证被调用，并且及时被调用
 * @author leo
 *
 * @param <DATA, KEY, RETURN>
 */
public class LazyBatchSaverWithReturn<DATA, KEY, RETURN> implements AutoCloseable {

	private final int flashSize;
	private final int holdReturnValueMaxNum;
	private final Function<List<DATA>, List<RETURN>> function;
	private List<KEY> list;
	private List<DATA> simpleList;
	private Map<KEY, DATA> dataMap = new HashMap<>();
	private Map<KEY, RETURN> returnMap = new HashMap<>();
	private LinkedList<KEY> returnKey = new LinkedList<>();
	
	/**
	 * 
	 * @param flashSize
	 * @param consumer
	 * @param holdReturnValueMaxNum 需要取返回值时，这个参数表示最多缓存多少个返回值，
	 */
	public LazyBatchSaverWithReturn(int flashSize, Function<List<DATA>, List<RETURN>> consumer, int holdReturnValueMaxNum) {
		this.flashSize = flashSize;
		this.function = consumer;
		this.holdReturnValueMaxNum = holdReturnValueMaxNum;
		this.list = clearAndInitList1();
		this.simpleList = clearAndInitList2();
	}

	public synchronized void addAndMayFlush(KEY signKey, DATA item) {
		this.list.add(signKey);
		this.dataMap.put(signKey, item);
		if (this.list.size() + this.simpleList.size() >= this.flashSize) {
			this.flushAll();
		}
	}
	
	private List<KEY> clearAndInitList1() {
		return new ArrayList<>(this.flashSize);
	}
	
	private List<DATA> clearAndInitList2() {
		return new ArrayList<>(this.flashSize);
	}
	
	
	/**
	 * 不需要被hold时使用
	 * @param item
	 */
	public synchronized void addAndMayFlush(DATA item) {
		this.simpleList.add(item);
		
		if (this.list.size() + this.simpleList.size() >= this.flashSize) {
			this.flushAll();
		}
	}
	
	/**
	 * 使用带key参数的add方法添加，但又确定不会调用getAndRemove
	 * @param signKey
	 */
	public synchronized void ignore(KEY signKey) {
		if (this.returnMap.containsKey(signKey)) {
			//1 已经求值完成的情况
			this.returnKey.remove(signKey);
			this.returnMap.remove(signKey);
		} else if (this.dataMap.containsKey(signKey)) {
			//2 将要求值的 但目前未求值的情况， 转移到简单list中
			this.list.remove(signKey);
			DATA data = this.dataMap.remove(signKey);
			this.simpleList.add(data);
		}
	}
	
	public synchronized void flushAll() {
		if (!this.list.isEmpty()){
			List<KEY> tempKeyList = this.list;
			this.list = this.clearAndInitList1();
			List<DATA> tempDatalist = new ArrayList<>(tempKeyList.size());
			for (KEY key : tempKeyList) {
				DATA data = this.dataMap.remove(key);
				tempDatalist.add(data);
			}
			//被求值，返回的是真实值
			List<RETURN> rList = this.function.apply(tempDatalist);
			
			for (int i = 0; i < tempKeyList.size(); i++) {
				this.returnKey.addLast(tempKeyList.get(i));
				returnMap.put(tempKeyList.get(i), rList.get(i));
			}
			//收缩
			while (this.returnKey.size() > this.holdReturnValueMaxNum) {
				KEY key = this.returnKey.removeFirst();
				returnMap.remove(key);
			}
		}
		
		//2. simple list
		if (!this.simpleList.isEmpty()) {
			List<DATA> tempSList = this.simpleList;
			this.simpleList = this.clearAndInitList2();
			this.function.apply(tempSList);
		}
	}
	
	/**
	 * 立即获取真实值，如果没有被求值，立即求值并返回
	 * @param signKey
	 * @return
	 */
	public synchronized RETURN getAndRemove(KEY signKey) {
		if (this.returnMap.containsKey(signKey)) {
			return this.returnMap.remove(signKey);
		} else if (this.dataMap.containsKey(signKey)) {
			return forceGetAndRemove(signKey);
		} else {
			return null;
		}
	}
	
	/**
	 * 尝试获取真实值，如果目前没有被求值，返回null
	 * @param signKey
	 * @return
	 */
	public synchronized RETURN tryGetAndRemove(KEY signKey) {
		if (this.returnMap.containsKey(signKey)) {
			return this.returnMap.remove(signKey);
		} else {
			return null;
		}
	}

	/*强制求值，并返回真实值*/
	private RETURN forceGetAndRemove(KEY signKey) {
		if (this.dataMap.containsKey(signKey)) {
			this.flushAll();
		}
		
		if (this.returnMap.containsKey(signKey)) {
			return this.returnMap.remove(signKey);
		} else {
			return null;
		}
	}

	/**
	 * 使用 try() {}语法可以自动关闭，特殊情况也可以手动关闭
	 */
	@Override
	public synchronized void close() {
		flushAll();
	}
}
