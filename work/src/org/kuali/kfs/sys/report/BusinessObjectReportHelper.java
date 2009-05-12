/*
 * Copyright 2009 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
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
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.util.ObjectUtils;

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
        return dataDictionaryService.getAttributeMaxLength(dataDictionaryBusinessObjectClass, propertyName);
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
    public Map<String, String> getTableDefintion() {
        String tableHeaderLineFormat = StringUtils.EMPTY;
        String tableCellFormat = StringUtils.EMPTY;
        String separatorLine = StringUtils.EMPTY;
        String singleRowFormat = StringUtils.EMPTY;

        // build the formatter for a single row
        List<Integer> cellWidthList = this.getTableCellWidth();
        for (int index = 0; index < this.columnCount; index++) {
            Integer cellWidth = cellWidthList.get(index);

            separatorLine = separatorLine + StringUtils.rightPad(StringUtils.EMPTY, cellWidth, KFSConstants.DASH) + " ";
            singleRowFormat = singleRowFormat + "%-" + cellWidth + "s ";
        }

        // build the formatters for mutiple rows
        int numberOfCell = cellWidthList.size();
        int rowCount = (int) Math.ceil(numberOfCell * 1.0 / columnCount);
        for (int index = 0; index < rowCount; index++) {
            tableHeaderLineFormat = tableHeaderLineFormat + singleRowFormat + "\n" + separatorLine + "\n";

            tableCellFormat = tableCellFormat + singleRowFormat + "\n";
        }

        // fill in the header labels
        List<String> tableHeaderLabelValues = new ArrayList<String>(orderedPropertyNameToHeaderLabelMap.values());
        this.paddingTableCellValues(numberOfCell, tableHeaderLabelValues);

        String tableHeaderLine = String.format(tableHeaderLineFormat, tableHeaderLabelValues.toArray());

        Map<String, String> tableDefintion = new HashMap<String, String>();
        tableDefintion.put(KFSConstants.ReportConstants.TABLE_HEADER_LINE_KEY, tableHeaderLine);
        tableDefintion.put(KFSConstants.ReportConstants.SEPARATOR_LINE_KEY, separatorLine);
        tableDefintion.put(KFSConstants.ReportConstants.TABLE_CELL_FORMAT_KEY, tableCellFormat);

        return tableDefintion;
    }

    /**
     * Returns the values in a list of the passed in business object in order of the spring definition. The value for the
     * "EMPTY_CELL" entry is an empty string.
     * 
     * @param businessObject for which to return the values
     * @return the values
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
                    tableCellValues.add(ObjectUtils.isNotNull(propertyValue) ? propertyValue.toString() : StringUtils.EMPTY);
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

    public String getTableCellFormat(boolean allowColspan) {
        List<Integer> cellWidthList = this.getTableCellWidth();
        
        if(allowColspan && ObjectUtils.isNotNull(this.columnSpanDefinition)) {
            this.applyColspanOnCellWidth(cellWidthList);
        }

        int numberOfCell = cellWidthList.size();
        int rowCount = (int) Math.ceil(numberOfCell * 1.0 / columnCount);

        String tableCellFormat = StringUtils.EMPTY;
        for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
            String singleRowFormat = StringUtils.EMPTY;
            
            for (int columnIndex = 0; columnIndex < this.columnCount; columnIndex++) {
                int index = columnCount * rowIndex + columnIndex; 
                
                if(index >= numberOfCell) {
                    break;
                }
                
                int width = cellWidthList.get(index);
                if(width > 0) {
                    singleRowFormat = singleRowFormat + "%-" + width + "s ";
                }
            }
            
            tableCellFormat = tableCellFormat + singleRowFormat + "\n";
        }

        return tableCellFormat;
    }

    /**
     * get the width of all table cells
     * 
     * @return the width of all table cells. The width is in the order defined as the orderedPropertyNameToHeaderLabelMap
     */
    public void applyColspanOnCellWidth(List<Integer> cellWidthList) {
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
     * get the width of all table cells
     * 
     * @return the width of all table cells. The width is in the order defined as the orderedPropertyNameToHeaderLabelMap
     */
    public void applyColspanOnCellValues(List<String> cellValues) {
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
     * This method...
     * 
     * @param businessObject
     * @return
     */
    public List<String> getTableCellValuesPaddingWithEmptyCell(BusinessObject businessObject, boolean allowColspan) {
        List<String> tableCellValues = this.getTableCellValues(businessObject, allowColspan);

        int numberOfCell = orderedPropertyNameToHeaderLabelMap.entrySet().size();
        this.paddingTableCellValues(numberOfCell, tableCellValues);

        return tableCellValues;
    }

    /**
     * get the width of all table cells
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

    // put empty strings into the table cell values if the values are not enough to feed the table
    private void paddingTableCellValues(int numberOfCell, List<String> tableCellValues) {
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
