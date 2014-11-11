package lib;

import java.util.ArrayList;

import main.Client2Mapper;
import main.Client2Server;
import main.ClientRequestHandlerV3;
import spec.Param;

/**
 * Client side library, called by the client. Without the inheritance from
 * ClientRequestHandler, these procedures contains no implementations thus do no
 * good for the user, once it inherits functionalities from the client request
 * handler, these two can act as a whole to serve the need of handling of each
 * request from client side
 * 
 * @author dexterchen
 *
 */
public class A1 extends ClientRequestHandlerV3 {
	/**
	 * Matrix Multiplication
	 * 
	 * @param A
	 *            Matrix A
	 * @param B
	 *            Matrix B
	 * @return Matrix C
	 */
	public double[][] Multiply(double[][] A, double[][] B) {

		/**
		 * Generate parameter list, Param: <String: TYPE, Object: VALUE>
		 */
		ArrayList<Param> paralist = new ArrayList<>();
		paralist.add(new Param(double[][].class.getCanonicalName(), A));
		paralist.add(new Param(double[][].class.getCanonicalName(), B));
		/**
		 * Construct Client2Server message object
		 */
		Client2Server clientRequestToServer = new Client2Server("A", "1",
				"Multiply", paralist);
		/**
		 * Construct Client2Mapper message object
		 */
		Client2Mapper clientRequestToMapper = new Client2Mapper();
		/**
		 * Set uid (programId+version+procId+TYPELIST)
		 */
		clientRequestToMapper.uid = clientRequestToServer.toString();
		/**
		 * hand over messages to handler and wait for returned result
		 */
		return (double[][]) super.getResult(clientRequestToMapper,
				clientRequestToServer);
	}

	/**
	 * Sorting, constructing is similar to Multiply
	 * 
	 * @param v
	 *            array to be sorted
	 * @return sorted array
	 */
	public double[] Sort(double[] v) {

		ArrayList<Param> paralist = new ArrayList<>();
		paralist.add(new Param(double[].class.getCanonicalName(), v));

		Client2Server clientRequestToServer = new Client2Server("A", "1",
				"Sort", paralist);

		Client2Mapper clientRequestToMapper = new Client2Mapper();

		clientRequestToMapper.uid = clientRequestToServer.toString();

		return (double[]) super.getResult(clientRequestToMapper,
				clientRequestToServer);
	}

	/**
	 * Find the minimum in an array, constructing is similar to Multiply
	 * 
	 * @param x
	 *            array
	 * @return minimum
	 */
	public double Min(double[] x) {

		ArrayList<Param> paralist = new ArrayList<>();
		paralist.add(new Param(double[].class.getCanonicalName(), x));

		Client2Server clientRequestToServer = new Client2Server("A", "1",
				"Min", paralist);

		Client2Mapper clientRequestToMapper = new Client2Mapper();

		clientRequestToMapper.uid = clientRequestToServer.toString();

		return (double) super.getResult(clientRequestToMapper,
				clientRequestToServer);
	}

	/**
	 * Find the maximum in an array, constructing is similar to Multiply
	 * 
	 * @param x
	 *            array
	 * @return maximum
	 */
	public double Max(double[] x) {

		ArrayList<Param> paralist = new ArrayList<>();
		paralist.add(new Param(double[].class.getCanonicalName(), x));

		Client2Server clientRequestToServer = new Client2Server("A", "1",
				"Max", paralist);

		Client2Mapper clientRequestToMapper = new Client2Mapper();

		clientRequestToMapper.uid = clientRequestToServer.toString();

		return (double) super.getResult(clientRequestToMapper,
				clientRequestToServer);
	}
}
