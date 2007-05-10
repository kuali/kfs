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
import java.util.Collection;

import org.kuali.core.service.DateTimeService;
import org.kuali.module.cg.bo.Award;
import org.kuali.module.cg.bo.Proposal;
import org.kuali.module.cg.bo.Close;
import org.kuali.module.cg.dao.AwardDao;
import org.kuali.module.cg.dao.ProposalDao;
import org.kuali.module.cg.dao.CloseDao;
import org.kuali.module.cg.lookup.valuefinder.NextCloseNumberFinder;
import org.kuali.module.cg.service.CloseService;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class CloseServiceImpl implements CloseService {
    
    private AwardDao awardDao;
    private ProposalDao proposalDao;
    private CloseDao closeDao;
    private DateTimeService dateTimeService;
    
    /**
     * -- Get the max proposal_close_number in cg_prpsl_close_t.
     * 
     * -- Get the Close with that max_close_number.
     * 
     * -- If todays date is the same as the user_initiate_date on that Close, continue. Else, break.
     * 
     * -- Get all proposals with a null closing_date and a submission_date <= the last_closed_date of the Close with the max_proposal_close number.
     * 
     * -- Save the number of proposals that come back.
     * 
     * -- Update each of these proposals setting the close_date to todays date.
     * 
     * -- Get all awards with a null closing_date, an entry_date <= the last_closed_date of the Close with the max_close number and a status_code not equal to 'U'.
     * 
     * -- Save the number of awards that come back.
     * 
     * -- Update each of these awards setting the close_date to todays date.
     * 
     * -- Update the Close with that max_close_number setting the proposal_closed_count to the number of proposals brought back above and the award_closed_count to the number of awards brought back above.
     * 
     * -- Save the Close.
     * 
     * @see org.kuali.module.cg.service.CloseService#close()
     */
    public void close() {
        
        Close max = closeDao.getMaxApprovedClose();
        Date today = dateTimeService.getCurrentSqlDateMidnight();

        if(null == max) { // no closes at all. Gotta wait until we get an approved one.
            return;
        }

        if(!today.equals(max.getUserInitiatedCloseDate())) {
            return;
        }
        
        Collection<Proposal> proposals = proposalDao.getProposalsToClose(max);
        Long proposalCloseCount = new Long(proposals.size());
        for(Proposal p : proposals) {
            p.setProposalClosingDate(today);
            proposalDao.save(p);
        }
        
        Collection<Award> awards = awardDao.getAwardsToClose(max);
        Long awardCloseCount = new Long(awards.size());
        for(Award a : awards) {
            a.setAwardClosingDate(today);
            awardDao.save(a);
        }
        
        max.setAwardClosedCount(awardCloseCount);
        max.setProposalClosedCount(proposalCloseCount);
        
        closeDao.save(max);
        
    }

    public Close getMostRecentClose() {
        return closeDao.getMaxApprovedClose();
    }

    public void save(Close close) {
        closeDao.save(close);
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
