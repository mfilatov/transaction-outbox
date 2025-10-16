package com.gruelbox.transactionoutbox;

import java.util.function.Consumer;

public interface TraceContextInterceptor {

  TraceContextInterceptor EMPTY = new TraceContextInterceptor() {};

  default TraceContext getTraceContext() {
    // no-op
    return null;
  }

  default Consumer<TransactionOutboxEntry> wrapTraceContext(
      TraceContext traceContext, Consumer<TransactionOutboxEntry> localExecutor) {
    // no-op
    return localExecutor;
  }
}
