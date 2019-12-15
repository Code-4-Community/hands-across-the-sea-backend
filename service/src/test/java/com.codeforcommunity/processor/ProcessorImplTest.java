package com.codeforcommunity.processor;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.codeforcommunity.JooqMock;
import org.jooq.generated.tables.records.NoteRecord;
import org.junit.jupiter.api.Test;

public class ProcessorImplTest {

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
//    assertEquals(-1, mockDb.timesCalled("INSERT"));
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
