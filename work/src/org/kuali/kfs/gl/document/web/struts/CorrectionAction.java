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

package org.kuali.module.gl.web.struts.action;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;
import org.kuali.Constants;
import org.kuali.KeyConstants;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.LookupService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.core.web.struts.action.KualiDocumentActionBase;
import org.kuali.module.gl.bo.CorrectionChange;
import org.kuali.module.gl.bo.CorrectionChangeGroup;
import org.kuali.module.gl.bo.CorrectionCriteria;
import org.kuali.module.gl.bo.OriginEntry;
import org.kuali.module.gl.bo.OriginEntryGroup;
import org.kuali.module.gl.dao.OriginEntryDao;
import org.kuali.module.gl.dao.OriginEntryGroupDao;
import org.kuali.module.gl.document.CorrectionDocument;
import org.kuali.module.gl.service.OriginEntryGroupService;
import org.kuali.module.gl.service.OriginEntryService;
import org.kuali.module.gl.web.struts.form.CorrectionForm;

/**
 * @author Laran Evans <lc278@cornell.edu>
 *         Shawn Choo  <schoo@indiana.edu>
 * @version $Id: CorrectionAction.java,v 1.10 2006-06-14 01:50:26 schoo Exp $
 * 
 */

public class CorrectionAction extends KualiDocumentActionBase {
    
    
    private DateTimeService dateTimeService;
    private CorrectionForm errorCorrectionForm;
    private CorrectionDocument document;
    private OriginEntryGroupService originEntryGroupService;
    private OriginEntryGroupDao originEntryGroupDao;
    private OriginEntryService originEntryService;
    private OriginEntryDao originEntryDao;
    
    
    /**
     * Add a new CorrectionGroup to the Document.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    
    
    
    public ActionForward uploadFile(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) 
    throws FileNotFoundException, IOException {
        
        CorrectionForm errorCorrectionForm = (CorrectionForm) form;
        document = (CorrectionDocument) errorCorrectionForm.getDocument();
        originEntryGroupService = (OriginEntryGroupService) SpringServiceLocator.getBeanFactory().getBean("glOriginEntryGroupService");
        originEntryService = (OriginEntryService) SpringServiceLocator.getBeanFactory().getBean("glOriginEntryService");
        
        java.sql.Date today = new java.sql.Date(System.currentTimeMillis());
        OriginEntryGroup newOriginEntryGroup = originEntryGroupService.createGroup(today, "GLCP", false, false, false);
        
        
      
        FormFile sourceFile = errorCorrectionForm.getSourceFile();
        
        BufferedReader br = new BufferedReader(new InputStreamReader(sourceFile.getInputStream()));
        OriginEntry entryFromFile = new OriginEntry();
        
        try {
            String currentLine = br.readLine();
            while (currentLine != null) {
                entryFromFile.setFromTextFile(currentLine);
                
                entryFromFile.setEntryGroupId(newOriginEntryGroup.getId());
                originEntryService.createEntry(entryFromFile, newOriginEntryGroup);
                
                currentLine = br.readLine();
            }
        }
        finally {
            br.close();
        }
        
        HttpSession session = request.getSession(true);
        session.setAttribute("newGroupId", newOriginEntryGroup.getId().toString());
        
        if (errorCorrectionForm.getEditMethod().equals("manual")){
            String[] newGroupId = {newOriginEntryGroup.getId().toString()};
            showAllEntries(newGroupId, errorCorrectionForm);
            if (errorCorrectionForm.getAllEntries().size() > 0){
                errorCorrectionForm.setEditableFlag("Y");
                //errorCorrectionForm.setManualEditFlag("Y");
                showEditMethod(mapping, form,request, response);
            }
            
        }
        
        if (errorCorrectionForm.getEditMethod().equals("criteria")){
            
            
            String groupId[] = {newOriginEntryGroup.getId().toString()};
            //the boolean is readOnly, show output only - not storing in DB 
            showAllEntries(groupId, errorCorrectionForm);
         }
        
        
        return mapping.findForward(Constants.MAPPING_BASIC);
        
    }
    
    
    public ActionForward addCorrectionGroup(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) 
    throws Exception {
        
        CorrectionForm errorCorrectionForm = (CorrectionForm) form;
        document = (CorrectionDocument) errorCorrectionForm.getDocument();
        // rebuild the document state
        CorrectionActionHelper.rebuildDocumentState(request, errorCorrectionForm);
        
        // create a new correction group and add it to the document
        document.addCorrectionGroup(new CorrectionChangeGroup());
        
        // for consistent presentation ...
        CorrectionActionHelper.sortForDisplay(document.getCorrectionChangeGroup());
        
        //show the document when users choose documents
        //Don't need this if multiple dropdown keeps holding choice from user
        HttpSession session = request.getSession(true);
        
        if (session.getAttribute("groupId") != null) {
            String[] groupId = (String[]) session.getAttribute("groupId");
            showAllEntries(groupId, errorCorrectionForm);
            
            errorCorrectionForm.setEditableFlag("N");
            //manualEditFlag is for activate a button for asking user to ask edit the docu.
            errorCorrectionForm.setManualEditFlag("N");
        }
        
        return mapping.findForward(Constants.MAPPING_BASIC);
    }
    
    /**
     * Remove an existing CorrectionChangeGroup from the Document.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward removeCorrectionGroup(ActionMapping mapping, ActionForm form, 
            HttpServletRequest request, HttpServletResponse response) 
    throws Exception {
        
        CorrectionForm errorCorrectionForm = (CorrectionForm) form;
        document = (CorrectionDocument) errorCorrectionForm.getDocument();
        // rebuild the document state
        CorrectionActionHelper.rebuildDocumentState(request, errorCorrectionForm);
        
        // load the correction group number from the request
        Integer groupNumber = 
            CorrectionActionHelper
            .getFromRequestCorrectionGroupNumberToRemove(request, document);
        
        // remove the correction group from the document
        document.removeCorrectionGroup(groupNumber);
        
        if(document.getCorrectionChangeGroup().isEmpty()) {
            document.addCorrectionGroup(new CorrectionChangeGroup());
        }
        
        // for consistent presentation ...
        CorrectionActionHelper.sortForDisplay(document.getCorrectionChangeGroup());
        
        //show the document when users choose documents
        //Don't need this if multiple dropdown keep holding choice from user
        HttpSession session = request.getSession(true);
        if (session.getAttribute("groupId") != null) {
            String[] groupId = (String[]) session.getAttribute("groupId");
            showAllEntries(groupId, errorCorrectionForm);
            errorCorrectionForm.setEditableFlag("N");
            //manualEditFlag is for activate a button for asking user to ask edit the docu.
            errorCorrectionForm.setManualEditFlag("N");
        }
        
        return mapping.findForward(Constants.MAPPING_BASIC);
    }
    
    /**
     * Add a new search criteria to a specific group of criteria.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward addSearchCriterion(ActionMapping mapping, ActionForm form, 
            HttpServletRequest request, HttpServletResponse response) 
    throws Exception {
        
        CorrectionForm errorCorrectionForm = (CorrectionForm) form;
        document = (CorrectionDocument) errorCorrectionForm.getDocument();
        // rebuild the document state
        CorrectionActionHelper.rebuildDocumentState(request, errorCorrectionForm);
        
        Map groupNumbers = new HashMap();
        for(Iterator i = document.getCorrectionChangeGroup().iterator(); i.hasNext();) {
            CorrectionChangeGroup g = (CorrectionChangeGroup) i.next();
            groupNumbers.put(g.getCorrectionChangeGroupLineNumber(), g);
        }
        
        for(Iterator i = groupNumbers.keySet().iterator(); i.hasNext();) {
            CorrectionChangeGroup correctionGroup = (CorrectionChangeGroup) groupNumbers.get(i.next());
            
            // populate the criterion submitted to be added
            CorrectionCriteria criterion = 
                CorrectionActionHelper
                .getFromRequestNewSearchCriterion(request, correctionGroup.getCorrectionChangeGroupLineNumber());
            
            if(null == criterion) {
                // not the criterion we intended to add.
                continue;
            }
            
            criterion.setFinancialDocumentNumber(document.getFinancialDocumentNumber());
            
            // add the new criterion to the appropriate group of search criteria owned by the document
            correctionGroup.addSearchCriterion(criterion);
        }
        
        // for consistent presentation ...
        CorrectionActionHelper.sortForDisplay(document.getCorrectionChangeGroup());
        
        //show the document when users choose documents
//      Don't need this if multiple dropdown keep holding choice from user
        HttpSession session = request.getSession(true);
        if (session.getAttribute("groupId") != null) {
            String[] groupId = (String[]) session.getAttribute("groupId");
            showAllEntries(groupId, errorCorrectionForm);
            errorCorrectionForm.setEditableFlag("N");
            //manualEditFlag is for activate a button for asking user to ask edit the docu.
            errorCorrectionForm.setManualEditFlag("N");
        }
        
        return mapping.findForward(Constants.MAPPING_BASIC);
    }
    
    /**
     * Remove a search criterion from a group.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward removeSearchCriterion(ActionMapping mapping, ActionForm form, 
            HttpServletRequest request, HttpServletResponse response) 
    throws Exception {
        
        CorrectionForm errorCorrectionForm = (CorrectionForm) form;
        document = (CorrectionDocument) errorCorrectionForm.getDocument();
        // rebuild the document state
        CorrectionActionHelper.rebuildDocumentState(request, errorCorrectionForm);
        
        // load the correction group from the request
        Integer[] criterionIndex = 
            CorrectionActionHelper.getCriterionToDelete(request, document);
        CorrectionChangeGroup group = document.getCorrectionGroup(criterionIndex[0]);
        CorrectionCriteria criterion = group.getSearchCriterion(criterionIndex[1]);
        // remove the criterion from the search criteria owned by the correction group
        group.getCorrectionCriteria().remove(criterion);
        
        // for consistent presentation ...
        CorrectionActionHelper.sortForDisplay(document.getCorrectionChangeGroup());
        
        //show the document when users choose documents
//      Don't need this if multiple dropdown keep holding choice from user
        HttpSession session = request.getSession(true);
        if (session.getAttribute("groupId") != null) {
            String[] groupId = (String[]) session.getAttribute("groupId");
            showAllEntries(groupId, errorCorrectionForm);
            errorCorrectionForm.setEditableFlag("N");
            //manualEditFlag is for activate a button for asking user to ask edit the docu.
            errorCorrectionForm.setManualEditFlag("N");
        }
        
        return mapping.findForward(Constants.MAPPING_BASIC);
    }
    
    /**
     * Add a new replacement specification to a specific group of specifications.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward addReplacementSpecification(ActionMapping mapping, ActionForm form, 
            HttpServletRequest request, HttpServletResponse response) 
    throws Exception {
        
        CorrectionForm errorCorrectionForm = (CorrectionForm) form;
        document = (CorrectionDocument) errorCorrectionForm.getDocument();
        // rebuild the document state
        CorrectionActionHelper.rebuildDocumentState(request, errorCorrectionForm);
        
        Map groupNumbers = new HashMap();
        for(Iterator i = document.getCorrectionChangeGroup().iterator(); i.hasNext();) {
            CorrectionChangeGroup g = (CorrectionChangeGroup) i.next();
            groupNumbers.put(g.getCorrectionChangeGroupLineNumber(), g);
        }
        
        for(Iterator i = groupNumbers.keySet().iterator(); i.hasNext();) {
            CorrectionChangeGroup correctionGroup = (CorrectionChangeGroup) groupNumbers.get(i.next());
            
            // populate the specification submitted to be added
            CorrectionChange specification = 
                CorrectionActionHelper
                .getFromRequestNewReplacementSpecification(request, correctionGroup.getCorrectionChangeGroupLineNumber());
            
            if(null == specification) {
                // not the specification we intended to add.
                continue;
            }
            
            specification.setFinancialDocumentNumber(document.getFinancialDocumentNumber());
            
            // add the new specification to the appropriate group of search criteria owned by the document
            correctionGroup.addReplacementSpecification(specification);
        }
        
        // for consistent presentation ...
        CorrectionActionHelper.sortForDisplay(document.getCorrectionChangeGroup());
        
        //show the document when users choose documents
//      Don't need this if multiple dropdown keep holding choice from user
        HttpSession session = request.getSession(true);
        if (session.getAttribute("groupId") != null) {
            String[] groupId = (String[]) session.getAttribute("groupId");
            showAllEntries(groupId, errorCorrectionForm);
            errorCorrectionForm.setEditableFlag("N");
            //manualEditFlag is for activate a button for asking user to ask edit the docu.
            errorCorrectionForm.setManualEditFlag("N");
        }
        
        return mapping.findForward(Constants.MAPPING_BASIC);
    }
    
    /**
     * Remove a replacement specification from a group.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward removeReplacementSpecification(ActionMapping mapping, ActionForm form, 
            HttpServletRequest request, HttpServletResponse response) 
    throws Exception {
        
        CorrectionForm errorCorrectionForm = (CorrectionForm) form;
        document = (CorrectionDocument) errorCorrectionForm.getDocument();
        // rebuild the document state
        CorrectionActionHelper.rebuildDocumentState(request, errorCorrectionForm);
        
        // load the correction group from the request
        Integer[] specificationIndex = 
            CorrectionActionHelper.getSpecificationToDelete(request, document);
        CorrectionChangeGroup group = document.getCorrectionGroup(specificationIndex[0]);
        CorrectionChange specification = 
            group.getReplacementSpecification(specificationIndex[1]);
        // remove the criterion from the search criteria owned by the correction group
        group.getCorrectionChange().remove(specification);
        
        // for consistent presentation ...
        CorrectionActionHelper.sortForDisplay(document.getCorrectionChangeGroup());
        
        //show the document when users choose documents
//      Don't need this if multiple dropdown keep holding choice from user
        HttpSession session = request.getSession(true);
        if (session.getAttribute("groupId") != null) {
            String[] groupId = (String[]) session.getAttribute("groupId");
            showAllEntries(groupId, errorCorrectionForm);
            errorCorrectionForm.setEditableFlag("N");
            //manualEditFlag is for activate a button for asking user to ask edit the docu.
            errorCorrectionForm.setManualEditFlag("N");
        }
        
        return mapping.findForward(Constants.MAPPING_BASIC);
    }
    
    /**
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @param withReplacement
     * @return
     * @throws Exception
     */
    public ActionForward searchAndReplaceWithCriteria(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response)
    throws Exception {
        
        originEntryService = (OriginEntryService) SpringServiceLocator.getBeanFactory().getBean("glOriginEntryService");
        originEntryGroupService = (OriginEntryGroupService) SpringServiceLocator.getBeanFactory().getBean("glOriginEntryGroupService");
        
        CorrectionForm errorCorrectionForm = (CorrectionForm) form;
        document = (CorrectionDocument) errorCorrectionForm.getDocument();
        // rebuild the document state
        CorrectionActionHelper.rebuildDocumentState(request, errorCorrectionForm);
        
        Collection<OriginEntry> resultCorrectionList = new ArrayList(); 
        String[] groupId;
        groupId = errorCorrectionForm.getGroupIdList(); 
        
        List correctionGroups = document.getCorrectionChangeGroup();
        
        //check required field and show error messages
        /*if (!checkEmptyValues(correctionGroups, groupId)){
            CorrectionActionHelper.sortForDisplay(document.getCorrectionChangeGroup());
            
            return mapping.findForward(Constants.MAPPING_BASIC);
        }*/
        
        
        resultCorrectionList = searchAndReplace(groupId, errorCorrectionForm);
        
        errorCorrectionForm.setAllEntries(resultCorrectionList);
        
        OriginEntryGroup newOriginEntryGroup = new OriginEntryGroup();
        java.sql.Date today = new java.sql.Date(System.currentTimeMillis());
        
        if (errorCorrectionForm.getDeleteOutput() == null){
            
        
        
        newOriginEntryGroup = originEntryGroupService.createGroup(today, "GLCP", true, true, true);
        
        } else {
            newOriginEntryGroup = originEntryGroupService.createGroup(today, "GLCP", true, false, true);
        }
        
        for (OriginEntry oe: resultCorrectionList) {
            originEntryService.createEntry(oe, newOriginEntryGroup);
            
        }
        
        
        return mapping.findForward(Constants.MAPPING_BASIC);
    }
    
