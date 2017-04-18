package net.aircommunity.platform.common.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * File utility extension.
 * 
 * @author Bin.Zhang
 */
public final class MoreFiles {
	private static final int EOF = -1;

	/**
	 * The extension separator character.
	 */
	public static final char EXTENSION_SEPARATOR = '.';

	/**
	 * The Unix separator character.
	 */
	private static final char UNIX_SEPARATOR = '/';

	/**
	 * The Windows separator character.
	 */
	private static final char WINDOWS_SEPARATOR = '\\';

	/**
	 * The default buffer size ({@value}) to use for {@link #copyLarge(InputStream, OutputStream)} and
	 * {@link #copyLarge(Reader, Writer)}
	 */
	private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

	/**
	 * Returns a {@link File} representing the system temporary directory.
	 *
	 * @return the system temporary directory.
	 */
	public static File getTempDir() {
		return new File(System.getProperty("java.io.tmpdir"));
	}

	/**
	 * Returns the path to the user's current directory.
	 *
	 * @return the path to the user's current directory.
	 */
	public static File getUserCurrentDir() {
		return new File(System.getProperty("user.dir"));
	}

	/**
	 * Returns a {@link File} representing the user's home directory.
	 *
	 * @return the user's home directory.
	 */
	public static File getUserHomeDir() {
		return new File(System.getProperty("user.home"));
	}

	/**
	 * Return the real path of the given input path.
	 * 
	 * @param path the input path absolute or relative
	 * @return real path resolved
	 */
	public static Path getRealPath(String path) {
		Path thePath = Paths.get(path);
		if (!thePath.isAbsolute()) {
			thePath = Paths.get(getUserCurrentDir().getAbsolutePath(), path);
			if (!thePath.toFile().exists()) {
				try {
					thePath = Paths.get(MoreFiles.class.getClassLoader().getResource(path).toURI());
				}
				catch (URISyntaxException e) {
					throw new IllegalArgumentException(
							String.format("Invalid path: %s, cause: %s", path, e.getMessage()), e);
				}
			}
		}
		return thePath;
	}

	/**
	 * Read all lines.
	 * 
	 * @param filePath the path to the file
	 * @return the lines from the file as a {@code List}; whether the {@code
	 *          List} is modifiable or not is implementation dependent and therefore not specified
	 * @throws IOException
	 */
	public static List<String> readAllLines(String filePath) throws IOException {
		try (BufferedReader reader = toReader(filePath)) {
			List<String> result = new ArrayList<>();
			for (;;) {
				String line = reader.readLine();
				if (line == null) {
					break;
				}
				result.add(line);
			}
			return result;
		}
	}

	/**
	 * Get the contents as a <code>Reader</code>.
	 * <p>
	 * This method buffers the input internally, so there is no need to use a <code>BufferedReader</code>.
	 * 
	 * @param filePath the <code>Reader</code> to read from
	 * @return the requested reader
	 * @throws Exception I/O error occurs
	 */
	public static BufferedReader toReader(String filePath) throws IOException {
		Path path = Paths.get(filePath);
		if (path != null && path.toFile().exists()) {
			return Files.newBufferedReader(path, StandardCharsets.UTF_8);
		}

		// try class path resource
		try {
			path = Paths.get(ClassLoader.getSystemResource(filePath).toURI());
			if (path != null && path.toFile().exists()) {
				return Files.newBufferedReader(path, StandardCharsets.UTF_8);
			}
		}
		catch (URISyntaxException e) {
			// ignored
		}

		// try class path resource of a jar
		InputStream in = MoreFiles.class.getClassLoader().getResourceAsStream(filePath);
		if (in != null) {
			return new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
		}
		return null;
	}

	/**
	 * Get the contents of a <code>Reader</code> as a String.
	 * <p>
	 * This method buffers the input internally, so there is no need to use a <code>BufferedReader</code>.
	 * 
	 * @param input the <code>Reader</code> to read from
	 * @return the requested String
	 * @throws Exception I/O error occurs
	 */
	public static String toString(String filePath) throws IOException {
		Reader reader = toReader(filePath);
		if (reader != null) {
			return toString(reader);
		}
		return null;
	}

