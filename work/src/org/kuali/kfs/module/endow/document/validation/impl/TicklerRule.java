/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.endow.document.validation.impl;

import java.sql.Date;
import java.sql.Timestamp;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.module.endow.EndowKeyConstants;
import org.kuali.kfs.module.endow.businessobject.Tickler;
import org.kuali.kfs.module.endow.businessobject.TicklerRecipientGroup;
import org.kuali.kfs.module.endow.businessobject.TicklerRecipientPrincipal;
import org.kuali.kfs.module.endow.document.service.KEMService;
import org.kuali.kfs.module.endow.document.service.impl.FrequencyCodeServiceImpl;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.util.KfsDateUtils;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * This TicklerRule class implements the Business rules associated with the Tickler.
 * 
 * @author Tapan S Mokha
 * @version 1.0
 */
public class TicklerRule extends MaintenanceDocumentRuleBase {

    private static Logger log = org.apache.log4j.Logger.getLogger(TicklerRule.class);
    
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

    /**
     * This method validates the Tickler before being submitted
     *  1. Check Frequency Or Next Due Date presence and validity.
     *  2. Check if at-least one Principal Or Group is present and active.
     *  3. Mark the Termination Date on DeActivation of tickler.
     *  4. Check Termination Date if the Tickler is marked as inactive and if > Todays' Date.
     *  
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document)
    {
        super.processCustomSaveDocumentBusinessRules(document);
        
        if(GlobalVariables.getMessageMap().getErrorCount() == 0)
        {
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
            
            //Rule 9: Put Termination date as system date if tickler is deactivated.
            markTerminationDateonDeActivation();

            //Rule 23: Check Term date is greater than today if tickler is reactivated.
            checkTerminationDateonActivation();
            
            return GlobalVariables.getMessageMap().getErrorCount() == 0;
        }
        else
        {
            return false;
        }
    }
    
    /**
     * Marks the Termination Date on DeActivation of tickler.     
    */
    private void markTerminationDateonDeActivation() 
    {
        //The tickler was deactivated
        if( getOldTickler().isActive() && !getNewTickler().isActive())
        {
            //Obtain System Date
            KEMService kemService = (KEMService) SpringContext.getBean(KEMService.class);
            getNewTickler().setTerminationDate(kemService.getCurrentDate());
        }
    }
    
    /**
     * Checks Termination Date if the Tickler is marked as inactive and if > Todays' Date.
     */
    private void checkTerminationDateonActivation() 
    {
        //Only if Tickler is being reactivated & termintaion field is not null
        if( getNewTickler().isActive() && !getOldTickler().isActive() && getNewTickler().getTerminationDate() != null )
        {
            //Obtain System Date
            KEMService kemService = (KEMService) SpringContext.getBean(KEMService.class);
            
            //Ensure Termination date is after today, non inclusive
            if( KfsDateUtils.getDifferenceInDays(new Timestamp(kemService.getCurrentSystemProcessDateObject().getTime()),new Timestamp(getNewTickler().getTerminationDate().getTime())) < 1 )
            {
                putGlobalError(EndowKeyConstants.TicklerConstants.ERROR_TICKLER_TERMINATION_DATE_GREATER_SYSTEMDATE);
            }
        }
   }
    
    /**
     * Checks Tickler's Frequency Or Next Due Date presence and validity.
     */
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
            FrequencyCodeServiceImpl frequencyCodeServiceImpl = (FrequencyCodeServiceImpl) SpringContext.getBean(FrequencyCodeServiceImpl.class);
            Date date = frequencyCodeServiceImpl.calculateProcessDate(getNewTickler().getFrequencyCode());
            if( date.toString().compareTo((getNewTickler().getNextDueDate().toString())) != 0 )
            {
                putGlobalError(EndowKeyConstants.TicklerConstants.ERROR_TICKLER_FREQUENCY_NEXTDUEDATE_MISMATCH);
            }
        }
    }

    /**
     * Checks if at-least one Principal Or Group is present and active.
     */
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
