# API Specification Template

This is a template for how API endpoints should be documented and defined. This section would include a high level
description of what the API is used for and why someone would use it. You should
also specify anything here that is common to all endpoints, i.e. content type, common
body elements, ect...

### Notes About Ordering

- The routes defined in the API should be defined with top level routes first and then sub-routes, i.e. `/animal` before
`/animal/dog`. 
- For any route with multiple methods or same level routes with similar functionality, define
`GET` then `POST` then `PUT`. 
- In response definitions define all `2XX` responses before any `4XX`.

# Example Note API

This is an example of an API for a basic note service that can get, create and
update notes. All request and response bodies will be of type JSON and include an
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

Any non standard data types that you use in your body specification you would specify
here. For types that are used in multiple endpoints, consider adding their definitions
as a section at the top of the document.

A DATE is a String in the format: "MM-DD-YYYY HH:MM:ss"


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

#### `200 OK`

The notes were successfully created.

```json
{
  "status": "OK"
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


