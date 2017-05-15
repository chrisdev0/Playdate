import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import secrets.Secrets;
import secrets.SecretsParserException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.Assert.*;

public class SecretsLoaderTest {

    private List<File> testFiles = new ArrayList<>();
    private Secrets secrets;
    private static int counter = 1;

    @Before
    public void init() {
        secrets = Secrets.getInstance();
    }

    /** Skapar en fil med namnet testFileNameNumber + nummer + .txt
     *  där nummer för filen blir antal element i testFiles när filen skapas + 1
     *  skriver contents till filen och returnerar filnamnet
     * */
    private String makeFileAndLoadContents(String contents) throws Exception {
        String testFileName = "testFileNameNumber" + counter++ + ".txt";
        File testFile = new File(testFileName);
        testFile.createNewFile();
        PrintWriter printWriter = new PrintWriter(testFile);
        printWriter.print(contents);
        printWriter.flush();
        printWriter.close();
        testFiles.add(testFile);
        return testFileName;
    }

    @Test
    public void testLoadSecretsFile() throws Exception{
        secrets.loadSecretsFile(makeFileAndLoadContents("key1=value1\nkey2=value2"));
        assertEquals("value1", secrets.getValue("key1").get());
        assertEquals("value2", secrets.getValue("key2").get());
    }

    @Test(expected = NoSuchElementException.class)
    public void testCommentsInSecretsFile() throws Exception{
        secrets.loadSecretsFile(makeFileAndLoadContents("key1=valuetest\n#commentrow=comment\nkey2=value3"));
        assertEquals(null, secrets.getValue("#commentrow").get());
    }

    @Test
    public void testEmptyRow() throws Exception{
        secrets.loadSecretsFile(makeFileAndLoadContents("key1=valuetest\n\nkey2=value3"));
        assertEquals("valuetest", secrets.getValue("key1").get());
        assertEquals("value3", secrets.getValue("key2").get());
    }

    @Test(expected = NoSuchElementException.class)
    public void testValueWithEqualSign() throws Exception{
        secrets.loadSecretsFile(makeFileAndLoadContents("key1=valuewith=sign\n#commentrow=comment\nkey2=value3"));
        assertEquals("valuewith=sign", secrets.getValue("#key1").get());
    }

    @Test(expected = NoSuchElementException.class)
    public void testKeyThatDoesntExist() throws Exception{
        assertEquals(null, secrets.getValue("keythatdoesn'texist").get());
    }

    @Test(expected = Exception.class)
    public void testFileNotExist() throws Exception{
        Secrets secrets = Secrets.getInstance();
        secrets.loadSecretsFile("fileThatDoesntExist.txt");
    }

    @Test(expected = SecretsParserException.class)
    public void testSecretsFileWithErrorsNoValue() throws Exception{
        secrets.loadSecretsFile(makeFileAndLoadContents("key1=\n#första key har inget value\nkey2=value3"));
    }

    @Test(expected = SecretsParserException.class)
    public void testSecretsFileWithErrorsNoEqualSign() throws Exception{
        secrets.loadSecretsFile(makeFileAndLoadContents("key1\n#första key har inget value\nkey2=value3"));
    }

    @After
    public void deleteTestFiles() {
        testFiles.forEach(File::delete);
    }

    @Test
    public void testLoadFromArgs() throws Exception {
        Secrets secrets = Secrets.getInstance();
        secrets.injectValuesFromArgs(new String[]{});
    }

}
