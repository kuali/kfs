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
package org.kuali.kfs.module.external.kc.service.impl;

import java.util.Collections;
import java.util.Map;

import org.kuali.kfs.module.external.kc.service.KcFinancialSystemModuleConfig;
import org.kuali.kfs.sys.FinancialSystemModuleConfiguration;

/**
 * Slim subclass to enforce class hierarchy not enforced by the parent class' contract.
 */
public class KcFinancialSystemModuleConfiguration extends FinancialSystemModuleConfiguration implements KcFinancialSystemModuleConfig {
    
    protected Map<Class,String> externalizableBusinessObjectServiceImplementations;
    protected Map<String,String> kfsToKcInquiryUrlClassMapping;
    protected Map<String,String> kfsToKcInquiryUrlParameterMapping;
    
    /**
     * Constructs a FinancialSystemModuleConfiguration.java.
     */
    public KcFinancialSystemModuleConfiguration() {
        super();
    }
    
    
    /**
     * @return the externalizableBusinessObjectImplementations
     */
    public Map<Class,String> getExternalizableBusinessObjectServiceImplementations() {
        if (this.externalizableBusinessObjectServiceImplementations == null)
            return null;
        return (Map<Class,String>) Collections.unmodifiableMap(this.externalizableBusinessObjectServiceImplementations);
    }

    /**
     * @param externalizableBusinessObjectImplementations the externalizableBusinessObjectImplementations to set
     */
    public void setExternalizableBusinessObjectServiceImplementations(
          Map<Class, String> externalizableBusinessObjectServiceImplementations) {
        this.externalizableBusinessObjectServiceImplementations = externalizableBusinessObjectServiceImplementations;
    }


    public Map<String, String> getKfsToKcInquiryUrlClassMapping() {
        return kfsToKcInquiryUrlClassMapping;
    }


    public void setKfsToKcInquiryUrlClassMapping(Map<String, String> kfsToKcInquiryUrlClassMapping) {
        this.kfsToKcInquiryUrlClassMapping = kfsToKcInquiryUrlClassMapping;
    }


    public Map<String, String> getKfsToKcInquiryUrlParameterMapping() {
        return kfsToKcInquiryUrlParameterMapping;
    }


    public void setKfsToKcInquiryUrlParameterMapping(Map<String, String> kfsToKcInquiryUrlParameterMapping) {
        this.kfsToKcInquiryUrlParameterMapping = kfsToKcInquiryUrlParameterMapping;
    }

}
