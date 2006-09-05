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

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;
import org.displaytag.tags.TableTagParameters;
import org.displaytag.util.ParamEncoder;
import org.kuali.Constants;
import org.kuali.KeyConstants;
import org.kuali.core.lookup.keyvalues.OEGDateComparator;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.LookupService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.core.web.struts.action.KualiDocumentActionBase;
import org.kuali.core.web.struts.form.KualiDocumentFormBase;
import org.kuali.module.gl.bo.CorrectionChange;
import org.kuali.module.gl.bo.CorrectionChangeGroup;
import org.kuali.module.gl.bo.CorrectionCriteria;
import org.kuali.module.gl.bo.OriginEntry;
import org.kuali.module.gl.bo.OriginEntryGroup;
import org.kuali.module.gl.dao.CorrectionChangeDao;
import org.kuali.module.gl.dao.CorrectionChangeGroupDao;
import org.kuali.module.gl.dao.CorrectionCriteriaDao;
import org.kuali.module.gl.document.CorrectionDocument;
import org.kuali.module.gl.service.CorrectionDocumentService;
import org.kuali.module.gl.service.OriginEntryGroupService;
import org.kuali.module.gl.service.OriginEntryService;
import org.kuali.module.gl.web.struts.form.CorrectionForm;

import edu.iu.uis.eden.clientapp.IDocHandler;

/**
 * @author Laran Evans <lc278@cornell.edu> Shawn Choo <schoo@indiana.edu>
 * @version $Id: CorrectionAction.java,v 1.43 2006-09-05 15:30:31 schoo Exp $
 * 
 */

public class CorrectionAction extends KualiDocumentActionBase {


    private DateTimeService dateTimeService;
    private CorrectionForm errorCorrectionForm;
    private CorrectionDocument document;
    private OriginEntryGroupService originEntryGroupService = (OriginEntryGroupService) SpringServiceLocator.getBeanFactory().getBean("glOriginEntryGroupService");
    private OriginEntryService originEntryService = (OriginEntryService) SpringServiceLocator.getBeanFactory().getBean("glOriginEntryService");
    
    

    private static final String[] DOCUMENT_LOAD_COMMANDS = { IDocHandler.ACTIONLIST_COMMAND, IDocHandler.DOCSEARCH_COMMAND, IDocHandler.SUPERUSER_COMMAND, IDocHandler.HELPDESK_ACTIONLIST_COMMAND };

    
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        // TODO Auto-generated method stub
        /*
        //check authorization manually, since the auth-check isn't inherited by this class
        String docTypeName = SpringServiceLocator.getDataDictionaryService().getDocumentTypeNameByClass(CorrectionDocument.class);
        DocumentAuthorizer cmDocAuthorizer = SpringServiceLocator.getDocumentAuthorizationService().getDocumentAuthorizer(docTypeName);
        KualiUser user = GlobalVariables.getUserSession().getKualiUser();
        if (!cmDocAuthorizer.canInitiate(docTypeName, user)) {
            throw new DocumentTypeAuthorizationException(user.getPersonUserIdentifier(), "initiate", docTypeName);
            
        } */
        CorrectionForm previousForm = (CorrectionForm) form;
        
        
            CorrectionForm errorCorrectionForm = (CorrectionForm) GlobalVariables.getUserSession().retrieveObject(Constants.CORRECTION_FORM_KEY);
        
        
            if (errorCorrectionForm != null ) {
            
                previousForm.setAllEntries(errorCorrectionForm.getAllEntries());
                previousForm.setEditableFlag(errorCorrectionForm.getEditableFlag());
                previousForm.setManualEditFlag(errorCorrectionForm.getManualEditFlag());
                //previousForm.setGroupIdList(errorCorrectionForm.getGroupIdList());
            }
        
            if (request.getParameter("document.correctionChangeGroupNextLineNumber") != null){
                CorrectionActionHelper.rebuildDocumentState(request, previousForm);
            }
        
            if (!previousForm.getMethodToCall().equals("viewResults")) {
                GlobalVariables.getUserSession().addObject(Constants.CORRECTION_FORM_KEY, previousForm);
            }
            
            Object displayTablePageNumber = GlobalVariables.getUserSession().retrieveObject(Constants.DISPLAY_TABLE_PAGE_NUMBER);
            Object displayTableColumnNumber = GlobalVariables.getUserSession().retrieveObject(Constants.DISPLAY_TABLE_COLUMN_NUMBER); 
            
            if (displayTablePageNumber != null){
                request.setAttribute("displayTablePageNumber", displayTablePageNumber.toString());
            }
            
            
            if (displayTableColumnNumber !=null){
                request.setAttribute("displayTableColumnNumber", displayTableColumnNumber.toString());
            }
            
            
            
