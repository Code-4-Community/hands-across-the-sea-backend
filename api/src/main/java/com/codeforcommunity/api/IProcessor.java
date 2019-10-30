package com.codeforcommunity.api;

import com.codeforcommunity.dto.MemberReturn;
import com.codeforcommunity.dto.NoteReturn;

import java.util.List;
import java.util.Optional;

public interface IProcessor {
  /**
   * Get all the members first and last names.
   */
  List<MemberReturn> getAllMembers();


  List<NoteReturn> getAllNotes();

  NoteReturn getANote(int noteId);
}
