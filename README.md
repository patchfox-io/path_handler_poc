# path_handler_poc

Repo which contains an external handler for proving out modularity of handlers with
the Patchfox Javalin POC (found [here](https://github.com/patchfox-io/javalin_poc)).

# API
`PathHandler` contains a single public method:

`void handle (@NotNull() Context ctx)`

`Context` is an object of type `io.javalin.http.Context` ([docs](https://javadoc.io/doc/io.javalin/javalin/5.4.2/io/javalin/http/Context.html)) which is used to pass information about the request and response from the servelet to the handlers; it is also used to render output (e.g. json, html, etc).

This method expects a Javalin `PathMatcher` ([docs](https://javadoc.io/doc/io.javalin/javalin/5.4.2/io/javalin/routing/PathMatcher.html)) to be included in the `Context` passed to the `handle` method. If this is done, the output will be a json object of the handler entries; otherwise, the only result will be a json object containing only the path which called this handler. In any case, the output is sent using the `json()` method on the context object

# Usage
```
// connect the handler to an http operation and endpoint for a user type
javalin.get("/api/paths", new PathHandler(), Role.ADMIN)

// add a "before" handler to add the matcher to the context
javalin.before("/api/paths", ctx -> {
    PathMatcher matcher = javalin.javalinServelet().getMatcher();
    ctx.attribute(PathHandler.MATCHER_KEY, matcher);
})
```