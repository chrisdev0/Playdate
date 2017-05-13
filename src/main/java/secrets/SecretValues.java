package secrets;

import spark.utils.IOUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Arrays;

import static com.lexicalscope.jewel.cli.CliFactory.parseArguments;
import static com.lexicalscope.jewel.cli.CliFactory.parseArgumentsUsingInstance;

public class SecretValues {

    public static void main(String[] args) throws Exception {

        File file = new File("newsecrets.txt");
        args = null;

        System.out.println("values");
        System.out.println(Arrays.toString(args));
        System.out.println();





    }

}
