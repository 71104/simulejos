package lejos.nxt;

import it.uniroma1.di.simulejos.NotImplementedException;

import java.util.Iterator;

/**
 * This class provides access to many of the internal structures of the leJOS
 * virtual machine. In particular it provides Java level access to the classes,
 * methods fields etc. that make up the currently executing program. These
 * structures are used by the VM to create the in memory program. They are
 * similar to the class file format used by a standard JVM, but with much of the
 * detail stripped away.
 * 
 * The structures fall into two general types. Those that are contained within
 * Java objects (and so can be made directly available to a user program) and
 * those that are simply raw data. To allow access to the second type of data we
 * create Java objects with a copy of the data in them. In some cases we also do
 * this even for tables/etc. that are available as objects as wrapping the class
 * in this way may make it easier to manipulate.
 * 
 * NOTE: Many of the structures in this file are a direct mapping to internal
 * data within the VM. Changes to the VM structures must also be reflected here.
 * Take care when editing this code to ensure that changes do not modify this
 * mapping. Also many of the tables use byte fields that really should be
 * treated as unsigned values. We have to take great care when converting these
 * to integers.
 * 
 * @author andy
 */
public final class VM {
	// The peek methods use the follow base address when accessing VM memory.
	private static final int ABSOLUTE = 0;
	private static final int THREADS = 1;
	private static final int HEAP = 2;
	private static final int IMAGE = 3;
	private static final int STATICS = 4;
	private static final int MEM = 5;

	// Offsets and masks to allow access to a standard Object header
	private static final int OBJ_HDR_SZ = 4;
	private static final int OBJ_FLAGS = 1;
	private static final int OBJ_LEN_MASK = 0x3f;
	private static final int OBJ_LEN_OBJECT = 0x3f;
	private static final int OBJ_LEN_BIGARRAY = 0x3e;
	private static final int OBJ_CLASS = 0;
	private static final int OBJ_BIGARRAY_LEN = 4;

	// Basic variable types used within the VM
	public static final int VM_OBJECT = 0;
	public static final int VM_CLASS = 2;
	public static final int VM_BOOLEAN = 4;
	public static final int VM_CHAR = 5;
	public static final int VM_FLOAT = 6;
	public static final int VM_DOUBLE = 7;
	public static final int VM_BYTE = 8;
	public static final int VM_SHORT = 9;
	public static final int VM_INT = 10;
	public static final int VM_LONG = 11;
	public static final int VM_VOID = 12;
	public static final int VM_OBJECTARRAY = 13;
	public static final int VM_STRING = 37;
	public static final int VM_CHARARRAY = 18;

	// The base address of the in memory program header
	private static final int IMAGE_BASE = memPeekInt(MEM, IMAGE * 4);
	// Provide access to the image header structure
	private static final int IMAGE_HDR_LEN = 20;
	private static final int LAST_CLASS_OFFSET = 17;

	private static final int LAST_CLASS = memPeekByte(IMAGE, LAST_CLASS_OFFSET);
	private int METHOD_BASE;
	private static final int METHOD_OFFSET = 4;
	private static VM theVM;

	// Singleton don't allow new from other classes
	private VM() {
		// IMAGE_BASE = memPeekInt(MEM, IMAGE*4);
		image = new VMImage(IMAGE_BASE);
		METHOD_BASE = memPeekShort(IMAGE, IMAGE_HDR_LEN + METHOD_OFFSET)
				+ IMAGE_BASE;
	}

	/**
	 * Obtain access to the single instance of the VM class. This can then be
	 * used to gain access to the more detailed information about the VM and
	 * it's internal structures.
	 * 
	 * @return the VM object
	 */
	public static VM getVM() {
		if (theVM == null)
			theVM = new VM();
		return theVM;
	}

	// Low level memory access functions

	/**
	 * Return up to 4 bytes from a specified memory location.
	 * 
	 * @param base
	 *            Base section of memory.
	 * @param offset
	 *            Offset (in bytes) of the location
	 * @param typ
	 *            The primitive data type to access
	 * @return Memory location contents.
	 */
	private static int memPeek(int base, int offset, int typ) {
		throw new NotImplementedException("VM.memPeek");
	}

	/**
	 * Copy the specified number of bytes from memory into the given object.
	 * 
	 * @param obj
	 *            Object to copy to
	 * @param objoffset
	 *            Offset (in bytes) within the object
	 * @param base
	 *            Base section to copy from
	 * @param offset
	 *            Offset within the section
	 * @param len
	 *            Number of bytes to copy
	 */
	private static void memCopy(Object obj, int objoffset, int base,
			int offset, int len) {
		throw new NotImplementedException("VM.memCopy");
	}

