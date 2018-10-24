package hudson.plugins.warnings.parser;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import org.junit.Test;

import hudson.plugins.analysis.util.model.FileAnnotation;
import hudson.plugins.analysis.util.model.Priority;

/**
 * Tests the class {@link CadenceIncisiveParser}.
 *
 *
 */
public class CadenceIncisiveParserTest extends ParserTester {
    private static final String WARNING_TYPE = new CadenceIncisiveParser().getGroup();

    /**
     * Test of createWarning method, of class {@link CadenceIncisiveParser}.
     *
     * @throws IOException
     *             in case of an error
     */
    @Test
    public void testCreateWarning() throws IOException {
        Collection<FileAnnotation> warnings = new CadenceIncisiveParser().parse(openFile());

        assertEquals(WRONG_NUMBER_OF_WARNINGS_DETECTED, 3, warnings.size());

        Iterator<FileAnnotation> iterator = warnings.iterator();
        checkWarning(iterator.next(),
                0,
                "Resolved design unit 'dummyram' at 'u_dummyrams' to 'dummysoc.dummyram:v' through a global search of all libraries.",
                "/NotFileRelated",
                WARNING_TYPE, "Warning (ncelab): CUSRCH", Priority.LOW);

        checkWarning(iterator.next(),
                313,
                "10 output ports were not connected",
                "/tmp/build-dir/../verilog/placeholder.v",
                WARNING_TYPE, "Warning (ncelab): CUVWSP", Priority.NORMAL);

        checkWarning(iterator.next(),
                310,
                "component instance is not fully bound (some.long:placeholder:blah:r1)",
                "/tmp/build-dir/freaking_gbit_astral.vhd",
                WARNING_TYPE, "Warning (ncelab): CUNOTB", Priority.NORMAL);

    }

    @Override
    protected String getWarningsFile() {
        return "CadenceIncisive.txt";
    }
}
