package net.listcode.commons;

import net.listcode.commons.types.MyTuple;
import net.listcode.commons.types.MyTuple3;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 函数式编程辅助工具类
 * @author LeoZhang
 */
public class Fn {

	private Fn(){}
    private static final String FN_NOT_NULL_MSG = "lambda表达式参数不能为空";
    private static final String COMPARATOR_NOT_NULL_MSG = "比较表达式参数不能为空";
    private static final String ENSURE_NOT_NULL_MSG = "ensure参数不能为空";

	/**
     *List集合容器级别的copy，内部元素引用相同
     * @param list
     * @param <T>
     * @return 如果输入参数为空，则返回一个size=0的ArrayList
     */
    public static <T> List<T> copy(List<T> list) {
        if (list == null) {
            return new ArrayList<>();
        }
        return list.stream().collect(Collectors.toList());
    }

    /**
     *  Set集合容器级别的copy，内部元素引用相同
     * @param set
     * @param <T>
     * @return 如果输入参数为空，则返回一个size=0的hashset
     */
    public static <T> Set<T> copy(Set<T> set) {
        if (set == null) {
            return new HashSet<>();
        }
        return set.stream().collect(Collectors.toSet());
    }

    /**
     * 深拷贝，List集合元素级别copy，容器引用不同,内部元素引用不同
     * @param list
     * @param itemCopyFn  传入自定义lambda表达式改变List元素,改变后的泛型不变
     * @param <T>
     * @return 如果输入参数为空，则返回一个size=0的ArrayList
     */
    public static <T> List<T> deepCopy(List<T> list, Function<T, T> itemCopyFn) {
        if (list == null) {
            return new ArrayList<>();
        }
        if(itemCopyFn == null){
            throw new IllegalArgumentException(FN_NOT_NULL_MSG);
        }
        return list.stream().map(itemCopyFn).collect(Collectors.toList());
    }


    /**
     *  深拷贝，Set集合元素级别copy，容器引用不同，内部元素引用不同
     * @param set
     * @param itemCopyFn 元素的copy方法
     * @return
     */
    public static <T> Set<T> deepCopy(Set<T> set, Function<T, T> itemCopyFn) {
        if (set == null) {
            return new HashSet<>();
        }
        if(itemCopyFn == null){
            throw new IllegalArgumentException(FN_NOT_NULL_MSG);
        }
        return set.stream().map(itemCopyFn).collect(Collectors.toSet());
    }

    /**
     *  深拷贝，List集合元素级别copy，容器引用不同,内部元素引用不同
     * @param list
     * @param function 传入自定义lambda表达式改变List元素,改变后的泛型改变
     * @param <T>
     * @param <R> 改变后的元素泛型
     * @return
     */
    public static <T, R> List<R> map(List<T> list, Function<T, R> function) {
        if (list == null) {
            return new ArrayList<>();
        }
        if(function == null){
            throw new IllegalArgumentException(FN_NOT_NULL_MSG);
        }
        return list.stream().map(function).collect(Collectors.toList());
    }


    /**
     *  将数组copy到List集合中，copy后改变原数组元素泛型
     * @param array
     * @param function 传入自定义lambda表达式改变数组元素,改变后的泛型改变
     * @param <T>
     * @param <R> 改变后元素的泛型
     * @return  如果输入参数为空，则返回一个size=0的ArrayList
     */
    public static <T, R> List<R> map(T[] array, Function<T, R> function) {
        if (array == null) {
            return new ArrayList<>();
        }
        if(function == null){
            throw new IllegalArgumentException(FN_NOT_NULL_MSG);
        }
        List<R> r = new ArrayList<>(array.length);
        for (int i = 0; i < array.length; i++) {
            r.add(i, function.apply(array[i]));
        }
        return r;
    }


