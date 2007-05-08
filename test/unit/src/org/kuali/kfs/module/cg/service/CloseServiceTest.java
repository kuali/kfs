/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.cg.service.impl;

import java.sql.Date;
import java.text.DateFormat;
import java.util.Vector;

import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.service.DocumentService;
import org.kuali.core.document.Document;
import org.kuali.core.exceptions.ValidationException;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.kfs.document.AccountingDocument;
import org.kuali.module.cg.bo.Award;
import org.kuali.module.cg.bo.Proposal;
import org.kuali.module.cg.bo.Close;
import org.kuali.module.cg.lookup.valuefinder.NextProposalNumberFinder;
import static org.kuali.module.financial.document.AccountingDocumentTestUtils.routeDocument;
import org.kuali.test.KualiTestBase;
import org.kuali.test.WithTestSpringContext;
import org.kuali.test.DocumentTestUtils;
import org.kuali.test.monitor.DocumentWorkflowStatusMonitor;
import org.kuali.test.monitor.ChangeMonitor;
import static org.kuali.test.fixtures.UserNameFixture.KHUNTLEY;
import org.kuali.test.fixtures.UserNameFixture;
import static org.kuali.rice.KNSServiceLocator.getDocumentService;
import org.kuali.workflow.WorkflowTestUtils;
import org.kuali.workflow.KualiWorkflowUtils;
import org.objectweb.jotm.Current;
import edu.iu.uis.eden.exception.WorkflowException;

@WithTestSpringContext(session = KHUNTLEY)
public class CloseServiceTest extends KualiTestBase {

    private static final String VALID_AWARD_STATUS_CODE = "R";
    private static final String INVALID_AWARD_STATUS_CODE = "U";

    private DateFormat dateFormat;
    private Date today;

    private int timeout = 0;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        dateFormat = DateFormat.getDateInstance();
        today = SpringServiceLocator.getDateTimeService().getCurrentSqlDateMidnight();

