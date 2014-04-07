/*
 * Copyright 2008 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.tem.report.service.impl;

import static java.awt.Color.BLACK;
import static net.sf.jasperreports.crosstabs.JRCellContents.POSITION_X_LEFT;
import static net.sf.jasperreports.crosstabs.JRCellContents.POSITION_Y_TOP;
import static net.sf.jasperreports.crosstabs.JRCrosstab.RUN_DIRECTION_LTR;
import static net.sf.jasperreports.engine.JRElement.MODE_OPAQUE;
import static net.sf.jasperreports.engine.JRVariable.CALCULATION_SUM;

import java.awt.Color;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import net.sf.jasperreports.crosstabs.design.JRDesignCellContents;
import net.sf.jasperreports.crosstabs.design.JRDesignCrosstab;
import net.sf.jasperreports.crosstabs.design.JRDesignCrosstabBucket;
import net.sf.jasperreports.crosstabs.design.JRDesignCrosstabCell;
import net.sf.jasperreports.crosstabs.design.JRDesignCrosstabColumnGroup;
import net.sf.jasperreports.crosstabs.design.JRDesignCrosstabDataset;
import net.sf.jasperreports.crosstabs.design.JRDesignCrosstabMeasure;
import net.sf.jasperreports.crosstabs.design.JRDesignCrosstabRowGroup;
import net.sf.jasperreports.engine.JRBand;
import net.sf.jasperreports.engine.JRBoxContainer;
import net.sf.jasperreports.engine.JRChild;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.base.JRBaseBoxPen;
import net.sf.jasperreports.engine.base.JRBaseLineBox;
import net.sf.jasperreports.engine.design.JRDesignBand;
import net.sf.jasperreports.engine.design.JRDesignDataset;
import net.sf.jasperreports.engine.design.JRDesignDatasetRun;
import net.sf.jasperreports.engine.design.JRDesignElement;
import net.sf.jasperreports.engine.design.JRDesignElementGroup;
import net.sf.jasperreports.engine.design.JRDesignExpression;
import net.sf.jasperreports.engine.design.JRDesignField;
import net.sf.jasperreports.engine.design.JRDesignFrame;
import net.sf.jasperreports.engine.design.JRDesignGroup;
import net.sf.jasperreports.engine.design.JRDesignParameter;
import net.sf.jasperreports.engine.design.JRDesignStaticText;
import net.sf.jasperreports.engine.design.JRDesignSubreport;
import net.sf.jasperreports.engine.design.JRDesignTextElement;
import net.sf.jasperreports.engine.design.JRDesignTextField;
import net.sf.jasperreports.engine.design.JasperDesign;

import org.apache.log4j.Logger;
import org.kuali.kfs.module.tem.report.RString;
import org.kuali.kfs.module.tem.report.RTextStyle;
import org.kuali.kfs.module.tem.report.annotations.ColumnFooter;
import org.kuali.kfs.module.tem.report.annotations.ColumnHeader;
import org.kuali.kfs.module.tem.report.annotations.Crosstab;
import org.kuali.kfs.module.tem.report.annotations.DetailSection;
import org.kuali.kfs.module.tem.report.annotations.Group;
import org.kuali.kfs.module.tem.report.annotations.PageFooter;
import org.kuali.kfs.module.tem.report.annotations.PageHeader;
import org.kuali.kfs.module.tem.report.annotations.Parameter;
import org.kuali.kfs.module.tem.report.annotations.SubReport;
import org.kuali.kfs.module.tem.report.annotations.Summary;
import org.kuali.kfs.module.tem.report.service.TravelReportFactoryService;
import org.kuali.kfs.sys.report.ReportInfo;

/**
 * Service interface for creating travel reports. Uses annotations from the {@link org.kuali.kfs.module.tem.report.annotations}
 * package to build reports on a {@link ReportInfo} instance. Primarily utilizes classes from
 * {@link net.sf.jasperreports.engine.design}
 *
 */
@SuppressWarnings("deprecation")
public class TravelReportFactoryServiceImpl implements TravelReportFactoryService {

    public static Logger LOG = Logger.getLogger(TravelReportFactoryServiceImpl.class);

    private static final int MARGIN            = 10;
    private static final int PAGEHEADER_HEIGHT = 25;
    private static final int REPORT_HEIGHT     = 842 - MARGIN;
    private static final int TITLE_HEIGHT      = 842 / 9;                                             // 1/9 of total height
    private static final int DETAIL_HEIGHT     = (REPORT_HEIGHT - TITLE_HEIGHT);
    private static final int SUMMARY_HEIGHT    = ((DETAIL_HEIGHT) / 5) + PAGEHEADER_HEIGHT;           // 20% of remaining height
    private static final int SUBREPORT_HEIGHT  = (DETAIL_HEIGHT - SUMMARY_HEIGHT) / 3;                // 3 subreports allowed
    private static final int COLHEADER_HEIGHT  = 20;
    private static final int PAGEFOOTER_HEIGHT = 17;
    private static final int COLFOOTER_HEIGHT  = 16;
    private static final int CT_HEADER_WIDTH   = 175;
    private static final int CELL_WIDTH        = 50;
    private static final int CELL_HEIGHT       = 18;
    private static final int GROUP_HEIGHT      = (DETAIL_HEIGHT / 3);
    protected Map<String,RTextStyle> styles;

    /**
     * Creates a level 1 header preset
     *
     * @param str is a {@link String} to use the preset on
     * @return {@link RString} instance that is the embodiment of the style
     */
    @Override
    public RString h1(final String str) {
        return applyStyle(str);
    }

    /**
     * Creates a level 2 header preset
     *
     * @param str is a {@link String} to use the preset on
     * @return {@link RString} instance that is the embodiment of the style
     */
    @Override
    public RString h2(final String str) {
        return applyStyle(str);
    }

    /**
     * Creates a level 3 header preset
     *
     * @param str is a {@link String} to use the preset on
     * @return {@link RString} instance that is the embodiment of the style
     */
    @Override
    public RString h3(final String str) {
        return applyStyle(str);
    }

    /**
     * Creates a level 4 header preset
     *
     * @param str is a {@link String} to use the preset on
     * @return {@link RString} instance that is the embodiment of the style
     */
    @Override
    public RString h4(final String str) {
        return applyStyle(str);
    }

    /**
     * Creates a level 5 header preset
     *
     * @param str is a {@link String} to use the preset on
     * @return {@link RString} instance that is the embodiment of the style
     */
    @Override
    public RString h5(final String str) {
        return applyStyle(str);
    }

    /**
     * Creates a Normal type preset
     *
     * @param str is a {@link String} to use the preset on
     * @return {@link RString} instance that is the embodiment of the style
     */
    public RString normal(final String str) {
        return applyStyle(str);
    }

    protected RString applyStyle(final String str) {
        final String styleName = new Throwable().getStackTrace()[1].getMethodName();
        return new RString(str, getStyles().get(styleName));
    }

    public JRBand createTitle(final ReportInfo report, final String title) throws Exception {
        final JRDesignBand retval = new JRDesignBand();
        retval.setHeight(93);

        final JRDesignTextField headerLine1 = h1("$P{report}.getInstitution()").toTextField();
        addDesignElementTo(retval, headerLine1, 0, 0, 356, 30);
        final JRDesignStaticText headerLine2 = h5(title + " for # ").toStaticText();
        addDesignElementTo(retval, headerLine2, 0, 31, 426, 24);
        final JRDesignTextField headerLine3 = h5("$P{report}.getTripId()").toTextField();
        addDesignElementTo(retval, headerLine3, 275, 31, 146, 24);
        final JRDesignStaticText headerLine4 = h5("Purpose: ").toStaticText();
        addDesignElementTo(retval, headerLine4, 0, 52, 100, 20);
        final JRDesignStaticText headerLine5 = h5("Dates: ").toStaticText();
        addDesignElementTo(retval, headerLine5, 0, 72, 100, 20);
        final JRDesignTextField headerLine4Field1 = h5("$P{report}.getPurpose()").toTextField();
        addDesignElementTo(retval, headerLine4Field1, 65, 52, 472, 20);
        final JRDesignTextField headerLine5Field1 = h5("$P{report}.getBeginDate()").toTextField(java.util.Date.class);
        addDesignElementTo(retval, headerLine5Field1, 45, 72, 75, 20);
        final JRDesignTextField headerLine5Field2 = h5("$P{report}.getEndDate()").toTextField(java.util.Date.class);
        addDesignElementTo(retval, headerLine5Field2, 150, 72, 75, 20);

        return retval;
    }

