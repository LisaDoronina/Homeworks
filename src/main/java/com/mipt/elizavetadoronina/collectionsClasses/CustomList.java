public interface CustomList<A> extends Iterable<A> {
  void add(A element);
  A get(int idx);
  A remove(int idx);
  int size();
  boolean isEmpty();
}