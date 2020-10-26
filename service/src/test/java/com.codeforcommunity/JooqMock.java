package com.codeforcommunity;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.spy;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.Table;
import org.jooq.generated.DefaultSchema;
import org.jooq.impl.DSL;
import org.jooq.tools.jdbc.MockConnection;
import org.jooq.tools.jdbc.MockDataProvider;
import org.jooq.tools.jdbc.MockExecuteContext;
import org.jooq.tools.jdbc.MockResult;

/**
 * A class to mock database interactions.
 *
 * @author Conner Nilsen
 */
public class JooqMock implements MockDataProvider {
  // Operations mapped to the list of things to walk through
  private final Map<String, Operations> recordReturns;
  // Actual DSL context
  private final DSLContext raw_context;
  // Spy DSL Context to use
  private final DSLContext context;
  // Map of class names to classes
  private final Map<String, Table> classMap;
  // the id to give a table object
  private int id;

  /** A class to hold all operation handler functions and call information. */
  class Operations {
    // List of Supplier functions to call in order, acts as a queue for record Supplier functions
    private final List<Supplier<Result<? extends Record>>> recordReturns;
    // Current location in the recordReturns list
    private int location = 0;
    // Count of times this operation has been called
    private int callCount = 0;
    // SQL used for each call linked to each Record returned (by position in list)
    private final List<List<String>> handlerSqlCalls;
    // Bindings used for each call linked to each Record returned
    private final List<List<Object[]>> handlerSqlBindings;

    /** Constructor for 'UNKNOWN' and 'DROP/CREATE' operations. */
    Operations() {
      this(() -> null);
    }

    /**
     * Constructor for Operations object that takes in a record and creates a Supplier for it while
     * initializing other class fields.
     *
     * @param record The record to be returned during the first call of this operation.
     */
    Operations(Record record) {
      this(() -> createResult(record));
    }

    /**
     * Constructor for operations object that takes in a List of records and creates a supplier for
     * it while initializing other class fields.
     *
     * @param records The record to be returned during the first call of this operation.
     */
    Operations(List<? extends Record> records) {
      this(() -> createResult(records));
    }

    /**
     * Constructor for operations object that takes in a record Supplier and initializes other class
     * fields.
     *
     * @param recordFunction The first record Supplier to be called for this operation.
     */
    Operations(Supplier<Result<? extends Record>> recordFunction) {
      recordReturns = new ArrayList<>();
      recordReturns.add(recordFunction);
      handlerSqlCalls = new ArrayList<>();
      handlerSqlBindings = new ArrayList<>();
    }

    /**
     * Add a record to the end of the record Supplier queue by creating a Supplier for the given
     * record.
     *
     * @param record Record to be returned at the end of the queue.
     */
    private void addRecord(Record record) {
      recordReturns.add(() -> createResult(record));
    }

    /**
     * Add a record to the end of the record Supplier queue by creating a Supplier for the given
     * record.
     *
     * @param records list of Record to be returned at the end of the queue.
     */
    private void addRecord(List<? extends Record> records) {
      recordReturns.add(() -> createResult(records));
    }

    /**
     * Add a custom record Supplier to the end of the record Supplier queue.
     *
     * @param recordFunction Record supplier to be called at the end of the queue.
     */
    private void addRecord(Supplier<Result<? extends Record>> recordFunction) {
      recordReturns.add(recordFunction);
    }

    /** Adds an empty return to the end of the Supplier queue. */
    private void addEmptyReturn() {
      recordReturns.add(context::newResult);
    }

    /**
     * Increment callCount, and call next record Supplier. If currently at the final record
     * supplier, then call the last record supplier in the queue.
     *
     * @param ctx The context supplied to execute.
     * @return TableRecord to be returned.
     */
    Result<? extends Record> call(MockExecuteContext ctx) {
      callCount++;

      // handle creating new arrays and adding sql/bindings if first call at location in list
      if (handlerSqlCalls.size() == location) {
        handlerSqlCalls.add(new ArrayList<>(Collections.singletonList(ctx.sql())));
        List<Object[]> objects = new ArrayList<>();
        objects.add(ctx.bindings());
        handlerSqlBindings.add(objects);
      }
      // handle adding sql/bindings if not first call for this position
      else {
        handlerSqlCalls.get(location).add(ctx.sql());
        handlerSqlBindings.get(location).add(ctx.bindings());
      }

      // if at end of list, repeatedly return the same thing every time called
      if (location + 1 == recordReturns.size()) {
        return recordReturns.get(location).get();
      }

      Result<? extends Record> record = recordReturns.get(location).get();
      location++;
      return record;
    }

    /**
     * Return the current location in the record Supplier queue.
     *
     * @return Int representing the current location.
     */
    int getLocation() {
      return location;
    }

