package com.codeforcommunity.api;

import com.codeforcommunity.dto.notes.ContentNote;
import com.codeforcommunity.dto.notes.FullNote;

import java.util.List;

public interface INotesProcessor {
  List<FullNote> getAllNotes();

  FullNote getANote(int noteId);

  List<FullNote> createNotes(List<ContentNote> notes);

  FullNote updateNote(int noteId, ContentNote newNote);

  void deleteNote(int noteId);
}
