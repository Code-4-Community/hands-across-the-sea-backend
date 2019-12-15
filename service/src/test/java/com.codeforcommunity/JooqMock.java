package com.codeforcommunity;

import java.util.ArrayList;
import java.util.Arrays;
import org.jooq.Table;
import java.sql.SQLException;
import java.util.List;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.generated.DefaultSchema;
import org.jooq.impl.DSL;
import org.jooq.impl.UpdatableRecordImpl;
import org.jooq.tools.jdbc.*;
import java.lang.String;
import java.util.Map;
import java.util.HashMap;
import java.util.function.Supplier;

/**
 * A class to mock database interactions.
 */
public class JooqMock implements MockDataProvider {
  // Operations mapped to the list of things to walk through
  private Map<String, Operations> recordReturns;
  // DSL Context to use
  private DSLContext context;
  // Map of class names to classes
  private Map<String, Table> classMap;

  /**
   * A class to hold all operation handler functions and call information.
   */
  class Operations {
    // List of Supplier functions to call in order, acts as a queue for record Supplier functions
    private List<Supplier<List<UpdatableRecordImpl>>> recordReturns;
    // Current location in the recordReturns list
    private int location = 0;
    // Count of times this operation has been called
    private int callCount = 0;
    // SQL used for each call linked to each UpdatableRecordImpl returned (by position in list)
    private List<List<String>> handlerSqlCalls;
    // Bindings used for each call linked to each UpdatableRecordImpl returned
    private List<List<Object[]>> handlerSqlBindings;

    /**
     * Constructor for 'UNKNOWN' and 'DROP/CREATE' operations.
     */
    Operations() {
      recordReturns = new ArrayList<>();
      recordReturns.add(() -> null);
      handlerSqlCalls = new ArrayList<>();
      handlerSqlBindings = new ArrayList<>();
    }

    /**
     * Constructor for Operations object that takes in a record and creates a Supplier for it while
     *  initializing other class fields.
     *
     * @param record The record to be returned during the first call of this operation.
     */
    Operations(UpdatableRecordImpl record) {
      recordReturns = new ArrayList<>();
      recordReturns.add(() -> Arrays.asList(record));
      handlerSqlCalls = new ArrayList<>();
      handlerSqlBindings = new ArrayList<>();
    }

    /**
     * Constructor for operations object that takes in a List of records and creates a supplier
     *  for it while initializing other class fields.
     *
     * @param records The record to be returned during the first call of this operation.
     */
    Operations(List<UpdatableRecordImpl> records) {
      recordReturns = new ArrayList<>();
      recordReturns.add(() -> records);
      handlerSqlCalls = new ArrayList<>();
      handlerSqlBindings = new ArrayList<>();
    }

    /**
     * Constructor for operations object that takes in a record Supplier and initializes other class
     *  fields.
     *
     * @param recordFunction The first record Supplier to be called for this operation.
     */
    Operations(Supplier<List<UpdatableRecordImpl>> recordFunction) {
      recordReturns = new ArrayList<>();
      recordReturns.add(recordFunction);
      handlerSqlCalls = new ArrayList<>();
      handlerSqlBindings = new ArrayList<>();
    }

    /**
     * Add a record to the end of the record Supplier queue by creating a Supplier for the given record.
     *
     * @param record Record to be returned at the end of the queue.
     */
    private void addRecord(UpdatableRecordImpl record) {
      recordReturns.add(() -> Arrays.asList(record));
    }

    /**
     * Add a record to the end of the record Supplier queue by creating a Supplier for the given record.
     * @param records
     */
    private void addRecord(List<UpdatableRecordImpl> records) {
      recordReturns.add(() -> records);
    }

    /**
     * Add a custom record Supplier to the end of the record Supplier queue.
     *
     * @param recordFunction Record supplier to be called at the end of the queue.
     */
    private void addRecord(Supplier<List<UpdatableRecordImpl>> recordFunction) {
      recordReturns.add(recordFunction);
    }

