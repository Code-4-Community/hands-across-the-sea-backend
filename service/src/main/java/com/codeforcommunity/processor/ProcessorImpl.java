package com.codeforcommunity.processor;


import com.codeforcommunity.api.IProcessor;
import com.codeforcommunity.dto.ContentNote;
import com.codeforcommunity.dto.MemberReturn;
import com.codeforcommunity.dto.FullNote;
import org.jooq.DSLContext;
import org.jooq.generated.Tables;
import org.jooq.generated.tables.pojos.Note;
import org.jooq.generated.tables.records.NoteRecord;
import org.jooq.generated.tables.records.NoteUserRecord;
import org.jooq.impl.UpdatableRecordImpl;

import java.util.ArrayList;
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
  public List<FullNote> getAllNotes() {
    List<Note> notes = db.selectFrom(Tables.NOTE).fetchInto(Note.class);
    return notes.stream()
        .map(this::dbNoteToReturn)
        .collect(Collectors.toList());
  }

  @Override
  public FullNote getANote(int noteId) {
    Note note = db.selectFrom(Tables.NOTE).where(Tables.NOTE.ID.eq(noteId)).fetchOneInto(Note.class);
    return dbNoteToReturn(note);
  }

  @Override
  public List<FullNote> createNotes(List<ContentNote> notes) {
    List<NoteRecord> noteRecords = notes.stream()
        .map(this::contentNoteToNoteRecord)
        .collect(Collectors.toList());

    noteRecords.forEach(UpdatableRecordImpl::store);

    return noteRecords.stream()
        .map(noteRecord -> this.dbNoteToReturn(noteRecord.into(Note.class)))
        .collect(Collectors.toList());
  }

  @Override
  public FullNote updateNote(int noteId, ContentNote newNote) {
    NoteRecord noteToUpdate = db.fetchOne(Tables.NOTE, Tables.NOTE.ID.eq(noteId));
    noteToUpdate.setTitle(newNote.getTitle());
    noteToUpdate.setBody(newNote.getContent());

    noteToUpdate.store();

    return dbNoteToReturn(noteToUpdate.into(Note.class));
  }

  String getNoteStuff(int noteId) {
    NoteRecord note = db.fetchOne(Tables.NOTE, Tables.NOTE.ID.eq(noteId));
    String ret = "";

    if (note.getBody().equals("hello")) {
       ret += "12";
       NoteRecord anotherNote = db.fetchOne(Tables.NOTE, Tables.NOTE.ID.eq(1));
    }

    if (note.getTitle().equals("Yellow")) {
      ret += "23";
      NoteUserRecord userRecord = db.newRecord(Tables.NOTE_USER);
      userRecord.setFirstName("Joey");
      //userRecord.store();
    }

    return ret;
  }

  @Override
  public void deleteNote(int noteId) {
    db.deleteFrom(Tables.NOTE)
        .where(Tables.NOTE.ID.eq(noteId))
        .execute();
  }

  private FullNote dbNoteToReturn(Note note) {
    return new FullNote(note.getId(), note.getTitle(), note.getBody(), "10/20/2019");
  }

  private NoteRecord contentNoteToNoteRecord(ContentNote note) {
    NoteRecord noteRecord = db.newRecord(Tables.NOTE);
    noteRecord.setBody(note.getContent());
    noteRecord.setTitle(note.getTitle());
    return noteRecord;
  }
}
