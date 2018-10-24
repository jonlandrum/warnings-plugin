package hudson.plugins.warnings.parser;


import org.junit.Rule;
import org.jvnet.hudson.test.JenkinsRule;

/**
 * Tests the class {@link DynamicDocumentParser}.
 *
 *
 */
public class DynamicDocumentParserTest extends AbstractEclipseParserTest {
    @Rule
    public JenkinsRule jenkins = new JenkinsRule();

    private static final String TYPE = "Eclipse Dynamic";

    @Override
    protected AbstractWarningsParser createParser() {
        // CHECKSTYLE:OFF
        return new DynamicDocumentParser(TYPE,
                "(WARNING|ERROR)\\s*in\\s*(.*)\\(at line\\s*(\\d+)\\).*(?:\\r?\\n[^\\^]*)+(?:\\r?\\n(.*)([\\^]+).*)\\r?\\n(?:\\s*\\[.*\\]\\s*)?(.*)",
                "import hudson.plugins.warnings.parser.Warning\n" +
                "import hudson.plugins.analysis.util.model.Priority\n" +
                "String type = matcher.group(1)\n" +
                "Priority priority;\n" +
                "if (\"warning\".equalsIgnoreCase(type)) {\n" +
                "    priority = Priority.NORMAL;\n" +
                "}\n" +
                "else {\n" +
                "    priority = Priority.HIGH;\n" +
                "}\n" +
                "String fileName = matcher.group(2)\n" +
                "String lineNumber = matcher.group(3)\n" +
                "String message = matcher.group(6)\n" +
                "Warning warning = new Warning(fileName, Integer.parseInt(lineNumber), \"" + TYPE + "\", \"\", message);\n" +
                "\n" +
                "int columnStart = 0;\n" +
                "if (matcher.group(4) != null) {" +
                "    columnStart = matcher.group(4).length();\n" +
                " }\n" +
                "int columnEnd = columnStart + matcher.group(5).length();\n" +
                "warning.setColumnPosition(columnStart, columnEnd);\n" +
                "\n" +
                "        return warning;\n", TYPE, TYPE);
        // CHECKSTYLE:ON
    }

    @Override
    protected String getType() {
        return TYPE;
    }
}

