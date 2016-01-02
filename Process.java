import java.util.Scanner;

/**
 * Created by PaulPelayo on 10/11/15.
 */
public class Process implements Comparable<Process>{

    private int arrivalTime;
    private int cpuTime;

    private int a;                  //arrivalTime   -   time that process arrives
    private int b;                  //cpuTime       -   time until process completes
    private int c;                  //cpuBurst      -   this number is used to calculate CPU Burst
                                    //                      randomNumber % c + 1 = cpuBurst
    private int io;                 //ioBurst       -   this number is used to calculate ioBurst
                                    //                      io * prevCPUBurst = ioBurst
    private char processState;      //r - running, b - blocked, q - ready, t - terminated, u - unstarted
    private int ioBurst;
    private int finishTime;
    private int waitTime;

    private int initialCPUBurst;
    private int index;

    private boolean usingQuantum;
    private boolean readyToBlock;
    private boolean firstRun;   //only for Uni

    public Process(int a, int b, int c, int io, int index){
        this.a = a;
        this.b = b;
        this.c = c;
        this.io = io;
        processState = 'u';
        //this.randomInput = randomInput;
        ioBurst = 0;
        finishTime = 0;
        initialCPUBurst = 0;
        this.index = index;
        usingQuantum = false;
        readyToBlock = false;
    }

//    public void increaseFinishTime(){
//        finishTime++;
//    }
    public void increaseWait(){
        waitTime++;
    }

    public int getWaitTime(){
        return waitTime;
    }

    public int getIndex() { return index;}

    public int getIO() {
        return io;
    }

    public int getC() {
        return c;
    }

    public int getB() {
        return b;
    }

    public int getA() {

        return a;
    }
    public void setInitialCPUBurst(int burst){
        initialCPUBurst = burst;
    }

    public int getInitialCPUBurst(){
        return initialCPUBurst;
    }

    public int getFinishTime(){
        return finishTime;
    }

    public boolean setFinishTime(int t){
        finishTime = t;
        return true;
    }

    public void setFirstRun(boolean b){
        firstRun = b;
    }

    public boolean isFirstRun(){
        return firstRun;
    }

    public int decreaseC(){
        c--;
        return c;
    }

    public void setUsingQuantum(boolean b){
        usingQuantum = b;
    }

    public boolean isUsuingQuantum(){
        return usingQuantum;
    }

//    public int getCPUBurst(){
//        return (Integer.parseInt(randomInput.nextLine()) % this.b) + 1;
//    }

    public boolean updateState(char c){
        processState = c;
        return true;
    }

    public char getState(){
        return processState;
    }



    public boolean increaseIOBurst(int burst){
        ioBurst = ioBurst + burst;
        return true;
    }

    public int getIOBurst(){
        return ioBurst;
    }

    public void increaseFinish(){
        finishTime++;
    }

    public void setReadyToBlock(boolean b){ readyToBlock = b;}

    public boolean isReadyToBlock() { return readyToBlock;}

    /*
     *  rewrite compareTo so that Collection.sort(unsortedProcesses) can sort
     *  the processes based on arrival time
     */

    public int compareTo(Process otherProcess){
        if(otherProcess.getA() > this.getA())
            return -1;
        else if(otherProcess.getA() < this.getA())
            return 1;
        else {
            if (otherProcess.getIndex() < this.getIndex())
                return 1;
            else if(otherProcess.getIndex() > this.getIndex())
                return -1;
            else
                return 0;
        }



//        else if(otherProcess.getB() > this.B)return -1;
//        else if(otherProcess.getB() < this.B)return 1;
//        else if(otherProcess.getC() > this.C)return -1;
//        else if(otherProcess.getC() < this.C)return 1;
//        else if(otherProcess.getIO() > this.IO)return -1;
//        else if(otherProcess.getIO() < this.IO)return 1;
        //else return 0;


    }


    public String displayProcess(){
        return "(" + this.getA() + " " + this.getB() + " " + this.getC() + " " + this.getIO() + ")";
    }

    public void getProcessSummary(){
        System.out.println("==============================");

        System.out.println("The current process index: " + getIndex());
        System.out.println("The current process is: " + displayProcess());
        System.out.println("Here are the variables: ");
        System.out.println("\ta: " + getA());
        System.out.println("\tb: " + getB());
        System.out.println("\tc: " + getC());
        System.out.println("\tIO: " + getIO());
        System.out.println("\tState: " + getState());
        System.out.println("\tFinish: " + getFinishTime());
        System.out.println("\tWait: " + getWaitTime());
        System.out.println("\tIOBurst: " + getIOBurst());
        System.out.println("\tInitial cpuBurst: " + getInitialCPUBurst());
        System.out.println("\tUsing Quantum: " + usingQuantum);
        System.out.println("\tReady to Block: " + readyToBlock);

        System.out.println();




    }
}
