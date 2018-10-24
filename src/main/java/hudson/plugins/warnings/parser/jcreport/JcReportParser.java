package hudson.plugins.warnings.parser.jcreport;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import hudson.Extension;
import hudson.plugins.analysis.util.SecureDigester;
import hudson.plugins.analysis.util.model.FileAnnotation;
import hudson.plugins.analysis.util.model.Priority;
import hudson.plugins.warnings.parser.AbstractWarningsParser;
import hudson.plugins.warnings.parser.Messages;
import hudson.plugins.warnings.parser.ParsingCanceledException;
import hudson.plugins.warnings.parser.Warning;

/**
 * JcReportParser-Class. This class parses from the jcReport.xml and creates warnings from its content.
 *
 *
 */
@Extension
public class JcReportParser extends AbstractWarningsParser {
    private static final long serialVersionUID = -1302787609831475403L;

    /**
     * Creates a new instance of {@link JcReportParser}.
     */
    public JcReportParser() {
        super(Messages._Warnings_JCReport_ParserName(),
                Messages._Warnings_JCReport_LinkName(),
                Messages._Warnings_JCReport_TrendName());
    }

    /**
     * This overwritten method passes the reader to createReport() and starts adding all the warnings to the Collection
     * that will be returned at the end of the method.
     *
     * @param reader the reader that parses from the source-file.
     * @return the collection of Warnings parsed from the Report.
     * @throws IOException              thrown by createReport()
     * @throws ParsingCanceledException thrown by createReport()
     */
    @Override
    public Collection<FileAnnotation> parse(final Reader reader) throws IOException, ParsingCanceledException {
        Report report = createReport(reader);
        List<FileAnnotation> warnings = new ArrayList<FileAnnotation>();

        for (int i = 0; i < report.getFiles().size(); i++) {
            File file = report.getFiles().get(i);

            for (int j = 0; j < file.getItems().size(); j++) {
                Item item = file.getItems().get(j);
                Warning warning = createWarning(file.getName(), getLineNumber(item.getLine()),
                        item.getFindingtype(), item.getMessage(), getPriority(item.getSeverity()));

                warning.setOrigin(item.getOrigin());
                warning.setPackageName(file.getPackageName());
                warning.setPathName(file.getSrcdir());
                warning.setColumnPosition(getLineNumber(item.getColumn()), getLineNumber(item.getEndcolumn()));
                warnings.add(warning);
            }
        }
        return warnings;
    }

    /**
     * The severity-level parsed from the JcReport will be matched with a priority.
     *
     * @param issueLevel the severity-level parsed from the JcReport.
     * @return the priority-enum matching with the issueLevel.
     */
    private Priority getPriority(final String issueLevel) {
        if (StringUtils.isEmpty(issueLevel)) {
            return Priority.HIGH;
        }

        if (issueLevel.contains("CriticalError")) {
            return Priority.HIGH;
        }
        else if (issueLevel.contains("Error")) {
            return Priority.HIGH;
        }
        else if (issueLevel.contains("CriticalWarning")) {
            return Priority.HIGH;
        }
        else if (issueLevel.contains("Warning")) {
            return Priority.NORMAL;
        }
        else {
            return Priority.LOW;
        }
    }

    /**
     * Creates a Report-Object out of the content within the JcReport.xml.
     *
     * @param source the Reader-object that is the source to build the Report-Object.
     * @return the finished Report-Object that creates the Warnings.
     * @throws IOException due to digester.parse(new InputSource(source))
     */
    public Report createReport(final Reader source) throws IOException {
        try {
            SecureDigester digester = new SecureDigester(JcReportParser.class);

            String report = "report";
            digester.addObjectCreate(report, Report.class);
            digester.addSetProperties(report);

            String file = "report/file";
            digester.addObjectCreate(file, File.class);
            digester.addSetProperties(file,  "package", "packageName");
            digester.addSetProperties(file,  "src-dir", "srcdir");
            digester.addSetProperties(file);
            digester.addSetNext(file, "addFile", File.class.getName());

            String item = "report/file/item";
            digester.addObjectCreate(item, Item.class);
            digester.addSetProperties(item);
            digester.addSetProperties(item,  "finding-type", "findingtype");
            digester.addSetProperties(item,  "end-line", "endline");
            digester.addSetProperties(item,  "end-column", "endcolumn");
            digester.addSetNext(item, "addItem", Item.class.getName());

            return digester.parse(new InputSource(source));
        }
        catch (SAXException exception) {
            throw new IOException(exception);
        }
    }
}
