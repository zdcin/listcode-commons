package net.listcode.commons;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

/**
 * 解决当次写入数据太多导致性能下降的问题，自动拆分成多个小的批量，多次写入
 * TODO 目前只处理了写入无返回值的情况，如果有返回值，目前不支持，需要扩展
 * @author LeoZhang
 */
public class BatchSaveSplitTool {

    private BatchSaveSplitTool(){}

    public enum SaveErrorPolicy {
        /**
         * 1. 全部停止，抛出异常
         */
        THROW,
        /**
         * 2. 忽略当次小批量，继续执行后续
         */
        IGNORE_SMALL_BATCH,
        /**
         * 3. 发生错误的小批量全部单条重试<br>
         *  注意 保存操作的幂等性，比如使用 insert ignore 保证insert只插入一次，并且不会抛出异常
         */
        RETRY_ALL;
    }

    /**
     * 把大size的批量写入改成多次批量写入，每次写入100个，写入发生异常时，会直接抛出
     * @param batchSaveFn
     * @param items
     * @param <T>
     */
    public static <T> void batchSaveWithSplit(Consumer<Collection<T>> batchSaveFn, Collection<T> items) {
        batchSaveWithSplit(batchSaveFn, items, 100);
    }

    /**
     * 把大size的批量写入改成多个小size的多次批量写入，写入发生异常时，会直接抛出
     * @param batchSaveFn
     * @param items
     * @param blockSize
     * @param <T>
     */
    public static <T> void batchSaveWithSplit(Consumer<Collection<T>> batchSaveFn, Collection<T> items, int blockSize) {
        if (items == null || items.isEmpty()) {
            return;
        }
        if (batchSaveFn == null) {
            throw new IllegalArgumentException("batchSaveFn can't be null!");
        }

        if (blockSize < 1) {
            throw new IllegalArgumentException("blockSize must > 0 !");
        }
        List<T> list = toList(items);

        for (List<T> subList : Fn.split(list, blockSize)) {
            batchSaveFn.accept(subList);
        }
    }

    /**
     *  把大size的批量写入改成多次批量写入，每次写入100个，<br>
     *  相当于调用 <br>
     *  batchSaveWithSplit(batchSaveFn, items,100, policy, errorItems, errors, singlItemSaveFn);<br>
     *
     * @param batchSaveFn
     * @param items
     * @param policy
     * @param errorItems
     * @param errors
     * @param singlItemSaveFn
     * @param <T>
     */
    public static <T> void batchSaveWithSplit(Consumer<Collection<T>> batchSaveFn, Collection<T> items,
                                              SaveErrorPolicy policy,
                                              List<T> errorItems,
                                              List<Exception> errors,
                                              Consumer<T> singlItemSaveFn) {
        batchSaveWithSplit(batchSaveFn, items,100, policy, errorItems, errors, singlItemSaveFn);
    }

    /**
     *  把大size的批量写入改成多次批量写入，每次写入100个，<br>
     *  相当于调用 <br>
     *  batchSaveWithSplit(batchSaveFn, items,100, policy, errorItems, errors, null);<br>
     *
     * @param batchSaveFn
     * @param items
     * @param policy
     * @param errorItems
     * @param errors
     * @param <T>
     */
    public static <T> void batchSaveWithSplit(Consumer<Collection<T>> batchSaveFn, Collection<T> items,
                                              SaveErrorPolicy policy,
                                              List<T> errorItems,
                                              List<Exception> errors) {
        batchSaveWithSplit(batchSaveFn, items,100, policy, errorItems, errors, null);
    }

    /**
     * 把大size的批量写入改成多个小size的多次批量写入
     * @param batchSaveFn
     * @param items
     * @param blockSize
     * @param policy 写入出错的处理策略
     * @param errorItems 非THROW策略时，记录写入失败的数据，如果null，会导致无法记录
     * @param errors 非THROW策略时，记录写入失败时发生的异常，如果null，会导致无法记录
     * @param singlItemSaveFn RETRY_ALL策略时，单个数据写入的方法，如果是null，会使用batchSaveFn方法包装代替
     * @param <T>
     */
    public static <T> void batchSaveWithSplit(Consumer<Collection<T>> batchSaveFn, Collection<T> items,
                                              int blockSize,
                                              SaveErrorPolicy policy,
                                              List<T> errorItems,
                                              List<Exception> errors,
                                              Consumer<T> singlItemSaveFn) {
        if (items == null || items.isEmpty()) {
            return;
        }
        if (batchSaveFn == null) {
            throw new IllegalArgumentException("batchSaveFn can't be null!");
        }
        if (blockSize < 1) {
            throw new IllegalArgumentException("blockSize must > 0 !");
        }
        if (policy == null) {
            throw new IllegalArgumentException("policy can't be null!");
        }
        List<T> list = toList(items);

        if (singlItemSaveFn == null) {
            singlItemSaveFn = x -> {
                batchSaveFn.accept(Arrays.asList(x));
            };
        }

        for (List<T> subList : Fn.split(list, blockSize)) {
            try {
                batchSaveFn.accept(subList);
            } catch(Exception e1) {
                switch (policy) {
                    case THROW:
                        throw new IllegalStateException(e1);
                    case IGNORE_SMALL_BATCH:
                        if (errorItems != null) {
                            errorItems.addAll(subList);
                        }
                        if (errors != null) {
                            errors.add(e1);
                        }
                        break;
                    case RETRY_ALL:
                        for (T i : subList) {
                            try {
                                singlItemSaveFn.accept(i);
                            } catch (Exception e2) {
                                if (errorItems != null) {
                                    errorItems.add(i);
                                }
                                if (errors != null) {
                                    errors.add(e2);
                                }
                            }
                        }
                        break;
                    default:
                        throw new IllegalArgumentException("policy illegal!");
                }
            }
        }
    }

    private static <T> List<T> toList(Collection<T> items) {
        List<T> list;
        if (items instanceof List) {
            list = (List<T>) items;
        } else {
            list = new ArrayList<>(items);
        }
        return list;
    }

}
