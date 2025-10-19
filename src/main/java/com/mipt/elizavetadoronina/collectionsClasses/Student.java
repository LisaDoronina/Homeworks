import java.util.Objects;

public class Student implements Comparable<Student> {
  private final int id;
  private final String name;
  private final double grade;

  public Student(int id, String name, double grade) {
    if (id < 0) {
      throw new IllegalArgumentException("ID cannot be negative");
    }
    if (name == null || name.trim().isEmpty()) {
      throw new IllegalArgumentException("Name cannot be null or empty");
    }
    if (grade < 0 || grade > 10) {
      throw new IllegalArgumentException("Grade must be between 0 and 10");
    }

    this.id = id;
    this.name = name.trim();
    this.grade = grade;
  }

  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public double getGrade() {
    return grade;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null || getClass() != obj.getClass()) return false;

    Student student = (Student) obj;
    return id == student.id;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Override
  public int compareTo(Student other) {
    return Integer.compare(this.id, other.id);
  }

  @Override
  public String toString() {
    return String.format("Student{id=%d, name='%s', grade=%.2f}", id, name, grade);
  }
}