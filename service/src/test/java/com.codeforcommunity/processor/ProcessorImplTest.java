package com.codeforcommunity.processor;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.codeforcommunity.JooqMock;
import org.jooq.generated.tables.records.NoteRecord;
import org.junit.jupiter.api.Test;

public class ProcessorImplTest {


  @Test
  public void testGetNoteStuff() {
    JooqMock mockDb = new JooqMock();
    ProcessorImpl p = new ProcessorImpl(mockDb.getContext());

    NoteRecord n = new NoteRecord();
    n.setId(0);
    n.setBody("hello");
    n.setTitle("Yellow");
    mockDb.addReturn("SELECT", n);

    assertEquals(0, mockDb.timesCalled("SELECT"));

    String val = p.getNoteStuff(0);

    assertEquals("1223", val);

    assertEquals(2, mockDb.timesCalled("SELECT"));
    assertEquals(-1, mockDb.timesCalled("INSERT"));
  }
}