    /**
     * Retrieves the names of crosstabs. Names are determined by the {@link Field} names of the {@link Field}s in
     * the {@link ReportInfo} class with the {@link Crosstab} annotation.
     *
     * @see org.kuali.kfs.module.tem.report.annotations.Crosstab
     * @param report instance to get crosstab names for
     * @return a {@link Collection} of names for the crosstabs
     */
    protected Collection<String> getCrosstabNames(final ReportInfo report) {
        final Collection<String> retval = new ArrayList<String>();
        for (final Field f : report.getClass().getDeclaredFields()) {
            if (f.getAnnotation(Crosstab.class) != null) {
                retval.add(f.getName());
            }
        }
        return retval;
    }

    /**
     * Retrieves the names of crosstabs. Names are determined by the {@link Field} names of the {@link Field}s in
     * the {@link ReportInfo} class with the {@link Crosstab} annotation. Also, only gets {@link Crosstab}
     * {@link Field}s by additional annotations that may describe them.
     *
     * @see org.kuali.kfs.module.tem.report.annotations.Crosstab
     * @param report instance to get crosstab names for
     * @param annotations {@link Class} instances of additional annotations that must be present on the {@link Field}
     * @return a {@link Collection} of names for the crosstabs
     */
    protected Collection<String> getCrosstabNames(final ReportInfo report, final Class ... annotations) {
        final Collection<String> retval = new ArrayList<String>();
        for (final Field f : report.getClass().getDeclaredFields()) {
            boolean valid = false;
            valid |= f.getAnnotation(Crosstab.class) != null;
            if (annotations != null && annotations.length > 0) {
                for (final Class annotation : annotations) {
                    valid &= f.getAnnotation(annotation) != null;
                }
            }

            if (valid) {
                retval.add(f.getName());
            }
        }
        return retval;
    }

    /**
     * Determines if the {@link ReportInfo} instance has any {@link Subreport} defined
     *
     * @param report to check for {@link Subreport} in
     * @return true if there is a {@link Subreport} or false otherwise
     */
    protected boolean hasSubreport(final ReportInfo report) {
        return hasFieldWithAnnotations(report, SubReport.class);
    }

    /**
     * Determines if the {@link ReportInfo} instance has any {@link Summary} defined
     *
     * @param report to check for {@link Summary} in
     * @return true if there is a {@link Summary} or false otherwise
     */
    @Override
    public boolean hasSummary(final ReportInfo report) {
        return hasFieldWithAnnotations(report, Summary.class);
    }

    /**
     * Determines if the {@link ReportInfo} instance has any {@link DetailSection} defined
     *
     * @param report to check for {@link DetailSection} in
     * @return true if there is a {@link DetailSection} or false otherwise
     */
    protected boolean hasDetail(final ReportInfo report) {
        return hasFieldWithAnnotations(report, DetailSection.class) || hasGroup(report);
    }

    /**
     * Determines if the {@link ReportInfo} instance has any {@link PageHeader} defined
     *
     * @param report to check for {@link PageHeader} in
     * @return true if there is a {@link PageHeader} or false otherwise
     */
    protected boolean hasPageHeader(final ReportInfo report) {
        return hasFieldWithAnnotations(report, PageHeader.class);
    }

    /**
     * Determines if the {@link ReportInfo} instance has any {@link PageFooter} defined
     *
     * @param report to check for {@link PageFooter} in
     * @return true if there is a {@link PageFooter} or false otherwise
     */
    protected boolean hasPageFooter(final ReportInfo report) {
        return hasFieldWithAnnotations(report, PageFooter.class);
    }

    /**
     * Determines if the {@link ReportInfo} instance has any {@link ColumnHeader} defined
     *
     * @param report to check for {@link ColumnHeader} in
     * @return true if there is a {@link ColumnHeader} or false otherwise
     */
    protected boolean hasColumnHeader(final ReportInfo report) {
        return hasFieldWithAnnotations(report, ColumnHeader.class);
    }

    /**
     * Determines if the {@link ReportInfo} instance has any {@link ColumnFooter} defined
     *
     * @param report to check for {@link ColumnFooter} in
     * @return true if there is a {@link ColumnFooter} or false otherwise
     */
    protected boolean hasColumnFooter(final ReportInfo report) {
        return hasFieldWithAnnotations(report, ColumnFooter.class);
    }

    /**
     * Check to see if a {@link ReportInfo} instance belongs to a {@link Class} with a given annotation. This
     * is a very general method used to find things like {@link ColumnFooter} instances or {@link PageHeader}
     * instances
     *
     * @param report is the {@link ReportInfo} instance
     * @param annotation to look for that would be on a field like {@link SubReport} or {@link Summary}
     * @return true if the annotation exists, false otherwise
     */
    protected boolean hasFieldWithAnnotation(final ReportInfo report, final Class annotation) {
        for (final Field field : report.getClass().getDeclaredFields()) {
            if (field.getAnnotation(annotation) != null) {
                return true;
            }
        }
        return false;
    }

    /**
     * Create a detail section in a {@link JasperReport}. Checks the {@link JasperReport} fields for a
     * {@link Detail} annotation and processes that {@link Detail} field.
     */
    public JRBand createDetail(final Field subReport) throws Exception {
        final JRDesignBand retval = new JRDesignBand();

        retval.setHeight(SUBREPORT_HEIGHT - 25);

        LOG.debug("Subreport Detail band has height of "+ retval.getHeight());

        // In this case, we are creating a subreport and subreports have either a crosstab or not
        LOG.debug("Checking if "+ subReport+ " is a crosstab "+ isCrosstab(subReport));
        if (isCrosstab(subReport)) {
            final JRDesignCrosstab crosstab = createCrosstab();
            LOG.debug("Got crosstab of height "+ crosstab.getHeight()+
                  " and width "+ crosstab.getWidth()+
                  " adding to design of height "+ retval.getHeight());

            retval.addElement(crosstab);
        }

        return retval;
    }

    /**
     * Create a summary section in a {@link JasperReport}. Checks the {@link JasperReport} fields for a
     * {@link Summary} annotation and processes that {@link Summary} field.
     *
     * @return subreport is the {@link SubReport} {@link Field}
     * @return {@link JRBand} of the summary that goes into a subreport
     */
    public JRBand createSummary(final Field subReport) throws Exception {
        final JRDesignBand retval = new JRDesignBand();

        retval.setHeight(SUBREPORT_HEIGHT);

        LOG.debug("Summary band has height of "+ retval.getHeight());

        // In this case, we are creating a subreport and subreports have either a crosstab or not
        LOG.debug("Checking if "+ subReport+ " is a crosstab "+ isCrosstab(subReport));
        if (isCrosstab(subReport)) {
            final JRDesignCrosstab crosstab = createCrosstab();
            LOG.debug("Got crosstab of height "+ crosstab.getHeight()+ " and width "+ crosstab.getWidth()+ " adding to design of height "+ retval.getHeight());

            retval.addElement(crosstab);
        }

        return retval;
    }

