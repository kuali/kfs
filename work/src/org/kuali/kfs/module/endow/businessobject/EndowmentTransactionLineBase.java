/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.businessobject;

import java.util.LinkedHashMap;

import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.KualiInteger;

/**
 * This is the generic class which contains all the elements on a typical Endowment Transaction Line.
 */
public abstract class EndowmentTransactionLineBase extends PersistableBusinessObjectBase implements EndowmentTransactionLine {

    private String documentNumber;
    private String transactionLineTypeCode;
    private KualiInteger transactionLineNumber;
    private String kemid;
    private String etranCode;
    private String transactionLineDescription;
    private String transactionIncomePrincipalIndicatorCode;
    private KualiDecimal transactionAmount;
    private boolean corpusIndicator;
    private KualiDecimal transactionUnits;
    private boolean linePosted;

    private KEMID kemidObj;
    private EndowmentTransactionCode etranCodeObj;
    private IncomePrincipalIndicator incomePrincipalIndicator;

    /**
     * Constructs a EndowmentTransactionLineBase.java.
     */
    public EndowmentTransactionLineBase() {
        setTransactionAmount(KualiDecimal.ZERO);
        setTransactionUnits(KualiDecimal.ZERO);

        kemidObj = new KEMID();
        etranCodeObj = new EndowmentTransactionCode();
        incomePrincipalIndicator = new IncomePrincipalIndicator();
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public String getTransactionLineTypeCode() {
        return transactionLineTypeCode;
    }

    public void setTransactionLineTypeCode(String transactionLineTypeCode) {
        this.transactionLineTypeCode = transactionLineTypeCode;
    }

    public KualiInteger getTransactionLineNumber() {
        return transactionLineNumber;
    }

    public void setTransactionLineNumber(KualiInteger transactionLineNumber) {
        this.transactionLineNumber = transactionLineNumber;
    }

    public String getKemid() {
        return kemid;
    }

    public void setKemid(String kemid) {
        this.kemid = kemid;
    }

    public String getEtranCode() {
        return etranCode;
    }

    public void setEtranCode(String etranCode) {
        this.etranCode = etranCode;
    }

    public String getTransactionLineDescription() {
        return transactionLineDescription;
    }

    public void setTransactionLineDescription(String transactionLineDescription) {
        this.transactionLineDescription = transactionLineDescription;
    }

    public KualiDecimal getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(KualiDecimal transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public boolean getCorpusIndicator() {
        return corpusIndicator;
    }

    public void setCorpusIndicator(boolean corpusIndicator) {
        this.corpusIndicator = corpusIndicator;
    }

    public KualiDecimal getTransactionUnits() {
        return transactionUnits;
    }

    public void setTransactionUnits(KualiDecimal transactionUnits) {
        this.transactionUnits = transactionUnits;
    }

    public boolean isLinePosted() {
        return linePosted;
    }

    public void setLinePosted(boolean linePosted) {
        this.linePosted = linePosted;
    }

    public String getTransactionIncomePrincipalIndicatorCode() {
        return transactionIncomePrincipalIndicatorCode;
    }

    public void setTransactionIncomePrincipalIndicatorCode(String transactionIncomePrincipalIndicatorCode) {
        this.transactionIncomePrincipalIndicatorCode = transactionIncomePrincipalIndicatorCode;
    }


    @Override
    protected LinkedHashMap toStringMapper() {
        // TODO Auto-generated method stub
        return null;
    }

    public IncomePrincipalIndicator getIncomePrincipalIndicator() {
        return incomePrincipalIndicator;
    }

    public void setIncomePrincipalIndicator(IncomePrincipalIndicator incomePrincipalIndicator) {
        this.incomePrincipalIndicator = incomePrincipalIndicator;
    }

    public void setKemidObj(KEMID kemidObj) {
        this.kemidObj = kemidObj;
    }

    public void setEtranCodeObj(EndowmentTransactionCode etranCodeObj) {
        this.etranCodeObj = etranCodeObj;
    }

    public KEMID getKemidObj() {
        return kemidObj;
    }

    public EndowmentTransactionCode getEtranCodeObj() {
        return etranCodeObj;
    }


}
