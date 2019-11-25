package edu.tamu.wumrwds.commons;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Heap<T extends Comparable<T>> {

    private List<T> heap;

    public Heap(List<T> elements) {
        heap = new ArrayList<>(elements.size());
        for (int i = 0; i < elements.size(); i++) {
            heap.add(elements.get(i));
        }

        constructHeap();
    }


    public static void main(String[] args) {
        Heap heap = new Heap(Arrays.asList(3, 7, 9, 8, 10, 2, 14, 1, 16, 4));
        System.out.println(heap);

        heap.insert(6);
        heap.insert(1);
        heap.insert(23);
        heap.insert(4);
        heap.insert(17);
        heap.insert(7);
        heap.insert(5);
        heap.insert(11);
        heap.insert(0);
        heap.insert(32);
        heap.insert(131);

        while (!heap.isEmpty()) {
            System.out.println(heap.maximum());
            heap.delete();
        }
    }





    public int size() {
        return heap.size();
    }

    public boolean isEmpty() {
        return heap.size() == 0;
    }

    public T maximum() {
        return heap.get(0);
    }

    public void insert(T element) {
        heap.add(element);
        int i = size() - 1;

        while (heap.get(parent(i)).compareTo(heap.get(i)) < 0 && i > 0) {
            swap(parent(i), i);
            i = parent(i);
        }
    }

    public void delete() {
        // swap the tail with the head, then delete the tail
        swap(0, size() - 1);
        heap.remove(size() - 1);

        maxHeapify(0);
    }








    private void constructHeap() {
        for (int i = size() / 2; i >= 0; i--) {
            maxHeapify(i);
        }
    }

    private void maxHeapify(int i) {
        int l = left(i);
        int r = right(i);

        int largest;
        if (l < size() && heap.get(l).compareTo(heap.get(i)) > 0)  {
            largest = l;
        }
        else {
            largest = i;
        }

        if (r < size() && heap.get(r).compareTo(heap.get(largest)) > 0) {
            largest = r;
        }

        if (largest != i) {
            swap(i, largest);
            maxHeapify(largest);
        }
    }

    private void swap(int i, int j) {
        T tmp = heap.get(i);
        heap.set(i, heap.get(j));
        heap.set(j, tmp);
    }

    private static int parent(int i) {
        return i / 2;
    }

    private static int left(int i) {
        return 2 * i;
    }

    private static int right(int i) {
        return 2 * i + 1;
    }
}
