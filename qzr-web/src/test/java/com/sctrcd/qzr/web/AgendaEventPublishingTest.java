package com.sctrcd.qzr.web;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.JacksonJsonMessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

/**
 * The page listens for rule activations over STOMP on a plain WebSocket at
 * <code>/drools/websocket</code>. This connects there as the page does and
 * checks that starting a quiz publishes the rules that fired.
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class AgendaEventPublishingTest {

    @LocalServerPort
    private int port;

    @Test
    void shouldPublishRuleActivationsToSubscribers() throws Exception {
        WebSocketStompClient stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        stompClient.setMessageConverter(new JacksonJsonMessageConverter());

        BlockingQueue<Map<?, ?>> events = new LinkedBlockingQueue<>();
        StompSession session = stompClient
                .connectAsync("ws://localhost:" + port + "/drools/websocket", new StompSessionHandlerAdapter() {})
                .get(10, TimeUnit.SECONDS);
        session.subscribe("/queue/agendaevents/", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return Map.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                events.add((Map<?, ?>) payload);
            }
        });

        // Each request without a session cookie starts a new quiz, which fires
        // "Ask dateOfBirth". Repeat until the subscription has been registered.
        HttpClient http = HttpClient.newHttpClient();
        HttpRequest startQuiz = HttpRequest.newBuilder(
                        URI.create("http://localhost:" + port + "/api/quizzes/health/questions/next"))
                .build();
        Map<?, ?> event = null;
        for (int attempt = 0; attempt < 50 && event == null; attempt++) {
            HttpResponse<String> response = http.send(startQuiz, HttpResponse.BodyHandlers.ofString());
            assertThat(response.statusCode()).isEqualTo(200);
            event = events.poll(200, TimeUnit.MILLISECONDS);
        }

        assertThat(event).isNotNull();
        assertThat(event.get("name")).isEqualTo("Ask dateOfBirth");
        session.disconnect();
        stompClient.stop();
    }
}
