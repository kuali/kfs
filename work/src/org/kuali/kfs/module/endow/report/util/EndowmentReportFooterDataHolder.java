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