    /**
     * Create a detail section in a {@link JasperReport}. Checks the {@link JasperReport} fields for a
     * {@link Detail} annotation and processes that {@link Detail} field.
     */
    protected JRBand createDetailForSummary(final ReportInfo report) throws Exception {
        final JRDesignBand retval = new JRDesignBand();

        int maxHeight = DETAIL_HEIGHT;
        LOG.debug("Summary: Initial height is "+ DETAIL_HEIGHT);

        retval.setHeight(CELL_HEIGHT + 5);

        LOG.debug("Summary: Detail band has height of "+ maxHeight);
        int y = 0;

        LOG.info("Summary: Adding fields for detail");
        final Field summaryField = getFieldWithAnnotation(report, Summary.class);
        if (isCrosstab(summaryField)) {
            // If the summary has a crosstab, then we want to use the Summary section for rendering the crosstab.
            final Collection<JRChild> elements = processFields(report, Summary.class, Crosstab.class);
            for (final JRChild element : elements) {
                final JRDesignCrosstab crosstab = (JRDesignCrosstab) element;

                LOG.debug("Adding crosstab to summary "+ crosstab+ " with height "+ crosstab.getHeight());
                crosstab.setY(y);
                retval.addElement(crosstab);
                y += crosstab.getHeight() + PAGEHEADER_HEIGHT;
                retval.setHeight(y);
            }
        }
        else {
            // No crosstab, so use the detail
            final JRDesignTextField nameField = normal("$F{name}").toTextField();
            addDesignElementTo(retval, nameField, (CELL_WIDTH * 3 + 5) * 0, 0, CELL_WIDTH * 3, CELL_HEIGHT);

            final JRDesignTextField amountField = normal("$F{amount}").toTextField(java.math.BigDecimal.class);
            addDesignElementTo(retval, amountField, (CELL_WIDTH * 3 + 5) * 1, 0, CELL_WIDTH * 3, CELL_HEIGHT);
        }

        return retval;
    }

    /**
     * Create a detail section in a {@link JasperReport}. Checks the {@link JasperReport} fields for a
     * {@link Detail} annotation and processes that {@link Detail} field.
     */
    public JRBand createDetail(final ReportInfo report, final Integer reportIndex) throws Exception {
        final JRDesignBand retval = new JRDesignBand();

        if (!(hasDetail(report) || hasSubreport(report))) {
            LOG.info("No detail for this report");
            LOG.debug("Has detail "+ hasDetail(report));
            LOG.debug("Has Subreport "+ hasSubreport(report));
            if (reportIndex > 0) {
                return null;
            }
            retval.setHeight(0);
            return retval;
        }

        int maxHeight = DETAIL_HEIGHT;
        LOG.debug("Initial height is "+ DETAIL_HEIGHT);

        retval.setHeight(0);

        LOG.info("Determining maximum detail space size");
        if (hasPageHeader(report)) {
            maxHeight -= PAGEHEADER_HEIGHT;
        }
        if (hasPageFooter(report)) {
            maxHeight -= PAGEFOOTER_HEIGHT;
        }
        if (hasColumnHeader(report)) {
            maxHeight -= COLHEADER_HEIGHT;
        }
        if (hasColumnFooter(report)) {
            maxHeight -= COLFOOTER_HEIGHT;
        }

        LOG.debug("Detail band has height of "+ maxHeight);

        // Detail includes subreports. This really means besides subreports.
        if (hasDetail(report)) {
            LOG.info("Adding fields for detail");
            final JRDesignTextField nameField = normal("$F{name}").toTextField();
            addDesignElementTo(retval, nameField, 0, 0, CELL_WIDTH * 3, CELL_HEIGHT);

            final JRDesignTextField dateField = normal("$F{date}").toTextField();
            addDesignElementTo(retval, dateField, (CELL_WIDTH * 3 + 5) * 1, 0, CELL_WIDTH * 3, CELL_HEIGHT);

            final JRDesignTextField amountField = normal("$F{amount}").toTextField(java.math.BigDecimal.class);
            addDesignElementTo(retval, amountField, (CELL_WIDTH * 3 + 5) * 2, 0, CELL_WIDTH * 3, CELL_HEIGHT);
        }

        int y = 0;
        int pageidx = 0;
        int upperBound = 19;

        final Collection<JRChild> elements = processFields(report, SubReport.class);
        LOG.debug("Building report detail starting at position "+ y);
        LOG.debug("Adding "+ elements.size()+ " elements to the report");
        for (final JRChild obj : elements) {

            if (obj != null && obj instanceof JRDesignElement) {
                final JRDesignElement element = (JRDesignElement) obj;
                // If we exceed the bounds of the report, we have to switch pages.
                // This is pretty much what we do until we reach the correctpage
                if ((y + element.getHeight()) >= maxHeight) {
                    pageidx++;
                    y = 0;
                }

                // Going to get rid of this. There should never be a crosstab in the detail section unless
                // it is in a subreport or a group
                if (element instanceof JRDesignCrosstab) {
                    final JRDesignCrosstab crosstab = (JRDesignCrosstab) element;
                    final String crosstabName       = crosstab.getDataset().getDatasetRun().getDatasetName();
                    final JRDesignStaticText headerLine4 = h4(crosstabName).toStaticText();

                    if (pageidx == reportIndex) {
                        addDesignElementTo(retval, headerLine4, 0, y, 356, 22);
                    }
                    y += 25;
                }

                LOG.debug("Adding element to detail "+ element.getClass()+ " " + element+ " with height "+ element.getHeight()+ " at y = "+ y);

                // When we're on the correct page index, we add elements
                LOG.debug("pageIdx = "+ pageidx);
                LOG.debug("reportIndex = "+ reportIndex);
                if (pageidx == reportIndex) {
                    element.setY(y);
                    retval.addElement(element);
                }
                y += element.getHeight() + 30;
                upperBound = Math.max(upperBound, y);
                LOG.debug("upperbound = "+ upperBound);
            }
        }

        // Only return if elements were added.
        if (hasCrosstabs(report) && subReportCount(retval) == 0) {
            return null;
        }

        if (hasGroup(report) && reportIndex > 0) {
            return null;
        }

        LOG.debug("Setting height to "+ upperBound);
        retval.setHeight(upperBound);

        return retval;
    }

    /**
     * Measure instance used for crosstabs.
     */
    protected JRDesignCrosstabMeasure measure(final String name) {
        final JRDesignCrosstabMeasure retval = new JRDesignCrosstabMeasure();
        final JRDesignExpression expression = new JRDesignExpression();
        expression.setValueClass(java.math.BigDecimal.class);
        expression.setText("$F{" + name + "}");
        retval.setValueExpression(expression);
        retval.setName(name + "Measure");
        retval.setValueClassName("java.math.BigDecimal");
        retval.setCalculation(CALCULATION_SUM);

        return retval;
    }

    /**
     * Convenience method for creating buckets ({@link JRDesignCrosstabBucket}) instances for things like groups and crosstabs.
     *
     * @param text is the {@link String} value of an expression used for the bucket
     * @param valueClass is a {@link Class} that the text is eventually translated to
     */
    protected JRDesignCrosstabBucket bucket(final String text, final Class valueClass) {
        final JRDesignCrosstabBucket retval = new JRDesignCrosstabBucket();
        final JRDesignExpression expression = new JRDesignExpression();
        expression.setValueClass(valueClass);
        expression.setText(text);
        retval.setExpression(expression);
        return retval;
    }

    /**
     * Convenience method for adding an element to a parent whilst adjusting the size and location
     *
     * @param band is a {@link JRDesignElementGroup} you want to add to
     * @param toAdd is the {@link JRDesignTextElement} you'd like to add to the aforementioned band.
     * @param x is the x-location
     * @param y is the y-location
     * @param width is the width to set to
     * @param height is the height to set to
     */
    protected void addDesignElementTo(final JRDesignElementGroup band, final JRDesignTextElement toAdd, int x, int y, int width, int height) {
        toAdd.setX(x);
        toAdd.setY(y);
        toAdd.setWidth(width);
        toAdd.setHeight(height);
        band.addElement(toAdd);
    }

