import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class CollectionPerformanceTester {

  private static final int ELEMENT_COUNT = 10000;

  public static void main(String[] args) {

    List<Integer> arrayList = new ArrayList<>();
    List<Integer> linkedList = new LinkedList<>();

    testAddToEnd(arrayList, linkedList);
    testAddToBeginning(arrayList, linkedList);
    testInsertInMiddle(arrayList, linkedList);
    testRandomAccess(arrayList, linkedList);
    testRemoveFromBeginning(arrayList, linkedList);
    testRemoveFromEnd(arrayList, linkedList);
  }

  private static void testAddToEnd(List<Integer> arrayList, List<Integer> linkedList) {
    System.out.println("1. Добавление в конец:");

    long startTime = System.nanoTime();
    for (int i = 0; i < ELEMENT_COUNT; i++) {
      arrayList.add(i);
    }
    long arrayListTime = System.nanoTime() - startTime;

    startTime = System.nanoTime();
    for (int i = 0; i < ELEMENT_COUNT; i++) {
      linkedList.add(i);
    }
    long linkedListTime = System.nanoTime() - startTime;

    printResults("ArrayList", "LinkedList", arrayListTime, linkedListTime);
    clearCollections(arrayList, linkedList);
  }

  private static void testAddToBeginning(List<Integer> arrayList, List<Integer> linkedList) {
    System.out.println("2. Добавление в начало:");

    long startTime = System.nanoTime();
    for (int i = 0; i < ELEMENT_COUNT; i++) {
      arrayList.add(0, i);
    }
    long arrayListTime = System.nanoTime() - startTime;

    startTime = System.nanoTime();
    for (int i = 0; i < ELEMENT_COUNT; i++) {
      linkedList.add(0, i);
    }
    long linkedListTime = System.nanoTime() - startTime;

    printResults("ArrayList", "LinkedList", arrayListTime, linkedListTime);
    clearCollections(arrayList, linkedList);
  }

  private static void testInsertInMiddle(List<Integer> arrayList, List<Integer> linkedList) {
    System.out.println("3. Вставка в середину:");

    fillCollections(arrayList, linkedList, ELEMENT_COUNT / 2);

    long startTime = System.nanoTime();
    for (int i = 0; i < 1000; i++) { // Меньше элементов для вставки в середину
      arrayList.add(arrayList.size() / 2, i);
    }
    long arrayListTime = System.nanoTime() - startTime;

    startTime = System.nanoTime();
    for (int i = 0; i < 1000; i++) {
      linkedList.add(linkedList.size() / 2, i);
    }
    long linkedListTime = System.nanoTime() - startTime;

    printResults("ArrayList", "LinkedList", arrayListTime, linkedListTime);
    clearCollections(arrayList, linkedList);
  }

  private static void testRandomAccess(List<Integer> arrayList, List<Integer> linkedList) {
    System.out.println("4. Доступ по индексу:");

    fillCollections(arrayList, linkedList, ELEMENT_COUNT);

    long startTime = System.nanoTime();
    for (int i = 0; i < 1000; i++) {
      int index = (int) (Math.random() * ELEMENT_COUNT);
      arrayList.get(index);
    }
    long arrayListTime = System.nanoTime() - startTime;

    startTime = System.nanoTime();
    for (int i = 0; i < 1000; i++) {
      int index = (int) (Math.random() * ELEMENT_COUNT);
      linkedList.get(index);
    }
    long linkedListTime = System.nanoTime() - startTime;

    printResults("ArrayList", "LinkedList", arrayListTime, linkedListTime);
    clearCollections(arrayList, linkedList);
  }

  private static void testRemoveFromBeginning(List<Integer> arrayList, List<Integer> linkedList) {
    System.out.println("5. Удаление из начала:");

    fillCollections(arrayList, linkedList, ELEMENT_COUNT);
    long startTime = System.nanoTime();
    while (!arrayList.isEmpty()) {
      arrayList.remove(0);
    }
    long arrayListTime = System.nanoTime() - startTime;

    fillCollections(arrayList, linkedList, ELEMENT_COUNT);
    startTime = System.nanoTime();
    while (!linkedList.isEmpty()) {
      linkedList.remove(0);
    }
    long linkedListTime = System.nanoTime() - startTime;

    printResults("ArrayList", "LinkedList", arrayListTime, linkedListTime);
    clearCollections(arrayList, linkedList);
  }

  private static void testRemoveFromEnd(List<Integer> arrayList, List<Integer> linkedList) {
    System.out.println("6. Удаление из конца:");

    // Тест ArrayList
    fillCollections(arrayList, linkedList, ELEMENT_COUNT);
    long startTime = System.nanoTime();
    while (!arrayList.isEmpty()) {
      arrayList.remove(arrayList.size() - 1);
    }
    long arrayListTime = System.nanoTime() - startTime;

    fillCollections(arrayList, linkedList, ELEMENT_COUNT);
    startTime = System.nanoTime();
    while (!linkedList.isEmpty()) {
      linkedList.remove(linkedList.size() - 1);
    }
    long linkedListTime = System.nanoTime() - startTime;

    printResults("ArrayList", "LinkedList", arrayListTime, linkedListTime);
    clearCollections(arrayList, linkedList);
  }

  private static void fillCollections(List<Integer> arrayList, List<Integer> linkedList, int count) {
    clearCollections(arrayList, linkedList);
    for (int i = 0; i < count; i++) {
      arrayList.add(i);
      linkedList.add(i);
    }
  }

  private static void clearCollections(List<Integer> arrayList, List<Integer> linkedList) {
    arrayList.clear();
    linkedList.clear();
  }


  private static void printResults(String firstCollection, String secondCollection,
                                   long firstTime, long secondTime) {

    double firstTimeMs = firstTime / 1000000.0;
    double secondTimeMs = secondTime / 1000000.0;

    String winner;
    if (firstTimeMs < secondTimeMs) {
      winner = firstCollection + " быстрее в " + String.format("%.2f", secondTimeMs / firstTimeMs) + " раз";
    } else {
      winner = secondCollection + " быстрее в " + String.format("%.2f", firstTimeMs / secondTimeMs) + " раз";
    }
    System.out.println("Результат: " + winner + "\n");
  }
}