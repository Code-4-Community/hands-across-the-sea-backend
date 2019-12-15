package com.codeforcommunity.processor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.codeforcommunity.JooqMock;
import com.codeforcommunity.dto.FullNote;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.jooq.UpdatableRecord;
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
      n.setUserId(0);
      n.setTitle("Note" + i);
      n.setBody("THIS IS A NOTE");

      list.add(n);
    }
    mockDb.addReturn("SELECT", list);

    NoteRecord n = new NoteRecord();
    n.setId(11);
    n.setUserId(11);
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
    n.setUserId(0);
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
  }

//  an example test using the JooqMock mock db
//  @Test
//  public void testGetNoteStuff() {
//    JooqMock mockDb = new JooqMock();
//    ProcessorImpl p = new ProcessorImpl(mockDb.getContext());
//
//    NoteRecord n = new NoteRecord();
//    n.setId(0);
//    n.setBody("hello");
//    n.setTitle("Yellow");
//    mockDb.addReturn("SELECT", n);
//
//    assertEquals(0, mockDb.timesCalled("SELECT"));
//
//    String val = p.getNoteStuff(0);
//
//    assertEquals("1223", val);
//
//    assertEquals(2, mockDb.timesCalled("SELECT"));
//    assertEquals(0, mockDb.timesCalled("INSERT"));
//    System.out.println(mockDb.getSqlStrings());
//  }
//
//  Some possible operation
//  String getNoteStuff(int noteId) {
//    NoteRecord note = db.fetchOne(Tables.NOTE, Tables.NOTE.ID.eq(noteId));
//    String ret = "";
//
//    if (note.getBody().equals("hello")) {
//      ret += "12";
//      NoteRecord anotherNote = db.fetchOne(Tables.NOTE, Tables.NOTE.ID.eq(1));
//    }
//
//    if (note.getTitle().equals("Yellow")) {
//      ret += "23";
//      NoteUserRecord userRecord = db.newRecord(Tables.NOTE_USER);
//      userRecord.setFirstName("Joey");
//      userRecord.store();
//    }
//
//    return ret;
//  }
}
