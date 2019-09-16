package net.listcode.commons.batch;

import java.util.function.Consumer;
import java.util.function.Function;


/**
 * 延迟获取某些值， 与 LazyBatchSaverWithReturn 配合使用
 * @author leo
 *
 */
public class ValueHolder<REAL_VALUE, HANDLE> {
	private REAL_VALUE v;
	private boolean hasValue = false;
	private Function< HANDLE, REAL_VALUE> evaluatFun = null;
	private final HANDLE handle;
	private  Consumer< HANDLE > ignoreMethod;
	public ValueHolder(Function< HANDLE, REAL_VALUE> supplier, HANDLE handle) {
		this.evaluatFun = supplier;
		this.handle = handle;
		this.ignoreMethod = null;
	}
	public ValueHolder(Function< HANDLE, REAL_VALUE> supplier, HANDLE handle, Consumer< HANDLE > ignoreMethod) {
		this.evaluatFun = supplier;
		this.handle = handle;
		this.ignoreMethod = ignoreMethod;
	}
	public REAL_VALUE get() {
		if (!hasValue) {
			return forceGet();
		} else {
			return this.v;
		}
	}
	
	public HANDLE getHandle() {
		return this.handle;
	}
	
	public REAL_VALUE tryGet() {
		if (!hasValue) {
			return null;
		} else {
			return this.v;
		}
	}
	
	public void ignore() {
		if (this.ignoreMethod == null) {
			throw new IllegalStateException("[ignoreMethod] filed is null");
		} else {
			ignoreMethod.accept(this.handle);
		}
	}
	
	public boolean hasValue() {
		return this.hasValue;
	}
	
	public void setValue(REAL_VALUE v) {
		this.v = v;
		this.hasValue = true;
	}
	
	private REAL_VALUE forceGet() {
		REAL_VALUE x =  evaluatFun.apply(this.handle);
		this.hasValue = true;
		return x;
	}
}
