package net.listcode.commons;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

/**
 * 表迭代器
 *
 * 这个迭代器 可以用于没有总数的高效分页查询, 也可以包装后加上总数, 支持带总数的分页查询
 *
 *
 * 优化: 预先加载, 甚至异步加载
 *
 * @author LeoZhang
 * @param <T>
 */
public class TableIterator<T> implements Iterable<T>{
    /**
     *
     * @param findFn 输入T,返回list《T》的查询功能
     * @param choiseNextParaFn 从listT中,取出一个, 作为下一次的输入条件
     */
    public TableIterator(
        Function<T, List<T>> findFn,
        Function<List<T>, T> choiseNextParaFn
    ) {

    }

    /**
     *
     * @param findFn 输入T,返回list《T》的查询功能
     * @param choiseNextParaFn 从listT中,取出一个, 作为下一次的输入条件
     * @param loadBatchSize 内部每批加载的size
     * @param asyncLoad 是否异步加载
     */
    public TableIterator(
            Function<T, List<T>> findFn,
            Function<List<T>, T> choiseNextParaFn, int loadBatchSize, boolean asyncLoad
    ) {

    }

    @Override
    public Iterator<T> iterator() {
        return null;
    }

//    public List<T> nextBatch(int batchSize) {
//        List<T> list = new ArrayList<>(batchSize);
//
//        for (int i = 0; i < batchSize; i++) {
//
//        }
//    }
}
