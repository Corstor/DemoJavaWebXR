package javaServerDemo2;

import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@SpringBootApplication
@RestController
public class Server {	
	public static void main(String[] args) {
		SpringApplication.run(Server.class, args);
	}
	
	@GetMapping("/")
    public ResponseEntity<Resource> index() {
		Resource resource = new ClassPathResource("static/index.html");
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(resource);
    }
	
	@GetMapping(value = "/event-stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamEvents() throws IOException {
        SseEmitter emitter = new SseEmitter(-1L);
        
        Counter counter = new Counter(emitter);
        counter.start();

        return counter.getEmitter();
    }
}
