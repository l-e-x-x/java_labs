package ru.spbstu.telematics.student_Finagin.lab_02_sorted_set;

import java.util.NoSuchElementException;

/*Класс дерева сортировки (сортированное множество)*/

public class SortedSet <T extends Comparable> implements ISortedSet {  

	SortedSetElement <T> rootElement_; // корень дерева сортировки
	
		/* Класс итератора */
	public class SortedSetIterator implements ISortedSetIterator <Comparable>{
		
		SortedSetElement<T> nextElement_; // следующий элемент (вычисляется методом next())
		SortedSetElement<T> currentElement_; // текущий элемент (для метода remove())
		
		int equalElementIndex_=-1; // индекс элемента в списке equals (равных элементов)  
		
		public SortedSetIterator() {
			nextElement_=this.getMinLeaf(SortedSet.this.rootElement_);
		}
		
		SortedSetElement<T> getMinLeaf(SortedSetElement<T> rootSubTreeElement)
		{	// возвращает min из левого поддерева или корень поддерева (параметр)
			SortedSetElement<T> resultElement=rootSubTreeElement;
			while (resultElement.getLeftElement_() != null)
				resultElement=resultElement.getLeftElement_();
			return resultElement;
		}
		
		SortedSetElement<T> getRootOfContainingLeftSubtree (SortedSetElement<T> thisElement)
		{
			SortedSetElement<T> resultElement=thisElement,
								currentElement;
			do
			{	/* обход наверх, до тех пор пока не найдем элемент-корень 
				   левого поддерева в котором содержится параметр thisElement */
				currentElement=resultElement;
				resultElement=resultElement.getParentElement_();
				if (resultElement == null)
					return null; // если дошли до корня дерева - обход окончен и ничего не найдено
			} while (currentElement != resultElement.getLeftElement_());
			return resultElement;
		}
		
			/* Интерфейс итератора */
		@Override
		public T next() throws NoSuchElementException {
			if (nextElement_ == null) // если все кончилось - кидаем исключение 
				throw new NoSuchElementException();
			if (currentElement_ != nextElement_)
				equalElementIndex_=-1;
			currentElement_=nextElement_;
			T nextEqual=currentElement_.getFromEqualsList(0);
			if (nextEqual == null)
			{	// если схожих элементов нет (список пуст) - вычисляем следующий по обходу дерева
				if (currentElement_.getRightElement_() != null)
					nextElement_=this.getMinLeaf(currentElement_.getRightElement_());
				else
					nextElement_=this.getRootOfContainingLeftSubtree(currentElement_);
				System.out.print(" [no EQ elements] ");
				return currentElement_.getValue_();
			}
			
			if (this.equalElementIndex_ == -1)
			{	// если схожие элементы есть, но их не обходили еще - возвращаем текущее значение  
				this.equalElementIndex_=0;
				System.out.print(" [got EQ elements, but now current] ");
				return currentElement_.getValue_();
			}
			nextEqual=currentElement_.getFromEqualsList(equalElementIndex_++); // берем текущее значение из списка схожих
			if (currentElement_.getFromEqualsList(equalElementIndex_) == null)
			{	// если список схожих обошли - вычисляем следующий по обходу дерева
				if (currentElement_.getRightElement_() != null)
					nextElement_=this.getMinLeaf(currentElement_.getRightElement_());
				else
					nextElement_=this.getRootOfContainingLeftSubtree(currentElement_);
			}
			System.out.print(" [from EQ element list] ");
			return nextEqual;
		}
		
		@Override
		public boolean hasNext() {
			if (nextElement_ == null)
				return false;
			return true;
		}
		
		@Override
		public void remove() throws IllegalStateException {
			if (currentElement_ == null) // если пытаемся удалить несуществующий элемент
				throw new IllegalStateException();
			switch (this.equalElementIndex_)
			{
			case -1: // если у элемента не было списка схожих элементов
				SortedSet.this.removeByElement(currentElement_);
				currentElement_=null;
				break; 
			case 0:
				T newValue=currentElement_.getFromEqualsList(0);
				currentElement_.setValue_(newValue);
				currentElement_.delFromEqualsListByIndex(0);
				this.equalElementIndex_--;
				break;
			default:
				currentElement_.delFromEqualsListByIndex(this.equalElementIndex_-1);
				this.equalElementIndex_--;
				break;
			}
		}
	}

	
		/* ---Методы---*/
	SortedSetElement <T> findElementByVal(T value)
	{	// возвращает инкапсулирующий элемент множества по значению, либо null, если не найден
		SortedSetElement<T> searchResultElement=this.rootElement_;
		while (searchResultElement != null)
		{
			int compareResult=searchResultElement.getValue_().compareTo(value);
			if (compareResult == 0)
				return searchResultElement; // если элемент найден
			if (compareResult == -1) // если текущий элемент меньше - направо
				searchResultElement=searchResultElement.getRightElement_();
			else  // если текущий элемент больше - налево
				searchResultElement=searchResultElement.getLeftElement_();
		}
		return null; // если ничего не найдено
	}

