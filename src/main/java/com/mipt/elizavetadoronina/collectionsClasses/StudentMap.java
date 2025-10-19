import java.util.*;
import java.util.stream.Collectors;

public class StudentMap {

  public static List<Student> findStudentsByGradeRange(Map<Integer, Student> map,
                                                       double minGrade, double maxGrade) {
    if (map == null) {
      throw new IllegalArgumentException("Map cannot be null");
    }
    if (minGrade > maxGrade) {
      throw new IllegalArgumentException("minGrade cannot be greater than maxGrade");
    }
    if (minGrade < 0 || maxGrade > 10) {
      throw new IllegalArgumentException("Grades must be between 0 and 10");
    }

    return map.values().stream()
            .filter(student -> student.getGrade() >= minGrade && student.getGrade() <= maxGrade)
            .collect(Collectors.toList());
  }

  public static List<Student> getTopNStudents(TreeMap<Integer, Student> treeMap, int n) {
    if (treeMap == null) {
      throw new IllegalArgumentException("TreeMap cannot be null");
    }
    if (n <= 0) {
      throw new IllegalArgumentException("Number must be positive");
    }
    if (treeMap.isEmpty()) {
      return new ArrayList<>();
    }

    return treeMap.values().stream()
            .limit(n)
            .collect(Collectors.toList());
  }

  public static TreeMap<Integer, Student> createTreeMapDescending() {
    return new TreeMap<>(Collections.reverseOrder());
  }

  public static void main(String[] args) {

    Map<Integer, Student> hashMap = new HashMap<>();
    hashMap.put(101, new Student(101, "vdfdf", 8.5));
    hashMap.put(102, new Student(102, "vfdfffd", 7.2));
    hashMap.put(103, new Student(103, "wfwe", 9.1));
    hashMap.put(104, new Student(104, "Аfweef", 6.8));
    hashMap.put(105, new Student(105, "eweeww", 8.9));
    hashMap.put(106, new Student(106, "gtgg", 7.5));

    System.out.println("HashMap студентов:");
    hashMap.forEach((id, student) ->
            System.out.println("  " + student));

    TreeMap<Integer, Student> treeMap = createTreeMapDescending();
    treeMap.putAll(hashMap);

    System.out.println("\nTreeMap (сортировка по убыванию id):");
    treeMap.forEach((id, student) ->
            System.out.println("  " + student));

    System.out.println("\n--- Поиск по диапазону оценок ---");

    double minGrade = 8.0;
    double maxGrade = 9.5;
    List<Student> studentsInRange = findStudentsByGradeRange(hashMap, minGrade, maxGrade);

    System.out.printf("Студенты с оценкой от %.1f до %.1f:\n", minGrade, maxGrade);
    if (studentsInRange.isEmpty()) {
      System.out.println("  Студенты не найдены");
    } else {
      studentsInRange.forEach(student ->
              System.out.println("  " + student));
    }

    System.out.println("\n--- Топ-N студентов по id ---");

    int topN = 3;
    List<Student> topStudents = getTopNStudents(treeMap, topN);

    System.out.printf("Топ студентов с наибольшими id:\n", topN);
    topStudents.forEach(student ->
            System.out.println("  " + student));

    System.out.println("\n--- Дополнительные тесты ---");

    testGradeRangeBoundaries(hashMap);

    testVariousNValues(treeMap);

    testEqualsAndHashCode();
  }

  private static void testGradeRangeBoundaries(Map<Integer, Student> map) {
    System.out.println("Тест граничных значений:");

    List<Student> allStudents = findStudentsByGradeRange(map, 0.0, 10.0);
    System.out.printf("Все студенты (0.0-10.0): %d студентов\n", allStudents.size());

    List<Student> highGrades = findStudentsByGradeRange(map, 9.0, 10.0);
    System.out.printf("Отличники (9.0-10.0): %d студентов\n", highGrades.size());
    highGrades.forEach(student ->
            System.out.println("  " + student));

    List<Student> lowGrades = findStudentsByGradeRange(map, 6.0, 7.0);
    System.out.printf("Студенты с низкими оценками (6.0-7.0): %d студентов\n", lowGrades.size());
    lowGrades.forEach(student ->
            System.out.println("  " + student));
  }

  private static void testVariousNValues(TreeMap<Integer, Student> treeMap) {
    System.out.println("\nТест различных значений N:");

    for (int n : new int[]{1, 2, 5, 10}) {
      List<Student> topN = getTopNStudents(treeMap, n);
      System.out.printf("Топ-%d: %d студентов\n", n, topN.size());
      if (!topN.isEmpty()) {
        System.out.printf("  Максимальный id: %d\n", topN.get(0).getId());
      }
    }
  }

  private static void testEqualsAndHashCode() {
    System.out.println("\n--- Тестирование equals и hashCode ---");

    Student student1 = new Student(1, "Тест Студент", 8.0);
    Student student2 = new Student(1, "Тест Студент", 8.0);
    Student student3 = new Student(2, "Тест Студент", 8.0);

    System.out.println("student1: " + student1);
    System.out.println("student2: " + student2 + " (тот же id)");
    System.out.println("student3: " + student3 + " (другой id)");

    System.out.println("student1.equals(student2): " + student1.equals(student2));
    System.out.println("student1.equals(student3): " + student1.equals(student3));
    System.out.println("student1.hashCode() == student2.hashCode(): " +
            (student1.hashCode() == student2.hashCode()));
    System.out.println("student1.hashCode() == student3.hashCode(): " +
            (student1.hashCode() == student3.hashCode()));

    Map<Integer, Student> testMap = new HashMap<>();
    testMap.put(student1.getId(), student1);
    testMap.put(student2.getId(), student2);

    System.out.println("Размер map после добавления student1 и student2: " + testMap.size());
    System.out.println("student1 == student2 в HashMap: " +
            (testMap.get(1) == student2));
  }
}