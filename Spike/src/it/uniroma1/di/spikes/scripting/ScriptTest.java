package it.uniroma1.di.spikes.scripting;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public final class ScriptTest {
	public static class Printer {
		public void print(String message) {
			System.out.println(message);
		}
	}

	public static void main(String[] arguments) throws ScriptException,
			NoSuchMethodException {
		final ScriptEngine engine = new ScriptEngineManager()
				.getEngineByName("JavaScript");
		engine.put("printer", new Printer());
		engine.eval("function hello() { printer.print('hello, world!'); }");
		((Invocable) engine).invokeFunction("hello");
	}
}
