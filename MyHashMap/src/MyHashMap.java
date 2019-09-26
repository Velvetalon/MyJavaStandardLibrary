import org.junit.jupiter.api.Test;

import java.lang.reflect.Array;
import java.security.MessageDigest;
import java.util.*;
import java.util.function.Consumer;

/**
 * HashMap实现。
 *
 * @author : OrsPced
 * @see java.util.Map
 */
public class MyHashMap<K, V> implements Map<K, V> {

    //内部类：Node
    private class Node {
        private MyEntry<K, V> entry = null;
        public Node next = null;

        Node( MyEntry<K, V> entry ){
            this.entry = entry;
        }

        public MyEntry<K, V> getEntry(){
            return entry;
        }

        public void setEntry( MyEntry<K, V> entry ){
            this.entry = entry;
        }
    }

    //实现内部类：MyEntry
    class MyEntry<K, V> implements Entry<K, V> {
        private int hash;
        private K key;
        private V value;

        MyEntry( K key, V value ){
            this.key = key;
            this.value = value;
            this.hash = key.hashCode();
        }

        public int getHash(){
            return hash;
        }

        @Override
        public K getKey(){
            return key;
        }

        @Override
        public V getValue(){
            return value;
        }

        @Override
        public V setValue( V value ){
            return null;
        }
    }

    //常量区
    private static final double LOAD_FACTOR = 0.75;       //负载因子阈值
    private static final int INITIAL_SIZE = 16;           //数组初始大小

    //成员变量区
    private int element_count = 0;        //当前元素计数
    private Node[] node_list = (Node[]) Array.newInstance(Node.class, INITIAL_SIZE);       //存储数组。

    MyHashMap(){
//        try {
//            md5 = MessageDigest.getInstance("MD5");
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public int size(){
        return element_count;
    }

    @Override
    public boolean isEmpty(){
        return element_count == 0;
    }

    @Override
    public boolean containsKey( Object key ){
        return get(key) != null;
    }

    @Override
    public boolean containsValue( Object value ){
        //遍历哈希表查找值
        for (Entry<K, V> entry : entrySet()) {
            V temp_value = entry.getValue();
            if (temp_value != null && temp_value.equals(value)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 从Map中查找目标Key。
     * @param key
     * @return
     */
    @Override
    public V get( Object key ){
        int index = getIndex(key.hashCode(), node_list.length);
        //目标位置为空则直接返回null
        if (node_list[index] == null || node_list[index].next == null) {
            return null;
        }

        //目标位置不为空则遍历链表，查找相同的key
        Node temp = node_list[index].next;
        while (temp != null) {
            if (temp.getEntry().getHash() == key.hashCode()) {
                return temp.getEntry().getValue();
            }
            temp = temp.next;
        }
        return null;
    }

    @Override
    public V put( K key, V value ){
        put(new MyEntry<>(key, value), node_list, true);
        return value;
    }

    /**
     * 如果目标位置为空，则创建节点并保存目标位置
     * 否则在列表中查找并替换重复项。
     * 如果没有重复项，则插入链表尾部。
     *
     * @param node   : 被加入数组的节点。
     * @param target : 目标数组。
     */
    private void put( Node node, Node[] target, boolean check ){
        int index = getIndex(node.getEntry().getHash(), node_list.length);
        if (target[index] == null) {
            target[index] = new Node(null);
        }
        if (target[index].next == null) {
            target[index].next = node;
            if (check) {
                //检查哈希表大小
                ++element_count;
                checkLoadFactor();
            }
            return;
        }

        Node temp = target[index].next;
        while (temp != null) {
            if (temp.getEntry().getHash() == node.getEntry().getHash()) {
                temp.setEntry(node.getEntry());
                return;
            }
            if (temp.next == null) {
                temp.next = node;
                temp.next.next = null;        //截断节点，防止出现循环引用
                if (check) {
                    //检查哈希表大小
                    ++element_count;
                    checkLoadFactor();
                }
            }
            temp = temp.next;
        }
    }

    private void put( MyEntry<K, V> entry, Node[] target, boolean check ){
        put(new Node(entry), target, check);
    }

    @Override
    public V remove( Object key ){
        if (key == null) {
            return null;
        }

        int index = getIndex(key.hashCode(), node_list.length);
        if (node_list[index] == null || node_list[index].next == null) {
            return null;
        }

        //在目标位置的链表中查找目标键值。
        Node last = node_list[index];
        Node current = node_list[index].next;
        while (current != null) {
            if (current.getEntry().getHash() == key.hashCode()) {
                last.next = current.next;
                --element_count;
                return current.getEntry().getValue();
            }
            last = last.next;
            current = current.next;
        }

        return null;
    }

    @Override
    public void putAll( Map<? extends K, ? extends V> m ){
        for (K k : m.keySet()) {
            put(k, m.get(k));
        }
    }

    @Override
    public void clear(){
        node_list = (Node[]) Array.newInstance(Node.class, INITIAL_SIZE);
        element_count = 0;
    }

    @Override
    public Set<K> keySet(){
        Set<K> set = new HashSet<>();
        traversing(node_list, (node -> {
            set.add(node.entry.getKey());
        }));
        return set;
    }

    @Override
    public Collection<V> values(){
        Collection<V> collection = new ArrayList<>();
        traversing(node_list, (node -> {
            collection.add(node.getEntry().getValue());
        }));
        return collection;
    }

    @Override
    public Set<Entry<K, V>> entrySet(){
        Set<Entry<K, V>> set = new HashSet<>();
        traversing(node_list, ( node ) -> {
            set.add(node.getEntry());
        });

        return set;
    }

    private void checkLoadFactor(){
        //检测数组大小。
        if (element_count >= Integer.MAX_VALUE * LOAD_FACTOR) {
            return;
        } else if (element_count >= Integer.MAX_VALUE) {
            throw new RuntimeException("哈希表已满");
        } else if (element_count >= node_list.length * LOAD_FACTOR) {
            resize();
        }
    }

/**
 * 列表扩容。
 */
private void resize(){
    //创建新列表
    Node[] new_list = (Node[]) Array.newInstance(Node.class, node_list.length << 1);
    traversing(node_list, (node -> {
        put(node, new_list, false);
    }));
    //移动完成后替换当前列表。
    node_list = new_list;
}

    //遍历list，并对其中的每一个元素执行指定的操作
    private void traversing( Node[] nl, Consumer<Node> con ){
        int head = 0, foot = nl.length - 1;
        Node node;
        while (head <= foot) {
            if (nl[head] != null && nl[head].next != null) {
                node = nl[head];
                while ((node = node.next) != null) {
                    con.accept(node);
                }
            }
            if (nl[foot] != null && nl[foot].next != null) {
                node = nl[foot];
                while ((node = node.next) != null) {
                    con.accept(node);
                }
            }
            ++head;
            --foot;
        }
    }

    private int getIndex( int hash, int mod ){
        return (hash & 0x7fffffff) & (mod - 1);
    }

    @Test
    public void test(){
        System.out.println(Integer.MIN_VALUE +
                        "的绝对值为：" +
                        Math.abs(Integer.MIN_VALUE));
    }
}
