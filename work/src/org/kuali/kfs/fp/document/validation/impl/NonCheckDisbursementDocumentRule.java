/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.financial.rules;

import java.util.Set;
import java.util.TreeSet;

import org.kuali.core.bo.AccountingLine;
import org.kuali.core.document.Document;
import org.kuali.core.document.TransactionalDocument;

/**
 * Business rule(s) applicable to NonCheckDisbursement documents.
 * @author Kuali Financial Transactions Team (kualidev@oncourse.iu.edu)
 */
public class NonCheckDisbursementDocumentRule extends TransactionalDocumentRuleBase {
    private static final Set _invalidObjectTypes = new TreeSet();
    private static final Set _invalidSubTypes = new TreeSet();
    private static final Set _invalidSubFundGroupTypes = new TreeSet();
    
    private Document _document;
    private String _objectType;
    private String _objectSubType;
    private String _subFundGroup;
    
    static {
    	_invalidObjectTypes.add(OBJECT_TYPE_CODE.INCOME_NOT_CASH);
    	_invalidObjectTypes.add(OBJECT_TYPE_CODE.EXPENSE_NOT_EXPENDITURE);
    	
    	_invalidSubTypes.add(OBJECT_SUB_TYPE_CODE.BUDGET_ONLY);
    	_invalidSubTypes.add(OBJECT_SUB_TYPE_CODE.CASH);
    	_invalidSubTypes.add(OBJECT_SUB_TYPE_CODE.SUBTYPE_FUND_BALANCE);
    	_invalidSubTypes.add(OBJECT_SUB_TYPE_CODE.HOURLY_WAGES);
    	_invalidSubTypes.add(OBJECT_SUB_TYPE_CODE.PLANT);
    	_invalidSubTypes.add(OBJECT_SUB_TYPE_CODE.SALARIES);
    	_invalidSubTypes.add(OBJECT_SUB_TYPE_CODE.VALUATIONS_AND_ADJUSTMENTS);
    	_invalidSubTypes.add(OBJECT_SUB_TYPE_CODE.RESERVES);
    	_invalidSubTypes.add(OBJECT_SUB_TYPE_CODE.MANDATORY_TRANSFER);
    	_invalidSubTypes.add(OBJECT_SUB_TYPE_CODE.FRINGE_BEN);
    	_invalidSubTypes.add(OBJECT_SUB_TYPE_CODE.COST_RECOVERY_EXPENSE);
    	
    	_invalidSubFundGroupTypes.add(SUB_FUND_GROUP_CODE.RENEWAL_AND_REPLACEMENT);
    }
    

    protected Set getInvalidObjectTypes() {
	return _invalidObjectTypes;
    }

    protected Set getInvalidSubTypes() {
	return _invalidSubTypes;
    }

    protected Set getInvalidSubFundGroupTypes() {
	return _invalidSubFundGroupTypes;
    }

    protected void init( TransactionalDocument document,
			AccountingLine accountingLine ) {
	setDocument( document );

	setObjectType( accountingLine
		       .getObjectTypeCode() );
	
	
	// We do this check because object sub type code 
	// isn't required.
	if( accountingLine.getObjectCode() != null
	    && accountingLine.getObjectCode().getFinancialObjectSubType() != null ) {
	    setObjectSubType( accountingLine
			      .getObjectCode()
			      .getFinancialObjectSubType().getCode() );
	}
	else {
	    setObjectSubType( new String() );
	}

	// We do this because sub fund group isn't required.
	if( accountingLine.getAccount().getSubFundGroup().getSubFundGroupCode() != null ) {
	    setSubFundGroupCode
		( accountingLine.getAccount().getSubFundGroup().getSubFundGroupCode() );
	}
	else {
	    setSubFundGroupCode( new String() );
	}
    }

    protected void setObjectType( String o ) {
		_objectType = o;
    }
    
    protected String getObjectType() {
		return _objectType;
    }
    
    protected void setObjectSubType( String o ) {
		_objectSubType = o;
    }
    
    protected String getObjectSubType() {
		return _objectSubType;
    }
    
    protected void setSubFundGroupCode( String o ) {
		_subFundGroup = o;
    }
    
    protected String getSubFundGroupCode() {
		return _subFundGroup;
    }
    
    protected void setDocument( Document d ) {
		_document = d;
    }

    protected Document getDocument() {
		return _document;
    }
    
    protected TransactionalDocument getTransactionalDocument() {
		return ( TransactionalDocument )getDocument();
    }
    
    public boolean processCustomAddAccountingLineBusinessRules(TransactionalDocument document, AccountingLine accountingLine) {
        return super.processCustomAddAccountingLineBusinessRules( document, accountingLine ) && validateAccountingLine(document, accountingLine);
    }
    public boolean processCustomReviewAccountingLineBusinessRules(TransactionalDocument document, AccountingLine accountingLine) {
        return super.processCustomReviewAccountingLineBusinessRules( document, accountingLine) && validateAccountingLine(document, accountingLine);
    }
    
    protected boolean validateAccountingLine(TransactionalDocument document, AccountingLine accountingLine) {
        boolean retval = true;
		init( document, accountingLine );

        // What does this do? 
        //SpringServiceLocator.getPersistenceService().linkObjects(accountingLine);

        retval &= !getInvalidObjectTypes().contains( getObjectType() );
		retval &= !getInvalidSubTypes().contains( getObjectSubType() );
        retval &= !getInvalidSubFundGroupTypes().contains( getSubFundGroupCode() );
        
        return retval;
    }

    
}