    /**
     * If report title not set
     * Guess what the report title should be. Uses the classname of the {@link ReportInfo} instance to
     * determine what the title should be. Rather than using camel case, adds spaces.
     *
     * @param report to create title for
     * @return
     */
    protected String getReportTitle(final ReportInfo report) {
        if(report.getReportTitle() != null){
            return report.getReportTitle();
        }

        final String className = report.getClass().getSimpleName();
        return splitByUpperCase(className.substring(0, className.indexOf("Report")));
    }

    /**
     * Capitalizes each word.
     *
     * @param toCapitalize {@link String} instance to capitalize
     * @return new {@link String} instance
     */
    protected String initialCaps(final String toCapitalize) {
        final char[] str_arr = toCapitalize.toCharArray();

        str_arr[0] = Character.toUpperCase(str_arr[0]);
        for (int i = 1; i < str_arr.length; i++) {
            if (str_arr[i] == ' ' && (i + 1) < str_arr.length) {
                str_arr[i + 1] = Character.toUpperCase(str_arr[i + 1]);
            }
        }
        return new String(str_arr);
    }

    /**
     * Adds spaces at upper case characters thus splitting up the camel case of a string like in a class name
     * @param toSplit is a {@link String} to split
     * @return a {@link String} instance that has added spaces before uppercase characters
     */
    protected String splitByUpperCase(final String toSplit) {
        final StringBuilder retval = new StringBuilder(toSplit);
        final char[] str_arr = toSplit.toCharArray();

        int inc = 0;
        for (int i = 1; i < str_arr.length; i++) {
            if (str_arr[i] >= 'A' && str_arr[i] <= 'Z' ) {
                retval.insert(i + inc, ' '); // Add a space after uppercase characters
                inc++;
            }
        }
        return retval.toString();
    }

    /**
     * Systematically retrieves groups from a {@link ReportInfo} instances, creates a {@link JRDesignGroup} from the info,
     * then adds the {@link JRDesignGroup} instance to a {@link JasperDesign} for each group.
     *
     * @param report the {@link ReportInfo} instance to get groups for
     * @param designObj is the {@link JasperDesign} instance to add {@link JRDesignGroup} instances to
     */
    protected void addGroupsFor(final ReportInfo report, final JasperDesign designObj) throws Exception {
        for (final Field field : report.getClass().getDeclaredFields()) {
            if (isGroup(field)) {
                LOG.info("Adding a group for field "+ field.getName());
                final JRDesignGroup group = createGroup(report, field);
                designObj.addGroup(group);
            }
        }
    }

    /**
     * Determines from the {@link Class} of a {@link ReportInfo} instance what {@link Field}s attached to it are
     * qualified as {@link JRDesignParameter}s to add to your {@link JasperDesign}.
     *
     * @param report the {@link ReportInfo} instance representing the report
     * @param designObj the {@link JasperDesign} instance to add parameters to
     */
    protected void addReportParametersFor(final ReportInfo report, final JasperDesign designObj) throws Exception {

        for (final Field field : report.getClass().getDeclaredFields()) {
            if (isParameter(field)) {
                final JRDesignParameter designParameter = new JRDesignParameter();
                designParameter.setName(field.getName());
                designParameter.setValueClass(field.getType());
                designObj.addParameter(designParameter);
                LOG.debug("Added parameter "+ designParameter.getName());
            }
            if (isSubreport(field)) {
                final JRDesignParameter designParameter = new JRDesignParameter();
                designParameter.setName(field.getName() + "Subreport");
                designParameter.setValueClass(JasperReport.class);
                designObj.addParameter(designParameter);
                LOG.debug("Added parameter "+ designParameter.getName());
            }
            else if (isCrosstab(field)) {
                final JRDesignDataset dataset = new JRDesignDataset(false);
                addReportFieldsFor(report, dataset);
                dataset.setName(initialCaps(field.getName()));
                designObj.addDataset(dataset);
            }
        }

        final JRDesignParameter designParameter = new JRDesignParameter();
        designParameter.setName("report");
        designParameter.setValueClass(report.getClass());
        designObj.addParameter(designParameter);
        LOG.debug("Added parameter "+ designParameter.getName());
    }

    /**
     * Determines from the {@link Class} of a {@link ReportInfo} instance what {@link Field}s attached to it are
     * qualified as {@link JRDesignParameter}s to add to your {@link JasperDesign}
     *
     * @param report the {@link ReportInfo} instance representing the report
     * @param designObj the {@link JasperDesign} instance to add parameters to
     */
    protected void addReportFieldsFor(final ReportInfo report, final JasperDesign designObj) throws Exception {
        final Class dataClass = findDataClassFor(report);
        LOG.debug("Found data class "+ dataClass);

        for (final Field field : dataClass.getDeclaredFields()) {
            final JRDesignField designField = new JRDesignField();
            LOG.debug("Adding field " + field.getName());
            designField.setName(field.getName());
            designField.setValueClass(field.getType());
            designObj.addField(designField);
        }
    }

    /**
     * Determines from the {@link Class} of a {@link ReportInfo} instance what {@link Field}s attached to it are
     * qualified as {@link JRDesignField}s to add to your {@link JasperDesign}.
     *
     * @param report the {@link ReportInfo} instance representing the report
     * @param designObj the {@link JasperDesign} instance to add {@link JRDesignField}s to
     */
    protected void addReportFieldsFor(final ReportInfo report, final JRDesignDataset dataset) throws Exception {
        final Class dataClass = findDataClassFor(report);
        LOG.debug("Found data class "+ dataClass);

        for (final Field field : dataClass.getDeclaredFields()) {
            final JRDesignField designField = new JRDesignField();
            designField.setName(field.getName());
            designField.setValueClass(field.getType());
            dataset.addField(designField);
        }
    }

    /**
     * A {@link ReportInfo} can have one type that represents the structure that all data will be. This method determines what
     * that is by accessing the parameterized type of a setter method  who's getter returns a {@link JRDataSource} instance.
     *
     * @param report is the report to get the data class for
     * @return {@link Class} instance that is the type for the data
     */
    protected Class findDataClassFor(final ReportInfo report) throws Exception {
        final Class reportClass = report.getClass();

        // Digging up all the setters
        for (final Field field : reportClass.getDeclaredFields()) {
            LOG.debug("Examinine field "+ field+ " with type "+ field.getType());
            if (field.getType().equals(JRDataSource.class)) {
                // get the dataset for this class
                final String setterName = "set" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
                final Method setter = reportClass.getMethod(setterName, new Class[] {Collection.class});
                LOG.debug("Determining what that data class should be. Found dataset setter method "+ setter.getName());

                // Should only have one parameter
                final ParameterizedType methodParamType = (ParameterizedType) (setter.getGenericParameterTypes()[0]);
                return (Class) methodParamType.getActualTypeArguments()[0];
            }
        }
        return null;
    }

