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

    registerGetANoteRoute(router);
    registerGetNotesRoute(router);
    registerPostNoteRoute(router);
    registerPutNoteRoute(router);
    registerDeleteNoteRoute(router);

    return router;
  }

  private void registerGetANoteRoute(Router router) {
    Route getNoteRoute = router.get("/:note_id");
    getNoteRoute.handler(this::handleGetANoteRoute);
  }

  //protected resource
  private void registerGetNotesRoute(Router router) {
    Route getNoteRoute = router.get("/");
    getNoteRoute.handler(this::handleGetNotesRoute);
  }

  //protected resource
  private void registerPostNoteRoute(Router router) {
    Route postNoteRoute = router.post("/");
    postNoteRoute.handler(this::handlePostNoteRoute);
  }

  //protected resource
  private void registerPutNoteRoute(Router router) {
    Route putNoteRoute = router.put("/:note_id");
    putNoteRoute.handler(this::handlePutNoteRoute);
  }

  //protected resource
  private void registerDeleteNoteRoute(Router router) {
    Route deleteNoteRoute = router.delete("/:note_id");
    deleteNoteRoute.handler(this::handleDeleteNoteRoute);
  }

  private void handleGetANoteRoute(RoutingContext ctx) {
    int noteId = Integer.parseInt(ctx.request().getParam("note_id"));
    List<FullNote> notes = Collections.singletonList(notesProcessor.getANote(noteId));
    NotesResponse response = new NotesResponse(HttpConstants.okMessage, notes);
    end(ctx.response(), HttpConstants.ok_code, JsonObject.mapFrom(response).encode());
  }

  private void handleGetNotesRoute(RoutingContext ctx) {
    try {
      List<FullNote> notes = notesProcessor.getAllNotes();
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
