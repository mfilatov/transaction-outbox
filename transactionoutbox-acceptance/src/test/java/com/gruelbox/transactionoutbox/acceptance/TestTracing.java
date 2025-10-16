package com.gruelbox.transactionoutbox.acceptance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.gruelbox.transactionoutbox.*;
import com.gruelbox.transactionoutbox.testing.InterfaceProcessor;
import com.gruelbox.transactionoutbox.testing.LatchListener;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
class TestTracing {

  @Test
  final void testTracingPassedToTask() throws InterruptedException {

    TransactionManager transactionManager = new StubThreadLocalTransactionManager();

    CountDownLatch latch = new CountDownLatch(1);
    TransactionOutbox outbox =
        TransactionOutbox.builder()
            .transactionManager(transactionManager)
            .serializeTracing(true)
            .tracingInterceptor(
                new TracingInterceptor() {

                  @Override
                  public Tracing getTracing() {
                    return new Tracing("test trace id", "test span id", (byte) 0x01);
                  }

                  @Override
                  public Consumer<TransactionOutboxEntry> wrapTrace(
                      Tracing tracing, Consumer<TransactionOutboxEntry> localExecutor) {
                    assertEquals("test trace id", tracing.getTraceId());
                    assertEquals("test span id", tracing.getSpanId());
                    assertEquals(0x01, tracing.getTraceFlags());

                    return localExecutor;
                  }
                })
            .instantiator(
                Instantiator.using(
                    clazz ->
                        (InterfaceProcessor)
                            (foo, bar) -> {
                              log.info("Processing ({}, {})", foo, bar);
                            }))
            .listener(new LatchListener(latch))
            .persistor(StubPersistor.builder().build())
            .build();

    transactionManager.inTransaction(
        () -> outbox.schedule(InterfaceProcessor.class).process(3, "Whee"));

    assertTrue(latch.await(2, TimeUnit.SECONDS));
  }
}
