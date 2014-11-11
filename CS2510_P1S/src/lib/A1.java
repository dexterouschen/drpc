package lib;

/**
 * Interface of a library (of many libraries) supported by this server, note
 * that there is a info.txt in lib directory, it's for keeping records of all
 * supported procedures, and later used for registration
 * 
 * @author dexterchen
 *
 */
public interface A1 {

	// o Matrix multiplication : Multiply( A[n,m], B[m,l], C[n,l]),
	// o Vector sorting: Sort(vi , 0 ≤ i ≤ n-1),
	// o Compute minimum: Min(xi , 0 ≤ i ≤ n-1), and
	// o Compute maximum: Max(xi , 0 ≤ i ≤ n-1).

	/**
	 * Matrix Multiplication
	 * 
	 * @param A
	 *            Matrix A
	 * @param B
	 *            Matrix B
	 * @return Matrix C
	 */
	double[][] Multiply(double[][] A, double[][] B, A1 a1);

	/**
	 * Sorting, constructing is similar to Multiply
	 * 
	 * @param v
	 *            array to be sorted
	 * @return sorted array
	 */
	double[] Sort(double[] v, A1 a1);

	/**
	 * Find the minimum in an array, constructing is similar to Multiply
	 * 
	 * @param x
	 *            array
	 * @return minimum
	 */
	double Min(double[] x, A1 a1);

	/**
	 * Find the maximum in an array, constructing is similar to Multiply
	 * 
	 * @param x
	 *            array
	 * @return maximum
	 */
	double Max(double[] x, A1 a1);

	// String func(String s1, String s2, A1 x);

	// int func2(int i1, float f1, A1 x);

}
