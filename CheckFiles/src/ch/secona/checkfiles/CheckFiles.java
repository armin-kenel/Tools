package ch.secona.checkfiles;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Checks a list of files in a given directory (arg1) for existence in another
 * directory (arg2).
 */
public class CheckFiles {
	private static final int PRINT_EACH_FILE_IF_LESS_THAN_THIS_NUMBER = 10;
	private int notFound = 0;
	private final String toCheck;
	private final String checkIn;

	/**
	 * @param args the command line arguments
	 */
	public CheckFiles(final String[] args) {
		if (args.length != 2) {
			title();
			throw new IllegalArgumentException("Two arguments expected.");
		}
		toCheck = args[0];
		checkIn = args[1];
		title();
	}

	/**
	 * Does the magic ...!
	 */
	public void execute() {
		final File toCheckDirectory = new File(toCheck);
		final File checkInDirectory = new File(checkIn);
		final List<File> toCheckFiles = new ArrayList<>();
		final List<File> checkInFiles = new ArrayList<>();

		readFiles(toCheckDirectory, toCheckFiles);
		readFiles(checkInDirectory, checkInFiles);
		line();
		System.out.println("Files to check : " + toCheckFiles.size());
		System.out.println("Files for check: " + checkInFiles.size());
		line();
		System.out.println("");
		toCheckFiles.stream().forEach(file -> {
			final String name = file.getName();
			final List<File> foundList = checkInFiles.stream().filter( //
					check -> check.getName().equalsIgnoreCase(name)).collect(Collectors.toList());

			if (foundList == null || foundList.isEmpty()) {
				System.out.println("Not found: " + name);
				notFound++;
			} else {
				final int size = foundList.size();

				if (size > 1) {
					System.out.println("" + size + " " + file.getAbsoluteFile());
					if (size < PRINT_EACH_FILE_IF_LESS_THAN_THIS_NUMBER) {
						foundList.stream().forEach(foundFile -> {
							System.out.println("- " + foundFile.getAbsolutePath());
						});
					}
					System.out.println("");
				}
			}
		});
		System.out.println("");
		line();
		System.out.println("Not found: " + notFound);
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
			if (fileEntry.isDirectory()) {
				readFiles(fileEntry, fileList);
			} else {
				fileList.add(fileEntry);
			}
		}
	}

	/**
	 * prints out the program name and the given arguments.<br>
	 * shows the syntax if arguments are not yet available.
	 */
	private void title() {
		line();
		System.out.println(CheckFiles.class.getSimpleName());
		line();
		if (toCheck == null) {
			System.out.println("arg1: directory with files to check");
		} else {
			System.out.println("arg1: " + toCheck);
		}
		if (checkIn == null) {
			System.out.println("arg2: directory with files");
		} else {
			System.out.println("arg2: " + checkIn);
		}
		line();
		System.out.println("");
	}

	/**
	 * main entry point when starting the application directly.
	 *
	 * @param args command line arguments:<br>
	 *             arg1: the directory with files to check<br>
	 *             arg2: check if files are in given directory
	 */
	public static void main(final String[] args) {
		final CheckFiles checkFiles = new CheckFiles(args);

		checkFiles.execute();
	}
}