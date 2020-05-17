package com.gwt;

import com.gwt.client.GwtTest;
import com.google.gwt.junit.tools.GWTTestSuite;
import junit.framework.Test;
import junit.framework.TestSuite;

public class GwtSuite extends GWTTestSuite {
	public static Test suite() {
		TestSuite suite = new TestSuite("Tests for Gwt");
		suite.addTestSuite(GwtTest.class);
		return suite;
	}
}
