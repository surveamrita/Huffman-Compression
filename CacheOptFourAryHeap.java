import java.util.NoSuchElementException;

public class CacheOptFourAryHeap implements PriorityQueue  {
	private Node[] heap;
	
	private int size = 0;
		public CacheOptFourAryHeap(int size) {
		heap = new Node[size+3];
	}

	public int getSize() {
		return size;
	}

	public boolean isEmpty() {
		return size == 0;
	}

	public boolean isFull() {
		return size == heap.length;
	}

	private int parent(int i) {
		return (i/4)+2;
	}

	private int kthChild(int i, int k) {
		return (4*i) + k -9;
	}

	public void insert(Node element) {
		if (!isFull()) {
			heap[size+3] = element;
			size++;
			minHeapifyUp(size+2);
		}
	}
	private void minHeapifyUp(int childInd) {
		Node tmp = heap[childInd];
		while (childInd > 3 && tmp.getFreq() < heap[parent(childInd)].getFreq()) {
			heap[childInd] = heap[parent(childInd)];
			childInd = parent(childInd);
		}
		heap[childInd] = tmp;
	}
	public Node getMin() {
		if (isEmpty())
            throw new NoSuchElementException("Underflow Exception");           
        return heap[3];
	}

	public Node extractMin() {
		if (!isEmpty()) {
			Node removed = heap[3];
			heap[3] = heap[size + 2];
			size--;
			minHeapifyDown(3);

			return removed;
		}
		return null;
	}

	private void minHeapifyDown(int ind) {
		int child;
		Node tmp = heap[ind];
		while (kthChild(ind, 1) < size+3) {
			child = minChild(ind);
			if (heap[child].getFreq() < tmp.getFreq())
				heap[ind] = heap[child];
			else
				break;
			ind = child;
		}
		heap[ind] = tmp;
	}

	private int minChild(int ind) {
		int bestChild = kthChild(ind, 1);
		int k = 2;
		int pos = kthChild(ind, k);
		while ((k <= 4) && (pos < size+3)) {
			if (heap[pos].getFreq() < heap[bestChild].getFreq())
				bestChild = pos;
			pos = kthChild(ind, ++k);
		}
		return bestChild;
	}
}
