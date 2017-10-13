package io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Merger {
	static final String END = "END";
	
	private IODeleteService iosrv;
	final ExecutorService es;
	
	public Merger(int nThreads) {
		this.iosrv = new IODeleteService(nThreads, 1000);
		this.es = Executors.newFixedThreadPool(nThreads);
	}
	
	public void merge(String dir, final String target) {
		Path root = Paths.get(dir);
		this.iosrv.exec(this.es);
		try {
			Files.walkFileTree(root, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attr) {
					File matrix = new File(target);					
					try {
						if(!matrix.exists())
							matrix.createNewFile();
						
						RandomAccessFile raf = new
								RandomAccessFile(matrix, "rw");
						FileChannel out = raf.getChannel();
						out.position(out.size()); //Move cursor to the end
						FileInputStream input = new
								FileInputStream(file.toString());
						FileChannel in = input.getChannel();
						in.transferTo(0, in.size(), out);
						in.close();
						input.close();
						iosrv.enque(file);
						raf.writeBytes("\n");
						out.close();
						raf.close();
					} catch (IOException | InterruptedException e) {
						e.printStackTrace();
					}
					return FileVisitResult.CONTINUE;
				}
			});
			this.iosrv.enque(Paths.get(END));
			es.awaitTermination(1, TimeUnit.DAYS);
			Files.delete(root);
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}
}