package com.github.jwickham.handlers;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.http.HandlerType;
import io.javalin.routing.HandlerEntry;
import io.javalin.routing.PathMatcher;

public class PathHandler implements Handler {
    public static final String MATCHER_KEY = "pathkey";

    public void handle (@NotNull() Context ctx) {
        Map<String, List<String>> pathList = getHandlerMapFromContext(ctx);
        
        ctx.json(pathList);
    }

    private Map<String, List<String>> getHandlerMapFromContext (Context ctx) {
        Map<String, List<String>> handlerMap = new HashMap<>();
        PathMatcher matcher = ctx.attribute(MATCHER_KEY);

        if (matcher == null || !(matcher instanceof PathMatcher)) {
            return getDefaultHandlerMap(ctx);
        }

        for (HandlerType handlerType : HandlerType.class.getEnumConstants()) {
            // since this is an internal method, this works, but is definitely a code smell
            List<HandlerEntry> entries = matcher.getAllEntriesOfType$javalin(handlerType);

            if (entries.size() < 1) continue;

            List<String> stringEntries = entries.stream()
                .map((entry) -> entry.getPath())
                .collect(Collectors.toList());

            handlerMap.put(handlerType.toString(), stringEntries);
        }

        return handlerMap;
    }

    private Map<String, List<String>> getDefaultHandlerMap (Context ctx) {
        Map<String, List<String>> handlerMap = new HashMap<>();
        String key = ctx.method().toString();
        List<String> value = Arrays.asList(new String[]{ ctx.path() });
        handlerMap.put(key, value);
        return handlerMap;
    }
}