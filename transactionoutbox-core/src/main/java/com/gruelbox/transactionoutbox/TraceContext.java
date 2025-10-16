package com.gruelbox.transactionoutbox;

import lombok.Value;

@Value
public class TraceContext {

  String traceId;

  String spanId;

  byte traceFlags;
}
