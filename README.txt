----------------------------------------------------
|README for Hypothetical Operating System Simulator|
----------------------------------------------------

---------------
|INSTRUCTIONS:|
---------------

To run this program, it is only necessary to
compile (command javac) the HypotheticalOS class,
and then run it with java.exe.

In the Command Prompt window, a successful run will
result in the following message:

The simulation has completed with no errors.
Please check Simulation_Results.txt for the
simulation results.

This informs the user that a text file has been
created successfully in the same folder with all of
the default data.  This text file will reveal all 
results of the simulation, including all 3 trial
runs with appropriate labels for every time step.

-------------------------------
|DETAILS FOR EVERY CLASS FILE:|
-------------------------------

---------------
|MemorySegment|
---------------

This class represents a single block of theoretical
memory, keeping track of its size, ID, and the
process currently held within it (if any). A boolean
called filled will represent whether or not this
memory segment is taken by a process already.

------------
|MemoryList|
------------

This class keeps track of all blocks of active
theoretical memory. It stores an array of
MemorySegment objects and has a method which can be
called to attempt to allocate a process to an empty
block of memory.  Likewise, there is a method to remove a given process from its respective location
in memory.  This class will also keep track of all
other relevant information on the memory segments 
handled, including total wasted space
(internal and external fragmentation).

------------
|HypProcess|
------------

This class represents a hypothetical process or job
and has fields indicating its size (in MB), time
needed to finish executing, and time of entry into
the ready queue.  Once the process is allocated to a
segment of memory, it will also maintain a parameter
indicating which memory segment it is contained in.
Finally, the process's state is tracked with an
enumeration value (either WAITING, READY, RUNNING,
or FINISHED) and will mostly  be handled by the
process itself (though the scheduler does play a
role).

------------------
|ProcessScheduler|
------------------

This class simulates short-term and long-term
schedulers, by sorting processes according to the
scheduling priority, either Shortest-Job First or
First-Come First-Served.  The waitQueue linked list
is ordered according to this priority to simulate
the long-term scheduler, and the readyQueue linked
list receives round robin scheduling with priority
given to the processes that come first according to
the scheduling priority.  This simulates the short-
term scheduler.  The ProcessScheduler class also
keeps track of the time step, incrementing by the
round robin timeslice every time processes get a
turn.  Finally, this class has methods which can be
called to retrieve information about all processes
in memory or waiting for memory, formatted to fit
the table as created by the main program.

----------------
|HypotheticalOS|
----------------

This is the main program for this system, and the
only one the user needs to run.  The final variables
that dictate the time units of each trial, the
number of CPU cores, the time slice for each round
robin turn, and the schemes for each trial in the
full simulation are all hard-coded.  However, any
modification to these variables is possible and can
produce alternate simulations.  Each simulation is
saved to the file Simulation_Results.txt and is in a
format that is easy to read, with relevant
information on all processes handled by the
theoretical schedulers.