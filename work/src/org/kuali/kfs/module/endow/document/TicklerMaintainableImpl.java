/*
 * Copyright 2009 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.endow.document;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.businessobject.Tickler;
import org.kuali.kfs.module.endow.businessobject.TicklerKEMID;
import org.kuali.kfs.module.endow.businessobject.TicklerRecipientGroup;
import org.kuali.kfs.module.endow.businessobject.TicklerRecipientPrincipal;
import org.kuali.kfs.module.endow.businessobject.TicklerSecurity;
import org.kuali.kfs.module.endow.document.service.FrequencyCodeService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.KualiMaintainableImpl;
import org.kuali.rice.krad.service.SequenceAccessorService;
import org.kuali.rice.krad.util.KRADConstants;

/**
 * This class implements the hok on points associated with the Tickler.
 * 
 * @author Tapan S Mokha
 * @version 1.0
 */
public class TicklerMaintainableImpl extends KualiMaintainableImpl 
{

    private static Logger log = org.apache.log4j.Logger.getLogger(TicklerMaintainableImpl.class);
    
    private Tickler newTickler;
    private Tickler oldTickler;
    
    private transient SequenceAccessorService sequenceAccessorService;
    
    /**
     * Called when page is refreshed and does 
     * 1. Updates frequency date when frequency code is selected.
     * 
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#refresh(java.lang.String, java.util.Map,
     *      org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    public void refresh(String refreshCaller, Map fieldValues, MaintenanceDocument document) 
    {
        super.refresh(refreshCaller, fieldValues, document);
        
        initializeAttributes(document);
        
        //Update Next Due date if frequency Code selected
        updateNextDueDate(refreshCaller, fieldValues);
    }

    /**
     * Updates Next Due date if frequency Code selected.
     *     
     * @param refreshCaller
     * @param fieldValues
     */
    private void updateNextDueDate(String refreshCaller, Map fieldValues) 
    {
        if (refreshCaller != null && refreshCaller.equalsIgnoreCase(EndowConstants.KUALI_FREQUENCY_LOOKUPABLE_IMPL) && fieldValues != null) 
        {
            String frequencyCode = newTickler.getFrequencyCode();
            if (StringUtils.isNotEmpty(frequencyCode)) 
            {
                FrequencyCodeService frequencyCodeService = (FrequencyCodeService) SpringContext.getBean(FrequencyCodeService.class);
                newTickler.setNextDueDate(frequencyCodeService.calculateProcessDate(frequencyCode));
            }
        }
    }

    /**
     * Called after a copy operation is performed and performs.
     * 1. Clears all associated collections (kemids, security, principals & groups)
     * 
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#processAfterCopy(org.kuali.rice.kns.document.MaintenanceDocument,
     *      java.util.Map)
     */
    @Override
    public void processAfterCopy(MaintenanceDocument arg0, Map<String, String[]> arg1) {
        super.processAfterCopy(arg0, arg1);

        initializeAttributes(arg0);

        //Clears all associated collections (kemids, security, principals & groups)
        clearAllCollections();
        
    }
    
    /**
     * Clears all associated collections (kemids, security, principals & groups)
     */
    private void clearAllCollections()
    {
        getOldTickler().getKemIds().clear();
        getOldTickler().getSecurities().clear();
        getOldTickler().getRecipientPrincipals().clear();
        getOldTickler().getRecipientGroups().clear();
        
        getNewTickler().getKemIds().clear();
        getNewTickler().getSecurities().clear();
        getNewTickler().getRecipientPrincipals().clear();
        getNewTickler().getRecipientGroups().clear();
    }

    /**
     * Called before a Tickler is saved and performs.
     * 1. Assign Tickler Number to Tickler in case of new or copy workflows.
     * 2. If tickler marked as inactive, mark all associated collections: kemids, securities, principals and groups records as inactive. 
     * 
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#prepareForSave()
     */
    @Override
    public void prepareForSave() 
    {
        super.prepareForSave();
        
        Tickler tickler = (Tickler) getBusinessObject();
        
        //Assign Tickler Number to Tickler in case of new or copy workflows
        assignTicklerNumber(tickler);
        
        //Rule 18: If tickler marked as inactive, mark all associated collections: kemids, securities, principals and groups records as inactive 
        inactivateTicklerAssociations(tickler);
        
    }

    /**
     * Assign Tickler Number to Tickler in case of new or copy workflows.
     *      
     * @param tickler
     */
    private void assignTicklerNumber(Tickler tickler) 
    {
        if( KRADConstants.MAINTENANCE_NEW_ACTION.equals(getMaintenanceAction()) || KRADConstants.MAINTENANCE_COPY_ACTION.equals(getMaintenanceAction()) )
        {
            //Only assign a Tickler number if not assigned already,may be assigned during a previous save operation.
            if(tickler.getNumber() == null)
            {
                String ticklerNumber = getSequenceAccessorService().getNextAvailableSequenceNumber(EndowConstants.Sequences.END_TICKLER_SEQ).toString();
                tickler.setNumber(ticklerNumber);
            }
        }
    }

    /**
     * If tickler marked as inactive, mark all associated collections: kemids, securities, principals and groups records as inactive.
     * 
     * @param tickler
     */
    private void inactivateTicklerAssociations(Tickler tickler)
    {
        if(!tickler.isActive())
        {
            //Inactivate Kemids
            for(TicklerKEMID kemId: tickler.getKemIds())
            {
                kemId.setActive(false);
            }
            
            //Inactivate Securities 
            for(TicklerSecurity security: tickler.getSecurities())
            {
                security.setActive(false);
            }
                
            //Inactivate Principals
            for(TicklerRecipientPrincipal principal : tickler.getRecipientPrincipals())
            {
                principal.setActive(false);
            }
            
            //Inactivate Groups
            for(TicklerRecipientGroup group : tickler.getRecipientGroups())
            {
                group.setActive(false);
            }

        }
    }

    /**
     * Initializes newTickler and oldTickler.
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
     * Obtain SequenceAccessorService
     * 
     * @return
     */
    public SequenceAccessorService getSequenceAccessorService() 
    {
        if(sequenceAccessorService == null)
        {
            sequenceAccessorService = SpringContext.getBean(SequenceAccessorService.class);
        }
        return sequenceAccessorService;
    }

    public void setSequenceAccessorService(SequenceAccessorService sequenceAccessorService) {
        this.sequenceAccessorService = sequenceAccessorService;
    }

    public Tickler getOldTickler() {
        return oldTickler;
    }

    public void setOldTickler(Tickler oldTickler) {
        this.oldTickler = oldTickler;
    }

    public Tickler getNewTickler() {
        return newTickler;
    }

    public void setNewTickler(Tickler newTickler) {
        this.newTickler = newTickler;
    }
    
}
