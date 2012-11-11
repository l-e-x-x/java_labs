package ru.spbstu.telematics.student_Finagin.lab_02_sorted_set;

public interface ISortedSetIterator <T extends Comparable> {
	T next();
	boolean hasNext();
	void remove();
}
