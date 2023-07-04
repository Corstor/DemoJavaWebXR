package javaServerDemo2;

import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@SpringBootApplication
@EnableWebSocket
@RestController
public class Server implements WebSocketConfigurer {	
	
	private final CopyOnWriteArrayList<WebSocketSession> sessions = new CopyOnWriteArrayList<>();
	
	public static void main(String[] args) {
		SpringApplication.run(Server.class, args);
	}
	
	@Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new CounterWebSocketHandler(), "/counter").setAllowedOrigins("*");
    }
	
	@GetMapping("/")
    public ResponseEntity<Resource> index() {
		Resource resource = new ClassPathResource("static/index.html");
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(resource);
    }
	
	private class CounterWebSocketHandler extends TextWebSocketHandler {
		private Counter counter;
		
		@Override
	    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
			sessions.add(session);
	        counter = new Counter(session);
	        counter.start();
	    }
		
		@Override
	    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
	        // Gestisci il messaggio ricevuto dal client
	        String receivedMessage = message.getPayload();
	        System.out.println("Messaggio ricevuto dal client: " + receivedMessage);
	        
	        // Invia una risposta al client se necessario
	        String responseMessage = "Risposta dal server";
	        session.sendMessage(new TextMessage(responseMessage));
	    }
		
		@Override
        public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
            sessions.remove(session);
            counter.isRunning(false);
        }
	}
}
