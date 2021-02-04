package com.codeforcommunity.processor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.codeforcommunity.JooqMock;
import com.codeforcommunity.JooqMock.OperationType;
import org.jooq.DSLContext;
import org.jooq.generated.Tables;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** A class for testing the ProcessorImpl. */
public class ProcessorImplTest {
  // the JooqMock to use for testing
  JooqMock mockDb;

  /** Method to setup mockDb and processor. */
  @BeforeEach
  void setup() {
    mockDb = new JooqMock();
  }

  @Test
  public void testSomethingTest() {
    assertTrue(true);
  }

  @Test
  public void testFetchExistsTrue() {
    setup();
    DSLContext db = mockDb.getContext();
    mockDb.addExistsReturn(true);
    assertTrue(db.fetchExists(db.selectFrom(Tables.USERS).where(Tables.USERS.ID.eq(1))));
    assertEquals(1, mockDb.getSqlOperationBindings().get(OperationType.EXISTS).size());
  }

  @Test
  public void testFetchExistsFalse() {
    setup();
    DSLContext db = mockDb.getContext();
    mockDb.addExistsReturn(false);
    assertFalse(db.fetchExists(db.selectFrom(Tables.USERS).where(Tables.USERS.ID.eq(1))));
    assertEquals(1, mockDb.getSqlOperationBindings().get(OperationType.EXISTS).size());
  }
}
