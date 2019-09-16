package net.listcode.commons;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;


import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.listcode.commons.batch.LazyBatchSaverWithReturn;
import net.listcode.commons.batch.ValueHolder;

/**
 * 这个例子说明 LazyBatchSaverWithReturn 和 ValueHolder怎么用
 */
@Slf4j
public class BatchSaverAndValueHolder_example {
	@Data
	private static class EventHistory{
		private Integer id;
		private String name;
	}
	//<DATA, KEY, RETURN>
	private static LazyBatchSaverWithReturn<EventHistory, Long, Integer> batchSaver;
	
	static {
		Function<List<EventHistory>, List<Integer>> function = eventList -> {
			List<Integer> ids = new ArrayList<>();
			try {
				if (eventList == null) {
					return ids;
				}
				List<EventHistory> r = null; //TODO ServiceUtils.getEventHistoryService().batchSave(eventList);
				ids = Fn.map(r, x -> x.getId());
			} catch (Exception e) {
				log.error("error in EventTool batch save, " + e.getMessage(), e);
			}
			return ids;
		};
		batchSaver = new LazyBatchSaverWithReturn<>(100, function
				, 100 * 1000);

		/*TODO 注释掉的两行代码表示用定时任务 批量保存数据*/
//		ContextTool.registFlushCallBack(EventHistory.class, t -> batchSaver.flushAll());
//		ContextTool.registClearUp(null, t -> batchSaver.close());
	}

	

	public static ValueHolder<Integer, Long> saveEvent(String name) {
		EventHistory entity = new EventHistory();
		entity.setName(name);

		long signKey = System.currentTimeMillis();// 模拟获取唯一标识，目前近似表示  UniqueId.next();
		batchSaver.addAndMayFlush(signKey, entity);
		//给 valueholder 使用的求值方法
		ValueHolder<Integer, Long> result = new ValueHolder<>( batchSaver::getAndRemove, signKey, batchSaver::ignore);
		return result;
	}


	public static void flushToDb() {
		batchSaver.flushAll();
	}
	
}
