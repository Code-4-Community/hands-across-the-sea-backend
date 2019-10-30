package com.codeforcommunity.processor;


import com.codeforcommunity.api.IProcessor;
import com.codeforcommunity.dto.MemberReturn;
import com.codeforcommunity.dto.NoteReturn;
import org.jooq.DSLContext;
import org.jooq.generated.Tables;
import org.jooq.generated.tables.pojos.Note;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ProcessorImpl implements IProcessor {

  private final DSLContext db;

  public ProcessorImpl(DSLContext db) {
    this.db = db;
  }

  @Override
  public List<MemberReturn> getAllMembers() {
    return new ArrayList<>();
  }

  @Override
  public List<NoteReturn> getAllNotes() {
    List<Note> notes = db.selectFrom(Tables.NOTE).fetchInto(Note.class);
    return notes.stream()
        .map(this::dbNoteToReturn)
        .collect(Collectors.toList());
  }

  @Override
  public NoteReturn getANote(int noteId) {
    Note note = db.selectFrom(Tables.NOTE).where(Tables.NOTE.ID.eq(noteId)).fetchOneInto(Note.class);
    return dbNoteToReturn(note);
  }

  private NoteReturn dbNoteToReturn(Note note) {
    return new NoteReturn(note.getId(), note.getTitle(), note.getBody(), "10/20/2019");
  }
}
