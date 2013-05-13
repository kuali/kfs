/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.businessobject;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.api.doctype.DocumentTypeService;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.krad.bo.DocumentHeader;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Business Object for Holding Tax Lot table
 */
public class TransactionArchive extends PersistableBusinessObjectBase {
    
    // Composite keys:
    private String  documentNumber;
    private Integer lineNumber;
    private String  lineTypeCode;
    
    // Other fields:
    private String description;
    private String typeCode;
    private String subTypeCode;
    private String srcTypeCode;
    private String kemid;
    private String etranCode;
    private String lineDescription;
    private String incomePrincipalIndicatorCode;
    private BigDecimal principalCashAmount;
    private BigDecimal incomeCashAmount;
    private boolean corpusIndicator;
    private BigDecimal corpusAmount;
    private Date postedDate;
    
    // Reference objects:
    protected List<TransactionArchiveSecurity> archiveSecurities;
    protected EndowmentTransactionCode etranObj;
    protected DocumentHeader documentHeader;
    protected KEMID kemidObj;
    
    // Transient members:
    private transient BigDecimal greaterAmount;
    private transient BigDecimal lessAmount;
    
    public TransactionArchive()
    {
        archiveSecurities   = new ArrayList<TransactionArchiveSecurity>();
        principalCashAmount = new BigDecimal(BigInteger.ZERO, 2);
        incomeCashAmount    = new BigDecimal(BigInteger.ZERO, 2);
        corpusAmount        = new BigDecimal(BigInteger.ZERO, 2);
    }

    /**
     * 
     * This method returns a multi-line field.
     * @return
     */
    public String getKemidResults() {
        StringBuilder result = new StringBuilder();
        if (ObjectUtils.isNotNull(kemidObj)) {
            result.append("[" + kemid + "," + " ,");
            result.append(kemidObj.getShortTitle() + "]");
        }
        
        return result.toString();
    }
      
    /**
     * 
     * This method returns a multi-line field.
     * @return
     */
    public String getEtranCodeResults() {
        StringBuilder result = new StringBuilder();
        if (ObjectUtils.isNotNull(etranObj)) {
            result.append("[" + etranCode + "," + " ,");
            result.append(etranObj.getName() + "]");
        }
        
        return result.toString();
    }
    
    /**
     * 
     * This method returns a multi-line field.
     * @return
     */
    public String getSecurityResults() {
        StringBuilder result = new StringBuilder();
        TransactionArchiveSecurity archiveSecurity = getArchiveSecurity();
        if (ObjectUtils.isNotNull(archiveSecurity)) {
            result.append("[" + archiveSecurity.getSecurityId() + "," + " ,");
            result.append(archiveSecurity.getSecurity().getDescription() + "]");
        }
        
        return result.toString();
    }
    
    /**
     * 
     * Data for the transaction document type results column.
     *
     * @return
     */
    public String getDocumentTypeResults() {
        
        DocumentTypeService documentTypeService = SpringContext.getBean(DocumentTypeService.class);
        org.kuali.rice.kew.api.doctype.DocumentType documentType = documentTypeService.getDocumentTypeByName(typeCode);
        
        StringBuilder result = new StringBuilder();
        result.append("[" + typeCode + "," + " ,");
        
        if (documentType != null) {
            result.append(documentType.getLabel());
        }
        result.append("]");
        
        return result.toString();
    }
    
    /**
     * Gets the documentHeader attribute. 
     * @return Returns the documentHeader.
     */
    public DocumentHeader getDocumentHeader() {
        return documentHeader;
    }

    /**
     * Sets the documentHeader attribute value.
     * @param documentHeader The documentHeader to set.
     */
    public void setDocumentHeader(DocumentHeader documentHeader) {
        this.documentHeader = documentHeader;
    }

    /**
     * 
     * This method...
     * @return
     */
    public String getTransactionArchiveViewer() {
        return "View Detail";
    }

    /**
     * Gets the etranObj attribute. 
     * @return Returns the etranObj.
     */
    public EndowmentTransactionCode getEtranObj() {
        return etranObj;
    }

    /**
     * Sets the etranObj attribute value.
     * @param etranObj The etranObj to set.
     */
    public void setEtranObj(EndowmentTransactionCode etranObj) {
        this.etranObj = etranObj;
    }
    
