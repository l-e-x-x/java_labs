package ru.spbstu.telematics.student_Finagin.lab_02_sorted_set;

/**
 * Коллекция-множество хранит данные в упорядоченном порядке
 */
public interface ISortedSet <T extends Comparable<T>> {
  /**
   * Добавить элемент в дерево
   * @param e
   */
  void add(Comparable<T> e);
  /**
   * Удалить элемент из дерева
   */
  boolean remove(Comparable<T> o);
  /**
   * Возвращает true, если элемент содержится в дереве
   */
  boolean contains(Comparable<T> o);
}