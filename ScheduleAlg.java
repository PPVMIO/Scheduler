import java.util.*;

/**
 * Created by PaulPelayo on 10/11/15.
 */
public class ScheduleAlg {
    private ArrayList<Process> processesOriginal;
    private Scanner randomInput;

    public ScheduleAlg(ArrayList<Process> processes, Scanner randomInput){
        this.processesOriginal = processes;
        this.randomInput = randomInput;
    }


    public void firstComeFirstServe(boolean detailed, String inputs){

        ArrayList<Process> processes = new ArrayList<Process>();
        ArrayList<Process> readyQueue = new ArrayList<Process>();
        Process temp = null;
        for (int i = 0; i < processesOriginal.size(); i++){
            temp = processesOriginal.get(i);
            processes.add(new Process(temp.getA(),temp.getB(),temp.getC(),temp.getIO(), i));
        }

        int t=0;
        int runningIndex = -1;
        boolean complete = false;
        int cpuBurst = 0;
        int num = 0 ;
        int cpuUtility = 0;
        int ioUtility = 0;

        int[] blocked = new int[processes.size()];
        for(int i = 0; i < processes.size();i++)
            blocked[i]= -1;


        System.out.println(inputs);
        System.out.println("The scheduling algorithm used was First Come First Served");
        System.out.println();


        while(!complete){
            if(detailed){System.out.print("Before cycle " + t + "  :	");
                for(Process p: processes){
                    if(p.getState() == 'q')
                        System.out.print("Ready 0\t\t\t");
                    if(p.getState() == 'r')
                        System.out.print("Running "+ cpuBurst+ "\t\t");
                    if(p.getState() == 'b')
                        System.out.print("Blocked " + blocked[processes.indexOf(p)] + "\t\t");
                    if(p.getState() == 't')
                        System.out.print("Terminated 0\t");
                    if(p.getState() == 'u')
                        System.out.print("Unstarted 0\t\t");
                }
                System.out.println();
            }

            unblock(processes, blocked, readyQueue);
            handleArrivals(processes, readyQueue, t);

            if(runningIndex == -1){
                if(!readyQueue.isEmpty()){
                    Process currProcess = readyQueue.get(0);
                    currProcess.updateState('r');
                    runningIndex = processes.indexOf(currProcess);

                    cpuBurst = generateCPUBurst(currProcess);
                    currProcess.setInitialCPUBurst(cpuBurst);
                    readyQueue.remove(0);
                }
            }
            else if(runningIndex != -1 && cpuBurst == 1){
                Process currProcess = processes.get(runningIndex);
                if(currProcess.getC() == 1){
                    if(currProcess.getFinishTime()==0)
                        currProcess.setFinishTime(t);
                    currProcess.updateState('t');
                    currProcess.decreaseC();
                }
                else{
                    currProcess.updateState('b');
                    currProcess.decreaseC();
                    blocked[runningIndex] = currProcess.getInitialCPUBurst() * currProcess.getIO();
                    currProcess.increaseIOBurst(blocked[runningIndex]);

                }
                if(!readyQueue.isEmpty()){
                    runningIndex = processes.indexOf(readyQueue.get(0));
                    currProcess = processes.get(runningIndex);
                    currProcess.updateState('r');
                    readyQueue.remove(0);
                    cpuBurst = generateCPUBurst(currProcess);
                    currProcess.setInitialCPUBurst(cpuBurst);
                }
                else
                    runningIndex =- 1;
            }

            else if(runningIndex!=-1 && cpuBurst>0){
                cpuBurst--;
                processes.get(runningIndex).decreaseC();
            }

            decreaseBlockedTime(processes, blocked);

            num  = 0;
            for(Process p: processes){
                //if cpu bust is over
                if(p.getC() == 0){
                    if(processes.indexOf(p) == runningIndex){
                        if(!readyQueue.isEmpty()){
                            runningIndex = processes.indexOf(readyQueue.get(0));
                            Process currProcess = processes.get(runningIndex);
                            currProcess.updateState('1');
                            readyQueue.remove(0);
                            cpuBurst = generateCPUBurst(currProcess);
                        }
                    }
                    num++;
                    if(p.getFinishTime() == 0)
                        p.setFinishTime(t);

                    p.updateState('t');
                    readyQueue.remove(p);
                }
                if(num == processes.size()){
                    complete=true;
                }
            }
            for(Process p:processes){
                if(p.getState() == 'q')
                    p.increaseWait();
            }

            t++;

            if(runningIndex!=-1)
                cpuUtility++;

            for(int i=0; i < processes.size(); i++){
                if(blocked[i] >= 0){
                    ioUtility++;
                    break;
                }
            }

        }
        finalPrettyPrint(cpuUtility, ioUtility, processesOriginal.size(), processes);

    }
    //end fcfs

