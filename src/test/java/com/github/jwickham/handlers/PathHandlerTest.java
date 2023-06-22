package com.github.jwickham.handlers;

import io.javalin.config.RoutingConfig;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.http.HandlerType;
import io.javalin.routing.HandlerEntry;
import io.javalin.routing.PathMatcher;
import io.javalin.security.RouteRole;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.mockito.Mockito.verify;

public class PathHandlerTest {
    private final Context ctx = mock(Context.class);

    @DisplayName("Handle method sends map of handler types to list of paths through Context.json()")
    @Test
    void testHandle() {
        PathHandler handler = spy(new PathHandler());

        try {
            handler.handle(ctx);
        } catch (Exception e) {
            fail(e);
        }

        verify(ctx).json(any(Map.class));
    }
    
    @DisplayName("Handle method response includes paths in the matcher")
    @Test
    void testPathMatcher() {
        PathMatcher matcher = new PathMatcher();
        RoutingConfig routingConfig = mock(RoutingConfig.class);
        Set<RouteRole> routeRoles = mock(Set.class);
        Handler handler = mock(Handler.class);
        matcher.add(new HandlerEntry(HandlerType.GET, "api", routingConfig, routeRoles, handler));
        matcher.add(new HandlerEntry(HandlerType.GET, "asdf", routingConfig, routeRoles, handler));
        matcher.add(new HandlerEntry(HandlerType.POST, "asdf", routingConfig, routeRoles, handler));

        when(ctx.attribute(PathHandler.MATCHER_KEY)).thenReturn(matcher);

        PathHandler pathHandler = spy(new PathHandler());

        try {
            pathHandler.handle(ctx);
        } catch (Exception e) {
            fail(e);
        }

        ArgumentCaptor<Map<String, List<String>>> captor = ArgumentCaptor.forClass(Map.class);
        verify(ctx).json(captor.capture());

        Map<String, List<String>> capturedArgument = captor.getValue();
        String handlerTypeGET = HandlerType.GET.toString();
        String handlerTypePOST = HandlerType.POST.toString();
        assertTrue(capturedArgument.containsKey(handlerTypeGET));
        assertTrue(capturedArgument.get(handlerTypeGET) != null);
        assertTrue(capturedArgument.get(handlerTypeGET).size() == 2);
        assertTrue(capturedArgument.get(handlerTypeGET).contains("api"));
        assertTrue(capturedArgument.get(handlerTypeGET).contains("asdf"));

        assertTrue(capturedArgument.containsKey(handlerTypePOST));
        assertTrue(capturedArgument.get(handlerTypePOST) != null);
        assertTrue(capturedArgument.get(handlerTypePOST).size() == 1);
        assertTrue(capturedArgument.get(handlerTypePOST).contains("asdf"));
    }

}