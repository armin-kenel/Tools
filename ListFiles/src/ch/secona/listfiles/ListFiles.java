package ch.secona.listfiles;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.filechooser.FileSystemView;

/**
 * Lists each file on a single line
 */
public class ListFiles {
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

	private String getVolumeLabel() {
		final List<File> files = Arrays.asList(File.listRoots());
		String drive = null;
		final int pos = toList.indexOf(':');
		if (pos > 0) {
			drive = toList.substring(0, pos + 1).toUpperCase();
		}
		for (final File f : files) {
			final String s1 = FileSystemView.getFileSystemView().getSystemDisplayName(f);

			if (drive != null && s1.contains(drive)) {
				return s1;
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

			System.out.println(name + ";" + directory + ";" + length + ";" + volume);
		});
		System.out.println("");
		line();
		System.out.println("Done.");
		line();
	}

	private static void line() {
		System.out.println("-------------------------------------------");
	}

	/**
	 * reads all files in a given directory and its sub directories and adds the
	 * file name to the passed list.
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