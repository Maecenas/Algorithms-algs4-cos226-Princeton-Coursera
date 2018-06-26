package assignment2;

import edu.princeton.cs.algs4.StdOut;

import java.util.Iterator;
import java.util.NoSuchElementException;


public class Deque<Item> implements Iterable<Item> {

    private final Node head;
    private int size;

    private class Node {

        private Item item;
        private Node prev;
        private Node next;

        Node(Item item) {
            this.item = item;
            this.prev = null;
            this.next = null;
        }

        Node(Item item, Node prev, Node next) {
            this.item = item;
            this.prev = prev;
            this.next = next;
        }

        void remove() {
            this.prev.next = this.next;
            this.next.prev = this.prev;
            this.nullify();
        }

        void nullify() {
            this.item = null;
            this.prev = null;
            this.next = null;
        }
    }

    public Deque() {
        head = new Node(null);
        head.next = head;
        head.prev = head;
        size = 0;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    public void addFirst(Item item) {
        validate(item);
        insert(item, head, head.next);
        ++size;
    }

    public void addLast(Item item) {
        validate(item);
        insert(item, head.prev, head);
        ++size;
    }

    public Item removeFirst() {
        validate();
        Item item = head.next.item;
        head.next.remove();
        --size;
        return item;
    }

    public Item removeLast() {
        validate();
        Item item = head.prev.item;
        head.prev.remove();
        --size;
        return item;
    }

    public Iterator<Item> iterator() {
        return new DequeIterator();
    }

    private class DequeIterator implements Iterator<Item> {

        private Node current = head.next;

        public boolean hasNext() {
            return current != head;
        }

        public Item next() {
            if (!hasNext()) throw new NoSuchElementException();
            current = current.next;
            return current.prev.item;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    private void insert(Item item, Node prev, Node next) {
        Node tmp = new Node(item, prev, next);
        prev.next = tmp;
        next.prev = tmp;
    }

    private void validate() {
        if (isEmpty()) throw new NoSuchElementException();
    }

    private void validate(Item item) {
        if (item == null) throw new IllegalArgumentException();
    }

    public static void main(String[] args) {
        Deque<Integer> deque = new Deque<Integer>();
        for (int i = 0; i < 10; i += 2) {
            deque.addFirst(i);
            deque.addLast(i + 1);
        }
        Iterator<Integer> it = deque.iterator();
        while (it.hasNext()) {
            StdOut.print(it.next() + " ");
        }
        StdOut.println("size: " + deque.size());

        for (int i = 0; i < 3; ++i) {
            deque.removeLast();
            deque.removeFirst();
        }
        StdOut.println("size: " + deque.size());

        for (int i = 0; i < 6; i += 2) {
            deque.addLast(i);
            deque.addFirst(i + 1);
        }
        it = deque.iterator();
        while (it.hasNext()) StdOut.print(it.next() + " ");
        StdOut.println(" size: " + deque.size());
    }
}


///**
// * Deque implemented with Resizable Array
// */

//public class Deque<Item> implements Iterable<Item> {

//    private static final int MIN_ARRAY_SIZE = 2;
//    private Item[] items;
//    private int num, first, last;

//    public Deque() {
//        items = (Item[]) new Object[MIN_ARRAY_SIZE];
//        num = 0;
//        first = 0;
//        last = 1;
//    }

//    public boolean isEmpty() {
//        return num == 0;
//    }

//    public int size() {
//        return num;
//    }

//    public void addFirst(Item item) {
//        if (item == null) {
//            throw new IllegalArgumentException();
//        }
//        if (first < 0) {
//            resize(items.length + num, Side.FRONT);
//        }
//        items[first--] = item;
//        num++;
//    }

//    public void addLast(Item item) {
//        if (item == null) {
//            throw new IllegalArgumentException();
//        }
//        if (last == items.length) {
//            resize(items.length + num, Side.END);
//        }
//        items[last++] = item;
//        num++;
//    }

//    public Item removeFirst() {
//        if (isEmpty()) {
//            throw new NoSuchElementException();
//        }
//        Item item = items[++first];
//        items[first] = null;
//        num--;
//        if (num > 0 && first + items.length - last >= num << 2) {
//            resize(num << 1, Side.BOTH);
//        }
//        return item;
//    }

//    public Item removeLast() {
//        if (isEmpty()) {
//            throw new NoSuchElementException();
//        }
//        Item item = items[--last];
//        items[last] = null;
//        num--;
//        if (num > 0 && first + items.length - last >= num << 2) {
//            resize(num << 1, Side.BOTH);
//        }
//        return item;
//    }

//    public Iterator<Item> iterator() {
//        return new DequeIterator();
//    }

//    private class DequeIterator implements Iterator<Item> {
//        private int cursor = first;

//        @Override
//        public boolean hasNext() {
//            return cursor != last - 1;
//        }

//        @Override
//        public Item next() {
//            if (!hasNext()) {
//                throw new NoSuchElementException();
//            }
//            return items[++cursor];
//        }

//        @Override
//        public void remove() {
//            throw new UnsupportedOperationException();
//        }
//    }

//    private void resize(int capacity, Side side) {
//        if (capacity < num) {
//            return;
//        }
//        Item[] tmpArr = (Item[]) new Object[capacity];
//        int destinationPosition = 0;
//        int length = last;
//        int start = 0;
//        switch (side) {
//            case BOTH:
//                start = first + 1;
//                destinationPosition = (capacity - num) >> 1;
//                first = destinationPosition - 1;
//                length = num;
//                last = destinationPosition + length;
//                break;
//            case FRONT:
//                if (capacity < items.length) {
//                    start = first;
//                    int difference = items.length - capacity;
//                    first -= difference;
//                    last -= difference;
//                    destinationPosition = first;
//                    length = num + 1;
//                } else {
//                    first = num - 1;
//                    last += num;
//                    length = num;
//                    destinationPosition = num;
//                }
//                break;
//            default:
//                // no actions required if side is END
//                break;
//        }

//    private void resize(int capacity) {
//        assert capacity >= size;

//        Item[] tmpArr = (Item[]) new Object[capacity];
//        System.arraycopy(items, start, tmpArr, destinationPosition, length);
//        items = tmpArr;
//    }

//    private enum Side {
//        FRONT,
//        END,
//        BOTH,
//    }

//    public static void main(String[] args) {
//        // unit testing (optional)
//    }
//}