    /**
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward loadDocument(ActionMapping mapping, ActionForm form, 
            HttpServletRequest request, HttpServletResponse response)    {
        
        CorrectionForm errorCorrectionForm = (CorrectionForm) form;
        CorrectionActionHelper.rebuildDocumentState(request, errorCorrectionForm);
        
        HttpSession session = request.getSession(true);
        String groupId[] = request.getParameterValues("pending-origin-entry-group-id");
        showAllEntries(errorCorrectionForm.getGroupIdList(), errorCorrectionForm);
        
        if (errorCorrectionForm.getAllEntries().size() > 0){
             
            if (errorCorrectionForm.getEditMethod().equals("manual")) {
                errorCorrectionForm.setManualEditFlag("Y");    
            }
            
        }
        
        //not need to if multiple dropdown keep values
        session.setAttribute("groupId", errorCorrectionForm.getGroupIdList());
        
        
        
        return mapping.findForward(Constants.MAPPING_BASIC);
    }
    
    /**
     * Change operator to search.
     * 
     * @param op
     * @param field 
     * @return field
     */
    
    public String changeSearchField(String op, String field){
        
        if (op.equals("ne")){ return "!" + field; }
        
        if (op.equals("gt")){ return ">" + field; }
        
        if (op.equals("lt")){ return "<" + field; }
        
        if (op.equals("sw")){ return field + "%"; }
        
        if (op.equals("ew")){ return "%" + field; }
        
        return "%" + field + "%";
        
    }
    
    
    /**
     * Method for Route
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    
    public ActionForward route(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        return mapping.findForward(Constants.MAPPING_BASIC);
    }
    
    
    /**
     * Method for Save
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    
    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        return mapping.findForward(Constants.MAPPING_BASIC);
    }
    
    /**
     * Method for Approve
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    
    public ActionForward blanketApprove(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return mapping.findForward(Constants.MAPPING_BASIC);
        
        
    }
    
    /**
     * Method for Approve
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    
    public ActionForward disapprove(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        return mapping.findForward(Constants.MAPPING_BASIC);
    }
    
    /**
     * Method for Cancel
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    
    public ActionForward cancel(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        return mapping.findForward(Constants.MAPPING_CANCEL);
    }
    
    
    /**
     * Method for Close
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    
    public ActionForward close(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception {
        return mapping.findForward(Constants.MAPPING_CLOSE);
    }   
    
    
   
    
    /**
     * Creating search Map from searchField and searchValues
     * 
     * @param CorrectionChangeGroup
     * @return Map fieldValues
     */
    private Map createSearchMap(CorrectionChangeGroup correctionGroup){
        Map fieldValues = new HashMap();
        CorrectionCriteria correctionSearchCriterion;
        Iterator fieldIter = correctionGroup.getCorrectionCriteria().iterator();
        while (fieldIter.hasNext()){
            correctionSearchCriterion = (CorrectionCriteria) fieldIter.next();
            String operator = correctionSearchCriterion.getOperator();
            String searchValue = correctionSearchCriterion.getCorrectionFieldValue();
            String searchField = correctionSearchCriterion.getCorrectionFieldName();
            if (!operator.equals("eq")){
                //Add operator
                searchValue = changeSearchField(operator, searchValue);
            }
            
            //check duplicate field, and make the Value list as Collection
            if (fieldValues.containsKey(searchField)){
                Collection fieldValueCollection = new ArrayList(); 
                
                if(fieldValues.get(searchField) instanceof Collection){
                    fieldValueCollection = (Collection) fieldValues.get(searchField);
                    
                } else {
                    String previousSearchValue = (String) fieldValues.get(searchField);
                    fieldValueCollection.add(previousSearchValue);
                }
                
                fieldValueCollection.add(searchValue);
                fieldValues.put(searchField, fieldValueCollection);
                
            } else {
                fieldValues.put(searchField, searchValue);
            }
        }
        return fieldValues;
    }
    
    
    /**
     * Create a new output file with all original Entries
     * 
     * @param newOriginEntryGroup
     * @param groupId
     */
    
