
public class MemorySegment
{
	private int size; //space in this segment
	private int memoryNum; //memory id
	private boolean filled;
	private HypProcess process; //current process

	public MemorySegment(int mem, int s)
	{
		memoryNum = mem;
		size = s;
		filled = false; //starts out empty
	}

	//returns the memory id of this segment
	public int getMemoryNum()
	{
		return memoryNum;
	}

	//returns available space for this segment
	public int getSize()
	{
		return size;
	}

	//return the opposite value of filled boolean
	public boolean isEmpty()
	{
		return !filled;
	}

	//returns the process currently held
	public HypProcess getProcess()
	{
		return process;
	}

	//returns either the size of this memory segment or
	//the size of internal fragmentation, depending on
	//whether there is a process stored
	public int wastedSpace()
	{
		if (filled)
			return size - process.getSize();
		else return size;
	}

	//called when adding a process to this segment
	//will not add the process if already filled
	//returns true if successful, false if not
	public boolean fill(HypProcess h)
	{
		if (!filled)
		{
			process = h;
			filled = true;
			return true;
		}
		else return false;
	}

	//returns the process object currently taking memory
	//returns null if filled is set to false
	public HypProcess remove()
	{
		if (filled == false)
			return null;
		filled = false;
		process = null;
		return process;
	}
}