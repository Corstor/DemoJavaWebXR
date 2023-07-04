package javaServerDemo2;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

public class Counter extends Thread {
	// 1000 milliseconds (1 second)
	private static final int SECOND = 1000;
	
	private long counter = 0;
	private final WebSocketSession session;
	private boolean isOn = true;
	
	public Counter(final WebSocketSession session) {
		this.session = session;
	}
	
	public void run() {
		while(this.isOn) {
			try {
				this.session.sendMessage(new TextMessage(String.valueOf(counter))); // Invia il valore del contatore
				Thread.sleep(SECOND);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			counter++;
		}
	}
	
	public void isRunning(boolean isOn) {
		this.isOn = isOn;
	}
}