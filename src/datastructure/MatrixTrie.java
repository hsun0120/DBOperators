package datastructure;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import io.IOMergeService;

public class MatrixTrie {
	private int count;
	private trieNode root;
	private int numThreads;
	private String temp;
	
	protected class trieNode {
		private TreeMap<Character, trieNode> children;
		private LinkedList<Tuple<String, Integer>> docs;
		
		public trieNode() {
			this.children = null;
			this.docs = null;
		}
		
		public void init() {
			this.children = new TreeMap<>();
		}
		
		public TreeMap<Character, trieNode> getChildren() {
			return this.children;
		}
		
		public int numOfChidren() {
			if(this.children == null) return 0;
			
			return this.children.size();
		}
		
		public trieNode insert(Character character) {
			if(this.numOfChidren() == 0) this.init();
			
			if(this.children.containsKey(character))
				return this.children.get(character);
			else {
				trieNode child = new trieNode();
				this.children.put(character, child);
				count++;
				return child;
			}
		}
		
		public LinkedList<Tuple<String, Integer>> getDocs() {
			return this.docs;
		}
		
		public void appendTuple(Tuple<String, Integer> tuple) {
			if(this.docs == null)
				this.docs = new LinkedList<>();
			
			this.docs.add(tuple);
		}
		
		@Override
		public String toString() {
			ListIterator<Tuple<String, Integer>> it = this.docs.listIterator();
			StringBuilder sb = new StringBuilder();
			while(it.hasNext()) {
				sb.append(it.next());
				if(it.hasNext())
					sb.append(", ");
			}
			return sb.toString();
		}
	}
	
	public MatrixTrie(int numThreads, String temp) {
		this.count = 0;
		this.root = new trieNode();
		this.numThreads = numThreads;
		this.temp = temp;
	}
	
	public trieNode insert(String term, Tuple<String, Integer> tuple) {
		trieNode curr = this.root;
		for(int i = 0; i < term.length(); i++) {
			curr = curr.insert(new Character(term.charAt(i)));
		}
		curr.appendTuple(tuple);
		return curr;
	}
	
	public int getCount() {
		return this.count;
	}
	
	public void clear() {
		this.save();
		this.root = new trieNode();
		this.count = 0;
	}
	
	public void save() {
		final ExecutorService es =
				Executors.newFixedThreadPool(this.numThreads);
		IOMergeService iosrv = new IOMergeService(this.numThreads, 1000);
		iosrv.exec(temp, es);
		this.saveHelper(this.root, "", iosrv);
		
		try { /* Indicate end of task */
			iosrv.enque(new Tuple<String,
					LinkedList<Tuple<String, Integer>>>("END", null));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		try { /* Wait for all threads to finish */
			es.awaitTermination(1, TimeUnit.HOURS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void output() {
		try(BufferedWriter writer = new BufferedWriter(new
				OutputStreamWriter(new FileOutputStream("matrix"), 
						StandardCharsets.UTF_8.toString()))) {
			this.traverseHelper(this.root, "", writer);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void saveHelper(trieNode node, String str, IOMergeService iosrv) {
		if(node.numOfChidren() == 0) { //Leaf node
			try {
				iosrv.enque(new Tuple<String, LinkedList<Tuple<String,
						Integer>>>(str, node.getDocs()));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return;
		}
		Iterator<Map.Entry<Character, trieNode>> it =
				node.getChildren().entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry<Character, trieNode> entry = it.next();
			this.saveHelper(entry.getValue(), str +
					String.valueOf(entry.getKey().charValue()), iosrv);
		}
		
		if(node.getDocs() != null) {
			try {
				iosrv.enque(new Tuple<String, LinkedList<Tuple<String,
						Integer>>>(str, node.getDocs()));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void traverseHelper(trieNode node, String str, BufferedWriter
			writer) {
		if(node.numOfChidren() == 0) { //Leaf node
			try {
				writer.write(str + " ");
				writer.write(node.getDocs().toString() + " ");
				writer.write(node.getDocs().size() + "\n");
			} catch (IOException e) {
				e.printStackTrace();
			}
			return;
		}
		Iterator<Map.Entry<Character, trieNode>> it =
				node.getChildren().entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry<Character, trieNode> entry = it.next();
			this.traverseHelper(entry.getValue(), str +
					String.valueOf(entry.getKey().charValue()), writer);
		}
		
		if(node.getDocs() != null) {
			try {
				writer.write(str + " ");
				writer.write(node.getDocs().toString() + " ");
				writer.write(node.getDocs().size() + "\n");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
}