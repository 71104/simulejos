package it.uniroma1.di.simulejos;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

final class VirtualClassLoader extends URLClassLoader {
	private static final Map<String, byte[]> classes = new ConcurrentHashMap<String, byte[]>();

	static {
		try {
			final ZipInputStream zis = new JarInputStream(
					VirtualClassLoader.class
							.getResourceAsStream("Framework.jar"));
			final byte[] buffer = new byte[0x1000];
			ZipEntry entry = zis.getNextEntry();
			while (entry != null) {
				String name = entry.getName();
				if (name.endsWith(".class")) {
					name = name.substring(0, name.length() - ".class".length())
							.replaceAll("\\/", ".");
				}
				final ByteArrayOutputStream baos = new ByteArrayOutputStream(
						zis.available());
				int read = 0;
				while ((read = zis.read(buffer)) > 0) {
					baos.write(buffer, 0, read);
				}
				classes.put(name, baos.toByteArray());
				entry = zis.getNextEntry();
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public VirtualClassLoader(URL[] urls) {
		super(urls);
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
}
