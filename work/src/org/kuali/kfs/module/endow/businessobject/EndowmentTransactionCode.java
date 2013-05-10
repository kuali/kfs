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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.krad.bo.KualiCodeBase;

/**
 * Business Object for Endowment Transaction table.
 */
public class EndowmentTransactionCode extends KualiCodeBase {

    private String endowmentTransactionTypeCode;
    private EndowmentTransactionType endowmentTransactionType;
    private boolean corpusIndicator;

    private List<GLLink> glLinks;

    /**
     * Constructs a EndowmentTransactionCode.
     */
    public EndowmentTransactionCode() {
        glLinks = new ArrayList<GLLink>();
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("code", this.code);
        return m;
    }


    /**
     * This method gets the endowmentTransactionType
     * 
     * @return endowmentTransactionType
     */
    public EndowmentTransactionType getEndowmentTransactionType() {
        return endowmentTransactionType;
    }

    /**
     * This method sets the endowmentTransactionType
     * 
     * @param endowmentTransactionType
     */
    public void setEndowmentTransactionType(EndowmentTransactionType endowmentTransactionType) {
        this.endowmentTransactionType = endowmentTransactionType;
    }

    /**
     * This method gets the endowmentTransactionTypeCode
     * 
     * @return endowmentTransactionTypeCode
     */
    public String getEndowmentTransactionTypeCode() {
        return endowmentTransactionTypeCode;
    }

    /**
     * This method sets the endowmentTransactionTypeCode
     * 
     * @param endowmentTransactionTypeCode
     */
    public void setEndowmentTransactionTypeCode(String endowmentTransactionTypeCode) {
        this.endowmentTransactionTypeCode = endowmentTransactionTypeCode;
    }

    /**
     * Gets endowment transaction code and description.
     * 
     * @return
     */
    public String getCodeAndDescription() {
        if (StringUtils.isEmpty(code)) {
            return KFSConstants.EMPTY_STRING;
        }
        return super.getCodeAndDescription();
    }

    /**
     * Sets endowment transaction code and description.
     * 
     * @param codeAndDescription
     */
    public void setCodeAndDescription(String codeAndDescription) {

    }


    /**
     * Gets glLinks.
     * 
     * @return glLinks
     */
    public List<GLLink> getGlLinks() {
        return glLinks;
    }

    /**
     * Sets glLinks.
     * 
     * @param glLinks
     */
    public void setGlLinks(List<GLLink> glLinks) {
        this.glLinks = glLinks;
    }

    /**
     * Gets the corpusIndicator.
     * 
     * @return corpusIndicator
     */
    public boolean isCorpusIndicator() {
        return corpusIndicator;
    }

    /**
     * Sets the corpusIndicator.
     * 
     * @param corpusIndicator
     */
    public void setCorpusIndicator(boolean corpusIndicator) {
        this.corpusIndicator = corpusIndicator;
    }


}