	/**
	 * Return the address of the given objects first data field.
	 * 
	 * @param obj
	 * @return the required address
	 */
	private static int getDataAddress(Object obj) {
		throw new NotImplementedException("VM.getDataAddress");
	}

	/**
	 * Return the address of the given object.
	 * 
	 * @param obj
	 * @return the required address
	 */
	private static int getObjectAddress(Object obj) {
		throw new NotImplementedException("VM.getObjectAddress");
	}

	/**
	 * Return a Java object reference the points to the location provided.
	 * 
	 * @param base
	 *            Memory section that offset refers to.
	 * @param offset
	 *            The offset from the base in bytes.
	 * @return
	 */
	private static Object memGetReference(int base, int offset) {
		throw new NotImplementedException("VM.memGetReference");
	}

	/**
	 * Return a single byte from the specified memory location.
	 * 
	 * @param base
	 * @param offset
	 * @return byte value from memory
	 */
	private static int memPeekByte(int base, int offset) {
		return memPeek(base, offset, VM_BYTE) & 0xff;
	}

	/**
	 * Return a 16 bit word from the specified memory location.
	 * 
	 * @param base
	 * @param offset
	 * @return short value from memory
	 */
	private static int memPeekShort(int base, int offset) {
		return memPeek(base, offset, VM_SHORT) & 0xffff;
	}

	/**
	 * Return a 32 bit word from the specified memory location.
	 * 
	 * @param base
	 * @param offset
	 * @return int value from memory
	 */
	private static int memPeekInt(int base, int offset) {
		return memPeek(base, offset, VM_INT);
	}

	// The flash structure for class data has a special header added to it to
	// allow it to be treated as a Java object. This is not the normal 4 byte
	// object header (because the second two bytes are used for locking which
	// would not work for a flash based object), it is a two byte header. We
	// need to allow for this header when moving around.
	private static final int CLASS_OBJ_HDR = 2;

	// The tables and the class objects are aligned on 4 byte boundaries. Other
	// items are aligned on 2 or 1 byte boundaries.
	private static final int TABLE_ALIGNMENT = 4;
	private static final int CLASS_ALIGNMENT = 4;
	private static final int METHOD_ALIGNMENT = 2;
	private static final int EXCEPTION_ALIGNMENT = 2;
	private static final int FIELD_ALIGNMENT = 1;
	private static final int STATIC_FIELD_ALIGNMENT = 2;
	private static final int CONSTANT_ALIGNMENT = 2;

	/**
	 * This class is used to create a Java class from in memory data. This data
	 * may not have a Java object header or may be of a different class to that
	 * desired. The new object will contain a snapshot of the in memory data.
	 * This snapshot can be updated (if required by calling the update() method.
	 */
	public static class VMClone {

		// Offset of the first cloned data field within the Java object. This
		// is after any red tape fields like the length and address. We also
		// need
		// to allow for the extra "this" value.
		private static final int CLONE_OFFSET = 8;
		private final int length;
		final int address;

		public void update() {
			memCopy(this, CLONE_OFFSET, ABSOLUTE, address, length);
		}

		private VMClone(int addr, int len) {
			address = addr;
			length = len;
			update();
		}
	}

	/**
	 * Class that represents a value within the VM. The type field indicates the
	 * basic type of the value. The value object is used to return the actual
	 * contents. For primitive types we return a boxed type, for objects we
	 * simply return the object. value.
	 */
	public static class VMValue {
		public final int type;
		public final Object value;

		// Number of bytes for a basic type
		private static final int[] lengths = { 4, 0, 1, 0, 1, 2, 4, 8, 1, 2, 4,
				8 };

