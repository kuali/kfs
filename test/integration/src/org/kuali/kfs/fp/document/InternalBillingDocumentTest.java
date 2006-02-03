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
package org.kuali.module.financial.document;

import java.util.ArrayList;
import java.util.List;

import org.kuali.KeyConstants;
import org.kuali.core.bo.SourceAccountingLine;
import org.kuali.core.bo.TargetAccountingLine;
import org.kuali.core.document.TransactionalDocument;
import org.kuali.core.document.TransactionalDocumentTestBase;
import org.kuali.core.exceptions.DocumentAuthorizationException;
import org.kuali.core.exceptions.ValidationException;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.test.parameters.DocumentParameter;
import org.kuali.test.parameters.TransactionalDocumentParameter;

/**
 * This class is used to test InternalBillingDocument.
 * 
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class InternalBillingDocumentTest extends TransactionalDocumentTestBase {
    public static final String COLLECTION_NAME = "InternalBillingDocumentTest.collection1";
    public static final String USER_NAME = "user1";
    public static final String DOCUMENT_PARAMETER = "internalBillingDocumentParameter1";
    private static final String SL_ACCT1 = "sourceLine2";
    private static final String TL_ACCT1 = "targetLine2";
    private static final String SL_ACCT2 = "sourceLine3";
    private static final String TL_ACCT2 = "targetLine3";
    public static final String SERIALIZED_LINE_PARAMTER = "serializedLine1";

    /**
     * Get names of fixture collections test class is using.
     *
     * @return String[]
     */
    public String[] getFixtureCollectionNames() {
        return new String[] { COLLECTION_NAME };
    }

    /**
     * 
     * @see org.kuali.core.document.DocumentTestBase#getDocumentParameterFixture()
     */
    public DocumentParameter getDocumentParameterFixture() {
        return (TransactionalDocumentParameter) getFixtureEntryFromCollection(COLLECTION_NAME, DOCUMENT_PARAMETER).createObject();
    }

    /**
     * 
     * @see org.kuali.core.document.TransactionalDocumentTestBase#getTargetAccountingLineParametersFromFixtures()
     */
    public List getTargetAccountingLineParametersFromFixtures() {
        ArrayList list = new ArrayList();
        list.add(getFixtureEntryFromCollection(COLLECTION_NAME, TL_ACCT1).createObject());
        list.add(getFixtureEntryFromCollection(COLLECTION_NAME, TL_ACCT2).createObject());
        list.add(getFixtureEntryFromCollection(COLLECTION_NAME, TL_ACCT1).createObject());
        return list;
    }

    /**
     * 
     * @see org.kuali.core.document.TransactionalDocumentTestBase#getSourceAccountingLineParametersFromFixtures()
     */
    public List getSourceAccountingLineParametersFromFixtures() {
        ArrayList list = new ArrayList();
        list.add(getFixtureEntryFromCollection(COLLECTION_NAME, SL_ACCT1).createObject());
        list.add(getFixtureEntryFromCollection(COLLECTION_NAME, SL_ACCT2).createObject());
        list.add(getFixtureEntryFromCollection(COLLECTION_NAME, SL_ACCT1).createObject());
        return list;
    }

    /**
     * 
     * @see org.kuali.core.document.TransactionalDocumentTestBase#getUserName()
     */
    public String getUserName() {
        return (String) getFixtureEntryFromCollection(COLLECTION_NAME, USER_NAME).createObject();
    }

    /**
     * provides default count for Pending entry count.
     *
     * @see org.kuali.core.document.TransactionalDocumentTestBase#getExpectedPrePeCount()
     * @return int
     */
    protected int getExpectedPrePeCount() {
        return 12;
    }
    ///////////////////////////////////////////////////////////////////////////
    // Start of Test Methods                                                 //
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Overrides the parent to do nothing since the IB doesn't set the posting period in 
     * the record it stores.  This test doesn't apply to this type of document.
     */
    public final void testConvertIntoCopy_invalidYear() throws Exception {
        //do nothing to pass
    }
    
    /**
     * Overrides the parent to do nothing since the IB doesn't set the posting period in 
     * the record it stores.  This test doesn't apply to this type of document.
     */
    public final void testConvertIntoErrorCorrection_invalidYear() throws Exception {
        //do nothing to pass
    }
    
    /**
     * Tests authoriation by trying to approve a
     * <code>{@link Document}</code>
     *
     * @exception Exception
     */
    public final void testApprove_addAccessibleAccount_ChangingTotals() 
        throws Exception {
        TransactionalDocument retrieved;
        TransactionalDocument original;
        String docId;
        
        // switch user to WESPRICE, build and route document with 
        // accountingLines
        changeCurrentUser( getInitialUserName() );
        original = ( TransactionalDocument )buildDocument();
        routeDocument( original );
        docId = original.getFinancialDocumentNumber();

        // switch user to another user, add accountingLines for accounts not 
        // controlled by this user
        changeCurrentUser( getTestUserName() );
        retrieved = ( TransactionalDocument )
            getDocumentService().getByDocumentHeaderId( docId );
        retrieved.addSourceAccountingLine( getSourceAccountingLineAccessibleAccount() );
        retrieved.addTargetAccountingLine( getTargetAccountingLineAccessibleAccount() );
        
        boolean failedAsExpected = false;
        try {
            approveDocument( retrieved );
        }
        catch( ValidationException e ) {
            failedAsExpected = true;
        }
        catch( DocumentAuthorizationException dae) {
            // this means that the workflow status didn't change in time for the check, so this is
            // an expected exception
            failedAsExpected = true;
        }
        assertTrue( failedAsExpected );
    }

    /**
     * Tests adding a <code>{@link SourceAccountingLine}</code> instance with
     * inaccessible account characteristics after approving the 
     * <code>{@link TransactionalDocument}</code>
     *
     * @exception Exception
     */
    public final void testApprove_addInaccessibleAccount_sourceLine()
        throws Exception {
        // switch user to WESPRICE, build and route document with 
        // accountingLines
        TransactionalDocument original;
        TransactionalDocument retrieved;
        String docId;

        changeCurrentUser( getInitialUserName() );
        original = ( TransactionalDocument )buildDocument();
        routeDocument( original );
        docId = original.getFinancialDocumentNumber();
        
        // switch user to AHORNICK, add sourceAccountingLine for account not controlled by this user
        // (and add a balancing targetAccountingLine for an accessible account)
        changeCurrentUser( getTestUserName() );
        retrieved = ( TransactionalDocument )
            getDocumentService().getByDocumentHeaderId( docId );
        retrieved.addSourceAccountingLine( getSourceAccountingLineInaccessibleAccount() );
        retrieved.addTargetAccountingLine( getTargetAccountingLineAccessibleAccount() );
        
        // approve document, wait for failure b/c totals have changed
        boolean failedAsExpected = false;
        try {
            approveDocument( retrieved );
        }
        catch( ValidationException e ) {
            failedAsExpected = true;
        }
        catch( DocumentAuthorizationException dae) {
            // this means that the workflow status didn't change in time for the check, so this is
            // an expected exception
            failedAsExpected = true;
        }
        assertTrue( failedAsExpected );
    }

    public final void testApprove_addInaccessibleAccount_targetLine() 
        throws Exception {
        TransactionalDocument retrieved;
        TransactionalDocument original;
        String docId;

        // switch user to WESPRICE, build and route document with 
        // accountingLines
        changeCurrentUser( getInitialUserName() );
        original = ( TransactionalDocument )buildDocument();
        routeDocument( original );
        docId = original.getFinancialDocumentNumber();

        // switch user to AHORNICK, add targetAccountingLine for accounts not 
        // controlled by this user
        // (and add a balancing sourceAccountingLine for an accessible account)
        changeCurrentUser( getTestUserName() );
        retrieved = ( TransactionalDocument )
            getDocumentService().getByDocumentHeaderId( docId );
        retrieved.addTargetAccountingLine( getTargetAccountingLineInaccessibleAccount() );
        retrieved.addSourceAccountingLine( getSourceAccountingLineAccessibleAccount() );

        // approve document, wait for failure
        boolean failedAsExpected = false;
        try {
            approveDocument( retrieved );
        }
        catch (ValidationException e) {
            failedAsExpected = true;
        } 
        catch( DocumentAuthorizationException dae) {
            // this means that the workflow status didn't change in time for the check, so this is
            // an expected exception
            failedAsExpected = true;
        }
        assertTrue(failedAsExpected);
    }

    public final void testApprove_deleteAccessibleAccount() throws Exception {
        // switch user to WESPRICE, build and route document with 
        // accountingLines
        TransactionalDocument retrieved;
        TransactionalDocument original;
        String docId;
        
        changeCurrentUser( getInitialUserName() );
        original = ( TransactionalDocument )buildDocument();
        routeDocument( original );
        docId = original.getFinancialDocumentNumber();

        // switch user to AHORNICK, delete sourceAccountingLine for accounts 
        // controlled by this user
        // (and delete matching targetAccountingLine, for balance)
        changeCurrentUser( getTestUserName() );
        retrieved = ( TransactionalDocument )
            getDocumentService().getByDocumentHeaderId( docId );
        deleteSourceAccountingLine(retrieved, 0);
        deleteTargetAccountingLine(retrieved, 0);
        
        // approve document, wait for failure b/c totals have changed
        boolean failedAsExpected = false;
        try {
            approveDocument( retrieved );
        }
        catch (ValidationException e) {
            failedAsExpected = true;
        }
        catch( DocumentAuthorizationException dae) {
            // this means that the workflow status didn't change in time for the check, so this is
            // an expected exception
            failedAsExpected = true;
        }
        assertTrue(failedAsExpected);
    }

    public final void testApprove_deleteInaccessibleAccount_sourceLine() 
        throws Exception {
        // switch user to WESPRICE, build and route document with 
        // accountingLines
        TransactionalDocument retrieved;
        TransactionalDocument original;
        String docId;
        
        changeCurrentUser( getInitialUserName() );
        original = ( TransactionalDocument )buildDocument();
        routeDocument( original );
        docId = original.getFinancialDocumentNumber();

        // switch user to AHORNICK, delete sourceAccountingLines for accounts 
        // not controlled by this user
        // (and delete matching accessible targetLine, for balance)
        changeCurrentUser( getTestUserName() );
        retrieved = ( TransactionalDocument )
            getDocumentService().getByDocumentHeaderId( docId );
        deleteSourceAccountingLine(retrieved, 1);
        deleteTargetAccountingLine(retrieved, 0);

        // approve document, wait for failure
        boolean failedAsExpected = false;
        try {
            approveDocument( retrieved );
        }
        catch (ValidationException e) {
            failedAsExpected = GlobalVariables.getErrorMap().containsMessageKey(
                    KeyConstants.ERROR_ACCOUNTINGLINE_INACCESSIBLE_DELETE);
        }
        catch( DocumentAuthorizationException dae) {
            // this means that the workflow status didn't change in time for the check, so this is
            // an expected exception
            failedAsExpected = true;
        }
        assertTrue(failedAsExpected);
    }

    public final void testApprove_deleteInaccessibleAccount_targetLine() 
        throws Exception {
        TransactionalDocument retrieved;
        TransactionalDocument original;
        String docId;
        
        // switch user to WESPRICE, build and route document with 
        // accountingLines
        changeCurrentUser( getInitialUserName() );
        original = ( TransactionalDocument )buildDocument();
        routeDocument( original );
        docId = original.getFinancialDocumentNumber();

        // switch user to AHORNICK, delete targetAccountingLine for accounts not controlled by this user
        // (and delete matching accessible sourceLine, for balance)
        changeCurrentUser( getTestUserName() );
        retrieved = ( TransactionalDocument )
            getDocumentService().getByDocumentHeaderId( docId );
        deleteTargetAccountingLine(retrieved, 1);
        deleteSourceAccountingLine(retrieved, 0);

        // approve document, wait for failure
        boolean failedAsExpected = false;
        try {
            approveDocument( retrieved );
        }
        catch (ValidationException e) {
            failedAsExpected = GlobalVariables.getErrorMap().containsMessageKey(
                    KeyConstants.ERROR_ACCOUNTINGLINE_INACCESSIBLE_DELETE);
        }
        catch( DocumentAuthorizationException dae) {
            // this means that the workflow status didn't change in time for the check, so this is
            // an expected exception
            failedAsExpected = true;
        }
        assertTrue(failedAsExpected);
    }

    public final void testApprove_deleteLastAccessibleAccount() 
        throws Exception {
        // switch user to WESPRICE, build and route document with 
        // accountingLines
        TransactionalDocument retrieved;
        TransactionalDocument original;
        String docId;
        
        changeCurrentUser( getInitialUserName() );
        original = ( TransactionalDocument )buildDocument();
        routeDocument( original );
        docId = original.getFinancialDocumentNumber();

        // switch user to AHORNICK, delete all accountingLines for that user
        changeCurrentUser( getTestUserName() );
        retrieved = ( TransactionalDocument )
            getDocumentService().getByDocumentHeaderId( docId );
        deleteSourceAccountingLine(retrieved, 2);
        deleteSourceAccountingLine(retrieved, 0);

        deleteTargetAccountingLine(retrieved, 2);
        deleteTargetAccountingLine(retrieved, 0);

        // approve document, wait for failure
        boolean failedAsExpected = false;
        try {
            approveDocument( retrieved );
        }
        catch (ValidationException e) {
            failedAsExpected = GlobalVariables.getErrorMap().containsMessageKey(
                    KeyConstants.ERROR_ACCOUNTINGLINE_LASTACCESSIBLE_DELETE);
        }
        catch( DocumentAuthorizationException dae) {
            // this means that the workflow status didn't change in time for the check, so this is
            // an expected exception
            failedAsExpected = true;
        }
        assertTrue(failedAsExpected);
    }


    public final void testApprove_updateAccessibleAccount() throws Exception {
        // switch user to WESPRICE, build and route document with 
        // accountingLines
        TransactionalDocument retrieved;
        TransactionalDocument original;
        String docId;

        changeCurrentUser( getInitialUserName() );
        original = ( TransactionalDocument )buildDocument();
        routeDocument( original );
        docId = original.getFinancialDocumentNumber();

        // switch user to AHORNICK, update sourceAccountingLine for accounts 
        // controlled by this user
        // (and delete update targetAccountingLine, for balance)
        changeCurrentUser( getTestUserName() );
        retrieved = ( TransactionalDocument )
            getDocumentService().getByDocumentHeaderId( docId );
        
        //make sure totals don't change
        KualiDecimal originalSourceLineAmt = retrieved.getSourceAccountingLine(0).getAmount();
        KualiDecimal originalTargetLineAmt = retrieved.getTargetAccountingLine(0).getAmount();
        KualiDecimal newSourceLineAmt = originalSourceLineAmt.divide(new KualiDecimal(2));
        KualiDecimal newTargetLineAmt = originalTargetLineAmt.divide(new KualiDecimal(2));
        
        //update existing lines with new amount which equals orig divided by 2 
        updateSourceAccountingLine(retrieved, 0, newSourceLineAmt.toString());
        updateTargetAccountingLine(retrieved, 0, newTargetLineAmt.toString());
        
        //add in new lines and change amounts
        TargetAccountingLine newTargetLine = getTargetAccountingLineAccessibleAccount();
        newTargetLine.setAmount(newTargetLineAmt);
        SourceAccountingLine newSourceLine = getSourceAccountingLineAccessibleAccount();
        newSourceLine.setAmount(newSourceLineAmt);
        retrieved.addTargetAccountingLine(newTargetLine);
        retrieved.addSourceAccountingLine(newSourceLine);

        try {
            approveDocument( retrieved );
        } catch(DocumentAuthorizationException dae) {
            // this means that the workflow status didn't change in time for the check, so this is
            // an expected exception
        }
    }

    public final void testApprove_updateInaccessibleAccount_sourceLine() 
        throws Exception {
        TransactionalDocument retrieved;
        TransactionalDocument original;
        String docId;

        // switch user to WESPRICE, build and route document with 
        // accountingLines
        changeCurrentUser( getInitialUserName() );
        original = ( TransactionalDocument )buildDocument();
        routeDocument( original );
        docId = original.getFinancialDocumentNumber();

        // switch user to AHORNICK, update sourceAccountingLines for accounts 
        // not controlled by this user
        // (and update matching accessible targetLine, for balance)
        changeCurrentUser( getTestUserName() );
        retrieved = ( TransactionalDocument )
            getDocumentService().getByDocumentHeaderId( docId );
        updateSourceAccountingLine(retrieved, 1, "3.14");
        updateTargetAccountingLine(retrieved, 0, "3.14");

        // approve document, wait for failure
        boolean failedAsExpected = false;
        try {
            approveDocument( retrieved );
        }
        catch (ValidationException e) {
            failedAsExpected = GlobalVariables.getErrorMap().containsMessageKey(
                    KeyConstants.ERROR_ACCOUNTINGLINE_INACCESSIBLE_UPDATE);
        }
        catch( DocumentAuthorizationException dae) {
            // this means that the workflow status didn't change in time for the check, so this is
            // an expected exception
            failedAsExpected = true;
        }
        assertTrue(failedAsExpected);
    }

    public final void testApprove_updateInaccessibleAccount_targetLine() 
        throws Exception {
        // switch user to WESPRICE, build and route document with 
        // accountingLines
        TransactionalDocument retrieved;
        TransactionalDocument original;
        String docId;

        changeCurrentUser( getInitialUserName() );
        original = ( TransactionalDocument )buildDocument();
        routeDocument(  original );
        docId = original.getFinancialDocumentNumber();

        // switch user to AHORNICK, update targetAccountingLine for accounts 
        // not controlled by this user
        // (and update matching accessible sourceLine, for balance)
        changeCurrentUser( getTestUserName() );
        retrieved = ( TransactionalDocument )
            getDocumentService().getByDocumentHeaderId( docId );
        updateTargetAccountingLine(retrieved, 1, "2.81");
        updateSourceAccountingLine(retrieved, 0, "2.81");

        // approve document, wait for failure
        boolean failedAsExpected = false;
        try {
            approveDocument( retrieved );
        }
        catch (ValidationException e) {
            failedAsExpected = GlobalVariables.getErrorMap().containsMessageKey(
                    KeyConstants.ERROR_ACCOUNTINGLINE_INACCESSIBLE_UPDATE);
        }
        catch( DocumentAuthorizationException dae) {
            // this means that the workflow status didn't change in time for the check, so this is
            // an expected exception
            failedAsExpected = true;
        }
        assertTrue(failedAsExpected);
    }
    ///////////////////////////////////////////////////////////////////////////
    // End of Test Methods                                                   //
    ///////////////////////////////////////////////////////////////////////////
}