    public void roundRobin(boolean detailed , String inputs){
        ArrayList<Process> processes = new ArrayList<Process>();
        ArrayList<Process> readyQueue = new ArrayList<Process>();
        ArrayList<Process> readyQueueCopy = new ArrayList<Process>();
        Process temp = null;
        for (int i = 0; i < processesOriginal.size(); i++){
            temp = processesOriginal.get(i);
            processes.add(new Process(temp.getA(),temp.getB(),temp.getC(),temp.getIO(), i));
        }

        int t = 0;
        int runningIndex = -1;
        boolean complete = false;
        int cpuBurst = 0;
        int num = 0 ;
        int cpuUtility = 0;
        int ioUtility = 0;
        int quant = 2;

        int[] bursts = new int[processes.size()];
        int[] blocked = new int[processes.size()];
        for(int i = 0; i < processes.size();i++)
            blocked[i] = -1;


        System.out.println(inputs);

        System.out.println("The scheduling algorithm used was Round Robin");
        System.out.println();

        while(!complete){
            if(detailed){System.out.print("Before cycle " + t + "  :\t");
                for(Process p: processes){
                    if(p.getState() == 'q')
                        System.out.print("Ready 0\t\t\t");
                    if(p.getState() == 'r')
                        System.out.print("Running "+ cpuBurst+ "\t\t");
                    if(p.getState() == 'b')
                        System.out.print("Blocked " + blocked[processes.indexOf(p)] + "\t\t");
                    if(p.getState() == 't')
                        System.out.print("Terminated 0\t");
                    if(p.getState() == 'u')
                        System.out.print("Unstarted 0\t\t");
                }
                System.out.println();

            }
            unblock(processes, blocked, readyQueueCopy);
            handleArrivals(processes, readyQueue, t);

            if(runningIndex == -1){
                if(!readyQueue.isEmpty()){
                    Process currProcess = readyQueue.get(0);
                    currProcess.updateState('r');
                    runningIndex = processes.indexOf(currProcess);
                    if(bursts[runningIndex] == 0)
                    {
                        bursts[runningIndex]= generateCPUBurst(currProcess);
                        processes.get(runningIndex).setInitialCPUBurst(bursts[runningIndex]);
                    }
                    readyQueue.remove(0);
                    quant = 2;

                }else if(!readyQueueCopy.isEmpty()){
                    Process currProcess = readyQueueCopy.get(0);
                    currProcess.updateState('r');
                    runningIndex = processes.indexOf(readyQueueCopy.get(0));
                    if(bursts[runningIndex]==0) {
                        bursts[runningIndex]=generateCPUBurst(currProcess);
                        processes.get(runningIndex).setInitialCPUBurst(bursts[runningIndex]);
                    }
                    readyQueueCopy.remove(0);
                    quant=2;

                }
            }
            else if(runningIndex != -1 && bursts[runningIndex] == 1 ){
                Process currProcess = processes.get(runningIndex);
                if(currProcess.getC() != 1){
                    currProcess.updateState('b');
                    bursts[runningIndex]--;
                    currProcess.decreaseC();
                    blocked[runningIndex] = currProcess.getInitialCPUBurst() * currProcess.getIO();
                    processes.get(runningIndex).increaseIOBurst(blocked[runningIndex]);
                }
                else{
                    if(currProcess.getFinishTime() == 0){
                        currProcess.setFinishTime(t);
                    }
                    currProcess.updateState('t');
                    currProcess.decreaseC();

                }
                if(!readyQueue.isEmpty()){
                    runningIndex = processes.indexOf(readyQueue.get(0));
                    currProcess = processes.get(runningIndex);
                    currProcess.updateState('r');
                    readyQueue.remove(0);
                    if(bursts[runningIndex] == 0 || bursts[runningIndex] == -1)
                    {
                        bursts[runningIndex]=generateCPUBurst(currProcess);
                        currProcess.setInitialCPUBurst(bursts[runningIndex]);
                    }
                    quant=2;
                }
                else if(!readyQueueCopy.isEmpty()){
                    currProcess = readyQueueCopy.get(0);
                    currProcess.updateState('r');

                    runningIndex = processes.indexOf(currProcess);
                    currProcess = processes.get(runningIndex);
                    if(bursts[runningIndex] == 0) {

                        bursts[runningIndex] = generateCPUBurst(currProcess);
                        currProcess.setInitialCPUBurst(bursts[runningIndex]);
                    }
                    readyQueueCopy.remove(0);
                    quant = 2;
                }
                else
                    runningIndex=-1;
            }

            else if(runningIndex != -1 && bursts[runningIndex] > 0){
                Process currProcess = processes.get(runningIndex);
                if(quant == 1){
                    currProcess.updateState('q');

                    currProcess.decreaseC();
                    bursts[runningIndex]--;
                    readyQueueCopy.add(currProcess);

                    if(!readyQueue.isEmpty()){
                        runningIndex = processes.indexOf(readyQueue.get(0));
                        currProcess = processes.get(runningIndex);
                        currProcess.updateState('r');
                        if(bursts[runningIndex]==0) {
                            bursts[runningIndex] = generateCPUBurst(currProcess);
                            currProcess.setInitialCPUBurst(bursts[runningIndex]);
                        }
                        readyQueue.remove(0);
                        quant = 2;
                    }
                    else if(!readyQueueCopy.isEmpty()){
                        readyQueueCopy.get(0).updateState('r');
                        runningIndex = processes.indexOf(readyQueueCopy.get(0));
                        currProcess = processes.get(runningIndex);
                        if(bursts[runningIndex]==0) {
                            bursts[runningIndex] = generateCPUBurst(currProcess);
                            currProcess.setInitialCPUBurst(bursts[runningIndex]);
                        }
                        readyQueueCopy.remove(0);
                        quant = 2;
                    }
                }else{
                    quant--;
                    bursts[runningIndex]--;
                    processes.get(runningIndex).decreaseC();
                }
            }

            Collections.sort(readyQueueCopy);
            readyQueue.addAll(readyQueueCopy);
            readyQueueCopy.clear();

            num = 0;
            decreaseBlockedTime(processes, blocked);

            for(Process p: processes){
                if(p.getC() == 0){
                    if(processes.indexOf(p) == runningIndex){
                        if(!readyQueue.isEmpty()){
                            Process fromQueue = readyQueue.get(0);
                            runningIndex = processes.indexOf(fromQueue);
                            processes.get(runningIndex).updateState('r');
                            if(bursts[runningIndex] == 0)
                                bursts[runningIndex] = generateCPUBurst(fromQueue);
                            quant = 2;
                        }else if(!readyQueueCopy.isEmpty()){
                            Process fromQueue = readyQueueCopy.get(0);

                            readyQueueCopy.get(0).updateState('r');
                            runningIndex=processes.indexOf(fromQueue);
                            if(bursts[runningIndex] == 0)
                                bursts[runningIndex]=generateCPUBurst(fromQueue);
                            readyQueueCopy.remove(0);
                            quant = 2;
                        }
                    }
                    num++;
                    if(p.getFinishTime() == 0)
                        p.setFinishTime(t);
                    p.updateState('t');
                    readyQueue.remove(p);
                }
                if(num == processes.size())
                    complete=true;
            }
            for(Process p: processes){
                if(p.getState() == 'q')
                    p.increaseWait();
            }
            t++;
            if(runningIndex != -1)
                cpuUtility++;
            for(int i=0; i< processes.size(); i++){
                if(blocked[i]>=0){
                    ioUtility++;
                    break;
                }
            }
        }

        finalPrettyPrint(cpuUtility, ioUtility, processesOriginal.size(), processes);

    }

