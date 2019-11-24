package main.java.com.codeforcommunity;

import java.sql.SQLException;
import java.util.List;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.tools.jdbc.*;
import java.lang.String;
import java.util.Map;
import java.util.HashMap;

public class JooqMock implements MockDataProvider {
  // Results given by the user
  private Map<String, Result<Record>> givenResults;
  // Results default to this class
  private Map<String, Result<Record>> defaultResults;
  // Default result to return if nothing is matched
  private Result<Record> basicDefault;
  // DSL Context to use
  private DSLContext context;

  /**
   * No param constructor for JooqMock.
   */
  public JooqMock() {
    setup();
    givenResults = new HashMap<String, Result<Record>>();
    givenResults.put("DEFAULT", basicDefault);
  }

  /**
   * Default param constructor for JooqMock.
   *
   * @param defaultResult The result to be returned if no pre-made results match what is requested.
   */
  public JooqMock(Result<Record> defaultResult) {
    this();
    givenResults.replace("DEFAULT", defaultResult);
  }

  /**
   * Constructor for JooqMock that sets the result for 'SELECT name ...'.
   * If name is "DEFAULT", will be used as default result if not results match what is requested.
   * It is recommended to use the JooqMock(Record defaultResult) constructor in that case.
   *
   * @param name Name of table to select from in 'SELECT name'.
   * @param namedResult Result to be returned when 'SELECT name' is called.
   */
  public JooqMock(String name, Result<Record> namedResult) {
    this();
    if (name.equals("DEFAULT"))
      givenResults.replace("DEFAULT", namedResult);
    else
      givenResults.put(name, namedResult);
  }

  /**
   * Constructor for JooqMock that sets the results to return.
   * If any results are named "DEFAULT", then that will be the default result.
   * If only one result exists in recordResults, it is recommended to instead use either the
   *  JooqMock(String name, Record namedResult) or JooqMock(Record defaultResult) constructors.
   *
   * @param recordResults The map of String to Record, such that the desired Record will be returned
   *  when the given String is used.
   */
  public JooqMock(Map<String, Result<Record>> recordResults) {
    setup();
    givenResults = recordResults;
  }

  /**
   * Add new named record to givenResults.
   *
   * @param name Name of result to add.
   * @param record Record to be mapped to name.
   * @param overwrite Whether to overwrite values if they already exist.
   * @return Whether or not this operation succeeded. 0 for failed, 1 for succeeded,
   *  2 for overwritten.
   */
  public int addToRecordResults(String name, Result<Record> record, boolean overwrite) {
    if (givenResults.containsKey(name) && !overwrite)
      return 0;
    else if (givenResults.containsKey(name)) {
      givenResults.replace(name, record);
      return 2;
    }

    givenResults.put(name, record);
    return 1;
  }

  /**
   * Add new named record to givenResults. Will fail if name exists.
   *
   * @param name Name of result to add.
   * @param record Record to be mapped to name.
   * @return Whether or not this operation succeeded. 0 for failed, 1 for succeeded.
   */
  public int addToRecordResults(String name, Result<Record> record) {
    return addToRecordResults(name, record, false);
  }

  /**
   * Add new named records to givenResults. Will fail if name exists.
   *
   * @param newResults New results to add.
   * @param overwrite Whether to overwrite values if they already exist.
   * @return A map of whether or not this operation succeeded. 0 for failed, 1 for succeeded,
   *  2 for overwritten.
   */
  public Map<String, Integer> addToRecordResults(Map<String, Result<Record>> newResults,
      boolean overwrite) {
    Map<String, Integer> result = new HashMap<>();
    newResults.forEach((k, v) -> result.put(k, addToRecordResults(k, v, overwrite)));

    return result;
  }

  /**
   * Add new named records to givenResults. Will fail if name exists.
   *
   * @param newResults New results to add.
   * @return A map of names to operation success. 0 for failed, 1 for succeeded.
   */
  public Map<String, Integer> addToRecordResults(Map<String, Result<Record>> newResults) {
    return addToRecordResults(newResults, false);
  }

  /**
   * Remove a result from givenResults. Will fail if name doesn't exist.
   *
   * @param name Name of result to remove.
   * @return 1 if successful, 0 otherwise.
   */
  public int removeFromRecordResults(String name) {
    if (!givenResults.containsKey(name))
      return 0;
    else if (name.equals("DEFAULT"))
      givenResults.replace(name, basicDefault);
    else
      givenResults.remove(name);

    return 1;
  }

  /**
   * Remove a list of results from givenResults. Will fail if name doesn't exist.
   *
   * @param names A list of names to remove.
   * @return a Map of Names to operation success. 0 for failed, 1 for succeeded.
   */
  public Map<String, Integer> removeFromRecordResults(List<String> names) {
    Map<String, Integer> result = new HashMap<>();
    for (String name : names) {
      result.put(name, removeFromRecordResults(name));
    }

    return result;
  }

  /**
   * Sets the default results in the defaultResults map.
   */
  public void setup() {
    context = DSL.using(SQLDialect.POSTGRES);
    defaultResults = new HashMap<>();
    basicDefault = context.newResult();
  }

  public DSLContext getContext() {
    return this.context;
  }

  @Override
  public MockResult[] execute(MockExecuteContext ctx) throws SQLException {
    MockResult[] mock = new MockResult[1];

    String sql = ctx.sql();

    if (sql.toUpperCase().startsWith("DROP"))
      throw new SQLException("Statement not supported: " + sql);

    givenResults.forEach((k, v) -> {
      if (k.length() >= sql.length() && k.substring(0, sql.length()).equals(sql)) {
        Result<Record> result = givenResults.get(k);
        mock[0] = new MockResult(result.size(), result);
      }
    });

    if (mock[0] != null) {
      return mock;
    }
    else if (sql.toUpperCase().startsWith("SELECT")) {
      String[] stmt = sql.toUpperCase().split(" ");

      Result<Record> result;
      if (givenResults.containsKey(stmt[1]))
        result = givenResults.get(stmt[1]);

      else if (defaultResults.containsKey(stmt[1]))
        result = defaultResults.get(stmt[1]);

      else
        result = givenResults.get("DEFAULT");

      mock[0] = new MockResult(result.size(), result);
    }

    else if (ctx.batch())
      throw new SQLException("Statement not yet handled: " + sql);

    return mock;
  }
}
