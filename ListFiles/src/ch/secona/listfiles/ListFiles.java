package ch.secona.listfiles;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.filechooser.FileSystemView;

/**
 * Lists each file (and files in its sub directories) on a single line
 */
public class ListFiles {
	private static final String DASH_POINT = ";";
	private static final char DOUBLE_POINT = ':';
	private final String toList;
	private final String volume;

	/**
	 * @param args the command line arguments
	 */
	public ListFiles(final String[] args) {
		if (args.length != 1) {
			title();
			throw new IllegalArgumentException("One argument expected.");
		}
		toList = args[0];
		title();
		volume = getVolumeLabel();
	}

	/**
	 * @return the volume label for the volume used in 'toList'
	 */
	private String getVolumeLabel() {
		final List<File> files = Arrays.asList(File.listRoots());
		final int pos = toList.indexOf(DOUBLE_POINT);
		String drive = null;

		if (pos > 0) {
			drive = toList.substring(0, pos + 1).toUpperCase();
		}
		for (final File file : files) {
			final String systemDisplayName = FileSystemView.getFileSystemView().getSystemDisplayName(file);

			if (drive != null && systemDisplayName.contains(drive)) {
				return systemDisplayName;
			}
		}
		return "";
	}

	/**
	 * Does the magic ...!
	 */
	public void execute() {
		final File toListDirectory = new File(toList);
		final List<File> toListFiles = new ArrayList<>();

		readFiles(toListDirectory, toListFiles);
		line();
		System.out.println("Files to list: " + toListFiles.size());
		line();
		System.out.println("");
		toListFiles.stream().forEach(file -> {
			final String absolutePath = file.getAbsolutePath();
			final String name = file.getName();
			final long length = file.length();
			final int l = absolutePath.length() - name.length() - 1;
			final String directory = absolutePath.substring(0, l);
			final String lastModified = getLastModified(absolutePath);

			System.out.println(name + DASH_POINT + directory + DASH_POINT + //
			length + DASH_POINT + lastModified + DASH_POINT + volume);
		});
		System.out.println("");
		line();
		System.out.println("Done.");
		line();
	}

	private String getLastModified(final String absolutePath) {
		final Path path = Paths.get(absolutePath);
		String lastModified = "";

		try {
			final BasicFileAttributes attr = Files.readAttributes(path, BasicFileAttributes.class);

			lastModified = "" + attr.lastModifiedTime();

			final int indexOfPoint = lastModified.indexOf('.');

			if (indexOfPoint > 0) {
				lastModified = lastModified.substring(0, indexOfPoint);
			}
			lastModified = lastModified.replace('T', ' ');
			lastModified = lastModified.replace("Z", "");
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return lastModified;
	}

	private static void line() {
		System.out.println("-------------------------------------------");
	}

	/**
	 * reads all files in a given directory and its sub directories and adds the
	 * file names to the passed list.<br/>
	 * hidden files and directories will be ignored.
	 *
	 * @param folder   folder to read
	 * @param fileList list where the read file names are added
	 */
	private void readFiles(final File folder, final List<File> fileList) {
		for (final File fileEntry : folder.listFiles()) {
			if (!fileEntry.isHidden()) {
				if (fileEntry.isDirectory()) {
					readFiles(fileEntry, fileList);
				} else {
					fileList.add(fileEntry);
				}
			}
		}
	}

	/**
	 * prints out the program name and the given arguments.<br>
	 * shows the syntax if arguments are not yet available.
	 */
	private void title() {
		line();
		System.out.println(ListFiles.class.getSimpleName());
		line();
		if (toList == null) {
			System.out.println("arg1: directory with files to list");
		} else {
			System.out.println("arg1: " + toList);
		}
		line();
		System.out.println("");
	}

	/**
	 * main entry point when starting the application directly.
	 *
	 * @param args command line arguments:<br>
	 *             arg1: the directory with files to list
	 */
	public static void main(final String[] args) {
		final ListFiles checkFiles = new ListFiles(args);

		checkFiles.execute();
	}
}