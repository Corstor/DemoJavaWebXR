package javaServerDemo2;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public class Counter extends Thread {
	private static final int SECOND = 1000;
	
	private int counter = 0;
	private final SseEmitter emitter;
	
	public Counter(final SseEmitter emitter) {
		this.emitter = emitter;
	}
	
	public void run() {
		while(true) {
			try {
				Thread.sleep(SECOND);
				this.emitter.send(SseEmitter.event().data(String.valueOf(counter))); // Invia il valore del contatore
			} catch (Exception e) {
				this.emitter.completeWithError(e);
			}
			
			counter++;
		}
	}
	
	public SseEmitter getEmitter() {
		return this.emitter;
	}
}
