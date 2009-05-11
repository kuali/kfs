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
     * @param pageWidth the max page width. If the table width is greater than the given page width, a warning message is logged
     * 
     * @return the primary information that can define a table structure
     */
    public Map<String, String> getTableDefintion(int pageWidth) {
        String tableHeaderLineFormat = StringUtils.EMPTY;
        String tableCellFormat = StringUtils.EMPTY;
        String separatorLine = StringUtils.EMPTY;
        String singleRowFormat = StringUtils.EMPTY;

        // build the formatter for a single row
        List<Integer> cellWidthList = this.getTableCellWidth();
        Integer tableWidth = 0;
        for (int index =0; index < this.columnCount; index++) {
            Integer cellWidth = cellWidthList.get(index);
            
            separatorLine = separatorLine + StringUtils.rightPad(StringUtils.EMPTY, cellWidth, KFSConstants.DASH) + " ";
            singleRowFormat = singleRowFormat + "%-" + cellWidth + "s ";
            
            tableWidth += cellWidth;
        }
        
        // send out a warning if the table width is greater than the given page width
        if(tableWidth > pageWidth) {
            LOG.warn("message is out of bounds writing anyway");
        }

        // build the formatters for mutiple rows 
        int numberOfCell = cellWidthList.size();
        int rowCount = (int) Math.ceil(numberOfCell * 1.0 / columnCount);
        for(int index =0; index < rowCount; index++) {
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
     * Returns the values in a list of the passed in business object in order of the spring definition. The value for 
     * the "EMPTY_CELL" entry is an empty string. 
     * 
     * @param businessObject for which to return the values
     * @return the values
     */
    public List<String> getTableCellValues(BusinessObject businessObject) {
        List<String> tableCellValues = new ArrayList<String>();
        
        for (Map.Entry<String, String> entry : orderedPropertyNameToHeaderLabelMap.entrySet()) {
            String attributeName = entry.getKey();

            if (attributeName.startsWith(KFSConstants.ReportConstants.EMPTY_CELL_ENTRY_KEY_PREFIX)) {
                tableCellValues.add(StringUtils.EMPTY);
            }
            else {
                try {
                    Object propertyValue = PropertyUtils.getProperty(businessObject, attributeName);
                    tableCellValues.add(ObjectUtils.isNotNull(propertyValue) ? propertyValue.toString() : StringUtils.EMPTY);
                }
                catch (Exception e) {
                    throw new RuntimeException("Failed getting propertyName=" + entry.getKey() + " from businessObjecName=" + dataDictionaryBusinessObjectClass.getName(), e);
                }
            }
        }
        
        return tableCellValues;
    }
    
    /**
     * This method...
     * @param businessObject
     * @return
     */
    public List<String> getTableCellValuesPaddingWithEmptyCell(BusinessObject businessObject) {
        List<String> tableCellValues = this.getTableCellValues(businessObject);
        
        int numberOfCell = orderedPropertyNameToHeaderLabelMap.entrySet().size();
        this.paddingTableCellValues(numberOfCell, tableCellValues);
        
        return tableCellValues;
    }

    /**
     * get the width of all table cells
     * @return the width of all table cells. The width is in the order defined as the orderedPropertyNameToHeaderLabelMap
     */
    public List<Integer> getTableCellWidth() {
        List<Integer> cellWidthList = new ArrayList<Integer>();
        for (Map.Entry<String, String> entry : orderedPropertyNameToHeaderLabelMap.entrySet()) {
            String attributeName = entry.getKey();

            int maxLengthOfAttribute = 0;
            if (!attributeName.startsWith(KFSConstants.ReportConstants.EMPTY_CELL_ENTRY_KEY_PREFIX)) {
                try {
                    maxLengthOfAttribute = retrievePropertyValueMaximumLength(dataDictionaryBusinessObjectClass, entry.getKey());
                }
                catch (Exception e) {
                    throw new RuntimeException("Failed getting propertyName=" + entry.getKey() + " from businessObjecName=" + dataDictionaryBusinessObjectClass.getName(), e);
                }
            }

            int cellWidth = entry.getValue().length();
            if (entry.getValue().length() > maxLengthOfAttribute) {
                cellWidth = entry.getValue().length();
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

                cellWidthList.set(colIndex, longestLength);
            }
        }
        
        return cellWidthList;
    }
    
    // put empty strings into the table cell values if the values are not enough to feed the table 
    private void paddingTableCellValues(int numberOfCell, List<String> tableCellValues) {
        int reminder = columnCount - numberOfCell % columnCount;
        if(reminder < columnCount) {
            List<String> paddingObject = new ArrayList<String>(reminder);
            for(int index = 0; index < reminder; index++) {
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
}
