import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

public class CollectionUtils {
  public static <T> List<T> mergeLists(List<? extends T> list1,
                                      List<? extends T> list2) {
    List<T> dubList = new ArrayList<>();
    dubList.addAll(list1);
    dubList.addAll(list2);
    return dubList;

  }

  public static <T> void addAll(List<? super T> destination,
                                List<? extends T> source) {
    for (T item : source) {
      destination.add(item);
    }
  }

  public static void main(String[] args) {
    final List<Integer> list1 = Arrays.asList(1, 2, 3);
    final List<Double> list2 = Arrays.asList(4.5, 5.6);
    final List<Number> merged = CollectionUtils.mergeLists(list1, list2);

    final List<Object> destination = new ArrayList<>();
    CollectionUtils.addAll(destination, list1);
  }
}