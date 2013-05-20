/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.cg.businessobject.defaultvalue;

import org.apache.cxf.common.util.StringUtils;
import org.kuali.kfs.module.cg.CGConstants;
import org.kuali.rice.krad.valuefinder.ValueFinder;
import org.kuali.kfs.sys.context.SpringContext;

/**
 * This class returns the default value for the invoice template
 */
public class DefaultInvoiceTemplateFinder implements ValueFinder {

    /**
     * @see org.kuali.rice.krad.valuefinder.ValueFinder#getValue()
     */
    public String getValue() {
        String value = null;
        value = KNSServiceLocator.getParameterService().getParameterValueAsString(CGConstants.CG_NAMESPACE_CODE, CGConstants.AGENCY_DETAIL_TYPE_CODE, CGConstants.DEFAULT_INVOICE_TEMPLATE);
        if (StringUtils.isEmpty(value))
            return "";
        return value;
    }

}
