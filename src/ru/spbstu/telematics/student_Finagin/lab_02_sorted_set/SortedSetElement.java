package ru.spbstu.telematics.student_Finagin.lab_02_sorted_set;
import java.util.Vector;

public class SortedSetElement <T extends Comparable>  {
	
	T value_;
	SortedSetElement <T> parentElement_,
						 leftElement_,
						 rightElement_;
	Vector <T> equalElements_;
	
	public SortedSetElement(T val, SortedSetElement<T> parent ) {
		parentElement_=parent;
		equalElements_=new Vector<T> ();
		value_=val;
	}
	
	public void setLeftElement_(SortedSetElement<T> leftElement) {
		this.leftElement_ = leftElement;
	}
	
	public void setRightElement_(SortedSetElement<T> rightElement) {
		this.rightElement_ = rightElement;
	}
	
	public SortedSetElement<T> getLeftElement_() {
		return leftElement_;
	}
	
	public SortedSetElement<T> getRightElement_() {
		return rightElement_;
	}
	
	public SortedSetElement<T> getParentElement_() {
		return parentElement_;
	}
	
	public T getValue_() {
		return value_;
	}

	public int compareWithParam(T param) {
		return value_.compareTo(param);
	}

	public void addToEquals(Comparable<?> e) {
		this.equalElements_.addElement((T) e);
		
	}
	
	public T getFromEqualsList(int fromIndex)
	{
		if ((this.equalElements_.size() == 0)||(fromIndex > this.equalElements_.size()-1))
			return null; // если список пуст или мы хотим забрать элемент вне списка
		return this.equalElements_.elementAt(fromIndex);
	}
	
	void removeElement(SortedSetElement<T> prevElement, SortedSetElement<T> nextElement)
	{
		if (prevElement.getLeftElement_() == this)
			prevElement.setLeftElement_(nextElement);
		else
			prevElement.setRightElement_(nextElement);
	}
	
}
