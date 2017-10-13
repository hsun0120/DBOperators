package io;

import java.io.File;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import datastructure.Tuple;

public class IOService {
	private int numThreads;
	LinkedBlockingQueue<Tuple<String, LinkedList<Tuple<String, Integer>>>> lbq;
	
	public IOService(int numThreads, int length) {
		this.numThreads = numThreads;
		this.lbq  = new LinkedBlockingQueue<>(length);
	}
	
	public void exec(String tempDir) {
		File temp = new File(tempDir);
		temp.mkdir();
		final ExecutorService es = Executors.newFixedThreadPool(this.numThreads);
		for(int i = 0; i < this.numThreads; i++)
			es.execute(new Writer(tempDir, this.lbq));
		es.shutdown();
		try {
			es.awaitTermination(1, TimeUnit.HOURS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void enque(Tuple<String, LinkedList<Tuple<String, Integer>>>
	tuple) throws InterruptedException {
		this.lbq.put(tuple);
	}
}