# Note API

This API is for getting, creating, and updating note objects. All request and response bodies will be of type JSON and include an
appropriate `Content-Type: application/json` header.

## `GET /api/note`

Used for getting one or all of the notes in the database.

### Query Parameters

##### note_id: INTEGER

- Get a note that has this specific ID.

### Responses

#### `200 OK`

Every thing is okay.

If there were any potentially confusing fields in the response body you
would explain what they mean and what they're for here.

```json
{
  "status": "OK",
  "notes": [
    {
      "id": INTEGER,
      "title": STRING,
      "content": STRING,
      "date": DATE
    },
    ...
  ]
}
```

A **DATE** is a String in the format: "MM-DD-YYYY HH:MM:ss"


#### `400 BAD REQUEST`

This happens if the client sends a request that does not conform to the standard 
outlined above.

```json
{
  "status": "BAD REQUEST",
  "reason": STRING
}
```


## `POST /api/note`

Used for creating one or more notes to be stored in the database.

### Request Body

```json
{
  "notes": [
    {
      "title": STRING,
      "content": STRING
    },
    ...
  ]
}
```

### Responses

#### `201 OK`

The notes were successfully created. Returns all the created notes as they would be show in a GET request so that the frontend can find out note's assigned ID numbers.

```json
{
  "status": "OK",
  "notes": [
    {
      "id": INTEGER,
      "title": STRING,
      "content": STRING,
      "date": DATE
    },
    ...
  ]
}
```

#### `400 BAD REQUEST`

The request body was malformed according to the specification.

```json
{
  "status": "BAD REQUEST",
  "reason": STRING
}
```


## `PUT /api/note/:noteid`

Used to update a specific note.

### Route Parameters

#### note_id: INTEGER

- Update the note with this id number

### Request Body

```json
{
  "note": {
    "title": STRING,
    "content": STRING
  }
}
```

### Responses

#### `200 OK`

The note was updated successfully, returns the new note object.

```json
{
  "status": "OK",
  "note": {
      "id": INTEGER,
      "title": STRING,
      "content": STRING,
      "date": DATE
  }
}
```

#### `400 BAD REQUEST`

The request body was malformed according to the specification OR if the note_id does not map to any note in the database.

```json
{
  "status": "BAD REQUEST",
  "reason": STRING
}
```

## `DELETE /api/note/:noteid`

Used to delete a specific note.

### Route Parameters

#### note_id: INTEGER

- Delete the note with this id number

### Responses

#### `200 OK`

The note was deleted successfully






