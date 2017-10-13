package io;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.LinkedBlockingQueue;

public class Deleter implements Runnable {
	static final Path END = Paths.get("END");
	
	private LinkedBlockingQueue<Path> lbq;
	
	public Deleter(LinkedBlockingQueue<Path> lbq) {
		this.lbq = lbq;
	}

	@Override
	public void run() {
		while(true) {
			Path file;
			try {
				file = this.lbq.take();
				
				if(file.equals(END)) {
					this.lbq.put(file);
					break;
				}
				
				Files.delete(file);
			} catch (InterruptedException | IOException e) {
				e.printStackTrace();
			}
		}
	}
}