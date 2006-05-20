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

package org.kuali.module.financial.bo;
import org.kuali.Constants;
import org.kuali.core.bo.AccountingLine;
import org.kuali.core.bo.AccountingLineParserBase;
import org.kuali.core.bo.BusinessObject;
import org.kuali.core.bo.SourceAccountingLine;
import org.kuali.core.document.TransactionalDocument;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.exceptions.TooFewFieldsException;
import org.kuali.module.chart.bo.codes.BalanceTyp;
import org.kuali.module.financial.document.JournalVoucherDocument;

/**
 * Base class for parsing serialized <code>{@link TransactionalDocument}</code>
 * <code>{@link AccountingLine}</code> instances for import into the 
 * <code>{@link TransactionalDocument}</code>.<br/>
 *
 * <p><code>{@link JournalVoucherAccountingLineParser}</code> requires a 
 * <code>{@link BalanceTyp}</code> to determine the number of expected
 * fields it will parse from the serialized CSV input. This is a special
 * case requires <code>{@link JournalVoucherAccountingLineParser} use
 * accessor methods to handle passing a <code>{@link BalanceTyp}</code>
 * around during a <code>{@link JournalVoucherAccountingLineParser}</code> 
 * instance.
 *
 * @author Kuali Financial Transactions Team (kualidev@oncourse.iu.edu)
 */
public class JournalVoucherAccountingLineParser extends AccountingLineParserBase {
	
    private static final KualiDecimal ZERO        = new KualiDecimal("0.00");
    private static final String EXTERNAL_ENCUMBRANCE = "EX";

    private static final int EXTERNAL_ENCUMBRANCE_EXPECTED_FIELDS   = 15;
    private static final int OFFSET_GENERATION_EXPECTED_FIELDS      = 12;
    private static final int NON_OFFSET_GENERATION_EXPECTED_FIELDS  = 11;

    private static final int CHART_IDX            = 0;
    private static final int ACCOUNT_IDX          = 1;
    private static final int SUBACCOUNT_IDX       = 2;
    private static final int OBJECT_CODE_IDX      = 3;
    private static final int SUBOBJECT_CODE_IDX   = 4;
    private static final int PROJECT_CODE_IDX     = 5;
    private static final int OBJECT_TYPE_CODE_IDX = 6;
    private static final int ORGANIZATION_REFERENCE_ID_IDX = 7;
    private static final int BUDGET_YEAR_IDX      = 8;
    private static final int OVERRIDE_CODE_IDX    = 9;
    private static final int DEBIT_AMOUNT_IDX     = 10;
    private static final int CREDIT_AMOUNT_IDX    = 11;
    private static final int REF_ORIGIN_CODE_IDX  = 12;
    private static final int REF_NUMBER_IDX       = 13;
    private static final int REF_TYPE_CODE_IDX    = 14;
    
    private JournalVoucherDocument _document;

    /**
     * @see AccountingLineParserBase#parseAccountingLine
     */
    public AccountingLine parseAccountingLine( String currentLine,
                                               TransactionalDocument document,
                                               boolean isSource )
        throws TooFewFieldsException
    {
        AccountingLine retval = new SourceAccountingLine();
        setJournalVoucherDocument( ( JournalVoucherDocument )document );

        String[] accountingLineData =
            currentLine.split("\\\"?\\s*,\\s*\\\"?");
        int fieldCount = accountingLineData.length;

        if (fieldCount < getExpectedFieldCount()) {
            throw new TooFewFieldsException(getExpectedFieldCount(), fieldCount);
        }

        Integer fiscalYear    = document.getPostingYear();
        String coa            = parseChartCode( accountingLineData );
        String account        = parseAccountNumber( accountingLineData );
        String subAccount     = parseSubAccountNumber( accountingLineData );
        String objectCode     = parseObjectCode( accountingLineData );
        String subObjectCode  = parseSubObjectCode( accountingLineData );
        String projectCode    = parseProjectCode( accountingLineData );
        String objectTypeCode = parseObjectTypeCode( accountingLineData );
        String orgRefId       = parseOrganizationReferenceId( accountingLineData );
        String budgetYear     = parseBudgetYear( accountingLineData );
        String overrideCode   = parseOverrideCode( accountingLineData );
        KualiDecimal amount;
        if( getExpectedFieldCount() > NON_OFFSET_GENERATION_EXPECTED_FIELDS ) {
            StringBuffer debitOrCredit = new StringBuffer();
            amount = parseDebitCreditAmount( accountingLineData, debitOrCredit );
            retval.setDebitCreditCode( debitOrCredit.toString() );
        }
        else {
            amount = parseDebitAmount(accountingLineData);
        }
        String refOriginCode  = null;
        String refNumber      = null;
        String refTypeCode    = null;

        if( EXTERNAL_ENCUMBRANCE_EXPECTED_FIELDS == getExpectedFieldCount()) {
            refOriginCode = parseRefOriginCode( accountingLineData );
            refNumber     = parseRefNumber( accountingLineData );
            refTypeCode   = parseRefTypeCode( accountingLineData );
        }

        retval.setBalanceTypeCode( getBalanceType().getCode() );
        retval.setFinancialDocumentNumber(document.getFinancialDocumentNumber());
        retval.setPostingYear(fiscalYear);
        retval.setChartOfAccountsCode( coa );
        retval.setAccountNumber( account );
        retval.setSubAccountNumber(subAccount);
        retval.setFinancialObjectCode( objectCode );
        retval.setFinancialSubObjectCode( subObjectCode );
        retval.setObjectTypeCode( objectTypeCode );
        retval.setOrganizationReferenceId( orgRefId );
        retval.setProjectCode( projectCode );
        retval.setBudgetYear(budgetYear);
        retval.setOverrideCode(overrideCode);
        retval.setAmount(amount);
        retval.setReferenceOriginCode( refOriginCode );
        retval.setReferenceTypeCode( refTypeCode );
        retval.setReferenceNumber( refNumber );

        ((BusinessObject) retval).refresh();

        return retval;
    }

