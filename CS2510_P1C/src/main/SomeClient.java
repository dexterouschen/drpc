package main;

import java.util.HashSet;
import java.util.Set;

import lib.A1;

public class SomeClient {

	static Integer hit1 = 0, hit2 = 0, hit3 = 0, miss1 = 0, miss2 = 0,
			miss3 = 0;
	static A1 a1 = new A1();

	static void testMatrix(int size) {
		double[][] A = new double[size][size];

		double[][] B = new double[size][size];

		for (int i = 0; i < A.length; i++) {
			for (int j = 0; j < A[0].length; j++) {
				A[i][j] = 1;// = Math.random() * 20 + 1;
			}
		}

		for (int i = 0; i < B.length; i++) {
			for (int j = 0; j < B[0].length; j++) {
				B[i][j] = 1;// = Math.random() * 20 + 1;
			}
		}

		double[][] C = a1.Multiply(A, B);

		for (int i = 0; i < C.length; i++) {
			for (int j = 0; j < C[0].length; j++) {
				System.out.print(((int) C[i][j]) + " ");
			}
			System.out.println();
		}
	}

	/**
	 * 1
	 */
	static void singleOfEach() {

		// double[] array = new double[(int) Math.random() * 20];
		// for (int i = 0; i < array.length; i++) {
		// array[i] = Math.random() * 20;
		// }
		//
		// System.out.println(a1.Max(array));
		// System.out.println(a1.Min(array));
		// System.out.println(a1.Sort(array));

		System.out.println("Max: "
				+ a1.Max(new double[] { 37, 4, 6, 5, 4, 5, 6 }));
		System.out.println("Min: "
				+ a1.Min(new double[] { 37, 4, 6, 5, 4, 5, 6 }));

		System.out.print("Sorted: ");
		double[] result = a1.Sort(new double[] { 37, 4, 6, 5, 4, 5, 6 });
		for (int i = 0; i < result.length; i++) {
			System.out.print(result[i] + " ");
		}
		System.out.println();
	}

	/**
	 * 2
	 */
	static void forLoopOfEach() {

		double[] array = new double[(int) Math.random() * 20];
		for (int i = 0; i < array.length; i++) {
			array[i] = Math.random() * 20;
		}

		for (int i = 0; i < 1000; i++) {
			double result = a1.Max(new double[] { 37, 4, 6, 5, 4, 5, 6 });
			if (result != Double.POSITIVE_INFINITY) {
				++hit1;
				System.out.println("Max: " + result);
			} else {
				++miss1;
			}
		}

		for (int i = 0; i < 1000; i++) {
			double result = a1.Min(new double[] { 37, 4, 6, 5, 4, 5, 6 });
			if (result != Double.POSITIVE_INFINITY) {
				++hit2;
				System.out.println("Min: " + result);
			} else {
				++miss2;
			}
		}

		for (int i = 0; i < 1000; i++) {
			double[] result = a1.Sort(new double[] { 37, 4, 6, 5, 4, 5, 6 });
			if (result != null) {
				++hit3;
				System.out.print("Sorted: ");
				for (int j = 0; j < result.length; j++) {
					System.out.print(result[j] + " ");
				}
				System.out.println();
			} else {
				++miss3;
			}
		}
	}

	/**
	 * 3
	 */
	static void forLoopMix() {
		double[] array = new double[(int) Math.random() * 20];
		for (int i = 0; i < array.length; i++) {
			array[i] = Math.random() * 20;
		}
		for (int i = 0; i < 1000; i++) {
			double result1 = a1.Max(new double[] { 37, 4, 6, 5, 4, 5, 6 });
			double result2 = a1.Min(new double[] { 37, 4, 6, 5, 4, 5, 6 });
			double[] result3 = a1.Sort(new double[] { 37, 4, 6, 5, 4, 5, 6 });

			if (result1 != Double.POSITIVE_INFINITY) {
				++hit1;
				System.out.println("Max: " + result1);
			} else {
				++miss3;
			}
			if (result2 != Double.POSITIVE_INFINITY) {
				++hit2;
				System.out.println("Min: " + result2);
			} else {
				++miss3;
			}
			if (result3 != null) {
				++hit3;
				System.out.print("Sorted: ");
				for (int j = 0; j < result3.length; j++) {
					System.out.print(result3[j] + " ");
				}
				System.out.println();
			} else {
				++miss3;
			}
		}
	}

