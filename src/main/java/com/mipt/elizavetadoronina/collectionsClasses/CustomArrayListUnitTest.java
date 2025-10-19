public class CustomArrayListUnitTest {

  private static int testCount = 0;
  private static int passedCount = 0;

  public static void main(String[] args) {
    System.out.println("Starting Comprehensive CustomArrayList Tests...\n");

    try {
      testAddAndGet();
      testRemove();
      testSizeAndIsEmpty();
      testDynamicExpansion();
      testIterator();
      testNullElement();
      testInvalidIndex();
      testInitialCapacity();
      testMultipleOperations();
      testForEachLoop();
      testGenericTypes();

      System.out.println("\n=== TEST RESULTS ===");
      System.out.println("Total tests: " + testCount);
      System.out.println("Passed: " + passedCount);
      System.out.println("Failed: " + (testCount - passedCount));

      if (passedCount == testCount) {
        System.out.println("\nALL TESTS PASSED!");
      } else {
        System.out.println("\nSOME TESTS FAILED!");
        System.exit(1);
      }

    } catch (Exception e) {
      System.out.println("\nERROR: " + e.getMessage());
      e.printStackTrace();
      System.exit(1);
    }
  }

  private static void testAddAndGet() {
    printTestHeader("Testing add() and get() methods");

    CustomList<String> list = new CustomArrayList<>();

    list.add("first");
    list.add("second");
    list.add("third");

    assertEquals("first", list.get(0), "get(0) should return 'first'");
    assertEquals("second", list.get(1), "get(1) should return 'second'");
    assertEquals("third", list.get(2), "get(2) should return 'third'");
    assertEquals(3, list.size(), "size should be 3 after adding 3 elements");

    CustomList<Integer> intList = new CustomArrayList<>();
    intList.add(100);
    intList.add(200);
    assertEquals(Integer.valueOf(100), intList.get(0), "Integer list get(0)");
    assertEquals(Integer.valueOf(200), intList.get(1), "Integer list get(1)");
  }

  private static void testRemove() {
    printTestHeader("Testing remove() method");

    CustomList<String> list = new CustomArrayList<>();
    list.add("A");
    list.add("B");
    list.add("C");
    list.add("D");

    String removed = list.remove(1);
    assertEquals("B", removed, "remove(1) should return 'B'");
    assertEquals(3, list.size(), "size should be 3 after removal");
    assertEquals("A", list.get(0), "get(0) after remove from middle");
    assertEquals("C", list.get(1), "get(1) after remove from middle");
    assertEquals("D", list.get(2), "get(2) after remove from middle");

    removed = list.remove(0);
    assertEquals("A", removed, "remove(0) should return 'A'");
    assertEquals(2, list.size(), "size should be 2 after removing first");
    assertEquals("C", list.get(0), "get(0) after removing first");
    assertEquals("D", list.get(1), "get(1) after removing first");

    removed = list.remove(1);
    assertEquals("D", removed, "remove(1) should return 'D'");
    assertEquals(1, list.size(), "size should be 1 after removing last");
    assertEquals("C", list.get(0), "get(0) after removing last");
  }

  private static void testSizeAndIsEmpty() {
    printTestHeader("Testing size() and isEmpty() methods");

    CustomList<String> list = new CustomArrayList<>();

    assertTrue(list.isEmpty(), "New list should be empty");
    assertEquals(0, list.size(), "New list size should be 0");

    list.add("element");
    assertFalse(list.isEmpty(), "List should not be empty after add");
    assertEquals(1, list.size(), "Size should be 1 after one add");

    list.remove(0);
    assertTrue(list.isEmpty(), "List should be empty after removing all elements");
    assertEquals(0, list.size(), "Size should be 0 after removing all elements");
  }

  private static void testDynamicExpansion() {
    printTestHeader("Testing dynamic array expansion");

    CustomArrayList<Integer> list = new CustomArrayList<>(3);
    assertEquals(3, list.capacity(), "Initial capacity should be 3");

    list.add(1);
    list.add(2);
    list.add(3);
    assertEquals(3, list.size(), "Size should be 3 after filling initial capacity");
    assertEquals(3, list.capacity(), "Capacity should still be 3");

    list.add(4);
    assertTrue(list.capacity() > 3, "Capacity should increase after expansion");
    assertEquals(4, list.size(), "Size should be 4 after expansion");
    assertEquals(Integer.valueOf(1), list.get(0), "Element integrity after expansion");
    assertEquals(Integer.valueOf(2), list.get(1), "Element integrity after expansion");
    assertEquals(Integer.valueOf(3), list.get(2), "Element integrity after expansion");
    assertEquals(Integer.valueOf(4), list.get(3), "Element integrity after expansion");
  }

  private static void testIterator() {
    printTestHeader("Testing iterator functionality");

    CustomList<String> list = new CustomArrayList<>();
    list.add("X");
    list.add("Y");
    list.add("Z");

    java.util.Iterator<String> iterator = list.iterator();
    assertTrue(iterator.hasNext(), "Iterator should have next on non-empty list");
    assertEquals("X", iterator.next(), "First iterator element");
    assertEquals("Y", iterator.next(), "Second iterator element");
    assertEquals("Z", iterator.next(), "Third iterator element");
    assertFalse(iterator.hasNext(), "Iterator should not have next after all elements");

    try {
      iterator.next();
      fail("Iterator should throw NoSuchElementException when no more elements");
    } catch (java.util.NoSuchElementException e) {
      passTest("Correctly threw NoSuchElementException");
    }

    CustomList<String> emptyList = new CustomArrayList<>();
    iterator = emptyList.iterator();
    assertFalse(iterator.hasNext(), "Iterator on empty list should not have next");
  }

  private static void testNullElement() {
    printTestHeader("Testing null element rejection");

    CustomList<String> list = new CustomArrayList<>();

    try {
      list.add(null);
      fail("Should have thrown IllegalArgumentException for null element");
    } catch (IllegalArgumentException e) {
      passTest("Correctly threw IllegalArgumentException for null element");
    }
  }

  private static void testInvalidIndex() {
    printTestHeader("Testing invalid index handling");

    CustomList<String> list = new CustomArrayList<>();
    list.add("test");

    testInvalidGet(list, -1, "negative index");
    testInvalidGet(list, 1, "out-of-bounds index");
    testInvalidGet(list, 100, "large out-of-bounds index");

    testInvalidRemove(list, -1, "negative index");
    testInvalidRemove(list, 1, "out-of-bounds index");
    testInvalidRemove(list, 100, "large out-of-bounds index");

    CustomList<String> emptyList = new CustomArrayList<>();
    testInvalidGet(emptyList, 0, "index on empty list");
    testInvalidRemove(emptyList, 0, "index on empty list");
  }

  private static void testInitialCapacity() {
    printTestHeader("Testing initial capacity constructor");

    CustomArrayList<String> list1 = new CustomArrayList<>(5);
    assertEquals(0, list1.size(), "Size should be 0 for new list with capacity 5");
    assertEquals(5, list1.capacity(), "Capacity should be 5");

    CustomArrayList<String> list2 = new CustomArrayList<>(0);
    assertEquals(0, list2.size(), "Size should be 0 for new list with capacity 0");
    assertEquals(0, list2.capacity(), "Capacity should be 0");

    try {
      new CustomArrayList<>(-1);
      fail("Should have thrown IllegalArgumentException for negative capacity");
    } catch (IllegalArgumentException e) {
      passTest("Correctly threw IllegalArgumentException for negative capacity");
    }
  }

  private static void testMultipleOperations() {
    printTestHeader("Testing multiple operations sequence");

    CustomList<String> list = new CustomArrayList<>();

    list.add("A");
    list.add("B");
    list.add("C");
    list.remove(1); // Remove "B"
    list.add("D");
    list.add("E");
    list.remove(0); // Remove "A"
    list.add("F");

    assertEquals(3, list.size(), "Final size after mixed operations");
    assertEquals("C", list.get(0), "First element after operations");
    assertEquals("D", list.get(1), "Second element after operations");
    assertEquals("F", list.get(2), "Third element after operations");
  }

  private static void testForEachLoop() {
    printTestHeader("Testing for-each loop compatibility");

    CustomList<String> list = new CustomArrayList<>();
    list.add("one");
    list.add("two");
    list.add("three");

    StringBuilder result = new StringBuilder();
    int count = 0;

    for (String element : list) {
      result.append(element).append(" ");
      count++;
    }

    assertEquals(3, count, "Should iterate over 3 elements");
    assertTrue(result.toString().contains("one"), "Should contain 'one'");
    assertTrue(result.toString().contains("two"), "Should contain 'two'");
    assertTrue(result.toString().contains("three"), "Should contain 'three'");
  }

  private static void testGenericTypes() {
    printTestHeader("Testing generic type safety");

    CustomList<Integer> intList = new CustomArrayList<>();
    intList.add(1);
    intList.add(2);
    assertEquals(Integer.valueOf(1), intList.get(0), "Integer type safety");

    CustomList<Double> doubleList = new CustomArrayList<>();
    doubleList.add(3.14);
    assertEquals(Double.valueOf(3.14), doubleList.get(0), "Double type safety");

    CustomList<Boolean> boolList = new CustomArrayList<>();
    boolList.add(true);
    assertEquals(Boolean.TRUE, boolList.get(0), "Boolean type safety");
  }

  private static void testInvalidGet(CustomList<String> list, int index, String description) {
    try {
      list.get(index);
      fail("Should have thrown IndexOutOfBoundsException for get with " + description);
    } catch (IndexOutOfBoundsException e) {
      passTest("Correctly threw IndexOutOfBoundsException for get with " + description);
    }
  }

  private static void testInvalidRemove(CustomList<String> list, int index, String description) {
    try {
      list.remove(index);
      fail("Should have thrown IndexOutOfBoundsException for remove with " + description);
    } catch (IndexOutOfBoundsException e) {
      passTest("Correctly threw IndexOutOfBoundsException for remove with " + description);
    }
  }

  private static void assertEquals(Object expected, Object actual, String message) {
    testCount++;
    if (!java.util.Objects.equals(expected, actual)) {
      System.out.println("FAIL: " + message + " - Expected: " + expected + ", Actual: " + actual);
      throw new AssertionError(message);
    } else {
      passedCount++;
      System.out.println("PASS: " + message);
    }
  }

  private static void assertTrue(boolean condition, String message) {
    testCount++;
    if (!condition) {
      System.out.println("FAIL: " + message + " - Expected true, got false");
      throw new AssertionError(message);
    } else {
      passedCount++;
      System.out.println("PASS: " + message);
    }
  }

  private static void assertFalse(boolean condition, String message) {
    testCount++;
    if (condition) {
      System.out.println("FAIL: " + message + " - Expected false, got true");
      throw new AssertionError(message);
    } else {
      passedCount++;
      System.out.println("PASS: " + message);
    }
  }

  private static void fail(String message) {
    testCount++;
    System.out.println("FAIL: " + message);
    throw new AssertionError(message);
  }

  private static void passTest(String message) {
    testCount++;
    passedCount++;
    System.out.println("PASS: " + message);
  }

  private static void printTestHeader(String header) {
    System.out.println("\n" + "=".repeat(50));
    System.out.println(header);
    System.out.println("=".repeat(50));
  }
}