import java.util.Iterator;
import java.util.NoSuchElementException;

public class CustomArrayList<A> implements CustomList<A> {

  private static final int DEFAULT_CAPACITY = 10;

  private static final double EXPANSION_FACTOR = 1.5;

  private Object[] elements;

  private int size;

  public CustomArrayList() {
    this.elements = new Object[DEFAULT_CAPACITY];
    this.size = 0;
  }

  public CustomArrayList(int initialCapacity) {
    if (initialCapacity < 0) {
      throw new IllegalArgumentException("Initial capacity cannot be negative: " + initialCapacity);
    }
    this.elements = new Object[initialCapacity];
    this.size = 0;
  }

  @Override
  public void add(A element) {
    if (element == null) {
      throw new IllegalArgumentException("Element cannot be null");
    }

    ensureCapacity();
    elements[size] = element;
    size++;
  }

  @Override
  @SuppressWarnings("unchecked")
  public A get(int index) {
    checkIndex(index);
    return (A) elements[index];
  }

  @Override
  @SuppressWarnings("unchecked")
  public A remove(int index) {
    checkIndex(index);

    A removedElement = (A) elements[index];

    for (int i = index; i < size - 1; i++) {
      elements[i] = elements[i + 1];
    }

    elements[size - 1] = null;
    size--;

    return removedElement;
  }

  @Override
  public int size() {
    return size;
  }

  @Override
  public boolean isEmpty() {
    return size == 0;
  }

  public int capacity() {
    return elements.length;
  }

  private void checkIndex(int index) {
    if (index < 0 || index >= size) {
      throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
    }
  }

  private void ensureCapacity() {
    if (size == elements.length) {
      int newCapacity = (int) (elements.length * EXPANSION_FACTOR);
      if (newCapacity <= elements.length) {
        newCapacity = elements.length + 1;
      }

      Object[] newElements = new Object[newCapacity];
      System.arraycopy(elements, 0, newElements, 0, size);
      elements = newElements;
    }
  }

  @Override
  public Iterator<A> iterator() {
    return new CustomArrayListIterator();
  }

  private class CustomArrayListIterator implements Iterator<A> {

    private int currentIndex = 0;

    @Override
    public boolean hasNext() {
      return currentIndex < size;
    }

    @Override
    @SuppressWarnings("unchecked")
    public A next() {
      if (!hasNext()) {
        throw new NoSuchElementException("No more elements in the list");
      }
      return (A) elements[currentIndex++];
    }
  }
}