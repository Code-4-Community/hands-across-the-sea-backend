package com.codeforcommunity.processor;

import com.codeforcommunity.JooqMock;

/** A class for testing the ProcessorImpl. */
public class ProcessorImplTest {
  // the JooqMock to use for testing
  JooqMock mockDb;

  /** Method to setup mockDb and processor. */
  void setup() {
    mockDb = new JooqMock();
  }
}
