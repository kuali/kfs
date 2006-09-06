/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.financial.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.kuali.Constants;
import org.kuali.core.bo.BusinessObject;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.chart.bo.ObjLevel;

/**
 * This class...
 * 
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class ObjectLevelCodeDescriptionFormatter extends CodeDescriptionFormatterBase {

    private String chartOfAccountsCode;

    public ObjectLevelCodeDescriptionFormatter(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }

    /**
     * @see org.kuali.module.financial.util.CodeDescriptionFormatterBase#getDescriptionOfBO(org.kuali.core.bo.BusinessObject)
     */
    @Override
    protected String getDescriptionOfBO(BusinessObject bo) {
        return ((ObjLevel) bo).getFinancialObjectLevelName();
    }

    /**
     * @see org.kuali.module.financial.util.CodeDescriptionFormatterBase#getValuesToBusinessObjectsMap(java.util.Set)
     */
    @Override
    protected Map<String, BusinessObject> getValuesToBusinessObjectsMap(Set values) {
        Map<String, Object> criteria = new HashMap<String, Object>();
        criteria.put(Constants.CHART_OF_ACCOUNTS_CODE_PROPERTY_NAME, chartOfAccountsCode);
        criteria.put(Constants.FINANCIAL_OBJECT_LEVEL_CODE_PROPERTY_NAME, values);
        Collection<ObjLevel> coll = SpringServiceLocator.getBusinessObjectService().findMatchingOrderBy(ObjLevel.class, criteria, Constants.VERSION_NUMBER, true);

        Map<String, BusinessObject> results = new HashMap<String, BusinessObject>();
        // TODO: worry about version #s
        for (ObjLevel ol : coll) {
            results.put(ol.getFinancialObjectLevelCode(), ol);
        }
        return results;
    }

}