        return super.execute(mapping, form, request, response);
    }
    public ActionForward uploadFile(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws FileNotFoundException, IOException {

        CorrectionForm errorCorrectionForm = (CorrectionForm) form;
        document = (CorrectionDocument) errorCorrectionForm.getDocument();
        
        java.sql.Date today = new java.sql.Date(System.currentTimeMillis());
        OriginEntryGroup newOriginEntryGroup = originEntryGroupService.createGroup(today, "GLCP", false, false, false);
        
        
        FormFile sourceFile = errorCorrectionForm.getSourceFile();
        String curDir = System.getProperty("user.dir");
        String lowerCaseFileName = StringUtils.lowerCase(sourceFile.getFileName());
        //sourceFile.setFileName(lowerCaseFileName);
        //fullFileName has file path
        String fullFileName = curDir + " - " + sourceFile.getFileName(); 
        Object fullFileNameObject = (Object) fullFileName;
        

        sourceFile.getInputStream();
        
        GlobalVariables.getUserSession().addObject("fileName", fullFileNameObject);
        //CommonsMultipartRequestHandler.CommonsFormFile test = (CommonsMultipartRequestHandler.CommonsFormFile) sourceFile;
        //CommonsMultipartRequestHandler test = (CommonsMultipartRequestHandler) sourceFile;
        
        
        /*DiskFile test = (DiskFile) sourceFile;
        String filePath = test.getFilePath();*/

        BufferedReader br = new BufferedReader(new InputStreamReader(sourceFile.getInputStream()));
        OriginEntry entryFromFile = new OriginEntry();
        int lineNumber = 0;
        try {
            String currentLine = br.readLine();
            while (currentLine != null ) {
                lineNumber += 1;
                if (!currentLine.equals("")){
                    
                    try{
                        entryFromFile.setFromTextFile(currentLine, lineNumber);
                        entryFromFile.setEntryGroupId(newOriginEntryGroup.getId());
                        originEntryService.createEntry(entryFromFile, newOriginEntryGroup);
                    } catch (NumberFormatException e){
                        
                    } 
                    
                    
                }
                
                currentLine = br.readLine();
            }
        }
        finally {
            br.close();
        }

        HttpSession session = request.getSession(true);
        session.setAttribute("newGroupId", newOriginEntryGroup.getId().toString());

        if (errorCorrectionForm.getEditMethod().equals("manual")) {
            String[] newGroupId = { newOriginEntryGroup.getId().toString() };
            showAllEntries(newGroupId, errorCorrectionForm, request);
            if (errorCorrectionForm.getAllEntries().size() > 0) {
                errorCorrectionForm.setEditableFlag("Y");
                // errorCorrectionForm.setManualEditFlag("Y");
                showEditMethod(mapping, form, request, response);
            }

        }

        if (errorCorrectionForm.getEditMethod().equals("criteria")) {


            String groupId[] = { newOriginEntryGroup.getId().toString() };
            // the boolean is readOnly, show output only - not storing in DB
            showAllEntries(groupId, errorCorrectionForm, request);
        }

       // errorCorrectionForm.setGroupIdList(new String[] {"newGroupId"});
        return mapping.findForward(Constants.MAPPING_BASIC);

    }

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

    public ActionForward addCorrectionGroup(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        CorrectionForm errorCorrectionForm = (CorrectionForm) form;
        document = (CorrectionDocument) errorCorrectionForm.getDocument();
        // rebuild the document state
        CorrectionActionHelper.rebuildDocumentState(request, errorCorrectionForm);

        // create a new correction group and add it to the document
        document.addCorrectionGroup(new CorrectionChangeGroup());

        // for consistent presentation ...
        CorrectionActionHelper.sortForDisplay(document.getCorrectionChangeGroup());
       
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
    public ActionForward removeCorrectionGroup(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        CorrectionForm errorCorrectionForm = (CorrectionForm) form;
        document = (CorrectionDocument) errorCorrectionForm.getDocument();
        // rebuild the document state
        CorrectionActionHelper.rebuildDocumentState(request, errorCorrectionForm);

        // load the correction group number from the request
        Integer groupNumber = CorrectionActionHelper.getFromRequestCorrectionGroupNumberToRemove(request, document, errorCorrectionForm);

        // remove the correction group from the document
        document.removeCorrectionGroup(groupNumber);

        if (document.getCorrectionChangeGroup().isEmpty()) {
            document.addCorrectionGroup(new CorrectionChangeGroup());
        }

        // for consistent presentation ...
        CorrectionActionHelper.sortForDisplay(document.getCorrectionChangeGroup());

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
    public ActionForward addSearchCriterion(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        CorrectionForm errorCorrectionForm = (CorrectionForm) form;
        document = (CorrectionDocument) errorCorrectionForm.getDocument();
        // rebuild the document state
        CorrectionActionHelper.rebuildDocumentState(request, errorCorrectionForm);

        Map groupNumbers = new HashMap();
        for (Iterator i = document.getCorrectionChangeGroup().iterator(); i.hasNext();) {
            CorrectionChangeGroup g = (CorrectionChangeGroup) i.next();
            groupNumbers.put(g.getCorrectionChangeGroupLineNumber(), g);
        }

        for (Iterator i = groupNumbers.keySet().iterator(); i.hasNext();) {
            CorrectionChangeGroup correctionGroup = (CorrectionChangeGroup) groupNumbers.get(i.next());

            // populate the criterion submitted to be added
            CorrectionCriteria criterion = CorrectionActionHelper.getFromRequestNewSearchCriterion(request, correctionGroup.getCorrectionChangeGroupLineNumber());

            if (null == criterion) {
                // not the criterion we intended to add.
                continue;
            }

            criterion.setFinancialDocumentNumber(document.getFinancialDocumentNumber());
            
            // In case user input unintened blanks.
            criterion.setCorrectionFieldValue(StringUtils.strip((criterion.getCorrectionFieldValue())));
            // add the new criterion to the appropriate group of search criteria owned by the document
            correctionGroup.addSearchCriterion(criterion);
        }

        // for consistent presentation ...
        CorrectionActionHelper.sortForDisplay(document.getCorrectionChangeGroup());

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
    public ActionForward removeSearchCriterion(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        CorrectionForm errorCorrectionForm = (CorrectionForm) form;
        document = (CorrectionDocument) errorCorrectionForm.getDocument();
        // rebuild the document state
        CorrectionActionHelper.rebuildDocumentState(request, errorCorrectionForm);

        // load the correction group from the request
        Integer[] criterionIndex = CorrectionActionHelper.getCriterionToDelete(request, document, errorCorrectionForm);
        CorrectionChangeGroup group = document.getCorrectionGroup(criterionIndex[0]);
        CorrectionCriteria criterion = group.getSearchCriterion(criterionIndex[1]);
        // remove the criterion from the search criteria owned by the correction group
        group.getCorrectionCriteria().remove(criterion);

        // for consistent presentation ...
        CorrectionActionHelper.sortForDisplay(document.getCorrectionChangeGroup());

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
    public ActionForward addReplacementSpecification(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        CorrectionForm errorCorrectionForm = (CorrectionForm) form;
        document = (CorrectionDocument) errorCorrectionForm.getDocument();
        // rebuild the document state
        CorrectionActionHelper.rebuildDocumentState(request, errorCorrectionForm);

        Map groupNumbers = new HashMap();
        for (Iterator i = document.getCorrectionChangeGroup().iterator(); i.hasNext();) {
            CorrectionChangeGroup g = (CorrectionChangeGroup) i.next();
            groupNumbers.put(g.getCorrectionChangeGroupLineNumber(), g);
        }

        for (Iterator i = groupNumbers.keySet().iterator(); i.hasNext();) {
            CorrectionChangeGroup correctionGroup = (CorrectionChangeGroup) groupNumbers.get(i.next());

            // populate the specification submitted to be added
            CorrectionChange specification = CorrectionActionHelper.getFromRequestNewReplacementSpecification(request, correctionGroup.getCorrectionChangeGroupLineNumber());

            if (null == specification) {
                // not the specification we intended to add.
                continue;
            }

            specification.setFinancialDocumentNumber(document.getFinancialDocumentNumber());

            
            // In case user input unintened blanks.
            specification.setCorrectionFieldValue(StringUtils.strip((specification.getCorrectionFieldValue())));
            // add the new specification to the appropriate group of search criteria owned by the document
            correctionGroup.addReplacementSpecification(specification);
        }

        // for consistent presentation ...
        CorrectionActionHelper.sortForDisplay(document.getCorrectionChangeGroup());

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
    public ActionForward removeReplacementSpecification(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        CorrectionForm errorCorrectionForm = (CorrectionForm) form;
        document = (CorrectionDocument) errorCorrectionForm.getDocument();
        // rebuild the document state
        CorrectionActionHelper.rebuildDocumentState(request, errorCorrectionForm);

        // load the correction group from the request
        Integer[] specificationIndex = CorrectionActionHelper.getSpecificationToDelete(request, document, errorCorrectionForm);
        CorrectionChangeGroup group = document.getCorrectionGroup(specificationIndex[0]);
        CorrectionChange specification = group.getReplacementSpecification(specificationIndex[1]);
        // remove the criterion from the search criteria owned by the correction group
        group.getCorrectionChange().remove(specification);

        // for consistent presentation ...
        CorrectionActionHelper.sortForDisplay(document.getCorrectionChangeGroup());
        
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
    public void searchAndReplaceWithCriteria(CorrectionForm errorCorrectionForm, HttpServletRequest request) throws Exception {

        document = (CorrectionDocument) errorCorrectionForm.getDocument();
        // rebuild the document state
        CorrectionActionHelper.rebuildDocumentState(request, errorCorrectionForm);

        Collection<OriginEntry> resultCorrectionList = new ArrayList();
        String[] groupId;
        groupId = errorCorrectionForm.getGroupIdList();

        List correctionGroups = document.getCorrectionChangeGroup();

        // check required field and show error messages
        /*
         * if (!checkEmptyValues(correctionGroups, groupId)){
         * CorrectionActionHelper.sortForDisplay(document.getCorrectionChangeGroup());
         * 
         * return mapping.findForward(Constants.MAPPING_BASIC); }
         */


        resultCorrectionList = searchAndReplace(groupId, errorCorrectionForm);

        errorCorrectionForm.setAllEntries(resultCorrectionList);

        OriginEntryGroup newOriginEntryGroup = new OriginEntryGroup();
        java.sql.Date today = new java.sql.Date(System.currentTimeMillis());

        if (errorCorrectionForm.isProcessInBatch()) {
            newOriginEntryGroup = originEntryGroupService.createGroup(today, "GLCP", true, true, true);

        }
        else {
            newOriginEntryGroup = originEntryGroupService.createGroup(today, "GLCP", true, false, true);
        }

        //save to DB and calculate Debits/Blanks and Total Credits
        KualiDecimal tempTotalDebitsOrBlanks = new KualiDecimal(0);
        KualiDecimal tempTotalCredits = new KualiDecimal(0); 
       
        for (OriginEntry oe : resultCorrectionList) {
            originEntryService.createEntry(oe, newOriginEntryGroup);
          
            if (oe.getTransactionDebitCreditCode().equals(" ") | oe.getTransactionDebitCreditCode().equals("D")){
                tempTotalDebitsOrBlanks = tempTotalDebitsOrBlanks.add(oe.getTransactionLedgerEntryAmount()); 
            } else {
                tempTotalCredits = tempTotalCredits.add(oe.getTransactionLedgerEntryAmount());
            }

        }
               
        document.setCorrectionDebitTotalAmount(tempTotalDebitsOrBlanks);
        document.setCorrectionCreditTotalAmount(tempTotalCredits);
        document.setCorrectionRowCount(resultCorrectionList.size());
        
        document.setCorrectionInputFileName(groupId[0].toString());
        document.setCorrectionOutputFileName(newOriginEntryGroup.getId().toString());
        document.setCorrectionTypeCode("C");
        //Store in DB CorrectionChange, CorrectionChangeGroup, CorrectionCriteria
        storeCorrectionGroup(document);
        
        document.setCorrectionDebitTotalAmount(tempTotalDebitsOrBlanks);
        document.setCorrectionCreditTotalAmount(tempTotalCredits);
        document.setCorrectionRowCount(resultCorrectionList.size());
        
        
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
    public ActionForward readDocument(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {

        CorrectionForm errorCorrectionForm = (CorrectionForm) form;
        CorrectionActionHelper.rebuildDocumentState(request, errorCorrectionForm);

        HttpSession session = request.getSession(true);
        showAllEntries(errorCorrectionForm.getGroupIdList(), errorCorrectionForm, request);

        if (errorCorrectionForm.getAllEntries().size() > 0) {

            if (errorCorrectionForm.getEditMethod().equals("manual")) {
                errorCorrectionForm.setManualEditFlag("Y");
                errorCorrectionForm.setEditableFlag("N");
            }

        }

        // not need to if multiple dropdown keep values
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

    public String changeSearchField(String op, String field) {

        if (op.equals("ne")) {
            return "!" + field;
        }

        if (op.equals("gt")) {
            return ">" + field;
        }

        if (op.equals("lt")) {
            return "<" + field;
        }

        if (op.equals("sw")) {
            return field + "%";
        }

        if (op.equals("ew")) {
            return "%" + field;
        }

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
    @Override
    public ActionForward route(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        KualiDocumentFormBase kualiDocumentFormBase = (KualiDocumentFormBase) form;
        CorrectionForm errorCorrectionForm = (CorrectionForm) form;
        document = (CorrectionDocument) errorCorrectionForm.getDocument();
        if (errorCorrectionForm.getChooseSystem().equals("file")){
            if (errorCorrectionForm.getEditMethod().equals("manual")){
                
                manualErrorCorrection(errorCorrectionForm, request);
                
                
            } else {
                
                fileUploadSearchAndReplaceWithCriteria(errorCorrectionForm, request);
                
                
            }
            document.setCorrectionInputFileName((String) GlobalVariables.getUserSession().retrieveObject("fileName"));
        }
        
        if (errorCorrectionForm.getChooseSystem().equals("system")){
            if (errorCorrectionForm.getEditMethod().equals("manual")){
                
                manualErrorCorrection(errorCorrectionForm, request);
                
                
        } else {
        
                searchAndReplaceWithCriteria(errorCorrectionForm, request);
                
                
            }
        }
           
        document.setCorrectionSelectionCode(errorCorrectionForm.getMatchCriteriaOnly());
        document.setCorrectionFileDeleteCode(errorCorrectionForm.isProcessInBatch());
        
        
        
        //build document common properties
        //cause an error from workflow?, commented out
        /*if (errorCorrectionForm.getChooseSystem().equals("file")){
            document.setCorrectionSelectionCode(false);
        } else {document.setCorrectionSelectionCode(true);}
        
        if (errorCorrectionForm.getDeleteOutput() != null) {
            document.setCorrectionFileDeleteCode(true);
        } else {document.setCorrectionFileDeleteCode(false);}*/
        
        
        
        return super.route(mapping, form, request, response);
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
     *//*

    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        return mapping.findForward(Constants.MAPPING_BASIC);
    }*/

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

    /*public ActionForward blanketApprove(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return mapping.findForward(Constants.MAPPING_BASIC);


    }*/

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

   /* public ActionForward disapprove(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        return mapping.findForward(Constants.MAPPING_BASIC);
    }*/

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

    /*public ActionForward cancel(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        return mapping.findForward(Constants.MAPPING_CANCEL);
    }
*/

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

/*    public ActionForward close(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return mapping.findForward(Constants.MAPPING_CLOSE);
    }
*/

    /**
     * Creating search Map from searchField and searchValues
     * 
     * @param CorrectionChangeGroup
     * @return Map fieldValues
     */
    private Map createSearchMap(CorrectionChangeGroup correctionGroup) {
        Map fieldValues = new HashMap();
        CorrectionCriteria correctionSearchCriterion;
        Iterator fieldIter = correctionGroup.getCorrectionCriteria().iterator();
        while (fieldIter.hasNext()) {
            correctionSearchCriterion = (CorrectionCriteria) fieldIter.next();
            String operator = correctionSearchCriterion.getOperator();
            String searchValue = correctionSearchCriterion.getCorrectionFieldValue();
            
            // correctionFieldName should be changed to Java BO 
            String searchField = changeFieldName(correctionSearchCriterion.getCorrectionFieldName());
            if (!operator.equals("eq")) {
                // Add operator
                searchValue = changeSearchField(operator, searchValue);
            }

            // check duplicate field, and make the Value list as Collection
            if (fieldValues.containsKey(searchField)) {
                Collection fieldValueCollection = new ArrayList();

                if (fieldValues.get(searchField) instanceof Collection) {
                    fieldValueCollection = (Collection) fieldValues.get(searchField);

                }
                else {
                    String previousSearchValue = (String) fieldValues.get(searchField);
                    fieldValueCollection.add(previousSearchValue);
                }

                fieldValueCollection.add(searchValue);
                fieldValues.put(searchField, fieldValueCollection);

            }
            else {
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

    private void createNewOutput(OriginEntryGroup newOriginEntryGroup, String groupId) {

        // create all OriginEntry which will change GroupID to newOriginEntryGroup ID
        Map groupIdMap = new HashMap();
        groupIdMap.put("entryGroupId", groupId);

        // get all Entries
        Collection oldEntries = originEntryService.getMatchingEntriesByCollection(groupIdMap);

        // change the Entries' groupId and create all
        Iterator oldEntryIter = oldEntries.iterator();
        while (oldEntryIter.hasNext()) {
            OriginEntry eachEntry = (OriginEntry) oldEntryIter.next();
            eachEntry.setEntryGroupId(newOriginEntryGroup.getId());
            // returnCollection.add(eachEntry);
            originEntryService.createEntry(eachEntry, newOriginEntryGroup);

        }

        // return returnCollection;
    }

    /**
     * Replace entries.
     * 
     * @param eachReplaceEntries
     * @param correctionGroup
     */
    public OriginEntry replaceOriginEntryValues(OriginEntry eachReplaceEntries, CorrectionChangeGroup correctionGroup) throws Exception {


        CorrectionChange correctionReplacementSpecification;
        Iterator replaceIter = correctionGroup.getCorrectionChange().iterator();

        while (replaceIter.hasNext()) {
            correctionReplacementSpecification = (CorrectionChange) replaceIter.next();
            String replaceValue = correctionReplacementSpecification.getCorrectionFieldValue();
            
            // correctionFieldName should be changed to Java BO 
            String replaceField = changeFieldName(correctionReplacementSpecification.getCorrectionFieldName());

            if (replaceField.equals("financialDocumentReversalDate")) {
                // convert String to Date
                Date convertDate = SpringServiceLocator.getDateTimeService().convertToSqlDate(replaceValue);
                BeanUtils.setProperty(eachReplaceEntries, replaceField, convertDate);
                continue;

            }
            if (replaceField.equals("transactionDate")) {
                // convert String to Date
                Date convertDate = SpringServiceLocator.getDateTimeService().convertToSqlDate(replaceValue);
                BeanUtils.setProperty(eachReplaceEntries, replaceField, convertDate);
                continue;
            }

            if (replaceField.equals("transactionLedgerEntrySequenceNumber")) {
                // convert String to Integer
                int convertInt = Integer.parseInt(replaceValue);
                BeanUtils.setProperty(eachReplaceEntries, replaceField, new Integer(convertInt));
                continue;
            }
            if (replaceField.equals("transactionLedgerEntryAmount")) {
                BeanUtils.setProperty(eachReplaceEntries, replaceField, new KualiDecimal(replaceValue));
                continue;
            }

            if (replaceField.equals("universityFiscalYear")) {
                // convert String to Integer
                int convertInt = Integer.parseInt(replaceValue);
                BeanUtils.setProperty(eachReplaceEntries, replaceField, new Integer(convertInt));
                continue;
            }
            
            if (replaceField.equals("budgetYear")) {
                // convert String to Integer
                int convertInt = Integer.parseInt(replaceValue);
                BeanUtils.setProperty(eachReplaceEntries, replaceField, new Integer(convertInt));
                continue;
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
    public boolean checkEmptyValues(List correctionGroups, String[] groupId) {
        boolean returnVal = true;

        // if the description is required, then
        /*
         * if (document.getDocumentHeader().getFinancialDocumentDescription() == null) {
         * //GlobalVariables.getErrorMap().put("documentHeader.financialDocumentDescription", KeyConstants.ERROR_REQUIRED,
         * "Financial Document Description");
         * GlobalVariables.getErrorMap().put("document.documentHeader.financialDocumentDescription", KeyConstants.ERROR_REQUIRED,
         * "Financial Document Description"); }
         */

        if (groupId == null) {
            GlobalVariables.getErrorMap().putError("searchFieldError", KeyConstants.ERROR_GL_ERROR_CORRECTION_ORIGINGROUP_REQUIRED);
            returnVal = false;
        }
        Iterator groupIter = correctionGroups.iterator();
        while (groupIter.hasNext()) {
            CorrectionChangeGroup correctionGroup = (CorrectionChangeGroup) groupIter.next();
            if (correctionGroup.getCorrectionCriteria().size() < 1) {
                // displaing error message
                GlobalVariables.getErrorMap().putError("searchFieldError", KeyConstants.ERROR_GL_ERROR_CORRECTION_SEARCHFIELD_REQUIRED);
                returnVal = false;
            }
            if (correctionGroup.getCorrectionChange().size() < 1) {
                GlobalVariables.getErrorMap().putError("searchFieldError", KeyConstants.ERROR_GL_ERROR_CORRECTION_MODIFYFIELD_REQUIRED);
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
    public ActionForward chooseMainDropdown(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        CorrectionForm errorCorrectionForm  = (CorrectionForm) form;
        GlobalVariables.getUserSession().removeObject(Constants.CORRECTION_FORM_KEY);
        
        errorCorrectionForm.setAllEntries(null);
        errorCorrectionForm.setEditableFlag("N");
        errorCorrectionForm.setManualEditFlag("N");
        
        
         
        document = (CorrectionDocument) errorCorrectionForm.getDocument();
        
        errorCorrectionForm.setProcessInBatch(true);
        
        //if users choose database to get document, then set the default entry group
        if (errorCorrectionForm.getChooseSystem().equals("system")){
            
            OriginEntryGroupService originEntryGroupService = (OriginEntryGroupService) SpringServiceLocator.getBeanFactory().getBean("glOriginEntryGroupService");
            List <OriginEntryGroup> entryGroupList = (List) originEntryGroupService.getRecentGroupsByDays(Constants.CORRECTION_RECENT_GROUPS_DAY);
            
            //if there is at least one group on the list 
            if (entryGroupList.size() > 0) {
                List <OriginEntryGroup> sceGroupList = new ArrayList();
                
                for(OriginEntryGroup oeg: entryGroupList){
                    if (oeg.getSourceCode().equals("SCE")){
                        sceGroupList.add(oeg);
                    }
                }
                //if there is at least one SCE group on the list
                if (sceGroupList.size()>0){
                    OEGDateComparator oegDateComparator = new OEGDateComparator();
                    Collections.sort(sceGroupList, oegDateComparator);
                    Collections.sort(entryGroupList, oegDateComparator);
                    
                    String[] defaultGroupId = {sceGroupList.get(0).getId().toString()}; 
                    errorCorrectionForm.setGroupIdList(defaultGroupId);
                } else {
                    //if there is not SCE group on the list, default group is first group on the list
                    String[] defaultGroupId = {entryGroupList.get(0).getId().toString()};
                    errorCorrectionForm.setGroupIdList(defaultGroupId);
                }
                
            }
            
        }
        
        
        
       /* //rebuild tabstate
        if (errorCorrectionForm.getEditMethod().equals("Manual")){
            errorCorrectionForm.getTabState(0).setOpen(true);
            errorCorrectionForm.getTabState(0).setOpen(true);
            errorCorrectionForm.getTabState(0).setOpen(true);
            errorCorrectionForm.getTabState(0).setOpen(true);
            errorCorrectionForm.getTabState(0).setOpen(false);
            errorCorrectionForm.getTabState(0).setOpen(false);
            
            
        }*/
        
        
        
        
        
        
        
        /*
        //default group is latest scrubber error file.
        OriginEntryGroupService originEntryGroupService = (OriginEntryGroupService) SpringServiceLocator.getBeanFactory().getBean("glOriginEntryGroupService");
        List <OriginEntryGroup> entryGroupList = (List) originEntryGroupService.getAllOriginEntryGroup();
        List <OriginEntryGroup> sceGroupList = new ArrayList();
        
        for(OriginEntryGroup oeg: entryGroupList){
            if (oeg.getSourceCode().equals("SCE")){
                sceGroupList.add(oeg);
            }
        }
        
        OEGDateComparator oegDateComparator = new OEGDateComparator();
        Collections.sort(sceGroupList, oegDateComparator);
        Collections.sort(entryGroupList, oegDateComparator);
                
        //if there is a SCE group then default value for group list is SCE. If not, the latest group is default 
        if (sceGroupList.get(0) != null ){
            String[] defaultGroupId = {sceGroupList.get(0).getId().toString()}; 
            errorCorrectionForm.setGroupIdList(defaultGroupId);
        } else {
            String[] defaultGroupId = {entryGroupList.get(0).toString()};
            errorCorrectionForm.setGroupIdList(defaultGroupId);
        }
        
        
*/        return mapping.findForward(Constants.MAPPING_BASIC);
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
    public ActionForward showOneEntry(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        HttpSession session = request.getSession(true);

        CorrectionForm errorCorrectionForm = (CorrectionForm) form;
        Map searchMap = new HashMap();
        
        String stringEditEntryId = "";
        for (Enumeration i = request.getParameterNames(); i.hasMoreElements();) {
            String parameterName = (String) i.nextElement();

            // check if the parameter name is a specifying the methodToCall
            if (parameterName.startsWith("methodToCall.showOneEntry.anchor" + errorCorrectionForm.getAnchor()) && parameterName.endsWith(".x")) {
                stringEditEntryId = StringUtils.substringBetween(parameterName, "methodToCall.showOneEntry.anchor" + errorCorrectionForm.getAnchor()+ ".", ".");
                
            }
        }
        
        Integer editEntryId = Integer.parseInt(stringEditEntryId);
        OriginEntry eachEntry = (OriginEntry) originEntryService.getExactMatchingEntry(editEntryId);
        errorCorrectionForm.setEachEntryForManualEdit(eachEntry);


        String[] newGroupId = { (String) session.getAttribute("newGroupId") };

        //showAllEntries(newGroupId, errorCorrectionForm, request);

        errorCorrectionForm.setEditableFlag("Y");
        // manualEditFlag is for activate a button for asking user to ask edit the docu.
        errorCorrectionForm.setManualEditFlag("N");

        CorrectionActionHelper.rebuildDocumentState(request, errorCorrectionForm);
        
        errorCorrectionForm.setEditingEntryId(editEntryId);
       
        
       /* Object displayTableParameterPage = GlobalVariables.getUserSession().retrieveObject(Constants.DISPLAY_TABLE_PAGE_NUMBER); 
        Object displayTableParameterColumn = GlobalVariables.getUserSession().retrieveObject(Constants.DISPLAY_TABLE_COLUMN_NUMBER);
      */ /* 
        if (displayTableParameterPage != null) {
            request.getParameterMap().put(Constants.DISPLAY_TABLE_PARAMETER_PAGE_NAME, displayTableParameterPage);
        }
        if (displayTableParameterColumn !=null) {
            request.getParameterMap().put(Constants.DISPLAY_TABLE_PARAMETER_COLUMN_NAME, displayTableParameterColumn);
        }*/
        

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
    public ActionForward editEntry(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        CorrectionForm errorCorrectionForm = (CorrectionForm) form;
        OriginEntry oe = errorCorrectionForm.getEachEntryForManualEdit();
        //OriginEntry temp = (OriginEntry) request.getAttribute("eachEntryForManualEdit");
      
        // set entryId
        // null id means user added a new entry.
        HttpSession session = request.getSession(true);
        String[] newGroupId = { (String) session.getAttribute("newGroupId") };
        Integer editEntryId;
        
        String stringEditEntryId = "";
        
        for (Enumeration i = request.getParameterNames(); i.hasMoreElements();) {
            String parameterName = (String) i.nextElement();

            // check if the parameter name is a specifying the methodToCall
            if (parameterName.startsWith("methodToCall.editEntry.anchor" + errorCorrectionForm.getAnchor()) && parameterName.endsWith(".x")) {
                stringEditEntryId = StringUtils.substringBetween(parameterName, "methodToCall.editEntry.anchor" + errorCorrectionForm.getAnchor()+ ".", ".");
                
            }
        }
        
        if (!stringEditEntryId.equals("")) {
            editEntryId = Integer.parseInt(stringEditEntryId);
            oe.setEntryId(editEntryId);
            oe.setEntryGroupId(Integer.parseInt(newGroupId[0]));
            // remove the original OriginEntry
            OriginEntry originalOe = originEntryService.getExactMatchingEntry(editEntryId);
            originEntryService.delete(originalOe);

            // save the new OriginEntry
            originEntryService.save(oe);


        }
        else {

            OriginEntryGroup newOriginEntryGroup = originEntryGroupService.getExactMatchingEntryGroup(Integer.parseInt(newGroupId[0]));
            originEntryService.createEntry(oe, newOriginEntryGroup);

        }

        showAllEntries(newGroupId, errorCorrectionForm, request);

        OriginEntry newOriginEntry = new OriginEntry();
        errorCorrectionForm.setEachEntryForManualEdit(newOriginEntry);

        errorCorrectionForm.setEditableFlag("Y");
        // manualEditFlag is for activate a button for asking user to ask edit the docu.
        errorCorrectionForm.setManualEditFlag("N");

        CorrectionActionHelper.rebuildDocumentState(request, errorCorrectionForm);
        return mapping.findForward(Constants.MAPPING_BASIC);


    }
    public ActionForward addEntry(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        CorrectionForm errorCorrectionForm = (CorrectionForm) form;
        OriginEntry oe = errorCorrectionForm.getNewEntryForManualEdit();
        
        HttpSession session = request.getSession(true);
        String[] newGroupId = { (String) session.getAttribute("newGroupId") };
 
        OriginEntryGroup newOriginEntryGroup = originEntryGroupService.getExactMatchingEntryGroup(Integer.parseInt(newGroupId[0]));
        originEntryService.createEntry(oe, newOriginEntryGroup);
        
        
        showAllEntries(newGroupId, errorCorrectionForm, request);
        
        
        
        
        
        
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
    public ActionForward searchForManualEdit(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        CorrectionForm errorCorrectionForm = (CorrectionForm) form;
        document = (CorrectionDocument) errorCorrectionForm.getDocument();
        HttpSession session = request.getSession(true);


        CorrectionActionHelper.rebuildDocumentState(request, errorCorrectionForm);
        List correctionGroups = document.getCorrectionChangeGroup();
        String groupId;
        /*
         * if (request.getAttribute("chooseSystem") == "file") { groupId = (String) session.getAttribute("newGroupId"); } else {
         * groupId = (String) session.getAttribute("newGroupId"); }
         */
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
        // searchResults is collection of OriginEntry
        searchResults = (Collection) lookupService.findCollectionBySearchUnbounded(OriginEntry.class, fieldValues);

        errorCorrectionForm.setAllEntries(searchResults);

        request.setAttribute("reqSearchResults", searchResults);
        
        String searchResultKey = GlobalVariables.getUserSession().addObject(searchResults , Constants.SEARCH_LIST_KEY_PREFIX);
        request.setAttribute(Constants.SEARCH_LIST_REQUEST_KEY, searchResultKey);
        String correctionFormKey = GlobalVariables.getUserSession().addObject(errorCorrectionForm ,Constants.CORRECTION_FORM_KEY);
        request.setAttribute(Constants.CORRECTION_FORM_KEY, correctionFormKey);
        
        
        errorCorrectionForm.setEditableFlag("Y");
        // manualEditFlag is for activate a button for asking user to ask edit the docu.
        errorCorrectionForm.setManualEditFlag("N");

        OriginEntry newOriginEntry = new OriginEntry();
        errorCorrectionForm.setEachEntryForManualEdit(newOriginEntry);


        return mapping.findForward(Constants.MAPPING_BASIC);

    }
    
    
    public ActionForward searchCancelForManualEdit(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        CorrectionForm errorCorrectionForm = (CorrectionForm) form;
        document = (CorrectionDocument) errorCorrectionForm.getDocument();
        document.getCorrectionChangeGroup().clear();
        document.addCorrectionGroup(new CorrectionChangeGroup());
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
    public void manualErrorCorrection(CorrectionForm errorCorrectionForm, HttpServletRequest request) throws Exception {
        
        document = (CorrectionDocument) errorCorrectionForm.getDocument();
        
        Map searchMap = new HashMap();
        
        HttpSession session = request.getSession(true);

        String newGroupId = (String) session.getAttribute("newGroupId");
        OriginEntryGroup newOriginEntryGroup = originEntryGroupService.getExactMatchingEntryGroup(Integer.parseInt(newGroupId));

        // if user checked Delete Output file option
        if (errorCorrectionForm.isProcessInBatch()) {
            newOriginEntryGroup.setProcess(true);
        }
        else {
            newOriginEntryGroup.setProcess(false);
        }

        newOriginEntryGroup.setValid(true);
        newOriginEntryGroup.setScrub(true);
        originEntryGroupService.save(newOriginEntryGroup);
        
        //Calculate Debits/Blanks and Total Credits
        //Shawn: Could be changed if not using DB everytime
        searchMap.put("entryGroupId", newGroupId);
        Collection<OriginEntry> searchResult = originEntryService.getMatchingEntriesByCollection(searchMap);
        
        KualiDecimal tempTotalDebitsOrBlanks = new KualiDecimal(0);
        KualiDecimal tempTotalCredits = new KualiDecimal(0);
        
        
        for (OriginEntry oe : searchResult) {
              if (oe.getTransactionDebitCreditCode().equals(" ") | oe.getTransactionDebitCreditCode().equals("D")){
                
                  
                  tempTotalDebitsOrBlanks = tempTotalDebitsOrBlanks.add(oe.getTransactionLedgerEntryAmount()); 
            } else {
                
                tempTotalCredits = tempTotalCredits.add(oe.getTransactionLedgerEntryAmount());
            }
        }
        
        document.setCorrectionDebitTotalAmount(tempTotalDebitsOrBlanks);
        document.setCorrectionCreditTotalAmount(tempTotalCredits);
        document.setCorrectionRowCount(searchResult.size());
        
        //TODO: should be changed.....if file upload then should be changed file name(with Directory).....etc
        
        //'errorCorrectionForm.getGroupIdList() is null' means file upload 
        if (errorCorrectionForm.getGroupIdList() != null){
            String inputFileName = errorCorrectionForm.getGroupIdList()[0].toString();
            document.setCorrectionInputFileName(inputFileName);
        }
        
        document.setCorrectionOutputFileName(newOriginEntryGroup.getId().toString());
        document.setCorrectionTypeCode("M");
        
        document.setCorrectionDebitTotalAmount(tempTotalDebitsOrBlanks);
        document.setCorrectionCreditTotalAmount(tempTotalCredits);
        document.setCorrectionRowCount(searchResult.size());
        
        /*errorCorrectionForm.setEditMethod(null);
        errorCorrectionForm.setChooseSystem(null);*/
     }

    /**
     * Show all entries for Manual edit with groupId
     * 
     * @param groupId
     */
    public void showAllEntries(String[] groupId, CorrectionForm errorCorrectionForm, HttpServletRequest request) {

		String previousCorrectionFormKey = request.getParameter(Constants.CORRECTION_FORM_KEY);
        if(!(previousCorrectionFormKey == null)) {
            GlobalVariables.getUserSession().removeObject(previousCorrectionFormKey);
        }
        Map searchMap = new HashMap();
        Collection resultFromGroupId = new ArrayList();
        Properties parameters = new Properties();
        
        // Configure the query.

        for (String eachGroupId : groupId) {
            searchMap.put("entryGroupId", eachGroupId);
            Collection searchResult = originEntryService.getMatchingEntriesByCollection(searchMap);
            resultFromGroupId.addAll(searchResult);
        }

        errorCorrectionForm.setAllEntries(resultFromGroupId);
        
        
         //request.setAttribute("reqSearchResults", resultFromGroupId);
        
        /*String searchResultKey = GlobalVariables.getUserSession().addObject(resultFromGroupId, Constants.SEARCH_LIST_KEY_PREFIX);
        request.setAttribute(Constants.SEARCH_LIST_REQUEST_KEY, searchResultKey);*/
        
        GlobalVariables.getUserSession().addObject(Constants.CORRECTION_FORM_KEY, errorCorrectionForm);
        //request.setAttribute(Constants.CORRECTION_FORM_KEY, correctionFormKey);
        
     }

    /**
     * Search and replace with criteria.
     * 
     * @param groupId
     * @param CorrectionGroups
     */
    public Collection searchAndReplace(String[] groupId, CorrectionForm errorCorrectionForm) {

        LookupService lookupService = (LookupService) SpringServiceLocator.getBeanFactory().getBean("lookupService");
        document = (CorrectionDocument) errorCorrectionForm.getDocument();
        List correctionGroups = document.getCorrectionChangeGroup();
        Map fieldValues = new HashMap();
        Map resultMap = new HashMap();
        CorrectionChangeGroup correctionGroup;
        // Collection searchResults = null;
        OriginEntryGroup newOriginEntryGroup = null;

        Collection<OriginEntry> finalResult = new ArrayList();

        Map changedEntryMap = new HashMap();
        Collection returnCollection;

        // search using Groups and Fields
        for (int i = 0; i < groupId.length; i += 1) {


            Iterator groupIter = correctionGroups.iterator();

            while (groupIter.hasNext()) {
                correctionGroup = (CorrectionChangeGroup) groupIter.next();

                // Set a Map with fields
                fieldValues = createSearchMap(correctionGroup);

                fieldValues.put("entryGroupId", groupId[i]);

                // searchResults is collection of OriginEntry
                // Collection<OriginEntry> searchResults = (Collection)
                // lookupService.findCollectionBySearchUnbounded(OriginEntry.class, fieldValues);
                Collection searchResults = (Collection) lookupService.findCollectionBySearchUnbounded(OriginEntry.class, fieldValues);
                if (searchResults.size() > 0) {

                    Iterator oeIter = searchResults.iterator();
                    while (oeIter.hasNext()) {
                        OriginEntry oe = (OriginEntry) oeIter.next();
                        // for(OriginEntry oe: searchResults){

                        OriginEntry replacedOriginEntry = new OriginEntry();

                        try {
                            replacedOriginEntry = replaceOriginEntryValues(oe, correctionGroup);
                        }
                        catch (Exception e) {
                        }

                        changedEntryMap.put(oe.getEntryId(), oe);
                    }

                }
                // initialize search field
                fieldValues = new HashMap();
            }

            // build result collection
            if (errorCorrectionForm.getMatchCriteriaOnly() == false) {
                resultMap.put("entryGroupId", groupId[i]);
                Collection<OriginEntry> tempResultList = originEntryService.getMatchingEntriesByCollection(resultMap);

                // not adding entries which are in changedMap
                for (OriginEntry oe : tempResultList) {
                    if (changedEntryMap.get(oe.getEntryId()) == null) {
                        finalResult.add(oe);
                    }

                }
            }
        }


        if (errorCorrectionForm.getMatchCriteriaOnly() == false) {

            finalResult.addAll(changedEntryMap.values());
            returnCollection = finalResult;

        }
        else {
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
    public void fileUploadSearchAndReplaceWithCriteria(CorrectionForm errorCorrectionForm, HttpServletRequest request) throws Exception {

        document = (CorrectionDocument) errorCorrectionForm.getDocument();
        // TODO: Change later. The purpose of this method is just for groupId.
        // This method will be useless if groupId set from session using session "chooseSystem".

        // rebuild the document state
        CorrectionActionHelper.rebuildDocumentState(request, errorCorrectionForm);
        Collection<OriginEntry> resultCorrectionList = new ArrayList();

        HttpSession session = request.getSession(true);
        String groupId[] = { (String) session.getAttribute("newGroupId") };

        List correctionGroups = document.getCorrectionChangeGroup();

        // check required field and show error messages
        /*
         * if (!checkEmpteyValues(correctionGroups, groupId)){
         * CorrectionActionHelper.sortForDisplay(document.getCorrectionChangeGroup());
         * 
         * return mapping.findForward(Constants.MAPPING_BASIC); }
         */

        // the boolean is readOnly, show output only - not storing in DB
        resultCorrectionList = searchAndReplace(groupId, errorCorrectionForm);
        
        
        OriginEntryGroup newOriginEntryGroup = new OriginEntryGroup();
        java.sql.Date today = new java.sql.Date(System.currentTimeMillis());

        if (errorCorrectionForm.isProcessInBatch()) {
            newOriginEntryGroup = originEntryGroupService.createGroup(today, "GLCP", true, true, true);

        }
        else {
            newOriginEntryGroup = originEntryGroupService.createGroup(today, "GLCP", true, false, true);
        }

        //save to DB and calculate Debits/Blanks and Total Credits
        KualiDecimal tempTotalDebitsOrBlanks = new KualiDecimal(0);
        KualiDecimal tempTotalCredits = new KualiDecimal(0); 
        
        for (OriginEntry oe : resultCorrectionList) {
            originEntryService.createEntry(oe, newOriginEntryGroup);
          
            if (oe.getTransactionDebitCreditCode().equals(" ") | oe.getTransactionDebitCreditCode().equals("D")){
                tempTotalDebitsOrBlanks = tempTotalDebitsOrBlanks.add(oe.getTransactionLedgerEntryAmount()); 
            } else {
                tempTotalCredits = tempTotalCredits.add(oe.getTransactionLedgerEntryAmount());
            }
        }
        
        document.setCorrectionDebitTotalAmount(tempTotalDebitsOrBlanks);
        document.setCorrectionCreditTotalAmount(tempTotalCredits);
        document.setCorrectionRowCount(resultCorrectionList.size());
        
        
        document.setCorrectionOutputFileName(newOriginEntryGroup.getId().toString());
        document.setCorrectionTypeCode("C");
        
        //Store in DB CorrectionChange, CorrectionChangeGroup, CorrectionCriteria
        storeCorrectionGroup(document);
        
        document.setCorrectionDebitTotalAmount(tempTotalDebitsOrBlanks);
        document.setCorrectionCreditTotalAmount(tempTotalCredits);
        document.setCorrectionRowCount(resultCorrectionList.size());
        
}


    // This method is for giving options to user for manual edit.
    // If user click 'edit', thne all entries store in DB.

    public ActionForward showEditMethod(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {

        CorrectionForm errorCorrectionForm = (CorrectionForm) form;
        // CorrectionActionHelper.rebuildDocumentState(request, errorCorrectionForm);

        HttpSession session = request.getSession(true);
        
        OriginEntryGroup newOriginEntryGroup;

        // in case of file upload
        if (errorCorrectionForm.getChooseSystem().equals("file")) {

            String[] groupId = { (String) session.getAttribute("newGroupId") };
            showAllEntries(groupId, errorCorrectionForm, request);

        }
        else {
            // create an OriginEntryGroup
            java.sql.Date today = new java.sql.Date(System.currentTimeMillis());
            // create a docu with all indicators as false(N)
            newOriginEntryGroup = originEntryGroupService.createGroup(today, "GLCP", false, false, false);

            // Create new Entries with newOriginEntryGroup
            Collection<OriginEntry> newEntries = errorCorrectionForm.getAllEntries();
            for (OriginEntry oe : newEntries) {
                oe.setEntryGroupId(newOriginEntryGroup.getId());
                originEntryService.createEntry(oe, newOriginEntryGroup);
            }
            String[] groupIdList = { newOriginEntryGroup.getId().toString() };
            //showAllEntries(groupIdList, errorCorrectionForm, request);
            session.setAttribute("newGroupId", newOriginEntryGroup.getId().toString());
            
            showAllEntries(groupIdList, errorCorrectionForm, request);
        }


        errorCorrectionForm.setEditableFlag("Y");
        // manualEditFlag is for activate a button for asking user to ask edit the docu.
        errorCorrectionForm.setManualEditFlag("N");

        // for add an entry
        
        errorCorrectionForm.setNewEntryForManualEdit(new OriginEntry());
        errorCorrectionForm.setProcessInBatch(true);
        // keep track the newGroupId in session


        // request.setAttribute("pending-origin-entry-group-id", groupId);


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
    public ActionForward addSearchCriterionForManualEdit(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        CorrectionForm errorCorrectionForm = (CorrectionForm) form;
        document = (CorrectionDocument) errorCorrectionForm.getDocument();
        // rebuild the document state
        CorrectionActionHelper.rebuildDocumentState(request, errorCorrectionForm);

        Map groupNumbers = new HashMap();
        for (Iterator i = document.getCorrectionChangeGroup().iterator(); i.hasNext();) {
            CorrectionChangeGroup g = (CorrectionChangeGroup) i.next();
            groupNumbers.put(g.getCorrectionChangeGroupLineNumber(), g);
        }

        for (Iterator i = groupNumbers.keySet().iterator(); i.hasNext();) {
            CorrectionChangeGroup correctionGroup = (CorrectionChangeGroup) groupNumbers.get(i.next());

            // populate the criterion submitted to be added
            CorrectionCriteria criterion = CorrectionActionHelper.getFromRequestNewSearchCriterion(request, correctionGroup.getCorrectionChangeGroupLineNumber());

            if (null == criterion) {
                // not the criterion we intended to add.
                continue;
            }

            criterion.setFinancialDocumentNumber(document.getFinancialDocumentNumber());

            //In case user input unintened blanks.
            criterion.setCorrectionFieldValue(StringUtils.strip((criterion.getCorrectionFieldValue())));
            // add the new criterion to the appropriate group of search criteria owned by the document
            correctionGroup.addSearchCriterion(criterion);
        }

        // for consistent presentation ...
        CorrectionActionHelper.sortForDisplay(document.getCorrectionChangeGroup());

        // for add an entry
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
    public ActionForward removeSearchCriterionForManualEdit(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        CorrectionForm errorCorrectionForm = (CorrectionForm) form;
        document = (CorrectionDocument) errorCorrectionForm.getDocument();
        // rebuild the document state
        CorrectionActionHelper.rebuildDocumentState(request, errorCorrectionForm);

        // load the correction group from the request
        Integer[] criterionIndex = CorrectionActionHelper.getCriterionToDelete(request, document, errorCorrectionForm);
        CorrectionChangeGroup group = document.getCorrectionGroup(criterionIndex[0]);
        CorrectionCriteria criterion = group.getSearchCriterion(criterionIndex[1]);
        // remove the criterion from the search criteria owned by the correction group
        group.getCorrectionCriteria().remove(criterion);

        // for consistent presentation ...
        CorrectionActionHelper.sortForDisplay(document.getCorrectionChangeGroup());

        HttpSession session = request.getSession(true);
        String[] groupId = { (String) session.getAttribute("newGroupId") };
        showAllEntries(groupId, errorCorrectionForm, request);

        errorCorrectionForm.setEditableFlag("Y");
        // manualEditFlag is for activate a button for asking user to ask edit the docu.
        errorCorrectionForm.setManualEditFlag("N");

        // for add an entry
        OriginEntry newOriginEntry = new OriginEntry();
        errorCorrectionForm.setEachEntryForManualEdit(newOriginEntry);

        return mapping.findForward(Constants.MAPPING_BASIC);
    }


    public ActionForward deleteEntry(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {

        CorrectionForm errorCorrectionForm = (CorrectionForm) form;

       
        String stringEditEntryId = "";
        for (Enumeration i = request.getParameterNames(); i.hasMoreElements();) {
            String parameterName = (String) i.nextElement();

            // check if the parameter name is a specifying the methodToCall
            if (parameterName.startsWith("methodToCall.deleteEntry.anchor" + errorCorrectionForm.getAnchor()) && parameterName.endsWith(".x")) {
                stringEditEntryId = StringUtils.substringBetween(parameterName, "methodToCall.deleteEntry.anchor" + errorCorrectionForm.getAnchor()+ ".", ".");
                
            }
        }
        
        Integer editEntryId = Integer.parseInt(stringEditEntryId);
        OriginEntry eachEntry = (OriginEntry) originEntryService.getExactMatchingEntry(editEntryId);

        originEntryService.delete(eachEntry);
        HttpSession session = request.getSession(true);
        String[] newGroupId = { (String) session.getAttribute("newGroupId") };
        showAllEntries(newGroupId, errorCorrectionForm, request);

        OriginEntry newOriginEntry = new OriginEntry();
        errorCorrectionForm.setEachEntryForManualEdit(newOriginEntry);

        errorCorrectionForm.setEditableFlag("Y");
        // manualEditFlag is for activate a button for asking user to ask edit the docu.
        errorCorrectionForm.setManualEditFlag("N");

        CorrectionActionHelper.rebuildDocumentState(request, errorCorrectionForm);

        return mapping.findForward(Constants.MAPPING_BASIC);
    }

    public ActionForward showOutputFile(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {

        CorrectionForm errorCorrectionForm = (CorrectionForm) form;
        document = (CorrectionDocument) errorCorrectionForm.getDocument();
        CorrectionActionHelper.rebuildDocumentState(request, errorCorrectionForm);
        HttpSession session = request.getSession(true);
        Collection<OriginEntry> resultCorrectionList = new ArrayList();
        String[] groupId = { (String) session.getAttribute("newGroupId")};
        
        if (!errorCorrectionForm.getChooseSystem().equals("file")){
             groupId = errorCorrectionForm.getGroupIdList(); 
             }
        List correctionGroups = document.getCorrectionChangeGroup();

        resultCorrectionList = searchAndReplace(groupId, errorCorrectionForm);

        errorCorrectionForm.setAllEntries(resultCorrectionList);
        
        request.setAttribute("reqSearchResults", resultCorrectionList);
        request.setAttribute(Constants.SEARCH_LIST_REQUEST_KEY, GlobalVariables.getUserSession().addObject(resultCorrectionList, Constants.SEARCH_LIST_KEY_PREFIX));
        
        //calculate Debits/Blanks and Total Credits
        KualiDecimal tempTotalDebitsOrBlanks = new KualiDecimal(0);
        KualiDecimal tempTotalCredits = new KualiDecimal(0); 
        
        for (OriginEntry oe : resultCorrectionList) {
           
            if (oe.getTransactionDebitCreditCode().equals(" ") | oe.getTransactionDebitCreditCode().equals("D")){
                tempTotalDebitsOrBlanks = tempTotalDebitsOrBlanks.add(oe.getTransactionLedgerEntryAmount()); 
            } else {
                tempTotalCredits = tempTotalCredits.add(oe.getTransactionLedgerEntryAmount());
            }
        }
        
        document.setCorrectionDebitTotalAmount(tempTotalDebitsOrBlanks);
        document.setCorrectionCreditTotalAmount(tempTotalCredits);
        document.setCorrectionRowCount(resultCorrectionList.size());
        
        document.setCorrectionDebitTotalAmount(tempTotalDebitsOrBlanks);
        document.setCorrectionCreditTotalAmount(tempTotalCredits);
        document.setCorrectionRowCount(resultCorrectionList.size());
        
        String searchResultKey = GlobalVariables.getUserSession().addObject(resultCorrectionList, Constants.SEARCH_LIST_KEY_PREFIX);
        request.setAttribute(Constants.SEARCH_LIST_REQUEST_KEY, searchResultKey);
        String correctionFormKey = GlobalVariables.getUserSession().addObject(errorCorrectionForm ,Constants.CORRECTION_FORM_KEY);
        request.setAttribute(Constants.CORRECTION_FORM_KEY, correctionFormKey);
        
        return mapping.findForward(Constants.MAPPING_BASIC);

    }
    
    
    
    public ActionForward saveToDesktop(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws IOException{
        
        CorrectionForm errorCorrectionForm = (CorrectionForm) form;
        CorrectionActionHelper.rebuildDocumentState(request, errorCorrectionForm);
        String[] groupId = errorCorrectionForm.getGroupIdList();
        
        OriginEntryGroup oeg = originEntryGroupService.getExactMatchingEntryGroup(Integer.parseInt(groupId[0]));
        String sourceName = "";
        String sourceCode = "";
        
        String fileName;
        if (oeg.getSource() != null) {
            sourceName = oeg.getSource().getName();
            sourceCode = oeg.getSource().getCode();
            }
        fileName = sourceCode+oeg.getId().toString()+"_"+oeg.getDate().toString()+".txt";
        
        BufferedOutputStream bw = new BufferedOutputStream(response.getOutputStream());
        
        
        originEntryService.flatFile(fileName, Integer.parseInt(groupId[0]), bw);
        
        //set response
        response.setContentType("application/txt");
        response.setHeader("Content-disposition", "attachment; filename=" + fileName);
        response.setHeader("Expires", "0");
        response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
        response.setHeader("Pragma", "public");
        
        //response.setContentLength(bw.size());
        
        // write to output
        
        bw.flush();
        bw.close();
       
       
        return (null);
        //return mapping.findForward(Constants.MAPPING_BASIC);
    }
    
    /*public ActionForward showPreviousCorrectionFiles(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws WorkflowException {
        
        CorrectionChangeGroupDao correctionChangeGroupDao 
            = (CorrectionChangeGroupDao) SpringServiceLocator.getBeanFactory().getBean("glCorrectionChangeGroupDao");
        CorrectionChangeDao correctionChangeDao 
            = (CorrectionChangeDao) SpringServiceLocator.getBeanFactory().getBean("glCorrectionChangeDao");
        CorrectionCriteriaDao correctionCriteriaDao 
        = (CorrectionCriteriaDao) SpringServiceLocator.getBeanFactory().getBean("glCorrectionCriteriaDao");
       
        CorrectionChangeGroup correctionChangeGroup = new CorrectionChangeGroup();
        CorrectionChange correctionChange = new CorrectionChange();
        CorrectionCriteria correctionCriteria = new CorrectionCriteria();
        CorrectionForm errorCorrectionForm = (CorrectionForm) form;
    
        DocumentDao documentDao = (DocumentDao) SpringServiceLocator.getBeanFactory().getBean("documentDao");
    
        CorrectionDocument document = (CorrectionDocument) errorCorrectionForm.getDocument();
        Integer docId = errorCorrectionForm.getOldDocId();
        CorrectionDocument oldDoc = (CorrectionDocument) documentDao.findByDocumentHeaderId(CorrectionDocument.class, docId.toString());
    
        buildFormForShowingPreviousDoc(errorCorrectionForm, request, oldDoc);
    
    return mapping.findForward(Constants.MAPPING_BASIC);
    
    }*/
    
    
    public ActionForward confirmDeleteDocument(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        
        CorrectionForm errorCorrectionForm = (CorrectionForm) form;
        showAllEntries(errorCorrectionForm.getGroupIdList(), errorCorrectionForm, request);
        errorCorrectionForm.setDeleteFileFlag("Y");
       
        return mapping.findForward(Constants.MAPPING_BASIC);
        
    }
    
    public ActionForward deleteDocument(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        
        CorrectionForm errorCorrectionForm = (CorrectionForm) form;
        showAllEntries(errorCorrectionForm.getGroupIdList(), errorCorrectionForm, request);
       
        for (String eachGroupId : errorCorrectionForm.getGroupIdList()) {
        
            OriginEntryGroup oeg = originEntryGroupService.getExactMatchingEntryGroup(Integer.parseInt(eachGroupId));
            oeg.setProcess(false);
            originEntryGroupService.save(oeg);
        }
        
       
        return mapping.findForward(Constants.MAPPING_BASIC);
        
    }

    public void storeCorrectionGroup(CorrectionDocument document){
    
        CorrectionChangeGroupDao correctionChangeGroupDao 
            = (CorrectionChangeGroupDao) SpringServiceLocator.getBeanFactory().getBean("glCorrectionChangeGroupDao");
        CorrectionChangeDao correctionChangeDao 
            = (CorrectionChangeDao) SpringServiceLocator.getBeanFactory().getBean("glCorrectionChangeDao");
        CorrectionCriteriaDao correctionCriteriaDao 
            = (CorrectionCriteriaDao) SpringServiceLocator.getBeanFactory().getBean("glCorrectionCriteriaDao");
    
        CorrectionChangeGroup correctionChangeGroup = new CorrectionChangeGroup();
        CorrectionChange correctionChange = new CorrectionChange();
        CorrectionCriteria correctionCriteria = new CorrectionCriteria();
    
        List<CorrectionChangeGroup> changeGroupList = document.getCorrectionChangeGroup();
        String docNumber = document.getDocumentHeader().getFinancialDocumentNumber();
    
        for(CorrectionChangeGroup ccg: changeGroupList){
            ccg.setFinancialDocumentNumber(docNumber);
            correctionChangeGroupDao.save(ccg);
        
            List<CorrectionChange> changeList = ccg.getCorrectionChange();
            
            for(CorrectionChange cChange: changeList){
                cChange.setFinancialDocumentNumber(docNumber);
                correctionChangeDao.save(cChange);
            }
        
            List<CorrectionCriteria> criteriaList = ccg.getCorrectionCriteria();
        
            for(CorrectionCriteria cCriteria: criteriaList){
                cCriteria.setFinancialDocumentNumber(docNumber);
                correctionCriteriaDao.save(cCriteria);
            }
        }
    }


    public ActionForward viewResults(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
    
   /*     request.setAttribute(Constants.SEARCH_LIST_REQUEST_KEY, request.getParameter(Constants.SEARCH_LIST_REQUEST_KEY));
        request.setAttribute("reqSearchResults", GlobalVariables.getUserSession().retrieveObject(request.getParameter(Constants.SEARCH_LIST_REQUEST_KEY)));
   */     
        //UserSession userSession = (UserSession) request.getSession().getAttribute(Constants.USER_SESSION_KEY);
        
        
        //errorCorrectionForm = (CorrectionForm) GlobalVariables.getUserSession().retrieveObject(Constants.CORRECTION_FORM_KEY);
        //errorCorrectionForm.setMethodToCall("viewResults");
        
        CorrectionForm sessionForm = (CorrectionForm) GlobalVariables.getUserSession().retrieveObject(Constants.CORRECTION_FORM_KEY);
        
        
        
        errorCorrectionForm = (CorrectionForm) form;
        //form = (CorrectionForm) errorCorrectionForm;
        
        
        
        errorCorrectionForm.setChooseSystem(sessionForm.getChooseSystem());
        errorCorrectionForm.setEditMethod(sessionForm.getEditMethod());
        errorCorrectionForm.setManualEditFlag(sessionForm.getManualEditFlag());
        errorCorrectionForm.setEditableFlag(sessionForm.getEditableFlag());
        
        
        errorCorrectionForm.setDocument(sessionForm.getDocument());
        errorCorrectionForm.setDeleteFileFlag(sessionForm.getDeleteFileFlag());
        errorCorrectionForm.setProcessInBatch(sessionForm.isProcessInBatch());
        errorCorrectionForm.setDocId(sessionForm.getDocId());
        errorCorrectionForm.setDocTypeName(sessionForm.getDocTypeName());
        errorCorrectionForm.setMatchCriteriaOnly(sessionForm.getMatchCriteriaOnly());
        errorCorrectionForm.setGroupIdList(sessionForm.getGroupIdList());
        
        Object displayTableParameterPage = request.getParameter(new ParamEncoder("allEntries").encodeParameterName(TableTagParameters.PARAMETER_PAGE));
        Object displayTableParameterColumn = request.getParameter(new ParamEncoder("allEntries").encodeParameterName(TableTagParameters.PARAMETER_SORT)); 
            
        GlobalVariables.getUserSession().addObject(Constants.DISPLAY_TABLE_PAGE_NUMBER, displayTableParameterPage);
        GlobalVariables.getUserSession().addObject(Constants.DISPLAY_TABLE_COLUMN_NUMBER, displayTableParameterColumn);
        
        
        return mapping.findForward(Constants.MAPPING_BASIC);
}

    private List combineAdHocRecipients(KualiDocumentFormBase kualiDocumentFormBase) {
       
        List adHocRecipients = new ArrayList();
        adHocRecipients.addAll(kualiDocumentFormBase.getAdHocRoutePersons());
        adHocRecipients.addAll(kualiDocumentFormBase.getAdHocRouteWorkgroups());
    
        return adHocRecipients;
}

    @Override
    public ActionForward docHandler(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        CorrectionForm errorCorrectionForm =  (CorrectionForm) form;
        String command = errorCorrectionForm.getCommand();

        // in all of the following cases we want to load the document
        if (ArrayUtils.contains(DOCUMENT_LOAD_COMMANDS, command) && errorCorrectionForm.getDocId() != null) {
            loadDocument(errorCorrectionForm);
            CorrectionDocument document = (CorrectionDocument) errorCorrectionForm.getDocument();
            buildFormForShowingPreviousDoc(errorCorrectionForm, request, document);
        }
        else if (IDocHandler.INITIATE_COMMAND.equals(command)) {
            createDocument(errorCorrectionForm);
            
        }
        else {
            //LOG.error("docHandler called with invalid parameters");
            throw new IllegalStateException("docHandler called with invalid parameters");
        }

        return mapping.findForward(Constants.MAPPING_BASIC);
    
    }

    public void buildFormForShowingPreviousDoc(CorrectionForm errorCorrectionForm, HttpServletRequest request, CorrectionDocument oldDoc){
        CorrectionDocumentService correctionDocumentService = (CorrectionDocumentService) SpringServiceLocator.getBeanFactory().getBean("glCorrectionDocumentService"); 
        
        //build errorCorrectionForm
        //CorrectionChangeGroup correctionChangeGroup;
        if (errorCorrectionForm.getAllEntries() != null){
            errorCorrectionForm.getAllEntries().clear();
        }
        Integer correctionChangeGroupNextLineNumber = oldDoc.getCorrectionChangeGroupNextLineNumber();
        Integer docId = Integer.parseInt(oldDoc.getFinancialDocumentNumber());
        CorrectionDocument document = (CorrectionDocument) errorCorrectionForm.getDocument();
        
        errorCorrectionForm.setDocument(oldDoc);
        
        try {
        if (oldDoc.getCorrectionTypeCode().equals("M")){
            errorCorrectionForm.setEditMethod("manual");
        } else {errorCorrectionForm.setEditMethod("criteria");}
        
        if (oldDoc.getCorrectionInputFileName() != null){
            errorCorrectionForm.setChooseSystem("file");
        } else {errorCorrectionForm.setChooseSystem("system");
        }
        
        
        if (errorCorrectionForm.getEditMethod().equals("criteria")){
            document.getCorrectionChangeGroup().clear();
            CorrectionChangeGroup ccg = new CorrectionChangeGroup();
            
            for (int i=0; i<= correctionChangeGroupNextLineNumber.intValue(); i++){
            ccg = correctionDocumentService.findByDocumentNumberAndCorrectionChangeGroupNumber(docId, i);
                if (ccg != null){
                
                    if (correctionDocumentService.findByDocumentHeaderIdAndCorrectionGroupNumber(docId, i) != null){
                        ccg.setCorrectionChange(correctionDocumentService.findByDocumentHeaderIdAndCorrectionGroupNumber(docId, i));
                    }
                    if (correctionDocumentService.findByDocumentNumberAndCorrectionGroupNumber(docId, i) !=null){
                        ccg.setCorrectionCriteria(correctionDocumentService.findByDocumentNumberAndCorrectionGroupNumber(docId, i));
                    }
                    document.getCorrectionChangeGroup().add(ccg);
                }
            }
        } 
        
        String[] groupId = {oldDoc.getCorrectionOutputFileName()};
        int intGroupId = 0;
        try{intGroupId = Integer.parseInt(groupId[0]);}
        catch (Exception e) {}
        
        
        
        OriginEntryGroupService originEntryGroupService = (OriginEntryGroupService) SpringServiceLocator.getBeanFactory().getBean("glOriginEntryGroupService");
        if (originEntryGroupService.getExactMatchingEntryGroup(new Integer(intGroupId)) != null){
            showAllEntries(groupId, errorCorrectionForm, request);
        } else {
            errorCorrectionForm.setOriginEntryGroupDeletedMessage("There are no records for the origin entry group, '" + groupId[0] + "'.  It was deleted from system.");
        }
        //There are no records for the origin entry group selected
        //TODO: if errorCorrectionForm.getAllEntries is null (the OriginEntryGroup was deleted), then originEntryGroupDeletedMessage should have message.
        document.setFinancialDocumentNumber(docId.toString());
    
        }
     catch (Exception e){}
     
         errorCorrectionForm.setProcessInBatch(oldDoc.getCorrectionFileDeleteCode());
         errorCorrectionForm.setMatchCriteriaOnly(oldDoc.getCorrectionSelectionCode());
         
    }
    
    public OriginEntryGroupService getOriginEntryGroupService() {
        return originEntryGroupService;
    }

    public void setOriginEntryGroupService(OriginEntryGroupService originEntryGroupService) {
        this.originEntryGroupService = originEntryGroupService;
    }

    public OriginEntryService getOriginEntryService() {
        return originEntryService;
    }

    public void setOriginEntryService(OriginEntryService originEntryService) {
        this.originEntryService = originEntryService;
    }
    
public String changeFieldName(String fieldName){
        
        if(fieldName.equals("Fiscal Year")){
            return "universityFiscalYear";
        }
        if(fieldName.equals("Budget Year")){
            return "budgetYear";
        }
        if(fieldName.equals("Chart Code")){
            return "chartOfAccountsCode";
        }
        if(fieldName.equals("Account Number")){
            return "accountNumber";
        }
        if(fieldName.equals("Sub Account Number")){
            return "subAccountNumber";
        }
        if(fieldName.equals("Object Code")){
            return "financialObjectCode";
        }
        if(fieldName.equals("Sub Object Code")){
            return "financialSubObjectCode";
        }
        if(fieldName.equals("Balance Type")){
            return "financialBalanceTypeCode";
        }
        if(fieldName.equals("Object Type")){
            return "financialObjectTypeCode";
        }
        if(fieldName.equals("Fiscal Period")){
            return "universityFiscalPeriodCode";
        }
        if(fieldName.equals("Document Type")){
            return "financialDocumentTypeCode";
        }
        if(fieldName.equals("Origin Code")){
            return "financialSystemOriginationCode";
        }
        if(fieldName.equals("Document Number")){
            return "financialDocumentNumber";
        }
        if(fieldName.equals("Sequence Number")){
            return "transactionLedgerEntrySequenceNumber";
        }
        if(fieldName.equals("Description")){
            return "transactionLedgerEntryDescription";
        }
        if(fieldName.equals("Amount")){
            return "transactionLedgerEntryAmount";
        }
        if(fieldName.equals("Debit Credit Indicator")){
            return "transactionDebitCreditCode";
        }
        if(fieldName.equals("Transaction Date")){
            return "transactionDate";
        }
        if(fieldName.equals("Org Doc Number")){
            return "organizationDocumentNumber";
        }
        if(fieldName.equals("Project Code")){
            return "projectCode";
        }
        if(fieldName.equals("Org Ref ID")){
            return "organizationReferenceId";
        }
        if(fieldName.equals("Ref Doc Type")){
            return "referenceFinancialDocumentTypeCode";
        }
        if(fieldName.equals("Ref Origin Code")){
            return "referenceFinancialSystemOriginationCode";
        }
        if(fieldName.equals("Ref Doc Number")){
            return "referenceFinancialDocumentNumber";
        }
        if(fieldName.equals("Reversal Date")){
            return "financialDocumentReversalDate";
        }
        if(fieldName.equals("Enc Update Code")){
            return "transactionEncumbranceUpdateCode";
        }
        
       return null;
    }
    
}
