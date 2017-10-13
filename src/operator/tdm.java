package operator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
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
	
	public tdm() {
		this.matrix = new MatrixTrie(4);
		this.numOfDoc = 0;
	}
	
	public void add(Histogram hist) {
		LinkedList<Tuple<String, Integer>> terms = hist.getTerms();
		ListIterator<Tuple<String, Integer>> it = terms.listIterator();
		while(it.hasNext()) {
			Tuple<String, Integer> tuple = it.next();
			if((double)Runtime.getRuntime().freeMemory()/
					Runtime.getRuntime().totalMemory() < 0.1)
				this.matrix.clear();
			this.matrix.insert(tuple.getFirst(), new Tuple<>(hist.getId(),
					tuple.getSecond()));
		}
		this.numOfDoc++;
	}
	
	public int size() {
		return this.numOfDoc;
	}
	
	public void end() {
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
	    tdm tdm = new tdm();
	    for(String doc: fileList) {
	    	try(Scanner sc = new Scanner(new InputStreamReader(new
	    			FileInputStream(doc), StandardCharsets.UTF_8))) {
	    		while(sc.hasNextLine()) {
	    			Histogram hist = new Histogram(null);
	    	    	hist.construct(sc.nextLine());
	    	    	tdm.add(hist);
	    		}
	    	} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
	    }
	    tdm.end();
	    Merger.merge("temp", "matrix");
	    long ed = System.nanoTime();
	    System.out.println("Matrix generation completed in " + (ed - st) + " ns.");
	}
}