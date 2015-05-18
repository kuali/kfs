/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.kns.web.ui;

import org.kuali.rice.core.web.format.Formatter;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.kns.lookup.HtmlData.MultipleAnchorHtmlData;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Represents a Column in a result table
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Deprecated
public class Column implements java.io.Serializable, PropertyRenderingConfigElement {
    private static final long serialVersionUID = -5916942413570667803L;
    private String columnTitle;
    private String sortable = "true";
    private String propertyName;
    private String propertyValue;
    private Object unformattedPropertyValue;
    private String propertyURL;
    private HtmlData columnAnchor;
    private Formatter formatter;
    private Comparator comparator;
    private boolean escapeXMLValue=true;
    
    private String alternateDisplayPropertyName;
    private String additionalDisplayPropertyName;
    
    private boolean total;
    
    /**
     * A comparator used to compare the propertyValue values
     */
    private Comparator valueComparator;
    
    /**
     * Represents the maximum column length.  If propertyValue's length exceeds this value, then 
     * it will be truncated to this length when displayed
     */
    private int maxLength;
    
    public Column() {
    }
    
    public Column(String columnTitle, String propertyName) {
        this.columnTitle = columnTitle;
        this.propertyName = propertyName;
    }
    
    public Column(String columnTitle, String sortable, String propertyName) {
        this.columnTitle = columnTitle;
        this.sortable = sortable;
        this.propertyName = propertyName;
    }

    public Column(String columnTitle, String sortable, String propertyName, Comparator comparator) {
        this(columnTitle, sortable, propertyName);
        this.comparator = comparator;
    }

    public Column(String columnTitle, String propertyName, Formatter formatter) {
        this.columnTitle = columnTitle;
        this.propertyName = propertyName;
        this.formatter = formatter;
    }

    /**
     * @return Returns the comparator.
     */
    public Comparator getComparator() {
        return comparator;
    }


    /**
     * @param comparator The comparator to set.
     */
    public void setComparator(Comparator comparator) {
        this.comparator = comparator;
    }


    /**
     * @return Returns the columnTitle.
     */
    public String getColumnTitle() {
        return columnTitle;
    }


    /**
     * @param columnTitle The columnTitle to set.
     */
    public void setColumnTitle(String columnTitle) {
        this.columnTitle = columnTitle;
    }


    /**
     * @return Returns the propertyName.
     */
    public String getPropertyName() {
        return propertyName;
    }


    /**
     * @param propertyName The propertyName to set.
     */
    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }


    /**
     * @return Returns the sortable.
     */
    public String getSortable() {
        return sortable;
    }


    /**
     * @param sortable The sortable to set.
     */
    public void setSortable(String sortable) {
        this.sortable = sortable;
    }


    /**
     * @return Returns the propertyURL.
     */
    public String getPropertyURL() {
        return propertyURL;
    }


    /**
     * @param propertyURL The propertyURL to set.
     */
    public void setPropertyURL(String propertyURL) {
        this.propertyURL = propertyURL;
    }

	/**
	 * @return the columnAnchor
	 */
	public HtmlData getColumnAnchor() {
		return this.columnAnchor;
	}

	public boolean isMultipleAnchors(){
		return this.columnAnchor instanceof MultipleAnchorHtmlData;
	}

	public List<AnchorHtmlData> getColumnAnchors(){
		if(isMultipleAnchors())
			return ((MultipleAnchorHtmlData)this.columnAnchor).getAnchorHtmlData();
		List<AnchorHtmlData> htmlData = new ArrayList<AnchorHtmlData>();
		htmlData.add((AnchorHtmlData)columnAnchor);
		return htmlData;
	}

	public int getNumberOfColumnAnchors(){
		return getColumnAnchors().size();
	}

	/**
	 * @param columnAnchor the columnAnchor to set
	 */
	public void setColumnAnchor(HtmlData columnAnchor) {
		this.columnAnchor = columnAnchor;
		if(columnAnchor!=null) 
			setPropertyURL(((AnchorHtmlData)columnAnchor).getHref());
	}

	/**
     * @return Returns the propertyValue.
     */
    public String getPropertyValue() {
        return propertyValue;
    }


    /**
     * @param propertyValue The propertyValue to set.
     */
    public void setPropertyValue(String propertyValue) {
        this.propertyValue = propertyValue;
    }
    
    /**
     * @return Returns the formatter.
     */
    public Formatter getFormatter() {
        return formatter;
    }


    /**
     * @param formatter The formatter to set.
     */
    public void setFormatter(Formatter formatter) {
        this.formatter = formatter;
    }

    public Comparator getValueComparator() {
        return valueComparator;
    }

    public void setValueComparator(Comparator valueComparator) {
        this.valueComparator = valueComparator;
    }

    /**
     * Returns the maximum column length.  If propertyValue's length exceeds this value, then 
     * it will be truncated to this length when displayed
     * @return
     */
    public int getMaxLength() {
        return maxLength;
    }

    /**
     * Sets the maximum column length.  If propertyValue's length exceeds this value, then 
     * it will be truncated to this length when displayed
     * @param maxColumnLength
     */
    public void setMaxLength(int maxColumnLength) {
        this.maxLength = maxColumnLength;
    }

	/**
	 * @return the escapeXMLValue
	 */
	public boolean isEscapeXMLValue() {
		return this.escapeXMLValue;
	}

	/**
	 * @param escapeXMLValue the escapeXMLValue to set
	 */
	public void setEscapeXMLValue(boolean escapeXMLValue) {
		this.escapeXMLValue = escapeXMLValue;
	}

	public String getAlternateDisplayPropertyName() {
		return this.alternateDisplayPropertyName;
	}

	public void setAlternateDisplayPropertyName(String alternateDisplayPropertyName) {
		this.alternateDisplayPropertyName = alternateDisplayPropertyName;
	}

	public String getAdditionalDisplayPropertyName() {
		return this.additionalDisplayPropertyName;
	}

	public void setAdditionalDisplayPropertyName(String additionalDisplayPropertyName) {
		this.additionalDisplayPropertyName = additionalDisplayPropertyName;
	}

	public boolean isTotal() {
		return this.total;
	}

	public void setTotal(boolean total) {
		this.total = total;
	}

	public Object getUnformattedPropertyValue() {
		return this.unformattedPropertyValue;
	}

	public void setUnformattedPropertyValue(Object unformattedPropertyValue) {
		this.unformattedPropertyValue = unformattedPropertyValue;
	}
	
}
