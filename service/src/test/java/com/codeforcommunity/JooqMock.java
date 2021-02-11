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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
 * @version 1.1
 */
public class JooqMock implements MockDataProvider {
  private static final Logger log = LogManager.getLogger(JooqMock.class);
  // Operations mapped to the list of things to walk through
  private final Map<OperationType, Operations> recordReturns;
  // Spy DSL Context to use
  private final DSLContext context;
  // Map of class names to classes
  private final Map<String, Table<?>> classMap;
  // the id to give a table object
  private int id;

  /** A class to hold all operation handler functions and call information. */
  private class Operations {
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

  /**
   * Represents an operation type in SQL. For all explicitly unsupported types you want to use, the
   * {@link OperationType#UNKNOWN} type acts as a catchall.
   */
  public enum OperationType {
    SELECT,
    UPDATE,
    EXISTS,
    DELETE,
    CREATE,
    DROP,
    INSERT,
    UNKNOWN;

    public static OperationType parse(String value) {
      try {
        OperationType type = OperationType.valueOf(value.toUpperCase());
        log.warn(
            "Please use an OperationType rather than passing in a string for the SQL operation");
        return type;
      } catch (IllegalArgumentException e) {
        log.fatal("Error trying to parse operation type, must be convertible to an OperationType");
        throw e;
      }
    }
  }