    /**
     * A lot like {@link #designReport(ReportInfo)} except it is intended for {@link SubReport}s
     *
     * @param report is an instance of the {@link ReportInfo} which represents a report.
     * @param field {@link Field} instance with a {@link SubReport} annotation
     * @return a {@link JasperDesign} instance used in a {@link JasperReport}
     * @see org.kuali.kfs.module.tem.report.service.TravelReportFactoryService#designReport(org.kuali.kfs.sys.report.ReportInfo)
     */
    public JasperDesign designReport(final ReportInfo report, final Field field) throws Exception {
        LOG.info("Designing a subreport for field "+ field.getName());

        LOG.debug("Checking the "+ field.getName()+ " for data");
        try {
            field.setAccessible(true);
            if (field.get(report) == null) {
                return null;
            }
            LOG.debug("Subreport has data. Proceeding to design subreport.");
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }

        final JasperDesign designObj = new JasperDesign();
        designObj.setName(field.getName());
        designObj.setTitle(createTitle(report, getReportTitle(report)));

        addReportParametersFor(report, designObj);
        designObj.addImport(report.getClass().getName());
        addReportFieldsFor(report, designObj);

        final JRDesignBand titleBand = new JRDesignBand();
        titleBand.setHeight(23);

        final JRDesignStaticText headerLine4 = h4(initialCaps(field.getName())).toStaticText();
        addDesignElementTo(titleBand, headerLine4, 0, 0, 356, 22);

        designObj.setPageHeader(titleBand);

        designObj.setPageWidth(595);
        designObj.setPageHeight(REPORT_HEIGHT);
        designObj.setLeftMargin(MARGIN);
        designObj.setRightMargin(MARGIN);
        designObj.setTopMargin(MARGIN);
        designObj.setBottomMargin(MARGIN);

        if (hasDetail(report)) {
            LOG.debug("Creating detail for subreport");
            designObj.setDetail(createDetail(report, 0));
        }
        else {
            LOG.debug("Creating summary for subreport");
            designObj.setSummary(createSummary(field));
        }

        return designObj;
    }

    /**
     * Populate the design of a report. Report's main content container is a design.
     *
     * @param report in
     * @return {@link JasperDesign} out
     * @throws Exception because there is a lot of under-the-hood I/O and reflection going on.
     */
    @Override
    public JasperDesign designSummary(final ReportInfo report) throws Exception {
        final JasperDesign designObj = new JasperDesign();
        final String reportTitle = getReportTitle(report);
        designObj.setName(reportTitle);
        designObj.setTitle(createTitle(report, reportTitle));

        LOG.info("Summary: Loading report parameters");
        addReportParametersFor(report, designObj);
        designObj.addImport(report.getClass().getName());
        LOG.info("Summary: Loading report fields");
        addReportFieldsFor(report, designObj);

        LOG.info("Summary: Setting report dimensions");
        designObj.setPageWidth(595);
        designObj.setPageHeight(REPORT_HEIGHT);
        designObj.setLeftMargin(MARGIN);
        designObj.setRightMargin(MARGIN);
        designObj.setTopMargin(MARGIN);
        designObj.setBottomMargin(MARGIN);

        LOG.info("Summary: Adding header and footer");
        final JRDesignBand header = new JRDesignBand();
        final JRDesignStaticText headerLine4 = h4("Summary").toStaticText();
        addDesignElementTo(header, headerLine4, 0, PAGEHEADER_HEIGHT, 356, 22);
        header.setHeight(PAGEHEADER_HEIGHT * 2);
        designObj.setPageHeader(header);

        LOG.info("Creating report detail");
        final Field summaryField = getFieldWithAnnotation(report, Summary.class);
        final JRBand summary = createSummary(summaryField);

        if (summary != null) {
            designObj.setSummary(summary);
            return designObj;
        }

        return null;
    }

    /**
     * Populate the design of a report. Report's main content container is a design.
     *
     * @param report in
     * @return {@link JasperDesign} out
     * @throws Exception because there is a lot of under-the-hood I/O and reflection going on.
     */
    @Override
    public JasperDesign designReport(final ReportInfo report, final Integer reportIndex) throws Exception {
        final JasperDesign designObj = new JasperDesign();
        final String reportTitle = getReportTitle(report);
        designObj.setName(reportTitle);

        if (reportIndex < 1) {
            designObj.setTitle(createTitle(report, reportTitle));
        }

        LOG.info("Loading report parameters");
        addReportParametersFor(report, designObj);
        designObj.addImport(report.getClass().getName());
        LOG.info("Loading report fields");
        addReportFieldsFor(report, designObj);

        LOG.info("Setting report dimensions");
        designObj.setPageWidth(595);
        designObj.setPageHeight(REPORT_HEIGHT);
        designObj.setLeftMargin(MARGIN);
        designObj.setRightMargin(MARGIN);
        designObj.setTopMargin(MARGIN);
        designObj.setBottomMargin(MARGIN);

        // Groups before detail
        LOG.info("Handling groups");
        addGroupsFor(report, designObj);

        LOG.info("Creating report detail");
        final JRBand detail = createDetail(report, reportIndex);

        if (detail != null) {
            designObj.setDetail(detail);
        }
        else {
            return null;
        }

        return designObj;
    }

    /**
     * Determines what to do with a {@link Field} in a {@link ReportInfo} instance by the annotations on that
     * {@link Field} and creates a {@link JRDesignElement} from it.
     *
     *
     * @param param
     * @param field
     * @return {@link JRDesignElement} instance
     * @throws Exception
     */
    protected JRChild createElementForField(final ReportInfo report, final Field field) throws Exception {
        LOG.info("Processing field "+ field.getName());
        if (isSubreport(field)) {
            LOG.debug("Creating a report element from field "+ field.getName());
            return createSubreport(report, field);
        }

        if (isCrosstab(field)) {
            LOG.debug("Creating a crosstab from field "+ field.getName());
            final JRDesignCrosstab crosstab       = createCrosstab(report, field);
            final JRDesignCrosstabDataset dataset = new JRDesignCrosstabDataset();
            final JRDesignDatasetRun dsRun        = new JRDesignDatasetRun();
            final JRDesignExpression dsExpression = new JRDesignExpression();
            final String getterName               = "get" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
            dsExpression.setText("$P{report}." + getterName + "()");
            dsExpression.setValueClass(JRDataSource.class);
            dsRun.setDatasetName(initialCaps(field.getName()));
            dsRun.setDataSourceExpression(dsExpression);
            dataset.setDatasetRun(dsRun);
            dataset.setDataPreSorted(true);
            crosstab.setDataset(dataset);

            return crosstab;
        }

        if (isSummary(field)) {
            LOG.debug("Building Summary JRDesignBand");
            final JRDesignBand summary = new JRDesignBand();
            final Class dataClass = findDataClassFor(report);

            final JRDesignStaticText header = h3("Summary").toStaticText();
            addDesignElementTo(summary, header, 0, 0, CT_HEADER_WIDTH, PAGEHEADER_HEIGHT);

            int fieldIdx = 0;
            for (final Field dataField : dataClass.getDeclaredFields()) {
                final JRDesignStaticText headerField = h5(dataField.getName()).toStaticText();
                addDesignElementTo(summary, headerField, (CELL_WIDTH * 3 + 5) * fieldIdx, PAGEHEADER_HEIGHT, CELL_WIDTH * 2, CELL_HEIGHT);

                final JRDesignTextField textElement = normal("$F{" + dataField.getName() + "}").toTextField(dataField.getType());
                LOG.debug("Adding summary field "+ dataField.getName()+ " at ("+ (CELL_WIDTH + 5) * fieldIdx+ "+ 0");
                addDesignElementTo(summary, textElement, (CELL_WIDTH * 3 + 5) * fieldIdx, PAGEHEADER_HEIGHT + CELL_HEIGHT, CELL_WIDTH * 3, CELL_HEIGHT);
                fieldIdx++;
            }
            summary.setHeight(SUMMARY_HEIGHT);
            return summary;
        }

        return null;
    }

    /**
     * A report (the parent) is created from a normal {@link ReportInfo} instance. {@link SubReport}s do not have a {@link ReportInfo} instance.
     * Their fields are gotten instead via the fields of its data type. These are required to be passed through to the report
     * when the report is created so that they can become fields in the report.
     *
     * @param report the parent report
     * @return a {@link Collection} instance containing {@link Field} objects that will be mapped to report fields when creating the
     * report.
     */
    @Override
    public Collection<Field> getSubreportFieldsFrom(final ReportInfo report) throws Exception {
        final Collection<Field> retval = new ArrayList<Field>();
        for (final Field field : report.getClass().getDeclaredFields()) {
            if (field.getAnnotation(SubReport.class) != null) {
                retval.add(field);
            }
        }
        return retval;
    }

