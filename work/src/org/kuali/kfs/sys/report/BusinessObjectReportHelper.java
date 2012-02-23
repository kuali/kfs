/*
 * Copyright 2009 The Kuali Foundation
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
package org.kuali.kfs.sys.report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.core.web.format.BigDecimalFormatter;
import org.kuali.rice.core.web.format.CurrencyFormatter;
import org.kuali.rice.core.web.format.Formatter;
import org.kuali.rice.core.web.format.IntegerFormatter;
import org.kuali.rice.core.web.format.KualiIntegerCurrencyFormatter;
import org.kuali.rice.core.web.format.LongFormatter;
import org.kuali.rice.core.web.format.PercentageFormatter;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Helper class for business objects to assist formatting them for error reporting. Utilizes spring injection for modularization and
 * configurability
 * 
 * @see org.kuali.kfs.sys.service.impl.ReportWriterTextServiceImpl
 */
public class BusinessObjectReportHelper {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BusinessObjectReportHelper.class);

    protected int minimumMessageLength;
    protected String messageLabel;
    protected Class<? extends BusinessObject> dataDictionaryBusinessObjectClass;
    protected Map<String, String> orderedPropertyNameToHeaderLabelMap;
    protected DataDictionaryService dataDictionaryService;

    private int columnCount = 0;
    private Map<String, Integer> columnSpanDefinition;
    
    public final static String LEFT_ALIGNMENT = "LEFT"; 
    public final static String RIGHT_ALIGNMENT = "RIGHT"; 
    public final static String LINE_BREAK = "\n";

    /**
     * Returns the values in a list of the passed in business object in order of the spring definition.
     * 
     * @param businessObject for which to return the values
     * @return the values
     */
    public List<Object> getValues(BusinessObject businessObject) {
        List<Object> keys = new ArrayList<Object>();

        for (Iterator<String> propertyNames = orderedPropertyNameToHeaderLabelMap.keySet().iterator(); propertyNames.hasNext();) {
            String propertyName = propertyNames.next();
            keys.add(retrievePropertyValue(businessObject, propertyName));
        }

        return keys;
    }

    /**
     * Returns a value for a given property, can be overridden to allow for pseudo-properties
     * 
     * @param businessObject
     * @param propertyName
     * @return
     */
    protected Object retrievePropertyValue(BusinessObject businessObject, String propertyName) {
        try {
            return PropertyUtils.getProperty(businessObject, propertyName);
        }
        catch (Exception e) {
            throw new RuntimeException("Failed getting propertyName=" + propertyName + " from businessObjecName=" + businessObject.getClass().getName(), e);
        }
    }

    /**
     * Returns the maximum length of a value for a given propery, can be overridden to allow for pseudo-properties
     * 
     * @param businessObjectClass
     * @param propertyName
     * @return
     */
    protected int retrievePropertyValueMaximumLength(Class<? extends BusinessObject> businessObjectClass, String propertyName) {
        return dataDictionaryService.getAttributeMaxLength(businessObjectClass, propertyName);
    }
    
    /**
     * Returns the maximum length of a value for a given propery, can be overridden to allow for pseudo-properties
     * 
     * @param businessObjectClass
     * @param propertyName
     * @return
     */
    protected Class<? extends Formatter> retrievePropertyFormatterClass(Class<? extends BusinessObject> businessObjectClass, String propertyName) {
        return dataDictionaryService.getAttributeFormatter(businessObjectClass, propertyName);
    }

    /**
     * Same as getValues except that it actually doesn't retrieve the values from the BO but instead returns a blank linke. This is
     * useful if indentation for message printing is necessary.
     * 
     * @param businessObject for which to return the values
     * @return spaces in the length of values
     */
    public List<Object> getBlankValues(BusinessObject businessObject) {
        List<Object> keys = new ArrayList<Object>();

        for (Iterator<String> propertyNames = orderedPropertyNameToHeaderLabelMap.keySet().iterator(); propertyNames.hasNext();) {
            String propertyName = propertyNames.next();

            keys.add("");
        }

        return keys;
    }

    /**
     * Returns multiple lines of what represent a table header. The last line in this list is the format of the table cells.
     * 
     * @param maximumPageWidth maximum before line is out of bounds. Used to fill message to the end of this range. Note that if
     *        there isn't at least maximumPageWidth characters available it will go minimumMessageLength out of bounds. It is up to
     *        the calling class to handle that
     * @return table header. Last element is the format of the table cells.
     */
    public List<String> getTableHeader(int maximumPageWidth) {
        String separatorLine = StringUtils.EMPTY;
        String messageFormat = StringUtils.EMPTY;

        // Construct the header based on orderedPropertyNameToHeaderLabelMap. It will pick the longest of label or DD size
        for (Iterator<Map.Entry<String, String>> entries = orderedPropertyNameToHeaderLabelMap.entrySet().iterator(); entries.hasNext();) {
            Map.Entry<String, String> entry = entries.next();

            int longest;
            try {
                longest = retrievePropertyValueMaximumLength(dataDictionaryBusinessObjectClass, entry.getKey());
            }
            catch (Exception e) {
                throw new RuntimeException("Failed getting propertyName=" + entry.getKey() + " from businessObjecName=" + dataDictionaryBusinessObjectClass.getName(), e);
            }
            if (entry.getValue().length() > longest) {
                longest = entry.getValue().length();
            }

            separatorLine = separatorLine + StringUtils.rightPad("", longest, KFSConstants.DASH) + " ";
            messageFormat = messageFormat + "%-" + longest + "s ";
        }

        // Now fill to the end of pageWidth for the message column. If there is not enough space go out of bounds
        int availableWidth = maximumPageWidth - (separatorLine.length() + 1);
        if (availableWidth < minimumMessageLength) {
            availableWidth = minimumMessageLength;
        }
        separatorLine = separatorLine + StringUtils.rightPad("", availableWidth, KFSConstants.DASH);
        messageFormat = messageFormat + "%-" + availableWidth + "s";

        // Fill in the header labels. We use the errorFormat to do this to get justification right
        List<Object> formatterArgs = new ArrayList<Object>();
        formatterArgs.addAll(orderedPropertyNameToHeaderLabelMap.values());
        formatterArgs.add(messageLabel);
        String tableHeaderLine = String.format(messageFormat, formatterArgs.toArray());

        // Construct return list
        List<String> tableHeader = new ArrayList<String>();
        tableHeader.add(tableHeaderLine);
        tableHeader.add(separatorLine);
        tableHeader.add(messageFormat);

        return tableHeader;
    }

    /**
     * get the primary information that can define a table structure
     * 
     * @return the primary information that can define a table structure
     */
    public Map<String, String> getTableDefinition() {       
        List<Integer> cellWidthList = this.getTableCellWidth();
        
        String separatorLine = this.getSepartorLine(cellWidthList);       
        String tableCellFormat = this.getTableCellFormat(false, true, null);
        String tableHeaderLineFormat = this.getTableCellFormat(false, false, separatorLine);

        // fill in the header labels
        int numberOfCell = cellWidthList.size();
        List<String> tableHeaderLabelValues = new ArrayList<String>(orderedPropertyNameToHeaderLabelMap.values());
        this.paddingTableCellValues(numberOfCell, tableHeaderLabelValues);

        String tableHeaderLine = String.format(tableHeaderLineFormat, tableHeaderLabelValues.toArray());

        Map<String, String> tableDefinition = new HashMap<String, String>();
        tableDefinition.put(KFSConstants.ReportConstants.TABLE_HEADER_LINE_KEY, tableHeaderLine);
        tableDefinition.put(KFSConstants.ReportConstants.SEPARATOR_LINE_KEY, separatorLine);
        tableDefinition.put(KFSConstants.ReportConstants.TABLE_CELL_FORMAT_KEY, tableCellFormat);

        return tableDefinition;
    }

    /**
     * Returns the values in a list of the passed in business object in order of the spring definition. The value for the
     * "EMPTY_CELL" entry is an empty string.
     * 
     * @param businessObject for which to return the values
     * @param allowColspan indicate whether colspan definition can be applied
     * @return the values being put into the table cells
     */
    public List<String> getTableCellValues(BusinessObject businessObject, boolean allowColspan) {
        List<String> tableCellValues = new ArrayList<String>();

        for (Map.Entry<String, String> entry : orderedPropertyNameToHeaderLabelMap.entrySet()) {
            String attributeName = entry.getKey();

            if (attributeName.startsWith(KFSConstants.ReportConstants.EMPTY_CELL_ENTRY_KEY_PREFIX)) {
                tableCellValues.add(StringUtils.EMPTY);
            }
            else {
                try {
                    Object propertyValue = retrievePropertyValue(businessObject, attributeName);
                    
                    if (ObjectUtils.isNotNull(propertyValue)) {
                        Formatter formatter = Formatter.getFormatter(propertyValue.getClass());
                        if(ObjectUtils.isNotNull(formatter) && ObjectUtils.isNotNull(propertyValue)) {
                            propertyValue = formatter.format(propertyValue);
                        }
                        else {
                            propertyValue = StringUtils.EMPTY;
                        }
                    } else {
                        propertyValue = StringUtils.EMPTY;
                    }
                    
                    tableCellValues.add(propertyValue.toString());
                }
                catch (Exception e) {
                    throw new RuntimeException("Failed getting propertyName=" + entry.getKey() + " from businessObjecName=" + dataDictionaryBusinessObjectClass.getName(), e);
                }
            }
        }
        
        if(allowColspan) {
            this.applyColspanOnCellValues(tableCellValues);
        }

        return tableCellValues;
    }

    /**
     * get the format string for all cells in a table row. Colspan definition will be applied if allowColspan is true 
     * 
     * @param allowColspan indicate whether colspan definition can be applied
     * @param allowRightAlignment indicate whether the right alignment can be applied
     * @param separatorLine the separation line for better look
     * 
     * @return the format string for all cells in a table row
     */
    public String getTableCellFormat(boolean allowColspan, boolean allowRightAlignment, String separatorLine) {
        List<Integer> cellWidthList = this.getTableCellWidth();
        List<String> cellAlignmentList = this.getTableCellAlignment();
        
        if(allowColspan) {
            this.applyColspanOnCellWidth(cellWidthList);
        }

        int numberOfCell = cellWidthList.size();
        int rowCount = (int) Math.ceil(numberOfCell * 1.0 / columnCount);

        StringBuffer tableCellFormat = new StringBuffer();
        for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
            StringBuffer singleRowFormat = new StringBuffer();
            
            for (int columnIndex = 0; columnIndex < this.columnCount; columnIndex++) {
                int index = columnCount * rowIndex + columnIndex; 
                
                if(index >= numberOfCell) {
                    break;
                }
                
                int width = cellWidthList.get(index);
                String alignment = (allowRightAlignment && cellAlignmentList.get(index).equals(RIGHT_ALIGNMENT)) ? StringUtils.EMPTY : "-";
                if(width > 0) {
                    // following translates to %<alignment><width>.<precision>s where the precision for Strings forces a maxLength
                    singleRowFormat = singleRowFormat.append("%").append(alignment).append(width).append("." + width).append("s ");
                }
            }
            
            tableCellFormat = tableCellFormat.append(singleRowFormat).append(LINE_BREAK);
            if(StringUtils.isNotBlank(separatorLine)) {
                tableCellFormat = tableCellFormat.append(separatorLine).append(LINE_BREAK);
            }
        }

        return tableCellFormat.toString();
    }
    
    /**
     * get the separator line
     * @param cellWidthList the given cell width list
     * @return the separator line
     */
    public String getSepartorLine(List<Integer> cellWidthList) {
        StringBuffer separatorLine = new StringBuffer();
        
        for (int index = 0; index < this.columnCount; index++) {
            Integer cellWidth = cellWidthList.get(index);
            separatorLine = separatorLine.append(StringUtils.rightPad(StringUtils.EMPTY, cellWidth, KFSConstants.DASH)).append(" ");
        }
        
        return separatorLine.toString();
    }

    /**
     * apply the colspan definition on the default width of the table cells
     * 
     * @param the default width of the table cells
     */
    public void applyColspanOnCellWidth(List<Integer> cellWidthList) {
        if(ObjectUtils.isNull(columnSpanDefinition)) {
            return;
        }
        
        int indexOfCurrentCell = 0;
        for (Map.Entry<String, String> entry : orderedPropertyNameToHeaderLabelMap.entrySet()) {
            String attributeName = entry.getKey();

            if (columnSpanDefinition.containsKey(attributeName)) {
                int columnSpan = columnSpanDefinition.get(attributeName);

                int widthOfCurrentNonEmptyCell = cellWidthList.get(indexOfCurrentCell);
                for (int i = 1; i < columnSpan; i++) {
                    widthOfCurrentNonEmptyCell += cellWidthList.get(indexOfCurrentCell + i);
                    cellWidthList.set(indexOfCurrentCell + i, 0);
                }
                cellWidthList.set(indexOfCurrentCell, widthOfCurrentNonEmptyCell + columnSpan - 1);
            }

            indexOfCurrentCell++;
        }
    }
    
    /**
     * apply the colspan definition on the default values of the table cells. The values will be removed if their positions are taken by others.
     * 
     * @param the default values of the table cells
     */
    public void applyColspanOnCellValues(List<String> cellValues) {
        if(ObjectUtils.isNull(columnSpanDefinition)) {
            return;
        }
        
        String REMOVE_ME = "REMOVE-ME-!";
        
        int indexOfCurrentCell = 0;
        for (Map.Entry<String, String> entry : orderedPropertyNameToHeaderLabelMap.entrySet()) {
            String attributeName = entry.getKey();

            if (columnSpanDefinition.containsKey(attributeName)) {
                int columnSpan = columnSpanDefinition.get(attributeName);

                for (int i = 1; i < columnSpan; i++) {
                    cellValues.set(indexOfCurrentCell + i, REMOVE_ME);
                }
            }

            indexOfCurrentCell++;
        }
        
        int originalLength = cellValues.size();
        for(int index = originalLength -1; index>=0; index-- ) {
            if(StringUtils.equals(cellValues.get(index), REMOVE_ME)) {
                cellValues.remove(index);
            }
        }
    }

    /**
     * get the values that can be fed into a predefined table. If the values are not enought to occupy the table cells, a number of empty values are provided.
     * 
     * @param businessObject the given business object whose property values will be collected 
     * @param allowColspan indicate whether colspan definition can be applied
     * @return
     */
    public List<String> getTableCellValuesPaddingWithEmptyCell(BusinessObject businessObject, boolean allowColspan) {
        List<String> tableCellValues = this.getTableCellValues(businessObject, allowColspan);

        int numberOfCell = orderedPropertyNameToHeaderLabelMap.entrySet().size();
        this.paddingTableCellValues(numberOfCell, tableCellValues);

        return tableCellValues;
    }

    /**
     * get the width of all table cells according to the definition
     * 
     * @return the width of all table cells. The width is in the order defined as the orderedPropertyNameToHeaderLabelMap
     */
    public List<Integer> getTableCellWidth() {
        List<Integer> cellWidthList = new ArrayList<Integer>();
        for (Map.Entry<String, String> entry : orderedPropertyNameToHeaderLabelMap.entrySet()) {
            String attributeName = entry.getKey();
            String attributeValue = entry.getValue();

            int cellWidth = attributeValue.length();
            if (!attributeName.startsWith(KFSConstants.ReportConstants.EMPTY_CELL_ENTRY_KEY_PREFIX)) {
                try {
                    cellWidth = retrievePropertyValueMaximumLength(dataDictionaryBusinessObjectClass, attributeName);
                }
                catch (Exception e) {
                    throw new RuntimeException("Failed getting propertyName=" + attributeName + " from businessObjecName=" + dataDictionaryBusinessObjectClass.getName(), e);
                }
            }

            if (attributeValue.length() > cellWidth) {
                cellWidth = attributeValue.length();
            }

            cellWidthList.add(cellWidth);
        }

        int numberOfCell = cellWidthList.size();
        int rowCount = (int) Math.ceil(numberOfCell * 1.0 / columnCount);
        for (int colIndex = 0; colIndex < columnCount; colIndex++) {
            int longestLength = cellWidthList.get(colIndex);

            for (int rowIndex = 1; rowIndex < rowCount; rowIndex++) {
                int currentIndex = rowIndex * columnCount + colIndex;
                if (currentIndex >= numberOfCell) {
                    break;
                }

                int currentLength = cellWidthList.get(currentIndex);
                if (currentLength > longestLength) {
                    cellWidthList.set(colIndex, currentLength);
                }
            }
        }

        for (int colIndex = 0; colIndex < columnCount; colIndex++) {
            int longestLength = cellWidthList.get(colIndex);

            for (int rowIndex = 1; rowIndex < rowCount; rowIndex++) {
                int currentIndex = rowIndex * columnCount + colIndex;
                if (currentIndex >= numberOfCell) {
                    break;
                }

                cellWidthList.set(currentIndex, longestLength);
            }
        }

        return cellWidthList;
    }
    
    /**
     * get the alignment definitions of all table cells in one row according to the property's formatter class
     * 
     * @return the alignment definitions of all table cells in one row according to the property's formatter class
     */
    public List<String> getTableCellAlignment() {
        List<String> cellWidthList = new ArrayList<String>();
        List<Class<? extends Formatter>> numberFormatters = this.getNumberFormatters();
        
        for (Map.Entry<String, String> entry : orderedPropertyNameToHeaderLabelMap.entrySet()) {
            String attributeName = entry.getKey();
            
            boolean isNumber = false;
            if (!attributeName.startsWith(KFSConstants.ReportConstants.EMPTY_CELL_ENTRY_KEY_PREFIX)) {
                try {
                    Class<? extends Formatter> formatterClass = this.retrievePropertyFormatterClass(dataDictionaryBusinessObjectClass, attributeName);
                    
                    isNumber = numberFormatters.contains(formatterClass);
                }
                catch (Exception e) {
                    throw new RuntimeException("Failed getting propertyName=" + attributeName + " from businessObjecName=" + dataDictionaryBusinessObjectClass.getName(), e);
                }
            }

            cellWidthList.add(isNumber ? RIGHT_ALIGNMENT : LEFT_ALIGNMENT);
        }
        
        return cellWidthList;
    }

    // put empty strings into the table cell values if the values are not enough to feed the table
    protected void paddingTableCellValues(int numberOfCell, List<String> tableCellValues) {
        int reminder = columnCount - numberOfCell % columnCount;
        if (reminder < columnCount) {
            List<String> paddingObject = new ArrayList<String>(reminder);
            for (int index = 0; index < reminder; index++) {
                paddingObject.add(StringUtils.EMPTY);
            }

            tableCellValues.addAll(paddingObject);
        }
    }
    
    /**
     * get formatter classes defined for numbers
     * 
     * @return the formatter classes defined for numbers
     */
    protected List<Class<? extends Formatter>> getNumberFormatters(){
        List<Class<? extends Formatter>> numberFormatters = new ArrayList<Class<? extends Formatter>>();
        
        numberFormatters.add(BigDecimalFormatter.class);
        numberFormatters.add(CurrencyFormatter.class); 
        numberFormatters.add(KualiIntegerCurrencyFormatter.class);
        numberFormatters.add(PercentageFormatter.class);
        numberFormatters.add(IntegerFormatter.class);
        numberFormatters.add(LongFormatter.class);
        
        return numberFormatters;
    }

    /**
     * Sets the minimumMessageLength
     * 
     * @param minimumMessageLength The minimumMessageLength to set.
     */
    public void setMinimumMessageLength(int minimumMessageLength) {
        this.minimumMessageLength = minimumMessageLength;
    }

    /**
     * Sets the messageLabel
     * 
     * @param messageLabel The messageLabel to set.
     */
    public void setMessageLabel(String messageLabel) {
        this.messageLabel = messageLabel;
    }

    /**
     * Sets the dataDictionaryBusinessObjectClass
     * 
     * @param dataDictionaryBusinessObjectClass The dataDictionaryBusinessObjectClass to set.
     */
    public void setDataDictionaryBusinessObjectClass(Class<? extends BusinessObject> dataDictionaryBusinessObjectClass) {
        this.dataDictionaryBusinessObjectClass = dataDictionaryBusinessObjectClass;
    }

    /**
     * Sets the orderedPropertyNameToHeaderLabelMap
     * 
     * @param orderedPropertyNameToHeaderLabelMap The orderedPropertyNameToHeaderLabelMap to set.
     */
    public void setOrderedPropertyNameToHeaderLabelMap(Map<String, String> orderedPropertyNameToHeaderLabelMap) {
        this.orderedPropertyNameToHeaderLabelMap = orderedPropertyNameToHeaderLabelMap;
    }

    /**
     * Sets the dataDictionaryService
     * 
     * @param dataDictionaryService The dataDictionaryService to set.
     */
    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

    /**
     * Sets the columnCount attribute value.
     * 
     * @param columnCount The columnCount to set.
     */
    public void setColumnCount(int columnCount) {
        this.columnCount = columnCount;
    }

    /**
     * Sets the columnSpanDefinition attribute value.
     * 
     * @param columnSpanDefinition The columnSpanDefinition to set.
     */
    public void setColumnSpanDefinition(Map<String, Integer> columnSpanDefinition) {
        this.columnSpanDefinition = columnSpanDefinition;
    }
}
