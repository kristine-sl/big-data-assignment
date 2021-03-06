package a.warmup;

import org.apache.pig.pigunit.PigTest;
import org.apache.pig.tools.parameters.ParseException;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;

public class PigTopTenTest {

    //TODO: Find way to test Pig script
    @Ignore
    @Test
    public void testScript() throws IOException, ParseException {

        String[] args = { "n=3", "input=popular_words.txt", "output=limited" };

        PigTest test = new PigTest( "PIG_SCRIPT", args );

        String[] output = {

                "(Word5, 5)",
                "(Word6, 6)",
                "(Word7, 7)",
                "(Word8, 8)",
                "(Word9, 9)",
                "(Word10, 10)",
                "(Word11, 11)",
                "(Word12, 12)",
                "(Word13, 13)",
                "(Word14, 14)"
        };

        test.assertOutput( "limited", output );
    }
}
