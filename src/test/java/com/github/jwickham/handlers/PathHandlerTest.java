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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
    
    @DisplayName("Handle method response includes paths in the matcher when a matcher is provided")
    @Test
    void testPathMatcherIsProvided() {
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
        assertNotNull(capturedArgument.get(handlerTypeGET));
        assertEquals(2, capturedArgument.get(handlerTypeGET).size());
        assertTrue(capturedArgument.get(handlerTypeGET).contains("api"));
        assertTrue(capturedArgument.get(handlerTypeGET).contains("asdf"));

        assertTrue(capturedArgument.containsKey(handlerTypePOST));
        assertNotNull(capturedArgument.get(handlerTypePOST));
        assertEquals(1, capturedArgument.get(handlerTypePOST).size());
        assertTrue(capturedArgument.get(handlerTypePOST).contains("asdf"));
    }

    @DisplayName("Handle method response includes only this path when a matcher is not provided")
    @Test
    void testPathMatcherIsNotProvided() {
        String path = "/api/foo";
        HandlerType handlerType = HandlerType.GET;
        String handlerTypeString = handlerType.toString();

        when(ctx.path()).thenReturn(path);
        when(ctx.method()).thenReturn(handlerType);

        PathHandler pathHandler = spy(new PathHandler());

        try {
            pathHandler.handle(ctx);
        } catch (Exception e) {
            fail(e);
        }

        ArgumentCaptor<Map<String, List<String>>> captor = ArgumentCaptor.forClass(Map.class);
        verify(ctx).json(captor.capture());

        Map<String, List<String>> capturedArgument = captor.getValue();
        assertTrue(capturedArgument.containsKey(handlerTypeString));
        assertNotNull(capturedArgument.get(handlerTypeString));
        assertEquals(1, capturedArgument.get(handlerTypeString).size());
        assertTrue(capturedArgument.get(handlerTypeString).contains(path));
    }
}