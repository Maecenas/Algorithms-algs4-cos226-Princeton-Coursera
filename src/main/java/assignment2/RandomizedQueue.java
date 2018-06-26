package assignment2;

import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

import java.util.Iterator;
import java.util.NoSuchElementException;


public class RandomizedQueue<Item> implements Iterable<Item> {

    private Item[] items;
    private int size;

    public RandomizedQueue() {
        this.size = 0;
        this.items = (Item[]) new Object[1];
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    public void enqueue(Item item) {
        validate(item);
        if (size == items.length) {
            resize(items.length << 1);
        }
        items[size++] = item;
    }

    public Item dequeue() {
        validate();
        int random = StdRandom.uniform(size);
        Item item = items[random];
        items[random] = items[--size];
        items[size] = null;
        if (size > 0 && size == items.length >> 2) {
            resize(items.length >> 1);
        }
        return item;
    }

    public Item sample() {
        validate();
        return items[StdRandom.uniform(size)];
    }

    public Iterator<Item> iterator() {
        return new RandomizedQueueIterator();
    }

    private class RandomizedQueueIterator implements Iterator<Item> {
        private int[] randomIndex;
        private int current;

        public RandomizedQueueIterator() {
            randomIndex = new int[size];
            for (int i = 0; i < size; i++) {
                randomIndex[i] = i;
            }
            StdRandom.shuffle(randomIndex);
            current = 0;
        }

        public boolean hasNext() {
            return current != randomIndex.length;
        }

        public Item next() {
            if (!hasNext()) throw new NoSuchElementException();
            return items[randomIndex[current++]];
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    private void resize(int capacity) {
        assert capacity >= size;

        Item[] tmpArr = (Item[]) new Object[capacity];
        System.arraycopy(items, 0, tmpArr, 0, size);
        items = tmpArr;
    }

    private void validate() {
        if (isEmpty()) throw new NoSuchElementException();
    }

    private void validate(Item item) {
        if (null == item) throw new IllegalArgumentException();
    }

    public static void main(String[] args) {
        RandomizedQueue<Integer> rq = new RandomizedQueue<>();
        for (int i = 0; i < 10; ++i)
            rq.enqueue(i);
        Iterator<Integer> it = rq.iterator();
        while (it.hasNext()) StdOut.print(it.next() + " ");
        StdOut.println(" size: " + rq.size());
        it = rq.iterator();
        while (it.hasNext()) StdOut.print(it.next() + " ");
        StdOut.println(" size: " + rq.size());

        for (int j = 0; j < 3; ++j) {
            for (int i = 0; i < 3; ++i)
                rq.dequeue();
            it = rq.iterator();
            while (it.hasNext()) StdOut.print(it.next() + " ");
            StdOut.println(" size: " + rq.size());
        }
    }
}