		/**
		 * Create the value object based upon an address.
		 * 
		 * @param typ
		 *            The basic type of the value
		 * @param addr
		 *            The absolute address of the value
		 */
		private VMValue(int typ, int addr) {
			type = typ;
			switch (typ) {
			case VM_OBJECT:
				value = memGetReference(ABSOLUTE, memPeekInt(ABSOLUTE, addr));
				break;
			case VM_INT:
				value = new Integer(memPeekInt(ABSOLUTE, addr));
				break;
			case VM_BYTE:
				value = new Byte((byte) memPeekByte(ABSOLUTE, addr));
				break;
			case VM_CHAR:
				value = new Character((char) memPeekShort(ABSOLUTE, addr));
				break;
			case VM_SHORT:
				value = new Short((short) memPeekShort(ABSOLUTE, addr));
				break;
			case VM_LONG:
				value = new Long(((long) memPeekInt(ABSOLUTE, addr) << 32)
						| (long) memPeekInt(ABSOLUTE, addr + 4));
				break;
			case VM_FLOAT:
				value = new Float(Float.intBitsToFloat(memPeekInt(ABSOLUTE,
						addr)));
				break;
			case VM_DOUBLE:
				value = new Double(Double.longBitsToDouble(((long) memPeekInt(
						ABSOLUTE, addr) << 32)
						| (long) memPeekInt(ABSOLUTE, addr + 4)));
				break;
			case VM_BOOLEAN:
				value = new Boolean(memPeekByte(ABSOLUTE, addr) != 0);
				break;
			case VM_CLASS:
				value = VM.getVM().getVMClass(memPeekByte(ABSOLUTE, addr));
				break;
			default:
				throw new NoSuchFieldError();
			}
		}

		/**
		 * Create a value object based upon a Java object.
		 * 
		 * @param obj
		 *            The actual object value.
		 */
		private VMValue(Object obj) {
			type = VM_OBJECT;
			this.value = obj;
		}
	}

	/**
	 * The image header for the currently active program.
	 */
	public final class VMImage extends VMClone {
		public short magicNumber;
		public short constantTableOffset;
		public short constantValuesOffset;
		public short numConstants;
		public short staticFieldsOffset;
		public short staticStateLength;
		public short numStaticFields;
		public short entryClassesOffset;
		public byte numEntryClasses;
		public byte lastClass;
		public short runtimeOptions;

		private VMImage(int addr) {
			super(addr, IMAGE_HDR_LEN);
		}

		/**
		 * Return an object that can be used to access all of the available
		 * class structures.
		 * 
		 * @return Class access object
		 */
		public VMClasses getVMClasses() {
			return new VMClasses(lastClass + 1);
		}

		/**
		 * Return an object that can be used to access all of the available
		 * constant values.
		 * 
		 * @return Constant access object
		 */
		public VMConstants getVMConstants() {
			return new VMConstants(constantTableOffset + IMAGE_BASE,
					numConstants);
		}

		/**
		 * Return an object that can be used to access all of the static fields.
		 * 
		 * @return Field access object
		 */
		public VMStaticFields getVMStaticFields() {
			return new VMStaticFields(staticFieldsOffset + IMAGE_BASE,
					numStaticFields);
		}

		/**
		 * Get the base address for the current image, useful when converting
		 * real address to relative ones.
		 * 
		 * @return the base address for the current image.
		 */
		public int getImageBase() {
			return IMAGE_BASE;
		}
	}

	// Cached version of the image header.
	private final VMImage image;

	/**
	 * Return the image header for the currently running program
	 * 
	 * @return Image header.
	 */
	public VMImage getImage() {
		return image;
	}

	/**
	 * This class provides the ability to iterate through a series of in memory
	 * structures and returns a Java accessible clone of the structure.
	 * 
	 * @param <E>
	 */
	public static abstract class VMItems<E> implements Iterable<E> {
		int cnt;

		private class VMItemsIterator implements Iterator<E> {
			int next = 0;

			public boolean hasNext() {
				return (next < cnt);
			}

			public E next() {
				return get(next++);
			}

			public void remove() {
				throw new UnsupportedOperationException("Not supported yet.");
			}

		}

		public Iterator<E> iterator() {
			return new VMItemsIterator();
		}

		abstract public E get(int entry);

		private VMItems(int cnt) {
			this.cnt = cnt;
		}

	}

	// Provide access to the static field data.
	private static final int FIELD_LEN = 2;

	/**
	 * This class can be used to gain access to all of the static fields.
	 */
	public static final class VMStaticFields extends VMItems<VMValue> {
		private final int baseAddr;
		private final int dataAddr;

		/**
		 * Return a VMValue object for the specified static field number
		 * 
		 * @param item
		 * @return VMValue for this item
		 */
		@Override
		public VMValue get(int item) {
			if (item >= cnt)
				throw new NoSuchFieldError();
			int addr = baseAddr
					+ item
					* ((FIELD_LEN + STATIC_FIELD_ALIGNMENT - 1) & ~(STATIC_FIELD_ALIGNMENT - 1));
			int rec = memPeekShort(ABSOLUTE, addr);
			int typ = (rec >> 12) & 0xf;
			int offset = rec & 0xfff;
			return new VMValue(typ, dataAddr + offset);
		}

