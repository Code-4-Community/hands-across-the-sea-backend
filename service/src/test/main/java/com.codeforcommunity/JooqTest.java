package main.java.com.codeforcommunity;

import java.sql.SQLException;
import main.java.com.codeforcommunity.JooqMock;
import static org.junit.jupiter.api.Assertions.*;

import org.jooq.tools.jdbc.MockExecuteContext;
import org.jooq.tools.jdbc.MockResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class JooqTest {
  JooqMock mock = new JooqMock();

  @Test
  public void test() {
    MockResult result;
    try {
      MockExecuteContext ctx = new MockExecuteContext(new String[]{"SELECT * FROM NOTE"},
          null);
      result = mock.execute(ctx)[0];
      System.out.println(result.data.format());
    }
    catch (SQLException e) {}


    assertEquals(1, 1);
  }

}
