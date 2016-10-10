package de.dfki.owlsmx.analysis;

public class PassedTime {	
	
	private static long time = System.currentTimeMillis();
	
	public static long getTime(String clazz) {
		long tmpTime = System.currentTimeMillis();
//		System.out.println(clazz + " " + (tmpTime-time) );
		return (tmpTime-time);
	}
	
	public static void start() {
		time = System.currentTimeMillis();
	}
}
