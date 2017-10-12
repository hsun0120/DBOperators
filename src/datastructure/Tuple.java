package datastructure;

public class Tuple {
		private String str;
		private int freq;
		
		public Tuple(String str, int freq) {
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