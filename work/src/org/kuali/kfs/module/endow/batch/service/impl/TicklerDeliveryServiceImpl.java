/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.batch.service.impl;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.batch.service.TicklerDeliveryService;
import org.kuali.kfs.module.endow.businessobject.Tickler;
import org.kuali.kfs.module.endow.businessobject.TicklerDeliveryStatisticsReportDetailTableRow;
import org.kuali.kfs.module.endow.businessobject.TicklerRecipientGroup;
import org.kuali.kfs.module.endow.businessobject.TicklerRecipientPrincipal;
import org.kuali.kfs.module.endow.document.service.KEMService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.ReportWriterService;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.KualiMaintainableImpl;
import org.kuali.rice.kns.service.MaintenanceDocumentDictionaryService;
import org.kuali.rice.krad.bo.AdHocRoutePerson;
import org.kuali.rice.krad.bo.AdHocRouteRecipient;
import org.kuali.rice.krad.bo.AdHocRouteWorkgroup;
import org.kuali.rice.krad.rules.rule.event.SendAdHocRequestsEvent;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.service.KualiRuleService;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class TicklerDeliveryServiceImpl implements TicklerDeliveryService {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(TicklerDeliveryServiceImpl.class);
    
    private BusinessObjectService businessObjectService;
    private KEMService kemService;
    private UniversityDateService universityDateService;
    private KualiRuleService kualiRuleService;
    private DocumentService documentService;
    private Date currentDate;
    private ReportWriterService ticklerDeliveryStatisticsReportsWriterService;
    
    private TicklerDeliveryStatisticsReportDetailTableRow ticklerDeliveryStatisticsReportDetailTableRow;
    
    public TicklerDeliveryServiceImpl(){
        //statistics report...
        ticklerDeliveryStatisticsReportDetailTableRow = new TicklerDeliveryStatisticsReportDetailTableRow();
    }
    
    public boolean generateTicklerNotices() {

        //set current date
        currentDate = kemService.getCurrentDate();                
        
        LOG.info("Begin generateTicklerNotices() with notification date of " + currentDate);

        //get tickler documents
        ArrayList<Tickler>ticklerBOs = new ArrayList<Tickler>(getTicklerBusinessObjects());
        
        //route tickler delivery notice
        routeTicklerDeliveryNotice(ticklerBOs);
        
        writeStatisticsReport();
        
        LOG.info("End generateTicklerNotices() with notification date of " + currentDate);
        
        return false;
    }

    /**
     * Retrieves a list of tickler BOs where the next review date is
     * equal to today, the term date is null or greater than today, and the record is active.
     * 
     * @param currentDate
     * @return
     */
    protected List<Tickler> getTicklerBusinessObjects(){        
        Map<String, Object> queryCriteria = new HashMap<String, Object>();
        queryCriteria.put(EndowPropertyConstants.TICKLER_NEXT_DUE_DATE, currentDate);
        queryCriteria.put(EndowPropertyConstants.TICKLER_ACTIVE_INDICATOR, KFSConstants.ParameterValues.YES);
        
        ArrayList<Tickler> ticklerBOs = null;
        ticklerBOs = new ArrayList<Tickler>(businessObjectService.findMatching(Tickler.class, queryCriteria));
        
        //Go through and remove tickler docs where the term date has expired
        if(ObjectUtils.isNotNull(ticklerBOs)){
        
            for(int i = ticklerBOs.size()-1; i > -1; i--){
                
                if(ObjectUtils.isNotNull(ticklerBOs.get(i).getTerminationDate()) && ticklerBOs.get(i).getTerminationDate().before(currentDate)){
                    ticklerBOs.remove(i);
                }
            }
        }
        
        return ticklerBOs;
    }
        
    /**
     * Routes FYI tickler documents to Tickler persons and groups
     * 
     * @param ticklerDocs
     * @return
     */
    protected boolean routeTicklerDeliveryNotice(List<Tickler> ticklerBOs){
        boolean success = false;
        boolean rulePassed = false;
        MaintenanceDocument ticklerDocument = null;
        
        if(ObjectUtils.isNotNull(ticklerBOs)){
        
            for(Tickler ticklerBO : ticklerBOs){
                
                //create a maintenance document from the 
                ticklerDocument = createTicklerDocument(ticklerBO);
                
                //add principals and groups
                ticklerDocument.setAdHocRoutePersons(convertTicklerPrincipalToAdhocRoutePerson(ticklerBO.getRecipientPrincipals()));
                ticklerDocument.setAdHocRouteWorkgroups(convertTicklerGroupsToAdhocRouteGroup(ticklerBO.getRecipientGroups()));
                
                //check rules to ensure valid recipients
                rulePassed = kualiRuleService.applyRules(new SendAdHocRequestsEvent(ticklerDocument));
                
                if (rulePassed) {
                    try{
                        //rule passed to send adhoc requests
                        documentService.routeDocument(ticklerDocument, "Tickler Notification - " + currentDate, combineAdHocRecipients(ticklerDocument));
                        ticklerDeliveryStatisticsReportDetailTableRow.increaseTicklerDeliveryNotificationsCount();
                        success = true;
                    }catch(WorkflowException wfe){
                        ticklerDeliveryStatisticsReportDetailTableRow.increaseNumberOfExceptionsCount();
                        //just warn, but continue routing with other tickler BOs
                        LOG.warn("Failed to route Tickler Delivery notices for Tickler Number " + ticklerBO.getNumber() + " with notification date of " + currentDate);
                    }
                }else{
                    ticklerDeliveryStatisticsReportDetailTableRow.increaseNumberOfExceptionsCount();
                    LOG.warn("Invalid recipients for Tickler Delivery notices for Tickler Number " + ticklerBO.getNumber() + " with notification date of " + currentDate);
                }
                
                
            }
        }else{
            //nothing to process
            success = true;
        }        
        
        return success;
    }
    
    /**
     * Creates a Tickler Maintenance Document based on a Tickler BO 
     * 
     * @param ticklerBo
     * @return
     */
    protected MaintenanceDocument createTicklerDocument(Tickler ticklerBo){
    
        MaintenanceDocument document = null;
        
        try{
            document = (MaintenanceDocument) documentService.getNewDocument(SpringContext.getBean(MaintenanceDocumentDictionaryService.class).getDocumentTypeName(ticklerBo.getClass()));            
        }
        catch (WorkflowException e) {
            throw new RuntimeException(e);
        }

        // add all the pieces
        document.getDocumentHeader().setDocumentDescription("Tickler Notification - " + currentDate);
        document.setOldMaintainableObject(new KualiMaintainableImpl(ticklerBo));
        document.getOldMaintainableObject().setBoClass(ticklerBo.getClass());
        document.setNewMaintainableObject(new KualiMaintainableImpl(ticklerBo));        
        document.getNewMaintainableObject().setBoClass(ticklerBo.getClass());
        document.getNewMaintainableObject().setMaintenanceAction(KRADConstants.MAINTENANCE_EDIT_ACTION);
        document.getNewMaintainableObject().setDocumentNumber(document.getDocumentNumber());
        
        return document;
    }
    
    /**
     * Converts tickler principals into normal AdHocRoutePerson list
     * 
     * @param principals
     * @return
     */
    protected List<AdHocRoutePerson> convertTicklerPrincipalToAdhocRoutePerson(List<TicklerRecipientPrincipal> principals){
        List<AdHocRoutePerson> personList = new ArrayList<AdHocRoutePerson>();
        AdHocRoutePerson person = null;
        
        if(ObjectUtils.isNotNull(principals)){
            //for each principal, make an AdHocRoutePerson
            for(TicklerRecipientPrincipal principal : principals){
                if(principal.isActive()){
                    person = new AdHocRoutePerson();
                    person.setId(principal.getContact().getPrincipalName());
                    person.setActionRequested(KewApiConstants.ACTION_REQUEST_FYI_REQ);                    
                    personList.add(person);
                }
            }
        }
        
        return personList;
    }
    
    /**
     * Converts tickler groups into normal AdHocRouteWorkgroup list
     * 
     * @param groups
     * @return
     */
    protected List<AdHocRouteWorkgroup> convertTicklerGroupsToAdhocRouteGroup(List<TicklerRecipientGroup> groups){
        List<AdHocRouteWorkgroup> groupList = new ArrayList<AdHocRouteWorkgroup>();
        AdHocRouteWorkgroup workgroup = null;
        
        if(ObjectUtils.isNotNull(groups)){
            //for each group, make an AdHocWorkgroup
            for(TicklerRecipientGroup group : groups){
                if(group.isActive()){
                    workgroup = new AdHocRouteWorkgroup();
                    workgroup.setId(group.getGroupId());
                    workgroup.setRecipientName(group.getAssignedToGroup().getName());
                    workgroup.setRecipientNamespaceCode(group.getAssignedToGroup().getNamespaceCode());
                    workgroup.setActionRequested(KewApiConstants.ACTION_REQUEST_FYI_REQ);
                    
                    groupList.add(workgroup);
                }
            }
        }
        
        return groupList;
    }
    
    /**
     * Combines persons and workgroups from document into one list.
     * 
     * @param ticklerDocument
     * @return
     */
    protected List<AdHocRouteRecipient> combineAdHocRecipients(MaintenanceDocument ticklerDocument) {
        List<AdHocRouteRecipient> adHocRecipients = new ArrayList<AdHocRouteRecipient>();
        adHocRecipients.addAll(ticklerDocument.getAdHocRoutePersons());
        adHocRecipients.addAll(ticklerDocument.getAdHocRouteWorkgroups());
        return adHocRecipients;
    }

    protected void writeStatisticsReport() {
        //now print the statistics report.....
        long totalTicklerDeliveryNotifications = 0;
        long totalNumberOfExceptions = 0;
                
        //write the header line....
        ticklerDeliveryStatisticsReportsWriterService.writeStatisticLine("Number of Tickler Notifications\t\tNumber of Exceptions");
        ticklerDeliveryStatisticsReportsWriterService.writeStatisticLine("-------------------------------\t\t--------------------");
            
        ticklerDeliveryStatisticsReportsWriterService.writeStatisticLine("%31d\t\t%20d", ticklerDeliveryStatisticsReportDetailTableRow.getTicklerDeliveryNotifications(), ticklerDeliveryStatisticsReportDetailTableRow.getNumberOfExceptions());
    }

    protected TicklerDeliveryStatisticsReportDetailTableRow getTicklerDeliveryStatisticsReportDetailTableRow() {
        return ticklerDeliveryStatisticsReportDetailTableRow;
    }

    public void setTicklerDeliveryStatisticsReportDetailTableRow(TicklerDeliveryStatisticsReportDetailTableRow ticklerDeliveryStatisticsReportDetailTableRow) {
        this.ticklerDeliveryStatisticsReportDetailTableRow = ticklerDeliveryStatisticsReportDetailTableRow;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public void setKemService(KEMService kemService) {
        this.kemService = kemService;
    }

    public void setUniversityDateService(UniversityDateService universityDateService) {
        this.universityDateService = universityDateService;
    }

    public void setKualiRuleService(KualiRuleService kualiRuleService) {
        this.kualiRuleService = kualiRuleService;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    public void setTicklerDeliveryStatisticsReportsWriterService(ReportWriterService ticklerDeliveryStatisticsReportsWriterService) {
        this.ticklerDeliveryStatisticsReportsWriterService = ticklerDeliveryStatisticsReportsWriterService;
    }

}
