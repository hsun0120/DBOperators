package datastructure;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.TreeMap;

import datastructure.Trie.trieNode;

public class Histogram {
	private TreeMap<String, trieNode> map;
	private Trie trie;
	private int size;
	private String docId;
	
	public Histogram(String docId) {
		this.map = new TreeMap<>();
		this.trie = new Trie();
		this.size = 0;
		this.docId = docId;
	}
	
	public void construct(String filename) {
		try(Scanner sc = new Scanner(new FileInputStream(filename),
				StandardCharsets.UTF_8.toString())) {
			this.docId = sc.next(); //TODO: change this if input format changes
			while(sc.hasNext()) {
				String term = sc.next();
				trieNode node = this.trie.insert(term);
				if(!this.map.containsKey(term)) {
					this.map.put(term, node);
					this.size++;
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public int size() {
		return this.size;
	}
	
	public String getId() {
		return this.docId;
	}
	
	public LinkedList<Tuple> getTerms() {
		LinkedList<Tuple> list = new LinkedList<>();
		Iterator<Entry<String, trieNode>> it = this.map.entrySet().iterator();
		while(it.hasNext()) {
			Entry<String, trieNode> entry = it.next();
			Tuple group = new Tuple(entry.getKey(),
					entry.getValue().getFreq());
			list.add(group);
		}
		return list;
	}
	
	public String getStats() {
		Iterator<Entry<String, trieNode>> it = this.map.entrySet().iterator();
		StringBuilder sb = new StringBuilder();
		while(it.hasNext()) {
			Entry<String, trieNode> entry = it.next();
			sb.append(entry.getKey() + ": ");
			sb.append(entry.getValue().getFreq() + "\n");
		}
		return sb.toString();
	}
	
	public static void main(String[] args) {
		Histogram doc = new Histogram("0");
		doc.construct("news.txt");
		System.out.println(doc.getStats());
	}
}