    /**
     * Set集合元素级别copy，容器引用不同,内部元素引用不同，copy后元素泛型改变
     * @param set
     * @param function 传入自定义lambda表达式改变Set元素,改变后的泛型改变
     * @param <T>
     * @param <R> 改变后元素的泛型
     * @return 如果传入参数为空 返回一个size为0的HashSet
     */
    public static <T, R> Set<R> map(Set<T> set, Function<T, R> function) {
        if (set == null) {
            return new HashSet<>();
        }
        if(function == null){
            throw new IllegalArgumentException(FN_NOT_NULL_MSG);
        }
        return set.stream().map(function).collect(Collectors.toSet());
    }

    /**
     * 根据断言过滤List集合元素
     * @param list
     * @param predicate  lambda布尔类型表达式
     * @param <T>
     * @return
     */
    public static <T> List<T> filter(List<T> list, Predicate<T> predicate) {
        if (list == null) {
            return new ArrayList<>();
        }
        if(predicate == null){
            throw new IllegalArgumentException(FN_NOT_NULL_MSG);
        }
        return list.stream().filter(predicate).collect(Collectors.toList());
    }


    /**
     * 根据断言过滤Set集合
     * @param set
     * @param predicate lambda布尔类型表达式
     * @param <T>
     * @return
     */
    public static <T> Set<T> filter(Set<T> set, Predicate<T> predicate) {
        if (set == null) {
            return new HashSet<>();
        }
        if(predicate == null){
            throw new IllegalArgumentException(FN_NOT_NULL_MSG);
        }
        return set.stream().filter(predicate).collect(Collectors.toSet());
    }


    /**
     * 对传入的List集合直接进行过滤，无返回值，如果list为空，直接 return
     * @param list
     * @param predicate  lambda布尔类型表达式
     */
    public static <T> void doFilter(List<T> list, Predicate<T> predicate) {
        if (list == null) {
            return;
        }
        if(predicate == null){
            throw new IllegalArgumentException(FN_NOT_NULL_MSG);
        }
        list.removeIf(t -> !predicate.test(t));
    }


    /**
     * 对传入的Set集合直接进行过滤，无返回值，如果Set为空，直接 return
     * @param set
     * @param predicate lambda布尔类型表达式
     */
    public static <T> void doFilter(Set<T> set, Predicate<T> predicate) {
        if (set == null) {
            return;
        }
        if(predicate == null){
            throw new IllegalArgumentException(FN_NOT_NULL_MSG);
        }
        set.removeIf(t -> !predicate.test(t));
    }

    /**
     * 将一个list分隔成元素个数最多为 blockSize 的多个list，最后一个list的size可能不足
     * 分隔前的list和分割后的多个list中元素引用不会改变，使用时如果有写操作需要注意
     * 如果T为引用类型，那么在subList对元素的修改会反映到list中
     * @param list
     * @param blockSize 分隔后的集合最大size
     * @param <T>
     * @return 多个list的集合，最后一个list的大小可能不足blockSize
     */
    public static <T> List<List<T>> split(List<T> list, int blockSize) {
        List<List<T>> ll = new ArrayList<>();
        if (list == null || list.isEmpty()) {
            return ll;
        }
        //拆分集合的个数
        int blockNum = list.size() / blockSize + (list.size() % blockSize > 0 ? 1:0);
        for (int i = 0; i < blockNum; i++) {
            int from = i * blockSize;
            int to = Math.min(list.size(), from + blockSize );
            ll.add(list.subList(from , to));
        }
        return ll;
    }


    /**
     * 将List拆分后再进行条件copy，copy后不改变原List元素泛型
     * @param list
     * @param blockSize  分隔后的集合最大size
     * @param copyFn  传入自定义lambda表达式改变List元素,改变后的泛型不变
     * @param <T>
     * @return
     */
    public static <T> List<List<T>> splitWithCopy(List<T> list, int blockSize, Function<T, T> copyFn) {
        List<List<T>> ll = new ArrayList<>();
        if (list == null || list.isEmpty()) {
            return ll;
        }
        if(copyFn == null){
            throw new IllegalArgumentException(FN_NOT_NULL_MSG);
        }
        //拆分集合的个数
        int blockNum = list.size() / blockSize + (list.size() % blockSize > 0 ? 1 : 0);
        for (int i = 0; i < blockNum; i++) {
            int from = i * blockSize;
            int to = Math.min(list.size(), from + blockSize);
            List<T> subList = list.subList(from, to);
            List<T> temp = new ArrayList<>(subList.size());
            for (T x : subList) {
                temp.add(copyFn.apply(x));
            }
            ll.add(temp);
        }
        return ll;
    }


