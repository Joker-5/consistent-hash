package com.joker.consistenthash;


import java.util.Collection;
import java.util.SortedMap;
import java.util.TreeMap;

public class ConsistentHash<T> {
    // 每个节点对应的虚拟节点数量
    private final int replicas;
    // 存储映射关系
    private final SortedMap<Integer, T> circle = new TreeMap<>();

    public ConsistentHash(int replicas, Collection<T> nodes) {
        this.replicas = replicas;
        for (T node : nodes) {
            add(node);
        }
    }

    public void add(T node) {
        for (int i = 0; i < replicas; i++) {
            String nd = getNodeName(node, i);
            circle.put(nd.hashCode(), node);
        }
    }

    public void remove(T node) {
        for (int i = 0; i < replicas; i++) {
            circle.remove(getNodeName(node, i).hashCode());
        }
    }

    // 获取虚拟节点对应的真实节点
    public T get(Object key) {
        if (circle.isEmpty()) {
            return null;
        }
        int hash = key.hashCode();
        if (!circle.containsKey(hash)) {
            // 顺时针找遇到的第一个节点
            SortedMap<Integer, T> tailMap = circle.tailMap(hash);
            hash = tailMap.isEmpty() ? circle.firstKey() : tailMap.firstKey();
        }
        return circle.get(hash);
    }

    public int size() {
        return circle.size();
    }

    private String getNodeName(T node, int index) {
        return node.toString() + "#" + index;
    }
}
