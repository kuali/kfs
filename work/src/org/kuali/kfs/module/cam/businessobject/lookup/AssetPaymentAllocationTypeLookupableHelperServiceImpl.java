/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