    /**
     * List集合中的元素按照元素根据lambda拷贝后的值（拷贝后为Integer类型）进行分组
     * @param list
     * @param hashFn 传入自定义lambda表达式改变List元素,改变后的泛型为Integer类型
     * @param <T>
     * @return   如果传入的list为空，返回一个size为0的ArrayList
     */
    public static <T> List<List<T>> group(List<T> list, Function<T, Integer> hashFn) {
            if(list == null){
              return new ArrayList<>();
            }
            if(hashFn == null){
                throw new IllegalArgumentException(FN_NOT_NULL_MSG);
            }
            Map<Integer, List<T>> temp = new TreeMap<>();
            for (T x : list) {
                int hash = hashFn.apply(x);
                if (!temp.containsKey(hash)) { //如果map中没有copy后的值
                    temp.put(hash, new LinkedList<>());
                }//如果有
                temp.get(hash).add(x);
            }
        //返回map集合的值
        return new ArrayList<>(temp.values());
        //throw new UnsupportedOperationException();
    }


    /**
     * List集合中的元素按照元素根据lambda拷贝后的值（拷贝后为Integer类型）进行分组后再copy(不改变泛型)
     * @param list
     * @param hashFn 传入自定义lambda表达式改变List元素,改变后的泛型为Integer类型
     * @param copyFn 传入自定义lambda表达式改变分组后List元素,不改变泛型
     * @param <T>
     * @return   如果传入的list为空，返回一个size为0的ArrayList
     */
    public static <T> List<List<T>> groupWithCopy(List<T> list, Function<T, Integer> hashFn, Function<T, T> copyFn) {
        if(list == null){
            return new ArrayList<>();
        }
        if(hashFn == null){
            throw new IllegalArgumentException(FN_NOT_NULL_MSG);
        }
        Map<Integer, List<T>> temp = new TreeMap<>();
        for (T x : list) {
            int hash = hashFn.apply(x);
            if (!temp.containsKey(hash)) {
                temp.put(hash, new LinkedList<>());
            }
            temp.get(hash).add(copyFn.apply(x));
        }
        return new ArrayList<>(temp.values());
        //throw new UnsupportedOperationException();
    }


    /**
     * Set集合中的元素按照元素根据lambda拷贝后的值（拷贝后为Integer类型）进行分组
     * @param
     * @param hashFn 传入自定义lambda表达式改变Set元素,改变后的泛型为Integer类型
     * @param <T>
     * @return
     */
    public static <T> List<Set<T>> group(Set<T> set, Function<T, Integer> hashFn) {
        if(set == null){
            return new ArrayList<>();
        }
        if(hashFn == null){
            throw new IllegalArgumentException(FN_NOT_NULL_MSG);
        }
        Map<Integer, List<T>> temp = new TreeMap<>();
        for (T x : set) {
            int hash = hashFn.apply(x);
            if (!temp.containsKey(hash)) {
                temp.put(hash, new LinkedList<>());
            }
            temp.get(hash).add(x);
        }
        List<Set<T>> groupList = new ArrayList<>(temp.size());
        //LinkedList转HashSet
		for (Entry<Integer, List<T>> entry : temp.entrySet()) {
			Set<T> s = new HashSet<>(entry.getValue().size());
			for (T t : entry.getValue()) {
				s.add(t);
			}
			groupList.add(s);
		}
        return  groupList;
        //throw new UnsupportedOperationException();
    }


