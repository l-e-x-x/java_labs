package ru.spbstu.telematics.student_Finagin.lab_02_sorted_set;

/**
 * Коллекция-множество хранит данные в упорядоченном порядке
 */
public interface ISortedSet {
  /**
   * Добавить элемент в дерево
   * @param e
   */
  void add(Comparable<?> e);
  /**
   * Удалить элемент из дерева
   */
  boolean remove(Comparable o);
  /**
   * Возвращает true, если элемент содержится в дереве
   */
  boolean contains(Comparable<?> o);
}