	/**
	 * 4
	 */
	static void concurrentOfEach() {
		Set<Thread> tSet1 = new HashSet<Thread>();
		Set<Thread> tSet2 = new HashSet<Thread>();
		Set<Thread> tSet3 = new HashSet<Thread>();

		double[] array = new double[(int) Math.random() * 20];
		for (int i = 0; i < array.length; i++) {
			array[i] = Math.random() * 20;
		}

		for (int i = 0; i < 1000; i++) {
			tSet1.add(new Thread() {
				@Override
				public void run() {
					double result = a1
							.Max(new double[] { 37, 4, 6, 5, 4, 5, 6 });
					if (result != Double.POSITIVE_INFINITY) {
						synchronized (hit1) {
							++hit1;
						}
						System.out.println("Max: " + result);
					} else {
						synchronized (miss1) {
							++miss1;
						}
					}
				}
			});
			tSet2.add(new Thread() {
				@Override
				public void run() {
					double result = a1
							.Min(new double[] { 37, 4, 6, 5, 4, 5, 6 });
					if (result != Double.POSITIVE_INFINITY) {
						synchronized (hit2) {
							++hit2;
						}
						System.out.println("Min: " + result);
					} else {
						synchronized (miss2) {
							++miss2;
						}
					}
				}
			});
			tSet3.add(new Thread() {
				@Override
				public void run() {
					double[] result = a1.Sort(new double[] { 37, 4, 6, 5, 4, 5,
							6 });
					if (result != null) {
						synchronized (hit3) {
							++hit3;
						}
						System.out.println("Sorted: ");
						for (int j = 0; j < result.length; j++) {
							System.out.print(result[j] + " ");
						}
						System.out.println();
					} else {
						synchronized (miss3) {
							++miss3;
						}
					}
				}
			});
		}

		tSet1.stream().parallel().forEach(x -> {
			x.run();
		});

		tSet2.stream().parallel().forEach(x -> {
			x.run();
		});

		tSet3.stream().parallel().forEach(x -> {
			x.run();
		});
	}

	/**
	 * 5
	 */
	static void concurrentMix() {
		Set<Thread> tSet1 = new HashSet<Thread>();

		double[] array = new double[(int) Math.random() * 20];
		for (int i = 0; i < array.length; i++) {
			array[i] = Math.random() * 20;
		}

		for (int i = 0; i < 1000; i++) {
			tSet1.add(new Thread() {
				@Override
				public void run() {
					double result = a1
							.Min(new double[] { 37, 4, 6, 5, 4, 5, 6 });
					if (result != Double.POSITIVE_INFINITY) {
						synchronized (hit1) {
							++hit1;
						}
						System.out.println(" Min: " + result);
					} else {
						synchronized (miss1) {
							++miss1;
						}
					}
				}
			});
			tSet1.add(new Thread() {
				@Override
				public void run() {
					double result = a1
							.Max(new double[] { 37, 4, 6, 5, 4, 5, 6 });
					if (result != Double.POSITIVE_INFINITY) {
						synchronized (hit2) {
							++hit2;
						}
						System.out.println(" Max: " + result);
					} else {
						synchronized (miss2) {
							++miss2;
						}
					}
				}
			});
			tSet1.add(new Thread() {
				@Override
				public void run() {
					double[] result = a1.Sort(new double[] { 37, 4, 6, 5, 4, 5,
							6 });
					if (result != null) {
						synchronized (hit3) {
							++hit3;
						}
						System.out.print("Sorted: ");
						for (int i = 0; i < result.length; i++) {
							System.out.print(result[i] + " ");
						}
						System.out.println();
					} else {
						synchronized (miss3) {
							++miss3;
						}
					}
				}
			});
		}

		tSet1.stream().parallel().forEach(x -> {
			x.run();
		});

	}

	public static void main(String[] args) {

		long startTime = System.currentTimeMillis();

		String choice = "";

		if (args != null && args.length > 0) {

			switch (args[0]) {
			case "1":
				choice = "1";
				singleOfEach();
				break;
			case "2":
				choice = "2";
				forLoopOfEach();
				break;
			case "3":
				choice = "3";
				forLoopMix();
				break;
			case "4":
				choice = "4";
				concurrentOfEach();
				break;
			case "5":
				choice = "5";
				concurrentMix();
				break;
			case "6":
				choice = "6";
				if (args.length == 2) {
					testMatrix(Integer.valueOf(args[1]));
				} else {
					testMatrix(1000);
				}
				break;
			}
		} else {
			testMatrix(1000);
		}

		if (!choice.equals("1") && !choice.equals("6") && !choice.equals("")) {
			System.out.println((hit1 + miss1) + "requests of Max, "
					+ ((double) hit1 / (hit1 + miss1)) * 100 + "% hit");
			System.out.println((hit2 + miss2) + "requests of Min, "
					+ ((double) hit2 / (hit2 + miss2)) * 100 + "% hit");
			System.out.println((hit3 + miss3) + "requests of Sort, "
					+ ((double) hit3 / (hit3 + miss3)) * 100 + "% hit");
		}
		long endTime = System.currentTimeMillis();

		System.out.println("in " + (endTime - startTime) / 1000.0 + "secs");

		System.exit(0);

	}
}
