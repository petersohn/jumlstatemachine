package com.kangirigungi.jstatemachine.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.junit.Assert;
import org.junit.Test;


public class Test1 {
	@Test
	public void proba() {}

	@Test
    public void castOk() {
		List<String> l = new ArrayList<String>();
		l.add("hello world");
		ArrayList<String> al = (ArrayList<String>)l;
		Assert.assertTrue(al != null);
		Assert.assertTrue(al.get(0).equals("hello world"));
    }

	@Test
	public void castNok() {
		List<String> l = new ArrayList<String>();
		l.add("hello world");
		Vector<String> al = (Vector<String>)l;
		Assert.assertTrue(al == null);
    }
}
