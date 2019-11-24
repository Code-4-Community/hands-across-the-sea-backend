package main.java.com.codeforcommunity;

import static org.jooq.generated.Tables.NOTE;

import java.sql.SQLException;
import java.util.List;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record4;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.tools.jdbc.*;
import java.lang.String;
import java.util.Map;
import java.util.HashMap;
import java.util.function.Supplier;
import org.jooq.generated.tables.Note;

public class JooqMock implements MockDataProvider {
  // Results given by the user
  private Map<String, Supplier<Result<? extends Record>>> givenResultHandlers;
  // Results default to this class
  private Map<String, Supplier<Result<? extends Record>>> defaultResultHandlers;
  // Default result to return if nothing is matched
  private Supplier<Result<? extends Record>> basicDefaultHandler;
  // DSL Context to use
  private DSLContext context;

  /**
   * No param constructor for JooqMock.
   */
  public JooqMock() {
    setup();
    givenResultHandlers = new HashMap<String, Supplier<Result<? extends Record>>>();
    givenResultHandlers.put("DEFAULT", basicDefaultHandler);
  }

  /**
   * Default param constructor for JooqMock.
   *
   * @param defaultResult The result handler to be called if no pre-made results match what is
   * requested.
   */
  public JooqMock(Supplier<Result<? extends Record>> defaultResult) {
    this();
    givenResultHandlers.replace("DEFAULT", defaultResult);
  }

  /**
   * Constructor for JooqMock that sets the result handler for 'SELECT name ...'.
   * If name is "DEFAULT", will be used as default result handler if no results
   *  match what is requested.
   * It is recommended to use the JooqMock(Record defaultResult) constructor in that case.
   *
   * @param name Name of table to select from in 'SELECT name'.
   * @param namedResult Result handler to be called when 'SELECT name' is called.
   */
  public JooqMock(String name, Supplier<Result<? extends Record>> namedResult) {
    this();
    if (name.toUpperCase().equals("DEFAULT"))
      givenResultHandlers.replace("DEFAULT", namedResult);
    else
      givenResultHandlers.put(name.toUpperCase(), namedResult);
  }

  /**
   * Constructor for JooqMock that sets the result handlers to call.
   * If any results are named "DEFAULT", then that will be the default result.
   * If only one result exists in recordResults, it is recommended to instead use either the
   *  JooqMock(String name, Record namedResult) or JooqMock(Record defaultResult) constructors.
   *
   * @param recordResults The map of String to Record handlers, such that the desired Record
   *  handler will be called when the given String is used.
   */
  public JooqMock(Map<String, Supplier<Result<? extends Record>>> recordResults) {
    setup();
    recordResults.forEach((k, v) -> {
      if (!k.equals(k.toUpperCase())) {
        recordResults.remove(k);
        recordResults.put(k.toUpperCase(), v);
      }
    });
    givenResultHandlers = recordResults;
  }

  /**
   * Add new named record handler to givenResultHandlers.
   *
   * @param name Name of result handler to add.
   * @param record Record handler to be mapped to name.
   * @param overwrite Whether to overwrite values if they already exist.
   * @return Whether or not this operation succeeded. 0 for failed, 1 for succeeded,
   *  2 for overwritten.
   */
  public int addToRecordResults(String name, Supplier<Result<? extends Record>> record,
      boolean overwrite) {
    if (givenResultHandlers.containsKey(name.toUpperCase()) && !overwrite)
      return 0;
    else if (givenResultHandlers.containsKey(name.toUpperCase())) {
      givenResultHandlers.replace(name.toUpperCase(), record);
      return 2;
    }

    givenResultHandlers.put(name.toUpperCase(), record);
    return 1;
  }

  /**
   * Add new named record to givenResultHandlers. Will fail if name exists.
   *
   * @param name Name of result handler to add.
   * @param record Record handler to be mapped to name.
   * @return Whether or not this operation succeeded. 0 for failed, 1 for succeeded.
   */
  public int addToRecordResults(String name, Supplier<Result<? extends Record>> record) {
    return addToRecordResults(name.toUpperCase(), record, false);
  }

  /**
   * Add new named record handlers to givenResultHandlers. Will fail if name exists.
   *
   * @param newResults New result handlers to add.
   * @param overwrite Whether to overwrite values if they already exist.
   * @return A map of whether or not this operation succeeded. 0 for failed, 1 for succeeded,
   *  2 for overwritten.
   */
  public Map<String, Integer> addToRecordResults(Map<String,
      Supplier<Result<? extends Record>>> newResults,
      boolean overwrite) {
    Map<String, Integer> result = new HashMap<>();
    newResults.forEach((k, v) -> result.put(k.toUpperCase(), addToRecordResults(k, v, overwrite)));

    return result;
  }

