package io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.concurrent.LinkedBlockingQueue;

import datastructure.Tuple;

public class Writer implements Runnable{
	static final String END = "END";
	
	private String dir;
	private LinkedBlockingQueue<Tuple<String, LinkedList<Tuple<String,
	Integer>>>> lbq;
	
	public Writer(String dir, LinkedBlockingQueue<Tuple<String,
			LinkedList<Tuple<String, Integer>>>> lbq) {
		this.dir = dir;
		this.lbq = lbq;
	}

	@Override
	public void run() {
		while(true) {
			try {
				Tuple<String, LinkedList<Tuple<String, Integer>>> tuple =
						this.lbq.take();
				/* Checking end indicator */
				if(tuple.getFirst().equals(END)) {
					this.lbq.put(new Tuple<String, LinkedList<Tuple<String,
							Integer>>>(END, null));
					break;
				}
				
				File file = new File(this.dir + "/" + tuple.getFirst());
				if(file.exists()) {
					RandomAccessFile raf = new
							RandomAccessFile(file, "rw");
					ListIterator<Tuple<String, Integer>> it =
							tuple.getSecond().listIterator();
					raf.seek(raf.length());
					while(it.hasNext())
						raf.writeBytes(" " + it.next().toString());
					raf.close();
				} else {
					BufferedWriter writer = new BufferedWriter(new
							OutputStreamWriter(new FileOutputStream(this.dir 
									+ "/" + tuple.getFirst()), 
									StandardCharsets.UTF_8.toString()));
					ListIterator<Tuple<String, Integer>> it =
							tuple.getSecond().listIterator();
					writer.write(tuple.getFirst());
					while(it.hasNext())
						writer.write(" " + it.next().toString());
					writer.close();
				}
				tuple.getSecond().clear();
			} catch (InterruptedException | IOException e) {
				e.printStackTrace();
			}
		}
	}
}