    /**
     * Gets the kemidObj attribute. 
     * @return Returns the kemidObj.
     */
    public KEMID getKemidObj() {
        return kemidObj;
    }

    /**
     * Sets the kemidObj attribute value.
     * @param kemidObj The kemidObj to set.
     */
    public void setKemidObj(KEMID kemidObj) {
        this.kemidObj = kemidObj;
    }

    /**
     * Gets the greaterAmount attribute. 
     * @return Returns the greaterAmount.
     */
    public BigDecimal getGreaterAmount() {
        return greaterAmount;
    }

    /**
     * Sets the greaterAmount attribute value.
     * @param greaterAmount The greaterAmount to set.
     */
    public void setGreaterAmount(BigDecimal greaterAmount) {
        this.greaterAmount = greaterAmount;
    }

    /**
     * Gets the lessAmount attribute. 
     * @return Returns the lessAmount.
     */
    public BigDecimal getLessAmount() {
        return lessAmount;
    }

    /**
     * Sets the lessAmount attribute value.
     * @param lessAmount The lessAmount to set.
     */
    public void setLessAmount(BigDecimal lessAmount) {
        this.lessAmount = lessAmount;
    }

    /**
     * Gets the archiveSecurity attribute. 
     * @return Returns the archiveSecurity.
     */
    public TransactionArchiveSecurity getArchiveSecurity() {
        return !archiveSecurities.isEmpty() ? archiveSecurities.get(0) 
               : new TransactionArchiveSecurity();
    }

    /**
     * Gets the documentNumber attribute. 
     * @return Returns the documentNumber.
     */
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * Sets the documentNumber attribute value.
     * @param documentNumber The documentNumber to set.
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    /**
     * Gets the lineNumber attribute. 
     * @return Returns the lineNumber.
     */
    public Integer getLineNumber() {
        return lineNumber;
    }

    /**
     * Sets the lineNumber attribute value.
     * @param lineNumber The lineNumber to set.
     */
    public void setLineNumber(Integer lineNumber) {
        this.lineNumber = lineNumber;
    }

    /**
     * Gets the lineTypeCode attribute. 
     * @return Returns the lineTypeCode.
     */
    public String getLineTypeCode() {
        return lineTypeCode;
    }

    /**
     * Sets the lineTypeCode attribute value.
     * @param lineTypeCode The lineTypeCode to set.
     */
    public void setLineTypeCode(String lineTypeCode) {
        this.lineTypeCode = lineTypeCode;
    }

    /**
     * Gets the description attribute. 
     * @return Returns the description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description attribute value.
     * @param description The description to set.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the typeCode attribute. 
     * @return Returns the typeCode.
     */
    public String getTypeCode() {
        return typeCode;
    }

    /**
     * Sets the typeCode attribute value.
     * @param typeCode The typeCode to set.
     */
    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    /**
     * Gets the subTypeCode attribute. 
     * @return Returns the subTypeCode.
     */
    public String getSubTypeCode() {
        return subTypeCode;
    }

    /**
     * Sets the subTypeCode attribute value.
     * @param subTypeCode The subTypeCode to set.
     */
    public void setSubTypeCode(String subTypeCode) {
        this.subTypeCode = subTypeCode;
    }

    /**
     * Gets the srcTypeCode attribute. 
     * @return Returns the srcTypeCode.
     */
    public String getSrcTypeCode() {
        return srcTypeCode;
    }

    /**
     * Sets the srcTypeCode attribute value.
     * @param srcTypeCode The srcTypeCode to set.
     */
    public void setSrcTypeCode(String srcTypeCode) {
        this.srcTypeCode = srcTypeCode;
    }

    /**
     * Gets the kemid attribute. 
     * @return Returns the kemid.
     */
    public String getKemid() {
        return kemid;
    }

    /**
     * Sets the kemid attribute value.
     * @param kemid The kemid to set.
     */
    public void setKemid(String kemid) {
        this.kemid = kemid;
    }

    /**
     * Gets the etranCode attribute. 
     * @return Returns the etranCode.
     */
    public String getEtranCode() {
        return etranCode;
    }

