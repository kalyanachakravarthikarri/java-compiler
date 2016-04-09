package com.neophoenix.compiler;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.neophoenix.compiler.impl.JavaFileCompiler;
import com.neophoenix.compiler.impl.MavenProjectCompiler;

/**
 * Utility class containing various method used for compilation
 * 
 * @author Kalyan
 * 
 */
public class CompilerUtils {

	private static Compiler javaCompiler = new JavaFileCompiler();
	private static Compiler mavenProjectCompiler = new MavenProjectCompiler();

	/**
	 * This method compiles the given file. Java files and zip files could be
	 * provided
	 * 
	 * @param file
	 *            - file to be compiled
	 * @return result of compilation
	 */
	public static CompilerOutput compile(String file) {

		CompilerOutput result = new CompilerOutput();

		if (file.endsWith(".java")) {
			result = javaCompiler.compile(file);
		} else if (file.endsWith(".zip")) {
			String destinationFile = new File("").getAbsolutePath()
					+ "/unzipped";
			System.out.println("destination is at : " + destinationFile);
			List<String> zippedFiles = null;
			try {
				zippedFiles = unZipAll(file, destinationFile);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String pomFile = getPOMFile(zippedFiles);
			if (pomFile != null) {
				result = mavenProjectCompiler.compile(pomFile);
			} else {
				result = javaCompiler.compile(zippedFiles
						.toArray(new String[zippedFiles.size()]));
			}
		} else {
			result.setError(true);
			result.setDescription("Invalid file type");
		}
		return result;
	}

	/**
	 * get the pom file location from given list of files
	 * 
	 * @param zippedFiles
	 *            - list of files
	 * @return - pom.xml location
	 */
	private static String getPOMFile(List<String> zippedFiles) {
		File file = null;
		for (String f : zippedFiles) {
			file = new File(f);
			if ("pom.xml".equalsIgnoreCase(file.getName())) {
				return f;
			}
		}
		return null;
	}

	/**
	 * Write the file to given destination
	 * @param uploadedInputStream
	 * @param uploadedFileLocation
	 */
	public static void writeToFile(InputStream uploadedInputStream,
			String uploadedFileLocation) {
		try {
			OutputStream out = new FileOutputStream(new File(
					uploadedFileLocation));
			int read = 0;
			byte[] bytes = new byte[1024];

			// out = new FileOutputStream(new File(uploadedFileLocation));
			while ((read = uploadedInputStream.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}
			out.flush();
			out.close();
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	/**
	 * Unzip the file to given location
	 * @param source
	 * @param destination
	 * @return list of file names which are unzipped
	 * @throws IOException
	 */
	public static List<String> unZipAll(String source, String destination)
			throws IOException {
		return unZipAll(new File(source), new File(destination));
	}

	/**
	 * Unzip the file to given location
	 * @param source
	 * @param destination
	 * @return list of file names which are unzipped
	 * @throws IOException
	 */
	public static List<String> unZipAll(File source, File destination)
			throws IOException {
		System.out.println("Unzipping - " + source.getName());
		List<String> filesUnzipped = new ArrayList<String>();
		int BUFFER = 2048;

		ZipFile zip = new ZipFile(source);
		try {
			destination.getParentFile().mkdirs();
			Enumeration zipFileEntries = zip.entries();

			// Process each entry
			while (zipFileEntries.hasMoreElements()) {
				// grab a zip file entry
				ZipEntry entry = (ZipEntry) zipFileEntries.nextElement();
				String currentEntry = entry.getName();
				File destFile = new File(destination, currentEntry);
				// destFile = new File(newPath, destFile.getName());
				File destinationParent = destFile.getParentFile();

				// create the parent directory structure if needed
				destinationParent.mkdirs();

				if (!entry.isDirectory()) {
					BufferedInputStream is = null;
					FileOutputStream fos = null;
					BufferedOutputStream dest = null;
					try {
						is = new BufferedInputStream(zip.getInputStream(entry));
						int currentByte;
						// establish buffer for writing file
						byte data[] = new byte[BUFFER];

						// write the current file to disk
						fos = new FileOutputStream(destFile);
						dest = new BufferedOutputStream(fos, BUFFER);

						// read and write until last byte is encountered
						while ((currentByte = is.read(data, 0, BUFFER)) != -1) {
							dest.write(data, 0, currentByte);
						}
					} catch (Exception e) {
						System.out.println("unable to extract entry:"
								+ entry.getName());
						throw e;
					} finally {
						if (dest != null) {
							dest.close();
						}
						if (fos != null) {
							fos.close();
						}
						if (is != null) {
							is.close();
						}
					}
					filesUnzipped.add(destFile.getAbsolutePath());
				} else {
					// Create directory
					destFile.mkdirs();
				}

				if (currentEntry.endsWith(".zip")) {
					// found a zip file, try to extract
					unZipAll(destFile, destinationParent);
					if (!destFile.delete()) {
						System.out.println("Could not delete zip");
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Failed to successfully unzip:"
					+ source.getName());
		} finally {
			zip.close();
		}
		System.out.println("Done Unzipping:" + source.getName());
		return filesUnzipped;
	}
}