	/**
	 * Get the contents of a <code>Reader</code> as a String.
	 * <p>
	 * This method buffers the input internally, so there is no need to use a <code>BufferedReader</code>.
	 * 
	 * @param input the <code>Reader</code> to read from
	 * @return the requested String
	 * @throws NullPointerException if the input is null
	 * @throws IOException if an I/O error occurs
	 */
	public static String toString(Reader input) throws IOException {
		StringBuilderWriter sw = new StringBuilderWriter();
		copy(input, sw);
		return sw.toString();
	}

	/**
	 * Get the contents of an <code>InputStream</code> as a <code>byte[]</code>.
	 * <p>
	 * This method buffers the input internally, so there is no need to use a <code>BufferedInputStream</code>.
	 * 
	 * @param input the <code>InputStream</code> to read from
	 * @return the requested byte array
	 * @throws NullPointerException if the input is null
	 * @throws IOException if an I/O error occurs
	 */
	public static byte[] toByteArray(InputStream input) throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		copy(input, output);
		return output.toByteArray();
	}

	/**
	 * Get the contents of a <code>Reader</code> as a <code>byte[]</code> using the specified character encoding.
	 * <p>
	 * This method buffers the input internally, so there is no need to use a <code>BufferedReader</code>.
	 * 
	 * @param input the <code>Reader</code> to read from
	 * @param encoding the encoding to use, null means platform default
	 * @return the requested byte array
	 * @throws NullPointerException if the input is null
	 * @throws IOException if an I/O error occurs
	 */
	public static byte[] toByteArray(Reader input, Charset encoding) throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		copy(input, output, encoding);
		return output.toByteArray();
	}

	/**
	 * Copy bytes from an <code>InputStream</code> to an <code>OutputStream</code>.
	 * <p>
	 * This method buffers the input internally, so there is no need to use a <code>BufferedInputStream</code>.
	 * <p>
	 * Large streams (over 2GB) will return a bytes copied value of <code>-1</code> after the copy has completed since
	 * the correct number of bytes cannot be returned as an int. For large streams use the
	 * <code>copyLarge(InputStream, OutputStream)</code> method.
	 * 
	 * @param input the <code>InputStream</code> to read from
	 * @param output the <code>OutputStream</code> to write to
	 * @return the number of bytes copied, or -1 if &gt; Integer.MAX_VALUE
	 * @throws NullPointerException if the input or output is null
	 * @throws IOException if an I/O error occurs
	 */
	public static int copy(InputStream input, OutputStream output) throws IOException {
		long count = copyLarge(input, output);
		if (count > Integer.MAX_VALUE) {
			return -1;
		}
		return (int) count;
	}

	/**
	 * Copy chars from a <code>Reader</code> to bytes on an <code>OutputStream</code> using the specified character
	 * encoding, and calling flush.
	 * <p>
	 * This method buffers the input internally, so there is no need to use a <code>BufferedReader</code>.
	 * </p>
	 * <p>
	 * Due to the implementation of OutputStreamWriter, this method performs a flush.
	 * </p>
	 * <p>
	 * This method uses {@link OutputStreamWriter}.
	 * </p>
	 *
	 * @param input the <code>Reader</code> to read from
	 * @param output the <code>OutputStream</code> to write to
	 * @param encoding the encoding to use, null means platform default
	 * @throws NullPointerException if the input or output is null
	 * @throws IOException if an I/O error occurs
	 */
	public static void copy(Reader input, OutputStream output, Charset encoding) throws IOException {
		OutputStreamWriter out = new OutputStreamWriter(output, toCharset(encoding));
		copy(input, out);
		// XXX Unless anyone is planning on rewriting OutputStreamWriter,
		// we have to flush here.
		out.flush();
	}

	private static Charset toCharset(Charset charset) {
		return charset == null ? Charset.defaultCharset() : charset;
	}

	/**
	 * Copy chars from a <code>Reader</code> to a <code>Writer</code>.
	 * <p>
	 * This method buffers the input internally, so there is no need to use a <code>BufferedReader</code>.
	 * <p>
	 * Large streams (over 2GB) will return a chars copied value of <code>-1</code> after the copy has completed since
	 * the correct number of chars cannot be returned as an int. For large streams use the
	 * <code>copyLarge(Reader, Writer)</code> method.
	 *
	 * @param input the <code>Reader</code> to read from
	 * @param output the <code>Writer</code> to write to
	 * @return the number of characters copied, or -1 if &gt; Integer.MAX_VALUE
	 * @throws NullPointerException if the input or output is null
	 * @throws IOException if an I/O error occurs
	 */
	public static int copy(Reader input, Writer output) throws IOException {
		long count = copyLarge(input, output);
		if (count > Integer.MAX_VALUE) {
			return -1;
		}
		return (int) count;
	}

	/**
	 * Copy bytes from a large (over 2GB) <code>InputStream</code> to an <code>OutputStream</code>.
	 * <p>
	 * This method buffers the input internally, so there is no need to use a <code>BufferedInputStream</code>.
	 * <p>
	 * The buffer size is given by {@link #DEFAULT_BUFFER_SIZE}.
	 * 
	 * @param input the <code>InputStream</code> to read from
	 * @param output the <code>OutputStream</code> to write to
	 * @return the number of bytes copied
	 * @throws NullPointerException if the input or output is null
	 * @throws IOException if an I/O error occurs
	 */
	public static long copyLarge(InputStream input, OutputStream output) throws IOException {
		return copyLarge(input, output, new byte[DEFAULT_BUFFER_SIZE]);
	}

	/**
	 * Copy bytes from a large (over 2GB) <code>InputStream</code> to an <code>OutputStream</code>.
	 * <p>
	 * This method uses the provided buffer, so there is no need to use a <code>BufferedInputStream</code>.
	 * <p>
	 * 
	 * @param input the <code>InputStream</code> to read from
	 * @param output the <code>OutputStream</code> to write to
	 * @param buffer the buffer to use for the copy
	 * @return the number of bytes copied
	 * @throws NullPointerException if the input or output is null
	 * @throws IOException if an I/O error occurs
	 */
	public static long copyLarge(InputStream input, OutputStream output, byte[] buffer) throws IOException {
		long count = 0;
		int n = 0;
		while (EOF != (n = input.read(buffer))) {
			output.write(buffer, 0, n);
			count += n;
		}
		return count;
	}

	/**
	 * Copy chars from a large (over 2GB) <code>Reader</code> to a <code>Writer</code>.
	 * <p>
	 * This method buffers the input internally, so there is no need to use a <code>BufferedReader</code>.
	 * <p>
	 * The buffer size is given by {@link #DEFAULT_BUFFER_SIZE}.
	 *
	 * @param input the <code>Reader</code> to read from
	 * @param output the <code>Writer</code> to write to
	 * @return the number of characters copied
	 * @throws NullPointerException if the input or output is null
	 * @throws IOException if an I/O error occurs
	 */
	public static long copyLarge(Reader input, Writer output) throws IOException {
		return copyLarge(input, output, new char[DEFAULT_BUFFER_SIZE]);
	}

	/**
	 * Copy chars from a large (over 2GB) <code>Reader</code> to a <code>Writer</code>.
	 * <p>
	 * This method uses the provided buffer, so there is no need to use a <code>BufferedReader</code>.
	 * <p>
	 *
	 * @param input the <code>Reader</code> to read from
	 * @param output the <code>Writer</code> to write to
	 * @param buffer the buffer to be used for the copy
	 * @return the number of characters copied
	 * @throws NullPointerException if the input or output is null
	 * @throws IOException if an I/O error occurs
	 */
	public static long copyLarge(Reader input, Writer output, char[] buffer) throws IOException {
		long count = 0;
		int n = 0;
		while (EOF != (n = input.read(buffer))) {
			output.write(buffer, 0, n);
			count += n;
		}
		return count;
	}

	/**
	 * {@link Writer} implementation that outputs to a {@link StringBuilder}.
	 * <p>
	 * <strong>NOTE:</strong> This implementation, as an alternative to <code>java.io.StringWriter</code>, provides an
	 * <i>un-synchronized</i> (i.e. for use in a single thread) implementation for better performance. For safe usage
	 * with multiple {@link Thread}s then <code>java.io.StringWriter</code> should be used.
	 */
	static class StringBuilderWriter extends Writer implements Serializable {
		private static final long serialVersionUID = 1L;
		private final StringBuilder builder;

		/**
		 * Construct a new {@link StringBuilder} instance with default capacity.
		 */
		public StringBuilderWriter() {
			this.builder = new StringBuilder();
		}

		/**
		 * Construct a new {@link StringBuilder} instance with the specified capacity.
		 *
		 * @param capacity The initial capacity of the underlying {@link StringBuilder}
		 */
		public StringBuilderWriter(int capacity) {
			this.builder = new StringBuilder(capacity);
		}

		/**
		 * Construct a new instance with the specified {@link StringBuilder}.
		 *
		 * @param builder The String builder
		 */
		public StringBuilderWriter(StringBuilder builder) {
			this.builder = builder != null ? builder : new StringBuilder();
		}

		/**
		 * Append a single character to this Writer.
		 *
		 * @param value The character to append
		 * @return This writer instance
		 */
		@Override
		public Writer append(char value) {
			builder.append(value);
			return this;
		}

		/**
		 * Append a character sequence to this Writer.
		 *
		 * @param value The character to append
		 * @return This writer instance
		 */
		@Override
		public Writer append(CharSequence value) {
			builder.append(value);
			return this;
		}

		/**
		 * Append a portion of a character sequence to the {@link StringBuilder}.
		 *
		 * @param value The character to append
		 * @param start The index of the first character
		 * @param end The index of the last character + 1
		 * @return This writer instance
		 */
		@Override
		public Writer append(CharSequence value, int start, int end) {
			builder.append(value, start, end);
			return this;
		}

		/**
		 * Closing this writer has no effect.
		 */
		@Override
		public void close() {
		}

		/**
		 * Flushing this writer has no effect.
		 */
		@Override
		public void flush() {
		}

		/**
		 * Write a String to the {@link StringBuilder}.
		 * 
		 * @param value The value to write
		 */
		@Override
		public void write(String value) {
			if (value != null) {
				builder.append(value);
			}
		}

		/**
		 * Write a portion of a character array to the {@link StringBuilder}.
		 *
		 * @param value The value to write
		 * @param offset The index of the first character
		 * @param length The number of characters to write
		 */
		@Override
		public void write(char[] value, int offset, int length) {
			if (value != null) {
				builder.append(value, offset, length);
			}
		}

		/**
		 * Return the underlying builder.
		 *
		 * @return The underlying builder
		 */
		public StringBuilder getBuilder() {
			return builder;
		}

		/**
		 * Returns {@link StringBuilder#toString()}.
		 *
		 * @return The contents of the String builder.
		 */
		@Override
		public String toString() {
			return builder.toString();
		}
	}

	/**
	 * Writes a String to a file creating the file if it does not exist.
	 *
	 * @param file the file to write
	 * @param data the content to write to the file
	 * @param encoding the encoding to use, {@code null} means platform default
	 * @throws IOException in case of an I/O error
	 */
	public static void write(Path path, String content, Charset encoding) throws IOException {
		File dir = path.toFile().getParentFile();
		if (!dir.exists()) {
			dir.mkdirs();
		}
		try (BufferedWriter writer = Files.newBufferedWriter(path, encoding)) {
			writer.write(content);
		}
	}

	/**
	 * Writes a UTF8 String to a file creating the file if it does not exist.
	 *
	 * @param file the file to write
	 * @param data the content to write to the file
	 * @throws IOException in case of an I/O error
	 */
	public static void write(Path path, String content) throws IOException {
		write(path, content, StandardCharsets.UTF_8);
	}

	/**
	 * Delete a directory recursively.
	 * 
	 * @param directory dir to be deleted
	 * @throws IOException
	 */
	public static void deleteRecursive(Path directory) throws IOException {
		if (!Files.isDirectory(directory)) {
			return;
		}
		Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				try {
					Files.delete(file);
				}
				catch (IOException e) {
					// ignored
				}
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
				try {
					Files.delete(dir);
				}
				catch (IOException e) {
					// ignored
				}
				return FileVisitResult.CONTINUE;
			}
		});
	}

	/**
	 * Gets the name minus the path from a full filename.
	 * <p>
	 * This method will handle a file in either Unix or Windows format. The text after the last forward or backslash is
	 * returned.
	 * 
	 * <pre>
	 * a/b/c.txt --> c.txt
	 * a.txt     --> a.txt
	 * a/b/c     --> c
	 * a/b/c/    --> ""
	 * </pre>
	 * <p>
	 * The output will be the same irrespective of the machine that the code is running on.
	 *
	 * @param filename the filename to query, null returns null
	 * @return the name of the file without the path, or an empty string if none exists
	 */
	public static String getName(String filename) {
		if (filename == null) {
			return null;
		}
		int index = indexOfLastSeparator(filename);
		return filename.substring(index + 1);
	}

	/**
	 * Gets the base name, minus the full path and extension, from a full filename.
	 * <p>
	 * This method will handle a file in either Unix or Windows format. The text after the last forward or backslash and
	 * before the last dot is returned.
	 * 
	 * <pre>
	 * a/b/c.txt --> c
	 * a.txt     --> a
	 * a/b/c     --> c
	 * a/b/c/    --> ""
	 * </pre>
	 * <p>
	 * The output will be the same irrespective of the machine that the code is running on.
	 *
	 * @param filename the filename to query, null returns null
	 * @return the name of the file without the path, or an empty string if none exists
	 */
	public static String getBaseName(String filename) {
		return removeExtension(getName(filename));
	}

	/**
	 * Gets the extension of a filename.
	 * <p>
	 * This method returns the textual part of the filename after the last dot. There must be no directory separator
	 * after the dot.
	 * 
	 * <pre>
	 * foo.txt      --> "txt"
	 * a/b/c.jpg    --> "jpg"
	 * a/b.txt/c    --> ""
	 * a/b/c        --> ""
	 * </pre>
	 * <p>
	 * The output will be the same irrespective of the machine that the code is running on.
	 *
	 * @param filename the filename to retrieve the extension of.
	 * @return the extension of the file or an empty string if none exists or {@code null} if the filename is
	 * {@code null}.
	 */
	public static String getExtension(String filename) {
		if (filename == null) {
			return null;
		}
		int index = indexOfExtension(filename);
		if (index == -1) {
			return "";
		}
		return filename.substring(index + 1).toLowerCase(Locale.ENGLISH);
	}

	// -----------------------------------------------------------------------
	/**
	 * Removes the extension from a filename.
	 * <p>
	 * This method returns the textual part of the filename before the last dot. There must be no directory separator
	 * after the dot.
	 * 
	 * <pre>
	 * foo.txt    --> foo
	 * a\b\c.jpg  --> a\b\c
	 * a\b\c      --> a\b\c
	 * a.b\c      --> a.b\c
	 * </pre>
	 * <p>
	 * The output will be the same irrespective of the machine that the code is running on.
	 *
	 * @param filename the filename to query, null returns null
	 * @return the filename minus the extension
	 */
	public static String removeExtension(String filename) {
		if (filename == null) {
			return null;
		}
		int index = indexOfExtension(filename);
		if (index == -1) {
			return filename;
		}
		return filename.substring(0, index);
	}

	/**
	 * Returns the index of the last extension separator character, which is a dot.
	 * <p>
	 * This method also checks that there is no directory separator after the last dot. To do this it uses
	 * {@link #indexOfLastSeparator(String)} which will handle a file in either Unix or Windows format.
	 * <p>
	 * The output will be the same irrespective of the machine that the code is running on.
	 * 
	 * @param filename the filename to find the last path separator in, null returns -1
	 * @return the index of the last separator character, or -1 if there is no such character
	 */
	private static int indexOfExtension(String filename) {
		if (filename == null) {
			return -1;
		}
		int extensionPos = filename.lastIndexOf(EXTENSION_SEPARATOR);
		int lastSeparator = indexOfLastSeparator(filename);
		return lastSeparator > extensionPos ? -1 : extensionPos;
	}

	/**
	 * Returns the index of the last directory separator character.
	 * <p>
	 * This method will handle a file in either Unix or Windows format. The position of the last forward or backslash is
	 * returned.
	 * <p>
	 * The output will be the same irrespective of the machine that the code is running on.
	 * 
	 * @param filename the filename to find the last path separator in, null returns -1
	 * @return the index of the last separator character, or -1 if there is no such character
	 */
	private static int indexOfLastSeparator(String filename) {
		if (filename == null) {
			return -1;
		}
		int lastUnixPos = filename.lastIndexOf(UNIX_SEPARATOR);
		int lastWindowsPos = filename.lastIndexOf(WINDOWS_SEPARATOR);
		return Math.max(lastUnixPos, lastWindowsPos);
	}

	private MoreFiles() {
		throw new AssertionError();
	}

}