        timeout = Current.getCurrent().getDefaultTimeout();
        Current.getCurrent().setDefaultTimeout(timeout * 2);
    }

    protected void tearDown() throws Exception {
        super.tearDown();

        Current.getCurrent().setDefaultTimeout(timeout);
        timeout = 0;
    }

    public void testClose_awardEntryDateLessThanCloseOnOrBeforeDate() throws Exception {
        Date closeInitiatedDate = today; // must be today, close will abort if not
        
        Date closeCloseOnOrBeforeDate = new Date(dateFormat.parse("Jun 1, 2000").getTime());
        Date awardEntryDate = new Date(dateFormat.parse("May 20, 2000").getTime()); // must be <= closeCloseOnOrBeforeDate
        Date proposalSubmissionDate = new Date(dateFormat.parse("May 2, 1999").getTime()); // must be less than closeCloseOnOrBeforeDate
        
        Date proposalBeginningDate = new Date(dateFormat.parse("Jul 1, 1999").getTime()); // not relevant
        Date proposalEndingDate = new Date(dateFormat.parse("Aug 1, 1999").getTime()); // not relevant
        
        Date proposalClosingDate = null; // must be null
        Date awardClosingDate = null; // must be null
        
        //
        
        Proposal proposal = createProposal(proposalBeginningDate, proposalEndingDate, proposalSubmissionDate, proposalClosingDate);
        SpringServiceLocator.getProposalService().save(proposal);
        
        Award award = createAward(proposal, awardEntryDate, awardClosingDate, VALID_AWARD_STATUS_CODE);
        SpringServiceLocator.getAwardService().save(award);
        
        Close close = createClose(closeCloseOnOrBeforeDate);
        saveAndRoute(close);
//        SpringServiceLocator.getCloseService().save(close);
        
        // Verify that everything should be OK for the close.
        
        // Be sure that the award should be closed.
        verifyAwardWillBeIncludedInClose(award, close);
        
        // Be sure that the proposal should be closed.
        verifyProposalWillBeIncludedInClose(proposal, close);
        
        // Run the close.
        SpringServiceLocator.getCloseService().close();
        
        // Verify.
        Long one = new Long(1);
        assertEquals("Awards were not closed properly.", one, close.getAwardClosedCount());
        assertEquals("Proposals were not closed properly.", one, close.getProposalClosedCount());
    }
    
    public void testClose_awardEntryDateEqualToCloseOnOrBeforeDate() throws Exception {
        Date closeInitiatedDate = today; // must be today, close will abort if not
        
        Date closeCloseOnOrBeforeDate = new Date(dateFormat.parse("Jun 1, 2000").getTime()); 
        Date awardEntryDate = new Date(dateFormat.parse("June 1, 2000").getTime()); // must be <= closeCloseOnOrBeforeDate
        Date proposalSubmissionDate = new Date(dateFormat.parse("May 2, 1999").getTime()); // must be less than closeCloseOnOrBeforeDate
        
        Date proposalBeginningDate = new Date(dateFormat.parse("Jul 1, 1999").getTime()); // not relevant
        Date proposalEndingDate = new Date(dateFormat.parse("Aug 1, 1999").getTime()); // not relevant
            
        Date proposalClosingDate = null; // must be null
        Date awardClosingDate = null; // must be null
        
        //
        
        Proposal proposal = createProposal(proposalBeginningDate, proposalEndingDate, proposalSubmissionDate, proposalClosingDate);
        SpringServiceLocator.getProposalService().save(proposal);
        
        Award award = createAward(proposal, awardEntryDate, awardClosingDate, VALID_AWARD_STATUS_CODE);
        SpringServiceLocator.getAwardService().save(award);
        
        Close close = createClose(closeCloseOnOrBeforeDate);
        saveAndRoute(close);
//        SpringServiceLocator.getCloseService().save(close);
        
        // Verify that everything should be OK for the close.
        
        // Be sure that the award should be closed.
        verifyAwardWillBeIncludedInClose(award, close);
        
        // Be sure that the proposal should be closed.
        verifyProposalWillBeIncludedInClose(proposal, close);
        
        // Run the close.
        SpringServiceLocator.getCloseService().close();

        // Verify.
        Long one = new Long(1);
        assertEquals("Awards were not closed properly.", one, close.getAwardClosedCount());
        assertEquals("Proposals were not closed properly.", one, close.getProposalClosedCount());
    }
    
    public void testClose_awardClosingDateNotNull() throws Exception {
        Date closeInitiatedDate = today; // must be today, close will abort if not
        
        Date closeCloseOnOrBeforeDate = new Date(dateFormat.parse("Jun 1, 2000").getTime());
        Date awardEntryDate = new Date(dateFormat.parse("June 1, 2000").getTime());
        Date proposalSubmissionDate = new Date(dateFormat.parse("May 2, 1999").getTime()); // must be less than closeCloseOnOrBeforeDate
        
        Date proposalBeginningDate = new Date(dateFormat.parse("Jul 1, 1999").getTime()); // not relevant
        Date proposalEndingDate = new Date(dateFormat.parse("Aug 1, 1999").getTime()); // not relevant
        
        Date proposalClosingDate = null; // must be null
        Date awardClosingDate = today;
        
        // Create and save objects for closing.
        
        Proposal proposal = createProposal(proposalBeginningDate, proposalEndingDate, proposalSubmissionDate, proposalClosingDate);
        SpringServiceLocator.getProposalService().save(proposal);
        
        Award award = createAward(proposal, awardEntryDate, awardClosingDate, VALID_AWARD_STATUS_CODE);
        SpringServiceLocator.getAwardService().save(award);
        
        Close close = createClose(closeCloseOnOrBeforeDate);
        saveAndRoute(close);
//        SpringServiceLocator.getCloseService().save(close);
        
        // Verify that everything should be OK for the close.
        
        // Be sure that the award should be closed.
        verifyAwardWillBeIncludedInClose(award, close, false, true, true);
        
        // Be sure that the proposal should be closed.
        verifyProposalWillBeIncludedInClose(proposal, close);
        
        // Run the close.
        SpringServiceLocator.getCloseService().close();
        
        Long zero = new Long(0);
        Long one = new Long(1);
        assertEquals("Awards were not closed properly.", zero, close.getAwardClosedCount());
        assertEquals("Proposals were not closed properly.", one, close.getProposalClosedCount());
    }

    public void testClose_awardStatusCodeInvalid() throws Exception {
        Date closeInitiatedDate = today; // must be today, close will abort if not
        
        Date closeCloseOnOrBeforeDate = new Date(dateFormat.parse("Jun 1, 2000").getTime());
        Date awardEntryDate = new Date(dateFormat.parse("June 1, 2000").getTime());
        Date proposalSubmissionDate = new Date(dateFormat.parse("May 2, 1999").getTime()); // must be less than closeCloseOnOrBeforeDate
        
        Date proposalBeginningDate = new Date(dateFormat.parse("Jul 1, 1999").getTime()); // not relevant
        Date proposalEndingDate = new Date(dateFormat.parse("Aug 1, 1999").getTime()); // not relevant
        
        Date proposalClosingDate = null;
        Date awardClosingDate = null;
        
        // Create and save objects for closing.
        
        Proposal proposal = createProposal(proposalBeginningDate, proposalEndingDate, proposalSubmissionDate, proposalClosingDate);
        SpringServiceLocator.getProposalService().save(proposal);
        
        Award award = createAward(proposal, awardEntryDate, awardClosingDate, INVALID_AWARD_STATUS_CODE);
        SpringServiceLocator.getAwardService().save(award);
        
        Close close = createClose(closeCloseOnOrBeforeDate);
        saveAndRoute(close);
//        SpringServiceLocator.getCloseService().save(close);
        
        // Verify that everything should be OK for the close.
        
        // Be sure that the award should be closed.
        verifyAwardWillBeIncludedInClose(award, close, true, false, true);
        
        // Be sure that the proposal should be closed.
        verifyProposalWillBeIncludedInClose(proposal, close);
        
        // Run the close.
        SpringServiceLocator.getCloseService().close();
        
        Long zero = new Long(0);
        Long one = new Long(1);
        assertEquals("Awards were not closed properly.", zero, close.getAwardClosedCount());
        assertEquals("Proposals were not closed properly.", one, close.getProposalClosedCount());
    }
    
    public void testClose_awardEntryDateGreaterThanCloseOnOrBeforeDate() throws Exception {
        Date closeInitiatedDate = today; // must be today, close will abort if not
        
        Date closeCloseOnOrBeforeDate = new Date(dateFormat.parse("Jun 1, 2000").getTime());
        Date awardEntryDate = new Date(dateFormat.parse("June 2, 2000").getTime());
        Date proposalSubmissionDate = new Date(dateFormat.parse("May 2, 1999").getTime()); // must be less than closeCloseOnOrBeforeDate
        
        Date proposalBeginningDate = new Date(dateFormat.parse("Jul 1, 1999").getTime()); // not relevant
        Date proposalEndingDate = new Date(dateFormat.parse("Aug 1, 1999").getTime()); // not relevant
            
        Date proposalClosingDate = null; // must be null
        Date awardClosingDate = null; // must be null
        
        // Create and save objects for closing.
        
        Proposal proposal = createProposal(proposalBeginningDate, proposalEndingDate, proposalSubmissionDate, proposalClosingDate);
        SpringServiceLocator.getProposalService().save(proposal);
        
        Award award = createAward(proposal, awardEntryDate, awardClosingDate, VALID_AWARD_STATUS_CODE);
        SpringServiceLocator.getAwardService().save(award);
        
        Close close = createClose(closeCloseOnOrBeforeDate);
        saveAndRoute(close);
//        SpringServiceLocator.getCloseService().save(close);
        
        // Verify that everything should be OK for the close.
        
        // Be sure that the award should be closed.
        verifyAwardWillBeIncludedInClose(award, close, true, true, false);
        
        // Be sure that the proposal should be closed.
        verifyProposalWillBeIncludedInClose(proposal, close);
        
        // Run the close.
        SpringServiceLocator.getCloseService().close();
        
        Long zero = new Long(0);
        Long one = new Long(1);
        assertEquals("Awards were not closed properly.", zero, close.getAwardClosedCount());
        assertEquals("Proposals were not closed properly.", one, close.getProposalClosedCount());
    }

    public void testClose_proposalClosingDateNotNull() throws Exception {
        Date closeInitiatedDate = today; // must be today, close will abort if not
        
        Date closeCloseOnOrBeforeDate = new Date(dateFormat.parse("Jun 1, 2000").getTime());
        Date awardEntryDate = new Date(dateFormat.parse("June 1, 2000").getTime());
        Date proposalSubmissionDate = new Date(dateFormat.parse("May 2, 1999").getTime()); // must be less than closeCloseOnOrBeforeDate
        
        Date proposalBeginningDate = new Date(dateFormat.parse("Jul 1, 1999").getTime()); // not relevant
        Date proposalEndingDate = new Date(dateFormat.parse("Aug 1, 1999").getTime()); // not relevant
        
        Date proposalClosingDate = today;
        Date awardClosingDate = null;
        
        // Create and save objects for closing.
        
        Proposal proposal = createProposal(proposalBeginningDate, proposalEndingDate, proposalSubmissionDate, proposalClosingDate);
        SpringServiceLocator.getProposalService().save(proposal);
        
        Award award = createAward(proposal, awardEntryDate, awardClosingDate, VALID_AWARD_STATUS_CODE);
        SpringServiceLocator.getAwardService().save(award);
        
        Close close = createClose(closeCloseOnOrBeforeDate);
        saveAndRoute(close);
//        SpringServiceLocator.getCloseService().save(close);
        
        // Verify that everything should be OK for the close.
        
        // Be sure that the award should be closed.
        verifyAwardWillBeIncludedInClose(award, close);
        
        // Be sure that the proposal should be closed.
        verifyProposalWillBeIncludedInClose(proposal, close, false, true);
        
        // Run the close.
        SpringServiceLocator.getCloseService().close();
        
        Long zero = new Long(0);
        Long one = new Long(1);
        assertEquals("Awards were not closed properly.", one, close.getAwardClosedCount());
        assertEquals("Proposals were not closed properly.", zero, close.getProposalClosedCount());
    }

    public void testClose_proposalSubmissionDateGreaterThanCloseCloseOnOrBeforeDate() throws Exception {
        Date closeInitiatedDate = today; // must be today, close will abort if not
        
        Date closeCloseOnOrBeforeDate = new Date(dateFormat.parse("Jun 1, 2000").getTime());
        Date awardEntryDate = new Date(dateFormat.parse("June 1, 2000").getTime());
        Date proposalSubmissionDate = new Date(dateFormat.parse("June 2, 2000").getTime());
        
        Date proposalBeginningDate = new Date(dateFormat.parse("Jul 1, 1999").getTime());
        Date proposalEndingDate = new Date(dateFormat.parse("Aug 1, 1999").getTime());
        
        Date proposalClosingDate = null;
        Date awardClosingDate = null;
        
        // Create and save objects for closing.
        
        Proposal proposal = createProposal(proposalBeginningDate, proposalEndingDate, proposalSubmissionDate, proposalClosingDate);
        SpringServiceLocator.getProposalService().save(proposal);
        
        Award award = createAward(proposal, awardEntryDate, awardClosingDate, VALID_AWARD_STATUS_CODE);
        SpringServiceLocator.getAwardService().save(award);
        
        Close close = createClose(closeCloseOnOrBeforeDate);
        saveAndRoute(close);
//        SpringServiceLocator.getCloseService().save(close);
        
        // Verify that everything should be OK for the close.
        
        // Be sure that the award should be closed.
        verifyAwardWillBeIncludedInClose(award, close);
        
        // Be sure that the proposal should be closed.
        verifyProposalWillBeIncludedInClose(proposal, close, true, false);
        
        // Run the close.
        SpringServiceLocator.getCloseService().close();
        
        Long zero = new Long(0);
        Long one = new Long(1);
        assertEquals("Awards were not closed properly.", one, close.getAwardClosedCount());
        assertEquals("Proposals were not closed properly.", zero, close.getProposalClosedCount());
    }
    
    private void verifyAwardWillBeIncludedInClose(Award award, Close close) {
        verifyAwardWillBeIncludedInClose(award, close, true, true, true);
    }
    private void verifyAwardWillBeIncludedInClose(Award award, Close close, boolean verifyAwardClosingDate, boolean verifyAwardStatusCode, boolean verifyAwardEntryDate) {
        if(verifyAwardClosingDate) {
            assertNull("Award closing date must be null to be included in closing.", 
                    award.getAwardClosingDate());
        }
        if(verifyAwardStatusCode) {
            assertNotSame("Award status code must not be 'U' to be included in closing.", 
                    award.getAwardStatusCode(), "U");
        }
        if(verifyAwardEntryDate) {
            assertTrue("Award entry date must be less than or equal to the close last closed date.", 
                    award.getAwardEntryDate().getTime() <= close.getCloseOnOrBeforeDate().getTime());
        }
    }
    
    private void verifyProposalWillBeIncludedInClose(Proposal proposal, Close close) {
        verifyProposalWillBeIncludedInClose(proposal, close, true, true);
    }
    private void verifyProposalWillBeIncludedInClose(Proposal proposal, Close close, boolean verifyProposalClosingDate, boolean verifyProposalSubmissionDate) {
        if(verifyProposalClosingDate) {
            assertNull("Proposal closing date must be null to be included in closing.", 
                    proposal.getProposalClosingDate());
        }
        if(verifyProposalSubmissionDate) {
            assertTrue("Proposal submission date must be less than or equal to closing last closed date.", 
                    proposal.getProposalSubmissionDate().getTime() <= close.getCloseOnOrBeforeDate().getTime());
        }
    }
    
    private Proposal createProposal(Date proposalBeginningDate, Date proposalEndingDate, Date proposalSubmissionDate, Date proposalClosingDate) {
        // Create and save a proposal
        Proposal proposal = new Proposal();
        proposal.setProposalNumber(NextProposalNumberFinder.getLongValue());
        // set required fields
            proposal.setAgencyNumber("12851");
            proposal.setProposalProjectTitle("Testing CG Close Process");
            proposal.setProposalBeginningDate(proposalBeginningDate);
            proposal.setProposalEndingDate(proposalEndingDate);
            proposal.setProposalDirectCostAmount(new KualiDecimal("3840.00"));
            proposal.setProposalIndirectCostAmount(new KualiDecimal("2016.00"));
        proposal.setProposalTotalAmount(
                proposal.getProposalDirectCostAmount().add(proposal.getProposalIndirectCostAmount()));
            proposal.setProposalSubmissionDate(proposalSubmissionDate);
            proposal.setProposalClosingDate(proposalClosingDate);
            proposal.setProposalAwardTypeCode("N");
            proposal.setProposalPurposeCode("C");
        return proposal;
    }
    
    private Award createAward(Proposal proposal, Date awardEntryDate, Date awardClosingDate, String awardStatusCode) {
        // Create and save an award
        Award award = new Award(proposal);
        award.setAwardEntryDate(awardEntryDate);
        award.setAwardClosingDate(awardClosingDate);
        award.setAwardStatusCode(awardStatusCode);
        return award;
    }
    
    private Close createClose(Date closeCloseOnOrBeforeDate) throws WorkflowException {
        Document document = DocumentTestUtils.createDocument(SpringServiceLocator.getDocumentService(), Close.class);
        Close close = (Close) document;//SpringServiceLocator.getDocumentService().getNewDocument(Close.class);
        close.setUserInitiatedCloseDate(today);
        close.setCloseOnOrBeforeDate(closeCloseOnOrBeforeDate);
        return close;
    }

    private void saveAndRoute(Close close) throws Exception {
        DocumentService documentService = SpringServiceLocator.getDocumentService();
        saveDocument(close, documentService);
        routeDocument(close, documentService);
    }

    public static void routeDocument(Document document, DocumentService documentService) throws Exception {
        final String ENROUTE_STATUS = "R";
        final String FINAL_STATUS = "F";

        // Verify that the doc isn't yet routed.
        assertFalse(ENROUTE_STATUS.equals(document.getDocumentHeader().getWorkflowDocument().getRouteHeader().getDocRouteStatus()));

        // Route the doc.
        documentService.routeDocument(document, "routing test doc", new Vector());

        // Routing should be configured to go straight to final.
        assertTrue(FINAL_STATUS.equals(document.getDocumentHeader().getWorkflowDocument().getRouteHeader().getDocRouteStatus()));
    }

    public static void saveDocument(Document document, DocumentService documentService) throws WorkflowException {
        try {
            documentService.saveDocument(document);
        }
        catch (ValidationException e) {
            // If the business rule evaluation fails then give us more info for debugging this test.
            fail(e.getMessage() + ", " + GlobalVariables.getErrorMap());
        }
    }
}