    /**
     * Create a subreport {@link JRDesignSubreport}. Since all kinds of reports are created through the {@link TravelReportFactoryServiceImpl} class,
     * even sub report instances will be considered normal reports and created individually. This method just marks where in the
     * parent report to place the sub report. This does not actually create another report.
     *
     * @param report is the parent report
     * @field is the field that is a {@link SubReport}
     * @return {@link JRDesignSubreport} to be added to report
     */
    protected JRDesignSubreport createSubreport(final ReportInfo report, final Field field) throws Exception {
        final JRDesignSubreport retval = new JRDesignSubreport(new JasperDesign());
        final JRDesignExpression dsExpression = new JRDesignExpression();
        final String getterName = "get" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
        dsExpression.setText("$P{report}." + getterName + "()");
        dsExpression.setValueClass(JRDataSource.class);
        retval.setDataSourceExpression(dsExpression);

        final JRDesignExpression expression = new JRDesignExpression();
        expression.setValueClass(JasperReport.class);
        expression.setText("$P{" + field.getName() + "Subreport}");
        retval.setExpression(expression);

        retval.setHeight(SUBREPORT_HEIGHT);

        return retval;
    }

    /**
     * Create a subreport for the specified field. {@link Field} instance must have {@link SubReport} annotation
     *
     * @return JasperReport as a subreport
     */
    @Override
    public JasperReport processReportForField(final ReportInfo report, final Field field) throws Exception {
        final JasperDesign design = designReport(report, field);
        if (design == null) {
            return null;
        }

        final JasperReport retval = JasperCompileManager.compileReport(design);
        retval.setWhenNoDataType(JasperReport.WHEN_NO_DATA_TYPE_ALL_SECTIONS_NO_DETAIL);

        return retval;
    }

    /**
     * Constructs a header for the {@link JRDesignGroup}
     *
     * @return {@link JRBand} instance that is your header
     */
    protected JRBand createGroupHeader(final ReportInfo report) {
        final JRDesignBand retval = new JRDesignBand();
        retval.setHeight(PAGEHEADER_HEIGHT * 4);

        final JRDesignTextField expenseType = h4("$F{expenseType}").toTextField();
        addDesignElementTo(retval, expenseType, 0, PAGEHEADER_HEIGHT, CT_HEADER_WIDTH, CELL_HEIGHT + 10);

        final JRDesignStaticText nameField = h5("Expense").toStaticText();
        addDesignElementTo(retval, nameField, (CELL_WIDTH * 3 + 5) * 0, PAGEHEADER_HEIGHT * 2 + CELL_HEIGHT, CELL_WIDTH * 3, CELL_HEIGHT);

        final JRDesignStaticText dateField = h5("Date").toStaticText();
        addDesignElementTo(retval, dateField, (CELL_WIDTH * 3 + 5) * 1, PAGEHEADER_HEIGHT * 2 + CELL_HEIGHT, CELL_WIDTH * 3, CELL_HEIGHT);

        final JRDesignStaticText amountField = h5("Amount").toStaticText();
        addDesignElementTo(retval, amountField, (CELL_WIDTH * 3 + 5) * 2, PAGEHEADER_HEIGHT * 2 + CELL_HEIGHT, CELL_WIDTH * 3, CELL_HEIGHT);

        return retval;
    }

    /**
     * Constructs a footer for the {@link JRDesignGroup}
     *
     * @return {@link JRBand} instance that is your footer
     */
    protected JRBand createGroupFooter(final ReportInfo report) {
        final JRDesignBand band = new JRDesignBand();
        band.setSplitAllowed(true);
        return band;
    }

    /**
     * Field name to group data on
     *
     * @param field with a {@link Group} annotation
     * @return String
     */
    protected String getGroupFieldFrom(final Field field) {
        final Group group = field.getAnnotation(Group.class);
        if (group == null) {
            // field isn't for a group Ahhh!!!
            throw new IllegalArgumentException(field + " is not grouped!");
        }
        return group.value();
    }

    /**
     * Factory method for creating a {@link JRDesignGroup} instance. This can really only be used on
     * reports that have at least 1 {@link Field} with a {@link Group} annotation and implements the {@link DetailedReport} interface.
     *
     * @param report instance the {@link JRDesignGroup} belongs to
     * @param field that the {@link Group} will draw data from
     * @return {@link JRDesignGroup} instance that can be set on a {@link JasperDesign}
     */
    protected JRDesignGroup createGroup(final ReportInfo report, final Field field) {
        final JRDesignGroup group = new JRDesignGroup();
        final String groupFieldName = getGroupFieldFrom(field);
        final JRDesignExpression expression = new JRDesignExpression();
        expression.setValueClass(String.class);
        expression.setText("$F{" + groupFieldName + "}");

        group.setExpression(expression);
        group.setName(groupFieldName);
        group.setGroupHeader(createGroupHeader(report));
        group.setGroupFooter(createGroupFooter(report));

        return group;
    }

    protected boolean hasGroup(final ReportInfo report) {
        return groupCount(report) > 0;
    }

    protected int groupCount(final ReportInfo report) {
        int retval = 0;
        try {
            for (final Field field : report.getClass().getDeclaredFields()) {
                if (field.getAnnotation(Group.class) != null) {
                    retval++;
                }
            }
        }
        catch (Exception e) {
            LOG.warn("Unable to get group count");
        }

        return retval;
    }

    protected int subReportCount(final JRBand design) {
        int retval = 0;
        for (final Object obj : design.getElements()) {
            if (obj instanceof JRDesignSubreport) {
                retval++;
            }
        }

        return retval;
    }

    protected int subReportCount(final ReportInfo report) {
        int retval = 0;
        try {
            for (final Field field : report.getClass().getDeclaredFields()) {
                if (field.getAnnotation(SubReport.class) != null) {
                    retval++;
                }
            }
        }
        catch (Exception e) {
            LOG.warn("Unable to get subreport count");
        }

        return retval;
    }

    protected int groupTotalHeight(final ReportInfo report) {
        return groupCount(report) + GROUP_HEIGHT;
    }

    /**
     * Overloaded version of {@link #processFields(ReportInfo, Class ...)}.
     *
     * @param report {@link ReportInfo} instance to process fields on
     * @return {@link Collection} of {@link JRDesignElement} instances that are part of the {@link ReportInfo}
     * @throws Exception because there are a lot of I/O and Reflection actions performed that can
     * potentially cause problems.
     */
    protected Collection<JRChild> processFields(final ReportInfo report) throws Exception {
        return processFields(report, new Class[] {});
    }

    /**
     * Create {@link JRDesignElement} instances used in a {@link JasperReport} based on fields in a {@link ReportInfo} instance.
     * This method tries to be smart about what fields in the {@link ReportInfo} are to be used and how they
     * are used in the input.
     *
     * @param report {@link ReportInfo} instance to process fields on
     * @param annotations is a filter on what fields by the {@link Annotation} they might have.
     * @return {@link Collection} of {@link JRDesignElement} instances that are part of the {@link ReportInfo}
     * @throws Exception because there are a lot of I/O and Reflection actions performed that can
     * potentially cause problems.
     */
    protected Collection<JRChild> processFields(final ReportInfo report, final Class ... annotations) throws Exception {
        final Collection<JRChild> retval = new ArrayList<JRChild>();

        for (final Field field : report.getClass().getDeclaredFields()) {
            boolean valid = true;
            valid &= hasAnnotations(field, annotations) && !hasAnnotations(field, Summary.class, SubReport.class);

            if (valid) {
                retval.add(createElementForField(report, field));
            }
        }

        return retval;
    }

