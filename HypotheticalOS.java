import java.util.Random;
import java.io.*;

public class HypotheticalOS
{
	public static void main(String[] args)
	{
		//string representing the default file name to save to
		final String FILE_NAME = "Simulation_Results.txt";

		//final ints that represent simulation values for this application
		final int TOTAL_PROCESSES = 20;
		final int CPU_CORES = 4;
		final int QUANTUM = 1;
		final int SIMULATION_TIME = 30;
		final int SIMULATIONS = 3;

		//final arrays of enumeration values representing requested scheduling or allocation schemes
		final MemoryList.MemorySelectScheme[] MEM_SCHEMES = new MemoryList.MemorySelectScheme[SIMULATIONS];
		MEM_SCHEMES[0] = MemoryList.MemorySelectScheme.FIRSTFIT;
		MEM_SCHEMES[1] = MemoryList.MemorySelectScheme.BESTFIT;
		MEM_SCHEMES[2] = MemoryList.MemorySelectScheme.BESTFIT;

		final ProcessScheduler.ScheduleOrder[] SCHED_ORDERS = new ProcessScheduler.ScheduleOrder[SIMULATIONS];
		SCHED_ORDERS[0] = ProcessScheduler.ScheduleOrder.FCFS;
		SCHED_ORDERS[1] = ProcessScheduler.ScheduleOrder.FCFS;
		SCHED_ORDERS[2] = ProcessScheduler.ScheduleOrder.SJF;

		//random arrays representing memory/time requests of processes
		Random r = new Random();
		int[] processSizes = new int[TOTAL_PROCESSES];
		int[] processLengths = new int[TOTAL_PROCESSES];

		//set these arrays' elements to random values in appropriate ranges
		for (int i = 0; i < processSizes.length; i++)
		{
			processSizes[i] = 16 + r.nextInt(49);
			processLengths[i] = 5 + r.nextInt(11);
		}

		try
		{
			//create FileWriter and BufferedWriter objects, save to file Simulation_Results
			FileWriter outputLog = new FileWriter(FILE_NAME);
			BufferedWriter write = new BufferedWriter(outputLog);

			//main loop for the simulations, executed one time per simulation
			for (int j = 0; j < SIMULATIONS; j++)
			{
				//put lines for spacing and asterisks for separation between simulations
				if (j > 0)
				{
					write.newLine();
					write.newLine();
					write.write("*******************************************************************");
					write.newLine();
					write.write("*******************************************************************");
					write.newLine();
				}
				write.write("Simulation "+(j+1)+" with "+MEM_SCHEMES[j].name()+" memory allocation and "+SCHED_ORDERS[j].name()+" process schedule:");
				write.newLine();
				write.newLine();
				MemoryList ram = new MemoryList(MEM_SCHEMES[j]);
				ProcessScheduler scheduler = new ProcessScheduler(QUANTUM, ram, SCHED_ORDERS[j]);

				//give the scheduler the set of 20 random processes created earlier
				for (int i = 0; i < processSizes.length; i++)
				{
					scheduler.addToBase(i+1, processSizes[i], processLengths[i]);
				}

				//now start the simulation loop, each iteration representing a time unit
				for (int i = 0; i < SIMULATION_TIME; i++)
				{
					scheduler.updateReadyQueue();
					//check that there are processes still waiting to be assigned
					if (scheduler.exhaustedQueues())
					{
						write.write("All processes finished.");
						break;
					}
					scheduler.assignToCPU(CPU_CORES);

					//make a table heading indicating the meaning of the numbers listed
					write.write("JOBS CURRENTLY IN MEMORY:");
					write.newLine();
					write.write("TIME | ID | MEMORY SEGMENT | MEMORY REQUEST | TIME LEFT | STATE");
					write.newLine();
					write.write("---------------------------------------------------------------");
					write.newLine();

					//get information on jobs in short-term scheduler
					String[] readyQueueStatusArray = scheduler.readyQueueString();
					//write this information to the file
					for (int n = 0; n < readyQueueStatusArray.length; n++)
					{
						write.write(readyQueueStatusArray[n]);
						write.newLine();
					}
					//make another table heading, this time for jobs waiting for memory
					write.newLine();
					write.write("JOBS WAITING FOR MEMORY:");
					write.newLine();
					write.write("TIME | ID | MEMORY SEGMENT | MEMORY REQUEST | TIME LEFT | STATE");
					write.newLine();
					write.write("---------------------------------------------------------------");
					write.newLine();
					//get information on jobs waiting to be assigned to memory
					String[] waitQueueStatusArray = scheduler.waitQueueString();
					//write this information to the file
					for (int n = 0; n < waitQueueStatusArray.length; n++)
					{
						write.write(waitQueueStatusArray[n]);
						write.newLine();
					}
					//output the number of processes waiting and the total memory wasted
					write.write("Total processes waiting for memory assignment: "+ scheduler.totalWaiting());
					write.newLine();
					write.write("Total wasted memory: "+ram.getWastedMemory() + " MB");
					write.newLine();
					write.newLine();
				}
				//update the queues in case a process finished in the final time step
				scheduler.updateReadyQueue();
				write.write("Simulation "+(j+1)+" complete. Total jobs completed: "+scheduler.jobsCompleted());
			}
			//close BufferedWriter and FileWriter objects
			write.close();
			outputLog.close();
			System.out.println("The simulation has completed with no errors.");
			System.out.println("Please check "+FILE_NAME+" for the simulation results.");
		}
		catch(IOException writeError)
		{
			System.out.println("There was an error writing to the file.");
		}
	}
}