		private VMStaticFields(int base, int cnt) {
			super(cnt);
			baseAddr = base;
			dataAddr = memPeekInt(MEM, STATICS * 4);
		}

	}

	// Provide access to constant values
	private static final int CONSTANT_LEN = 4;

	/**
	 * This class allows access to all of the constant values.
	 */
	public final class VMConstants extends VMItems<VMValue> {
		private final int baseAddr;

		/**
		 * Return a VMValue object for the specified constant table entry.
		 * 
		 * @param item
		 * @return VMValue object for the constant.
		 */
		@Override
		public VMValue get(int item) {
			if (item >= cnt)
				throw new NoSuchFieldError();
			int addr = baseAddr
					+ item
					* ((CONSTANT_LEN + CONSTANT_ALIGNMENT - 1) & ~(CONSTANT_ALIGNMENT - 1));
			int offset = memPeekShort(ABSOLUTE, addr) + IMAGE_BASE;
			int typ = memPeekByte(ABSOLUTE, addr + 2);
			if (typ == VM_STRING) {
				// Must be an optimized string constant
				int len = memPeekByte(ABSOLUTE, addr + 3);
				char chars[] = new char[len];
				for (int i = 0; i < len; i++)
					chars[i] = (char) memPeekByte(ABSOLUTE, offset + i);
				return new VMValue(new String(chars));
			} else if (typ == VM_CHARARRAY) {
				// Non optimized string constant
				return new VMValue(new String((char[]) memGetReference(
						ABSOLUTE, offset)));
			}
			return new VMValue(typ, offset);
		}

		private VMConstants(int base, int cnt) {
			super(cnt);
			baseAddr = base;
		}
	}

	// Provide access to internal exception data
	private static final int EXCEPTION_LEN = 7;

	/**
	 * An exception record
	 */
	public static final class VMException extends VMClone {
		public short start;
		public short end;
		public short handler;
		public byte classIndex;

		private VMException(int addr) {
			super(addr, EXCEPTION_LEN);
		}
	}

	/**
	 * Class to provide access to a series of exception records
	 */
	public static final class VMExceptions extends VMItems<VMException> {
		private final int baseAddr;

		private VMExceptions(int baseAddr, int cnt) {
			super(cnt);
			this.baseAddr = baseAddr;
		}

		@Override
		public VMException get(int item) {
			return new VMException(
					baseAddr
							+ item
							* ((EXCEPTION_LEN + EXCEPTION_ALIGNMENT - 1) & ~(EXCEPTION_ALIGNMENT - 1)));
		}
	}

	// Provide access to internal method data
	private static final int METHOD_LEN = 11;

	/**
	 * Provide access to information about a method
	 */
	public final class VMMethod extends VMClone {
		public short signature;
		public short exceptionTable;
		public short codeOffset;
		public byte numLocals;
		public byte maxOperands;
		public byte numParameters;
		public byte numExceptionHandlers;
		public byte mflags;

		// Flag values
		public static final byte M_NATIVE = 1;
		public static final byte M_SYNCHRONIZED = 2;
		public static final byte M_STATIC = 4;

		private VMMethod(int addr) {
			super(addr, METHOD_LEN);
		}

		/**
		 * Return access to the exception records for this method.
		 * 
		 * @return the VMExceptions object
		 */
		public VMExceptions getVMExceptions() {
			return new VMExceptions(exceptionTable + IMAGE_BASE,
					numExceptionHandlers);
		}

		public int getMethodNumber() {
			return (address - METHOD_BASE)
					/ ((METHOD_LEN + METHOD_ALIGNMENT - 1) & ~(METHOD_ALIGNMENT - 1));
		}
	}

	/**
	 * Provide access to a series of method records
	 */
	public final class VMMethods extends VMItems<VMMethod> {
		private final int baseAddr;

		private VMMethods(int baseAddr, int cnt) {
			super(cnt);
			this.baseAddr = baseAddr;
		}

		/**
		 * Return access to a specific method.
		 * 
		 * @param item
		 * @return the VMMethod object
		 */
		@Override
		public VMMethod get(int item) {
			return new VMMethod(
					baseAddr
							+ item
							* ((METHOD_LEN + METHOD_ALIGNMENT - 1) & ~(METHOD_ALIGNMENT - 1)));
		}

	}

