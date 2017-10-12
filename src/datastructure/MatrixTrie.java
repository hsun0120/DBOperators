package datastructure;

import java.util.LinkedList;
import java.util.ListIterator;
import java.util.TreeMap;

public class MatrixTrie {
	private int count;
	private trieNode root;
	
	protected class trieNode {
		private TreeMap<Character, trieNode> children;
		private LinkedList<Group> docs;
		
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
		
		public void appendGroup(Group group) {
			if(this.docs == null)
				this.docs = new LinkedList<>();
			
			this.docs.add(group);
		}
		
		@Override
		public String toString() {
			ListIterator<Group> it = this.docs.listIterator();
			StringBuilder sb = new StringBuilder();
			while(it.hasNext()) {
				sb.append(it.next());
				if(it.hasNext())
					sb.append(", ");
			}
			return sb.toString();
		}
	}
	
	public MatrixTrie() {
		this.count = 0;
		this.root = new trieNode();
	}
	
	public trieNode insert(String term, Group group) {
		trieNode curr = this.root;
		for(int i = 0; i < term.length(); i++) {
			curr = curr.insert(new Character(term.charAt(i)));
		}
		curr.appendGroup(group);
		return curr;
	}
	
	public int getCount() {
		return this.count;
	}
}