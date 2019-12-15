package com.codeforcommunity.processor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.codeforcommunity.JooqMock;
import com.codeforcommunity.dto.ContentNote;
import com.codeforcommunity.dto.FullNote;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.jooq.DSLContext;
import org.jooq.UpdatableRecord;
import org.jooq.generated.Tables;
import org.jooq.generated.tables.Note;
import org.jooq.generated.tables.records.NoteRecord;
import org.jooq.impl.UpdatableRecordImpl;
import org.junit.jupiter.api.Test;

public class ProcessorImplTest {
  JooqMock mockDb;
  ProcessorImpl processor;

  void setup() {
    mockDb = new JooqMock();
    processor = new ProcessorImpl(mockDb.getContext());
  }

  @Test
  public void testGetAllMembers() {
    setup();

    assertEquals(0, processor.getAllMembers().size());
  }

  @Test
  public void testGetAllNotes() {
    setup();

    List<UpdatableRecordImpl> list = new ArrayList<>();
    for (int i = 0; i < 5; i++) {
      NoteRecord n = new NoteRecord();
      n.setId(i);
      n.setTitle("Note" + i);
      n.setBody("THIS IS A NOTE");

      list.add(n);
    }
    mockDb.addReturn("SELECT", list);

    NoteRecord n = new NoteRecord();
    n.setId(11);
    n.setTitle("NoTe11");
    n.setBody("nOtE11");

    mockDb.addReturn("SELECT", n);

    List<FullNote> notes = processor.getAllNotes();

    for (int i = 0; i < 5; i++) {
      assertEquals(i, notes.get(i).getId());
      assertEquals("Note" + i, notes.get(i).getTitle());
      assertEquals("THIS IS A NOTE", notes.get(i).getContent());
      assertEquals("10/20/2019", notes.get(i).getDate());
    }

    notes = processor.getAllNotes();
    assertEquals(11, notes.get(0).getId());
    assertEquals("NoTe11", notes.get(0).getTitle());
    assertEquals("nOtE11", notes.get(0).getContent());
    assertEquals("10/20/2019", notes.get(0).getDate());

    assertEquals(2, mockDb.timesCalled("SELECT"));
  }

  @Test
  public void testGetANote() {
    setup();

    NoteRecord n = new NoteRecord();
    n.setId(0);
    n.setTitle("TITLE");
    n.setBody("SET BODY");
    mockDb.addReturn("SELECT", n);

    FullNote note = processor.getANote(5);

    assertEquals(0, note.getId());
    assertEquals(1, mockDb.timesCalled("SELECT"));

    String sql = mockDb.getSqlStrings().get("SELECT").get(0);
    Object[] bindings = mockDb.getSqlBindings().get("SELECT").get(0);

    // certify count of bindings
    assertEquals(1, bindings.length);
    assertTrue(sql.contains("select")
        && sql.contains("from \"note\" where \"note\".\"id\" = ?"));
    assertEquals(5, bindings[0]);
    assertEquals(1, mockDb.timesCalled("SELECT"));
  }

  @Test
  public void testInsertNotes() {
    setup();
    DSLContext ctx = mockDb.getContext();

    NoteRecord primer = ctx.newRecord(Tables.NOTE);
    primer.setId(0);
    mockDb.addReturn("INSERT", primer);

    NoteRecord record = ctx.newRecord(Tables.NOTE);
    record.setTitle("Hello");
    record.setBody("World");
    assertEquals(1, record.store());

    assertEquals(1, mockDb.timesCalled("INSERT"));
    String sql = mockDb.getSqlStrings().get("INSERT").get(0);
    Object[] bindings = mockDb.getSqlBindings().get("INSERT").get(0);
    assertEquals("insert into \"note\" (\"title\", \"body\") values (?, ?) returning "
        + "\"note\".\"id\"", sql);
    assertEquals(2, bindings.length);
    assertEquals("Hello", bindings[0]);
    assertEquals("World", bindings[1]);
  }

  @Test
  public void testCreateNotes() {
    setup();
    DSLContext ctx = mockDb.getContext();

    List<ContentNote> notes = new ArrayList<>();
    for (int i = 0; i < 3; i++) {
      ContentNote n = new ContentNote("Note" + i, "HELLO WORLD");
      notes.add(n);

      NoteRecord primer = ctx.newRecord(Tables.NOTE);
      primer.setId(i);
      mockDb.addReturn("INSERT", primer);
    }

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
}