	boolean removeByElement(SortedSetElement<T> removingElement)
	{	// удаление указанного элемента (параметр)
		SortedSetElement<T> rightOfRemovingElement=removingElement.getRightElement_();
		if (rightOfRemovingElement == null)
		{	// если у текущего элемента нет правого поддерева 
			if (rootElement_ == removingElement)
				rootElement_=removingElement.getLeftElement_(); // просто изменяем корень (если удаляется корень)
			else // или все левое поддерево отдаем родителю удаляемого
				removingElement.giveElementToParent(removingElement.getLeftElement_());			
			return true;
		}
		
		SortedSetElement<T> leftOfRightOfRemovingElem=rightOfRemovingElement.getLeftElement_();
		if (leftOfRightOfRemovingElem == null)
		{	// если у правого (от удаляемого) нет левого поддерева
				// все левое поддерево отдаем правому от удаляемого
			rightOfRemovingElement.setLeftElement_(removingElement.getLeftElement_());
			if (rootElement_ == removingElement)
				rootElement_=rightOfRemovingElement; // просто изменяем корень (если удаляется корень)
			else // или отдаем правый элемент родителю удаляемого
				removingElement.giveElementToParent(rightOfRemovingElement);			
			return true;
		}
		
		while(leftOfRightOfRemovingElem.getLeftElement_() != null)
			leftOfRightOfRemovingElem=leftOfRightOfRemovingElem.getLeftElement_(); // находим наименьший элемент в поддереве правого
		leftOfRightOfRemovingElem.giveElementToParent(leftOfRightOfRemovingElem.getRightElement_()); // отдаем его правое поддерево родителю
			// заменяем текущий удаляемый на наименьший в поддереве правого (вместе со схожими) 
		removingElement.setValue_(leftOfRightOfRemovingElem.getValue_());
		removingElement.setEqualElements_(leftOfRightOfRemovingElem.getEqualElements_());
		return true;
	}
	
	 /* Интерфейс сортированного множества*/
	@Override
	public void add(Comparable<?> e) {
		if (rootElement_== null) // если корня нет
		{
			rootElement_=new SortedSetElement <T> ((T) e,null); // создаем корень
			System.out.println("Added " + e + " as a root");
		}
		else
		{	// если корень есть
			SortedSetElement <T> thisElement=rootElement_,
								 prevElement=null; 
			int compareResult;
			do
			{	// выполняем обход
				prevElement=thisElement;
				compareResult=thisElement.compareWithParam((T) e);
				switch (compareResult)
				{
				case 1: // если текущий узел больше параметра - идем налево
					thisElement=thisElement.getLeftElement_();
					break;	
				case -1: // если текущий узел меньше параметра - идем направо
					thisElement=thisElement.getRightElement_();
					break;
				}
			} while ((thisElement != null)&& // выполняем пока не дойдем до листа
					 (compareResult != 0));  // (или до узла с таким же значением)
			
			SortedSetElement <T> newElement=new SortedSetElement <T>((T)e, prevElement); // создаем новый узел
			switch (compareResult)
			{
			case 0: // если равно - добавляем к списку в узле с таким же значением
				thisElement.addToEquals(e);
				System.out.println("Added " + e + " to element <" + thisElement.getValue_() + "> eq list");
				break; 
			case 1: // если в узле значение больше  - добавляем налево
				prevElement.setLeftElement_(newElement);
				System.out.println("Added " + e + " to the left of element <" + prevElement.getValue_() + ">");
				break;
			case -1: // если в узле значение меньше  - добавляем направо
				prevElement.setRightElement_(newElement);
				System.out.println("Added " + e + " to the right of element <" + prevElement.getValue_() + ">");
				break;			
			} // end switch
		}	// end else (root != null)
	}

	@Override
	public boolean remove(Comparable<?> o) {
		SortedSetElement<T> removingElement=this.findElementByVal((T)o);
		if (removingElement == null)
			return false; // если элемент не найден
		if (removingElement.delLastFromEqualsList()) // пробуем удалить последний из списка схожих
			return true; // если в списке схожих элементов был хотя бы один - оk
		return this.removeByElement(removingElement); // или удаляем найденный элемент
	}

	@Override
	public boolean contains(Comparable <?> o) {
		if (this.findElementByVal((T) o) != null)
			return true;
		return false;
	}
	
	public SortedSetIterator iterator()
	{
		SortedSetIterator iter=new SortedSetIterator();
		return iter;
	}

}
