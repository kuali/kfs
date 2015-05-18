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
package org.kuali.rice.kns.util;

import org.apache.commons.lang.NumberUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.util.collect.CollectionUtils;

import java.util.Enumeration;

/**
 * Utility for that is used along with the tableRenderPagingBanner.tag.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public final class PagingBannerUtils {

	/** do not call. */
	private PagingBannerUtils() {
		throw new UnsupportedOperationException("do not call");
	}
	
    /**
     * find the number string in a method to call parameter with the following format parameterPrefix.1 or
     * parameterPrefix.1.bleh
     * 
     * @param paramPrefix the 
     * @param parameterNames the parameter names.
     * @return the numerical value or -1
     */
    public static int getNumbericalValueAfterPrefix(String paramPrefix, Enumeration<String> parameterNames) {
            	
    	for (String parameterName : CollectionUtils.toIterable(parameterNames)) {
    		if (parameterName.startsWith(paramPrefix)) {
            	parameterName = WebUtils.endsWithCoordinates(parameterName) ? parameterName : parameterName + ".x";
            	String numberStr = StringUtils.substringBetween(parameterName, paramPrefix, ".");
            	if (NumberUtils.isDigits(numberStr)) {
            		return Integer.parseInt(numberStr);
            	}
            }
        }

    	return -1;
    }

    /**
     * same as method above except for use when it is not feasible to use ordinals to identify columns -- for example,
     * if dynamic attributes may be used
     */
    public static String getStringValueAfterPrefix(String paramPrefix, Enumeration<String> parameterNames) {
        for (String parameterName : CollectionUtils.toIterable(parameterNames)) {
            if (parameterName.startsWith(paramPrefix)) {
                parameterName = WebUtils.endsWithCoordinates(parameterName) ? parameterName : parameterName + ".x";
                return StringUtils.substringBetween(parameterName, paramPrefix, ".");
            }
        }

        return "";
    }
}
