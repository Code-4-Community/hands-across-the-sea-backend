package com.codeforcommunity.processor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.codeforcommunity.JooqMock;
import com.codeforcommunity.dto.notes.ContentNote;
import com.codeforcommunity.dto.notes.FullNote;
import java.util.ArrayList;
import java.util.List;
import org.jooq.DSLContext;
import org.jooq.generated.Tables;
import org.jooq.generated.tables.Note;
import org.jooq.generated.tables.records.NoteRecord;
import org.jooq.impl.UpdatableRecordImpl;
import org.junit.jupiter.api.Test;

/**
 * A class for testing the ProcessorImpl.
 */
public class ProcessorImplTest {
  // the JooqMock to use for testing
  JooqMock mockDb;
  // the ProcessorImpl to use for testing
  NotesProcessorImpl processor;

  /**
   * Method to setup mockDb and processor.
   */
  void setup() {
    mockDb = new JooqMock();
    processor = new NotesProcessorImpl(mockDb.getContext());
  }

  /**
   * Test the getAllNotes method.
   */
  @Test
  public void testGetAllNotes() {
    setup();

    // prime mockDb by adding list of returns
    List<UpdatableRecordImpl> list = new ArrayList<>();
    for (int i = 0; i < 5; i++) {
      NoteRecord n = new NoteRecord();
      n.setId(i);
      n.setTitle("Note" + i);
      n.setBody("THIS IS A NOTE");

      list.add(n);
    }
    mockDb.addReturn("SELECT", list);

    // further prime mockDb by adding a later return
    NoteRecord n = new NoteRecord();
    n.setId(11);
    n.setTitle("NoTe11");
    n.setBody("nOtE11");
    mockDb.addReturn("SELECT", n);

    // test getAllNotes method for first set of notes
    List<FullNote> notes = processor.getAllNotes();
    for (int i = 0; i < 5; i++) {
      assertEquals(i, notes.get(i).getId());
      assertEquals("Note" + i, notes.get(i).getTitle());
      assertEquals("THIS IS A NOTE", notes.get(i).getContent());
      assertEquals("10/20/2019", notes.get(i).getDate());
    }

    // test getAllNotes method for last note
    notes = processor.getAllNotes();
    assertEquals(11, notes.get(0).getId());
    assertEquals("NoTe11", notes.get(0).getTitle());
    assertEquals("nOtE11", notes.get(0).getContent());
    assertEquals("10/20/2019", notes.get(0).getDate());

    // check that SELECT has expected amount of times called
    assertEquals(2, mockDb.timesCalled("SELECT"));
  }

  /**
   * Test the getANote method.
   */
  @Test
  public void testGetANote() {
    setup();

    // prime database with return
    NoteRecord n = new NoteRecord();
    n.setId(5);
    n.setTitle("TITLE");
    n.setBody("SET BODY");
    mockDb.addReturn("SELECT", n);

    // test getANote
    FullNote note = processor.getANote(0);
    assertEquals(5, note.getId());
    assertEquals(1, mockDb.timesCalled("SELECT"));

    String sql = mockDb.getSqlStrings().get("SELECT").get(0);
    Object[] bindings = mockDb.getSqlBindings().get("SELECT").get(0);

    // certify count of bindings and sql statement
    assertEquals(1, bindings.length);
    assertTrue(sql.contains("select")
        && sql.contains("from \"note\" where \"note\".\"id\" = ?"));
    assertEquals(0, bindings[0]);
    assertEquals(1, mockDb.timesCalled("SELECT"));
  }

  /**
   * Test inserting notes with the mockDb.
   */
  @Test
  public void testInsertNotes() {
    setup();
    DSLContext ctx = mockDb.getContext();

    // prime database for INSERT call
    NoteRecord primer = ctx.newRecord(Tables.NOTE);
    primer.setId(0);
    mockDb.addReturn("INSERT", primer);

    // create test record to insert
    NoteRecord record = ctx.newRecord(Tables.NOTE);
    record.setTitle("Hello");
    record.setBody("World");

    // call store and check to see if it worked
    assertEquals(1, record.store());

    // test attributes about database
    assertEquals(1, mockDb.timesCalled("INSERT"));
    String sql = mockDb.getSqlStrings().get("INSERT").get(0);
    Object[] bindings = mockDb.getSqlBindings().get("INSERT").get(0);
    assertEquals("insert into \"note\" (\"title\", \"body\") values (?, ?) returning "
        + "\"note\".\"id\"", sql);
    assertEquals(2, bindings.length);
    assertEquals("Hello", bindings[0]);
    assertEquals("World", bindings[1]);
  }

  /**
   * Test the createNotes method.
   */
  @Test
  public void testCreateNotes() {
    setup();
    DSLContext ctx = mockDb.getContext();

    // prime database and create test records
    List<ContentNote> notes = new ArrayList<>();
    for (int i = 0; i < 3; i++) {
      // test records
      ContentNote n = new ContentNote("Note" + i, "HELLO WORLD");
      notes.add(n);

      // primers
      NoteRecord primer = ctx.newRecord(Tables.NOTE);
      primer.setId(i);
      mockDb.addReturn("INSERT", primer);
    }

    // check attributes about INSERT
    assertEquals(0, mockDb.timesCalled("INSERT"));
    List<FullNote> returnNotes = processor.createNotes(notes);
    assertEquals(3, mockDb.timesCalled("INSERT"));
    for (int i = 0; i < 3; i++) {
      String sql = mockDb.getSqlStrings().get("INSERT").get(i);
      Object[] bindings = mockDb.getSqlBindings().get("INSERT").get(i);

      assertEquals(2, bindings.length);
      assertEquals("insert into \"note\" (\"title\", \"body\") values (?, ?) returning "
        + "\"note\".\"id\"", sql);
      assertEquals("Note" + i, bindings[0]);
      assertEquals("HELLO WORLD", bindings[1]);
      assertEquals(i, returnNotes.get(i).getId());
    }
  }

