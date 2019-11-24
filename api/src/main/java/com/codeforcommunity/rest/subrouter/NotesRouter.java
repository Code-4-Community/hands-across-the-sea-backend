package com.codeforcommunity.rest.subrouter;

import com.codeforcommunity.api.INotesProcessor;
import com.codeforcommunity.dto.notes.FullNote;
import com.codeforcommunity.dto.notes.NoteRequest;
import com.codeforcommunity.dto.notes.NoteResponse;
import com.codeforcommunity.dto.notes.NotesRequest;
import com.codeforcommunity.dto.notes.NotesResponse;
import com.codeforcommunity.rest.HttpConstants;
import com.codeforcommunity.rest.IRouter;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.codeforcommunity.rest.ApiRouter.end;
import static com.codeforcommunity.rest.ApiRouter.endClientError;
import static com.codeforcommunity.rest.ApiRouter.endServerError;

public class NotesRouter implements IRouter {

  private final INotesProcessor notesProcessor;

  public NotesRouter(INotesProcessor notesProcessor) {
    this.notesProcessor = notesProcessor;
  }

  @Override
  public Router initializeRouter(Vertx vertx) {
    Router router = Router.router(vertx);

    registerGetNoteRoute(router);
    registerPostNoteRoute(router);
    registerPutNoteRoute(router);
    registerDeleteNoteRoute(router);

    return router;
  }


  //protected resource
  private void registerGetNoteRoute(Router router) {
    Route getNoteRoute = router.route(HttpMethod.GET, HttpConstants.noteRoute);
    getNoteRoute.handler(this::handleGetNoteRoute);
  }

  //protected resource
  private void registerPostNoteRoute(Router router) {
    Route postNoteRoute = router.route(HttpMethod.POST, HttpConstants.noteRoute);
    postNoteRoute.handler(this::handlePostNoteRoute);
  }

  //protected resource
  private void registerPutNoteRoute(Router router) {
    Route putNoteRoute = router.route(HttpMethod.PUT, HttpConstants.noteRoute + "/:" + HttpConstants.noteIdParam);
    putNoteRoute.handler(this::handlePutNoteRoute);
  }

  //protected resource
  private void registerDeleteNoteRoute(Router router) {
    Route deleteNoteRoute = router.route(HttpMethod.DELETE, HttpConstants.noteRoute + "/:" + HttpConstants.noteIdParam);
    deleteNoteRoute.handler(this::handleDeleteNoteRoute);
  }


  private void handleGetNoteRoute(RoutingContext ctx) {

    Optional<String> optionalNoteId;

    try {
      optionalNoteId = Optional.ofNullable(ctx.request().getParam(HttpConstants.noteIdParam));
    } catch (Exception e) {
      endClientError(ctx.response());
      return;
    }

    try {
      List<FullNote> notes;
      if (optionalNoteId.isPresent()) {
        int noteId = Integer.parseInt(optionalNoteId.get());
        notes = Collections.singletonList(notesProcessor.getANote(noteId));
      } else {
        notes = notesProcessor.getAllNotes();
      }
      NotesResponse response = new NotesResponse(HttpConstants.okMessage, notes);
      end(ctx.response(), HttpConstants.ok_code, JsonObject.mapFrom(response).encode());
    } catch (Exception e) {
      endServerError(ctx.response(), e);
    }
  }

  private void handlePostNoteRoute(RoutingContext ctx) {

    NotesRequest requestBody;

    try {
      requestBody = ctx.getBodyAsJson().mapTo(NotesRequest.class);
      assert requestBody != null;
    } catch (Exception e) {
      endClientError(ctx.response());
      return;
    }



    try {
      List<FullNote> notes = notesProcessor.createNotes(requestBody.getNotes());
      NotesResponse response = new NotesResponse(HttpConstants.okMessage, notes);
      end(ctx.response(), HttpConstants.created_code, JsonObject.mapFrom(response).encode());
    } catch (Exception e) {
      endServerError(ctx.response(), e);
    }
  }

  private void handlePutNoteRoute(RoutingContext ctx) {

    NoteRequest requestBody;

    try {
      requestBody = ctx.getBodyAsJson().mapTo(NoteRequest.class);
    } catch (Exception e) {
      endClientError(ctx.response());
      return;
    }

    try {
      HttpServerRequest request = ctx.request();
      int noteId = Integer.parseInt(request.getParam(HttpConstants.noteIdParam));
      FullNote updatedNote = notesProcessor.updateNote(noteId, requestBody.getNote());
      NoteResponse response = new NoteResponse(HttpConstants.okMessage, updatedNote);
      end(ctx.response(), HttpConstants.ok_code, JsonObject.mapFrom(response).encode());
    } catch (Exception e) {
      endServerError(ctx.response(), e);
    }
  }

  private void handleDeleteNoteRoute(RoutingContext ctx) {

    int noteId;

    try {
      noteId = Integer.parseInt(ctx.request().getParam(HttpConstants.noteIdParam));
    } catch (Exception e) {
      endClientError(ctx.response());
      return;
    }

    try {
      notesProcessor.deleteNote(noteId);
      end(ctx.response(), HttpConstants.ok_code);
    } catch (Exception e) {
      endServerError(ctx.response(), e);
    }
  }
}