	// Provide access to the internal class data
	// This is the size of data within the structure it does not include
	// the special object header. We need to add the size of the special header
	// when moving from item to item.
	private static final int CLASS_LEN = 10;

	/**
	 * Provide access to the internal class data
	 */
	public final class VMClass extends VMClone {
		public short size;
		public short CIAData1;
		public short CIAData2;
		public byte CIACnt1;
		public byte CIACnt2;
		public byte parentClass;
		public byte flags;

		// Class flags
		public static final byte C_ARRAY = 2;
		public static final byte C_HASCLINIT = 4;
		public static final byte C_INTERFACE = 8;
		public static final byte C_NOREFS = 0x10;
		public static final byte C_PRIMITIVE = 0x20;

		// The following are not part of the internal structure
		private int clsNo;

		private VMClass(int addr) {
			super(addr + CLASS_OBJ_HDR, CLASS_LEN);
			clsNo = (addr - IMAGE_BASE - IMAGE_HDR_LEN)
					/ ((CLASS_LEN + CLASS_OBJ_HDR + CLASS_ALIGNMENT - 1) & ~(CLASS_ALIGNMENT - 1));
		}

		/**
		 * Return access to the methods for this class
		 * 
		 * @return the VMMethods object
		 */
		public VMMethods getMethods() {
			// Interfaces and arrays do not have method tables.
			if ((flags & (C_ARRAY | C_INTERFACE)) != 0) {
				if ((flags & (C_HASCLINIT)) != 0) {
					// But some interfaces have a <clinit> method
					return new VMMethods(CIAData1 + IMAGE_BASE, 1);
				}
				return new VMMethods(0, 0);
			} else
				return new VMMethods(CIAData1 + IMAGE_BASE,
						((int) CIACnt1 & 0xff));
		}

		/**
		 * Return the class number of this class
		 * 
		 * @return the class number
		 */
		public int getClassNo() {
			return clsNo;
		}

		/**
		 * Return a Java Class object for this class.
		 * 
		 * @return Java Class object
		 */
		public Class<?> getJavaClass() {
			// return classFactory.makeRef(getClassAddress(clsNo));
			return (Class<?>) memGetReference(ABSOLUTE, getClassAddress(clsNo));
		}
	}

	/**
	 * Provide access to a series of class records
	 */
	public final class VMClasses extends VMItems<VMClass> {
		private VMClasses(int cnt) {
			super(cnt);
		}

		/**
		 * return a specific class object
		 * 
		 * @param item
		 * @return the VMClass object
		 */
		@Override
		public VMClass get(int item) {
			if (item >= cnt)
				throw new NoClassDefFoundError();
			return new VMClass(getClassAddress(item));
		}

	}

	public final class VMFields extends VMItems<VMValue> {
		private final int fieldOffsets[];
		private final byte fieldTypes[];
		// Keep a reference to make sure the object does not get gc'ed
		private final Object obj;

		private VMFields(Object obj) {
			super(0);
			VMClass cls = getVMClass(obj);
			if (isArray(obj)) {
				fieldOffsets = new int[0];
				fieldTypes = new byte[0];
			} else {
				// first work out how many fields there are (including those
				// from super classes.
				cnt = ((int) cls.CIACnt2 & 0xff);
				while (cls.parentClass != 0) {
					cls = getVMClass(cls.parentClass);
					cnt += ((int) cls.CIACnt2 & 0xff);
				}
				// Now create a type and offset map. Note that we only have type
				// data so we have to start at the object end and work back
				// from there.
				fieldOffsets = new int[cnt];
				fieldTypes = new byte[cnt];
				cls = getVMClass(obj);
				int fieldBase = getObjectAddress(obj);
				int offset = fieldBase + cls.size;
				int item = cnt - 1;
				for (;;) {
					int fieldTable = cls.CIAData2 + IMAGE_BASE;
					for (int i = ((int) cls.CIACnt2 & 0xff) - 1; i >= 0; i--) {
						fieldTypes[item] = (byte) memPeekByte(ABSOLUTE,
								fieldTable + i);
						offset -= VMValue.lengths[fieldTypes[item]];
						fieldOffsets[item--] = offset;
					}
					if (cls.parentClass == 0)
						break;
					cls = getVMClass(cls.parentClass);
				}
			}
			this.obj = obj;
		}

