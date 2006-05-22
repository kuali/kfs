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
import java.io.DataInputStream;
import java.io.InputStream;
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

import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.PersistenceBrokerFactory;
import org.apache.ojb.broker.query.Criteria;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.Constants;
import org.kuali.KeyConstants;
import org.kuali.core.bo.BusinessObject;
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
import org.kuali.module.gl.document.CorrectionDocument;
import org.kuali.module.gl.service.OriginEntryGroupService;
import org.kuali.module.gl.service.OriginEntryService;
import org.kuali.module.gl.web.struts.form.CorrectionForm;

/**
 * @author Laran Evans <lc278@cornell.edu>
 * @version $Id: CorrectionAction.java,v 1.2 2006-05-22 18:51:26 wesprice Exp $
 * 
 */

public class CorrectionAction extends KualiDocumentActionBase {
    
    //shawn
    private DateTimeService dateTimeService;
    private CorrectionForm errorCorrectionForm;
    private CorrectionDocument document;
    private OriginEntryGroupService originEntryGroupService;
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
    
    public void setConvenienceObject(ActionForm form) {
        errorCorrectionForm = (CorrectionForm) form;
		document = (CorrectionDocument) errorCorrectionForm.getDocument();
		originEntryGroupService = (OriginEntryGroupService) SpringServiceLocator.getBeanFactory().getBean("glOriginEntryGroupService");
		originEntryDao = (OriginEntryDao) SpringServiceLocator.getBeanFactory().getBean("glOriginEntryDao");
		originEntryService = (OriginEntryService) SpringServiceLocator.getBeanFactory().getBean("glOriginEntryService");
		
    }
    
    
    
    
    
    public ActionForward uploadFile(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) 
    throws Exception {
        
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream inputStream = 
            classLoader.getResourceAsStream("fileUpload");
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        
        //InputStream inputStream = request.getInputStream();
        DataInputStream in = new DataInputStream(inputStream);

        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        String test = in.readLine();
        
        try {
            String currentLine = br.readLine();
            while (currentLine != null) {
                
                

              

                currentLine = br.readLine();
            }
        }
        finally {
            br.close();
        }
        
        
        
        
        return mapping.findForward(Constants.MAPPING_BASIC);
        
    }
    
    
	public ActionForward addCorrectionGroup(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) 
	throws Exception {
	    setConvenienceObject(form);
		
		// rebuild the document state
		CorrectionActionHelper.rebuildDocumentState(request, document);
		
		// create a new correction group and add it to the document
		document.addCorrectionGroup(new CorrectionChangeGroup());
		
		// for consistent presentation ...
		CorrectionActionHelper.sortForDisplay(document.getCorrectionChangeGroup());
		
		return mapping.findForward(Constants.MAPPING_BASIC);
	}