    /**
     * Sets the etranCode attribute value.
     * @param etranCode The etranCode to set.
     */
    public void setEtranCode(String etranCode) {
        this.etranCode = etranCode;
    }

    /**
     * Gets the lineDescription attribute. 
     * @return Returns the lineDescription.
     */
    public String getLineDescription() {
        return lineDescription;
    }

    /**
     * Sets the lineDescription attribute value.
     * @param lineDescription The lineDescription to set.
     */
    public void setLineDescription(String lineDescription) {
        this.lineDescription = lineDescription;
    }

    /**
     * Gets the incomePrincipalIndicatorCode attribute. 
     * @return Returns the incomePrincipalIndicatorCode.
     */
    public String getIncomePrincipalIndicatorCode() {
        return incomePrincipalIndicatorCode;
    }

    /**
     * Sets the incomePrincipalIndicatorCode attribute value.
     * @param incomePrincipalIndicatorCode The incomePrincipalIndicatorCode to set.
     */
    public void setIncomePrincipalIndicatorCode(String incomePrincipalIndicatorCode) {
        this.incomePrincipalIndicatorCode = incomePrincipalIndicatorCode;
    }

    /**
     * Gets the principalCashAmount attribute. 
     * @return Returns the principalCashAmount.
     */
    public BigDecimal getPrincipalCashAmount() {
        return principalCashAmount;
    }

    /**
     * Sets the principalCashAmount attribute value.
     * @param principalCashAmount The principalCashAmount to set.
     */
    public void setPrincipalCashAmount(BigDecimal principalCashAmount) {
        this.principalCashAmount = principalCashAmount;
    }

    /**
     * Gets the incomeCashAmount attribute. 
     * @return Returns the incomeCashAmount.
     */
    public BigDecimal getIncomeCashAmount() {
        return incomeCashAmount;
    }

    /**
     * Sets the incomeCashAmount attribute value.
     * @param incomeCashAmount The incomeCashAmount to set.
     */
    public void setIncomeCashAmount(BigDecimal incomeCashAmount) {
        this.incomeCashAmount = incomeCashAmount;
    }

    /**
     * Gets the corpusIndicator attribute. 
     * @return Returns the corpusIndicator.
     */
    public boolean getCorpusIndicator() {
        return corpusIndicator;
    }

    /**
     * Sets the corpusIndicator attribute value.
     * @param corpusIndicator The corpusIndicator to set.
     */
    public void setCorpusIndicator(boolean corpusIndicator) {
        this.corpusIndicator = corpusIndicator;
    }

    /**
     * Gets the corpusAmount attribute. 
     * @return Returns the corpusAmount.
     */
    public BigDecimal getCorpusAmount() {
        return corpusAmount;
    }

    /**
     * Sets the corpusAmount attribute value.
     * @param corpusAmount The corpusAmount to set.
     */
    public void setCorpusAmount(BigDecimal corpusAmount) {
        this.corpusAmount = corpusAmount;
    }

    /**
     * Gets the postedDate attribute. 
     * @return Returns the postedDate.
     */
    public Date getPostedDate() {
        return postedDate;
    }

    /**
     * Sets the postedDate attribute value.
     * @param postedDate The postedDate to set.
     */
    public void setPostedDate(Date postedDate) {
        this.postedDate = postedDate;
    }

    /**
     * Gets the archiveSecurities attribute. 
     * @return Returns the archiveSecurities.
     */
    public List<TransactionArchiveSecurity> getArchiveSecurities() {
        return archiveSecurities;
    }

    /**
     * Sets the archiveSecurities attribute value.
     * @param archiveSecurities The archiveSecurities to set.
     */
    public void setArchiveSecurities(List<TransactionArchiveSecurity> archiveSecurities) {
        this.archiveSecurities = archiveSecurities;
    }
    /**
     * Gets the code and description 
     * @return Returns code + " - " + description  
     */
    public String getCodeAndDescription() {
        if (StringUtils.isEmpty(typeCode)) {
            return KFSConstants.EMPTY_STRING;
        }
        return typeCode + " - " + description;
    }
    
    /**
     * Sets the codeAndDescription attribute value.
     * @param set codeAndDescription 
     */
    public void setCodeAndDescription(String codeAndDescription) {}
    
}
