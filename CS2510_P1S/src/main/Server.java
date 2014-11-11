package main;

import java.util.Arrays;

import lib.A1;

import org.ejml.simple.SimpleMatrix;

/**
 * The server class that contains all the actual implementations of procedures,
 * it's the only part the developer of the server side should worry about, the
 * request handling part is decoupled and put in separated class. It also
 * implements all the interfaces of all the libraries supported, and in Java, if
 * you implements a interface, you must implements all procedures defined in the
 * interface, so this mechanism can be used as a reminder for the developer of
 * the server side to implement all the procedures supported
 * 
 * @author dexterchen
 *
 */
public class Server extends ServerRequestHandlerV2 implements A1 {
	// extends ServerRequestHandler implements A1 {

	public static void main(String[] args) {
		/**
		 * Set the class name of the server, needed for Java Reflection
		 */
		ServerRequestHandlerV2.serverClass = Server.class.getCanonicalName();
		/**
		 * Activate the server request handler
		 */
		ServerRequestHandlerV2.run();
	}

	/**
	 * Implementation of Min, A1 a1 here is a dummy parameter to differentiate
	 * Min of A1 from other Min(s) from other libraries, used for Java
	 * Reflection
	 */
	@Override
	public double Min(double[] x, A1 a1) {
		// TODO Auto-generated method stub
		Arrays.sort(x);
		return x[0];
	}

	/**
	 * Implementation of Max, A1 a1 here is a dummy parameter to differentiate
	 * Max of A1 from other Max(s) from other libraries, used for Java
	 * Reflection
	 */
	@Override
	public double Max(double[] x, A1 a1) {
		// TODO Auto-generated method stub
		Arrays.sort(x);
		return x[x.length - 1];
	}

	/**
	 * Implementation of Multiply, A1 a1 here is a dummy parameter to
	 * differentiate Multiply of A1 from other Multiply(s) from other libraries,
	 * used for Java Reflection
	 */
	@Override
	public double[][] Multiply(double[][] A, double[][] B, A1 a1) {
		// TODO Auto-generated method stub
		try {
			SimpleMatrix m1 = new SimpleMatrix(A);
			SimpleMatrix m2 = new SimpleMatrix(B);

			SimpleMatrix m3 = m2.mult(m1);// m1 X m2
			double[][] retMat = new double[m3.numRows()][m3.numCols()];
			for (int i = 0; i < retMat.length; i++) {
				for (int j = 0; j < retMat[0].length; j++) {
					retMat[i][j] = m3.get(i, j);
				}
			}
			return retMat;
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}

	/**
	 * Implementation of Sort, A1 a1 here is a dummy parameter to differentiate
	 * Sort of A1 from other Sort(s) from other libraries, used for Java
	 * Reflection
	 */
	@Override
	public double[] Sort(double[] v, A1 a1) {
		// TODO Auto-generated method stub
		Arrays.sort(v);
		return v;
	}
}
