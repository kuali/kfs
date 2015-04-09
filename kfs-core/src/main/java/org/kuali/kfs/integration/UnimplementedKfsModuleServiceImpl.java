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