    private void createNewOutput(OriginEntryGroup newOriginEntryGroup, String groupId){
        
        originEntryDao = (OriginEntryDao) SpringServiceLocator.getBeanFactory().getBean("glOriginEntryDao");
        originEntryService = (OriginEntryService) SpringServiceLocator.getBeanFactory().getBean("glOriginEntryService");
        // Collection returnCollection = new ArrayList();
        //create all OriginEntry which will change GroupID to newOriginEntryGroup ID
        Map groupIdMap = new HashMap();
        groupIdMap.put("entryGroupId", groupId);
        
        //get all Entries
        Collection oldEntries = originEntryDao.getMatchingEntriesByCollection(groupIdMap);
        
        //change the Entries' groupId and create all
        Iterator oldEntryIter = oldEntries.iterator();
        while (oldEntryIter.hasNext()){
            OriginEntry eachEntry = (OriginEntry) oldEntryIter.next();
            eachEntry.setEntryGroupId(newOriginEntryGroup.getId());
            //returnCollection.add(eachEntry);
            originEntryService.createEntry(eachEntry, newOriginEntryGroup);
            
        }
        
        //return returnCollection;
    }
    
    /**
     * Replace entries.
     * 
     * @param eachReplaceEntries
     * @param correctionGroup
     */
    public OriginEntry replaceOriginEntryValues(OriginEntry eachReplaceEntries, CorrectionChangeGroup correctionGroup) throws Exception{
        
        
       
        CorrectionChange correctionReplacementSpecification;
        Iterator replaceIter = correctionGroup.getCorrectionChange().iterator();
        
        while (replaceIter.hasNext()){
            correctionReplacementSpecification = (CorrectionChange) replaceIter.next();
            String replaceValue = correctionReplacementSpecification.getCorrectionFieldValue();
            String replaceField = correctionReplacementSpecification.getCorrectionFieldName();
           
            if (replaceField.equals("financialDocumentReversalDate")){
                //convert String to Date
                Date convertDate = SpringServiceLocator.getDateTimeService().convertToSqlDate(replaceValue);
                BeanUtils.setProperty(eachReplaceEntries, replaceField, convertDate);
                
            }
            if (replaceField.equals("transactionDate")){
                //convert String to Date
                Date convertDate = SpringServiceLocator.getDateTimeService().convertToSqlDate(replaceValue);
                BeanUtils.setProperty(eachReplaceEntries, replaceField, convertDate);
            }
            
            if (replaceField.equals("transactionLedgerEntrySequenceNumber")){
                //convert String to Integer
                int convertInt = Integer.parseInt(replaceValue);
                BeanUtils.setProperty(eachReplaceEntries, replaceField, new Integer(convertInt));
            }
            if (replaceField.equals("transactionLedgerEntryAmount")){
                BeanUtils.setProperty(eachReplaceEntries, replaceField, new KualiDecimal(replaceValue));
            }
            
            if (replaceField.equals("universityFiscalYear")){
                //convert String to Integer
                int convertInt = Integer.parseInt(replaceValue);
                BeanUtils.setProperty(eachReplaceEntries, replaceField, new Integer(convertInt));
            }
            
            BeanUtils.setProperty(eachReplaceEntries, replaceField, replaceValue);
            
            
        }   
        return eachReplaceEntries;
    }
    
    
    /**
     * check required fields and show error messages.
     * 
     * @param correctionGroups
     * @param groupId
     * @return boolean returnVal
     */
    public boolean checkEmptyValues(List correctionGroups, String[] groupId){
        boolean returnVal = true; 
        
        //if the description is required, then  
        /*if (document.getDocumentHeader().getFinancialDocumentDescription() == null) {
         //GlobalVariables.getErrorMap().put("documentHeader.financialDocumentDescription", KeyConstants.ERROR_REQUIRED, "Financial Document Description");
          GlobalVariables.getErrorMap().put("document.documentHeader.financialDocumentDescription", KeyConstants.ERROR_REQUIRED, "Financial Document Description");
          }*/
        
        if (groupId== null) {
            GlobalVariables.getErrorMap().put("searchFieldError", 
                    KeyConstants.ERROR_GL_ERROR_CORRECTION_ORIGINGROUP_REQUIRED);
            returnVal = false;
        }
        Iterator groupIter = correctionGroups.iterator();
        while (groupIter.hasNext()) {
            CorrectionChangeGroup correctionGroup = (CorrectionChangeGroup) groupIter.next();
            if (correctionGroup.getCorrectionCriteria().size() < 1){
                //displaing error message
                GlobalVariables.getErrorMap().put("searchFieldError", 
                        KeyConstants.ERROR_GL_ERROR_CORRECTION_SEARCHFIELD_REQUIRED);
                returnVal = false;
            }
            if (correctionGroup.getCorrectionChange().size() < 1){
                GlobalVariables.getErrorMap().put("searchFieldError", 
                        KeyConstants.ERROR_GL_ERROR_CORRECTION_MODIFYFIELD_REQUIRED);
                returnVal = false;
            }
            
        }
        
        return returnVal;
    }
    
    
    /**
     * Control main dropdown button.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward chooseMainDropdown(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response)
    throws Exception {
        
        CorrectionForm errorCorrectionForm = (CorrectionForm) form;
        document = (CorrectionDocument) errorCorrectionForm.getDocument();
        
        //errorCorrectionForm.setChooseSystem(request.getParameter("chooseSystem"));
        
        //request.setAttribute("chooseSystem", request.getParameter("chooseSystem"));
        //errorCorrectionForm.setEditMethod(request.getParameter("editMethod"));
        //String editMethod = request.getParameter("editMethod");
        //request.setAttribute("editMethod", request.getParameter("editMethod"));
        
        //document.addCorrectionGroup(new CorrectionChangeGroup());
        
        return mapping.findForward(Constants.MAPPING_BASIC);
    }
    
    
    /**
     * 
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward viewResults(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        
        
        
        
        
        
        return mapping.findForward(Constants.MAPPING_BASIC);
    }    
    
    
    /**
     * Show one entry for Manual edit
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward showOneEntry(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        HttpSession session = request.getSession(true);
        
        originEntryDao = (OriginEntryDao) SpringServiceLocator.getBeanFactory().getBean("glOriginEntryDao");
        CorrectionForm errorCorrectionForm = (CorrectionForm) form;
        
        Map searchMap = new HashMap();
        
        
        String stringEditEntryId = request.getParameter("methodToCall.showOneEntry");
        Integer editEntryId = Integer.parseInt(stringEditEntryId);
        OriginEntry eachEntry = (OriginEntry) originEntryDao.getExactMatchingEntry(editEntryId);
        errorCorrectionForm.setEachEntryForManualEdit(eachEntry);
        
        //originEntryDao.deleteEntry(eachEntry);
        
        String[] newGroupId = {(String) session.getAttribute("newGroupId")};
        
        showAllEntries(newGroupId, errorCorrectionForm);
        
        errorCorrectionForm.setEditableFlag("Y");
        //manualEditFlag is for activate a button for asking user to ask edit the docu.
        errorCorrectionForm.setManualEditFlag("N");
        
        CorrectionActionHelper.rebuildDocumentState(request, errorCorrectionForm);
        
        return mapping.findForward(Constants.MAPPING_BASIC);
    }    
    
    /**
     * Get request from User input and change entry values 
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward editEntry(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        originEntryDao = (OriginEntryDao) SpringServiceLocator.getBeanFactory().getBean("glOriginEntryDao");
        originEntryGroupDao = (OriginEntryGroupDao) SpringServiceLocator.getBeanFactory().getBean("glOriginEntryGroupDao");
        originEntryService = (OriginEntryService) SpringServiceLocator.getBeanFactory().getBean("glOriginEntryService");
        
        CorrectionForm errorCorrectionForm = (CorrectionForm) form;
        
        
        Date convertDate;
        int convertInt;
        
        OriginEntry oe = new OriginEntry();
        
        String editAccountNumber = request.getParameter("editAccountNumber");
        String editFinancialDocumentNumber = request.getParameter("editFinancialDocumentNumber");
        String editReferenceFinancialDocumentNumber = request.getParameter("editReferenceFinancialDocumentNumber");
        String editReferenceFinancialDocumentTypeCode = request.getParameter("editReferenceFinancialDocumentTypeCode");
        String editFinancialDocumentReversalDate = request.getParameter("editFinancialDocumentReversalDate");
        String editFinancialDocumentTypeCode = request.getParameter("editFinancialDocumentTypeCode");
        String editFinancialBalanceTypeCode = request.getParameter("editFinancialBalanceTypeCode");
        String editChartOfAccountsCode = request.getParameter("editChartOfAccountsCode");
        String editFinancialObjectTypeCode = request.getParameter("editFinancialObjectTypeCode");
        String editFinancialObjectCode = request.getParameter("editFinancialObjectCode");
        String editFinancialSubObjectCode = request.getParameter("editFinancialSubObjectCode");
        String editFinancialSystemOriginationCode = request.getParameter("editFinancialSystemOriginationCode");
        String editReferenceFinancialSystemOriginationCode = request.getParameter("editReferenceFinancialSystemOriginationCode");
        String editOrganizationDocumentNumber = request.getParameter("editOrganizationDocumentNumber");
        String editOrganizationReferenceId = request.getParameter("editOrganizationReferenceId");
        String editProjectCode = request.getParameter("editProjectCode");
        String editSubAccountNumber = request.getParameter("editSubAccountNumber");
        String editTransactionDate = request.getParameter("editTransactionDate");
        String editTransactionDebitCreditCode = request.getParameter("editTransactionDebitCreditCode");
        String editTransactionEncumbranceUpdateCode = request.getParameter("editTransactionEncumbranceUpdateCode");
        String editTransactionLedgerEntrySequenceNumber = request.getParameter("editTransactionLedgerEntrySequenceNumber");
        String editTransactionLedgerEntryAmount = request.getParameter("editTransactionLedgerEntryAmount");
        String editTransactionLedgerEntryDescription = request.getParameter("editTransactionLedgerEntryDescription");
        String editUniversityFiscalPeriodCode = request.getParameter("editUniversityFiscalPeriodCode");
        String editUniversityFiscalYear = request.getParameter("editUniversityFiscalYear");
        String editBudgetYear = request.getParameter("editBudgetYear");
        
        
        oe.setAccountNumber(editAccountNumber);
        oe.setFinancialDocumentNumber(editFinancialDocumentNumber);
        oe.setReferenceFinancialDocumentNumber(editReferenceFinancialDocumentNumber);
        oe.setReferenceFinancialDocumentTypeCode(editReferenceFinancialDocumentTypeCode);
        
        if (!(editFinancialDocumentReversalDate == null | editFinancialDocumentReversalDate.equals(""))) {
            convertDate = SpringServiceLocator.getDateTimeService().convertToSqlDate(editFinancialDocumentReversalDate);
            oe.setFinancialDocumentReversalDate(convertDate);
        }
        
        oe.setFinancialDocumentTypeCode(editFinancialDocumentTypeCode);
        oe.setFinancialBalanceTypeCode(editFinancialBalanceTypeCode);
        oe.setChartOfAccountsCode(editChartOfAccountsCode);
        oe.setFinancialObjectTypeCode(editFinancialObjectTypeCode);
        oe.setFinancialObjectCode(editFinancialObjectCode);
        oe.setFinancialSubObjectCode(editFinancialSubObjectCode);
        oe.setFinancialSystemOriginationCode(editFinancialSystemOriginationCode);
        oe.setReferenceFinancialSystemOriginationCode(editReferenceFinancialSystemOriginationCode);
        oe.setOrganizationDocumentNumber(editOrganizationDocumentNumber);
        oe.setOrganizationReferenceId(editOrganizationReferenceId);
        oe.setProjectCode(editProjectCode);
        oe.setSubAccountNumber(editSubAccountNumber);
        
        if (!(editTransactionDate == null | editTransactionDate.equals(""))) {
            convertDate = SpringServiceLocator.getDateTimeService().convertToSqlDate(editTransactionDate);
            oe.setTransactionDate(convertDate);
        }
        
        oe.setTransactionDebitCreditCode(editTransactionDebitCreditCode);
        oe.setTransactionEncumbranceUpdateCode(editTransactionEncumbranceUpdateCode);
        
        if (!(editTransactionLedgerEntrySequenceNumber == null | editTransactionLedgerEntrySequenceNumber.equals(""))) {
            convertInt = Integer.parseInt(editTransactionLedgerEntrySequenceNumber);
            oe.setTransactionLedgerEntrySequenceNumber(new Integer(convertInt));
        }
        if (!(editTransactionLedgerEntryAmount == null | editTransactionLedgerEntryAmount.equals(""))) {
            oe.setTransactionLedgerEntryAmount(new KualiDecimal(editTransactionLedgerEntryAmount));
        }
        
        oe.setTransactionLedgerEntryDescription(editTransactionLedgerEntryDescription);
        oe.setUniversityFiscalPeriodCode(editUniversityFiscalPeriodCode);
        
        if (!(editUniversityFiscalYear == null | editUniversityFiscalYear.equals(""))) {
            convertInt = Integer.parseInt(editUniversityFiscalYear);
            oe.setUniversityFiscalYear(new Integer(convertInt));
        }
        
        oe.setBudgetYear(editBudgetYear);
        
        //set entryId
        //null id means user added a new entry. 
        HttpSession session = request.getSession(true);
        String[] newGroupId ={(String) session.getAttribute("newGroupId") } ;
        Integer editEntryId;
        String stringEditEntryId = request.getParameter("methodToCall.editEntry");
        
        if (stringEditEntryId != null) {
             editEntryId = Integer.parseInt(stringEditEntryId);
             oe.setEntryId(editEntryId);
             oe.setEntryGroupId(Integer.parseInt(newGroupId[0]));
             //remove the original OriginEntry
             OriginEntry originalOe = originEntryDao.getExactMatchingEntry(editEntryId);
             originEntryDao.deleteEntry(originalOe);
             
             //save the new OriginEntry
             originEntryDao.saveOriginEntry(oe);
             
             
        } else {
            
            OriginEntryGroup newOriginEntryGroup = originEntryGroupDao.getExactMatchingEntryGroup(Integer.parseInt(newGroupId[0]));
            originEntryService.createEntry(oe, newOriginEntryGroup);   
            
        }
        
        showAllEntries(newGroupId, errorCorrectionForm);
        
        OriginEntry newOriginEntry = new OriginEntry(); 
        errorCorrectionForm.setEachEntryForManualEdit(newOriginEntry);
        
        errorCorrectionForm.setEditableFlag("Y");
        //manualEditFlag is for activate a button for asking user to ask edit the docu.
        errorCorrectionForm.setManualEditFlag("N");
        
        CorrectionActionHelper.rebuildDocumentState(request, errorCorrectionForm);
        return mapping.findForward(Constants.MAPPING_BASIC);
        
        
    }
    
    /**
     * Search function for Manual edit
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward searchForManualEdit(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        
        CorrectionForm errorCorrectionForm = (CorrectionForm) form;
        document = (CorrectionDocument) errorCorrectionForm.getDocument();
        HttpSession session = request.getSession(true);
        
        
        CorrectionActionHelper.rebuildDocumentState(request, errorCorrectionForm);
        List correctionGroups = document.getCorrectionChangeGroup();
        String groupId;
        /*if (request.getAttribute("chooseSystem") == "file") {
            groupId = (String) session.getAttribute("newGroupId");
        } else { groupId = (String) session.getAttribute("newGroupId"); }*/
        groupId = (String) session.getAttribute("newGroupId");

