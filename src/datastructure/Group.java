package datastructure;

public class Group {
		private String str;
		private int freq;
		
		public Group(String str, int freq) {
			this.str = str;
			this.freq = freq;
		}
		
		public String getString() {
			return this.str;
		}
		
		public int getFreq() {
			return this.freq;
		}
		
		@Override
		public String toString() {
			return "[" + str + " : " + freq + "]";
		}
	}