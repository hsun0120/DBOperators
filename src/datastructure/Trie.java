package datastructure;

import java.util.TreeMap;

public class Trie {
	private int count;
	private trieNode root;
	
	protected class trieNode {
		private TreeMap<Character, trieNode> children;
		private int freq;
		
		public trieNode() {
			this.children = null;
			this.freq = 0;
		}
		
		public void init() {
			this.children = new TreeMap<>();
		}
		
		public void incFreq() {
			this.freq++;
		}
		
		public TreeMap<Character, trieNode> getChildren() {
			return this.children;
		}
		
		public int numOfChidren() {
			if(this.children == null) return 0;
			
			return this.children.size();
		}
		
		public int getFreq() {
			return this.freq;
		}
		
		public trieNode insert(Character character) {
			if(this.numOfChidren() == 0) this.init();
			
			if(this.children.containsKey(character))
				return this.children.get(character);
			else {
				trieNode child = new trieNode();
				this.children.put(character, child);
				return child;
			}
		}
	}
	
	public Trie() {
		this.count = 0;
		this.root = new trieNode();
	}
	
	public trieNode insert(String term) {
		trieNode curr = this.root;
		for(int i = 0; i < term.length(); i++) {
			curr = curr.insert(new Character(term.charAt(i)));
		}
		curr.incFreq();
		return curr;
	}
	
	public int getCount() {
		return this.count;
	}
}