package com.codeforcommunity.api.authenticated;

import com.codeforcommunity.auth.JWTData;
import com.codeforcommunity.dto.school.BookLog;
import com.codeforcommunity.dto.school.BookLogListResponse;
import com.codeforcommunity.dto.school.UpsertBookLogRequest;

public interface IProtectedBookLogProcessor {

  BookLog createBookLog(JWTData userData, int schoolId, UpsertBookLogRequest request);

  BookLogListResponse getBookLog(JWTData userData, int schoolId);

  BookLog updateBookLog(JWTData userData, int schoolId, int bookId, UpsertBookLogRequest request);

  void deleteBookLog(JWTData userData, int schoolId, int bookId);
}
