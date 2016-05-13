package testdata;

// This file was generated by colf(1); DO NOT EDIT

import java.nio.ByteBuffer;
import java.util.InputMismatchException;

import static java.lang.String.format;


/**
 * @author Commander Colfer
 * @see <a href="https://github.com/pascaldekloe/colfer">Colfer's home</a>
 */
public class O implements java.io.Serializable {

	/** The upper limit for serial byte sizes. */
	public static int colferSizeMax = 16 * 1024 * 1024;

	/** The upper limit for the number of elements in a list. */
	public static int colferListMax = 64 * 1024;

	private static final java.nio.charset.Charset _utf8 = java.nio.charset.Charset.forName("UTF-8");
	private static final byte[] _zeroA = new byte[0];
	private static final O[] _zeroOs = new O[0];

	public boolean b;
	public int u32;
	public long u64;
	public int i32;
	public long i64;
	public float f32;
	public double f64;
	public java.time.Instant t;
	public String s = "";
	public byte[] a = _zeroA;
	public O o;
	public O[] os = _zeroOs;


	/**
	 * Serializes the object.
	 * All {@code null} entries in {@link #os} will be replaced with a {@code new} value.
	 * The {@link ByteBuffer#limit()} is set when {@link ByteBuffer#remaining()} exceeds {@link #colferSizeMax}.
	 * @param buf the data destination.
	 */
	public final void marshal(ByteBuffer buf)
	throws java.nio.BufferOverflowException, InputMismatchException {
		buf.order(java.nio.ByteOrder.BIG_ENDIAN);
		if (buf.remaining() > colferSizeMax)
			buf.limit(buf.position() + colferSizeMax);

		if (this.b) {
			buf.put((byte) 0);
		}

		if (this.u32 != 0) {
			buf.put((byte) 1);
			putVarint(buf, this.u32);
		}

		if (this.u64 != 0) {
			buf.put((byte) 2);
			putVarint(buf, this.u64);
		}

		if (this.i32 != 0) {
			int x = this.i32;
			if (x < 0) {
				x = -x;
				buf.put((byte) (3 | 0x80));
			} else
				buf.put((byte) 3);
			putVarint(buf, x);
		}

		if (this.i64 != 0) {
			long x = this.i64;
			if (x < 0) {
				x = -x;
				buf.put((byte) (4 | 0x80));
			} else
				buf.put((byte) 4);
			putVarint(buf, x);
		}

		if (this.f32 != 0.0f) {
			buf.put((byte) 5);
			buf.putFloat(this.f32);
		}

		if (this.f64 != 0.0) {
			buf.put((byte) 6);
			buf.putDouble(this.f64);
		}

		if (this.t != null) {
			long s = this.t.getEpochSecond();
			int ns = this.t.getNano();
			if (ns == 0) {
				if (s != 0) {
					buf.put((byte) 7);
					buf.putLong(s);
				}
			} else {
				buf.put((byte) (7 | 0x80));
				buf.putLong(s);
				buf.putInt(ns);
			}
		}

		if (! this.s.isEmpty()) {
			ByteBuffer bytes = this._utf8.encode(this.s);
			buf.put((byte) 8);
			putVarint(buf, bytes.limit());
			buf.put(bytes);
		}

		if (this.a.length != 0) {
			buf.put((byte) 9);
			putVarint(buf, this.a.length);
			buf.put(this.a);
		}

		if (this.o != null) {
			buf.put((byte) 10);
			this.o.marshal(buf);
		}

		if (this.os.length != 0) {
			buf.put((byte) 11);
			O[] a = this.os;
			int length = a.length;
			if (length > colferListMax)
				throw new InputMismatchException(format("colfer: field testdata.o.os exceeds %d elements", colferListMax));
			putVarint(buf, length);
			for (int i = 0; i < length; i++) {
				O o = a[i];
				if (o == null) {
					o = new O();
					a[i] = o;
				}
				o.marshal(buf);
			}
		}

		buf.put((byte) 0x7f);
	}

