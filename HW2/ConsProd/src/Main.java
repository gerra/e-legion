import java.io.*;
import java.nio.channels.Pipe;
import java.util.concurrent.TimeUnit;

/**
 * Created by root on 27.09.15.
 */
public class Main {
    public static void main(String[] args) throws IOException {

        PipedOutputStream outputStream = new PipedOutputStream();
        PipedInputStream inputStream = new PipedInputStream(outputStream);

        new Thread(new Producer(outputStream)).start();
        new Thread(new Consumer(inputStream)).start();
    }

    private static class Producer implements Runnable {
        private OutputStream mOut;

        public Producer(OutputStream mOut) {
            this.mOut = mOut;
        }

        @Override
        public void run() {
            for (int i = 0; i < 10; i++) {
                try {
                    mOut.write(("i = " + i).getBytes());
                    TimeUnit.SECONDS.sleep(1);
                } catch (IOException e) {
                    Thread.currentThread().interrupt();
                } catch (InterruptedException e) {
                    Thread.interrupted();
                }

            }
        }
    }

    private static class Consumer implements Runnable {
        private InputStream mIn;

        public Consumer(InputStream mIn) {
            this.mIn = mIn;
        }

        @Override
        public void run() {
            final byte[] buffer = new byte[64];
            while (!Thread.currentThread().isInterrupted()) {
                int bytes = 0;
                try {
                    bytes = mIn.read(buffer);
                    if (bytes > 0) {
                        System.out.println(new String(buffer, 0, bytes));
                    }
                } catch (IOException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}
