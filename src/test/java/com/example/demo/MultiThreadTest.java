package com.example.demo;

import cloud.nextsol.core.tax.TaxDtoOuterClass;
import cloud.nextsol.core.tax.TaxServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Metadata;
import io.grpc.stub.MetadataUtils;
import org.junit.jupiter.api.Test;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiThreadTest {

    private static ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9090).usePlaintext().build();
    private static TaxServiceGrpc.TaxServiceBlockingStub stub = TaxServiceGrpc.newBlockingStub(channel);
    private static TaxServiceGrpc.TaxServiceBlockingStub stub1 = TaxServiceGrpc.newBlockingStub(channel);

    private static TaxDtoOuterClass.GetByIdRes result;
    private static TaxDtoOuterClass.CreateRes result2;

    @Test
    public void concurrencyTest() throws InterruptedException {

        int numberOfThreads = 100;
        ExecutorService service = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        Metadata.Key<String> key1 = Metadata.Key.of("app_id", Metadata.ASCII_STRING_MARSHALLER);
        Metadata.Key<String> key2 = Metadata.Key.of("user_id", Metadata.ASCII_STRING_MARSHALLER);
        Metadata.Key<String> key3 = Metadata.Key.of("owner", Metadata.ASCII_STRING_MARSHALLER);
        Metadata header = new Metadata();
        header.put(key1, "mpos");
        header.put(key2, "1407");
        header.put(key3, "dangph1");

        stub = MetadataUtils.attachHeaders(stub, header);
        for (int i = 0; i < numberOfThreads; i++) {



            service.execute(() -> {
                TaxDtoOuterClass.GetByIdReq request = TaxDtoOuterClass.GetByIdReq.newBuilder()
                        .setId("5fec4585eb9b7f6ddf452359").build();
                result = stub.getById(request);
                latch.countDown();
            });
        }

        Thread.sleep(Long.MAX_VALUE);
        service.shutdown();

    }


    @Test
    public void concurrencyTestCreate() throws InterruptedException {
        Random random = new Random();
        int numberOfThreads = 10;
        ExecutorService service = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);


        Metadata.Key<String> key1 = Metadata.Key.of("app_id", Metadata.ASCII_STRING_MARSHALLER);
        Metadata.Key<String> key2 = Metadata.Key.of("user_id", Metadata.ASCII_STRING_MARSHALLER);
        Metadata.Key<String> key3 = Metadata.Key.of("owner", Metadata.ASCII_STRING_MARSHALLER);
        Metadata.Key<String> key4= Metadata.Key.of("location", Metadata.ASCII_STRING_MARSHALLER);
        Metadata.Key<String> key5 = Metadata.Key.of("is_super_app", Metadata.ASCII_STRING_MARSHALLER);

        for (int i = 0; i < numberOfThreads; i++) {
            Metadata header = new Metadata();

            header.put(key1, "mpos ");
            header.put(key2, "user_id "+i);
            header.put(key3, "owner "+i);
            header.put(key4, "location ");
            header.put(key5, "false");

            Runnable runnable = () -> {
                TaxDtoOuterClass.CreateReq request = TaxDtoOuterClass.CreateReq.newBuilder()
                        .setCode("SDK" +random.ints())
                        .setName("Concurrency test")
                        .setPercent(9.9)
                        .setObjectClass("Concurrency test")
                        .setObjectName("Concurrency test")
                        .setAppSubClass("Concurrency test")
                        .setAppKey("Concurrency test")
                        .setAppSubKey("Concurrency test")

                        .build();
                TaxServiceGrpc.TaxServiceBlockingStub myStub = TaxServiceGrpc.newBlockingStub(channel);
                myStub = MetadataUtils.attachHeaders(myStub, header);
                result2 = myStub.create(request);
                System.out.println(result2);
                latch.countDown();
            };


            service.execute(runnable);
        }

        Thread.sleep(Long.MAX_VALUE);
        service.shutdown();

    }


    @Test
    public void concurrencyTest2() throws InterruptedException {

        int numberOfThreads = 10;
        ExecutorService service = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        Metadata.Key<String> key1 = Metadata.Key.of("app_id", Metadata.ASCII_STRING_MARSHALLER);
        Metadata.Key<String> key2 = Metadata.Key.of("user_id", Metadata.ASCII_STRING_MARSHALLER);
        Metadata.Key<String> key3 = Metadata.Key.of("owner", Metadata.ASCII_STRING_MARSHALLER);

        for (int i = 0; i < numberOfThreads; i++) {
            Metadata header = new Metadata();
            header.put(key1, "app_id_test_" +i);
            header.put(key2, "user_id_test_" +i);
            header.put(key3, "owner_test_" +i);


            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    TaxDtoOuterClass.GetByIdReq request = TaxDtoOuterClass.GetByIdReq.newBuilder()
                            .setId("5fdd59ca94a76202321a0859").build();
                    TaxServiceGrpc.TaxServiceBlockingStub myStub = TaxServiceGrpc.newBlockingStub(channel);
                    myStub = MetadataUtils.attachHeaders(myStub, header);
                    result = myStub.getById(request);
                    latch.countDown();
                }
            };
            service.execute(runnable);
        }

        Thread.sleep(Long.MAX_VALUE);
        service.shutdown();

    }
}
