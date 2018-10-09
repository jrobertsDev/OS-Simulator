
import java.util.Arrays;
import java.util.LinkedList;

public class ProcessScheduler
{
	public enum ScheduleOrder {FCFS, SJF};
	private LinkedList<HypProcess> waitQueue;
	private LinkedList<HypProcess> readyQueue;
	private LinkedList<HypProcess> baseQueue;
	private LinkedList<HypProcess> finished;
	private int timeElapsed;
	private int quantum;
	private ScheduleOrder schedule;
	private MemoryList ram;

	public ProcessScheduler(int quant, MemoryList mem, ScheduleOrder schOrd)
	{
		quantum = quant;
		schedule = schOrd;
		ram = mem;
		timeElapsed = 0;
		waitQueue = new LinkedList<HypProcess>();
		readyQueue = new LinkedList<HypProcess>();
		baseQueue = new LinkedList<HypProcess>();
		finished = new LinkedList<HypProcess>();
	}

	//invoked for the initial generation of processes to schedule
	public void addToBase(int id , int spaceRequest, int execTime)
	{
		HypProcess p = new HypProcess(id, spaceRequest, execTime, schedule);
		baseQueue.add(p);
	}

	//returns the amount of processes waiting to be assigned
	public int totalWaiting()
	{
		return waitQueue.size();
	}

	//returns how many processes have finished
	public int jobsCompleted()
	{
		return finished.size();
	}

	//returns true only if all processes have finished
	public boolean exhaustedQueues()
	{
		return (waitQueue.size() == 0 && readyQueue.size() == 0);
	}

	//this will be called before every time unit to update the schedule
	public void updateReadyQueue()
	{
		//at the start, make the queue of waiting jobs the same as the randomly
		//generated list (FCFS), or sort it according to shortest job first (SJF)
		if (timeElapsed == 0)
		{
			if (schedule == ScheduleOrder.SJF)
			{
				HypProcess[] processArray = baseQueue.toArray(new HypProcess[0]);
				Arrays.sort(processArray);
				LinkedList<HypProcess> temp = new LinkedList<HypProcess>();
				temp.addAll(Arrays.asList(processArray));
				waitQueue = temp;
			}
			else
				waitQueue = baseQueue;
		}

		//pull out processes that are finished
		int index = 0;
		while (index < readyQueue.size())
		{
			if (readyQueue.get(index).getState() == HypProcess.ProcessState.FINISHED)
			{
				HypProcess temp = readyQueue.remove(index);
				ram.deallocateProcess(temp);
				finished.add(temp);
			}
			else index++;
		}

		//look for processes to add that are waiting for a memory segment
		index = 0;
		while (index < waitQueue.size())
		{
			if (ram.allocateProcess(waitQueue.get(index)))
			{
				waitQueue.get(index).setInTime(timeElapsed);
				readyQueue.add(waitQueue.remove(index));
			}
			else index++;
		}
	}

	public void assignToCPU(int numThreads)
	{
		int processorsToAllocate = Math.min(numThreads, readyQueue.size());
		HypProcess[] nextToGo = new HypProcess[processorsToAllocate];
		int amountLastProcesses = 0;
		int fillIndex = 0;

		for (int i =0; i < readyQueue.size(); i++)
		{
			if (readyQueue.get(i).getState() == HypProcess.ProcessState.RUNNING)
				amountLastProcesses++;
		}

		if (processorsToAllocate == numThreads && amountLastProcesses > 0)
		{
			//executed represents the array of processes that went last time slice
			HypProcess[] executed = new HypProcess[amountLastProcesses];

			int nextIndex = 0;
			boolean filledExecuted = false;
			int exIndex = 0;

			//fill up the executed array so that it may be sorted based on current priority
			while (!filledExecuted && nextIndex < readyQueue.size())
			{
				if (readyQueue.get(nextIndex).getState() != HypProcess.ProcessState.READY)
				{
					executed[exIndex] = readyQueue.get(nextIndex);
					exIndex++;
				}
				nextIndex++;
				if (exIndex >= amountLastProcesses)
					filledExecuted = true;
			}

			//sort the array of processes that went last time step
			Arrays.sort(executed);

			/*fill up the array of processes to run next with highest priority processes
			from last time step; the amount to add is equal to the difference between
			the remaining processes and the number of cores to allocate to*/
			if (readyQueue.size() - amountLastProcesses < processorsToAllocate)
			{
				int processesToRepeat = processorsToAllocate - (readyQueue.size() - amountLastProcesses);
				for (int i = 0; i < processesToRepeat; i++)
				{
					nextToGo[i] = executed[i];
					fillIndex++;
				}
			}
		}

		/*add processes that went last time step to the array of processes to go this time, and
		set the states of processes that went last time to READY (they will set to RUNNING if
		they go again on their own)*/
		for (int i = 0; i < readyQueue.size(); i++)
		{
			if ((readyQueue.get(i).getState() != HypProcess.ProcessState.RUNNING && fillIndex < nextToGo.length) || nextToGo.length < numThreads)
			{
				nextToGo[fillIndex] = readyQueue.get(i);
				fillIndex++;
			}
			else
				readyQueue.get(i).setState(HypProcess.ProcessState.READY);
		}

		//finally, give the processes that are next in line CPU time
		for (int i = 0; i < nextToGo.length; i++)
			nextToGo[i].executeProcess(quantum);

		//update the time elapsed
		timeElapsed += quantum;
	}

	//returns an array of strings representing relevant information on processes currently in memory
	public String[] readyQueueString()
	{
		String[] rqs = new String[readyQueue.size()];
		for (int k = 0; k < readyQueue.size(); k++)
		{
			rqs[k] = String.format("%-7s",timeElapsed);
			String padId = Integer.toString(readyQueue.get(k).getId());
			rqs[k] += String.format("%5s", padId+"   ");
			rqs[k] += String.format("%17s", readyQueue.get(k).getMemorySegment()+"   ")+String.format("%17s",readyQueue.get(k).getSize()+" MB   ");
			rqs[k] += String.format("%12s",readyQueue.get(k).getTimeRemaining()+"   ") + String.format("%7s",readyQueue.get(k).getState().name()+"  ");
		}
		if (rqs.length == 0)
		{
			String[] emptyStringArray = new String[1];
			emptyStringArray[0] = "";
			return emptyStringArray;
		}
		else
			return rqs;
	}

	//returns an array of strings representing information on processes currently waiting for memory
	public String[] waitQueueString()
	{
		String[] wqs = new String[waitQueue.size()];
		for (int k = 0; k < waitQueue.size(); k++)
		{
			wqs[k] = String.format("%-7s",timeElapsed);
			String padId = Integer.toString(waitQueue.get(k).getId());
			wqs[k] += String.format("%5s", padId+"   ");
			wqs[k] += String.format("%17s", waitQueue.get(k).getMemorySegment()+"   ")+String.format("%17s", waitQueue.get(k).getSize()+" MB   ");
			wqs[k] += String.format("%12s",waitQueue.get(k).getTimeRemaining()+"   ")+ String.format("%7s",waitQueue.get(k).getState().name()+"  ");
		}
		if (wqs.length == 0)
		{
			String[] emptyStringArray = new String[1];
			emptyStringArray[0] = "";
			return emptyStringArray;
		}
		else
			return wqs;
	}
}