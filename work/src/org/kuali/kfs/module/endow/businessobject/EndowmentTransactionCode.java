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
