import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class TokenRingAlgorithm {
    public static String process_id; //Ex P1
    public static int pid; // Ex. 1

    private static InetAddress server_host_addr = InetAddress.getLoopbackAddress();
    private static ServerSocket server_socket;
    public static int[] process_portList;

    public static volatile TokenRingFrame tokenRingFrame;
    public static int num_processes;
    public static volatile File file = new File("tokenCount.txt");
    public static volatile int count = 48;
    public static volatile int initialStage = 0;

    public static Thread connectionThread = null;


    public TokenRingAlgorithm(int num_processes, String processId) throws IOException {
        pid = Integer.parseInt(processId);
        process_id = "P" + processId;
        this.num_processes = num_processes;

        process_portList = new int[num_processes];
        for (int i = 0; i < num_processes; i++) {
            process_portList[i] = 2001 + i;
        }

        tokenRingFrame = new TokenRingFrame();
        tokenRingFrame.setCount(count); //ASCII value of 0
        tokenRingFrame.setTokenOwnership(false);
        tokenRingFrame.setPid(0);
        tokenRingFrame.setProcess_id("P0");

    }

    public void initialize() throws IOException {
        System.out.println("PROCESS: " + process_id);
        System.out.println("\t\t    ___");
        System.out.println("\t\t   / []\\");
        System.out.println("\t\t _|_____|_");
        System.out.println("\t\t| | === | |");
        System.out.println("\t\t|_|  0  |_|");
        System.out.println("\t\t ||_____||");
        System.out.println("\t\t|~ \\___/ ~|");
        System.out.println("\t\t/=\\ /=\\ /=\\");
        System.out.println("\t\t[_] [_] [_]\n");

        if (tokenRingFrame.getPid() == 0 && initialStage == 0) {
            tokenRingFrame.setTokenOwnership(true);
            FileWriter fw = new FileWriter(file, false);
            fw.write(48);
            fw.close();
            initialStage = 1;
        }

        //Thread 01: manageConnections
        connectionThread = (new Thread(() -> {
            manageConnections();
            return;
        }));

        connectionThread.start();

        try {
            // Added this delay to wait till all other processes are in the listening state
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        handleTokenRing();

    }

    public void manageConnections(){
        int server_port = process_portList[pid];
        try {
            server_socket = new ServerSocket(server_port, 0, server_host_addr);
            //while(events_delivered != process_eventList.length){
            while(true){
                Socket clientSocket = server_socket.accept();
                ObjectInputStream obj_ip = new ObjectInputStream(clientSocket.getInputStream());
                TokenRingFrame receivedTokenRingFrame = (TokenRingFrame) obj_ip.readObject();
                System.out.println("Received the token from process P" + pid + "!");
                clientSocket.close();
                tokenRingFrame.setCount(count);
                tokenRingFrame.setTokenOwnership(true);
                tokenRingFrame.setPid(pid);
                tokenRingFrame.setProcess_id(process_id);
                handleTokenRing();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private synchronized void handleTokenRing()  {

        try {
            if (tokenRingFrame.isTokenOwnership() && tokenRingFrame.getPid() == pid) {
                System.out.println("Token is with me! (" + process_id + ")");
            } else {
                System.out.println("Token is with the process P" + tokenRingFrame.getPid() + "!");
            }
            //while(true) {

                if (tokenRingFrame.isTokenOwnership() && tokenRingFrame.getPid() == pid) {
                    int option = 0;
                    do {
                        System.out.println("Do you want to access the shared resource (tokenCount.txt)? Enter 1 for yes and 0 for no:");
                        Scanner scanner = new Scanner(System.in);
                        option = scanner.nextInt();
                        if (option == 1) {
                            synchronized (file) {
                                System.out.println("\nLocking the shared file!");
                                FileReader fr = new FileReader(file);
                                count = fr.read();
                                fr.close();
                                FileWriter fw = new FileWriter(file, false);
                                count = count + 1;
                                fw.write(count);
                                tokenRingFrame.setCount(count);
                                fw.close();
                                System.out.println("Incremented the counter in shared file: tokenCount.txt to " + ((char) count) + ".");
                                System.out.println("Releasing the lock on shared file!\n");
                            }
                        } else {
                            System.out.println("Passing the token ring to the process P" + (pid + 1) % num_processes);
                            Socket socket = new Socket(server_host_addr, process_portList[(pid + 1) % num_processes]);
                            ObjectOutputStream obj_op = new ObjectOutputStream(socket.getOutputStream());
                            obj_op.writeObject(tokenRingFrame);

                            obj_op.close();
                            socket.close();
                        }
                    } while (option != 0);
                }
            //}
        }catch (Exception e) {
            e.printStackTrace();
        }

    }

}

