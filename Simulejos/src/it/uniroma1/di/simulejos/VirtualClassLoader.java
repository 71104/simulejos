package it.uniroma1.di.simulejos;

import java.net.URL;
import java.net.URLClassLoader;

final class VirtualClassLoader extends URLClassLoader {
	VirtualClassLoader(URL[] urls) {
		super(urls);
	}

	@Override
	protected synchronized Class<?> loadClass(String name, boolean resolve)
			throws ClassNotFoundException {
		Class<?> c = findLoadedClass(name);
		if (c == null) {
			try {
				c = findClass(name);
			} catch (ClassNotFoundException e) {
				c = getParent().loadClass(name);
			}
		}
		if (resolve) {
			resolveClass(c);
		}
		return c;
	}

	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException {
		return loadClass(name, false);
	}
}
