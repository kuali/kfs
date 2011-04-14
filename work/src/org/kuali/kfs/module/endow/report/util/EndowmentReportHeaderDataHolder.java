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

import java.util.List;

/** 
 * The data holder for report request headers
 */
public class EndowmentReportHeaderDataHolder {

    private String institutionName;
    private String reportRequested;
    private String dateRequested;
    private String requestedBy;
    private String endowmentOption;
    private String reportOption; 
    
    private String benefittingCampus;
    private String benefittingChart;
    private String benefittingOrganization;
    private String kemidTypeCode;
    private String kemidPurposeCode;
    private String combineGroupCode;
    
    private List<KemidsWithMultipleBenefittingOrganizationsDataHolder> kemidsWithMultipleBenefittingOrganizationsDataHolders;
    
    private List<String> kemidsSelected;
    
    /**
     * 
     */
    public String getInstitutionName() {
        return institutionName;
    }

    /**
     * 
     */
    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }

    /**
     * 
     */
    public String getReportRequested() {
        return reportRequested;
    }

    /**
     * 
     */
    public void setReportRequested(String reportRequested) {
        this.reportRequested = reportRequested;
    }

    /**
     * 
     */
    public String getDateRequested() {
        return dateRequested;
    }

    /**
     * 
     */
    public void setDateRequested(String dateRequested) {
        this.dateRequested = dateRequested;
    }

    /**
     * 
     */
    public String getRequestedBy() {
        return requestedBy;
    }

    /**
     * 
     */
    public void setRequestedBy(String requestedBy) {
        this.requestedBy = requestedBy;
    }

    /**
     * 
     */
    public String getEndowmentOption() {
        return endowmentOption;
    }

    /**
     * 
     */
    public void setEndowmentOption(String endowmentOption) {
        this.endowmentOption = endowmentOption;
    }

    /**
     * 
     */
    public String getReportOption() {
        return reportOption;
    }

    /**
     * 
     */
    public void setReportOption(String reportOption) {
        this.reportOption = reportOption;
    }

    /**
     * 
     */
    public String getBenefittingCampus() {
        return benefittingCampus;
    }

    /**
     * 
     */
    public void setBenefittingCampus(String benefittingCampus) {
        this.benefittingCampus = benefittingCampus;
    }

    /**
     * 
     */
    public String getBenefittingChart() {
        return benefittingChart;
    }

    /**
     * 
     */
    public void setBenefittingChart(String benefittingChart) {
        this.benefittingChart = benefittingChart;
    }

    /**
     * 
     */
    public String getBenefittingOrganization() {
        return benefittingOrganization;
    }

    /**
     * 
     */
    public void setBenefittingOrganization(String benefittingOrganization) {
        this.benefittingOrganization = benefittingOrganization;
    }

    /**
     * 
     */
    public String getKemidTypeCode() {
        return kemidTypeCode;
    }

    /**
     * 
     */
    public void setKemidTypeCode(String kemidTypeCode) {
        this.kemidTypeCode = kemidTypeCode;
    }

    /**
     * 
     */
    public String getKemidPurposeCode() {
        return kemidPurposeCode;
    }

    /**
     * 
     */
    public void setKemidPurposeCode(String kemidPurposeCode) {
        this.kemidPurposeCode = kemidPurposeCode;
    }

    /**
     * 
     */
    public String getCombineGroupCode() {
        return combineGroupCode;
    }

    /**
     * 
     */
    public void setCombineGroupCode(String combineGroupCode) {
        this.combineGroupCode = combineGroupCode;
    }

    /**
     * 
     */
    public List<KemidsWithMultipleBenefittingOrganizationsDataHolder> getKemidsWithMultipleBenefittingOrganizationsDataHolders() {
        return kemidsWithMultipleBenefittingOrganizationsDataHolders;
    }

    /**
     * 
     */
    public void setKemidsWithMultipleBenefittingOrganizationsDataHolders(List<KemidsWithMultipleBenefittingOrganizationsDataHolder> kemidsWithMultipleBenefittingOrganizationsDataHolders) {
        this.kemidsWithMultipleBenefittingOrganizationsDataHolders = kemidsWithMultipleBenefittingOrganizationsDataHolders;
    }

    /**
     * 
     */
    public List<String> getKemidsSelected() {
        return kemidsSelected;
    }

    /**
     * 
     */
    public void setKemidsSelected(List<String> kemidsSelected) {
        this.kemidsSelected = kemidsSelected;
    } 
    
    
}
