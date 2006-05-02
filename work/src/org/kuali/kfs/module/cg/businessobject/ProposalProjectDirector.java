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
	private String proposalProjectDirNote1Txt;
	private String proposalProjectDirNote2Txt;
	private String proposalProjectDirNote3Txt;
	private String proposalPrimaryProjectDirectorIndicator;
	private String proposalProjectDirectorProjectTitle;

    private ContractsAndGrantsProjectDirectorView contractsAndGrantsProjectDirectorView;
    
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
	 * @param - personUniversalIdentifier The personUniversalIdentifier to set.
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
	 * @param - proposalNumber The proposalNumber to set.
	 * 
	 */
	public void setProposalNumber(Long proposalNumber) {
		this.proposalNumber = proposalNumber;
	}


	/**
	 * Gets the proposalProjectDirNote1Txt attribute.
	 * 
	 * @return - Returns the proposalProjectDirNote1Txt
	 * 
	 */
	public String getProposalProjectDirNote1Txt() { 
		return proposalProjectDirNote1Txt;
	}

	/**
	 * Sets the proposalProjectDirNote1Txt attribute.
	 * 
	 * @param - proposalProjectDirNote1Txt The proposalProjectDirNote1Txt to set.
	 * 
	 */
	public void setProposalProjectDirNote1Txt(String proposalProjectDirNote1Txt) {
		this.proposalProjectDirNote1Txt = proposalProjectDirNote1Txt;
	}


	/**
	 * Gets the proposalProjectDirNote2Txt attribute.
	 * 
	 * @return - Returns the proposalProjectDirNote2Txt
	 * 
	 */
	public String getProposalProjectDirNote2Txt() { 
		return proposalProjectDirNote2Txt;
	}

	/**
	 * Sets the proposalProjectDirNote2Txt attribute.
	 * 
	 * @param - proposalProjectDirNote2Txt The proposalProjectDirNote2Txt to set.
	 * 
	 */
	public void setProposalProjectDirNote2Txt(String proposalProjectDirNote2Txt) {
		this.proposalProjectDirNote2Txt = proposalProjectDirNote2Txt;
	}


	/**
	 * Gets the proposalProjectDirNote3Txt attribute.
	 * 
	 * @return - Returns the proposalProjectDirNote3Txt
	 * 
	 */
	public String getProposalProjectDirNote3Txt() { 
		return proposalProjectDirNote3Txt;
	}

	/**
	 * Sets the proposalProjectDirNote3Txt attribute.
	 * 
	 * @param - proposalProjectDirNote3Txt The proposalProjectDirNote3Txt to set.
	 * 
	 */
	public void setProposalProjectDirNote3Txt(String proposalProjectDirNote3Txt) {
		this.proposalProjectDirNote3Txt = proposalProjectDirNote3Txt;
	}


	/**
	 * Gets the proposalPrimaryProjectDirectorIndicator attribute.
	 * 
	 * @return - Returns the proposalPrimaryProjectDirectorIndicator
	 * 
	 */
	public String getProposalPrimaryProjectDirectorIndicator() { 
		return proposalPrimaryProjectDirectorIndicator;
	}

	/**
	 * Sets the proposalPrimaryProjectDirectorIndicator attribute.
	 * 
	 * @param - proposalPrimaryProjectDirectorIndicator The proposalPrimaryProjectDirectorIndicator to set.
	 * 
	 */
	public void setProposalPrimaryProjectDirectorIndicator(String proposalPrimaryProjectDirectorIndicator) {
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
	 * @param - proposalProjectDirectorProjectTitle The proposalProjectDirectorProjectTitle to set.
	 * 
	 */
	public void setProposalProjectDirectorProjectTitle(String proposalProjectDirectorProjectTitle) {
		this.proposalProjectDirectorProjectTitle = proposalProjectDirectorProjectTitle;
	}

    /**
     * @return Returns the contractsAndGrantsProjectDirectorView.
     */
    public ContractsAndGrantsProjectDirectorView getContractsAndGrantsProjectDirectorView() {
        return contractsAndGrantsProjectDirectorView;
    }
   
	/**
	 * @see org.kuali.bo.BusinessObjectBase#toStringMapper()
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
