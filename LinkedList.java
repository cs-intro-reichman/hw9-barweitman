/**
 * Represents a list of Nodes. 
 */
public class LinkedList {

    private Node first; // Pointer to the first element of this list
    private Node last;  // Pointer to the last element of this list
    private int size;   // Number of elements in this list

    public LinkedList() {
        first = null;
        last = null;
        size = 0;
    }

    public Node getFirst() {
        return first;
    }

    public Node getLast() {
        return last;
    }

    public int getSize() {
        return size;
    }

    public Node getNode(int index) {
        if (index < 0 || index >= size) {
            return null; // Return null for invalid index
        }
        Node current = first;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        return current;
    }

    public void add(int index, MemoryBlock block) {
        if (index < 0 || index > size) {
            return; // Do nothing for invalid index
        }
        Node newNode = new Node(block);
        if (index == 0) {
            newNode.next = first;
            first = newNode;
            if (size == 0) last = newNode;
        } else if (index == size) {
            last.next = newNode;
            last = newNode;
        } else {
            Node previous = getNode(index - 1);
            newNode.next = previous.next;
            previous.next = newNode;
        }
        size++;
    }

    public void addLast(MemoryBlock block) {
        add(size, block);
    }

    public void addFirst(MemoryBlock block) {
        add(0, block);
    }

    public MemoryBlock getBlock(int index) {
        Node node = getNode(index);
        return node != null ? node.block : null; // Return null if node is not found
    }

    public int indexOf(MemoryBlock block) {
        Node current = first;
        int index = 0;
        while (current != null) {
            if (current.block.equals(block)) {
                return index;
            }
            current = current.next;
            index++;
        }
        return -1; // Return -1 if not found
    }

    public void remove(Node node) {
        if (first == null || node == null) return; // Do nothing if the list is empty or node is null
        if (first == node) {
            first = first.next;
            if (first == null) last = null;
        } else {
            Node current = first;
            while (current.next != null && current.next != node) {
                current = current.next;
            }
            if (current.next == node) {
                current.next = current.next.next;
                if (current.next == null) last = current;
            }
        }
        size--;
    }

    public void remove(int index) {
        if (index < 0 || index >= size) return; // Do nothing for invalid index
        if (index == 0) {
            first = first.next;
            if (first == null) last = null;
        } else {
            Node previous = getNode(index - 1);
            Node toRemove = previous.next;
            previous.next = toRemove.next;
            if (toRemove == last) last = previous;
        }
        size--;
    }

    public void remove(MemoryBlock block) {
        Node current = first, previous = null;
        while (current != null) {
            if (current.block.equals(block)) {
                if (previous == null) {
                    first = current.next;
                    if (first == null) last = null;
                } else {
                    previous.next = current.next;
                    if (current == last) last = previous;
                }
                size--;
                return;
            }
            previous = current;
            current = current.next;
        }
    }

    public ListIterator iterator() {
        return new ListIterator(first);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        Node current = first;
        while (current != null) {
            sb.append(current.toString()).append(" -> ");
            current = current.next;
        }
        return sb.toString();
    }
}
