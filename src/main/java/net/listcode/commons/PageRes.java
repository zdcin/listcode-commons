package net.listcode.commons;

import lombok.Value;

import java.io.Serializable;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 分页查询时，需要总数时，使用该包装类；不需要总数的分页直接使用list就够
 *
 * 翻页的几种场景：
 * 1， 以下实现针对这种方式
 * 输入：常量条件，pageSize常量，第几页变量
 * 输出1： 数据，总数，最大页（减少计算），当前页（冗余）
 * 输出2： 数据，当前页（冗余）
 *
 * 2， 这种方式没有实现
 * 输入：常量条件，pageSize常量， 变量条件（一般使用上一次list的最后一条数据，或者标明下一次的值）
 * 输出1: 数据
 * 输出2：数据，下次查询变量
 *
 * //todo 增加pagehelper构造方法
 * @author ZhangDong
 *
 * @param <T>
 */
@Value
public class PageRes<T> implements Serializable {
    private static final long serialVersionUID = 2533442163922235261L;

    /**当前页的数据*/
    private List<T> list;

    /**一共有多少条数据*/
    private int itemTotal;

    /**现在是第几页，从1开始算第一页*/
    private int pageNum;

    /**一页几条*/
    private int pageSize;

    /**最大页数，从1开始算*/

    private int pageTotal;

    /**
     * 是否计算总数， 如果不计算，pageTotal 和 totalNum 无意义
     */
    private boolean useCount;

    /**
     * 不带总数的构造方法
     * @param list
     * @param pageNum
     * @param pageSize
     */
    public PageRes(List<T> list, int pageNum, int pageSize) {
        this.list = list;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.useCount = false;
        this.itemTotal = -1;
        this.pageTotal = -1;
    }

    /**
     * 带总数的构造方法
     * @param list
     * @param itemTotal， 如果小于0，说明不使用总数，
     * @param pageNum
     * @param pageSize
     */
    public PageRes(List<T> list, int itemTotal, int pageNum, int pageSize) {
        if (itemTotal < 0) {
            this.list = list;
            this.pageNum = pageNum;
            this.pageSize = pageSize;
            this.useCount = false;
            this.itemTotal = -1;
            this.pageTotal = -1;
        } else {
            this.useCount = true;
            this.list = list;
            this.itemTotal = itemTotal;
            this.pageNum = pageNum;
            this.pageSize = pageSize;
            if (this.pageSize == 0) {
                this.pageTotal = 0;
            } else {
                this.pageTotal = (this.itemTotal + this.pageSize - 1) / this.pageSize;
            }
        }
    }

    /**
     * 私有构造方法，仅仅内部使用
     * @param list
     * @param itemTotal
     * @param pageNum
     * @param pageSize
     * @param pageTotal
     * @param useCount
     */
    private PageRes(List<T> list, int itemTotal, int pageNum, int pageSize, int pageTotal, boolean useCount) {
        this.list = list;
        this.itemTotal = itemTotal;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.pageTotal = pageTotal;
        this.useCount = useCount;
    }

//    /**
//     * 把当前list中的数据转化为另外一种类型，list size必须不变
//     * @param list
//     * @param <M>
//     * @return
//     */
//    public <M> PageRes<M> buildWithTransedList(List<M> list) {
//        PageRes<M> res = new PageRes<>(list, this.getItemTotal(), this.getPageNum(), this.getPageSize(), this.getPageTotal(), this.isUseCount());
//        return res;
//    }

    /**
     *
     * @param transformFn
     * @param <M>
     * @return
     */
    public <M> PageRes<M> buildWithTransform(Function<T, M> transformFn) {
        if (transformFn == null) {
            throw new IllegalArgumentException("transformFn can't be null!");
        }
        List<M> distList = null;
        if (this.list != null) {
            distList = this.list.stream().map(transformFn).collect(Collectors.toList());
        }
        PageRes<M> res = new PageRes<>(distList, this.getItemTotal(), this.getPageNum(), this.getPageSize(), this.getPageTotal(), this.isUseCount());
        return res;
    }
}