  /**
   * Constructor for JooqMock. 'UNKNOWN' and 'DROP'/'CREATE' operations are added by default.
   * Mockito is also used to provide an id for a record automatically when it's inserted. The next
   * id can be retrieved through the getId() method, and it increases sequentially.
   */
  public JooqMock() {
    // create DSL context
    MockConnection connection = new MockConnection(this);
    // Actual DSL context
    DSLContext raw_context = DSL.using(connection, SQLDialect.POSTGRES);
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
    recordReturns.put(OperationType.UNKNOWN, new Operations());
    recordReturns.put(OperationType.DROP, new Operations());
    recordReturns.put(OperationType.CREATE, new Operations());

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
   * @deprecated in favor of the StatementType version
   */
  @Deprecated
  public void addReturn(String operation, Record record) {
    this.addSafeReturn(OperationType.valueOf(operation), record);
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
  public void addReturn(OperationType operation, Record record) {
    if (record == null) {
      log.warn("addEmptyReturn() is preferred to adding a null record with addReturn()");
    }

    this.addSafeReturn(operation, record);
  }

  /** A method for adding records that doesn't log empty return if not required. */
  private void addSafeReturn(OperationType operation, Record record) {
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
   * @deprecated in favor of the StatementType version
   */
  @Deprecated
  public void addReturn(String operation, List<? extends Record> records) {
    this.addReturn(OperationType.parse(operation), records);
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
  public void addReturn(OperationType operation, List<? extends Record> records) {
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
   * @deprecated in favor of the StatementType version
   */
  @Deprecated
  public void addReturn(String operation, Supplier<Result<? extends Record>> recordFunction) {
    this.addReturn(OperationType.parse(operation), recordFunction);
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
  public void addReturn(
      OperationType operation, Supplier<Result<? extends Record>> recordFunction) {
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
  public void addReturn(Map<OperationType, List<? extends Record>> records) {
    records.forEach(
        (k, v) -> {
          for (Record record : v) {
            addSafeReturn(k, record);
          }
        });
  }

  /**
   * Adds an empty return to the last position for the current value.
   *
   * @param operation the operation to add the empty return to.
   * @deprecated in favor of the StatementType version
   */
  @Deprecated
  public void addEmptyReturn(String operation) {
    this.addEmptyReturn(OperationType.parse(operation));
  }

  /**
   * Adds an empty return to the last position for the current value.
   *
   * @param operation the operation to add the empty return to.
   */
  public void addEmptyReturn(OperationType operation) {
    addReturn(operation, Collections.emptyList());
  }

  /**
   * Adds a return for an {@link OperationType#EXISTS} operation. Gets a table that exists and
   * returns a valid record for it.
   *
   * @param returnTrue Whether or not the exists query should return true or false
   */
  public void addExistsReturn(boolean returnTrue) {
    List<Record> records = new ArrayList<>();
    if (returnTrue) {
      records.add(context.newRecord((Table<?>) classMap.values().toArray()[0]));
    }

    addReturn(OperationType.EXISTS, records);
  }

  /**
   * Return count of times operation was called.
   *
   * @param operation Operation to get value for.
   * @return Count of times operation was called or -1 if key does not exist.
   * @deprecated in favor of the StatementType version
   */
  @Deprecated
  public int timesCalled(String operation) {
    OperationType type = OperationType.parse(operation);
    if (!recordReturns.containsKey(type)) {
      return -1;
    }

    return recordReturns.get(type).getCallCount();
  }

  /**
   * Return count of times operation was called.
   *
   * @param operation Operation to get value for.
   * @return Count of times operation was called or -1 if key does not exist.
   */
  public int timesCalled(OperationType operation) {
    if (!recordReturns.containsKey(operation)) {
      return -1;
    }

    return recordReturns.get(operation).getCallCount();
  }

  /**
   * Combines everything in the List<List<String>> into one list to be what each Operation's name is
   * mapped to.
   *
   * @return Map of each Operation to each SQL operation used
   * @deprecated Use {@link JooqMock#getSqlOperationStrings()}
   */
  @Deprecated
  public Map<String, List<String>> getSqlStrings() {
    Map<String, List<String>> result = new HashMap<>();
    recordReturns.forEach(
        (k, v) -> {
          List<String> opResult = new ArrayList<>();
          for (List<String> list : v.getSqlStrings()) {
            opResult.addAll(list);
          }
          result.put(k.toString(), opResult);
        });
    return result;
  }

  /**
   * Combines everything in the List<List<String>> into one list to be what each {@link
   * OperationType}'s name is mapped to.
   *
   * @return Map of each Operation to each SQL operation used
   */
  public Map<OperationType, List<String>> getSqlOperationStrings() {
    Map<OperationType, List<String>> result = new HashMap<>();
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
   * @deprecated in favor of {@link JooqMock#getSqlOperationBindings()} or {@link
   *     JooqMock#getRawSqlBindings()}
   */
  public Map<String, List<Object[]>> getSqlBindings() {
    Map<String, List<Object[]>> result = new HashMap<>();
    recordReturns.forEach(
        (k, v) -> {
          List<Object[]> opResult = new ArrayList<>();
          for (List<Object[]> list : v.getSqlBindings()) {
            opResult.addAll(list);
          }
          result.put(k.toString(), opResult);
        });
    return result;
  }

  /**
   * Combines everything in the List<List<Object[]>> into one list to be what each Operation's name
   * is mapped to. See {@link JooqMock#getRawSqlBindings()} for inspecting the bindings used at each
   * call (which only really matters when an individual return is used multiple times).
   *
   * @return List of each binding used with the operation.
   */
  public Map<OperationType, List<Object[]>> getSqlOperationBindings() {
    Map<OperationType, List<Object[]>> result = new HashMap<>();
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
   * Same as {@link JooqMock#getSqlOperationBindings()}, but also allows for inspecting bindings
   * used at each call. Only differs from {@code getSqlBindings(OperationType)} when an individual
   * return is used multiple times.
   *
   * @return List of list of each binding used with the operation.
   */
  public Map<OperationType, List<List<Object[]>>> getRawSqlBindings() {
    Map<OperationType, List<List<Object[]>>> result = new HashMap<>();
    recordReturns.forEach(
        (k, v) -> {
          List<List<Object[]>> opResult = v.getSqlBindings();
          result.put(k, opResult);
        });
    return result;
  }

  /**
   * Returns the ID of the next insertion. Insertions occur sequentially.
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
   * Execute a single sql operation and return result.
   *
   * @param ctx Mock context supplied to execute.
   * @return MockResult requested.
   */
  private MockResult getResult(MockExecuteContext ctx) {
    String sql = ctx.sql();
    OperationType operation;

    if (sql.toUpperCase().startsWith("DROP")) {
      Result<Record> result = context.newResult();
      recordReturns.get(OperationType.DROP).call(ctx);
      return new MockResult(result.size(), result);
    }
    if (sql.toUpperCase().startsWith("CREATE")) {
      Result<Record> result = context.newResult();
      recordReturns.get(OperationType.CREATE).call(ctx);
      return new MockResult(result.size(), result);
    }
    if (sql.toUpperCase().contains("WHERE EXISTS")) {
      operation = OperationType.EXISTS;
    } else if (sql.toUpperCase().startsWith("SELECT")) {
      operation = OperationType.SELECT;
    } else if (sql.toUpperCase().startsWith("INSERT")) {
      operation = OperationType.INSERT;
    } else if (sql.toUpperCase().startsWith("UPDATE")) {
      operation = OperationType.UPDATE;
    } else if (sql.toUpperCase().startsWith("DELETE")) {
      operation = OperationType.DELETE;
    } else {
      Result<Record> result = context.newResult();
      log.info("Unknown operation encountered, adding to UNKNOWN StatementType: {}", ctx.sql());
      recordReturns.get(OperationType.UNKNOWN).call(ctx);
      return new MockResult(result.size(), result);
    }

    Result<? extends Record> result;
    // catch and rethrow exception if return not primed
    try {
      result = recordReturns.get(operation).call(ctx);
    } catch (NullPointerException e) {
      log.warn(
          "WARNING: JooqMock could not find a primed result for the given operation,"
              + "so an empty result is being returned. Provided SQL string was '{}'",
          ctx.sql());
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