    /**
     * Overloaded convenience method for creating {@link JRDesignCrosstabCell} instances. This instance assumes there is no column group.
     *
     * @param type is a {@link Class} is used to determine how to render the {@link JRDesignTextField} contained
     * in the {@link JRDesignCrosstabCell}
     * @param value is the {@link String} value to render in the {@link JRDesignCrosstabCell}
     * @param rowGroup is the name of the rowgroup this cell is part of.
     */
    protected JRDesignCrosstabCell crosstabCell(final Class type, final String value, final String rowGroup) {
        return crosstabCell(type, value, rowGroup, null);
    }

    /**
     * Convenience method for creating {@link JRDesignCrosstabCell} instances. It is possible to be part of a row group and a column group.
     *
     * @param type is a {@link Class} is used to determine how to render the {@link JRDesignTextField} contained
     * in the {@link JRDesignCrosstabCell}
     * @param value is the {@link String} value to render in the {@link JRDesignCrosstabCell}
     * @param rowGroup is the name of the rowgroup this cell is part of.
     * @param colGroup is the name of the column group this cell is part of.
     */
    protected JRDesignCrosstabCell crosstabCell(final Class type, final String value, final String rowGroup, final String colGroup) {
        LOG.debug("Creating cell with row group = "+ rowGroup+ " and column group = "+ colGroup);
        final JRDesignCrosstabCell retval = new JRDesignCrosstabCell();
        final JRDesignCellContents contents = new JRDesignCellContents();
        final JRDesignTextField field = normal(value).toTextField(type);
        addDesignElementTo(contents, field, 0, 0, CELL_WIDTH, CELL_HEIGHT);
        contents.setMode(MODE_OPAQUE);
        contents.setBackcolor(Color.LIGHT_GRAY);

        final JRDesignFrame frame = new JRDesignFrame();
        frame.copyBox(new TravelReportLineBox(contents));
        contents.setBox(frame);
        retval.setWidth(CELL_WIDTH);
        retval.setHeight(CELL_HEIGHT);
        retval.setContents(contents);
        retval.setRowTotalGroup(rowGroup);
        retval.setColumnTotalGroup(colGroup);
        return retval;
    }

    /**
     * Overloaded convenience method for creating {@link JRDesignCrosstabCell} instances. This case assumes there is no row or column group
     *
     * @param type is a {@link Class} is used to determine how to render the {@link JRDesignTextField} contained
     * in the {@link JRDesignCrosstabCell}
     * @param value is the {@link String} value to render in the {@link JRDesignCrosstabCell}
     */
    protected JRDesignCrosstabCell crosstabCell(final Class type, final String value) {
        return crosstabCell(type, value, null, null);
    }

    /**
     * Creates a crosstab. Intended for use withing a summary. Creates a crosstab and a custom dataset for the
     * crosstab to use in a {@link Summary}
     */
    protected JRDesignCrosstab createCrosstab(final ReportInfo report, final Field field) throws Exception {
        final JRDesignCrosstab crosstab = new JRDesignCrosstab();
        LOG.debug("<crosstab>");
        LOG.debug("<reportElement width=\"400\" height=\"" + (SUMMARY_HEIGHT - 25) + "\" />");
        crosstab.setWidth(595);
        crosstab.setHeight(0);

        final JRDesignCellContents nodataCell = new JRDesignCellContents();
        nodataCell.setBackcolor(Color.LIGHT_GRAY);
        nodataCell.setMode(MODE_OPAQUE);

        final JRDesignFrame frame = new JRDesignFrame();
        frame.copyBox(new TravelReportLineBox(nodataCell));
        nodataCell.setBox(frame);

        LOG.debug("<crosstabHeaderCell/>");
        crosstab.setHeaderCell(nodataCell);

        final JRDesignCrosstabRowGroup rowGroup = new JRDesignCrosstabRowGroup();
        final JRDesignCellContents rowHeader = new JRDesignCellContents();
        final JRDesignCellContents rowTotalHeader = new JRDesignCellContents();
        rowHeader.setMode(MODE_OPAQUE);
        rowHeader.setBackcolor(Color.LIGHT_GRAY);

        final JRDesignFrame rowFrame = new JRDesignFrame();
        rowFrame.copyBox(new TravelReportLineBox(rowHeader));
        rowHeader.setBox(rowFrame);

        final JRDesignStaticText rowTotalText = h3("Daily Total").toStaticText();
        addDesignElementTo(rowTotalHeader, rowTotalText, 0, 0, CELL_WIDTH, CELL_HEIGHT);

        final JRDesignTextField rowHeaderField = normal("$V{Expenses}").toTextField();
        addDesignElementTo(rowHeader, rowHeaderField, 0, 0, CT_HEADER_WIDTH, CELL_HEIGHT);
        rowGroup.setName("Expenses");
        rowGroup.setWidth(CT_HEADER_WIDTH);
        rowGroup.setHeader(rowHeader);
        rowGroup.setTotalHeader(rowTotalHeader);
        rowGroup.setBucket(bucket("$F{name}", String.class));

        final JRDesignCrosstabColumnGroup columnGroup = new JRDesignCrosstabColumnGroup();
        final JRDesignCellContents columnHeader = new JRDesignCellContents();
        final JRDesignCellContents columnTotalHeader = new JRDesignCellContents();
        columnHeader.setMode(MODE_OPAQUE);
        columnHeader.setBackcolor(Color.LIGHT_GRAY);

        final JRDesignFrame columnFrame = new JRDesignFrame();
        columnFrame.copyBox(new TravelReportLineBox(columnHeader));
        columnHeader.setBox(columnFrame);

        final JRDesignStaticText columnTotalText = h3("Expense Totals").toStaticText();
        addDesignElementTo(columnTotalHeader, columnTotalText, 0, 0, CELL_WIDTH, CELL_HEIGHT);

        final JRDesignTextField columnHeaderField = normal("$V{Days}").toTextField();
        addDesignElementTo(columnHeader, columnHeaderField, 0, 0, CELL_WIDTH, CELL_HEIGHT);
        columnGroup.setName("Days");
        columnGroup.setHeight(CELL_HEIGHT);
        columnGroup.setHeader(columnHeader);
        columnGroup.setTotalHeader(columnTotalHeader);
        columnGroup.setBucket(bucket("$F{date}", java.lang.String.class));

        LOG.debug("<rowGroup name=\"Expenses\" width=\"400\">");
        crosstab.addRowGroup(rowGroup);
        crosstab.addColumnGroup(columnGroup);

        crosstab.addMeasure(measure("amount"));

        crosstab.addCell(crosstabCell(java.math.BigDecimal.class, "$V{amountMeasure}"));
        crosstab.addCell(crosstabCell(java.math.BigDecimal.class, "$V{amountMeasure}", "Expenses"));
        crosstab.addCell(crosstabCell(java.math.BigDecimal.class, "$V{amountMeasure}", null, "Days"));
        crosstab.addCell(crosstabCell(java.math.BigDecimal.class, "$V{amountMeasure}", "Expenses", "Days"));

        return crosstab;
    }