    public void uniprogram(boolean detailed, String inputs){
        ArrayList<Process> processes = new ArrayList<Process>();
        Process temp = null;
        for (int i = 0; i < processesOriginal.size(); i++){
            temp = processesOriginal.get(i);
            processes.add(new Process(temp.getA(),temp.getB(),temp.getC(),temp.getIO(), i));
        }

        int t = 0;
        int runningIndex = -1;
        boolean complete = false;
        int cpuBurst = 0;
        int num = 0 ;
        int cpuUtility = 0;
        int ioUtility = 0;
        boolean usedCPU = false;


        int[] blocked = new int[processes.size()];
        for(int i = 0; i < processes.size();i++)
            blocked[i] = -1;


        System.out.println(inputs);

        System.out.println("The scheduling algorithm used was Uniprogrammed");
        System.out.println();



        while(!complete){
            for (Process p: processes){
                p.setFirstRun(false);
            }

            if(detailed){
                System.out.print("Before cycle " + t + "  :\t");
                for(Process p: processes){
                    if(p.getState() == 'q')
                        System.out.print("Ready 0\t\t\t");
                    if(p.getState() == 'r')
                        System.out.print("Running "+ cpuBurst+ "\t\t");
                    if(p.getState() == 'b')
                        System.out.print("Blocked " + blocked[processes.indexOf(p)] + "\t\t");
                    if(p.getState() == 't')
                        System.out.print("Terminated 0\t");
                    if(p.getState() == 'u')
                        System.out.print("Unstarted 0\t\t");
                }
                System.out.println();

            }

            //unblock is handled differently in uniprogram (setFirstRun)
            if(runningIndex != -1){
                if(blocked[runningIndex] == 0){
                    blocked[runningIndex]--;
                    Process currProcess = processes.get(runningIndex);
                    if(currProcess.getState() != -2){
                        currProcess.updateState('r');
                        cpuBurst = generateCPUBurst(currProcess);
                        currProcess.setInitialCPUBurst(cpuBurst);
                        currProcess.setFirstRun(true);
                    }
                }
            }

            //handleArrivals is handled differently in uniprogram (setFirstRun)
            for(Process p:processes){
                if(p.getA() == t){
                    if(runningIndex == -1){
                        runningIndex = processes.indexOf(p);
                        Process currProcess = processes.get(runningIndex);
                        cpuBurst= generateCPUBurst(currProcess);
                        currProcess.setInitialCPUBurst(cpuBurst);
                        currProcess.setFirstRun(true);
                        p.updateState('r');
                    }
                    else
                        p.updateState('q');
                }
            }

            Process currProcess = processes.get(runningIndex);
            if(runningIndex != -1 && cpuBurst == 1 && !currProcess.isFirstRun()){
                if(currProcess.getState() == 'r'){
                    if(currProcess.getC() != 1){
                        currProcess.updateState('b');
                        currProcess.decreaseC();
                        blocked[runningIndex] = currProcess.getInitialCPUBurst() * currProcess.getIO();
                        currProcess.increaseIOBurst(blocked[runningIndex]);
                    }
                    else{
                        if(currProcess.getFinishTime() == 0)
                            currProcess.setFinishTime(t);
                        currProcess.updateState('t');
                        currProcess.decreaseC();
                        if(runningIndex == processes.size() - 1){
                            complete = true;
                        }
                        //handle the ready waiting processes
                        else if(processes.get(runningIndex + 1).getState() == 'q'){
                            runningIndex++;
                            currProcess = processes.get(runningIndex);
                            currProcess.updateState('r');
                            cpuBurst = generateCPUBurst(currProcess);
                            currProcess.setInitialCPUBurst(cpuBurst);
                        }
                        else
                            runningIndex = -1;
                    }
                }
            }

            else if(runningIndex != -1 && cpuBurst > 1 && !currProcess.isFirstRun()){
                if(currProcess.getState() == 'r'){
                    if(currProcess.getC() == 1){
                        if(currProcess.getFinishTime() == 0)
                            currProcess.setFinishTime(t);
                        currProcess.updateState('t');
                        currProcess.decreaseC();
                        if(runningIndex == processes.size() - 1) {
                            complete = true;
                        }
                        //handle the ready waiting processes
                        else if(processes.get(runningIndex + 1).getState() == 'q'){
                            runningIndex++;
                            currProcess = processes.get(runningIndex);
                            currProcess.updateState('r');
                            cpuBurst = generateCPUBurst(currProcess);
                            currProcess.setInitialCPUBurst(cpuBurst);
                        }
                        else
                            runningIndex = -1;
                    }
                    else{
                        cpuBurst--;
                        currProcess.decreaseC();
                    }
                }
            }
            if(blocked[runningIndex] > 0){
                blocked[runningIndex]--;
            }

            num = 0;

            for(Process p:processes){
                if(p.getC() == 0)
                    num++;
                if(num == processes.size())
                    complete = true;
            }
            for(Process p:processes){
                if(p.getState() == 'q')
                    p.increaseWait();
                if(p.getState() == 'r')
                    usedCPU = true;

            }
            if(usedCPU)
                cpuUtility++;
            t++;
            for(int i = 0; i < processes.size(); i++){
                if(blocked[i] >= 0){
                    ioUtility++;
                    break;
                }
            }

        }
        finalPrettyPrint(cpuUtility, ioUtility, processesOriginal.size(), processes);

    }

