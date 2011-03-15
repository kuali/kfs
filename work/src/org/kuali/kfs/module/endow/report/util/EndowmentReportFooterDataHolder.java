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
package org.kuali.kfs.module.endow.report.util;

import java.util.ArrayList;
import java.util.List;

public class EndowmentReportFooterDataHolder {

    private String reference;
    private String EstablishedDate;
    private String kemidType;
    private String kemidPurpose;
    private String reportRunDate;
    private List<BenefittingForFooter> BenefittingList = null;
    
    public EndowmentReportFooterDataHolder() {       
    }

    /**
     * Creates benefitting info and register it
     * 
     * @return
     */
    public BenefittingForFooter createBenefittingForFooter() {
        if (BenefittingList == null) {
            BenefittingList = new ArrayList<BenefittingForFooter>();
        }
        BenefittingForFooter benefittingForFooter = new BenefittingForFooter();
        BenefittingList.add(benefittingForFooter);
        return benefittingForFooter;
    }
    
    /**
     * Benefitting footer data holder
     */
    public class BenefittingForFooter {
        private String campusName;
        private String chartName;
        private String organizationName;    
        private String benefittingPercent;
        
        public String getCampusName() {
            return campusName;
        }
        public void setCampusName(String campusName) {
            this.campusName = campusName;
        }
        public String getChartName() {
            return chartName;
        }
        public void setChartName(String chartName) {
            this.chartName = chartName;
        }
        public String getOrganizationName() {
            return organizationName;
        }
        public void setOrganizationName(String organizationName) {
            this.organizationName = organizationName;
        }
        public String getBenefittingPercent() {
            return benefittingPercent;
        }
        public void setBenefittingPercent(String benefittingPercent) {
            this.benefittingPercent = benefittingPercent;
        }        
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getEstablishedDate() {
        return EstablishedDate;
    }

    public void setEstablishedDate(String establishedDate) {
        EstablishedDate = establishedDate;
    }

    public String getKemidType() {
        return kemidType;
    }

    public void setKemidType(String kemidType) {
        this.kemidType = kemidType;
    }

    public String getKemidPurpose() {
        return kemidPurpose;
    }

    public void setKemidPurpose(String kemidPurpose) {
        this.kemidPurpose = kemidPurpose;
    }

    public String getReportRunDate() {
        return reportRunDate;
    }

    public void setReportRunDate(String reportRunDate) {
        this.reportRunDate = reportRunDate;
    }

    public List<BenefittingForFooter> getBenefittingList() {
        return BenefittingList;
    }

    public void setBenefittingList(List<BenefittingForFooter> benefittingList) {
        BenefittingList = benefittingList;
    }
    
}