		/**
		 * Return a specified field
		 * 
		 * @param item
		 *            The required field number
		 * @return A value object to access the field.
		 */
		@Override
		public VMValue get(int item) {
			if (item >= cnt)
				throw new NoSuchFieldError();
			return new VMValue(fieldTypes[item], fieldOffsets[item]);
		}
	}

	public static final class VMElements extends VMItems<VMValue> {
		private final int typ;
		private final Object obj;

		VMElements(Object obj) {
			super(0);
			if (obj == null)
				throw new NullPointerException();
			int addr = getObjectAddress(obj);
			int len = memPeekByte(ABSOLUTE, addr + OBJ_FLAGS) & OBJ_LEN_MASK;
			if (len == OBJ_LEN_OBJECT) {
				typ = -1;
			} else {
				int cls = memPeekByte(ABSOLUTE, addr + OBJ_CLASS);
				if (cnt >= OBJ_LEN_BIGARRAY)
					cnt = memPeekByte(ABSOLUTE, addr + OBJ_BIGARRAY_LEN);
				else
					cnt = len;
				// Convert class into basic type, anything other than a
				// primitive
				// is an object.
				if (cls >= (VM_OBJECTARRAY + VM_BOOLEAN)
						&& cls <= (VM_OBJECTARRAY + VM_LONG))
					typ = cls - VM_OBJECTARRAY;
				else
					typ = VM_OBJECT;
			}
			this.obj = obj;
		}

		@Override
		public VMValue get(int item) {
			if (item >= cnt)
				throw new ArrayIndexOutOfBoundsException();
			int offset = getDataAddress(obj) + item * VMValue.lengths[typ];
			return new VMValue(typ, offset);
		}

		public int length() {
			return cnt;
		}
	}

	// NOTE: Some of the following methods should really be in VMClasses, but
	// it just makes things a little cleaner to access them if they are here!

	/**
	 * Return the address of the specified class number
	 * 
	 * @param clsNo
	 * @return the address of the object
	 */
	private static int getClassAddress(int clsNo) {
		return IMAGE_BASE
				+ IMAGE_HDR_LEN
				+ (clsNo & 0xff)
				* ((CLASS_LEN + CLASS_OBJ_HDR + CLASS_ALIGNMENT - 1) & ~(CLASS_ALIGNMENT - 1));
	}

	/**
	 * Return the class number of the class for the specified object
	 * 
	 * @param obj
	 *            Object to obtain the class number for.
	 * @return The requested class number
	 */
	private int getClassNo(Object obj) {
		if (obj == null)
			throw new NullPointerException();
		// First we get the address of the object
		int addr = getObjectAddress(obj);
		int cls = memPeekByte(ABSOLUTE, addr + OBJ_CLASS);
		return cls;
	}

	public static int getClassNumber(Class<?> cls) {
		int addr = getObjectAddress(cls);
		return (addr - IMAGE_BASE - IMAGE_HDR_LEN)
				/ ((CLASS_LEN + CLASS_OBJ_HDR + +CLASS_ALIGNMENT - 1) & ~(CLASS_ALIGNMENT - 1));
	}

	/**
	 * Check to see if it is allowed to assign an object of class src to an
	 * object of class dst.
	 * 
	 * @param src
	 *            The src class number
	 * @param dst
	 *            The destination class number
	 * @return true if the assignment is allowed.
	 */
	private static native boolean isAssignable(int src, int dst);

	/**
	 * Check to see if it is allowed to assign an object of class src to an
	 * oobject of class dst.
	 * 
	 * @param src
	 *            The src class.
	 * @param dst
	 *            The destination class.
	 * @return true if the assignment is allowed.
	 * @exception NullPointerException
	 *                if either class is null.
	 */
	public static boolean isAssignable(Class<?> src, Class<?> dst) {
		if (src == null || dst == null)
			throw new NullPointerException();
		int srcNo = getClassNumber(src);
		int dstNo = getClassNumber(dst);
		return isAssignable(srcNo, dstNo);
	}

	/**
	 * Return the Class object for the provided object. Note: The actual object
	 * returned actually resides in flash rom and is part of the leJOS loader.
	 * It is not possible to extend this class or modify the contents.
	 * 
	 * @param obj
	 *            the object for which the class is required
	 * @return the Class object
	 */
	public Class<?> getClass(Object obj) {
		// return classFactory.makeRef(getClassAddress(getClassNo(value)));
		return (Class<?>) memGetReference(ABSOLUTE,
				getClassAddress(getClassNo(obj)));
	}

