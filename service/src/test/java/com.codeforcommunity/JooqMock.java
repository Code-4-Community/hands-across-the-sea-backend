package com.codeforcommunity;

import java.util.ArrayList;
import org.jooq.Table;
import org.jooq.TableRecord;

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

public class JooqMock implements MockDataProvider {
  // Operations mapped to the list of things to walk through
  private Map<String, Operations> recordReturns;
  // Default result to return if nothing is matched
  private Supplier<Result<? extends Record>> basicDefaultHandler;
  // DSL Context to use
  private DSLContext context;
  // Map of class names to classes
  private Map<String, Table> classMap;

  class Operations {
    private List<Supplier<UpdatableRecordImpl>> recordReturns;
    private int location = 0;
    private int callCount = 0;
    Operations(UpdatableRecordImpl record) {
      recordReturns = new ArrayList<>();
      recordReturns.add(() -> record);
    }

    void addRecord(UpdatableRecordImpl record) {
      recordReturns.add(() -> record);
    }

    TableRecord call() {
      callCount++;
      if (location + 1 == recordReturns.size()) {
        return recordReturns.get(location).get();
      }

      TableRecord record = recordReturns.get(location).get();
      location++;
      return record;
    }

    int getLocation() {
      return location;
    }

    int getCallCount() {
      return callCount;
    }
  }

  /**
   * Constructor for JooqMock.
   */
  public JooqMock() {
    MockConnection connection = new MockConnection(this);
    context = DSL.using(connection, SQLDialect.POSTGRES);
    basicDefaultHandler = () -> context.newResult();
    recordReturns = new HashMap<>();
    classMap = new HashMap<>();
    DefaultSchema schema = DefaultSchema.DEFAULT_SCHEMA;
    List<Table<?>> tables = schema.getTables();
    for (Table table : tables) {
      classMap.put(table.getName(), table);
    }
  }

  /**
   * Add record to return during a call of execute. Will return this record after
   *  returning all records that have been added prior to this.
   * The final record acts as the default record for when new records run out.
   *
   * @param operation The operation to return this for (e.g. 'SELECT', 'INSERT').
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
   * Add multiple records to return during next call of execute. Records will be returned
   *  in the order they are in the list.
   * The final record acts as the default record for when new records run out.
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
  public int timeCalled(String operation) {
    if (!recordReturns.containsKey(operation)) {
      return -1;
    }

    return recordReturns.get(operation).getCallCount();
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
    MockResult mock = new MockResult();

    if (sql.toUpperCase().startsWith("DROP"))
      throw new SQLException("Statement not supported: " + sql);

    else if (sql.toUpperCase().startsWith("SELECT")) {
      String table = sql.split("from")[1].split("\"")[1];
      Result<Record> result = context.newResult(classMap.get(table).fields());

      result.add(recordReturns.get("SELECT").call());
      mock = new MockResult(result.size(), result);
    }

    return mock;
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
