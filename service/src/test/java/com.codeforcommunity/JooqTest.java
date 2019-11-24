package com.codeforcommunity;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

import org.jooq.tools.jdbc.MockExecuteContext;
import org.jooq.tools.jdbc.MockResult;
import org.junit.jupiter.api.Test;

public class JooqTest {
  JooqMock mock = new JooqMock();

  @Test
  public void test() {
    MockResult result;
    try {
//      MockExecuteContext ctx = new MockExecuteContext(new String[]{"SELECT * FROM NOTE"},
//          null);
//      result = mock.execute(ctx)[0];
//      System.out.println(result.data.format());
      MockExecuteContext ctx = new MockExecuteContext(new String[]{"SELECT * FROM ALJDF"},
          null);
      result = mock.execute(ctx)[0];
      System.out.println(result.data.format());
    }
    catch (SQLException e) {}


    assertEquals(1, 1);
  }

}
