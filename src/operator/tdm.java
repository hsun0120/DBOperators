package operator;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator;

import datastructure.Tuple;
import datastructure.Histogram;
import datastructure.MatrixTrie;

public class tdm {
	private MatrixTrie matrix;
	private int numOfDoc;
	
	public tdm() {
		this.matrix = new MatrixTrie();
		this.numOfDoc = 0;
	}
	
	public void add(Histogram hist) {
		LinkedList<Tuple> terms = hist.getTerms();
		ListIterator<Tuple> it = terms.listIterator();
		while(it.hasNext()) {
			Tuple group = it.next();
			if((double)Runtime.getRuntime().freeMemory()/
					Runtime.getRuntime().totalMemory() < 0.1)
				this.matrix.clear();
			this.matrix.insert(group.getString(), new Tuple(hist.getId(),
					group.getFreq()));
		}
		this.numOfDoc++;
	}
	
	public int size() {
		return this.numOfDoc;
	}
	
	public static void main(String[] args) {
		LocalDate start = LocalDate.parse("2017-04-10"),
		          end   = LocalDate.parse("2017-04-12");
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
	    tdm tdm = new tdm();
	    for(String doc: fileList) {
	    	Histogram hist = new Histogram(tdm.size() + "");
	    	hist.construct(doc);
	    	tdm.add(hist);
	    }
	    
	    System.out.println("Test complete.");
	}
}