import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Scanner;

/**
 * Created by PaulPelayo on 10/11/15.
 */
public class Scheduler {

    //static final String PATH = "input/input-5.txt";
    static final String RANDOMNUMPATH = "input/random-number.txt";
    static String fcfsInput;
    static String rrInput;
    static String uniInput;
    static String sjfInput;
    //static ArrayList<Process> processes = new ArrayList();

    public static void main(String args[]){
        Scanner textLine = new Scanner (System.in);
        boolean detailed = false;
        String textFile = "";


        if (args.length == 1){
            detailed = false;
            textFile = args[0];

        }

        if (args.length == 2){
            if (args[0].equals("--verbose"))
                detailed = true;
            else {
                System.out.println("Incorrect Input, program will terminate");
                System.exit(1);
            }
            textFile = args[1];


        }
//        System.out.println("Please enter the path of the input:");
//        String textFile = textLine.next();//textLine.next();
//        String wantsDetails = "";
//        while (!wantsDetails.equals("y") && !wantsDetails.equals("n")){
//            System.out.println("Do you wish to view the detailed version?");
//            wantsDetails = textLine.next();
//
//        }






        Scanner v1 = null;
        Scanner v2 = null;
        Scanner v3 = null;
        Scanner v4 = null;
        Scanner randomNumScanner1 = null;
        Scanner randomNumScanner2 = null;
        Scanner randomNumScanner3 = null;
        Scanner randomNumScanner4 = null;




        try {
            v1 = new Scanner(new File(textFile));
            v2 = new Scanner(new File(textFile));
            v3 = new Scanner(new File(textFile));
            v4 = new Scanner(new File(textFile));
            randomNumScanner1 = new Scanner(new File(RANDOMNUMPATH));
            randomNumScanner2 = new Scanner(new File(RANDOMNUMPATH));
            randomNumScanner3 = new Scanner(new File(RANDOMNUMPATH));
            randomNumScanner4 = new Scanner(new File(RANDOMNUMPATH));
        }
        catch (IOException e)
        {
            System.out.println("If you are having trouble reading in the files, please look at the README file");
            System.out.println("File Error: " + textFile + " was not found " + e);
        }

        ArrayList<Process> p1 = setUp(v1, 1);       //fcfs
        ArrayList<Process> p2 = setUp(v2, 2);       //rr
        ArrayList<Process> p3 = setUp(v3, 3);       //uni
        ArrayList<Process> p4 = setUp(v4, 4);       //sjf


        ScheduleAlg saFCFS = new ScheduleAlg(p1, randomNumScanner1);
        ScheduleAlg saRR = new ScheduleAlg(p2, randomNumScanner2);
        ScheduleAlg saUNI = new ScheduleAlg(p3, randomNumScanner3);
        ScheduleAlg saSJF = new ScheduleAlg(p4, randomNumScanner4);

        System.out.println("\n\t\t\t~~~ STARTING SCHEDULER ~~~");
        System.out.println("============================================================================================");
        System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
        System.out.println("============================================================================================\n");

        saFCFS.firstComeFirstServe(detailed, fcfsInput);
        saRR.roundRobin(detailed, rrInput);
        saUNI.uniprogram(detailed, uniInput);
        saSJF.shortestJobFirst(detailed, sjfInput);

    }

    public static ArrayList<Process> setUp(Scanner input, int type){
        // String originalLine = input.nextLine();
        //Scanner lineBreaker = new Scanner(originalLine);
        //System.out.println(originalLine);
        ArrayList<Process> processes = new ArrayList<Process>();
        int num, a, b, c, io;
        num = input.nextInt();
//        System.out.println("num: " + num);

//        if (type == 1)          //fcfs
//        else if (type == 2)     //rr
//        else if (type == 3)     //uni
//        else
        String originalInput = "The original input was: " + num + " ";

        for (int i = 0; i < num; i++){

            a = Integer.parseInt(input.next().substring(1));                //remove leading '('
            //System.out.println("a: " + a);

            b = input.nextInt();
            //System.out.println("b: " + b);

            c = input.nextInt();
            //System.out.println("c: " + c);

            io = Integer.parseInt(input.next().substring(0, 1));            //remove trailing '('
            //System.out.println("io: " + io);

            originalInput = originalInput + "(" + a + " " + b + " " + c + " " + io + ") ";

            Process p = new Process(a, b, c, io, i);
            processes.add(p);
        }

        //System.out.println(originalInput);
        Collections.sort(processes);

        String sortedInput = "The (sorted) input is: " + num + " ";
        for (Process p: processes)
            sortedInput = sortedInput + "(" + p.getA() + " " + p.getB() + " " + p.getC() + " " + p.getIO() + ") ";

        if (type == 1)          //fcfs
            fcfsInput = originalInput + '\n' + sortedInput + '\n';
        else if (type == 2)     //rr
            rrInput = originalInput + '\n' + sortedInput + '\n';
        else if (type == 3)     //uni
            uniInput = originalInput + '\n' + sortedInput + '\n';
        else                    //sjf
            sjfInput = originalInput + '\n' + sortedInput + '\n';

        //System.out.println(sortedInput);
        return processes;

    }





}
