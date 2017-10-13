package io;

import java.io.File;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;

import datastructure.Tuple;

public class IOMergeService {
	private int numThreads;
	LinkedBlockingQueue<Tuple<String, LinkedList<Tuple<String, Integer>>>> lbq;
	
	public IOMergeService(int numThreads, int length) {
		this.numThreads = numThreads;
		this.lbq  = new LinkedBlockingQueue<>(length);
	}
	
	public void exec(String tempDir, ExecutorService es) {
		File temp = new File(tempDir);
		temp.mkdir();
		for(int i = 0; i < this.numThreads; i++)
			es.execute(new Writer(tempDir, this.lbq));
		es.shutdown();
	}
	
	public void enque(Tuple<String, LinkedList<Tuple<String, Integer>>>
	tuple) throws InterruptedException {
		this.lbq.put(tuple);
	}
}