    /**
     * Increment callCount, and call next record Supplier. If currently at the final record supplier,
     * then call the last record supplier in the queue.
     *
     * @param ctx The context supplied to execute.
     * @return TableRecord to be returned.
     */
    List<UpdatableRecordImpl> call(MockExecuteContext ctx) {
      callCount++;

      // handle creating new arrays and adding sql/bindings if first call at location in list
      if (handlerSqlCalls.size() == location) {
        handlerSqlCalls.add(new ArrayList<>(Arrays.asList(ctx.sql())));
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

      List<UpdatableRecordImpl> record = recordReturns.get(location).get();
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
     * Return the SQL strings used with each call linked to each UpdatableRecordImpl
     *  returned by position in the list.
     * @return List<List<String>> of every SQL string used.
     */
    List<List<String>> getSqlStrings() {
      return this.handlerSqlCalls;
    }

    /**
     * Return the SQL bindings used with each call linked to each UpdatableRecordImpl.
     *
     * @return List<List<Object[]>> of all SQL bindings used.
     */
    List<List<Object[]>> getSqlBindings() {
      return this.handlerSqlBindings;
    }
  }

  /**
   * Constructor for JooqMock. 'UNKNOWN' and 'DROP/CREATE' operations are added by
   * default.
   */
  public JooqMock() {
    // create DSL context
    MockConnection connection = new MockConnection(this);
    context = DSL.using(connection, SQLDialect.POSTGRES);

    // create the recordReturns object and add the 'UNKNOWN' and 'DROP/CREATE' operations
    recordReturns = new HashMap<>();
    recordReturns.put("UNKNOWN", new Operations());
    recordReturns.put("DROP/CREATE", new Operations());

    // create the classMap object and seed with database tables
    classMap = new HashMap<>();
    DefaultSchema schema = DefaultSchema.DEFAULT_SCHEMA;
    List<Table<?>> tables = schema.getTables();
    for (Table table : tables) {
      classMap.put(table.getName(), table);
    }
  }

  /**
   * Add record to return during a call of execute. Will return this record after
   *  returning all record (functions) that have been added prior to this.
   * The final record acts as the default record for when new records run out.
   *
   * The 'UNKNOWN' and 'DROP/CREATE' operations are added by default since they
   *  return nothing.
   *
   * @param operation The operation to return this for (e.g. 'SELECT', 'INSERT', ...).
   * @param record The record to return
   */
  public void addReturn(String operation, UpdatableRecordImpl record) {
    if (!recordReturns.containsKey(operation)) {
      recordReturns.put(operation, new Operations(record));
      return;
    }
    recordReturns.get(operation).addRecord(record);
  }

/**
 * Add List of records to return during a call of execute. Will return this list after
 *  returning all record (functions) that have been added prior to this.
 * The final record acts as the default record for when new records run out.
 *
 * The 'UNKNOWN' and 'DROP/CREATE' operations are added by default since they
 *  return nothing.
 *
 * @param operation The operation to return this for (e.g. 'SELECT', 'INSERT', ...).
 * @param records The List of records to return
 */
  public void addReturn(String operation, List<UpdatableRecordImpl> records) {
    if (!recordReturns.containsKey(operation)) {
      recordReturns.put(operation, new Operations(records));
      return;
    }
    recordReturns.get(operation).addRecord(records);
  }

  /**
   * Add custom record return function to run during call of execute. Will return record the
   *  supplier supplies after returning all record (functions) that have been added prior to this.
   * The final record acts as the default record for when new records run out.
   *
   * The 'UNKNOWN' and 'DROP/CREATE' operations are added by default since they
   *  return nothing.
   *
   * @param operation THe operation to run this for (e.g. 'SELECT', 'INSERT'...).
   * @param recordFunction The function to run when execute is called.
   */
  public void addReturn(String operation, Supplier<List<UpdatableRecordImpl>> recordFunction) {
    if (!recordReturns.containsKey(operation)) {
      recordReturns.put(operation, new Operations(recordFunction));
      return;
    }
    recordReturns.get(operation).addRecord(recordFunction);
  }

  /**
   * Add multiple records to return during next call of execute. Records will be returned
   *  in the order they are in the list.
   * The final record acts as the default record for when new records run out.
   *
   * The 'UNKNOWN' and 'DROP/CREATE' operations are added by default since they
   *  return nothing.
   *
   * @param records A map of operations to records to be returned.
   */
  public void addReturn(Map<String, List<UpdatableRecordImpl>> records) {
    records.forEach((k, v) -> {
      for (UpdatableRecordImpl record : v) {
        addReturn(k, record);
      }
    });
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
   * Combines everything in the List<List<String>> into one list to be what each Operation's name
   *  is mapped to.
   *
   * @return Map of each Operation to each SQL statement used
   */
  public Map<String, List<String>> getSqlStrings() {
    Map<String, List<String>> result = new HashMap<>();
    recordReturns.forEach((k, v) -> {
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
   *  is mapped to.
   *
   * @return Map of each Operation to each SQL binding used
   */
  public Map<String, List<Object[]>> getSqlBindings() {
    Map<String, List<Object[]>> result = new HashMap<>();
    recordReturns.forEach((k, v) -> {
      List<Object[]> opResult = new ArrayList<>();
      for (List<Object[]> list : v.getSqlBindings()) {
        opResult.addAll(list);
      }
      result.put(k, opResult);
    });
    return result;
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

    Result<Record> result = context.newResult(classMap.get(table).fields());
    // catch and rethrow exception if return not primed
    try {
      result.addAll(recordReturns.get(operation).call(ctx));
    }
    catch (NullPointerException e) {
      throw new IllegalStateException("You probably forgot to prime your "
          + "JooqMock by calling addReturn (with one of SELECT/INSERT/UPDATE/DELETE as "
          + "your operation.");
    }
    return new MockResult(result.size(), result);
  }

  @Override
  public MockResult[] execute(MockExecuteContext ctx) throws SQLException {
    MockResult[] mock;
    mock = new MockResult[]{ getResult(ctx) };

    return mock;
  }
}
