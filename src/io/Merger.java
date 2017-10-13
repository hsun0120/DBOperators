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

public class Merger {
	public static void merge(String dir, final String target) {
		Path root = Paths.get(dir);
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
						raf.writeBytes("\n");
						out.close();
						raf.close();
						Files.delete(file);
					} catch (IOException e) {
						e.printStackTrace();
					}
					return FileVisitResult.CONTINUE;
				}
				
				@Override
				public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
					try {
						Files.delete(dir);
					} catch (IOException e) {
						e.printStackTrace();
					}
					return FileVisitResult.CONTINUE;
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}