    /**
     * Set集合中的元素按照元素根据lambda拷贝后的值（拷贝后为Integer类型）进行分组后再拷贝
     * @param
     * @param hashFn 传入自定义lambda表达式改变Set元素,改变后的泛型为Integer类型
     * @param copyFn 传入自定义lambda表达式改变分组后的Set元素,改变后泛型不变
     * @param <T>
     * @return
     */
    public static <T> List<Set<T>> groupWithCopy(Set<T> set, Function<T, Integer> hashFn, Function<T, T> copyFn) {
        if(set == null){
            return new ArrayList<>();
        }
        if(hashFn == null || copyFn == null){
            throw new IllegalArgumentException(FN_NOT_NULL_MSG);
        }
        Map<Integer, List<T>> temp = new TreeMap<>();
        for (T x : set) {
            int hash = hashFn.apply(x);
            if (!temp.containsKey(hash)) {
                temp.put(hash, new LinkedList<T>());
            }
            temp.get(hash).add(copyFn.apply(x));
        }

        List<Set<T>> groupList = new ArrayList<>(temp.size());
        //LinkedList转HashSet
		for (Entry<Integer, List<T>> entry : temp.entrySet()) {
			Set<T> s = new HashSet<>(entry.getValue().size());
			for (T t : entry.getValue()) {
				s.add(t);
			}
			groupList.add(s);
			s.clear();
		}
        return  groupList;
        //throw new UnsupportedOperationException();
    }


    /**
     * 按两个List的原顺存储两个集合中的元素，一对元素维护在MyTuple<T1, T2>>类中
     * @param list1
     * @param list2
     * @param <T1>
     * @param <T2>
     * @return
     */
    public static <T1, T2> List<MyTuple<T1, T2>> zip(List<T1> list1, List<T2> list2) {
        if(list1 == null || list2 == null){
            throw new IllegalArgumentException("参数均不能为空");
        }
        //两个List中最大的集合大小
        int max = Math.max(list1.size(), list2.size());
        List<MyTuple<T1, T2>> list = new ArrayList<>(max);
        for (int i = 0; i < max; i++) {
            T1 no1 = i >= list1.size() ? null : list1.get(i);
            T2 no2 = i >= list2.size() ? null : list2.get(i);
            list.add( new MyTuple<>(no1, no2));
        }
        return list;
    }


    /**
     * 按三个List的原顺存储三个集合中的元素，三个元素维护在MyTuple3<T1, T2，T3>>类中
     * @param list1
     * @param list2
     * @param list3
     * @param <T1>
     * @param <T2>
     * @param <T3>
     * @return
     */
    public static <T1, T2, T3> List<MyTuple3<T1, T2, T3>> zip(List<T1> list1, List<T2> list2, List<T3> list3) {
        if(list1 == null || list2 == null || list3 == null){
            throw new IllegalArgumentException("参数均不能为空");
        }
        //三个List中最大的集合大小
        int max = Math.max(list3.size(), Math.max(list1.size(), list2.size()));
        List<MyTuple3<T1, T2, T3>> list = new ArrayList<>(max);
        for (int i = 0; i < max; i++) {
            T1 no1= i >= list1.size() ? null : list1.get(i);
            T2 no2 = i >= list2.size() ? null : list2.get(i);
            T3 no3 = i >= list3.size() ? null : list3.get(i);
            MyTuple3<T1, T2, T3> e = new MyTuple3<>(no1, no2, no3);
            list.add(e);
        }
        return list;
    }


    /**
     * 把List每个元素之间串联指定的分隔符字符串sp
     * @param sp 连接元素间的字符串
     * @param list
     * @param <T>
     * @return 串联后的字符串   如果List为空，则返回空字符串
     */
    public static <T> String join(String sp, List<T> list) {
        return String.join(sp, map(list, t -> "" + t));
    }


    /**
     * 把List每个元素按指定lambda表达式copy（copy后为字符串类型）后再用分隔符字符串sp串联
     * @param sp 连接元素间的字符串
     * @param list
     * @param toStrFn
     * @param <T>
     * @return 串联并拷贝后的字符串   如果List为空，则返回空字符串
     */
    public static <T> String join(String sp, List<T> list, Function<T, String> toStrFn) {
        if(toStrFn == null){
            throw new IllegalArgumentException(FN_NOT_NULL_MSG);
        }
        return String.join(sp, map(list, t -> toStrFn.apply(t)));
    }


