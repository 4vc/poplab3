import java.util.concurrent.Semaphore;
import java.util.concurrent.LinkedBlockingDeque;

public class Main {
    public static void main(String[] args) {
        int storageSize = 8;
        int itemNumbers = 14;
        int numProducers = 2;
        int numConsumers = 3;

        LinkedBlockingDeque<String> storage = new LinkedBlockingDeque<>(storageSize);
        Semaphore accessStorage = new Semaphore(1);
        Semaphore fullStorage = new Semaphore(storageSize);
        Semaphore emptyStorage = new Semaphore(0);

        Thread[] producers = new Thread[numProducers];
        Thread[] consumers = new Thread[numConsumers];

        for (int i = 0; i < numProducers; i++) {
            producers[i] = new Thread(new Producer(itemNumbers, storage, accessStorage, fullStorage, emptyStorage));
            producers[i].start();
        }

        for (int i = 0; i < numConsumers; i++) {
            consumers[i] = new Thread(new Consumer(itemNumbers, storage, accessStorage, fullStorage, emptyStorage));
            consumers[i].start();
        }
    }
}

class Producer implements Runnable {
    private int itemNumbers;
    private LinkedBlockingDeque<String> storage;
    private Semaphore accessStorage;
    private Semaphore fullStorage;
    private Semaphore emptyStorage;

    public Producer(int itemNumbers, LinkedBlockingDeque<String> storage, Semaphore accessStorage, Semaphore fullStorage, Semaphore emptyStorage) {
        this.itemNumbers = itemNumbers;
        this.storage = storage;
        this.accessStorage = accessStorage;
        this.fullStorage = fullStorage;
        this.emptyStorage = emptyStorage;
    }

    public void run() {
        try {
            for (int i = 1; i <= itemNumbers; i++) {
                fullStorage.acquire();
                accessStorage.acquire();

                storage.offer("item " + i);
                System.out.println("Added item " + i);

                accessStorage.release();
                emptyStorage.release();
                Thread.sleep(100);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

class Consumer implements Runnable {
    private int itemNumbers;
    private LinkedBlockingDeque<String> storage;
    private Semaphore accessStorage;
    private Semaphore fullStorage;
    private Semaphore emptyStorage;

    public Consumer(int itemNumbers, LinkedBlockingDeque<String> storage, Semaphore accessStorage, Semaphore fullStorage, Semaphore emptyStorage) {
        this.itemNumbers = itemNumbers;
        this.storage = storage;
        this.accessStorage = accessStorage;
        this.fullStorage = fullStorage;
        this.emptyStorage = emptyStorage;
    }

    public void run() {
        try {
            for (int i = 1; i <= itemNumbers; i++) {
                emptyStorage.acquire();
                accessStorage.acquire();

                String item = storage.pollFirst();
                System.out.println("Took " + item);

                accessStorage.release();
                fullStorage.release();
                Thread.sleep(500);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
