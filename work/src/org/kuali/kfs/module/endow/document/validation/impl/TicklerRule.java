/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.document.validation.impl;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.EndowKeyConstants;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.CloseCode;
import org.kuali.kfs.module.endow.businessobject.Tickler;
import org.kuali.kfs.module.endow.businessobject.TicklerRecipientGroup;
import org.kuali.kfs.module.endow.businessobject.TicklerRecipientPrincipal;
import org.kuali.kfs.module.endow.businessobject.lookup.CalculateProcessDateUsingFrequencyCodeService;
import org.kuali.kfs.module.endow.document.service.KEMService;
import org.kuali.kfs.module.endow.document.service.ValidateDateBasedOnFrequencyCodeService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.bo.PersistableBusinessObject;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.util.DateUtils;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.MessageMap;
import org.kuali.rice.kns.util.ObjectUtils;

/**
 * This TicklerRule class implements the Business rules associated with the KEMID.
 */
public class TicklerRule extends MaintenanceDocumentRuleBase {

    private static Logger LOG = org.apache.log4j.Logger.getLogger(TicklerRule.class);
    
    private Tickler newTickler;
    private Tickler oldTickler;

    /**
     * This method initializes the old and new Tickler.
     * 
     * @param document
     */
    private void initializeAttributes(MaintenanceDocument document) {
        if (newTickler == null) {
            newTickler = (Tickler) document.getNewMaintainableObject().getBusinessObject();
        }
        if (oldTickler == null) {
            oldTickler = (Tickler) document.getOldMaintainableObject().getBusinessObject();
        }
        
    }

    @Override
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document)
    {
        super.processCustomSaveDocumentBusinessRules(document);
        
        boolean success = true;
        int originalErrorCount = GlobalVariables.getMessageMap().getErrorCount();
        
        //Initialize Tickler Attributes
        initializeAttributes(document);
        
        //Rule 4 & 5: Ensure either Frequency or Next Tickler Date are entered.
        checkFrequencyOrNextDueDateRequirement();
        
        //Rule 16 only applies is Tickler is active.
        if(getNewTickler().isActive())
        {
            //Rule 16 Ensure atleast one active Principal or Group is attached to the Tickler.
            checkPrincipalOrGroup();
        }
        
        //Rule23: Check Term date is greater than today if tickler is reactivated.
        checkTerminationDate();
        
        success &= GlobalVariables.getMessageMap().getErrorCount() == originalErrorCount;
        return success;
        
    }
    
    private void checkTerminationDate() 
    {
        //Only if Tickler is being reactivated & termintaion field is not null
        if( getNewTickler().isActive() && !getOldTickler().isActive() && getNewTickler().getTerminationDate() != null )
        {
            //Obtain System Date
            KEMService kemService = (KEMService) SpringContext.getBean(KEMService.class);
            
            //Ensure Termination date is after today, non inclusive
            if( DateUtils.getDifferenceInDays(new Timestamp(kemService.getCurrentSystemProcessDateObject().getTime()),new Timestamp(getNewTickler().getTerminationDate().getTime())) < 1 )
            {
                putGlobalError(EndowKeyConstants.TicklerConstants.ERROR_TICKLER_TERMINATION_DATE_GREATER_SYSTEMDATE);
            }
        }
   }
    
    private void checkFrequencyOrNextDueDateRequirement()
    {
        //Check whether frequency and next due date are both missing.
        if( (StringUtils.isEmpty(getNewTickler().getFrequencyCode()) && getNewTickler().getNextDueDate() == null) )
        {
            putGlobalError(EndowKeyConstants.TicklerConstants.ERROR_TICKLER_FREQUENCYORNEXTDUEDATEREQUIREMENT);
        }
        
        //Check whether frequency and next due date are both present. If yes, then check if the date matches up with frequency next due date.
        if( !StringUtils.isEmpty(getNewTickler().getFrequencyCode()) && getNewTickler().getNextDueDate() != null ) 
        {
            CalculateProcessDateUsingFrequencyCodeService calculateProcessDateUsingFrequencyCodeService = (CalculateProcessDateUsingFrequencyCodeService) SpringContext.getBean(CalculateProcessDateUsingFrequencyCodeService.class);
            Date date = calculateProcessDateUsingFrequencyCodeService.calculateProcessDate(getNewTickler().getFrequencyCode());
            if( date.toString().compareTo((getNewTickler().getNextDueDate().toString())) != 0 )
            {
                putGlobalError(EndowKeyConstants.TicklerConstants.ERROR_TICKLER_FREQUENCY_NEXTDUEDATE_MISMATCH);
            }
        }
    }

    private void checkPrincipalOrGroup()
    {
        //Check if atleast one principal is active.
        boolean activePrincipal = false;
        for(TicklerRecipientPrincipal principal : getNewTickler().getRecipientPrincipals())
        {
            if(principal.isActive())
            {
                activePrincipal = true;
            }
        }
        
        //Check if atleast one group is active.
        boolean activeGroup = false;
        for(TicklerRecipientGroup group: getNewTickler().getRecipientGroups())
        {
            if(group.isActive())
            {
                activeGroup = true;
            }
        }
        
        if( !activePrincipal && !activeGroup )
        {
            putGlobalError(EndowKeyConstants.TicklerConstants.ERROR_TICKLER_PRINCIPAL_GROUP_REQUIRED);
        }
    }

    public Tickler getNewTickler() {
        return newTickler;
    }

    public void setNewTickler(Tickler newTickler) {
        this.newTickler = newTickler;
    }

    public Tickler getOldTickler() {
        return oldTickler;
    }

    public void setOldTickler(Tickler oldTickler) {
        this.oldTickler = oldTickler;
    }

    
    
    
}