	/**
	 * Return a VMClass object for the provided object. Note: The object
	 * returned is actually a copy of the in flash object.
	 * 
	 * @param obj
	 *            the object for which the class is required
	 * @return the VMClass object
	 */
	public VMClass getVMClass(Object obj) {
		return new VMClass(getClassAddress(getClassNo(obj)));
	}

	/**
	 * Return the class for the specified primitive type.
	 * 
	 * @param clsNo
	 * @return Class object for this type.
	 */
	public static Class<?> getClass(int clsNo) {
		if (clsNo > LAST_CLASS)
			return null;
		return (Class<?>) memGetReference(ABSOLUTE, getClassAddress(clsNo));
	}

	/**
	 * Return a VMClass object for the provided class object. Note: The object
	 * returned is actually a copy of the in flash object.
	 * 
	 * @param cls
	 * @return the VMClass object
	 */
	public final VMClass getVMClass(Class<?> cls) {
		return new VMClass(getObjectAddress(cls));
	}

	/**
	 * Return a VMClass object for the provided class number. Note: The object
	 * returned is actually a copy of the in flash object.
	 * 
	 * @param clsNo
	 * @return the VMClass object
	 */
	public final VMClass getVMClass(int clsNo) {
		if (clsNo > LAST_CLASS)
			return null;
		return new VMClass(getClassAddress(clsNo));
	}

	/**
	 * Return data about the specified method number
	 * 
	 * @param methodNo
	 * @return Method object
	 */
	public final VMMethod getMethod(int methodNo) {
		return new VMMethod(METHOD_BASE + methodNo * ((METHOD_LEN + 1) & ~1));
	}

	/**
	 * Return true if the specified object is an array
	 * 
	 * @param obj
	 *            object to test
	 * @return true iff the specified object is an array
	 */
	public final boolean isArray(Object obj) {
		if (obj == null)
			return false;
		return (memPeekByte(ABSOLUTE, getObjectAddress(obj) + OBJ_FLAGS) & OBJ_LEN_MASK) != OBJ_LEN_OBJECT;
	}

	/**
	 * Provide access to the fields of an object
	 * 
	 * @param obj
	 *            the object to obtain the fields for
	 * @return fields object
	 */
	public final VMFields getFields(Object obj) {
		return new VMFields(obj);
	}

	public final VMElements getElements(Object obj) {
		return new VMElements(obj);
	}

	private static final int STACKFRAME_LEN = 20;

	public final class VMStackFrame extends VMClone {
		public int methodRecord;
		public Object monitor;
		public int localsBase;
		public int stackTop;
		public int pc;

		private VMStackFrame(int addr) {
			super(addr, STACKFRAME_LEN);
		}

		public VMMethod getVMMethod() {
			return new VMMethod(methodRecord);
		}
	}

	public final class VMStackFrames extends VMItems<VMStackFrame> {
		private final int base;
		private final int size;

		private VMStackFrames(Object stackFrame, int size) {
			super(size);
			base = getDataAddress(stackFrame);
			this.size = size;
		}

		@Override
		public VMStackFrame get(int item) {
			int offset = base + (size - item) * STACKFRAME_LEN;
			return new VMStackFrame(offset);
		}
	}

	// Provide access to the internal Thread data.
	private static final int THREAD_LEN = 31;

	/**
	 * Internal version of a thread structure
	 */
	public final class VMThread extends VMClone {
		public Thread nextThread;
		public Object waitingOn;
		public int sync;
		public int sleepUntil;
		public Object stackFrameArray;
		public Object stackArray;
		public byte stackFrameIndex;
		public byte monitorCount;
		public byte threadId;
		public byte state;
		public byte priority;
		public byte interrupted;
		public byte daemon;
		// The following is not part of the VM internal structure
		private final Thread thread;

		private VMThread(int addr) {
			super(addr, THREAD_LEN);
			thread = (Thread) memGetReference(ABSOLUTE, addr - OBJ_HDR_SZ);
		}

		/**
		 * Return a Java Thread object for this thread.
		 * 
		 * @return Java Thread object
		 */
		public Thread getJavaThread() {
			return thread;
		}

		public VMStackFrames getStackFrames() {
			return new VMStackFrames(stackFrameArray, stackFrameIndex);
		}

		public VMStackFrames getStackFrames(int frameCnt) {
			return new VMStackFrames(stackFrameArray, frameCnt);

		}
	}

