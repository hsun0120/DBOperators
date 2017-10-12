package datastructure;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;
import java.util.TreeMap;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class MatrixTrie {
	private int count;
	private trieNode root;
	
	protected class trieNode {
		private TreeMap<Character, trieNode> children;
		private LinkedList<Tuple> docs;
		
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
		
		public LinkedList<Tuple> getDocs() {
			return this.docs;
		}
		
		public void appendTuple(Tuple tuple) {
			if(this.docs == null)
				this.docs = new LinkedList<>();
			
			this.docs.add(tuple);
		}
		
		@Override
		public String toString() {
			ListIterator<Tuple> it = this.docs.listIterator();
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
	
	public trieNode insert(String term, Tuple group) {
		trieNode curr = this.root;
		for(int i = 0; i < term.length(); i++) {
			curr = curr.insert(new Character(term.charAt(i)));
		}
		curr.appendTuple(group);
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
		try {
			Directory directory = FSDirectory.open(FileSystems.getDefault().getPath("indexDir"));
	        IndexWriterConfig indexWriterConfig = new IndexWriterConfig();
			IndexWriter writer = new IndexWriter(directory, indexWriterConfig);
			this.saveHelper(this.root, "", writer);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void saveHelper(trieNode node, String str, IndexWriter writer) {
		if(node.numOfChidren() == 0) { //Leaf node
			Document vocab = new Document();
			vocab.add(new StringField("vocab", str, Store.NO));
			try {
				writer.updateDocument(new Term("vocab", str), vocab);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return;
		}
		Iterator<Map.Entry<Character, trieNode>> it =
				node.getChildren().entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry<Character, trieNode> entry = it.next();
			this.saveHelper(entry.getValue(), str +
					String.valueOf(entry.getKey().charValue()), writer);
		}
		
		if(node.getDocs() != null) {
			Document vocab = new Document();
			vocab.add(new StringField("vocab", str, Store.NO));
			try {
				writer.updateDocument(new Term("vocab", str), vocab);
			} catch (IOException e) {
				e.printStackTrace();
			}
			node.getDocs().clear();
		}
	}
	
}