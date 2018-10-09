
//comparable interface used for sorting by the scheduler
public class HypProcess implements Comparable
{
	public enum ProcessState {WAITING, READY, RUNNING, FINISHED};
	private int memoryRequest;
	private int executionTime;
	private int timeRemaining;
	private int idNo;
	private int inTime;
	private int memSegment;
	private ProcessState currentState;
	private ProcessScheduler.ScheduleOrder schedule;

	public HypProcess(int id, int mem, int exTime, ProcessScheduler.ScheduleOrder sched)
	{
		idNo = id;
		memoryRequest = mem;
		executionTime = exTime;
		timeRemaining = executionTime;
		inTime = -1; //default value of inTime should not be valid, for distinguishment
		currentState = ProcessState.WAITING; //default state is waiting
		memSegment = -1; //default memory segment ID is -1 (indicates process is waiting)
		schedule = sched; //needed for comparison in compareTo() method
	}

	//returns process's ID
	public int getId()
	{
		return idNo;
	}

	//returns process's memory request in KB
	public int getSize()
	{
		return memoryRequest;
	}

	//sets the time the process enters memory
	public void setInTime(int t)
	{
		inTime = t;
		//process enters memory in ready state
		currentState = ProcessState.READY;
	}

	//returns the time the process entered memory
	public int getInTime()
	{
		return inTime;
	}

	//returns the time the process needs to finish
	public int getTimeRemaining()
	{
		return timeRemaining;
	}

	//returns the initial time request of the process
	public int getExecutionTime()
	{
		return executionTime;
	}

	//allows processes to be sorted by the scheduler
	public int compareTo(Object otherProcess) throws InvalidOrNullSchedulingException
	{
		if (schedule == ProcessScheduler.ScheduleOrder.FCFS)
		{
			Integer compareThis = new Integer(inTime);
			Integer compareOther = new Integer(((HypProcess)otherProcess).getInTime());
			return compareThis.compareTo(compareOther);
		}
		else if (schedule == ProcessScheduler.ScheduleOrder.SJF)
		{
			Integer compareThis = new Integer(executionTime);
			Integer compareOther = new Integer(((HypProcess)otherProcess).getExecutionTime());
			return compareThis.compareTo(compareOther);
		}
		else
		{
			throw new InvalidOrNullSchedulingException();
		}
	}

	//sets the enumeration value representing the current process state
	public void setState(ProcessState state)
	{
		currentState = state;
	}

	//returns the enumeration value representing the current process state
	public ProcessState getState()
	{
		return currentState;
	}

	//returns the id of the memory segment currently in use
	public int getMemorySegment()
	{
		return memSegment;
	}

	//tells this process which memory segment it is currently in
	public void setMemorySegment(int address)
	{
		memSegment = address;
	}

	//decrements timeRemaining by the amount passed by quantum
	//changes state appropriately based on completion status
	public void executeProcess(int quantum)
	{
		timeRemaining = timeRemaining - quantum;
		if (timeRemaining <= 0)
		{
			timeRemaining = 0;
			currentState = ProcessState.FINISHED;
		}
		else
			currentState = ProcessState.RUNNING;
	}

	//exception class unique to this class, mainly for debug purposes
	private class InvalidOrNullSchedulingException extends RuntimeException
	{
		public String toString()
		{
			return "Scheduling not properly defined.";
		}
	}
}