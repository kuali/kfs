/*
 * Copyright (c) 2004, 2005 The National Association of College and University 
 * Business Officers, Cornell University, Trustees of Indiana University, 
 * Michigan State University Board of Trustees, Trustees of San Joaquin Delta 
 * College, University of Hawai'i, The Arizona Board of Regents on behalf of the 
 * University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); 
 * By obtaining, using and/or copying this Original Work, you agree that you 
 * have read, understand, and will comply with the terms and conditions of the 
 * Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,  DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN 
 * THE SOFTWARE.
 */

package org.kuali.module.cg.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.BusinessObjectBase;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class ProposalProjectDirector extends BusinessObjectBase {

    private String personUniversalIdentifier;
    private Long proposalNumber;
    private String proposalProjectDirectorNote1Text;
    private String proposalProjectDirectorNote2Text;
    private String proposalProjectDirectorNote3Text;
    private boolean proposalPrimaryProjectDirectorIndicator;
    private String proposalProjectDirectorProjectTitle;

    private ContractsAndGrantsProjectDirectorView personUniversal;

    /**
     * Default constructor.
     */
    public ProposalProjectDirector() {

    }

    /**
     * Gets the personUniversalIdentifier attribute.
     * 
     * @return - Returns the personUniversalIdentifier
     * 
     */
    public String getPersonUniversalIdentifier() {
        return personUniversalIdentifier;
    }

    /**
     * Sets the personUniversalIdentifier attribute.
     * 
     * @param personUniversalIdentifier The personUniversalIdentifier to set.
     * 
     */
    public void setPersonUniversalIdentifier(String personUniversalIdentifier) {
        this.personUniversalIdentifier = personUniversalIdentifier;
    }


    /**
     * Gets the proposalNumber attribute.
     * 
     * @return - Returns the proposalNumber
     * 
     */
    public Long getProposalNumber() {
        return proposalNumber;
    }

    /**
     * Sets the proposalNumber attribute.
     * 
     * @param proposalNumber The proposalNumber to set.
     * 
     */
    public void setProposalNumber(Long proposalNumber) {
        this.proposalNumber = proposalNumber;
    }


    /**
     * Gets the proposalProjectDirectorNote1Text attribute.
     * 
     * @return - Returns the proposalProjectDirectorNote1Text
     * 
     */
    public String getProposalProjectDirectorNote1Text() {
        return proposalProjectDirectorNote1Text;
    }

    /**
     * Sets the proposalProjectDirectorNote1Text attribute.
     * 
     * @param proposalProjectDirectorNote1Text The proposalProjectDirectorNote1Text to set.
     * 
     */
    public void setProposalProjectDirectorNote1Text(String proposalProjectDirectorNote1Text) {
        this.proposalProjectDirectorNote1Text = proposalProjectDirectorNote1Text;
    }


    /**
     * Gets the proposalProjectDirectorNote2Text attribute.
     * 
     * @return - Returns the proposalProjectDirectorNote2Text
     * 
     */
    public String getProposalProjectDirectorNote2Text() {
        return proposalProjectDirectorNote2Text;
    }

    /**
     * Sets the proposalProjectDirectorNote2Text attribute.
     * 
     * @param proposalProjectDirectorNote2Text The proposalProjectDirectorNote2Text to set.
     * 
     */
    public void setProposalProjectDirectorNote2Text(String proposalProjectDirectorNote2Text) {
        this.proposalProjectDirectorNote2Text = proposalProjectDirectorNote2Text;
    }


    /**
     * Gets the proposalProjectDirectorNote3Text attribute.
     * 
     * @return - Returns the proposalProjectDirectorNote3Text
     * 
     */
    public String getProposalProjectDirectorNote3Text() {
        return proposalProjectDirectorNote3Text;
    }

    /**
     * Sets the proposalProjectDirectorNote3Text attribute.
     * 
     * @param proposalProjectDirectorNote3Text The proposalProjectDirectorNote3Text to set.
     * 
     */
    public void setProposalProjectDirectorNote3Text(String proposalProjectDirectorNote3Text) {
        this.proposalProjectDirectorNote3Text = proposalProjectDirectorNote3Text;
    }


    /**
     * Gets the proposalPrimaryProjectDirectorIndicator attribute.
     * 
     * @return - Returns the proposalPrimaryProjectDirectorIndicator
     * 
     */
    public boolean isProposalPrimaryProjectDirectorIndicator() {
        return proposalPrimaryProjectDirectorIndicator;
    }


    /**
     * Sets the proposalPrimaryProjectDirectorIndicator attribute.
     * 
     * @param proposalPrimaryProjectDirectorIndicator The proposalPrimaryProjectDirectorIndicator to set.
     * 
     */
    public void setProposalPrimaryProjectDirectorIndicator(boolean proposalPrimaryProjectDirectorIndicator) {
        this.proposalPrimaryProjectDirectorIndicator = proposalPrimaryProjectDirectorIndicator;
    }


    /**
     * Gets the proposalProjectDirectorProjectTitle attribute.
     * 
     * @return - Returns the proposalProjectDirectorProjectTitle
     * 
     */
    public String getProposalProjectDirectorProjectTitle() {
        return proposalProjectDirectorProjectTitle;
    }

    /**
     * Sets the proposalProjectDirectorProjectTitle attribute.
     * 
     * @param proposalProjectDirectorProjectTitle The proposalProjectDirectorProjectTitle to set.
     * 
     */
    public void setProposalProjectDirectorProjectTitle(String proposalProjectDirectorProjectTitle) {
        this.proposalProjectDirectorProjectTitle = proposalProjectDirectorProjectTitle;
    }

    /**
     * @return Returns the personUniversal.
     */
    public ContractsAndGrantsProjectDirectorView getPersonUniversal() {
        return personUniversal;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("personUniversalIdentifier", this.personUniversalIdentifier);
        if (this.proposalNumber != null) {
            m.put("proposalNumber", this.proposalNumber.toString());
        }
        return m;
    }
}
