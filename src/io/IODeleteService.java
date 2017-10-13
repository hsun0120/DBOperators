package io;

import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;

public class IODeleteService {
	private int numThreads;
	private LinkedBlockingQueue<Path> lbq;
	
	public IODeleteService(int numThreads, int length) {
		this.numThreads = numThreads;
		this.lbq  = new LinkedBlockingQueue<>(length);
	}
	
	public void exec(ExecutorService es) {
		for(int i = 0; i < this.numThreads; i++)
			es.execute(new Deleter(this.lbq));
		es.shutdown();
	}
	
	public void enque(Path file) throws InterruptedException {
		this.lbq.put(file);
	}
}