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

import org.apache.commons.lang.StringUtils;
import org.displaytag.decorator.DisplaytagColumnDecorator;
import org.displaytag.exception.DecoratorException;
import org.displaytag.properties.MediaTypeEnum;
import org.kuali.rice.kns.web.comparator.CellComparatorHelper;
import org.kuali.rice.krad.util.KRADConstants;

import javax.servlet.jsp.PageContext;

/** @see #decorate(Object, PageContext, MediaTypeEnum) */
@Deprecated
public class FormatAwareDecorator implements DisplaytagColumnDecorator {

    /**
     * Empty values don't show up properly in HTML. So, the String "&nbsp;" is substituted for an empty or null value of cellValue
     * if mediaType is MediaTypeEnum.HTML. If mediaType is not {@link MediaTypeEnum.HTML} and cellValue is not null, then
     * <code>CellComparatorHelper.getSanitizedValue(cellValue.toString())</code> is returned.
     * 
     * @param cellValue
     * @param pageContext
     * @param mediaType
     */
    public Object decorate(Object cellValue, PageContext pageContext, MediaTypeEnum mediaType) throws DecoratorException {

        if (null == cellValue) {
            return getEmptyStringFor(mediaType);
        }
        
        final String decoratedOutput;
        
        if (isCollection(cellValue)) {
        	decoratedOutput = createCollectionString(cellValue);
        } else {
        	decoratedOutput = MediaTypeEnum.HTML.equals(mediaType) ? cellValue.toString() : CellComparatorHelper
                    .getSanitizedStaticValue(cellValue.toString());
        }

        return StringUtils.isBlank(decoratedOutput) ? getEmptyStringFor(mediaType) : StringUtils.trim(decoratedOutput);
    }
    
    /**
     * Takes a cellValue which is a collection and creates a String representations.
     * 
     * <p>
     * If a column resulting from lookup contains collection values, each of the collection entry
     * should be printed on one line (i.e. separated by a <br/>). If there is no entry in the
     * collection, then we'll just print an &nbsp for the column.
     * </p>
     * 
     * @param cellValue the cell value to convert
     * @return the string representation of the cell value
     */
    private static String createCollectionString(Object cellValue) {
    	String decoratedOutput = "";
    	
    	String cellContentToBeParsed = cellValue.toString().substring(1, cellValue.toString().indexOf("]"));
        if (StringUtils.isNotBlank(cellContentToBeParsed)) {
            String[] parsed = cellContentToBeParsed.split(",");
            for (String elem : parsed) {
                decoratedOutput = decoratedOutput + elem + "<br/>";                    
            }
        }
        return decoratedOutput;
    }
    
    /**
     * Checks if a cell value is a Collection
     * 
     * @param cellValue to check
     * @return true if a Collection
     */
    private static boolean isCollection(Object cellValue) {
        return cellValue != null && (cellValue.toString().indexOf("[") == 0 && cellValue.toString().indexOf("]") > 0 && ((cellValue.toString().length() -1) == cellValue.toString().indexOf("]"))); 
    }
    
    /**
     * Gets an empty string type based on the media type.
     * 
     * @param mediaType the media type
     * @return the empty string 
     */
    private static String getEmptyStringFor(MediaTypeEnum mediaType) {
    	return MediaTypeEnum.HTML.equals(mediaType) ? "&nbsp" : KRADConstants.EMPTY_STRING;
    }

}
