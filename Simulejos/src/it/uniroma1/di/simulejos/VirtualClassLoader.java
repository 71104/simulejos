package it.uniroma1.di.simulejos;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

final class VirtualClassLoader extends URLClassLoader {
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
			final ZipInputStream zis = new JarInputStream(
					VirtualClassLoader.class
							.getResourceAsStream("Framework.jar"));
			ZipEntry entry = zis.getNextEntry();
			while (entry != null) {
				if (!entry.isDirectory()) {
					String name = entry.getName();
					if (name.endsWith(".class")) {
						name = name.substring(0,
								name.length() - ".class".length()).replaceAll(
								"\\/", ".");
						classes.put(name, readAll(zis));
					} else {
						resources.put(name, readAll(zis));
					}
				}
				entry = zis.getNextEntry();
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public VirtualClassLoader(URL[] urls) {
		super(urls, VirtualClassLoader.class.getClassLoader());
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
