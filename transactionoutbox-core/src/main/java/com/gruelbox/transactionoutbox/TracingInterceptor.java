package com.gruelbox.transactionoutbox;

import java.util.function.Consumer;

public interface TracingInterceptor {

  TracingInterceptor EMPTY = new TracingInterceptor() {};

  default Tracing getTracing() {
    // no-op
    return null;
  }

  default Consumer<TransactionOutboxEntry> wrapTrace(
      Tracing tracing, Consumer<TransactionOutboxEntry> localExecutor) {
    // no-op
    return localExecutor;
  }
}