	/**
	 * Provide access to a series of internal thread records
	 */
	public final class VMThreads implements Iterable<VMThread> {
		private class VMThreadIterator implements Iterator<VMThread> {
			private int nextPriority = Thread.MAX_PRIORITY - 1;
			private int first = 0;
			private int nextThread = 0;

			private void findNext() {
				if (nextThread != 0)
					nextThread = memPeekInt(ABSOLUTE, nextThread + OBJ_HDR_SZ);
				if (nextThread == first) {
					first = 0;
					while (nextPriority >= 0 && first == 0) {
						first = memPeekInt(THREADS, nextPriority * 4);
						nextPriority--;
					}
					nextThread = first;
				}
			}

			public boolean hasNext() {
				return nextThread != 0;
			}

			public VMThread next() {
				VMThread ret = new VMThread(nextThread + OBJ_HDR_SZ);
				findNext();
				return ret;
			}

			public void remove() {
				throw new UnsupportedOperationException("Not supported.");
			}

			VMThreadIterator() {
				findNext();
			}
		}

		public Iterator<VMThread> iterator() {
			return new VMThreadIterator();
		}

		private VMThreads() {
			// Nothing
		}
	}

	/**
	 * Returns access to all of the current internal thread objects
	 * 
	 * @return the VMThreads object
	 */
	public final VMThreads getVMThreads() {
		return new VMThreads();
	}

	public final VMThread getVMThread(Thread thread) {
		return new VMThread(getDataAddress(thread));
	}

	private static final int STACKTRACE_OFFSET = OBJ_HDR_SZ;

	/**
	 * Return the stack trace data associated with a throwable.
	 * 
	 * @param t
	 *            The throwable
	 * @return The array of stack trace data.
	 */
	public static final int[] getThrowableStackTrace(Throwable t) {
		return (int[]) memGetReference(ABSOLUTE,
				memPeekInt(ABSOLUTE, getObjectAddress(t) + STACKTRACE_OFFSET));
	}

	/**
	 * Suspend a thread. This places the specified thread into a suspended
	 * state. If thread is null all threads except for the current thread will
	 * be suspended.
	 * 
	 * @param thread
	 */
	public native static final void suspendThread(Object thread);

	/**
	 * Resume a thread. A suspended thread will be resumed to it's previous
	 * state. If thread is null all suspended threads will be resumed.
	 * 
	 * @param thread
	 */
	public native static final void resumeThread(Object thread);

	/**
	 * leJOS allows several "programs" to be linked into a single nxj file the
	 * system by default will start execution of program 0. This function allows
	 * other programs to be called.
	 * 
	 * @param progNo
	 *            program number to call
	 */
	public native static final void executeProgram(int progNo);

	// Flags used to control the Virtual Machine.
	public static final int VM_TYPECHECKS = 1;
	public static final int VM_ASSERT = 2;

	/**
	 * Control the run time operation of the leJOS Virtual Machine.
	 * 
	 * @param options
	 *            Bit flags.
	 */
	public static final native void setVMOptions(int options);

	/**
	 * Return the currently operating Virtual Machine options.
	 * 
	 * @return the options
	 */
	public static final native int getVMOptions();

	/**
	 * Enable/Disable strict run time type checking for some operations within
	 * the Virtual Machine.
	 * 
	 * @param on
	 */
	public static void enableRunTimeTypeChecks(boolean on) {
		int cur = getVMOptions();
		if (on)
			cur |= VM_TYPECHECKS;
		else
			cur &= ~VM_TYPECHECKS;
		setVMOptions(cur);
	}

	/**
	 * Native method to create the stack trace in a compact internal form. This
	 * is currently an array of integers with one int per stack frame. The high
	 * 16 bits contains the method number, the low 16 bits contains the PC
	 * within the method.
	 * 
	 * @param thread
	 *            The thread to create the stack for.
	 * @param ignore
	 *            Ignore stack frames that have a this which matches ignore.
	 * @return An array of stack frame details.
	 */
	public static native int[] createStackTrace(Thread thread, Object ignore);

	/**
	 * Native method to call the firmware exception handler. This will display
	 * the exception details and exit the program.
	 * 
	 * @param exception
	 *            exception class with details of the current exception
	 * @param method
	 *            Method number of the method in which the exception occurred
	 * @param pc
	 *            PC at which the exception occurred
	 */
	public static native void firmwareExceptionHandler(Throwable exception,
			int method, int pc);
}
