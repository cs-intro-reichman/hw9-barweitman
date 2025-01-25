/**
 * Represents a managed memory space. The memory space manages a list of allocated 
 * memory blocks, and a list free memory blocks. The methods "malloc" and "free" are 
 * used, respectively, for creating new blocks and recycling existing blocks.
 */
public class MemorySpace {
	
	// A list of the memory blocks that are presently allocated
	private LinkedList allocatedList;

	// A list of memory blocks that are presently free
	private LinkedList freeList;

	/**
	 * Constructs a new managed memory space of a given maximal size.
	 * 
	 * @param maxSize
	 *            the size of the memory space to be managed
	 */
	public MemorySpace(int maxSize) {
		// initiallizes an empty list of allocated blocks.
		allocatedList = new LinkedList();
	    // Initializes a free list containing a single block which represents
	    // the entire memory. The base address of this single initial block is
	    // zero, and its length is the given memory size.
		freeList = new LinkedList();
		freeList.addLast(new MemoryBlock(0, maxSize));
	}

	/**
	 * Allocates a memory block of a requested length (in words). Returns the
	 * base address of the allocated block, or -1 if unable to allocate.
	 * 
	 * This implementation scans the freeList, looking for the first free memory block 
	 * whose length equals at least the given length. If such a block is found, the method 
	 * performs the following operations:
	 * 
	 * (1) A new memory block is constructed. The base address of the new block is set to
	 * the base address of the found free block. The length of the new block is set to the value 
	 * of the method's length parameter.
	 * 
	 * (2) The new memory block is appended to the end of the allocatedList.
	 * 
	 * (3) The base address and the length of the found free block are updated, to reflect the allocation.
	 * For example, suppose that the requested block length is 17, and suppose that the base
	 * address and length of the the found free block are 250 and 20, respectively.
	 * In such a case, the base address and length of of the allocated block
	 * are set to 250 and 17, respectively, and the base address and length
	 * of the found free block are set to 267 and 3, respectively.
	 * 
	 * (4) The new memory block is returned.
	 * 
	 * If the length of the found block is exactly the same as the requested length, 
	 * then the found block is removed from the freeList and appended to the allocatedList.
	 * 
	 * @param length
	 *        the length (in words) of the memory block that has to be allocated
	 * @return the base address of the allocated block, or -1 if unable to allocate
	 */
	
	 public int malloc(int length) {
        Node current = freeList.getFirst();

        while (current != null) {
            MemoryBlock freeBlock = current.block;

            // Check if the free block is large enough
            if (freeBlock.length >= length) {
                int baseAddress = freeBlock.baseAddress;

                // Create a new allocated block
                MemoryBlock allocatedBlock = new MemoryBlock(baseAddress, length);
                allocatedList.addLast(allocatedBlock);

                // Adjust the free block
                freeBlock.baseAddress += length;
                freeBlock.length -= length;

                // If the free block is now empty, remove it from the free list
                if (freeBlock.length == 0) {
                    freeList.remove(current);
                }

                return baseAddress;
            }

            current = current.next;
        }

        // If no suitable block is found, return -1
        return -1;
    }

    /**
     * Frees the memory block whose base address equals the given address.
     * The block is removed from the allocated list and added back to the free list.
     * 
     * @param address the starting address of the block to free
     */
    public void free(int address) {
        Node current = allocatedList.getFirst();

        while (current != null) {
            MemoryBlock allocatedBlock = current.block;

            // Check if this is the block to free
            if (allocatedBlock.baseAddress == address) {
                // Remove the block from the allocated list
                allocatedList.remove(current);

                // Add the block back to the free list
                MemoryBlock freedBlock = new MemoryBlock(address, allocatedBlock.length);
                freeList.addLast(freedBlock);

                // Defragment the free list to merge adjacent blocks
                defrag();
                return;
            }

            current = current.next;
        }

        // If no block is found at the given address, print an error message
        System.out.println("Error: Invalid address to free");
    }

    /**
     * Performs defragmentation of the memory space by merging adjacent free blocks.
     */
    public void defrag() {
        if (freeList.getSize() <= 1) {
            return; // Nothing to defragment
        }

        // Sort the free list by base address
        freeList = sortFreeListByBaseAddress();

        Node current = freeList.getFirst();

        while (current != null && current.next != null) {
            MemoryBlock currentBlock = current.block;
            MemoryBlock nextBlock = current.next.block;

            // Check if the blocks are contiguous
            if (currentBlock.baseAddress + currentBlock.length == nextBlock.baseAddress) {
                // Merge the blocks
                currentBlock.length += nextBlock.length;

                // Remove the next block from the free list
                freeList.remove(current.next);
            } else {
                current = current.next;
            }
        }
    }

    /**
     * Sorts the free list by the base address of the memory blocks.
     * 
     * @return a new sorted free list
     */
    private LinkedList sortFreeListByBaseAddress() {
        LinkedList sortedList = new LinkedList();
        Node currentNode = freeList.getFirst();

        while (currentNode != null) {
            MemoryBlock currentBlock = currentNode.block;

            Node sortedNode = sortedList.getFirst();
            int index = 0;

            while (sortedNode != null && currentBlock.baseAddress >= sortedNode.block.baseAddress) {
                sortedNode = sortedNode.next;
                index++;
            }

            sortedList.add(index, currentBlock);
            currentNode = currentNode.next;
        }

        return sortedList;
    }

    /**
     * A textual representation of the memory space, for debugging purposes.
     */
    @Override
    public String toString() {
        return "Free List:\n" + freeList.toString() + "\nAllocated List:\n" + allocatedList.toString();
    }
}