/**
 * Represents a list of Nodes. 
 */
public class LinkedList {

    private Node first;
    private Node last;
    private int size;

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
            System.out.println("Error: index must be between 0 and size");
            return null;
        }
        Node current = first;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        return current;
    }

    public void add(int index, MemoryBlock block) {
        if (index < 0 || index > size) {
            System.out.println("Error: index must be between 0 and size");
            return;
        }
        Node newNode = new Node(block);
        if (index == 0) {
            newNode.next = first;
            first = newNode;
            if (size == 0) {
                last = newNode;
            }
        } else if (index == size) {
            last.next = newNode;
            last = newNode;
        } else {
            Node prevNode = getNode(index - 1);
            if (prevNode != null) {
                newNode.next = prevNode.next;
                prevNode.next = newNode;
            }
        }
        size++;
    }

    public void addFirst(MemoryBlock block) {
        add(0, block);
    }

    public void addLast(MemoryBlock block) {
        add(size, block);
    }

    public MemoryBlock getBlock(int index) {
        Node node = getNode(index);
        if (node == null) {
            System.out.println("Error: Cannot retrieve block at index " + index);
            return null;
        }
        return node.block;
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
        System.out.println("Error: Block not found in the list");
        return -1;
    }

    public void remove(Node node) {
        if (node == null) {
            System.out.println("Error: Cannot remove a null node");
            return;
        }
        if (first == null) {
            System.out.println("Error: List is empty");
            return;
        }
        if (node == first) {
            first = first.next;
            if (first == null) {
                last = null;
            }
        } else {
            Node current = first;
            while (current.next != null && current.next != node) {
                current = current.next;
            }
            if (current.next == node) {
                current.next = node.next;
                if (node.next == null) {
                    last = current;
                }
            } else {
                System.out.println("Error: Node not found in the list");
            }
        }
        size--;
    }

    public void remove(MemoryBlock block) {
        if (block == null) {
            System.out.println("Error: Cannot remove a null block");
            return;
        }
        if (first == null) {
            System.out.println("Error: List is empty");
            return;
        }
        Node current = first;
        Node prev = null;
        while (current != null) {
            if (current.block.equals(block)) {
                if (prev == null) {
                    first = current.next;
                } else {
                    prev.next = current.next;
                }
                if (current.next == null) {
                    last = prev;
                }
                size--;
                return;
            }
            prev = current;
            current = current.next;
        }
        System.out.println("Error: Block not found in the list");
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        Node current = first;
        while (current != null) {
            result.append(current.block.toString());
            if (current.next != null) {
                result.append(" -> ");
            }
            current = current.next;
        }
        return result.toString();
    }
}