  /**
   * Add new named record handlers to givenResultHandlers. Will fail if name exists.
   *
   * @param newResults New results to add.
   * @return A map of names to operation success. 0 for failed, 1 for succeeded.
   */
  public Map<String, Integer> addToRecordResults(Map<String,
      Supplier<Result<? extends Record>>> newResults) {
    return addToRecordResults(newResults, false);
  }

  /**
   * Remove a result handler from givenResultHandlers. Will fail if name doesn't exist.
   *
   * @param name Name of result handler to remove.
   * @return 1 if successful, 0 otherwise.
   */
  public int removeFromRecordResults(String name) {
    if (!givenResultHandlers.containsKey(name.toUpperCase()))
      return 0;
    else if (name.equals("DEFAULT"))
      givenResultHandlers.replace(name.toUpperCase(), basicDefaultHandler);
    else
      givenResultHandlers.remove(name.toUpperCase());

    return 1;
  }

  /**
   * Remove a list of result handlers from givenResultHandlers. Will fail if name doesn't exist.
   *
   * @param names A list of names of handlers to remove.
   * @return a Map of Names to operation success. 0 for failed, 1 for succeeded.
   */
  public Map<String, Integer> removeFromRecordResults(List<String> names) {
    Map<String, Integer> result = new HashMap<>();
    for (String name : names) {
      result.put(name.toUpperCase(), removeFromRecordResults(name));
    }

    return result;
  }

  /**
   * Sets the default result handlers in the defaultResultHandlers map, creates a DSLContext, and
   * sets the basicDefaultHandler handler.
   */
  private void setup() {
    context = DSL.using(SQLDialect.POSTGRES);
    setDefaultResultHandlers();
    basicDefaultHandler = () -> context.newResult();
  }

  private void setDefaultResultHandlers() {
    this.defaultResultHandlers = new HashMap<>();

    defaultResultHandlers.put("NOTE", () -> {
      Result<Record4<Integer, Integer, String, String>> result = context.newResult(NOTE.ID,
          NOTE.USER_ID, NOTE.TITLE, NOTE.BODY);
      result.add(context.newRecord(NOTE.ID, NOTE.USER_ID, NOTE.TITLE, NOTE.BODY).values(0, 0,
          "NOTE 1", "Hello World!"));
      return result;
    });
  }

  /**
   * Returns the context this class uses so that custom result handlers can be created.
   *
   * @return A mock DSLContext.
   */
  public DSLContext getContext() {
    return this.context;
  }

  /**
   * Execute a single sql statement and return result.
   *
   * @param sql Statement to execute.
   * @return MockResult requested.
   */
  private MockResult getResult(String sql) throws SQLException {
    final MockResult[] mock = new MockResult[1];

    if (sql.toUpperCase().startsWith("DROP"))
      throw new SQLException("Statement not supported: " + sql);

    givenResultHandlers.forEach((k, v) -> {
      if (k.length() >= sql.length() && k.substring(0, sql.length()).equals(sql)) {
        Result<? extends Record> result = givenResultHandlers.get(k).get();
        mock[0] = new MockResult(result.size(), result);
      }
    });

    if (mock[0] != null) {
      return mock[0];
    }
    else if (sql.toUpperCase().startsWith("SELECT")) {
      String[] stmt = sql.toUpperCase().split(" ");
      String table = "";
      boolean found = false;

      for (String s : stmt) {
        if (found) {
          table = s.toUpperCase();
          break;
        }
        if (s.toUpperCase().equals("FROM")) {
          found = true;
        }
      }

      Result<? extends Record> result;
      if (givenResultHandlers.containsKey(table))
        result = givenResultHandlers.get(table).get();

      else if (defaultResultHandlers.containsKey(table))
        result = defaultResultHandlers.get(table).get();

      else
        result = givenResultHandlers.get("DEFAULT").get();

      mock[0] = new MockResult(result.size(), result);
    }

    return mock[0];
  }

  @Override
  public MockResult[] execute(MockExecuteContext ctx) throws SQLException {
    MockResult[] mock;

    String sql = ctx.sql();

    // Will add batch capabilities eventually
//    if (ctx.batch()) {
//      String[] stmts = ctx.batchSQL();
//      mock = new MockResult[stmts.length];
//      for (int i = 0; i < stmts.length; i++) {
//        sql = stmts[i];
//        mock[i] = getResult(sql);
//      }
//    }

//    else {
      mock = new MockResult[]{ getResult(sql) };
//    }

    return mock;
  }
}