    /**
     * Extracts the Chart Code from the parsed CSV data.
     *
     * @param lineData <code>{@link String}[]</code> of parsed CSV data.
     * @return <code>{@link String}</code> instance of the chart code.
     */
    private String parseChartCode( String[] lineData ) {
        return parseField( lineData, CHART_IDX );
    }

    /**
     * Extracts the Account Number from the parsed CSV data.
     *
     * @param lineData <code>{@link String}[]</code> of parsed CSV data.
     * @return <code>{@link String}</code> instance of the account number.
     */
    private String parseAccountNumber( String[] lineData ) {
        return parseField( lineData, ACCOUNT_IDX );
    }

    /**
     * Extracts the SubAccount Number from the parsed CSV data.
     *
     * @param lineData <code>{@link String}[]</code> of parsed CSV data.
     * @return <code>{@link String}</code> instance of the subaccount number.
     */
    private String parseSubAccountNumber( String[] lineData ) {
        return parseField( lineData, SUBACCOUNT_IDX );
    }

    /**
     * Extracts the Object Code from the parsed CSV data.
     *
     * @param lineData <code>{@link String}[]</code> of parsed CSV data.
     * @return <code>{@link String}</code> instance of the object code.
     */
    private String parseObjectCode( String[] lineData ) {
        return parseField( lineData, OBJECT_CODE_IDX );
    }

    /**
     * Extracts the SubObject Code from the parsed CSV data.
     *
     * @param lineData <code>{@link String}[]</code> of parsed CSV data.
     * @return <code>{@link String}</code> instance of the subobject code.
     */
    private String parseSubObjectCode( String[] lineData ) {
        return parseField( lineData, SUBOBJECT_CODE_IDX );
    }

    /**
     * Extracts the Project Code from the parsed CSV data.
     *
     * @param lineData <code>{@link String}[]</code> of parsed CSV data.
     * @return <code>{@link String}</code> instance of the project code.
     */
    private String parseProjectCode( String[] lineData ) {
        return parseField( lineData, PROJECT_CODE_IDX );
    }

    /**
     * Extracts the ObjectType Code from the parsed CSV data.
     *
     * @param lineData <code>{@link String}[]</code> of parsed CSV data.
     * @return <code>{@link String}</code> instance of the objecttype code.
     */
    private String parseObjectTypeCode( String[] lineData ) {
        return parseField( lineData, OBJECT_TYPE_CODE_IDX );
    }

    /**
     * Extracts the Organization Reference Id from the parsed CSV data.
     *
     * @param lineData <code>{@link String}[]</code> of parsed CSV data.
     * @return <code>{@link String}</code> instance of the organization 
     * reference id.
     */
    private String parseOrganizationReferenceId( String[] lineData ) {
        return parseField( lineData, ORGANIZATION_REFERENCE_ID_IDX );
    }

    /**
     * Extracts the Debit Amount from the parsed CSV data.
     *
     * @param lineData <code>{@link String}[]</code> of parsed CSV data.
     * @return <code>{@link KualiDecimal}</code> instance of the debit amount
     */
    private KualiDecimal parseDebitAmount( String[] lineData ) {
        return new KualiDecimal( parseField( lineData, DEBIT_AMOUNT_IDX ) );
    }

