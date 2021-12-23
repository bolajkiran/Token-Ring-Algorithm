import java.io.IOException;

public class LockingScheme {

    public static void main(String[] args) throws InterruptedException, IOException {
        TokenRingAlgorithm tokenRingAlgorithm = new TokenRingAlgorithm(Integer.parseInt(args[0]), args[1]);
        tokenRingAlgorithm.initialize();

        try {

            // Handle processes' connection concurrently
            if(tokenRingAlgorithm.connectionThread != null){
                tokenRingAlgorithm.connectionThread.join();
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Process "+ TokenRingAlgorithm.process_id + " ended!");

    }
}

