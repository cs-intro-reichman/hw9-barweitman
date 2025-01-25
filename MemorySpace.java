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
	
	 /**
 * Represents a managed memory space. The memory space manages a list of allocated
 * memory blocks and a list of free memory blocks. The methods "malloc" and "free"
 * are used for creating new blocks and recycling existing blocks, respectively.
 */
    public int malloc(int length) {
        Node current = freeList.getFirst();

        while (current != null) {
            MemoryBlock freeBlock = current.block;

            if (freeBlock.length >= length) {
                int baseAddress = freeBlock.baseAddress;

                if (freeBlock.length == length) {
                    freeList.remove(current); // Exact fit, remove the free block
                } else {
                    freeBlock.baseAddress += length; // Update the free block's base address
                    freeBlock.length -= length;      // Update the free block's length
                }

                allocatedList.addLast(new MemoryBlock(baseAddress, length)); // Add to allocated list
                System.out.println("Successfully allocated memory block at address " + baseAddress);
                return baseAddress;
            }

            current = current.next;
        }

        System.out.println("Error: Not enough memory to allocate " + length + " words");
        return -1; // Allocation failed
    }

    /**
     * Frees the memory block whose base address equals the given address.
     * Removes the block from the allocated list and adds it back to the free list.
     *
     * @param address the base address of the block to free
     */
    public void free(int address) {
        Node current = allocatedList.getFirst();

        while (current != null) {
            MemoryBlock allocatedBlock = current.block;

            if (allocatedBlock.baseAddress == address) {
                allocatedList.remove(current); // Remove from allocated list
                freeList.addLast(allocatedBlock); // Add back to free list
                System.out.println("Successfully freed memory block at address " + address);
                defrag(); // Attempt to merge adjacent free blocks
                return;
            }

            current = current.next;
        }

        System.out.println("Error: Invalid address to free");
    }

    /**
     * Performs defragmentation of the free list. Merges adjacent memory blocks.
     */
    public void defrag() {
        if (freeList.getSize() <= 1) {
            return; // Nothing to defragment if 0 or 1 block
        }

        // Use a temporary list to store sorted blocks
        LinkedList tempList = new LinkedList();
        Node current = freeList.getFirst();

        // Sort freeList blocks by base address while transferring them to tempList
        while (current != null) {
            MemoryBlock currentBlock = current.block;
            Node tempCurrent = tempList.getFirst();
            int index = 0;

            while (tempCurrent != null && currentBlock.baseAddress > tempCurrent.block.baseAddress) {
                tempCurrent = tempCurrent.next;
                index++;
            }

            tempList.add(index, currentBlock);
            current = current.next;
        }

        // Replace freeList with the sorted tempList
        freeList = tempList;

        // Merge adjacent blocks in freeList
        current = freeList.getFirst();

        while (current != null && current.next != null) {
            MemoryBlock currentBlock = current.block;
            MemoryBlock nextBlock = current.next.block;

            // If the current and next blocks are contiguous, merge them
            if (currentBlock.baseAddress + currentBlock.length == nextBlock.baseAddress) {
                currentBlock.length += nextBlock.length; // Extend the current block
                freeList.remove(current.next);          // Remove the next block
            } else {
                current = current.next; // Move to the next block
            }
        }
    }

    /**
     * A textual representation of the memory space, for debugging purposes.
     */
    @Override
    public String toString() {
        return "Free List:\n" + freeList.toString() + "\nAllocated List:\n" + allocatedList.toString();
    }
}