	/**
	 * Remove an existing CorrectionGroup from the Document.
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
	    setConvenienceObject(form);

		// rebuild the document state
		CorrectionActionHelper.rebuildDocumentState(request, document);

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
	    setConvenienceObject(form);
	    
		// rebuild the document state
		CorrectionActionHelper.rebuildDocumentState(request, document);

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
	    setConvenienceObject(form);

		// rebuild the document state
		CorrectionActionHelper.rebuildDocumentState(request, document);

		// load the correction group from the request
		Integer[] criterionIndex = 
			CorrectionActionHelper.getCriterionToDelete(request, document);
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
	public ActionForward addReplacementSpecification(ActionMapping mapping, ActionForm form, 
			HttpServletRequest request, HttpServletResponse response) 
	throws Exception {
	    setConvenienceObject(form);
		
		// rebuild the document state
		CorrectionActionHelper.rebuildDocumentState(request, document);

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
	    setConvenienceObject(form);
		
		// rebuild the document state
		CorrectionActionHelper.rebuildDocumentState(request, document);

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
		
		return mapping.findForward(Constants.MAPPING_BASIC);
	}

	/**
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param reponse
	 * @param withReplacement
	 * @return
	 * @throws Exception
	 */
	public ActionForward searchAndReplaceWithCriteria(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse reponse, boolean withReplacement)
	throws Exception {
	    setConvenienceObject(form);

		
		// rebuild the document state
		CorrectionActionHelper.rebuildDocumentState(request, document);
		
		//shawn :commented out - matchingEntries, criteria...etc 
		
		// Create a container for all of the matching entries we'll find.
		Collection matchingEntries = errorCorrectionForm.getEntriesThatMatchSearchCriteria();

		// Clear the container. We don't want to retain results from previous searches.
		matchingEntries.clear();
		
		// Configure the query.
		PersistenceBroker broker = PersistenceBrokerFactory.defaultPersistenceBroker();
	    Criteria criteria = new Criteria();
	    String groupId[] = request.getParameterValues("pending-origin-entry-group-id");
        
	    BusinessObject obj = null;
	    Class example = OriginEntry.class;
        try {
            obj = (BusinessObject) example.newInstance();
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException("Cannot get new instance of " + example.getName(), e);
        }
        catch (InstantiationException e) {
            throw new RuntimeException("Cannot instantiate " + example.getName(), e);
        }
	    
        //change string GroupID to Integer GroupID
	    /*for (int i=0; i<stringGroupId.length; i+=1) {
	        //int numberGroupID[] = null;
	        int temp = Integer.parseInt(stringGroupId[i]);
	        groupId.add(new Integer(temp));    
	    }*/
	    
	    
	    Map fieldValues = new HashMap();
	    
		CorrectionChangeGroup correctionGroup;
		List correctionGroups = document.getCorrectionChangeGroup();
		
	    Collection searchResults = null;
	     
	    //check required field and show error messages
	    if (!checkEmpteyValues(correctionGroups, groupId)){
	        CorrectionActionHelper.sortForDisplay(document.getCorrectionChangeGroup());
			
			return mapping.findForward(Constants.MAPPING_BASIC);
	    }
	   
	    
	    //searchFields.put()
	    
	    //message for test result
	    String message= ""; 
	    //search using Groups and Fields
	    for (int i=0; i<groupId.length; i+=1) {
	        
	        
	        Iterator groupIter = correctionGroups.iterator();
	        
	        //tempGroupNumber is just for test
	        int tempGroupNumber = 1; 
	        
	        while (groupIter.hasNext()) {
	            correctionGroup = (CorrectionChangeGroup) groupIter.next();
	            	
	          
	            	
	            //Set a Map with fields
	            fieldValues = createSearchMap(correctionGroup);
	            
	            fieldValues.put("entryGroupId", groupId[i]);
	            
	            LookupService lookupService = (LookupService) SpringServiceLocator.getBeanFactory().getBean("lookupService");
	            
	            //searchResults is collection of OriginEntry
	            searchResults = (Collection) lookupService.findCollectionBySearchUnbounded(OriginEntry.class, fieldValues);
	    
	            
	            //below 4-5 lines are just for test 
	            String convert = String.valueOf(searchResults.size());
	    	    //Long resultSize = Long.valueOf(convert);
	    	    message += "OriginGroup " + groupId[i] + ",  Group : " + convert + "items found. /     ";
	    	    tempGroupNumber += 1;
	           
	            if (searchResults.size() > 0) {
	                //creat Output
	                //create an OriginEntryGroup
	        	    java.sql.Date today = new java.sql.Date(System.currentTimeMillis());
	        	    OriginEntryGroup newOriginEntryGroup = originEntryGroupService.createGroup(today, "GLCP", true, true, false);
	        	    
	                createNewOutput(newOriginEntryGroup, groupId[i]);
	                
	                fieldValues.remove("entryGroupId");
	                fieldValues.put("entryGroupId", newOriginEntryGroup.getId().toString());
	                Collection replaceEntries = null;
	                
	                //search entries to replace and save
	                replaceEntries = (Collection) lookupService.findCollectionBySearchUnbounded(OriginEntry.class, fieldValues);
	                Iterator replaceEntriesIter = replaceEntries.iterator();
	                while (replaceEntriesIter.hasNext()) {
	    	            OriginEntry eachReplaceEntries = (OriginEntry) replaceEntriesIter.next();
	    	            //replace entries
	    	            replaceOriginEntryValues(eachReplaceEntries, correctionGroup);
	                }
	            }
	            //initialize search field
	            fieldValues = new HashMap();
	        }
	        request.setAttribute("resultMessage", message);
	        
	    }
	    
	
		CorrectionActionHelper.sortForDisplay(document.getCorrectionChangeGroup());
		
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
	public ActionForward showAllEntries(ActionMapping mapping, ActionForm form, 
			HttpServletRequest request, HttpServletResponse response) 
	throws Exception {
	    setConvenienceObject(form);
	    
	    Collection matchingEntries = errorCorrectionForm.getEntriesThatMatchSearchCriteria();
	    String groupId[] = null;
        groupId = request.getParameterValues("pending-origin-entry-group-id");
	    
		// Clear the container. We don't want to retain results from previous searches.
		matchingEntries.clear();
       
		
		Map searchMap = new HashMap();
		
		// Configure the query.
		/*PersistenceBroker broker = PersistenceBrokerFactory.defaultPersistenceBroker();
	    Criteria criteria = new Criteria();*/
	    searchMap.put("entryGroupId", groupId[0]);
	    Collection resultFromGroupId = originEntryDao.getMatchingEntriesByCollection(searchMap);
		Iterator entriesInGroup = resultFromGroupId.iterator();
		// For each entry ...
        while (entriesInGroup.hasNext()) {
            OriginEntry eachOriginEntry = (OriginEntry) entriesInGroup.next();
           	matchingEntries.add(eachOriginEntry);
			
    		}
        
		return mapping.findForward(Constants.MAPPING_BASIC);
	}
	
	/**
	 * Get as a Set all OriginEntries that satisfies all of the SearchCriteria within any 
	 * Single CorrectionGroup, for any CorrectionGroup. So, if an OriginEntry satisfies 
	 * all SearchCriteria within a given CorrectionGroup it will be added to this Set. If
	 * the same OriginEntry satisfies all SearchCriteria in more than one CorrectionGroup
	 * it is still present in the Set exactly once. If an OriginEntry satisfies some, but
	 * not all SearchCriteria within a CorrectionGroup it is not added to the Set.
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward getMatchingEntriesWithoutReplacement(ActionMapping mapping, ActionForm form, 
			HttpServletRequest request, HttpServletResponse response) 
	throws Exception {
		return mapping.findForward(Constants.MAPPING_BASIC);
	    //return getMatchingEntries(mapping, form, request, response, false);
		/*
		CorrectionForm errorCorrectionForm = (CorrectionForm) form;
		GeneralLedgerErrorCorrectionDocument document = 
			(GeneralLedgerErrorCorrectionDocument) errorCorrectionForm.getDocument();

		// rebuild the document state
		CorrectionActionHelper.rebuildDocumentState(request, document);

		// We want to first find origin entry group with the given id.
		//OriginEntryGroupService originEntryGroupService = new OriginEntryGroupServiceImpl();
		OriginEntryGroupService originEntryGroupService = (OriginEntryGroupService) SpringServiceLocator.getBeanFactory().getBean("glOriginEntryGroupService");
		
		OriginEntryGroup group 
			= originEntryGroupService.getOriginEntryGroup(document.getOriginEntryGroupId());
		
		// Create a container for all of the matching entries we'll find.
		Set matchingEntries = errorCorrectionForm.getEntriesThatMatchSearchCriteria();

		// Clear the container. We don't want to retain results from previous searches.
		matchingEntries.clear();
		
		// Configure the query.
		PersistenceBroker broker = PersistenceBrokerFactory.defaultPersistenceBroker();
	    Criteria criteria = new Criteria();
	    criteria.addEqualTo("ORIGIN_ENTRY_GRP_ID", group.getId());

		// Load all entries in that group.
	    QueryByCriteria qbc = QueryFactory.newQuery(OriginEntry.class, criteria);
	    Iterator entriesInGroup = broker.getIteratorByQuery(qbc);
		
		// For each entry ...
        while (entriesInGroup.hasNext()) {
            OriginEntry originEntry = (OriginEntry) entriesInGroup.next();
            List correctionGroups = document.getCorrectionGroups();
            
    		try {
    			//  ... and for each criteria group ...
    			for(Iterator iterator = correctionGroups.iterator(); iterator.hasNext();) {
    				CorrectionGroup correctionGroup = (CorrectionGroup) iterator.next(); 
    				List matchCriteria = correctionGroup.getSearchCriteria();
    				
    				// ... if the entry matches all of the criteria in the current group ...
    				if(CorrectionActionHelper
    						.originEntrySatisfiesAllCriteria(originEntry, matchCriteria)) {
    					// ... indicate the match ...
    					matchingEntries.add(originEntry);
    					break;
    				}
    			}
    		} catch(Throwable t) {
    			t.printStackTrace();
    		}
        }
		
		// for consistent presentation ...
		CorrectionActionHelper.sortForDisplay(document.getCorrectionGroups());
		
		return mapping.findForward(Constants.MAPPING_BASIC);
		*/
	}

