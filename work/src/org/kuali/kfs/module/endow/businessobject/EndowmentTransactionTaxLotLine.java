/*
 * Copyright 2010 The Kuali Foundation.
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
import java.util.LinkedHashMap;

import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * This class provides an implementation for the Tax Lot Lines in the Endowment transactional documents.
 */
public class EndowmentTransactionTaxLotLine extends PersistableBusinessObjectBase {
    // private KualiInteger lineNumber;
    private String documentNumber;
    private Integer documentLineNumber;
    private String documentLineTypeCode;
    private Integer transactionHoldingLotNumber;
    private String kemid;
    private String securityID;
    private String registrationCode;
    private String ipIndicator;
    private BigDecimal lotUnits;
    private BigDecimal lotHoldingCost;
    private BigDecimal lotLongTermGainLoss;
    private BigDecimal lotShortTermGainLoss;
    private Date lotAcquiredDate;
    private boolean newLotIndicator;

    private EndowmentTransactionLine transactionLine;
    private KEMID kemidObjRef;
    private Security security;
    private RegistrationCode registrationCodeObjRef;
    private IncomePrincipalIndicator ipIndicatorObjRef;

    /**
     * Constructs a EndowmentTransactionTaxLotLine.java.
     */
    public EndowmentTransactionTaxLotLine()
    {
        lotUnits             = new BigDecimal(BigInteger.ZERO, 4);
        lotHoldingCost       = new BigDecimal(BigInteger.ZERO, 2);
        lotLongTermGainLoss  = new BigDecimal(BigInteger.ZERO, 2);
        lotShortTermGainLoss = new BigDecimal(BigInteger.ZERO, 2);
    }
    
    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put(EndowPropertyConstants.TRANSACTION_LINE_DOCUMENT_NUMBER, this.documentNumber);
        m.put(EndowPropertyConstants.TRANSACTION_LINE_TYPE_CODE, this.documentLineTypeCode);
        m.put(EndowPropertyConstants.TRANSACTION_LINE_NUMBER, this.documentLineNumber);
        m.put(EndowPropertyConstants.TRANSACTION_HOLDING_LOT_NUMBER, this.transactionHoldingLotNumber);
        return m;

    }

    /**
     * Gets the lineNumber.
     * 
     * @return lineNumber
     */
    /*
     * public KualiInteger getLineNumber() { return lineNumber; }
     */
    /**
     * Sets the lineNumber.
     * 
     * @param lineNumber
     */
    /*
     * public void setLineNumber(KualiInteger lineNumber) { this.lineNumber = lineNumber; }
     */
    /**
     * Gets the documentNumber.
     * 
     * @return documentNumber
     */
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * Sets the documentNumber.
     * 
     * @param documentNumber
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    /**
     * Gets the documentLineNumber.
     * 
     * @return documentLineNumber
     */
    public Integer getDocumentLineNumber() {
        return documentLineNumber;
    }

    /**
     * Sets the documentLineNumber.
     * 
     * @param documentLineNumber
     */
    public void setDocumentLineNumber(Integer documentLineNumber) {
        this.documentLineNumber = documentLineNumber;
    }

    /**
     * Gets the documentLineTypeCode.
     * 
     * @return documentLineTypeCode
     */
    public String getDocumentLineTypeCode() {
        return documentLineTypeCode;
    }

    /**
     * Sets the documentLineTypeCode.
     * 
     * @param documentLineTypeCode
     */
    public void setDocumentLineTypeCode(String documentLineTypeCode) {
        this.documentLineTypeCode = documentLineTypeCode;
    }

    /**
     * Gets the transactionHoldingLotNumber.
     * 
     * @return transactionHoldingLotNumber
     */
    public Integer getTransactionHoldingLotNumber() {
        return transactionHoldingLotNumber;
    }

    /**
     * Sets the transactionHoldingLotNumber.
     * 
     * @param transactionHoldingLotNumber
     */
    public void setTransactionHoldingLotNumber(Integer transactionHoldingLotNumber) {
        this.transactionHoldingLotNumber = transactionHoldingLotNumber;
    }

    /**
     * Gets the lotUnits.
     * 
     * @return lotUnits
     */
    public BigDecimal getLotUnits() {
        return lotUnits;
    }

    /**
     * Sets the lotUnits.
     * 
     * @param lotUnits
     */
    public void setLotUnits(BigDecimal lotUnits) {
        this.lotUnits = lotUnits;
    }

    /**
     * Gets the lotHoldingCost.
     * 
     * @return lotHoldingCost
     */
    public BigDecimal getLotHoldingCost() {
        return lotHoldingCost;
    }

    /**
     * Sets the lotHoldingCost.
     * 
     * @param lotHoldingCost
     */
    public void setLotHoldingCost(BigDecimal lotHoldingCost) {
        this.lotHoldingCost = lotHoldingCost;
    }

    /**
     * Gets the lotLongTermGainLoss.
     * 
     * @return lotLongTermGainLoss
     */
    public BigDecimal getLotLongTermGainLoss() {
        return lotLongTermGainLoss;
    }

    /**
     * Sets the lotLongTermGainLoss.
     * 
     * @param lotLongTermGainLoss
     */
    public void setLotLongTermGainLoss(BigDecimal lotLongTermGainLoss) {
        this.lotLongTermGainLoss = lotLongTermGainLoss;
    }

    /**
     * Gets the lotShortTermGainLoss.
     * 
     * @return lotShortTermGainLoss
     */
    public BigDecimal getLotShortTermGainLoss() {
        return lotShortTermGainLoss;
    }

    /**
     * Sets the lotShortTermGainLoss.
     * 
     * @param lotShortTermGainLoss
     */
    public void setLotShortTermGainLoss(BigDecimal lotShortTermGainLoss) {
        this.lotShortTermGainLoss = lotShortTermGainLoss;
    }

    /**
     * Gets the transactionLine.
     * 
     * @return transactionLine
     */
    public EndowmentTransactionLine getTransactionLine() {
        return transactionLine;
    }

    /**
     * Sets the transactionLine.
     * 
     * @param transactionLine
     */
    public void setTransactionLine(EndowmentTransactionLine transactionLine) {
        this.transactionLine = transactionLine;
    }

    /**
     * Gets the lotAcquiredDate.
     * 
     * @return lotAcquiredDate
     */
    public Date getLotAcquiredDate() {
        return lotAcquiredDate;
    }

    /**
     * Sets the lotAcquiredDate.
     * 
     * @param lotAcquiredDate
     */
    public void setLotAcquiredDate(Date lotAcquiredDate) {
        this.lotAcquiredDate = lotAcquiredDate;
    }

    /**
     * Gets the kemid.
     * 
     * @return kemid
     */
    public String getKemid() {
        return kemid;
    }

    /**
     * Sets the kemid.
     * 
     * @param kemid
     */
    public void setKemid(String kemid) {
        this.kemid = kemid;
    }

    /**
     * Gets the securityID.
     * 
     * @return securityID
     */
    public String getSecurityID() {
        return securityID;
    }

    /**
     * Sets the securityID.
     * 
     * @param securityID
     */
    public void setSecurityID(String securityID) {
        this.securityID = securityID;
    }

    /**
     * Gets the registrationCode.
     * 
     * @return registrationCode
     */
    public String getRegistrationCode() {
        return registrationCode;
    }

    /**
     * Sets the registrationCode.
     * 
     * @param registrationCode
     */
    public void setRegistrationCode(String registrationCode) {
        this.registrationCode = registrationCode;
    }

    /**
     * Gets the ipIndicator.
     * 
     * @return ipIndicator
     */
    public String getIpIndicator() {
        return ipIndicator;
    }

    /**
     * Sets the ipIndicator.
     * 
     * @param ipIndicator
     */
    public void setIpIndicator(String ipIndicator) {
        this.ipIndicator = ipIndicator;
    }

    /**
     * Gets the newLotIndicator attribute. 
     * @return Returns the newLotIndicator.
     */
    public boolean isNewLotIndicator() {
        return newLotIndicator;
    }

    /**
     * Sets the newLotIndicator.
     * 
     * @param newLotIndicator
     */
    public void setNewLotIndicator (boolean newLotIndicator){
        this.newLotIndicator = newLotIndicator;
    }

    /**
     * Gets the kemidObjRef.
     * 
     * @return kemidObjRef
     */
    public KEMID getKemidObjRef() {
        return kemidObjRef;
    }

    /**
     * Sets the kemidObjRef.
     * 
     * @param kemidObjRef
     */
    public void setKemidObjRef(KEMID kemidObjRef) {
        this.kemidObjRef = kemidObjRef;
    }

    /**
     * Gets the security.
     * 
     * @return security
     */
    public Security getSecurity() {
        return security;
    }

    /**
     * Sets the security.
     * 
     * @param security
     */
    public void setSecurity(Security security) {
        this.security = security;
    }

    /**
     * Gets the registrationCodeObjRef.
     * 
     * @return registrationCodeObjRef
     */
    public RegistrationCode getRegistrationCodeObjRef() {
        return registrationCodeObjRef;
    }

    /**
     * Sets the registrationCodeObjRef.
     * 
     * @param registrationCodeObjRef
     */
    public void setRegistrationCodeObjRef(RegistrationCode registrationCodeObjRef) {
        this.registrationCodeObjRef = registrationCodeObjRef;
    }

    /**
     * Gets the ipIndicatorObjRef.
     * 
     * @return ipIndicatorObjRef
     */
    public IncomePrincipalIndicator getIpIndicatorObjRef() {
        return ipIndicatorObjRef;
    }

    /**
     * Sets the ipIndicatorObjRef.
     * 
     * @param ipIndicatorObjRef
     */
    public void setIpIndicatorObjRef(IncomePrincipalIndicator ipIndicatorObjRef) {
        this.ipIndicatorObjRef = ipIndicatorObjRef;
    }
}

