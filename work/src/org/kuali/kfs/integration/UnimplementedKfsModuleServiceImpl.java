/*
 * Copyright 2009 The Kuali Foundation
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
package org.kuali.kfs.integration;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.sys.service.impl.KfsModuleServiceImpl;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.bo.ExternalizableBusinessObject;

public class UnimplementedKfsModuleServiceImpl extends KfsModuleServiceImpl {

    public <T extends ExternalizableBusinessObject> T getExternalizableBusinessObject(Class<T> businessObjectClass, Map<String, Object> fieldValues) {
        return null;
    }

    public <T extends ExternalizableBusinessObject> List<T> getExternalizableBusinessObjectsList(Class<T> businessObjectClass, Map<String, Object> fieldValues) {
        return Collections.emptyList();
    }

    public <T extends ExternalizableBusinessObject> List<T> getExternalizableBusinessObjectsListForLookup(Class<T> businessObjectClass, Map<String, Object> fieldValues, boolean unbounded) {
        return Collections.emptyList();
    }

    @SuppressWarnings("unchecked")
    public List<? extends ExternalizableBusinessObject> retrieveExternalizableBusinessObjectsList(
            BusinessObject businessObject, String externalizableRelationshipName, Class externalizableClazz) {
        return Collections.emptyList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean isExternalizableBusinessObjectInquirable(Class boClass) {
        return false;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public boolean isExternalizableBusinessObjectLookupable(Class boClass) {
        return false;
    }
    
    public <T extends ExternalizableBusinessObject> T retrieveExternalizableBusinessObjectIfNecessary(BusinessObject businessObject, T currentInstanceExternalizableBO, String externalizableRelationshipName) {
        return currentInstanceExternalizableBO;
    };
    
    @SuppressWarnings("unchecked")
    @Override
    public String getExternalizableBusinessObjectInquiryUrl(Class inquiryBusinessObjectClass, Map<String, String[]> parameters) {
        return "";
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public String getExternalizableBusinessObjectLookupUrl(Class inquiryBusinessObjectClass, Map<String, String> parameters) {
        return "";
    }
    
    @Override
    public boolean isExternalJob(String jobName) {
        return false;
    }
}