    /**
     * Extracts the Credit Amount from the parsed CSV data.
     *
     * @param lineData <code>{@link String}[]</code> of parsed CSV data.
     * @return <code>{@link KualiDecimal}</code> instance of the credit amount
     */
    private KualiDecimal parseCreditAmount( String[] lineData ) {
        return new KualiDecimal( parseField( lineData, CREDIT_AMOUNT_IDX ) );
    }

    /**
     * Extracts the Amount from the parsed CSV data. The amount could be a 
     * debit, credit or neither. This method uses 
     * <code>parseDebitAmount()</code> and <code>parseCreditAmount</code>.
     *
     * @param lineData <code>{@link String}[]</code> of parsed CSV data.
     * @return <code>{@link KualiDecimal}</code> instance of the amount.
     */
    private KualiDecimal parseDebitCreditAmount( String[] lineData,
                                                 StringBuffer debitOrCredit ) {
        KualiDecimal amount = null;
        if( parseDebitAmount( lineData ).compareTo( ZERO ) != 0 ) {
            amount = parseDebitAmount( lineData );
            debitOrCredit.append( Constants.GL_DEBIT_CODE ); 
        }
        else {
            amount = parseCreditAmount( lineData );
            debitOrCredit.append( Constants.GL_CREDIT_CODE ); 
        }
        return amount;
    }

    private String parseOverrideCode(String[] lineData) {
        return parseField( lineData, OVERRIDE_CODE_IDX );
    }

    private String parseBudgetYear(String[] lineData) {
        return parseField( lineData, BUDGET_YEAR_IDX );
    }

    /**
     * Extracts the Chart Code from the parsed CSV data.
     *
     * @param lineData <code>{@link String}[]</code> of parsed CSV data.
     * @return <code>{@link String}</code> instance of the chart code.
     */
    private String parseRefOriginCode( String[] lineData ) {
        return parseField( lineData, REF_ORIGIN_CODE_IDX );
    }

    /**
     * Extracts the Chart Code from the parsed CSV data.
     *
     * @param lineData <code>{@link String}[]</code> of parsed CSV data.
     * @return <code>{@link String}</code> instance of the chart code.
     */ 
   private String parseRefNumber( String[] lineData ) {
        return parseField( lineData, REF_NUMBER_IDX );
    }

    /**
     * Extracts the Chart Code from the parsed CSV data.
     *
     * @param lineData <code>{@link String}[]</code> of parsed CSV data.
     * @return <code>{@link String}</code> instance of the chart code.
     */
    private String parseRefTypeCode( String[] lineData ) {
        return parseField( lineData, REF_TYPE_CODE_IDX );
    }
    
    /**
     * Retrieves serialized data from a <code>{@link String}[]</code> by 
     * index. 
     *
     * @param lineData <code>{@link String}[]</code> to grab data from.
     * @param fieldIndex <code>int</code> value of index.
     * @return <code>{@link String}</code> instance of some serialized data.
     */
    private String parseField( String[] lineData, int fieldIndex ) 
        throws ArrayIndexOutOfBoundsException{
        return lineData[fieldIndex];
    }
    
    /**
     * Accessor method to get the <code>{@link BalanceTyp} belonging to the
     * contained <code>{@link JournalVoucherDocument}</code> instance.
     *
     * @return <code>{@link BalanceTyp}</code> instance.
     */
    private BalanceTyp getBalanceType() {
        return getJournalVoucherDocument().getBalanceType();
    }

    /**
     * Accessor method to set the contained
     * <code>{@link JournalVoucherDocument}</code> instance.
     *
     * @param document <code>{@link JournalVoucherDocument}</code> instance.
     */ 
    private void setJournalVoucherDocument( JournalVoucherDocument document ) {
        _document = document;
    }
    
    /**
     * Accessor method to get the contained
     * <code>{@link JournalVoucherDocument}</code> instance.
     *
     * @return <code>{@link JournalVoucherDocument}</code> instance.
     */
    private JournalVoucherDocument getJournalVoucherDocument() {
        return _document;
    }

    /**
     * Determines the number of fields to be parsed.
     *
     * @return int number of fields expected.
     */
    public int getExpectedFieldCount() {
        if( getBalanceType().getCode().equals( EXTERNAL_ENCUMBRANCE ) ) {
            return EXTERNAL_ENCUMBRANCE_EXPECTED_FIELDS;
        }
        else if( getBalanceType().isFinancialOffsetGenerationIndicator() ) {
            return OFFSET_GENERATION_EXPECTED_FIELDS;
        }
        return NON_OFFSET_GENERATION_EXPECTED_FIELDS;
    }
}
