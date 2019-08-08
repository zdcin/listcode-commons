package net.listcode.commons;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import lombok.extern.slf4j.Slf4j;

/**
 * 多个文件逐行遍历迭代器，流式读取，占用内存低
 *
 * @author leo
 */
@Slf4j
public class MutiFileLineIterator implements Iterator<String>, Closeable {
	/** 文件名列表 */
	private final List<String> fileNames;
	/** 第几个文件 */
	private int fileIndex = 0;
	/** 文件输入流，低层 */
	private InputStreamReader isr;
	/** 文件读取器，高层 */
	private BufferedReader currentReader;

	// private String nextLine;

	/** 缓存读到的行数据, 保证下一行一定要预先加载，否则 hasNext方法会出错 */
	private LinkedList<String> queue = new LinkedList<>();

	public MutiFileLineIterator(List<String> fileNames) {
		// TODO 空文件，或者文件不存在的情况
		this.fileNames = fileNames;
		for (String f : this.fileNames) {
			log.info("input file : " + f);
		}
		this.initReader();
		// 读第一行到缓存
		try {
			this.doPreLoad();
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
	}

	private void initReader() {
		try {
			File firstFile = new File(this.fileNames.get(this.fileIndex));
			isr = new InputStreamReader(new FileInputStream(firstFile), "UTF-8");
			currentReader = new BufferedReader(isr);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
	}

	private boolean hasNextFile() {
		if (fileIndex + 1 >= this.fileNames.size()) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public boolean hasNext() {
		if (!this.queue.isEmpty()) {
			return true;
		}
		return false;
	}

	@Override
	public String next() {
		if (!hasNext()) {
			throw new NoSuchElementException();
		}
		
		try {
			return this.pickNext();
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			return null;
		}
	}

	private String pickNext() throws IOException {
		String result = null;
		if (!this.queue.isEmpty()) {
			result = this.queue.removeFirst();
		}
		// 检查预加载的逻辑
		this.doPreLoad();
		return result;
	}

	/**
	 * 执行预加载，目前只是加载下一行
	 */
	private void doPreLoad() throws IOException {
		
		String nextLine = currentReader.readLine();
		if (nextLine == null) {
			while (this.hasNextFile()) {
				this.toNextFile();
				nextLine = currentReader.readLine();
				if (nextLine != null) {
					break;
				}
			}
		}
		if (nextLine != null) {
			this.queue.addLast(nextLine);
		} // else 什么也不用做
	}

	private void toNextFile() throws IOException {
		this.closeCurrentStream();
		this.fileIndex++;
		this.initReader();
	}

	private void closeCurrentStream() throws IOException {
		if (currentReader != null) {
			currentReader.close();
			currentReader = null;
		}
		if (isr != null) {
			isr.close();
			isr = null;
		}
	}

//        int cursor;       // index of next element to return
//        int lastRet = -1; // index of last element returned; -1 if no such
//        int expectedModCount = modCount;
//       ...
	@Override
	public void close() {
		try {
			this.closeCurrentStream();
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
	}
}