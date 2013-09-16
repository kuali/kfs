/*
 * Copyright 2013 The Kuali Foundation.
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
package org.kuali.kfs.integration.ld.businessobject.inquiry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.krad.bo.BusinessObject;


/**
 * AbstractPositionDataDetailsInquirableImpl that contains common PositionDataDetails inquiry logic and can be extended
 * by LD inquirable classes or optional module (ex: EC) inquirable classes.
 */
public abstract class AbstractPositionDataDetailsInquirableImpl extends AbstractLaborIntegrationInquirableImpl {

    public AbstractPositionDataDetailsInquirableImpl() {
        super();
    }

    @Override
    protected void addMoreParameters(Properties parameter, String attributeName) {
        parameter.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.START_METHOD);
    }

    @Override
    protected List buildUserDefinedAttributeKeyList() {
        List keys = new ArrayList();
        keys.add(KFSPropertyConstants.POSITION_NUMBER);
        keys.add(getEffectiveDateKey());
        return keys;
    }

    /**
     * Overridden by sub-classes to return the EffectiveDate key used by buildUserDefinedAttributeKeyList
     *
     * @return effectiveDate key
     */
    protected abstract String getEffectiveDateKey();

    @Override
    protected String getAttributeName(String attributeName) {
        return KFSPropertyConstants.POSITION_NUMBER;
    }

    @Override
    protected String getBaseUrl() {
        return KFSConstants.INQUIRY_ACTION;
    }

    /**
     * Overridden by sub-classes so they can leverage the EBO mechanism where necessary.
     *
     * @see org.kuali.kfs.integration.ld.businessobject.inquiry.AbstractLaborIntegrationInquirableImpl#getInquiryBusinessObjectClass(java.lang.String)
     */
    @Override
    protected abstract Class getInquiryBusinessObjectClass(String attributeName);

    /**
     * Overridden by sub-classes so they can leverage the EBO mechanism where necessary.
     *
     * @see org.kuali.rice.kns.inquiry.KualiInquirableImpl#getBusinessObject(java.util.Map)
     */
    @Override
    public abstract BusinessObject getBusinessObject(Map fieldValues);

    @Override
    protected String getKeyName(String keyName) {
        return keyName;
    }

    @Override
    protected Object getKeyValue(String keyName, Object keyValue) {
        return keyValue;
    }

    @Override
    protected String getLookupableImplAttributeName() {
        return null;
    }

    @Override
    protected Map getUserDefinedAttributeMap() {
        Map userDefinedAttributeMap = new HashMap();
        userDefinedAttributeMap.put(KFSPropertyConstants.POSITION_NUMBER, KFSPropertyConstants.POSITION_NUMBER);

        return userDefinedAttributeMap;
    }

}