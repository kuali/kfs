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
package org.kuali.module.gl.web.inquirable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.kuali.Constants;
import org.kuali.PropertyConstants;
import org.kuali.core.bo.user.Options;
import org.kuali.core.service.BusinessObjectDictionaryService;
import org.kuali.core.service.LookupService;
import org.kuali.core.service.OptionsService;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.gl.bo.Balance;
import org.kuali.module.gl.web.Constant;

/**
 * This class is used to generate the URL for the user-defined attributes for available account balace screen. It is entended the
 * AbstractGLInquirableImpl class, so it covers both the default implementation and customized implemetnation.
 * 
 * @author Bin Gao from Michigan State University
 */
public class AccountBalanceInquirableImpl extends AbstractGLInquirableImpl {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AccountBalanceInquirableImpl.class);

    private BusinessObjectDictionaryService dataDictionary;
    private LookupService lookupService;
    private Class businessObjectClass;

    /**
     * @see org.kuali.module.gl.web.inquirable.AbstractGLInquirableImpl#buildUserDefinedAttributeKeyList()
     */
    protected List buildUserDefinedAttributeKeyList() {
        List keys = new ArrayList();

        keys.add(PropertyConstants.UNIVERSITY_FISCAL_YEAR);
        keys.add(PropertyConstants.ACCOUNT_NUMBER);
        keys.add(PropertyConstants.CHART_OF_ACCOUNTS_CODE);
        keys.add(PropertyConstants.SUB_ACCOUNT_NUMBER);
        keys.add(PropertyConstants.OBJECT_CODE);
        keys.add(PropertyConstants.SUB_OBJECT_CODE);
        keys.add(Constant.CONSOLIDATION_OPTION);
        keys.add(Constant.PENDING_ENTRY_OPTION);

        return keys;
    }

    /**
     * @see org.kuali.module.gl.web.inquirable.AbstractGLInquirableImpl#getUserDefinedAttributeMap()
     */
    protected Map getUserDefinedAttributeMap() {
        Map userDefinedAttributeMap = new HashMap();

        OptionsService os = SpringServiceLocator.getOptionsService();
        Options o = os.getCurrentYearOptions();
        
        userDefinedAttributeMap.put(PropertyConstants.OBJECT_CODE, "");
        userDefinedAttributeMap.put(PropertyConstants.CURRENT_BUDGET_LINE_BALANCE_AMOUNT, Constant.BALANCE_TYPE_CB);
        userDefinedAttributeMap.put(PropertyConstants.ACCOUNT_LINE_ACTUALS_BALANCE_AMOUNT, o.getActualFinancialBalanceTypeCd());
        userDefinedAttributeMap.put(PropertyConstants.ACCOUNT_LINE_ENCUMBRANCE_BALANCE_AMOUNT, o.getExtrnlEncumFinBalanceTypCd());

        return userDefinedAttributeMap;
    }

    /**
     * @see org.kuali.module.gl.web.inquirable.AbstractGLInquirableImpl#getAttributeName(java.lang.String)
     */
    protected String getAttributeName(String attributeName) {
        return attributeName;
    }

    /**
     * @see org.kuali.module.gl.web.inquirable.AbstractGLInquirableImpl#getKeyValue(java.lang.String, java.lang.Object)
     */
    protected Object getKeyValue(String keyName, Object keyValue) {
        if (isExclusiveField(keyName, keyValue)) {
            keyValue = "";
        }
        return keyValue;
    }

    /**
     * @see org.kuali.module.gl.web.inquirable.AbstractGLInquirableImpl#getKeyName(java.lang.String)
     */
    protected String getKeyName(String keyName) {
        return keyName;
    }

    /**
     * @see org.kuali.module.gl.web.inquirable.AbstractGLInquirableImpl#getLookupableImplAttributeName()
     */
    protected String getLookupableImplAttributeName() {
        return Constant.GL_LOOKUPABLE_BALANCE;
    }

    /**
     * @see org.kuali.module.gl.web.inquirable.AbstractGLInquirableImpl#getBaseUrl()
     */
    protected String getBaseUrl() {
        return Constants.GL_BALANCE_INQUIRY_ACTION;
    }

    /**
     * @see org.kuali.module.gl.web.inquirable.AbstractGLInquirableImpl#getInquiryBusinessObjectClass()
     */
    protected Class getInquiryBusinessObjectClass() {
        return Balance.class;
    }

    /**
     * @see org.kuali.module.gl.web.inquirable.AbstractGLInquirableImpl#addMoreParameters(java.util.Properties, java.lang.String)
     */
    protected void addMoreParameters(Properties parameter, String attributeName) {
        parameter.put(Constants.LOOKUPABLE_IMPL_ATTRIBUTE_NAME, getLookupableImplAttributeName());
        parameter.put(Constant.AMOUNT_VIEW_OPTION, Constant.MONTHLY);

        String balanceTypeCode = (String) getUserDefinedAttributeMap().get(getAttributeName(attributeName));
        parameter.put(Constants.BALANCE_TYPE_PROPERTY_NAME, balanceTypeCode);
    }
}
