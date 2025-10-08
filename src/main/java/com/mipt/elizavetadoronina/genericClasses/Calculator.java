public class Calculator<T extends Number> {
  public double sum(T a, T b) {
    double aDoub = (a != null) ? a.doubleValue() : 0.0;
    double bDoub = (b != null) ? b.doubleValue() : 0.0;
    return aDoub + bDoub;
  }

  public double subtract(T a, T b) {
    double aDoub = (a != null) ? a.doubleValue() : 0.0;
    double bDoub = (b != null) ? b.doubleValue() : 0.0;
    return aDoub - bDoub;
  }

  public double multiply(T a, T b) {
    double aDoub = (a != null) ? a.doubleValue() : 0.0;
    double bDoub = (b != null) ? b.doubleValue() : 0.0;
    return aDoub * bDoub;
  }

  public double divide(T a, T b) {
    double aDoub = (a != null) ? a.doubleValue() : 0.0;
    double bDoub = (b != null) ? b.doubleValue() : 0.0;
    if (b.doubleValue() == 0.0) {
      return Double.NaN;
    }
    return aDoub / bDoub;
  }


  public static void main(String[] args) {
    final Calculator<Integer> intCalc = new Calculator<>();
    final double result = intCalc.sum(5, null);
    System.out.println(result);

    final Calculator<Double> doubCalc = new Calculator<>();
    final double div = doubCalc.divide(10.0, 4.0);
    System.out.println(div);
  }
}