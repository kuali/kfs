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