    /**
     * 把数组每个元素之间串联指定的分隔符字符串sp
     * @param sp 连接元素间的字符串
     * @param list
     * @param <T>
     * @return  如果数组为null，返回空字符串
     */
    public static <T> String join(String sp, T[] list) {

        return String.join(sp, map(list, t -> "" + t));
    }


    /**
     * 把数组每个元素按指定lambda表达式copy（copy后为字符串类型）后再用分隔符字符串sp串联
     * @param sp 连接元素间的字符串
     * @param list
     * @param toStrFn
     * @param <T>
     * @return  List如果为null，返回空字符串
     */
    public static <T> String join(String sp, T[] list, Function<T, String> toStrFn) {
        if(toStrFn == null){
            throw new IllegalArgumentException(FN_NOT_NULL_MSG);
        }
        return String.join(sp, map(list, t -> toStrFn.apply(t)));
    }


    /**
     * 对List按照指定比较器进行排序，返回新的List，不改变原List
     * T为基本数据类型、基本数据类型包装类或String，Comparator可传空，否则不可传空
     * @param list
     * @param comp 自定义比较器
     * @param <T>
     * @return
     */
    public static <T> List<T> sortWithCopy(List<T> list, Comparator<T> comp) {
        if (comp == null) {
            throw new IllegalArgumentException(COMPARATOR_NOT_NULL_MSG);
        }
        List<T> copy = copy(list);
        copy.sort(comp);
        return copy;
    }


    /**
     * 对List按照指定比较器进行排序
     * T为基本数据类型、基本数据类型包装类或String，Comparator可传空，否则不可传空
     * @param list
     * @param comp
     * @param <T>
     */
    public static <T> void doSort(List<T> list, Comparator<T> comp) {
        if (comp == null) {
            throw new IllegalArgumentException(COMPARATOR_NOT_NULL_MSG);
        }
        list.sort(comp);
    }


    /**
     * 过滤List的重复元素,返回新的List,不改变原List
     * @param list
     * @param <T>
     * @return list
     */
    public static <T> List<T> distinct(List<T> list) {
        List<T> list2 = copy(list);
        return list2.stream().distinct().collect(Collectors.toList());
    }


    /**
     * 按照指定lambda表达式copy后的值对List进行元素过滤,不改变原List
     * @param list
     * @param hashFn  自定义lambda表达式 copy后的值为Integer类型
     * @param <T>
     * @return   如果输入参数为空，则返回一个size=0的ArrayList
     */
    public static <T> List<T> distinct(List<T> list, Function<T, Integer> hashFn) {
        if(list == null){
            return new ArrayList<>();
        }
        if(hashFn == null){
            throw new IllegalArgumentException(FN_NOT_NULL_MSG);
        }
        Set<Integer> set = new HashSet<>(list.size());
                List<T> list2= new ArrayList<>(list.size());
                list.forEach(x ->{
                    int hash = hashFn.apply(x);
                    if (!set.contains(hash)) {
                        list2.add(x);
                set.add(hash);
            }
        });
        return list2;
    }
    
    /**
     * 构建一个Map对象， 对collection的每一个元素执行toKeyFn和toValueFn，生成的两个值作为map的一对key、value<br>
     * 如果生成重复的key，以迭代器遍历collection时同key的最后一个元素的为准
     * @param collection
     * @param toKeyFn 不能为空，否则抛出IllegalArgumentException
     * @param toValueFn 不能为空，否则抛出IllegalArgumentException
     * @return 
     */
    public static <T, K, V> Map<K, V> toMap(Collection<T> collection, Function<T, K> toKeyFn, Function<T, V> toValueFn) {
		if (collection == null) {
			return new HashMap<>();
		}
		if (toKeyFn == null) {
			throw new IllegalArgumentException(FN_NOT_NULL_MSG);
		}
		if (toValueFn == null) {
			throw new IllegalArgumentException(FN_NOT_NULL_MSG);
		}
		Map<K,V> map = new HashMap<>(collection.size());
		for (T x : collection) {
			map.put(toKeyFn.apply(x), toValueFn.apply(x));
		}
		return map;
	}

