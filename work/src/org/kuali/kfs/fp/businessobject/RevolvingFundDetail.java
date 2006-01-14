package org.kuali.module.financial.bo;

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

import java.util.LinkedHashMap;

import org.kuali.core.bo.BusinessObjectBase;

/**
 * @author Kuali Financial Transactions Team (kualidev@oncourse.iu.edu)
 */

public class RevolvingFundDetail extends BusinessObjectBase {
	private static final long serialVersionUID = -9064692781575306404L;


    /**
	 * Default no-arg constructor.
	 */
	public RevolvingFundDetail() {}


	private String documentHeaderId;
	
	private String bankAccountNumber;
	//TODO - is this an object reference?
	private String bankCode;
	
	private String columnTypeIndicator;
	
	private Integer lineNumber;
	
	private Integer revolvingFundAmount;
	
	private java.sql.Timestamp revolvingFundDate;
	
	private String revolvingFundReferenceNumber;
	
	private String revolvingFundDescription;
	//TODO - is this an object reference?
	private String typeCode;
	
	
	/**
	 * @param o
	 */
	public void setDocumentHeaderId(String o) {
	    documentHeaderId = o;
	}
	/**
	 * @return docHdrId
	 */
	public String getDocumentHeaderId() {
		return documentHeaderId;
	}
		
	/**
	 * @param o
	 */
	public void setBankAccountNumber(String o) {
	    bankAccountNumber = o;
	}
	/**
	 * @return fdocBankAcctNbr
	 */
	public String getBankAccountNumber() {
		return bankAccountNumber;
	}
		
	/**
	 * @param o
	 */
	public void setBankCode(String o) {
	    bankCode = o;
	}
	/**
	 * @return fdocBankCd
	 */
	public String getBankCode() {
		return bankCode;
	}
		
	/**
	 * @param o
	 */
	public void setColumnTypeIndicator(String o) {
	    columnTypeIndicator = o;
	}
	/**
	 * @return fdocColumnTypInd
	 */
	public String getColumnTypeIndicator() {
		return columnTypeIndicator;
	}
		
	/**
	 * @param o
	 */
	public void setLineNumber(Integer o) {
	    lineNumber = o;
	}
	/**
	 * @return fdocLineNbr
	 */
	public Integer getLineNumber() {
		return lineNumber;
	}
		
	/**
	 * @param o
	 */
	public void setRevolvingFundAmount(Integer o) {
	    revolvingFundAmount = o;
	}
	/**
	 * @return fdocRevolvfndAmt
	 */
	public Integer getRevolvingFundAmount() {
		return revolvingFundAmount;
	}
		
	/**
	 * @param o
	 */
	public void setRevolvingFundDate(java.sql.Timestamp o) {
	    revolvingFundDate = o;
	}
	/**
	 * @return fdocRevolvfndDt
	 */
	public java.sql.Timestamp getRevolvingFundDate() {
		return revolvingFundDate;
	}
		
	/**
	 * @param o
	 */
	public void setRevolvingFundReferenceNumber(String o) {
	    revolvingFundReferenceNumber = o;
	}
	/**
	 * @return fdocRvlfndrefNbr
	 */
	public String getRevolvingFundReferenceNumber() {
		return revolvingFundReferenceNumber;
	}
		
	/**
	 * @param o
	 */
	public void setRevolvingFundDescription(String o) {
	    revolvingFundDescription = o;
	}
	/**
	 * @return fdocRvlFndDesc
	 */
	public String getRevolvingFundDescription() {
		return revolvingFundDescription;
	}
		
	/**
	 * @param o
	 */
	public void setTypeCode(String o) {
	    typeCode = o;
	}
	/**
	 * @return fdocTypCd
	 */
	public String getTypeCode() {
		return typeCode;
	}
	
	/**
	 * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
			LinkedHashMap m = new LinkedHashMap();

			//m.put("<unique identifier 1>", get<UniqueIdentifier1>());
			//m.put("<unique identifier 1>", get<UniqueIdentifier1>());

			return m;
	}

}
