package com.gruelbox.transactionoutbox;

import lombok.Value;

@Value
public class Tracing {

  String traceId;

  String spanId;
}
