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
package org.kuali.kfs.sys.context;

import java.util.HashSet;
import java.util.Set;

import javax.xml.namespace.QName;

import org.kuali.rice.core.impl.resourceloader.SpringBeanFactoryResourceLoader;

/**
 * A custom {@link org.kuali.rice.kew.plugin.ResourceLoader} which wraps a Spring BeanFactory and delegates certain service lookups to
 * the BeanFactory.
 */
public class FinancialSystemResourceLoader extends SpringBeanFactoryResourceLoader {

    private static final String CONVERSIONS_DELIMITER = "|";
    //private static final String LOOKUPABLE_REGEX = "workflow-.+-Lookupable(.+)";

    private Set<String> overridableServices = new HashSet<String>();

    public FinancialSystemResourceLoader() {
        super(new QName("FinancialSystemResourceLoader"));
    }

    @Override
    public Object getService(QName serviceName) {
        if (overridableServices.contains(serviceName.getLocalPart())) {
            return super.getService(serviceName);
        }
//        else if (isKualiLookupable(serviceName)) {
//            return fetchKualiLookupable(serviceName);
//        }
        else if (serviceName.getLocalPart().indexOf("Lookupable") > -1) {
            return super.getService(serviceName);
        }
        else if (serviceName.getLocalPart().contains("InactivationBlockingDetectionService")) {
            return super.getService(serviceName);
        }
        return null;
    }

//    protected boolean isKualiLookupable(QName serviceName) {
//        return serviceName.getLocalPart().matches(LOOKUPABLE_REGEX);
//    }
//
//    protected Object fetchKualiLookupable(QName serviceName) {
//        String lookupableName = serviceName.getLocalPart();
//        WorkflowLookupable workflowLookupable = null;
//        if (lookupableName.indexOf(".") > 0) {
//            String lookupableImplName = lookupableName.substring(0, lookupableName.indexOf("("));
//            WorkflowLookupableImpl workflowLookupableImpl = (WorkflowLookupableImpl) getBeanFactory().getBean(lookupableImplName);
//            String allConversions = lookupableName.substring(lookupableName.indexOf("(") + 1, lookupableName.indexOf(")"));
//            String fieldConversions = null;
//            String lookupParameters = null;
//            if (allConversions.indexOf(CONVERSIONS_DELIMITER) > 0) {
//                fieldConversions = allConversions.substring(0, allConversions.indexOf(CONVERSIONS_DELIMITER));
//                lookupParameters = allConversions.substring(allConversions.indexOf(CONVERSIONS_DELIMITER) + 1);
//            }
//            else {
//                fieldConversions = allConversions;
//            }
//            workflowLookupableImpl.setFieldConversions(fieldConversions);
//            workflowLookupableImpl.setLookupParameters(lookupParameters);
//            workflowLookupable = (WorkflowLookupable) super.wrap(serviceName, workflowLookupableImpl);
//        }
//        else {
//            workflowLookupable = (WorkflowLookupable) super.getService(serviceName);
//        }
//        return workflowLookupable;
//    }

    public Set<String> getOverridableServices() {
        return overridableServices;
    }

    public void setOverridableServices(Set<String> overridableServices) {
        this.overridableServices = overridableServices;
    }

}