    /**
     * Creates a crosstab. Intended for a subreport because this assumes the main dataset.
     * @return a {@link JRDesignCrosstab} instance
     */
    protected JRDesignCrosstab createCrosstab() throws Exception {
        final JRDesignCrosstab crosstab = new JRDesignCrosstab();
        crosstab.setRunDirection(RUN_DIRECTION_LTR);
        crosstab.setWidth(595);
        crosstab.setHeight(0);

        final JRDesignCellContents nodataCell = new JRDesignCellContents();
        nodataCell.setBackcolor(Color.LIGHT_GRAY);
        nodataCell.setMode(MODE_OPAQUE);

        final JRDesignFrame frame = new JRDesignFrame();
        frame.copyBox(new TravelReportLineBox(nodataCell));
        nodataCell.setBox(frame);

        final JRDesignCrosstabRowGroup rowGroup = new JRDesignCrosstabRowGroup();
        final JRDesignCellContents rowHeader = new JRDesignCellContents();
        final JRDesignCellContents rowTotalHeader = new JRDesignCellContents();
        rowHeader.setMode(MODE_OPAQUE);
        rowHeader.setBackcolor(Color.LIGHT_GRAY);

        final JRDesignFrame rowFrame = new JRDesignFrame();
        rowFrame.copyBox(new TravelReportLineBox(rowHeader));
        rowHeader.setBox(rowFrame);

        final JRDesignStaticText rowTotalText = h3("Daily Total").toStaticText();
        addDesignElementTo(rowTotalHeader, rowTotalText, 0, 0, CELL_WIDTH, CELL_HEIGHT);

        final JRDesignTextField rowHeaderField = normal("$V{Expenses}").toTextField();
        addDesignElementTo(rowHeader, rowHeaderField, 0, 0, CT_HEADER_WIDTH, CELL_HEIGHT);
        rowGroup.setPosition(POSITION_X_LEFT);
        rowGroup.setName("Expenses");
        rowGroup.setWidth(CT_HEADER_WIDTH);
        rowGroup.setHeader(rowHeader);
        rowGroup.setTotalHeader(rowTotalHeader);
        rowGroup.setBucket(bucket("$F{name}", String.class));

        final JRDesignCrosstabColumnGroup columnGroup = new JRDesignCrosstabColumnGroup();
        final JRDesignCellContents columnHeader = new JRDesignCellContents();
        final JRDesignCellContents columnTotalHeader = new JRDesignCellContents();
        columnHeader.setMode(MODE_OPAQUE);
        columnHeader.setBackcolor(Color.LIGHT_GRAY);

        final JRDesignFrame columnFrame = new JRDesignFrame();
        columnFrame.copyBox(new TravelReportLineBox(columnHeader));

        final JRDesignStaticText columnTotalText = h3("Expense Totals").toStaticText();
        addDesignElementTo(columnTotalHeader, columnTotalText, 0, 0, CELL_WIDTH, CELL_HEIGHT);

        final JRDesignTextField columnHeaderField = normal("$V{Days}").toTextField();
        addDesignElementTo(columnHeader, columnHeaderField, 0, 0, CELL_WIDTH, CELL_HEIGHT);
        columnGroup.setPosition(POSITION_Y_TOP);
        columnGroup.setName("Days");
        columnGroup.setHeight(CELL_HEIGHT);
        columnGroup.setHeader(columnHeader);
        columnGroup.setTotalHeader(columnTotalHeader);
        columnGroup.setBucket(bucket("$F{date}", java.lang.String.class));

        crosstab.addRowGroup(rowGroup);
        crosstab.addColumnGroup(columnGroup);

        crosstab.addMeasure(measure("amount"));

        crosstab.addCell(crosstabCell(java.math.BigDecimal.class, "$V{amountMeasure}"));
        crosstab.addCell(crosstabCell(java.math.BigDecimal.class, "$V{amountMeasure}", "Expenses"));
        crosstab.addCell(crosstabCell(java.math.BigDecimal.class, "$V{amountMeasure}", null, "Days"));
        crosstab.addCell(crosstabCell(java.math.BigDecimal.class, "$V{amountMeasure}", "Expenses", "Days"));

        return crosstab;
    }

    /**
     * Tells us if a given {@link ReportInfo} instance defines any crosstabs in its
     * class definition
     *
     * @param report is a {@link ReportInfo} instance
     */
    protected boolean hasCrosstabs(final ReportInfo report) {
        for (final Field field : report.getClass().getDeclaredFields()) {
            if (isCrosstab(field)) {
                return true;
            }
        }
        return false;
    }

    protected boolean hasFieldWithAnnotations(final ReportInfo report, final Class ... annotations) {
        return hasFieldWithAnnotations(report.getClass(), annotations);
    }

    protected boolean hasFieldWithAnnotations(final Class reportClass, final Class ... annotations) {
        for (final Field field : reportClass.getDeclaredFields()) {
            if (hasAnnotations(field, annotations)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Field getFieldWithAnnotation(final ReportInfo report, final Class ... annotations) {
        return getFieldsWithAnnotation(report.getClass(), annotations).iterator().next();
    }

    protected Field getFieldWithAnnotation(final Class searchClass, final Class ... annotations) {
        return getFieldsWithAnnotation(searchClass, annotations).iterator().next();
    }

    protected Collection<Field> getFieldsWithAnnotation(final Class searchClass, final Class ... annotations) {
        final Collection<Field> retval = new ArrayList<Field>();
        for (final Field field : searchClass.getDeclaredFields()) {
            if (hasAnnotations(field, annotations)) {
                retval.add(field);
            }
        }
        return retval;
    }

    protected boolean hasAnnotations(final Field field, final Class ... annotations) {
        for (final Class annotation : annotations) {
            LOG.debug("Checking if field "+ field.getName()+ " has annotation "+ annotation.getSimpleName());
            if (field.getAnnotation(annotation) == null) {
                return false;
            }
        }

        return true;
    }

    /**
     * Determine whether a {@link Field} in the {@link ReportInfo} instance is a report {@link Subreport} or not.
     *
     * @param field a {@link Field} instance in a {@link ReportInfo} class
     * @param true if the {@link Subreport} annotation is on a {@link Field}
     */
    protected boolean isSubreport(final Field field) {
        return field.getAnnotation(SubReport.class) != null;
    }

    /**
     * Determine whether a {@link Field} in the {@link ReportInfo} instance is a report {@link Summary} or not.
     *
     * @param field a {@link Field} instance in a {@link ReportInfo} class
     * @param true if the {@link Summary} annotation is on a {@link Field}
     */
    @Override
    public boolean isSummary(final Field field) {
        return field.getAnnotation(Summary.class) != null;
    }

    /**
     * Determine whether a {@link Field} in the {@link ReportInfo} instance is a report {@link Crosstab} or not.
     *
     * @param field a {@link Field} instance in a {@link ReportInfo} class
     * @param true if the {@link Crosstab} annotation is on a {@link Field}
     */
    protected boolean isCrosstab(final Field field) {
        for (final Annotation annotation : field.getDeclaredAnnotations()) {
            if (annotation instanceof Crosstab) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determine whether a {@link Field} in the {@link ReportInfo} instance is a report {@link Group} or not.
     *
     * @param field a {@link Field} instance in a {@link ReportInfo} class
     * @param true if the {@link Group} annotation is on a {@link Field}
     */
    protected boolean isGroup(final Field field) {
        for (final Annotation annotation : field.getDeclaredAnnotations()) {
            if (annotation instanceof Group) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determine whether a {@link Field} in the {@link ReportInfo} instance is a report {@link Parameter} or not.
     *
     * @param field a {@link Field} instance in a {@link ReportInfo} class
     * @param true if the {@link Parameter} annotation is on a {@link Field}
     */
    protected boolean isParameter(final Field field) {
        for (final Annotation annotation : field.getDeclaredAnnotations()) {
            if (annotation instanceof Parameter) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets the styles property.
     * @return Returns the styles.
     */
    public Map<String,RTextStyle> getStyles() {
        return styles;
    }

    /**
     * Sets the styles property value.
     * @param styles The styles to set.
     */
    public void setStyles(final Map<String,RTextStyle> styles) {
        this.styles = styles;
    }

    private static class TravelReportLineBox extends JRBaseLineBox {
        protected TravelReportLineBox(JRBoxContainer container) {
            super(container);

            final JRBaseBoxPen pen = new JRBaseBoxPen(this) {
                    @Override
                    public Color getLineColor() {
                        return BLACK;
                    }

                    @Override
                    public Byte getLineStyle() {
                        return LINE_STYLE_SOLID;
                    }

                    @Override
                    public Float getLineWidth() {
                        return 0.5f;
                    }
                };

            this.pen = pen;
        }
    }
}