  /**
   * Test the updateNote method.
   */
  @Test
  public void testUpdateNote() {
    setup();

    // prime database for SELECT and UPDATE
    NoteRecord primer = new NoteRecord();
    primer.setId(0);
    primer.setTitle("TITLE");
    primer.setBody("SET BODY");
    mockDb.addReturn("SELECT", primer);
    mockDb.addReturn("UPDATE", primer);

    // create newer version of note (note should be updated to this)
    ContentNote n = new ContentNote("TITLE", "SET BODY");
    FullNote returnNote = processor.updateNote(0, n);

    // check database attributes
    assertEquals(-1, mockDb.timesCalled("INSERT"));
    assertEquals(1, mockDb.timesCalled("UPDATE"));
    assertEquals(1, mockDb.timesCalled("SELECT"));
    assertEquals("TITLE", returnNote.getTitle());
    assertEquals("SET BODY", returnNote.getContent());
    String sql = mockDb.getSqlStrings().get("UPDATE").get(0);
    Object[] bindings = mockDb.getSqlBindings().get("UPDATE").get(0);

    assertEquals(3, bindings.length);
    assertEquals("update \"note\" set \"title\" = ?, \"body\" = ? where \"note\""
        + ".\"id\" = ?", sql);
    assertEquals("TITLE", bindings[0]);
    assertEquals("SET BODY", bindings[1]);
    assertEquals(0, bindings[2]);
  }

  /**
   * Test the deleteNote method.
   */
  @Test
  public void deleteNote() {
    setup();

    // prime DELETE call
    NoteRecord primer = new NoteRecord();
    primer.setId(0);
    primer.setTitle("TITLE");
    primer.setBody("SET BODY");
    mockDb.addReturn("DELETE", primer);

    // test database attributes
    processor.deleteNote(0);
    assertEquals(1, mockDb.timesCalled("DELETE"));
    assertEquals(-1, mockDb.timesCalled("SELECT"));
    assertEquals(-1, mockDb.timesCalled("INSERT"));
    assertEquals(-1, mockDb.timesCalled("UPDATE"));
    String sql = mockDb.getSqlStrings().get("DELETE").get(0);
    Object[] bindings = mockDb.getSqlBindings().get("DELETE").get(0);
    assertEquals("delete from \"note\" where \"note\".\"id\" = ?", sql);
    assertEquals(1, bindings.length);
    assertEquals(0, bindings[0]);
  }

  /**
   * Test calling select without priming mock db.
   */
  @Test
  public void testUnprimedSelect() {
    setup();
    DSLContext ctx = mockDb.getContext();

    Exception e = assertThrows(IllegalStateException.class, () ->
      ctx.selectFrom(Tables.NOTE).where(Tables.NOTE.ID.eq(0)).fetchOneInto(Note.class));
    assertEquals("You probably forgot to prime your "
          + "JooqMock by calling addReturn (with one of SELECT/INSERT/UPDATE/DELETE as "
          + "your operation.", e.getMessage());
  }

  /**
   * Test calling insert without priming mock db.
   */
  @Test
  public void testUnprimedInsert() {
    setup();
    DSLContext ctx = mockDb.getContext();

    Exception e = assertThrows(IllegalStateException.class, () -> {
      NoteRecord record = ctx.newRecord(Tables.NOTE);
      record.setTitle("Hello");
      record.setBody("World");
      record.store();
    });
    assertEquals("You probably forgot to prime your "
        + "JooqMock by calling addReturn (with one of SELECT/INSERT/UPDATE/DELETE as "
        + "your operation.", e.getMessage());
  }

  /**
   * Test calling update without priming mock db.
   */
  @Test
  public void testUnprimedUpdate() {
    setup();
    DSLContext ctx = mockDb.getContext();

    // prime database for SELECT
    NoteRecord primer = new NoteRecord();
    primer.setId(0);
    primer.setTitle("TITLE");
    primer.setBody("SET BODY");
    mockDb.addReturn("SELECT", primer);

    Exception e = assertThrows(IllegalStateException.class, () -> {
      NoteRecord update = ctx.fetchOne(Tables.NOTE, Tables.NOTE.ID.eq(0));
      update.setTitle("Hello");
      update.store();
    });
    assertEquals("You probably forgot to prime your "
        + "JooqMock by calling addReturn (with one of SELECT/INSERT/UPDATE/DELETE as "
        + "your operation.", e.getMessage());
  }

  /**
   * Test calling delete without priming mock db.
   */
  @Test
  public void testUnprimedDelete() {
    setup();
    DSLContext ctx = mockDb.getContext();

    Exception e = assertThrows(IllegalStateException.class, () ->
      ctx.deleteFrom(Tables.NOTE).where(Tables.NOTE.ID.eq(0)).execute());
    assertEquals("You probably forgot to prime your "
        + "JooqMock by calling addReturn (with one of SELECT/INSERT/UPDATE/DELETE as "
        + "your operation.", e.getMessage());
  }
}
