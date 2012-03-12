/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.cam.businessobject.lookup;

import java.util.List;
import java.util.Map;

import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.bo.ExternalizableBusinessObject;

public class AssetPaymentAllocationTypeLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {

    /**
     * @see org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl#getExternalBusinessObjectProperty(java.lang.Object,
     *      java.lang.String)
     */
    @Override
    protected String getExternalBusinessObjectProperty(Object sampleBo, String propertyName) {
        return super.getExternalBusinessObjectProperty(sampleBo, propertyName);
    }

    /**
     * @see org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl#getExternalizableBusinessObjectClass(java.lang.Class,
     *      java.lang.String)
     */
    @Override
    protected Class<? extends ExternalizableBusinessObject> getExternalizableBusinessObjectClass(Class boClass, String propertyName) {
        return super.getExternalizableBusinessObjectClass(boClass, propertyName);
    }

    /**
     * @see org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl#getExternalizableBusinessObjectFieldValues(java.lang.String,
     *      java.util.Map)
     */
    @Override
    protected Map<String, String> getExternalizableBusinessObjectFieldValues(String eboPropertyName, Map<String, String> fieldValues) {
        return super.getExternalizableBusinessObjectFieldValues(eboPropertyName, fieldValues);
    }

    /**
     * @see org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl#getExternalizableBusinessObjectProperties(java.lang.Class,
     *      java.util.Map)
     */
    @Override
    protected List<String> getExternalizableBusinessObjectProperties(Class boClass, Map<String, String> fieldValues) {
        return super.getExternalizableBusinessObjectProperties(boClass, fieldValues);
    }

    /**
     * @see org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl#getPrimaryKeyFieldLabels()
     */
    @Override
    public String getPrimaryKeyFieldLabels() {
        return super.getPrimaryKeyFieldLabels();
    }

    /**
     * @see org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl#getSearchResults(java.util.Map)
     */
    @Override
    public List<? extends BusinessObject> getSearchResults(Map<String, String> fieldValues) {
        return super.getSearchResults(fieldValues);
    }

    /**
     * @see org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl#getSearchResultsHelper(java.util.Map, boolean)
     */
    @Override
    protected List<? extends BusinessObject> getSearchResultsHelper(Map<String, String> fieldValues, boolean unbounded) {
        return super.getSearchResultsHelper(fieldValues, unbounded);
    }

    /**
     * @see org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl#getSearchResultsUnbounded(java.util.Map)
     */
    @Override
    public List<? extends BusinessObject> getSearchResultsUnbounded(Map<String, String> fieldValues) {
        return super.getSearchResultsUnbounded(fieldValues);
    }

    /**
     * @see org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl#hasExternalBusinessObjectProperty(java.lang.Class,
     *      java.util.Map)
     */
    @Override
    protected boolean hasExternalBusinessObjectProperty(Class boClass, Map<String, String> fieldValues) {
        return super.hasExternalBusinessObjectProperty(boClass, fieldValues);
    }

    /**
     * @see org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl#isExternalBusinessObjectProperty(java.lang.Object,
     *      java.lang.String)
     */
    @Override
    protected boolean isExternalBusinessObjectProperty(Object sampleBo, String propertyName) {
        return super.isExternalBusinessObjectProperty(sampleBo, propertyName);
    }

    /**
     * @see org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl#isSearchUsingOnlyPrimaryKeyValues()
     */
    @Override
    public boolean isSearchUsingOnlyPrimaryKeyValues() {
        return super.isSearchUsingOnlyPrimaryKeyValues();
    }

    /**
     * @see org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl#removeExternalizableBusinessObjectFieldValues(java.lang.Class,
     *      java.util.Map)
     */
    @Override
    protected Map<String, String> removeExternalizableBusinessObjectFieldValues(Class boClass, Map<String, String> fieldValues) {
        return super.removeExternalizableBusinessObjectFieldValues(boClass, fieldValues);
    }

}
