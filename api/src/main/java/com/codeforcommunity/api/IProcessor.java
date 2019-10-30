package com.codeforcommunity.api;

import com.codeforcommunity.dto.ContentNote;
import com.codeforcommunity.dto.MemberReturn;
import com.codeforcommunity.dto.FullNote;

import java.util.List;

public interface IProcessor {
  /**
   * Get all the members first and last names.
   */
  List<MemberReturn> getAllMembers();


  List<FullNote> getAllNotes();

  FullNote getANote(int noteId);

  List<FullNote> createNotes(List<ContentNote> notes);

  FullNote updateNote(int noteId, ContentNote newNote);
}