    public static <T extends Comparable> T max(Collection<T> collection) {

        if (collection == null || collection.isEmpty()) {
            return null;
        }
        T temp = (T) Collections.max(collection);

        return temp;
    }

    /**
     *
     * @param collection
     * @param ensure 与集合中元素一起参加max运算，可以看做下限保证
     * @param <T>
     * @return
     */
    public static <T extends Comparable> T max(Collection<T> collection, T ensure) {
        if (ensure == null) {
            throw new  IllegalArgumentException(ENSURE_NOT_NULL_MSG);
        }
        if (collection == null || collection.isEmpty()) {
            return ensure;
        }
        T temp = (T)Collections.max(collection);
        if (ensure.compareTo(temp) > 0) {
            temp = ensure;
        }
        return temp;
    }

    public static <T> T max(Collection<T> collection, Comparator<T> comparator) {
        if (comparator == null) {
            throw new IllegalArgumentException(COMPARATOR_NOT_NULL_MSG);
        }
        if (collection == null || collection.isEmpty()) {
            return null;
        }
        T temp = Collections.max(collection, comparator);

        return temp;
    }

    /**
     *
     *
     * 使用示例：<br>
     *<br>
     *     class P1{<br>
     *         public int a;<br>
     *         public String b;<br>
     *     }<br>
     *<br>
     *     List<P1> listp1;<br>
     *<br>
     *<br>
     *     max(listp1, new P1(10, "xx"), (x, y) -> Integer.compare(x.a, y.a))<br>
     *     <br>
     * @param collection
     * @param ensure 与集合中元素一起参加max运算，可以看做下限保证
     * @param comparator 比较函数
     * @param <T>
     * @return
     */
    public static <T> T max(Collection<T> collection, T ensure, Comparator<T> comparator) {
        if (comparator == null) {
            throw new IllegalArgumentException(COMPARATOR_NOT_NULL_MSG);
        }
        if (ensure == null) {
            throw new  IllegalArgumentException(ENSURE_NOT_NULL_MSG);
        }
        if (collection == null || collection.isEmpty()) {
            return ensure;
        }
        T temp = Collections.max(collection, comparator);
        if (comparator.compare(ensure, temp) > 0) {
            temp = ensure;
        }
        return temp;
    }

    public static <T extends Comparable> T min(Collection<T> collection) {

        if (collection == null || collection.isEmpty()) {
            return null;
        }
        T temp = (T)Collections.min(collection);

        return temp;
    }

    /**
     *
     * @param collection
     * @param ensure 与集合中元素一起参加min运算，可以看做上限保证
     * @param <T>
     * @return
     */
    public static <T extends Comparable> T min(Collection<T> collection, T ensure) {
        if (ensure == null) {
            throw new  IllegalArgumentException(ENSURE_NOT_NULL_MSG);
        }
        if (collection == null || collection.isEmpty()) {
            return ensure;
        }
        T temp = (T)Collections.min(collection);
        if (ensure.compareTo(temp) < 0) {
            temp = ensure;
        }
        return temp;
    }

    public static <T> T min(Collection<T> collection, Comparator<T> comparator) {
        if (comparator == null) {
            throw new IllegalArgumentException(COMPARATOR_NOT_NULL_MSG);
        }
        if (collection == null || collection.isEmpty()) {
            return null;
        }

        T temp = Collections.min(collection, comparator);

        return temp;
    }

    /**
     *
     * @param collection 可以null和empty
     * @param ensure 与集合中元素一起参加min运算，可以看做上限保证
     * @param comparator
     * @param <T>
     * @return
     */
    public static <T> T min(Collection<T> collection, T ensure, Comparator<T> comparator) {
        if (comparator == null) {
            throw new IllegalArgumentException(COMPARATOR_NOT_NULL_MSG);
        }
        if (ensure == null) {
            throw new IllegalArgumentException(ENSURE_NOT_NULL_MSG);
        }
        if (collection == null || collection.isEmpty()) {
            return ensure;
        }
        T temp = Collections.min(collection, comparator);
        if (comparator.compare(ensure, temp) < 0) {
            temp = ensure;
        }
        return temp;
    }

}
