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
