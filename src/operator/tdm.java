package operator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Scanner;

import datastructure.Tuple;
import io.Merger;
import datastructure.Histogram;
import datastructure.MatrixTrie;

public class tdm {
	private MatrixTrie matrix;
	private int numOfDoc;
	private boolean io;
	
	public tdm(int numThreads, String tempDir) {
		this.matrix = new MatrixTrie(numThreads, tempDir);
		this.numOfDoc = 0;
		this.io = false;
	}
	
	public void add(Histogram hist) {
		LinkedList<Tuple<String, Integer>> terms = hist.getTerms();
		ListIterator<Tuple<String, Integer>> it = terms.listIterator();
		while(it.hasNext()) {
			Tuple<String, Integer> tuple = it.next();
			if((double)Runtime.getRuntime().freeMemory()/
					Runtime.getRuntime().totalMemory() < 0.1) {
				this.matrix.clear();
				this.io = true;
			}
			this.matrix.insert(tuple.getFirst(), new Tuple<>(hist.getId(),
					tuple.getSecond()));
		}
		this.numOfDoc++;
	}
	
	public int size() {
		return this.numOfDoc;
	}
	
	public void end() {
		if(!this.io) {
			this.matrix.output(); //Output from memory
			return;
		}
		this.matrix.save();
	}
	
	public static void main(String[] args) {
		LocalDate start = LocalDate.parse("2017-03-13"),
		          end   = LocalDate.parse("2017-03-14");
		ArrayList<String> list = new ArrayList<>();
		for(LocalDate date = start; date.isBefore(end); date = date.plusDays(1)) {
			File folder = new File(args[0] + "/" + date.toString().replace("-", ""));
		    String[] fileList = folder.list(); //Get all the files of the source folder
		    for(int i = 0; i < fileList.length; i++)
		    	list.add(args[0] + "/" + date.toString().replace("-", "") + "/"
		    	+ fileList[i]);
		}
	    String[] fileList = new String[list.size()];
	    fileList = list.toArray(fileList);
	    long st = System.nanoTime();
	    tdm tdm = new tdm(4, args[1]);
	    /* Initialize writer for row sum */
	    try(BufferedWriter writer = new BufferedWriter(new
							OutputStreamWriter(new FileOutputStream("colSum"), 
									StandardCharsets.UTF_8.toString()))) {
	    	
	    	for(String doc: fileList) {
	    		try(Scanner sc = new Scanner(new InputStreamReader(new
	    				FileInputStream(doc), StandardCharsets.UTF_8))) {
	    			while(sc.hasNextLine()) {
	    				Histogram hist = new Histogram(null);
	    				hist.construct(sc.nextLine());
	    				writer.write(hist.getId() + " ");
	    				writer.write(hist.size() + "\n");
	    				tdm.add(hist);
	    			}
	    		} catch (FileNotFoundException e) {
	    			e.printStackTrace();
	    		}
	    	}
	    } catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	    
	    tdm.end();
	    Merger merger = new Merger(4);
	    merger.merge("temp", args[2]);
	    long ed = System.nanoTime();
	    System.out.println("Matrix generation completed in " + (ed - st) + " ns.");
	}
}