	/**
	 * Deserializes the object.
	 * The {@link ByteBuffer#limit()} is set when {@link ByteBuffer#remaining()} exceeds {@link #colferSizeMax}.
	 * @param buf the data source.
	 */
	public final void unmarshal(ByteBuffer buf)
	throws java.nio.BufferUnderflowException, InputMismatchException {
		if (buf.remaining() > colferSizeMax)
			buf.limit(buf.position() + colferSizeMax);
		byte header = buf.get();

		if (header == (byte) 0) {
			this.b = true;
			header = buf.get();
		}

		if (header == (byte) 1) {
			this.u32 = getVarint32(buf);
			header = buf.get();
		}

		if (header == (byte) 2) {
			this.u64 = getVarint64(buf);
			header = buf.get();
		}

		if (header == (byte) 3) {
			this.i32 = getVarint32(buf);
			header = buf.get();
		} else if (header == (byte) (3 | 0x80)) {
			this.i32 = (~getVarint32(buf)) + 1;
			header = buf.get();
		}

		if (header == (byte) 4) {
			this.i64 = getVarint64(buf);
			header = buf.get();
		} else if (header == (byte) (4 | 0x80)) {
			this.i64 = (~getVarint64(buf)) + 1;
			header = buf.get();
		}

		if (header == (byte) 5) {
			this.f32 = buf.getFloat();
			header = buf.get();
		}

		if (header == (byte) 6) {
			this.f64 = buf.getDouble();
			header = buf.get();
		}

		if (header == (byte) 7) {
			long s = buf.getLong();
			this.t = java.time.Instant.ofEpochSecond(s);
			header = buf.get();
		} else if (header == (byte) (7 | 0x80)) {
			long s = buf.getLong();
			int ns = buf.getInt();
			this.t = java.time.Instant.ofEpochSecond(s, ns);
			header = buf.get();
		}

		if (header == (byte) 8) {
			ByteBuffer blob = ByteBuffer.allocate(getVarint32(buf));
			buf.get(blob.array());
			this.s = this._utf8.decode(blob).toString();
			header = buf.get();
		}

		if (header == (byte) 9) {
			this.a = new byte[getVarint32(buf)];
			buf.get(this.a);
			header = buf.get();
		}

		if (header == (byte) 10) {
			this.o = new O();
			this.o.unmarshal(buf);
			header = buf.get();
		}

		if (header == (byte) 11) {
			int length = getVarint32(buf);
			if (length > colferListMax)
				throw new InputMismatchException(format("colfer: field testdata.o.os exceeds %d elements", colferListMax));
			O[] a = new O[length];
			for (int i = 0; i < length; i++) {
				O o = new O();
				o.unmarshal(buf);
				a[i] = o;
			}
			this.os = a;
			header = buf.get();
		}

		if (header != 0x7f)
			throw new InputMismatchException(format("colfer: unknown header at byte %d", buf.position() - 1));
	}

	public boolean getB() {
		return this.b;
	}

	public void setB(boolean value) {
		this.b = value;
	}

	public int getU32() {
		return this.u32;
	}

	public void setU32(int value) {
		this.u32 = value;
	}

	public long getU64() {
		return this.u64;
	}

	public void setU64(long value) {
		this.u64 = value;
	}

	public int getI32() {
		return this.i32;
	}

	public void setI32(int value) {
		this.i32 = value;
	}

	public long getI64() {
		return this.i64;
	}

	public void setI64(long value) {
		this.i64 = value;
	}

	public float getF32() {
		return this.f32;
	}

	public void setF32(float value) {
		this.f32 = value;
	}

	public double getF64() {
		return this.f64;
	}

	public void setF64(double value) {
		this.f64 = value;
	}

	public java.time.Instant getT() {
		return this.t;
	}

	public void setT(java.time.Instant value) {
		this.t = value;
	}

	public String getS() {
		return this.s;
	}

	public void setS(String value) {
		this.s = value;
	}

	public byte[] getA() {
		return this.a;
	}

	public void setA(byte[] value) {
		this.a = value;
	}

	public O getO() {
		return this.o;
	}

	public void setO(O value) {
		this.o = value;
	}

	public O[] getOs() {
		return this.os;
	}

	public void setOs(O[] value) {
		this.os = value;
	}

	@Override
	public final int hashCode() {
		return java.util.Objects.hash(0x7f, b, u32, u64, i32, i64, f32, f64, t, s, a, o, os);
	}

	@Override
	public final boolean equals(Object o) {
		return o instanceof O && equals((O) o);
	}

	public final boolean equals(O o) {
		return o != null
			&& this.b == o.b
			&& this.u32 == o.u32
			&& this.u64 == o.u64
			&& this.i32 == o.i32
			&& this.i64 == o.i64
			&& java.util.Objects.equals(this.f32, o.f32)
			&& java.util.Objects.equals(this.f64, o.f64)
			&& java.util.Objects.equals(this.t, o.t)
			&& java.util.Objects.equals(this.s, o.s)
			&& java.util.Arrays.equals(this.a, o.a)
			&& java.util.Objects.equals(this.o, o.o)
			&& java.util.Arrays.equals(this.os, o.os);
	}

	/**
	 * Serializes a 32-bit integer.
	 * @param buf the data destination.
	 * @param x the value.
	 */
	private static void putVarint(ByteBuffer buf, int x) {
		while ((x & 0xffffff80) != 0) {
			buf.put((byte) (x | 0x80));
			x >>>= 7;
		}
		buf.put((byte) x);
	}

	/**
	 * Serializes a 64-bit integer.
	 * @param buf the data destination.
	 * @param x the value.
	 */
	private static void putVarint(ByteBuffer buf, long x) {
		boolean includeLast = x >= 0;
		while ((x & 0xffffffffffffff80L) != 0) {
			buf.put((byte) (x | 0x80));
			x >>>= 7;
		}
		if (includeLast) buf.put((byte) x);
	}

	/**
	 * Deserializes a 32-bit integer.
	 * @param buf the data source.
	 * @return the value.
	 */
	private static int getVarint32(ByteBuffer buf) {
		int x = 0;
		for (int shift = 0; true; shift += 7) {
			int b = buf.get() & 0xff;
			if (shift == 28 || b < 0x80) {
				x |= b << shift;
				return x;
			}
			x |= (b & 0x7f) << shift;
		}
	}

	/**
	 * Deserializes a 64-bit integer.
	 * @param buf the data source.
	 * @return the value.
	 */
	private static long getVarint64(ByteBuffer buf) {
		long x = 0;
		for (int shift = 0; true; shift += 7) {
			long b = buf.get() & 0xffL;
			if (shift == 56 || b < 0x80) {
				x |= b << shift;
				return x;
			}
			x |= (b & 0x7f) << shift;
		}
	}

}
