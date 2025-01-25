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
		if (length <= 0) {
			System.out.println("Error: Requested length must be greater than 0");
			return -1;
		}
		Node currentNode = freeList.getFirst();
		while (currentNode != null) {
			MemoryBlock freeBlock = currentNode.block;
			if (freeBlock.length >= length) {
				int allocatedBaseAddress = freeBlock.baseAddress;
				MemoryBlock allocatedBlock = new MemoryBlock(allocatedBaseAddress, length);
				allocatedList.addLast(allocatedBlock);
	
				if (freeBlock.length == length) {
					freeList.remove(currentNode.block);
				} else {
					freeBlock.baseAddress += length;
					freeBlock.length -= length;
				}
				return allocatedBaseAddress;
			}
			currentNode = currentNode.next;
		}
		System.out.println("Error: Not enough memory to allocate " + length + " words");
		return -1;
	}
    /**
     * Frees the memory block whose base address matches the given address.
     *
     * @param address The base address of the block to free.
     */
    public void free(int address) {
		if (allocatedList.getSize() == 0) {
			System.out.println("Error: No allocated blocks to free");
			return;
		}
		Node currentNode = allocatedList.getFirst();
		while (currentNode != null) {
			MemoryBlock allocatedBlock = currentNode.block;
			if (allocatedBlock.baseAddress == address) {
				allocatedList.remove(currentNode.block);
				freeList.addLast(allocatedBlock);
				defrag();
				System.out.println("Successfully freed memory block at address " + address);
				return;
			}
			currentNode = currentNode.next;
		}
		System.out.println("Error: Invalid address to free");
	}
    /**
     * Performs defragmentation of the free list by merging adjacent memory blocks.
     */
	public void defrag() {
		if (freeList.getSize() <= 1) {
			return; 
		}
		freeList = sortFreeListByBaseAddress();
		Node currentNode = freeList.getFirst();
		while (currentNode != null && currentNode.next != null) {
			MemoryBlock currentBlock = currentNode.block;
			MemoryBlock nextBlock = currentNode.next.block;
			if (currentBlock.baseAddress + currentBlock.length == nextBlock.baseAddress) {
				currentBlock.length += nextBlock.length;
				freeList.remove(nextBlock);
			} else {
				currentNode = currentNode.next; 
			}
		}
	}
	
	/**
	 * Sorts the free list by base address in ascending order.
	 * @return A sorted LinkedList.
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
}
