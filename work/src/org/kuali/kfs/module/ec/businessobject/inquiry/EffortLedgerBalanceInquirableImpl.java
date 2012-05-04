/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.ec.businessobject.inquiry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.ObjectUtils;
import org.kuali.kfs.gl.businessobject.inquiry.AbstractGeneralLedgerInquirableImpl;
import org.kuali.kfs.integration.ld.LaborLedgerBalanceForEffortCertification;
import org.kuali.kfs.module.ec.EffortConstants;
import org.kuali.kfs.module.ec.EffortPropertyConstants;
import org.kuali.kfs.module.ec.businessobject.EffortCertificationDetail;
import org.kuali.kfs.module.ec.businessobject.EffortCertificationDetailBuild;
import org.kuali.kfs.module.ec.document.EffortCertificationDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.service.KualiModuleService;

public class EffortLedgerBalanceInquirableImpl extends AbstractGeneralLedgerInquirableImpl {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(EffortLedgerBalanceInquirableImpl.class);

    private KualiModuleService kualiModuleService = SpringContext.getBean(KualiModuleService.class);

    /**
     * @see org.kuali.kfs.gl.businessobject.inquiry.AbstractGeneralLedgerInquirableImpl#addMoreParameters(java.util.Properties, java.lang.String)
     */
    @Override
    protected void addMoreParameters(Properties parameter, String attributeName) {
        BusinessObject businessObject = this.getBusinessObject();
        EffortCertificationDocument document = null;

        if (businessObject instanceof EffortCertificationDetailBuild) {
            EffortCertificationDetailBuild effortCertificationDetail = (EffortCertificationDetailBuild) businessObject;
            document = effortCertificationDetail.getEffortCertificationDocumentBuild();
        }
        else if (businessObject instanceof EffortCertificationDetail) {
            EffortCertificationDetail effortCertificationDetail = (EffortCertificationDetail) businessObject;
            document = effortCertificationDetail.getEffortCertificationDocument();
        }

        if (document != null) {
            parameter.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, ObjectUtils.toString(document.getUniversityFiscalYear()));
            parameter.put(EffortPropertyConstants.EFFORT_CERTIFICATION_REPORT_NUMBER, document.getEffortCertificationReportNumber());
            parameter.put(KFSPropertyConstants.EMPLID, document.getEmplid());
        }
    }

    /**
     * @see org.kuali.kfs.gl.businessobject.inquiry.AbstractGeneralLedgerInquirableImpl#buildUserDefinedAttributeKeyList()
     */
    @Override
    protected List<String> buildUserDefinedAttributeKeyList() {
        List<String> keys = new ArrayList<String>();

        keys.add(KFSPropertyConstants.ACCOUNT_NUMBER);
        keys.add(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        keys.add(KFSPropertyConstants.SUB_ACCOUNT_NUMBER);
        keys.add(KFSPropertyConstants.FINANCIAL_OBJECT_CODE);
        keys.add(KFSPropertyConstants.POSITION_NUMBER);

        return keys;
    }

    /**
     * @see org.kuali.kfs.gl.businessobject.inquiry.AbstractGeneralLedgerInquirableImpl#getAttributeName(java.lang.String)
     */
    @Override
    protected String getAttributeName(String attributeName) {
        return attributeName;
    }

    /**
     * @see org.kuali.kfs.gl.businessobject.inquiry.AbstractGeneralLedgerInquirableImpl#getBaseUrl()
     */
    @Override
    protected String getBaseUrl() {
        return KFSConstants.GL_BALANCE_INQUIRY_ACTION;
    }

    /**
     * @see org.kuali.kfs.gl.businessobject.inquiry.AbstractGeneralLedgerInquirableImpl#getInquiryBusinessObjectClass(java.lang.String)
     */
    @Override
    protected Class getInquiryBusinessObjectClass(String attributeName) {
        return LaborLedgerBalanceForEffortCertification.class;
    }

    /**
     * @see org.kuali.kfs.gl.businessobject.inquiry.AbstractGeneralLedgerInquirableImpl#getKeyName(java.lang.String)
     */
    @Override
    protected String getKeyName(String keyName) {
        return keyName;
    }

    /**
     * @see org.kuali.kfs.gl.businessobject.inquiry.AbstractGeneralLedgerInquirableImpl#getKeyValue(java.lang.String, java.lang.Object)
     */
    @Override
    protected Object getKeyValue(String keyName, Object keyValue) {
        return keyValue;
    }

    /**
     * @see org.kuali.kfs.gl.businessobject.inquiry.AbstractGeneralLedgerInquirableImpl#getLookupableImplAttributeName()
     */
    @Override
    protected String getLookupableImplAttributeName() {
        return null;
    }

    /**
     * @see org.kuali.kfs.gl.businessobject.inquiry.AbstractGeneralLedgerInquirableImpl#getUserDefinedAttributeMap()
     */
    @Override
    protected Map<String, Object> getUserDefinedAttributeMap() {
        Map<String, Object> userDefinedAttributeMap = new HashMap<String, Object>();

        userDefinedAttributeMap.put(EffortPropertyConstants.EFFORT_CERTIFICATION_PAYROLL_AMOUNT, KualiDecimal.ZERO);
        userDefinedAttributeMap.put(EffortPropertyConstants.EFFORT_CERTIFICATION_ORIGINAL_PAYROLL_AMOUNT, KualiDecimal.ZERO);

        return userDefinedAttributeMap;
    }

    /**
     * @see org.kuali.kfs.gl.businessobject.inquiry.AbstractGeneralLedgerInquirableImpl#isExclusiveField(java.lang.Object, java.lang.Object)
     */
    @Override
    protected boolean isExclusiveField(Object keyName, Object keyValue) {
        if (super.isExclusiveField(keyName, keyValue)) {
            return true;
        }

        if (keyName != null && keyValue != null) {
            if (keyName.equals(EffortPropertyConstants.SOURCE_ACCOUNT_NUMBER) && keyValue.equals(EffortConstants.DASH_ACCOUNT_NUMBER)) {
                return true;
            }
            else if (keyName.equals(EffortPropertyConstants.SOURCE_CHART_OF_ACCOUNTS_CODE) && keyValue.equals(EffortConstants.DASH_CHART_OF_ACCOUNTS_CODE)) {
                return true;
            }
        }
        return false;
    }
}