        CorrectionChangeGroup correctionGroup;
        
        Map fieldValues = new HashMap();
        Collection searchResults = null;
        
        
        Iterator groupIter = correctionGroups.iterator();
        
        while (groupIter.hasNext()) {
            
            correctionGroup = (CorrectionChangeGroup) groupIter.next();
            fieldValues = createSearchMap(correctionGroup);
            
            fieldValues.put("entryGroupId", groupId);
        }
        
        
        LookupService lookupService = (LookupService) SpringServiceLocator.getBeanFactory().getBean("lookupService");
        //searchResults is collection of OriginEntry
        searchResults = (Collection) lookupService.findCollectionBySearchUnbounded(OriginEntry.class, fieldValues);
        
        errorCorrectionForm.setAllEntries(searchResults);
        
        errorCorrectionForm.setEditableFlag("Y");
        //manualEditFlag is for activate a button for asking user to ask edit the docu.
        errorCorrectionForm.setManualEditFlag("N");
        
        OriginEntry newOriginEntry = new OriginEntry(); 
        errorCorrectionForm.setEachEntryForManualEdit(newOriginEntry);
        
        
        return mapping.findForward(Constants.MAPPING_BASIC);
        
    }
    
    /**
     * Creating a new output and change values from Manual edit.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */ 
    public ActionForward manualErrorCorrection(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        
        CorrectionForm errorCorrectionForm = (CorrectionForm) form;
        originEntryGroupDao = (OriginEntryGroupDao) SpringServiceLocator.getBeanFactory().getBean("glOriginEntryGroupDao");
        
        HttpSession session = request.getSession(true);
        
        String newGroupId = (String) session.getAttribute("newGroupId");
        OriginEntryGroup newOriginEntryGroup = originEntryGroupDao.getExactMatchingEntryGroup(Integer.parseInt(newGroupId));
        
        //if user checked Delete Output file option
        if (errorCorrectionForm.getDeleteOutput().equals("on")) {
            newOriginEntryGroup.setProcess(false);
        } else {newOriginEntryGroup.setProcess(true);}
        
        newOriginEntryGroup.setValid(true);
        newOriginEntryGroup.setScrub(true);
        originEntryGroupDao.save(newOriginEntryGroup);
        
        
        
        return mapping.findForward(Constants.MAPPING_BASIC);
        
    }
    
    /**
     * Show all entries for Manual edit with groupId
     * 
     * @param groupId
     */
    public void showAllEntries(String[] groupId, CorrectionForm errorCorrectionForm){
 
        originEntryDao = (OriginEntryDao) SpringServiceLocator.getBeanFactory().getBean("glOriginEntryDao");
        Map searchMap = new HashMap();
        Collection resultFromGroupId = new ArrayList();
        
        // Configure the query.
       
        for (String eachGroupId: groupId){
            searchMap.put("entryGroupId", eachGroupId);
            Collection searchResult = originEntryDao.getMatchingEntriesByCollection(searchMap);
            resultFromGroupId.addAll(searchResult);
        }
       
        errorCorrectionForm.setAllEntries(resultFromGroupId);
        
        //all entries to a Map
        /*Iterator iter = resultFromGroupId.iterator();
        while (iter.hasNext()){
            OriginEntry oe = (OriginEntry) iter.next();
            errorCorrectionForm.getAllEntriesForManualEditHashMap().put(oe.getEntryId(), oe);
        }*/
        
    }
    
    /**
     * Search and replace with criteria.
     * 
     * @param groupId
     * @param CorrectionGroups
     */
    public Collection searchAndReplace(String[] groupId, CorrectionForm errorCorrectionForm){
        
        //originEntryGroupService = (OriginEntryGroupService) SpringServiceLocator.getBeanFactory().getBean("glOriginEntryGroupService");
        originEntryGroupDao = (OriginEntryGroupDao) SpringServiceLocator.getBeanFactory().getBean("glOriginEntryGroupDao");
        originEntryDao = (OriginEntryDao) SpringServiceLocator.getBeanFactory().getBean("glOriginEntryDao");
        LookupService lookupService = (LookupService) SpringServiceLocator.getBeanFactory().getBean("lookupService");
        document = (CorrectionDocument) errorCorrectionForm.getDocument();
        List correctionGroups = document.getCorrectionChangeGroup();
        Map fieldValues = new HashMap();
        Map resultMap = new HashMap();
        CorrectionChangeGroup correctionGroup;
        //Collection searchResults = null;
        OriginEntryGroup newOriginEntryGroup = null;
        
        Collection<OriginEntry> finalResult = new ArrayList();
        
        Map changedEntryMap = new HashMap();
        Collection returnCollection;
        
        //search using Groups and Fields
        for (int i=0; i<groupId.length; i+=1) {
            
            
            
            Iterator groupIter = correctionGroups.iterator();
            
            while (groupIter.hasNext()) {
                correctionGroup = (CorrectionChangeGroup) groupIter.next();
                
                //Set a Map with fields
                fieldValues = createSearchMap(correctionGroup);
                
                fieldValues.put("entryGroupId", groupId[i]);
                
                //searchResults is collection of OriginEntry
                //Collection<OriginEntry> searchResults = (Collection) lookupService.findCollectionBySearchUnbounded(OriginEntry.class, fieldValues);
                Collection searchResults = (Collection) lookupService.findCollectionBySearchUnbounded(OriginEntry.class, fieldValues);
                if (searchResults.size() > 0) {
                    
                    Iterator oeIter = searchResults.iterator();
                    while(oeIter.hasNext()){
                        OriginEntry oe = (OriginEntry) oeIter.next();
                    //for(OriginEntry oe: searchResults){
                        
                        OriginEntry replacedOriginEntry = new OriginEntry();
                        
                        try {
                            replacedOriginEntry = replaceOriginEntryValues(oe, correctionGroup);
                        } catch (Exception e){}
                        
                        changedEntryMap.put(oe.getEntryId(), oe);
                    }
                    
                }
                //initialize search field
                fieldValues = new HashMap();
             }
            
            //build result collection
            if (errorCorrectionForm.getMatchCriteriaOnly() == null){
                resultMap.put("entryGroupId", groupId[i]);
                Collection<OriginEntry> tempResultList = originEntryDao.getMatchingEntriesByCollection(resultMap);
                
                //not adding entries which are in changedMap 
                for(OriginEntry oe: tempResultList){
                    if (changedEntryMap.get(oe.getEntryId()) == null){
                        finalResult.add(oe);    
                    }
                        
                }
            }
        }
        
        
        if (errorCorrectionForm.getMatchCriteriaOnly() == null){
            
            finalResult.addAll(changedEntryMap.values());
            returnCollection = finalResult;
        
        } else {
            returnCollection = changedEntryMap.values();
        }
        
        CorrectionActionHelper.sortForDisplay(document.getCorrectionChangeGroup());
        
        
        return returnCollection;
    }
    
    
    /**
     * Search and replace with Criteria in FileUpload
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward fileUploadSearchAndReplaceWithCriteria(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response)
    throws Exception {
        
        CorrectionForm errorCorrectionForm = (CorrectionForm) form;
        document = (CorrectionDocument) errorCorrectionForm.getDocument();
        //TODO: Change later. The purpose of this method is just for groupId. 
        //This method will be useless if groupId set from session using session "chooseSystem".
        
        // rebuild the document state
        CorrectionActionHelper.rebuildDocumentState(request, errorCorrectionForm);
        
        
        // Configure the query.
        //PersistenceBroker broker = PersistenceBrokerFactory.defaultPersistenceBroker();
        
        HttpSession session = request.getSession(true);
        String groupId[] = {(String) session.getAttribute("newGroupId")};
        
        List correctionGroups = document.getCorrectionChangeGroup();
        
        //check required field and show error messages
        /*if (!checkEmpteyValues(correctionGroups, groupId)){
         CorrectionActionHelper.sortForDisplay(document.getCorrectionChangeGroup());
         
         return mapping.findForward(Constants.MAPPING_BASIC);
         }*/
        
