package it.uniroma1.di.simulejos;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

final class VirtualClassLoader extends URLClassLoader {
	private static final Manifest manifest;
	private static final Set<String> packages = Collections
			.synchronizedSet(new HashSet<String>());
	private static final Map<String, byte[]> classes = new ConcurrentHashMap<String, byte[]>();
	private static final Map<String, byte[]> resources = new ConcurrentHashMap<String, byte[]>();

	private static byte[] readAll(InputStream is) throws IOException {
		final byte[] buffer = new byte[0x1000];
		final ByteArrayOutputStream baos = new ByteArrayOutputStream(
				is.available());
		int read = 0;
		while ((read = is.read(buffer)) > 0) {
			baos.write(buffer, 0, read);
		}
		return baos.toByteArray();
	}

	static {
		try {
			final JarInputStream jis = new JarInputStream(
					VirtualClassLoader.class
							.getResourceAsStream("Framework.jar"));
			manifest = jis.getManifest();
			JarEntry entry = jis.getNextJarEntry();
			while (entry != null) {
				if (!entry.isDirectory()) {
					String name = entry.getName();
					if (name.endsWith(".class")) {
						name = name.substring(0,
								name.length() - ".class".length()).replaceAll(
								"\\/", ".");
						classes.put(name, readAll(jis));
					} else {
						resources.put(name, readAll(jis));
					}
				}
				entry = jis.getNextJarEntry();
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public VirtualClassLoader(URL[] urls) {
		super(urls, VirtualClassLoader.class.getClassLoader());
		for (String name : packages) {
			definePackage(name, manifest, null);
		}
	}

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		final byte[] bytes = classes.get(name);
		if (bytes != null) {
			return defineClass(name, bytes, 0, bytes.length);
		} else {
			return super.findClass(name);
		}
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

	@Override
	public InputStream getResourceAsStream(String name) {
		final byte[] bytes = resources.get(name);
		if (bytes != null) {
			return new ByteArrayInputStream(bytes);
		} else {
			return super.getResourceAsStream(name);
		}
	}
}
