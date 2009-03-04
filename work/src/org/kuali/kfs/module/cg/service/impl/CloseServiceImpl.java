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
package org.kuali.kfs.module.cg.service.impl;

import java.sql.Date;
import java.text.MessageFormat;
import java.util.Collection;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.cg.CGConstants;
import org.kuali.kfs.module.cg.CGKeyConstants;
import org.kuali.kfs.module.cg.businessobject.Award;
import org.kuali.kfs.module.cg.businessobject.CFDAClose;
import org.kuali.kfs.module.cg.businessobject.Proposal;
import org.kuali.kfs.module.cg.dataaccess.AwardDao;
import org.kuali.kfs.module.cg.dataaccess.CloseDao;
import org.kuali.kfs.module.cg.dataaccess.ProposalDao;
import org.kuali.kfs.module.cg.service.CloseService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.bo.Note;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class CloseServiceImpl implements CloseService {

    private AwardDao awardDao;
    private ProposalDao proposalDao;
    private CloseDao closeDao;
    private DateTimeService dateTimeService;

    /**
     * <ul>
     * <li>Get the max proposal_close_number in cg_prpsl_close_t.</li>
     * <li>Get the Close with that max_close_number.</li>got
     * <li>If todays date is the same as the user_initiate_date on that Close, continue. Else, break.</li>
     * <li>Get all proposals with a null closing_date and a submission_date <= the last_closed_date of the Close with the
     * max_proposal_close number.</li>
     * <li>Save the number of proposals that come back.</li>
     * <li>Update each of these proposals setting the close_date to todays date.</li>
     * <li>Get all awards with a null closing_date, an entry_date <= the last_closed_date of the Close with the max_close number
     * and a status_code not equal to 'U'.</li>
     * <li>Save the number of awards that come back.</li>
     * <li>Update each of these awards setting the close_date to todays date.</li>
     * <li>Update the Close with that max_close_number setting the proposal_closed_count to the number of proposals brought back
     * above and the award_closed_count to the number of awards brought back above.</li>
     * <li>Save the Close.</li>
     * </ul>
     * 
     * @see org.kuali.kfs.module.cg.service.CloseService#close()
     */
    public boolean close() {

        CFDAClose max = closeDao.getMaxApprovedClose();
        Date today = dateTimeService.getCurrentSqlDateMidnight();

        if (null == max) { // no closes at all. Gotta wait until we get an approved one.
            return true;
        }

        boolean result = true;
        String noteText = null;
        if (StringUtils.equals(max.getDocumentHeader().getWorkflowDocument().getRouteHeader().getCurrentRouteNodeNames(), 
                CGConstants.CGKimConstants.UNPROCESSED_ROUTING_NODE_NAME)){

            KualiConfigurationService kualiConfigurationService = SpringContext.getBean(KualiConfigurationService.class);

            try {

                Collection<Proposal> proposals = proposalDao.getProposalsToClose(max);
                Long proposalCloseCount = new Long(proposals.size());
                for (Proposal p : proposals) {
                    p.setProposalClosingDate(today);
                    proposalDao.save(p);
                }

                Collection<Award> awards = awardDao.getAwardsToClose(max);
                Long awardCloseCount = new Long(awards.size());
                for (Award a : awards) {
                    a.setAwardClosingDate(today);
                    awardDao.save(a);
                }

                max.setAwardClosedCount(awardCloseCount);
                max.setProposalClosedCount(proposalCloseCount);

                closeDao.save(max);
                noteText = kualiConfigurationService.getPropertyString(CGKeyConstants.MESSAGE_CLOSE_JOB_SUCCEEDED);

            } catch (Exception e) {
                String messageProperty = kualiConfigurationService.getPropertyString(CGKeyConstants.ERROR_CLOSE_JOB_FAILED);
                noteText = MessageFormat.format(messageProperty, e.getMessage(), e.getCause().getMessage());
            } finally {
               result = this.addDocumentNoteAfterClosing(max, noteText);
            }
        }
        return result;
    }
    
    public CFDAClose getMostRecentClose() {
        CFDAClose mostRecentClose = closeDao.getMostRecentClose();
        return mostRecentClose;
    }

    /**
     * @see org.kuali.kfs.module.cg.service.CloseService#addDocumentNoteAfterClosing(String)
     */
    private boolean addDocumentNoteAfterClosing(CFDAClose close, String noteText) {
        Note note = new Note();
        note.setNoteText(noteText);
        note.setAuthorUniversalIdentifier(GlobalVariables.getUserSession().getPerson().getPrincipalId());

        DocumentService service = SpringContext.getBean(DocumentService.class);
        try {
            service.addNoteToDocument(close, note);
            service.approveDocument(close, note.getNoteText(), null);
        } catch (WorkflowException we) {
            we.printStackTrace();
            return false;
        }
        return true;
    }


    public void setAwardDao(AwardDao awardDao) {
        this.awardDao = awardDao;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    public void setCloseDao(CloseDao closeDao) {
        this.closeDao = closeDao;
    }

    public void setProposalDao(ProposalDao proposalDao) {
        this.proposalDao = proposalDao;
    }
}