//      the boolean is readOnly, show output only - not storing in DB 
        searchAndReplace(groupId, errorCorrectionForm);
        
        
        return mapping.findForward(Constants.MAPPING_BASIC);
        
    }
    
    
    //This method is for giving options to user for manual edit.
    //If user click 'edit', thne all entries store in DB. 
    
    public ActionForward showEditMethod(ActionMapping mapping, ActionForm form,
        HttpServletRequest request, HttpServletResponse response) {
        
        CorrectionForm errorCorrectionForm = (CorrectionForm) form;
        //CorrectionActionHelper.rebuildDocumentState(request, errorCorrectionForm);
        
        HttpSession session = request.getSession(true);
        originEntryGroupService = (OriginEntryGroupService) SpringServiceLocator.getBeanFactory().getBean("glOriginEntryGroupService");
        originEntryService = (OriginEntryService) SpringServiceLocator.getBeanFactory().getBean("glOriginEntryService");
        
        
        OriginEntryGroup newOriginEntryGroup;
        
        //in case of file upload
        if (errorCorrectionForm.getChooseSystem().equals("file")) {
            
            String[] groupId = {(String) session.getAttribute("newGroupId")};
            showAllEntries(groupId, errorCorrectionForm);
            
        } else {
                //To get all entries
                String[] groupId = (String[]) session.getAttribute("groupId");
                showAllEntries(groupId, errorCorrectionForm);
        
                //create an OriginEntryGroup
                java.sql.Date today = new java.sql.Date(System.currentTimeMillis());
                //create a docu with all indicators as false(N)
                newOriginEntryGroup = originEntryGroupService.createGroup(today, "GLCP", false, false, false);
        
                //Create new Entries with newOriginEntryGroup 
                Collection<OriginEntry> newEntries = errorCorrectionForm.getAllEntries();
                for (OriginEntry oe : newEntries){
                    oe.setEntryGroupId(newOriginEntryGroup.getId());
                    originEntryService.createEntry(oe, newOriginEntryGroup);
                }
                String[] groupIdList = {newOriginEntryGroup.getId().toString()};
                showAllEntries(groupIdList, errorCorrectionForm);
                session.setAttribute("newGroupId", newOriginEntryGroup.getId().toString());
        }
        
        
        errorCorrectionForm.setEditableFlag("Y");
        //manualEditFlag is for activate a button for asking user to ask edit the docu.
        errorCorrectionForm.setManualEditFlag("N");
        
        //for add an entry
        OriginEntry newOriginEntry = new OriginEntry(); 
        errorCorrectionForm.setEachEntryForManualEdit(newOriginEntry);
        
        //keep track the newGroupId in session
        
        
        //request.setAttribute("pending-origin-entry-group-id", groupId);
        
        
        
        
        
        
        return mapping.findForward(Constants.MAPPING_BASIC);
    }
    
    
    /**
     * Add a new search criteria to a specific group of criteria.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward addSearchCriterionForManualEdit(ActionMapping mapping, ActionForm form, 
            HttpServletRequest request, HttpServletResponse response) 
    throws Exception {
        
        CorrectionForm errorCorrectionForm = (CorrectionForm) form;
        document = (CorrectionDocument) errorCorrectionForm.getDocument();
        // rebuild the document state
        CorrectionActionHelper.rebuildDocumentState(request, errorCorrectionForm);
        
        Map groupNumbers = new HashMap();
        for(Iterator i = document.getCorrectionChangeGroup().iterator(); i.hasNext();) {
            CorrectionChangeGroup g = (CorrectionChangeGroup) i.next();
            groupNumbers.put(g.getCorrectionChangeGroupLineNumber(), g);
        }
        
        for(Iterator i = groupNumbers.keySet().iterator(); i.hasNext();) {
            CorrectionChangeGroup correctionGroup = (CorrectionChangeGroup) groupNumbers.get(i.next());
            
            // populate the criterion submitted to be added
            CorrectionCriteria criterion = 
                CorrectionActionHelper
                .getFromRequestNewSearchCriterion(request, correctionGroup.getCorrectionChangeGroupLineNumber());
            
            if(null == criterion) {
                // not the criterion we intended to add.
                continue;
            }
            
            criterion.setFinancialDocumentNumber(document.getFinancialDocumentNumber());
            
            // add the new criterion to the appropriate group of search criteria owned by the document
            correctionGroup.addSearchCriterion(criterion);
        }
        
        // for consistent presentation ...
        CorrectionActionHelper.sortForDisplay(document.getCorrectionChangeGroup());
        
        HttpSession session = request.getSession(true);
        String[] groupId = { (String) session.getAttribute("newGroupId") };
        showAllEntries(groupId, errorCorrectionForm);
        
        errorCorrectionForm.setEditableFlag("Y");
        //manualEditFlag is for activate a button for asking user to ask edit the docu.
        errorCorrectionForm.setManualEditFlag("N");
        
        //for add an entry
        OriginEntry newOriginEntry = new OriginEntry(); 
        errorCorrectionForm.setEachEntryForManualEdit(newOriginEntry);
        
        
        return mapping.findForward(Constants.MAPPING_BASIC);
    }
    
    
    /**
     * Remove a search criterion from a group.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward removeSearchCriterionForManualEdit(ActionMapping mapping, ActionForm form, 
            HttpServletRequest request, HttpServletResponse response) 
    throws Exception {
        
        CorrectionForm errorCorrectionForm = (CorrectionForm) form;
        document = (CorrectionDocument) errorCorrectionForm.getDocument();
        // rebuild the document state
        CorrectionActionHelper.rebuildDocumentState(request, errorCorrectionForm);
        
        // load the correction group from the request
        Integer[] criterionIndex = 
            CorrectionActionHelper.getCriterionToDelete(request, document);
        CorrectionChangeGroup group = document.getCorrectionGroup(criterionIndex[0]);
        CorrectionCriteria criterion = group.getSearchCriterion(criterionIndex[1]);
        // remove the criterion from the search criteria owned by the correction group
        group.getCorrectionCriteria().remove(criterion);
        
        // for consistent presentation ...
        CorrectionActionHelper.sortForDisplay(document.getCorrectionChangeGroup());
        
        HttpSession session = request.getSession(true);
        String[] groupId = {(String) session.getAttribute("newGroupId")};
        showAllEntries(groupId, errorCorrectionForm);
        
        errorCorrectionForm.setEditableFlag("Y");
        //manualEditFlag is for activate a button for asking user to ask edit the docu.
        errorCorrectionForm.setManualEditFlag("N");
        
        //for add an entry
        OriginEntry newOriginEntry = new OriginEntry(); 
        errorCorrectionForm.setEachEntryForManualEdit(newOriginEntry);
        
        return mapping.findForward(Constants.MAPPING_BASIC);
    }
    
    
    public ActionForward deleteEntry(ActionMapping mapping, ActionForm form, 
            HttpServletRequest request, HttpServletResponse response) {
        
        originEntryDao = (OriginEntryDao) SpringServiceLocator.getBeanFactory().getBean("glOriginEntryDao");
        CorrectionForm errorCorrectionForm = (CorrectionForm) form;
        
        String stringEditEntryId = request.getParameter("methodToCall.deleteEntry");
        Integer editEntryId = Integer.parseInt(stringEditEntryId);
        OriginEntry eachEntry = (OriginEntry) originEntryDao.getExactMatchingEntry(editEntryId);
        
        originEntryDao.deleteEntry(eachEntry);
        HttpSession session = request.getSession(true);
        String[] newGroupId = {(String) session.getAttribute("newGroupId")};
        showAllEntries(newGroupId, errorCorrectionForm);
        
        OriginEntry newOriginEntry = new OriginEntry(); 
        errorCorrectionForm.setEachEntryForManualEdit(newOriginEntry);
        
        errorCorrectionForm.setEditableFlag("Y");
        //manualEditFlag is for activate a button for asking user to ask edit the docu.
        errorCorrectionForm.setManualEditFlag("N");
        
        CorrectionActionHelper.rebuildDocumentState(request, errorCorrectionForm);
        
        return mapping.findForward(Constants.MAPPING_BASIC);
    }
    
    public ActionForward showOutputFile(ActionMapping mapping, ActionForm form, 
            HttpServletRequest request, HttpServletResponse response) {
        
        CorrectionForm errorCorrectionForm = (CorrectionForm) form;
        document = (CorrectionDocument) errorCorrectionForm.getDocument();
        // rebuild the document state
        CorrectionActionHelper.rebuildDocumentState(request, errorCorrectionForm);
        
        Collection<OriginEntry> resultCorrectionList = new ArrayList(); 
        String[] groupId;
        groupId = errorCorrectionForm.getGroupIdList(); 
        
        List correctionGroups = document.getCorrectionChangeGroup();
        
        resultCorrectionList = searchAndReplace(groupId, errorCorrectionForm);
        
        errorCorrectionForm.setAllEntries(resultCorrectionList);
        
        return mapping.findForward(Constants.MAPPING_BASIC);
        
    }
    
}

