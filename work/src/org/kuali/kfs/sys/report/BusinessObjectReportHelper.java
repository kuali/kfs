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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.service.DataDictionaryService;

/**
 * Helper class for business objects to assist formatting them for error reporting. Utilizes spring injection for modularization and configurability
 * @see org.kuali.kfs.sys.service.impl.ReportWriterTextServiceImpl
 */
public class BusinessObjectReportHelper  {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BusinessObjectReportHelper.class);

    protected int minimumMessageLength;
    protected String messageLabel;
    protected List<Class<? extends BusinessObject>> responsibleClasses;
    protected Class<? extends BusinessObject> dataDictionaryBusinessObjectClass;
    protected Map<String, String> orderedPropertyNameToHeaderLabelMap;
    protected DataDictionaryService dataDictionaryService;
    
    /**
     * Returns the values in a list of the passed in business object in order of the spring definition.
     * @param businessObject for which to return the values
     * @return the values
     */
    public List<Object> getValues(BusinessObject businessObject) {
        List<Object> keys = new ArrayList<Object>();
        
        for (Iterator<String> propertyNames = orderedPropertyNameToHeaderLabelMap.keySet().iterator(); propertyNames.hasNext();) {
            String propertyName = propertyNames.next();
            
            try {
                keys.add(PropertyUtils.getProperty(businessObject, propertyName));
            } catch (Exception e) {
                throw new RuntimeException("Failed getting propertyName=" + propertyName + " from businessObjecName=" + businessObject.getClass().getName(), e);
            }
        }
        
        return keys;
    }
    
    /**
     * Same as getValues except that it actually doesn't retrieve the values from the BO but instead returns a blank linke. This is useful
     * if indentation for message printing is necessary.
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
     * @param maximumPageWidth maximum before line is out of bounds. Used to fill message to the end of this range. Note that if there isn't at least maximumPageWidth characters available it will go minimumMessageLength out of bounds. It is up to the calling class to handle that
     * @return table header. Last element is the format of the table cells.
     */
    public List<String> getTableHeader(int maximumPageWidth) {
        String separatorLine = StringUtils.EMPTY;
        String messageFormat = StringUtils.EMPTY;
        
        // Construct the header based on orderedPropertyNameToHeaderLabelMap. It will pick the longest of label or DD size
        for (Iterator<Map.Entry<String,String>> entries = orderedPropertyNameToHeaderLabelMap.entrySet().iterator(); entries.hasNext();) {
            Map.Entry<String,String> entry = entries.next();
            
            int longest;
            try {
                longest = dataDictionaryService.getAttributeMaxLength(dataDictionaryBusinessObjectClass, entry.getKey());
            } catch (Exception e) {
                throw new RuntimeException("Failed getting propertyName=" + entry.getKey() + " from businessObjecName=" + dataDictionaryBusinessObjectClass.getName(), e);
            }
            if (entry.getValue().length() > longest) {
                longest = entry.getValue().length();
            }
            
            separatorLine = separatorLine + StringUtils.rightPad("", longest, KFSConstants.DASH) + " ";
            messageFormat = messageFormat + "%-" + longest + "s ";
        }
        
        // Now fill to the end of pageWidth for the message column. If there is not enough space go out of bounds
        int availableWidth = maximumPageWidth - (separatorLine.length()+1);
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
     * Gets the responsibleClasses
     * 
     * @return responsibleClasses
     */
    public List<Class<? extends BusinessObject>> getResponsibleClasses() {
        return responsibleClasses;
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
     * Sets the responsibleClasses
     * 
     * @param responsibleClasses The responsibleClasses to set.
     */
    public void setResponsibleClasses(List<Class<? extends BusinessObject>> responsibleClasses) {
        this.responsibleClasses = responsibleClasses;
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
}
