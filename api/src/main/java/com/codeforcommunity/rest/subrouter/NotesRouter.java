package com.codeforcommunity.rest.subrouter;

import com.codeforcommunity.api.INotesProcessor;
import com.codeforcommunity.dto.notes.FullNote;
import com.codeforcommunity.dto.notes.NoteRequest;
import com.codeforcommunity.dto.notes.NoteResponse;
import com.codeforcommunity.dto.notes.NotesRequest;
import com.codeforcommunity.dto.notes.NotesResponse;
import com.codeforcommunity.exceptions.MissingParameterException;
import com.codeforcommunity.rest.HttpConstants;
import com.codeforcommunity.rest.IRouter;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

import java.util.Collections;
import java.util.List;

import static com.codeforcommunity.rest.ApiRouter.end;
import static com.codeforcommunity.rest.RestFunctions.getJsonBodyAsClass;
import static com.codeforcommunity.rest.RestFunctions.getRequestParameterAsInt;

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

  private void registerGetNotesRoute(Router router) {
    Route getNoteRoute = router.get("/");
    getNoteRoute.handler(this::handleGetNotesRoute);
  }

  private void registerPostNoteRoute(Router router) {
    Route postNoteRoute = router.post("/");
    postNoteRoute.handler(this::handlePostNoteRoute);
  }

  private void registerPutNoteRoute(Router router) {
    Route putNoteRoute = router.put("/:note_id");
    putNoteRoute.handler(this::handlePutNoteRoute);
  }

  private void registerDeleteNoteRoute(Router router) {
    Route deleteNoteRoute = router.delete("/:note_id");
    deleteNoteRoute.handler(this::handleDeleteNoteRoute);
  }

  private void handleGetANoteRoute(RoutingContext ctx) {
    int noteId = getRequestParameterAsInt(ctx.request(), "note_id");
    List<FullNote> notes = Collections.singletonList(notesProcessor.getANote(noteId));
    NotesResponse response = new NotesResponse(HttpConstants.okMessage, notes);
    end(ctx.response(), HttpConstants.ok_code, JsonObject.mapFrom(response).encode());
  }

  private void handleGetNotesRoute(RoutingContext ctx) {
    List<FullNote> notes = notesProcessor.getAllNotes();
    NotesResponse response = new NotesResponse(HttpConstants.okMessage, notes);
    end(ctx.response(), HttpConstants.ok_code, JsonObject.mapFrom(response).encode());
  }

  private void handlePostNoteRoute(RoutingContext ctx) {
    NotesRequest requestBody = getJsonBodyAsClass(ctx, NotesRequest.class);

    List<FullNote> notes = notesProcessor.createNotes(requestBody.getNotes());
    NotesResponse response = new NotesResponse(HttpConstants.okMessage, notes);
    end(ctx.response(), HttpConstants.created_code, JsonObject.mapFrom(response).encode());
  }

  private void handlePutNoteRoute(RoutingContext ctx) {
    NoteRequest requestBody = getJsonBodyAsClass(ctx, NoteRequest.class);
    int noteId = getRequestParameterAsInt(ctx.request(), "note_id");
    FullNote updatedNote = notesProcessor.updateNote(noteId, requestBody.getNote());
    NoteResponse response = new NoteResponse(HttpConstants.okMessage, updatedNote);
    end(ctx.response(), HttpConstants.ok_code, JsonObject.mapFrom(response).encode());
  }

  private void handleDeleteNoteRoute(RoutingContext ctx) {

    int noteId = getRequestParameterAsInt(ctx.request(), "note_id");

    notesProcessor.deleteNote(noteId);
    end(ctx.response(), HttpConstants.ok_code);
  }
}
