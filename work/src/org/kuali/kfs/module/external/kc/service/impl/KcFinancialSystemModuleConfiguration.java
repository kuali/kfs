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