    /**
     * Gets the lookupService attribute. 
     * @return Returns the lookupService.
     */
    /*public LookupService getLookupService() {
        return lookupService;
    }
    *//**
     * Sets the lookupService attribute value.
     * @param lookupService The lookupService to set.
     *//*
    public void setLookupService(LookupService lookupService) {
        this.lookupService = lookupService;
    }*/
	
	
	public String changeSearchField(String op, String field){
	
	    if (op.equals("ne")){ return "!" + field; }
	    
	    if (op.equals("gt")){ return ">" + field; }
	    
	    if (op.equals("lt")){ return "<" + field; }
	    
	    if (op.equals("sw")){ return field + "%"; }
	    
	    if (op.equals("ew")){ return "%" + field; }
	    
	    return "%" + field + "%";
	    
	}
	
	
	
	
	
	
	public ActionForward route(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

	    return mapping.findForward(Constants.MAPPING_BASIC);
	}

	  
	public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
 
	    return mapping.findForward(Constants.MAPPING_BASIC);
	}
    
	
	public ActionForward blanketApprove(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
	    return mapping.findForward(Constants.MAPPING_BASIC);
	
	
	}
	
	
	public ActionForward disapprove(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

	    return mapping.findForward(Constants.MAPPING_BASIC);
	}

	
	public ActionForward cancel(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
	
	    return mapping.findForward(Constants.MAPPING_CANCEL);
	}

	
	
	public ActionForward close(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)throws Exception {
	    return mapping.findForward(Constants.MAPPING_CLOSE);
	}	
	
	
	private boolean checkEmptyValues(CorrectionDocument document){
	    
	    
	    return true;
	}
	
	
	private Map createSearchMap(CorrectionChangeGroup correctionGroup){
	    Map fieldValues = new HashMap();
	    CorrectionCriteria correctionSearchCriterion;
	    Iterator fieldIter = correctionGroup.getCorrectionCriteria().iterator();
        while (fieldIter.hasNext()){
            correctionSearchCriterion = (CorrectionCriteria) fieldIter.next();
            String operator = correctionSearchCriterion.getCorrectionOperatorCode();
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
	
	private void createNewOutput(OriginEntryGroup newOriginEntryGroup, String groupId){
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
	            originEntryService.createEntry(eachEntry, newOriginEntryGroup);
	            }
	}
	
	public void replaceOriginEntryValues(OriginEntry eachReplaceEntries, CorrectionChangeGroup correctionGroup) throws Exception{
	   
	    CorrectionChange correctionReplacementSpecification;
	    Iterator replaceIter = correctionGroup.getCorrectionChange().iterator();
        
	    while (replaceIter.hasNext()){
            correctionReplacementSpecification = (CorrectionChange) replaceIter.next();
            String replaceValue = correctionReplacementSpecification.getCorrectionFieldValue();
            String replaceField = correctionReplacementSpecification.getCorrectionFieldName();
               
            	if (replaceField.equals("accountNumber")){
                    eachReplaceEntries.setAccountNumber(replaceValue);
                }
                if (replaceField.equals("financialDocumentNumber")){
                    eachReplaceEntries.setFinancialDocumentNumber(replaceValue);
                }
                if (replaceField.equals("referenceFinancialDocumentNumber")){
                    eachReplaceEntries.setReferenceFinancialDocumentNumber(replaceValue);
                }
                if (replaceField.equals("referenceFinancialDocumentTypeCode")){
                    eachReplaceEntries.setReferenceFinancialDocumentTypeCode(replaceValue);
                }
                if (replaceField.equals("financialDocumentReversalDate")){
                    //convert String to Date
                    Date convertDate = SpringServiceLocator.getDateTimeService().convertToSqlDate(replaceValue);
                    eachReplaceEntries.setFinancialDocumentReversalDate(convertDate);
                }
                if (replaceField.equals("financialDocumentTypeCode")){
                    eachReplaceEntries.setFinancialDocumentTypeCode(replaceValue);
                }
                if (replaceField.equals("financialBalanceTypeCode")){
                    eachReplaceEntries.setFinancialBalanceTypeCode(replaceValue);
                }
                if (replaceField.equals("chartOfAccountsCode")){
                    eachReplaceEntries.setChartOfAccountsCode(replaceValue);
                }
                if (replaceField.equals("financialObjectTypeCode")){
                    eachReplaceEntries.setFinancialObjectTypeCode(replaceValue);
                }
                if (replaceField.equals("financialObjectCode")){
                    eachReplaceEntries.setFinancialObjectCode(replaceValue);
                }
                if (replaceField.equals("financialSubObjectCode")){
                    eachReplaceEntries.setFinancialSubObjectCode(replaceValue);
                }
                if (replaceField.equals("financialSystemOriginationCode")){
                    eachReplaceEntries.setFinancialSystemOriginationCode(replaceValue);
                }
                if (replaceField.equals("organizationDocumentNumber")){
                    eachReplaceEntries.setOrganizationDocumentNumber(replaceValue);
                }
                if (replaceField.equals("organizationReferenceId")){
                    eachReplaceEntries.setOrganizationReferenceId(replaceValue);
                }
                if (replaceField.equals("projectCode")){
                    eachReplaceEntries.setProjectCode(replaceValue);
                }
                if (replaceField.equals("subAccountNumber")){
                    eachReplaceEntries.setSubAccountNumber(replaceValue);
                }
                if (replaceField.equals("transactionDate")){
                    //convert String to Date
                    Date convertDate = SpringServiceLocator.getDateTimeService().convertToSqlDate(replaceValue);
                    eachReplaceEntries.setFinancialDocumentReversalDate(convertDate);
                }
                if (replaceField.equals("transactionDebitCreditCode")){
                    eachReplaceEntries.setTransactionDebitCreditCode(replaceValue);
                }
                if (replaceField.equals("transactionEncumbranceUpdateCode")){
                    eachReplaceEntries.setTransactionEncumbranceUpdateCode(replaceValue);
                }
                if (replaceField.equals("transactionLedgerEntrySequenceNumber")){
                    //convert String to Integer
                    int convertInt = Integer.parseInt(replaceValue);
                    eachReplaceEntries.setTransactionLedgerEntrySequenceNumber(new Integer(convertInt));
                }
                if (replaceField.equals("transactionLedgerEntryAmount")){
                    //convert String to Integer
                    int convertInt = Integer.parseInt(replaceValue);
                    eachReplaceEntries.setTransactionLedgerEntryAmount(new KualiDecimal(convertInt));
                }
                if (replaceField.equals("transactionLedgerEntryDescription")){
                    eachReplaceEntries.setTransactionLedgerEntryDescription(replaceValue);
                }
                if (replaceField.equals("universityFiscalPeriodCode")){
                    eachReplaceEntries.setUniversityFiscalPeriodCode(replaceValue);
                }
                if (replaceField.equals("universityFiscalYear")){
                    //convert String to Integer
                    int convertInt = Integer.parseInt(replaceValue);
                    eachReplaceEntries.setUniversityFiscalYear(new Integer(convertInt));
                }
                if (replaceField.equals("budgetYear")){
                    eachReplaceEntries.setBudgetYear(replaceValue);
                }
                
                originEntryDao.saveOriginEntry(eachReplaceEntries);
                
            }   
        }
	
	
	
	public boolean checkEmpteyValues(List correctionGroups, String[] groupId){
	    boolean returnVal = true; 
	    
	    //if the description is required, then  
	    if (document.getDocumentHeader().getFinancialDocumentDescription() == null) {
	        //GlobalVariables.getErrorMap().put("documentHeader.financialDocumentDescription", KeyConstants.ERROR_REQUIRED, "Financial Document Description");
	        GlobalVariables.getErrorMap().put("document.documentHeader.financialDocumentDescription", KeyConstants.ERROR_REQUIRED, "Financial Document Description");
	    }
	     
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
}   
