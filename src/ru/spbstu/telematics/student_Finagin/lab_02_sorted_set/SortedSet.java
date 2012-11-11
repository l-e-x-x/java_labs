package ru.spbstu.telematics.student_Finagin.lab_02_sorted_set;

/*Класс дерева сортировки (сортированное множество)*/

public class SortedSet <T extends Comparable> implements ISortedSet {  

	SortedSetElement <T> rootElement_; // корень дерева сортировки
	
		/* Класс итератора */
	public class SortedSetIterator implements ISortedSetIterator <Comparable>{
		
		SortedSetElement<T> nextElement_; // следующий элемент (вычисляется методом next())
		SortedSetElement<T> currentElement_; // текущий элемент (для метода remove())
		
		int equalElementIndex_=0; // индекс элемента в списке equals (равных элементов)  
		
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
		
		public T next() {
			currentElement_=nextElement_;
			T nextEqual=currentElement_.getFromEqualsList(this.equalElementIndex_);
			if (nextEqual != null)
			{	
				this.equalElementIndex_++;
				return nextEqual;
			}
			
			this.equalElementIndex_=0;
			if (currentElement_.getRightElement_() != null)
				nextElement_=this.getMinLeaf(currentElement_.getRightElement_());
			else
				nextElement_=this.getRootOfContainingLeftSubtree(currentElement_);
			return currentElement_.getValue_();
		}
		
		@Override
		public boolean hasNext() {
			if (nextElement_ == null)
				return false;
			return true;
		}
		
		@Override
		public void remove() {
			// TODO Auto-generated method stub
			
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

	 /* Интерфейс сортированного множества*/
	@Override
	public void add(Comparable<?> e) {
		if (rootElement_== null) // если корня нет
			rootElement_=new SortedSetElement <T> ((T) e,null); // создаем корень
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
	public boolean remove(Comparable o) {
		// TODO Auto-generated method stub
		return false;
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