    /**
     * Return the current call count for this operation.
     *
     * @return Int representing the current call count.
     */
    int getCallCount() {
      return callCount;
    }

    /**
     * Return the SQL strings used with each call linked to each Record returned by position in the
     * list.
     *
     * @return List<List<String>> of every SQL string used.
     */
    List<List<String>> getSqlStrings() {
      return this.handlerSqlCalls;
    }

    /**
     * Return the SQL bindings used with each call linked to each Record.
     *
     * @return List<List<Object[]>> of all SQL bindings used.
     */
    List<List<Object[]>> getSqlBindings() {
      return this.handlerSqlBindings;
    }
  }

  /** Constructor for JooqMock. 'UNKNOWN' and 'DROP/CREATE' operations are added by default. */
  public JooqMock() {
    // create DSL context
    MockConnection connection = new MockConnection(this);
    raw_context = DSL.using(connection, SQLDialect.POSTGRES);
    context = spy(raw_context);
    id = 1;

    // Sets the id of an object being inserted
    doAnswer(
            invocation -> {
              Object object = invocation.callRealMethod();
              if (object instanceof Record) {
                Record record = (Record) object;
                Field<?> field = record.field("id");
                if (field != null) {
                  Field<Integer> itemId = field.coerce(Integer.class);
                  record.set(itemId, id);
                }
              }
              id++;
              return object;
            })
        .when(context)
        .newRecord(any(Table.class));

    // create the recordReturns object and add the 'UNKNOWN' and 'DROP/CREATE' operations
    recordReturns = new HashMap<>();
    recordReturns.put("UNKNOWN", new Operations());
    recordReturns.put("DROP/CREATE", new Operations());

    // create the classMap object and seed with database tables
    classMap = new HashMap<>();
    DefaultSchema schema = DefaultSchema.DEFAULT_SCHEMA;
    List<Table<?>> tables = schema.getTables();
    for (Table<?> table : tables) {
      classMap.put(table.getName(), table);
    }
  }

  /**
   * Creates a result from a given record.
   *
   * @param r the Record to create a result for.
   * @return the result for the given Record.
   */
  private Result<? extends Record> createResult(Record r) {
    if (r == null) {
      return context.newResult();
    }
    Result<Record> res = context.newResult(r.fields());
    res.add(r);
    return res;
  }

  /**
   * Creates a result from a given record.
   *
   * @param r the List of Records to create a result for.
   * @return the result for the given Records.
   */
  private Result<? extends Record> createResult(List<? extends Record> r) {
    if (r.parallelStream().anyMatch(Objects::isNull)) {
      throw new IllegalArgumentException(
          "Record in provided list was null. No records " + "should be null in a list of returns.");
    }
    if (r.size() == 0) {
      return context.newResult();
    }
    Result<Record> res = context.newResult(r.get(0).fields());
    res.addAll(r);
    return res;
  }

  /**
   * Add record to return during a call of execute. Will return this record after returning all
   * record (functions) that have been added prior to this. The final record acts as the default
   * record for when new records run out.
   *
   * <p>The 'UNKNOWN' and 'DROP/CREATE' operations are added by default since they return nothing.
   *
   * @param operation The operation to return this for (e.g. 'SELECT', 'INSERT', ...).
   * @param record The record to return
   */
  public void addReturn(String operation, Record record) {
    if (record == null && !recordReturns.containsKey(operation)) {
      recordReturns.put(operation, new Operations(context::newResult));
    }
    if (!recordReturns.containsKey(operation)) {
      recordReturns.put(operation, new Operations(record));
      return;
    }
    recordReturns.get(operation).addRecord(record);
  }

  /**
   * Add List of records to return during a call of execute. Will return this list after returning
   * all record (functions) that have been added prior to this. The final record acts as the default
   * record for when new records run out.
   *
   * <p>The 'UNKNOWN' and 'DROP/CREATE' operations are added by default since they return nothing.
   *
   * @param operation The operation to return this for (e.g. 'SELECT', 'INSERT', ...).
   * @param records The List of records to return
   */
  public void addReturn(String operation, List<? extends Record> records) {
    if (!recordReturns.containsKey(operation)) {
      recordReturns.put(operation, new Operations(records));
      return;
    }
    recordReturns.get(operation).addRecord(records);
  }

  /**
   * Add custom record return function to run during call of execute. Will return record the
   * supplier supplies after returning all record (functions) that have been added prior to this.
   * The final record acts as the default record for when new records run out.
   *
   * <p>The 'UNKNOWN' and 'DROP/CREATE' operations are added by default since they return nothing.
   *
   * @param operation THe operation to run this for (e.g. 'SELECT', 'INSERT'...).
   * @param recordFunction The function to run when execute is called.
   */
  public void addReturn(String operation, Supplier<Result<? extends Record>> recordFunction) {
    if (!recordReturns.containsKey(operation)) {
      recordReturns.put(operation, new Operations(recordFunction));
      return;
    }
    recordReturns.get(operation).addRecord(recordFunction);
  }

