package datastructure;

public class Tuple<T, E> {
		private T e1;
		private E e2;
		
		public Tuple(T e1, E e2) {
			this.e1 = e1;
			this.e2 = e2;
		}
		
		public T getFirst() {
			return this.e1;
		}
		
		public E getSecond() {
			return this.e2;
		}
		
		@Override
		public String toString() {
			return "[" + this.e1 + ":" + this.e2 + "]";
		}
	}