    public void shortestJobFirst(boolean detailed, String inputs)
    {
        ArrayList<Process> processes = new ArrayList<Process>();
        ArrayList<Process> readyQueue = new ArrayList<Process>();
        Process temp = null;
        for (int i = 0; i < processesOriginal.size(); i++){
            temp = processesOriginal.get(i);
            processes.add(new Process(temp.getA(),temp.getB(),temp.getC(),temp.getIO(), i));
        }

        int t=0;
        int runningIndex = -1;
        boolean complete = false;
        int cpuBurst = 0;
        int num = 0 ;
        int cpuUtility = 0;
        int ioUtility = 0;
//        boolean isRunning = false;

        int[] blocked = new int[processes.size()];
        for(int i = 0; i < processes.size();i++)
            blocked[i]= -1;


        System.out.println(inputs);

        System.out.println("The scheduling algorithm used was Shortest Job First");
        System.out.println();

        blocked = new int[processes.size()];
        for(int i=0;i<processes.size();i++)
            blocked[i]=-1;
        while(!complete){
            if(detailed){
                System.out.print("Before cycle " + t + "  :\t");
                for(Process p: processes){
                    if(p.getState() == 'q')
                        System.out.print("Ready 0\t\t\t");
                    if(p.getState() == 'r')
                        System.out.print("Running "+ cpuBurst+ "\t\t");
                    if(p.getState() == 'b')
                        System.out.print("Blocked " + blocked[processes.indexOf(p)] + "\t\t");
                    if(p.getState() == 't')
                        System.out.print("Terminated 0\t");
                    if(p.getState() == 'u')
                        System.out.print("Unstarted 0\t\t");
                }
                System.out.println();
            }

            unblock(processes, blocked, readyQueue);
            handleArrivals(processes, readyQueue, t);

            Process currProcess;
            if(runningIndex == -1){
                if(!readyQueue.isEmpty()){

                    int m = Integer.MAX_VALUE;
                    int tempIndex =- 1;
                    for(int i = 0; i < readyQueue.size();i++){
                        if(readyQueue.get(i).getC() < m){
                            tempIndex = i;
                            m = readyQueue.get(i).getC();
                        }
                    }
                    readyQueue.get(tempIndex).updateState('r');
                    runningIndex = processes.indexOf(readyQueue.get(tempIndex));
                    currProcess = processes.get(runningIndex);
                    cpuBurst= generateCPUBurst(currProcess);
                    currProcess.setInitialCPUBurst(cpuBurst);
                    readyQueue.remove(tempIndex);
                }
            }
            else if(runningIndex != -1 && cpuBurst == 1){
                currProcess = processes.get(runningIndex);
                if(currProcess.getC() != 1){
                    currProcess.updateState('b');
                    currProcess.decreaseC();
                    blocked[runningIndex] = currProcess.getInitialCPUBurst() * currProcess.getIO();
                    currProcess.increaseIOBurst(blocked[runningIndex]);
                }
                else{
                    if(currProcess.getFinishTime() == 0)
                        currProcess.setFinishTime(t);
                    currProcess.updateState('t');
                    currProcess.decreaseC();

                }
                if(!readyQueue.isEmpty()){

                    int m = Integer.MAX_VALUE;
                    int tempIndex = -1;
                    for(int i = 0 ;i<readyQueue.size();i++){
                        if(readyQueue.get(i).getC() < m){
                            tempIndex = i;
                            m = readyQueue.get(i).getC();
                        }
                    }
                    readyQueue.get(tempIndex).updateState('r');
                    runningIndex = processes.indexOf(readyQueue.get(tempIndex));
                    currProcess = processes.get(runningIndex);
                    cpuBurst= generateCPUBurst(currProcess);
                    currProcess.setInitialCPUBurst(cpuBurst);
                    readyQueue.remove(tempIndex);
                }
                else
                    runningIndex = -1;
            }

            else if(runningIndex !=-1 && cpuBurst > 0){
                cpuBurst--;
                processes.get(runningIndex).decreaseC();
            }

            for(int i=0;i<processes.size();i++){
                if(blocked[i]>0){
                    blocked[i]--;
                }
            }
            num = 0;
            for(Process p: processes){
                if(p.getC() == 0){
                    if(processes.indexOf(p)==runningIndex){
                        if(!readyQueue.isEmpty()){
                            int m = Integer.MAX_VALUE;
                            int tempIndex = -1;
                            for(int i = 0 ;i<readyQueue.size();i++){
                                if(readyQueue.get(i).getC() < m){
                                    tempIndex = i;
                                    m = readyQueue.get(i).getC();
                                }
                            }
                            readyQueue.get(tempIndex).updateState('r');
                            runningIndex=processes.indexOf(readyQueue.get(tempIndex));
                            currProcess = processes.get(runningIndex);
                            cpuBurst= generateCPUBurst(currProcess);
                            currProcess.setInitialCPUBurst(cpuBurst);
                            readyQueue.remove(tempIndex);
                        }
                        else
                            runningIndex = -1;
                    }
                    num++;
                    if(p.getFinishTime() == 0)
                        p.setFinishTime(t);

                    p.updateState('t');
                    readyQueue.remove(p);
                }
                if(num == processes.size())
                    complete=true;

            }
            for(Process p: processes)
                if(p.getState()=='q')
                    p.increaseWait();

            t++;
            if(runningIndex != -1)
                cpuUtility++;
            for(int i=0; i < processes.size(); i++){
                if(blocked[i] >= 0){
                    ioUtility++;
                    break;
                }
            }
        }
        finalPrettyPrint(cpuUtility, ioUtility, processesOriginal.size(), processes);

    }

