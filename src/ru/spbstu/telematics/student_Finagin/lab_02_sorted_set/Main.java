package ru.spbstu.telematics.student_Finagin.lab_02_sorted_set;
import java.util.Vector;

public class Main {

	/**
	 * @param args
	 */
	
	public static void main(String[] args) {
		SortedSet<Integer> sortedSet =new SortedSet<Integer>();
		sortedSet.add(10);
		sortedSet.add(23);
		sortedSet.add(-1);
		sortedSet.add(2);
		sortedSet.add(5);
		sortedSet.add(11);
		sortedSet.add(4);
		sortedSet.add(4);
		sortedSet.add(1);
		sortedSet.add(23);
		sortedSet.add(-1);
		SortedSet<Integer>.SortedSetIterator iterator=sortedSet.iterator();
		while (iterator.hasNext())
			System.out.print(iterator.next() + " -> ");
		if (sortedSet.contains(0))
			System.out.println("\ncontains!");
		else
			System.out.println("\nnot in set");
	}

}
