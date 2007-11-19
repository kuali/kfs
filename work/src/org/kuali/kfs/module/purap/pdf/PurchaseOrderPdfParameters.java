/*
 * Copyright 2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.module.purap.pdf;

import org.kuali.module.purap.bo.CampusParameter;

/**
 * Contains the parameters needed for creating a purchase order pdf document.
 */
public class PurchaseOrderPdfParameters {
    private String imageTempLocation;
    private String key;
    private String logoImage;
    private String directorSignatureImage;
    private String contractManagerSignatureImage;
    private CampusParameter campusParameter;
    private String statusInquiryUrl;
    private String contractLanguage;
    private String pdfFileLocation;
    private String pdfFileName;
    private String contractManagerCampusCode;
    private boolean useImage;

    public String getContractManagerCampusCode() {
        return contractManagerCampusCode;
    }

    public void setContractManagerCampusCode(String contractManagerCampusCode) {
        this.contractManagerCampusCode = contractManagerCampusCode;
    }

    public String getPdfFileName() {
        return pdfFileName;
    }

    public void setPdfFileName(String pdfFileName) {
        this.pdfFileName = pdfFileName;
    }

    public String getPdfFileLocation() {
        return pdfFileLocation;
    }

    public void setPdfFileLocation(String pdfFileLocation) {
        this.pdfFileLocation = pdfFileLocation;
    }

    public CampusParameter getCampusParameter() {
        return campusParameter;
    }

    public void setCampusParameter(CampusParameter campusParameter) {
        this.campusParameter = campusParameter;
    }

    public String getContractLanguage() {
        return contractLanguage;
    }

    public void setContractLanguage(String contractLanguage) {
        this.contractLanguage = contractLanguage;
    }

    public String getContractManagerSignatureImage() {
        return contractManagerSignatureImage;
    }

    public void setContractManagerSignatureImage(String contractManagerSignatureImage) {
        this.contractManagerSignatureImage = contractManagerSignatureImage;
    }

    public String getDirectorSignatureImage() {
        return directorSignatureImage;
    }

    public void setDirectorSignatureImage(String directorSignatureImage) {
        this.directorSignatureImage = directorSignatureImage;
    }

    public String getImageTempLocation() {
        return imageTempLocation;
    }

    public void setImageTempLocation(String imageTempLocation) {
        this.imageTempLocation = imageTempLocation;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getLogoImage() {
        return logoImage;
    }

    public void setLogoImage(String logoImage) {
        this.logoImage = logoImage;
    }

    public String getStatusInquiryUrl() {
        return statusInquiryUrl;
    }

    public void setStatusInquiryUrl(String statusInquiryUrl) {
        this.statusInquiryUrl = statusInquiryUrl;
    }

    public boolean isUseImage() {
        return useImage;
    }

    public void setUseImage(boolean useImage) {
        this.useImage = useImage;
    }


}