    private void unblock(ArrayList<Process> processes, int[] blocked, ArrayList<Process> readyQueue){
        for(int i=0; i< processes.size();i++){
            if(blocked[i] == 0){
                blocked[i]--;
                if(processes.get(i).getState()!=-2){
                    processes.get(i).updateState('q');
                    readyQueue.add(processes.get(i));
                }
            }
        }

    }


    private void handleArrivals(ArrayList<Process> processes, ArrayList<Process> readyQueue, int t){
        for (Process p: processes){
            if(p.getA() == t){
                p.updateState('q');
                readyQueue.add(p);
            }
        }
    }

    private void decreaseBlockedTime(ArrayList<Process> processes, int[] blocked){
        for(int i=0;i < processes.size();i++){//decrement blocked time for all
            if(blocked[i] > 0){
                blocked[i]--;
            }
        }
    }

    private int generateCPUBurst(Process p){
        int rand = 0;
        try{
            rand = Integer.parseInt(randomInput.nextLine());
        }
        catch(NoSuchElementException e){
            p.getProcessSummary();
            System.out.println("Read past end of file error: " + e) ;
            System.exit(1);
        }
        return (rand % processesOriginal.get(p.getIndex()).getB()) + 1;
    }




    public void finalPrettyPrint(int cpuUtility, int ioUtility, int num, ArrayList<Process> processesCopy){

        int max = 0;
        int totalWait = 0;
        int totalTime = 0;
        for (int i = 0; i < processesOriginal.size(); i++){
            Process p = processesCopy.get(i);
            if(p.getFinishTime() > max)
                max = p.getFinishTime();
            totalWait = totalWait + p.getWaitTime();
            totalTime = totalTime + (p.getFinishTime() - p.getA());
            System.out.println("Process " + i + ": ");
            System.out.println("\t(A, B, C, M) = " + processesOriginal.get(i).displayProcess());//p.displayProcess());
            System.out.println("\tFinishing Time: " + p.getFinishTime());
            System.out.println("\tTurnaround Time: " + (p.getFinishTime() - p.getA()));
            System.out.println("\tI/O Time: " + p.getIOBurst());
            System.out.println("\tWaiting Time: " + p.getWaitTime());
            //System.out.println();
        }

        System.out.println("Summary Data:");
        System.out.println("\tFinishing time: "+ max);
        System.out.println("\tCPU Utilization: "+ ((double) cpuUtility)/max);
        System.out.println("\tI/O Utilization: "+ ((double) ioUtility)/max);
        System.out.println("\tThroughout "+ ((double) num/max)*100 +" processes per hundred cycles");
        System.out.println("\tAverage Turnaround time " + ((double) totalTime)/num);
        System.out.println("\tAverage Wait time "+ ((double) totalWait)/num);
        //System.out.println();
        System.out.println("\n============================================================================================");
        System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
        System.out.println("============================================================================================\n");

    }

    /*
     *  Used for testing
     */
    public void displayAllProcess(ArrayList<Process> tempProcesses){
        for(Process p: tempProcesses)
            p.getProcessSummary();
    }


}