  /**
   * Add multiple records to return during next call of execute. Records will be returned in the
   * order they are in the list. The final record acts as the default record for when new records
   * run out.
   *
   * <p>The 'UNKNOWN' and 'DROP/CREATE' operations are added by default since they return nothing.
   *
   * @param records A map of operations to records to be returned.
   */
  public void addReturn(Map<String, List<? extends Record>> records) {
    records.forEach(
        (k, v) -> {
          for (Record record : v) {
            addReturn(k, record);
          }
        });
  }

  /**
   * Adds an empty return to the last position for the current value.
   *
   * @param operation the operation to add the empty return to.
   */
  public void addEmptyReturn(String operation) {
    addReturn(operation, Collections.emptyList());
  }

  /**
   * Return count of times operation was called.
   *
   * @param operation Operation to get value for.
   * @return Count of times operation was called or -1 if key does not exist.
   */
  public int timesCalled(String operation) {
    if (!recordReturns.containsKey(operation)) {
      return -1;
    }

    return recordReturns.get(operation).getCallCount();
  }

  /**
   * Combines everything in the List<List<String>> into one list to be what each Operation's name is
   * mapped to.
   *
   * @return Map of each Operation to each SQL statement used
   */
  public Map<String, List<String>> getSqlStrings() {
    Map<String, List<String>> result = new HashMap<>();
    recordReturns.forEach(
        (k, v) -> {
          List<String> opResult = new ArrayList<>();
          for (List<String> list : v.getSqlStrings()) {
            opResult.addAll(list);
          }
          result.put(k, opResult);
        });
    return result;
  }

  /**
   * Combines everything in the List<List<Object[]>> into one list to be what each Operation's name
   * is mapped to.
   *
   * @return Map of each Operation to each SQL binding used
   */
  public Map<String, List<Object[]>> getSqlBindings() {
    Map<String, List<Object[]>> result = new HashMap<>();
    recordReturns.forEach(
        (k, v) -> {
          List<Object[]> opResult = new ArrayList<>();
          for (List<Object[]> list : v.getSqlBindings()) {
            opResult.addAll(list);
          }
          result.put(k, opResult);
        });
    return result;
  }

  /**
   * Returns the ID of the next insertion.
   *
   * @return an integer representing the ID.
   */
  public int getId() {
    return id;
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
   * @param ctx Mock context supplied to execute.
   * @return MockResult requested.
   */
  private MockResult getResult(MockExecuteContext ctx) throws SQLException {
    String sql = ctx.sql();
    String operation;
    String table;

    // Handle 'DROP' and 'CREATE' statements
    if (sql.toUpperCase().startsWith("DROP") || sql.toUpperCase().startsWith("CREATE")) {
      Result<Record> result = context.newResult();
      recordReturns.get("DROP/CREATE").call(ctx);
      return new MockResult(result.size(), result);
    }

    // Handle 'SELECT' statements
    if (sql.toUpperCase().startsWith("SELECT")) {
      table = sql.split("from")[1].split("\"")[1];
      operation = "SELECT";
    }

    // Handle 'INSERT' statements
    else if (sql.toUpperCase().startsWith("INSERT")) {
      table = sql.split("into")[1].split("\"")[1];
      operation = "INSERT";
    }

    // Handle 'UPDATE' statements
    else if (sql.toUpperCase().startsWith("UPDATE")) {
      table = sql.split("\"")[1];
      operation = "UPDATE";
    }

    // Handle 'DELETE' statements
    else if (sql.toUpperCase().startsWith("DELETE")) {
      table = sql.split("from")[1].split("\"")[1];
      operation = "DELETE";
    }

    // Handle 'UNKNOWN' statements
    else {
      Result<Record> result = context.newResult();
      recordReturns.get("UNKNOWN").call(ctx);
      return new MockResult(result.size(), result);
    }

    Result<? extends Record> result;
    // catch and rethrow exception if return not primed
    try {
      result = recordReturns.get(operation).call(ctx);
    } catch (NullPointerException e) {
      System.out.println(
          "WARNING: JooqMock could not find a primed result for the given operation,"
              + "so an empty result is being returned. Provided SQL string was '"
              + ctx.sql()
              + "'");
      result = context.newResult();
    }
    return new MockResult(result.size(), result);
  }

  @Override
  public MockResult[] execute(MockExecuteContext ctx) throws SQLException {
    MockResult[] mock;
    mock = new MockResult[] {getResult(ctx)};

    return mock;
  }
}
