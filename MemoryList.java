
public class MemoryList
{
	private final int[] MEM_SIZES = {32, 48, 24, 16, 64, 32, 24};
	public enum MemorySelectScheme {FIRSTFIT, BESTFIT};
	private MemorySegment[] memory;
	private MemorySelectScheme scheme;
	private int processesHeld;

	//constructor that takes an enumeration value for memory select
	//as its only parameter, then sets every memory segment's size
	public MemoryList(MemorySelectScheme fit)
	{
		processesHeld = 0;
		scheme = fit;
		memory = new MemorySegment[MEM_SIZES.length];
		for (int i = 0; i < memory.length; i++)
			memory[i] = new MemorySegment(i, MEM_SIZES[i]);
	}

	/*takes in a process, and  based on the specific scheme used (see
	the enum called MemorySelectScheme) for memory allocation, finds
	an available slot in memory and fills it; returns true if and only
	if it can add the process; if it can't, then return false to let
	the caller know it couldn't be allocated*/
	public boolean allocateProcess(HypProcess hp)
	{
		if (processesHeld == MEM_SIZES.length)
			return false;
		if (scheme == MemorySelectScheme.FIRSTFIT)
		{
			int index = firstAvailable(hp, 0);
			hp.setMemorySegment(index);
			if (index >= 0)
			{
				if (memory[index].fill(hp))
				{
					processesHeld++;
					return true;
				}
				else
					return false;
			}
			else
				return false;
		}
		else //best-fit scheme
		{
			int index = firstAvailable(hp, 0);
			if (index == -1)
			{
				return false; //none available!
			}
			//search through the rest of the memory segments
			//to find the minimum-size segment that still fits
			MemorySegment smallest = memory[index];
			while (index != -1)
			{
				if (index > -1 && memory[index].getSize() < smallest.getSize())
					smallest = memory[index];
				index = firstAvailable(hp, index+1);
			}
			if (smallest.fill(hp))
			{
				hp.setMemorySegment(smallest.getMemoryNum());
				processesHeld++;
				return true;
			}
			else //an error occurs filling the slot
				return false;
		}
	}

	//private process for this class only -- returns first fit index
	private int firstAvailable(HypProcess hp, int start)
	{
		for (int i = start; i < memory.length; i++)
		{
			//check that the page is empty AND that it's big enough
			if (memory[i].getSize() >= hp.getSize() && memory[i].isEmpty())
				return i;
		}
		return -1;
	}

	//takes a HypProcess object, searches for it, and if it exists in the
	//block of memory, return the removed process; otherwise, return null
	public HypProcess deallocateProcess(HypProcess hp)
	{
		for (int i = 0; i < memory.length; i++)
		{
			if (memory[i].getProcess() == hp)
			{
				processesHeld--;
				return memory[i].remove();
			}
		}
		return null;
	}

	//returns a boolean indicating the existence of an available segment
	public boolean isFull()
	{
		return (processesHeld >= MEM_SIZES.length);
	}

	//returns the total memory unused throughout all the segments
	public int getWastedMemory()
	{
		int total = 0;
		for (int i = 0; i < memory.length; i++)
		{
			total += memory[i].wastedSpace();
		}
		return total;
	}
}