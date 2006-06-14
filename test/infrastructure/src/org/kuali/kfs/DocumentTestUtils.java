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
package org.kuali.test;

import org.kuali.core.bo.AccountingLine;
import org.kuali.core.bo.SourceAccountingLine;
import org.kuali.core.bo.TargetAccountingLine;
import org.kuali.core.bo.user.KualiUser;
import org.kuali.core.document.DocumentNote;
import org.kuali.core.exceptions.InfrastructureException;
import org.kuali.core.util.KualiDecimal;
import org.kuali.module.financial.bo.InternalBillingItem;

/**
 * DocumentTestUtils
 * 
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class DocumentTestUtils {
    public static SourceAccountingLine createSourceLine(String documentHeaderId, String chartOfAccounts, String accountNumber, String subAccountNumber, String financialObjectCode, String financialSubObjectCode, String projectCode, int linePostingYear, KualiDecimal lineAmount, int sequenceNumber, String referenceNumber, String referenceTypeCode, String balanceTypeCode, String referenceOriginCode, String debitCreditCode, String encumbranceUpdateCode, String objectTypeCode) {

        return (SourceAccountingLine) createLine(SourceAccountingLine.class, documentHeaderId, chartOfAccounts, accountNumber, subAccountNumber, financialObjectCode, financialSubObjectCode, projectCode, linePostingYear, lineAmount, sequenceNumber, referenceNumber, referenceTypeCode, balanceTypeCode, referenceOriginCode, debitCreditCode, encumbranceUpdateCode, objectTypeCode);
    }

    public static TargetAccountingLine createTargetLine(String documentHeaderId, String chartOfAccounts, String accountNumber, String subAccountNumber, String financialObjectCode, String financialSubObjectCode, String projectCode, int linePostingYear, KualiDecimal lineAmount, int sequenceNumber, String referenceNumber, String referenceTypeCode, String balanceTypeCode, String referenceOriginCode, String debitCreditCode, String encumbranceUpdateCode, String objectTypeCode) {
        return (TargetAccountingLine) createLine(TargetAccountingLine.class, documentHeaderId, chartOfAccounts, accountNumber, subAccountNumber, financialObjectCode, financialSubObjectCode, projectCode, linePostingYear, lineAmount, sequenceNumber, referenceNumber, referenceTypeCode, balanceTypeCode, referenceOriginCode, debitCreditCode, encumbranceUpdateCode, objectTypeCode);
    }

    /**
     * Using this allows you to create an AccountingLine in one line of code for testing, rather than having to go through all the
     * object and sub-object setups by hand each time.
     * 
     * Note that this method returns an object with all of the reference objects populated from the parent primitives.
     * 
     * @param lineClass
     * @param documentHeaderId
     * @param chartOfAccounts
     * @param accountNumber
     * @param subAccountNumber
     * @param financialObjectCode
     * @param financialSubObjectCode
     * @param projectCode
     * @param linePostingYear
     * @param lineAmount
     * @param sequenceNumber
     * @param referenceNumber
     * @param referenceTypeCode
     * @param balanceTypeCode
     * @param referenceOriginCode
     * @return AccountingLine - returns a fully populated AccountingLine object
     * 
     * @throws Exception
     */
    private static AccountingLine createLine(Class lineClass, String documentHeaderId, String chartOfAccounts, String accountNumber, String subAccountNumber, String financialObjectCode, String financialSubObjectCode, String projectCode, int linePostingYear, KualiDecimal lineAmount, int sequenceNumber, String referenceNumber, String referenceTypeCode, String balanceTypeCode, String referenceOriginCode, String debitCreditCode, String encumbranceUpdateCode, String objectTypeCode) {

        AccountingLine line;

        line = createLineHelper(lineClass, documentHeaderId, chartOfAccounts, accountNumber, subAccountNumber, financialObjectCode, financialSubObjectCode, projectCode, linePostingYear, lineAmount, sequenceNumber, referenceNumber, referenceTypeCode, balanceTypeCode, referenceOriginCode, debitCreditCode, encumbranceUpdateCode, objectTypeCode);

        // have the persistence service refresh all the reference objects from the
        // primitives primary key values.
        line.refresh();

        return line;
    }


    /**
     * Using this allows you to create an AccountingLine in one line of code for testing, rather than having to go through all the
     * object and sub-object setups by hand each time.
     * 
     * This accountingLine returned does NOT have the reference objects populated, so they will be either null or empty objects.
     * 
     * @param lineClass
     * @param documentHeaderId
     * @param chartOfAccounts
     * @param accountNumber
     * @param subAccountNumber
     * @param financialObjectCode
     * @param financialSubObjectCode
     * @param projectCode
     * @param linePostingYear
     * @param lineAmount
     * @param sequenceNumber
     * @param referenceNumber
     * @param referenceTypeCode
     * @param balanceTypeCode
     * @param referenceOriginCode
     * @return AccountingLine - returns a fully populated AccountingLine object
     * 
     * @throws Exception
     */
    public static AccountingLine createLineHelper(Class lineClass, String documentHeaderId, String chartOfAccounts, String accountNumber, String subAccountNumber, String financialObjectCode, String financialSubObjectCode, String projectCode, int linePostingYear, KualiDecimal lineAmount, int sequenceNumber, String referenceNumber, String referenceTypeCode, String balanceTypeCode, String referenceOriginCode, String debitCreditCode, String encumbranceUpdateCode, String objectTypeCode) {

        Integer postingYear = new Integer(linePostingYear);
        AccountingLine line = null;
        try {
            line = (AccountingLine) lineClass.newInstance();
        }
        catch (InstantiationException e) {
            throw new InfrastructureException("unable to create line instance", e);
        }
        catch (IllegalAccessException e) {
            throw new InfrastructureException("unable to create line instance", e);
        }

        line.setFinancialDocumentNumber(documentHeaderId);
        line.setPostingYear(postingYear);
        line.setSequenceNumber(new Integer(sequenceNumber));
        line.setChartOfAccountsCode(chartOfAccounts);
        line.setAccountNumber(accountNumber);
        line.setSubAccountNumber(subAccountNumber);
        line.setFinancialObjectCode(financialObjectCode);
        line.setFinancialSubObjectCode(financialSubObjectCode);
        line.setProjectCode(projectCode);
        line.setBalanceTypeCode(balanceTypeCode);
        line.setObjectTypeCode(objectTypeCode);
        line.setReferenceOriginCode(referenceOriginCode);
        line.setReferenceNumber(referenceNumber);
        line.setReferenceTypeCode(referenceTypeCode);
        line.setDebitCreditCode(debitCreditCode);
        line.setEncumbranceUpdateCode(encumbranceUpdateCode);
        line.setAmount(lineAmount);

        return line;
    }


    /**
     * @param quantity
     * @param stockDescription
     * @param stockNumber
     * @param unitAmount
     * @param unitOfMeasureCode
     * @return new InternalBillingItem initialized with the given values
     */
    public static InternalBillingItem createBillingItem(Integer quantity, String stockDescription, String stockNumber, Double unitAmount, String unitOfMeasureCode) {
        InternalBillingItem item = new InternalBillingItem();

        item.setItemQuantity(quantity);
        // item.setItemServiceDate( timestamp );
        item.setItemStockDescription(stockDescription);
        item.setItemStockNumber(stockNumber);
        item.setItemUnitAmount(new KualiDecimal(unitAmount.toString()));
        item.setUnitOfMeasureCode(unitOfMeasureCode);

        return item;
    }


    /**
     * @param documentHeaderId
     * @param documentNoteAuthor
     * @param documentNoteText
     * @return new DocumentNote initialized with the given values
     */
    public static DocumentNote createDocumentNote(String documentHeaderId, KualiUser documentNoteAuthor, String documentNoteText) {
        java.util.Date now = new java.util.Date();
        DocumentNote documentNote = new DocumentNote();
        documentNote.setFinancialDocumentNumber(documentHeaderId);
        documentNote.setFinDocumentAuthorUniversalId(documentNoteAuthor.getPersonUniversalIdentifier());
        documentNote.setFinancialDocumentNoteText(documentNoteText);
        documentNote.setFinDocNotePostedDttmStamp(new java.sql.Timestamp(now.getTime()));

        return documentNote;
    }
}
