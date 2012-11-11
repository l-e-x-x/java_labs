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
	
	public void setParentElement_(SortedSetElement<T> parentElement_) {
		this.parentElement_ = parentElement_;
	}
	
	public void setValue_(T value_) {
		this.value_ = value_;
	}
	
	public void setEqualElements_(Vector<T> equalElements_) {
		this.equalElements_ = equalElements_;
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

	public Vector<T> getEqualElements_() {
		return equalElements_;
	}
	
	public int compareWithParam(T param) {
		return value_.compareTo(param);
	}

	public void addToEquals(Comparable<?> e) {
		this.equalElements_.addElement((T) e);
		
	}
	
	public T getFromEqualsList(int fromIndex)
	{
		if ((this.equalElements_.isEmpty())||(fromIndex > this.equalElements_.size()-1))
			return null; // если список пуст или мы хотим забрать элемент вне списка
		return this.equalElements_.elementAt(fromIndex);
	}
	
	public boolean delLastFromEqualsList()
	{
		if (this.equalElements_.isEmpty())
			return false; // если элементов нет
		this.equalElements_.removeElementAt(this.equalElements_.size()-1); // удаляем последний
		return true; // ok
	}
	
	void giveElementToParent(SortedSetElement<T> newElement)
	{
			// передаем родителю новый элемент заместо старого
		if (this.parentElement_.getLeftElement_() == this)
			this.parentElement_.setLeftElement_(newElement); 
		else
			this.parentElement_.setRightElement_(newElement);
		if (newElement != null) // если новый элемент не фиктивный
			newElement.setParentElement_(this.parentElement_); // устанавливаем для него нового родителя
	}
	
}
