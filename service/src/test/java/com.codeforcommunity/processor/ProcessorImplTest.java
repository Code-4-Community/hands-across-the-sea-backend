package com.codeforcommunity.processor;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.codeforcommunity.JooqMock;
import org.junit.jupiter.api.Test;

/** A class for testing the ProcessorImpl. */
public class ProcessorImplTest {
  // the JooqMock to use for testing
  JooqMock mockDb;

  /** Method to setup mockDb and processor. */
  void setup() {
    mockDb = new JooqMock();
  }

  @Test
  public void testSomethingTest() {
    assertTrue(true);
  }
}
