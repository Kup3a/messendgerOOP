package tools;

import java.io.*;

/**
 * Created by user on 20.10.2015.
 */
public class FileWork {
    /**
     *Writes a content into a file with giving path
     *
     * @param content   the string to write into a file
     * @param path      the path to needed file
     * @return
     *
     * After using this method file will contain only the content. Old information will be deleted.
     */
    private static int rewriteTheFile (String content, String path) {
        File file = new File(path);

        try {
            PrintWriter toFile = new PrintWriter(file);
            toFile.write(content);
            toFile.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return 1;
        }
        return 0;
    }

    /**
     * Read content of file with giving path
     *
     * @param path  the path to needed file
     * @return      data from the file
     * @throws FileNotFoundException
     */
    public static String readFile (String path) throws FileNotFoundException {
        StringBuilder sb = new StringBuilder();

        exists(path);

        BufferedReader in = new BufferedReader(new FileReader(new File(path)));
        String s;
        try {
            s = in.readLine();
            while (s != null) {
                sb.append(s);
                sb.append("\n");
                s = in.readLine();
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return sb.toString();
    }

    /**
     * Check the path to a file
     *
     * @param path  the path to check
     * @throws FileNotFoundException
     */
    private static void exists (String path) throws FileNotFoundException {
        File f = new File(path);
        if (!f.exists()) {
            throw new FileNotFoundException(f.getName());
        }
    }

    /**
     *Add a content to the end of file with giving path
     *
     * @param content   the string to write into a file
     * @param path      the path to needed file
     * @return
     * @throws FileNotFoundException
     *
     *
     */
    public static int writeToFile (String content, String path) throws FileNotFoundException {
        //exists(path);
        File f = new File(path);
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        StringBuilder sb = new StringBuilder();
        String oldFile = readFile(path);
        sb.append(oldFile);
        sb.append(content);
        rewriteTheFile(sb.toString(), path);

        return 0;
    }
}
