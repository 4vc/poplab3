using System;
using System.Threading;
using System.Collections.Generic;

class ProducerConsumer
{
    static List<string> storage = new List<string>();
    static Semaphore accessStorage = new Semaphore(1, 1);
    static Semaphore fullStorage;
    static Semaphore emptyStorage;

    static void Main()
    {
        int storageSize = 5;
        int itemNumbers = 15;
        int numProducers = 2;
        int numConsumers = 4;

        fullStorage = new Semaphore(storageSize, storageSize);
        emptyStorage = new Semaphore(0, storageSize);

        Thread[] producers = new Thread[numProducers];
        Thread[] consumers = new Thread[numConsumers];

        for (int i = 0; i < numProducers; i++)
        {
            producers[i] = new Thread(() => Produce(itemNumbers));
            producers[i].Start();
        }

        for (int i = 0; i < numConsumers; i++)
        {
            consumers[i] = new Thread(() => Consume(itemNumbers));
            consumers[i].Start();
        }

        for (int i = 0; i < numProducers; i++)
        {
            producers[i].Join();
        }

        for (int i = 0; i < numConsumers; i++)
        {
            consumers[i].Join();
        }
    }

    static void Produce(int itemNumbers)
    {
        for (int i = 1; i <= itemNumbers; i++)
        {
            fullStorage.WaitOne();
            accessStorage.WaitOne();

            storage.Add("item " + i.ToString());
            Console.WriteLine("Added item " + i.ToString());

            accessStorage.Release();
            emptyStorage.Release();
            Thread.Sleep(100);
        }
    }

    static void Consume(int itemNumbers)
    {
        for (int i = 1; i <= itemNumbers; i++)
        {
            emptyStorage.WaitOne();
            accessStorage.WaitOne();

            string item = storage[0];
            Console.WriteLine("Took " + item);

            storage.RemoveAt(0);

            accessStorage.Release();
            fullStorage.Release();
            Thread.Sleep(500);
        }
    }
}
