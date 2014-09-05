package hudson.plugins.warnings.parser;

import java.io.IOException;
import java.util.Collection;
import org.junit.Test;
import static org.junit.Assert.*;

import hudson.plugins.analysis.util.model.FileAnnotation;

/**
 * Author: <a href="mailto:alexey.kislin@gmail.com">Alexey Kislin</a>
 * Date: 9/5/14
 * Time: 3:23 PM
 */
public class ScalacParserTest extends ParserTester {

    private static final String WRONG_CATEGORY_PARSING = "Wrong category parsing detected: ";

    @Test
    public void issue14043() throws IOException {
        Collection<FileAnnotation> warnings = parse("scalac.txt");
        assertEquals(WRONG_NUMBER_OF_WARNINGS_DETECTED, 2, warnings.size());
        for(FileAnnotation a : warnings)
            assertEquals(WRONG_CATEGORY_PARSING, a.getCategory() , "warning");
    }

    private Collection<FileAnnotation> parse(final String fileName) throws IOException {
        return new ScalacParser().parse(openFile(fileName));
    }

    @Override
    protected String getWarningsFile() {
        return "scalac.txt";
    }
}
