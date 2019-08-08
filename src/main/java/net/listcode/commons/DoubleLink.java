package net.listcode.commons;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * 双向链表
 *
 * @author leo
 * @param <T>
 */

public class DoubleLink<T> {

    /** Node内部类 **/
    public static class Node<TT> {
        /**
         * 节点值
         */
        @Getter
        @Setter
        private TT vlaue;
        private Node<TT> prev;// 前驱
        private Node<TT> next;// 后继

        public Node(TT value) {
            this.vlaue = value;
            this.prev = null;
            this.next = null;
        }

        public synchronized void insertAfter(Node<TT> after) {
            if (after == null) {
                throw new IllegalArgumentException("cant be null!!");
            }
            after.next = this.next;
            if (this.next != null) {
                this.next.prev = after;
            }
            this.next = after;
            after.prev = this;
        }

        public synchronized void insertBefore(Node<TT> before) {
            if (before == null) {
                throw new IllegalArgumentException("cant be null!!");
            }
            before.prev = this.prev;
            if (this.prev != null) {
                this.prev.next = before;
            }
            this.prev = before;
            before.next = this;
        }

        public synchronized boolean isHead() {
            return this.prev == null;
        }

        public synchronized boolean isTail() {
            return this.next == null;
        }

        public synchronized boolean hasPre() {
            return !this.isHead();
        }

        public synchronized boolean hasNext() {
            return !this.isTail();
        }

        public synchronized Node<TT> getNext() {
            return this.next;
        }

        public synchronized Node<TT> getPre() {
            return this.prev;
        }

        public synchronized void delNext() {
            Node<TT> nextNode = this.next;
            if (nextNode == null) {
                return;
            } else {
                this.next = nextNode.next;
                if (nextNode.next != null) {
                    nextNode.next.prev = this;
                }
            }
        }

        public synchronized void delPre() {
            Node<TT> preNode = this.prev;
            if (preNode == null) {
                return;
            } else {
                this.prev = preNode.prev;
                if (preNode.prev != null) {
                    preNode.prev.next = this;
                }
            }
        }
    }

    /**
     * 链表长度
     */
    private int size;
    /** 头节点 */
    private Node<T> head;
    /** 尾节点 */
    private Node<T> tail;

    public DoubleLink() {
        initEmpty();
    }

    private void initEmpty() {
        head = null;
        tail = null;
        size = 0;
    }

    private void initWithFirst(Node<T> node) {
        head = node;
        tail = node;
        size = 1;
    }

    public synchronized int size() {
        return this.size;
    }

    /**
     * 判断链表的长度是否为空
     */
    public synchronized boolean isEmpty() {
        return size == 0;
    }

    public synchronized Node<T> addFirst(T v) {
        Node<T> node = new Node<>(v);
        if (this.head == null) {
            initWithFirst(node);
        } else {
            this.head.insertBefore(node);
            this.head = node;
            this.size++;
        }
        return node;
    }

    public synchronized Node<T> addLast(T v) {
        Node<T> node = new Node<>(v);
        if (this.tail == null) {
            initWithFirst(node);
        } else {
            this.tail.next = node;
            node.prev = this.tail;
            this.size++;
            this.tail = node;
        }
        return node;
    }

    /**
     *
     * 添加到指定节点前面，使用这个方法时，必须保证node在链表内，效率高，避免了遍历检查
     *
     * @param node
     * @param v
     */
    public synchronized void addBefore(Node<T>node, T v) {
//		int index = -1;
//		Node<T> current = this.getFirst();
//		for (int i=0; i < this.size; i++) {
//			if (current == node) {
//				current
//				break;
//			}
//		}
//		if (index == -1) {
//
//		}
//		this.insert(index, v);
        if (node == this.head) {
            this.addFirst(v);
        } else {
            node.insertBefore(new Node<>(v));
            this.size ++;
        }
    }

    /**
     *
     * 添加到指定节点后面，使用这个方法时，必须保证node在链表内，效率高，避免了遍历检查
     *
     * @param node
     * @param v
     */
    public synchronized void addAfter(Node<T>node, T v) {
        if (this.tail == node) {
            this.addLast(v);
        } else {
            node.insertAfter(new Node<>(v));
            this.size ++;
        }
    }

    public synchronized Node<T> insert(int index, T v) {
        if (index == 0) {
            return this.addFirst(v);
        }
        if (index == size) {
            return this.addLast(v);
        }
        if (index > size) {
            throw new IndexOutOfBoundsException();
        }
        Node<T> node = new Node<>(v);
        Node<T> pre = this.get(index - 1);
        pre.insertAfter(node);
        this.size++;
        return node;
    }

    /**
     * 获取第一个节点的值
     */
    public synchronized Node<T> getFirst() {
        return this.head;
    }

    /**
     * 获取最后一个节点的值
     */
    public synchronized Node<T> getLast() {
        return this.tail;
    }

    public synchronized Node<T> get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        Node<T> cur = this.head;
        for (int i = 0; i < index; i++) {
            cur = cur.next;
        }
        return cur;
    }

    /**
     * 删除节点的方法
     */
    public synchronized void del(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        if (index == 0) {
            delFirst();
        }

        if (index == size - 1) {
            delLast();
        }

        Node<T> cur = this.head;
        for (int i = 0; i < index; i++) {
            cur = cur.next;
        }
        Node<T> temp = cur.prev;
        temp.next = cur.next;
        cur.next.prev = temp;
        this.size--;
    }

    /**
     * 删除指定节点，使用这个方法时，必须保证node在链表内，效率高，避免了遍历检查
     * @param node
     */
    public synchronized void delWithoutCheck(Node<T> node) {
        if (node == this.head) {
            this.delFirst();
            return;
        } else if (node == this.tail) {
            this.delLast();
            return;
        } else {
            node.getPre().delNext();
            this.size--;
        }
    }

    /**
     * 删除第一个节点
     */
    public synchronized void delFirst() {
        if (this.head == null) {
            return;
        }
        if (this.size == 1) {
            this.initEmpty();
            return;
        }
        // 把第二个变成第一个
        Node<T> newHead = this.get(1);
        this.head = newHead;
        newHead.delPre();
        this.size--;
    }

    /**
     * 删除最后一个节点
     */
    public synchronized void delLast() {
        if (this.tail == null) {
            return;
        }
        if (this.size == 1) {
            this.initEmpty();
            return;
        }
        // 把倒数第二个变成倒数第一个
        Node<T> newtail = this.tail.prev;
        this.tail = newtail;
        newtail.delNext();
        this.size--;
    }

    public synchronized List<T> toList() {
        if (this.size == 0) {
            return new ArrayList<>();
        }
        List<T> list = new ArrayList<>(this.size);
        Node<T> node = this.getFirst();
        list.add(node.getVlaue());
        while (node.hasNext()) {
            node = node.getNext();
            list.add(node.getVlaue());
        }
        return list;
    }
}