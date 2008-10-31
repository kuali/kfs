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
package org.kuali.kfs.module.purap.document.searching.generator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kuali.kfs.module.purap.PurapParameterConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.kfs.sys.service.impl.ParameterConstants;
import org.kuali.rice.kew.docsearch.DocSearchCriteriaDTO;
import org.kuali.rice.kew.docsearch.SearchAttributeCriteriaComponent;
import org.kuali.rice.kew.docsearch.StandardDocumentSearchGenerator;
import org.kuali.rice.kew.exception.WorkflowServiceError;
import org.kuali.rice.kew.user.WorkflowUser;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.service.KIMServiceLocator;

/**
 * This class...
 */
public abstract class PurApDocumentSearchGenerator extends StandardDocumentSearchGenerator {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurApDocumentSearchGenerator.class);

    private Map<String, SearchAttributeCriteriaComponent> searchComponentsByFormKey;

    public Map<String, SearchAttributeCriteriaComponent> getSearchComponentsByFormKey() {
        if (searchComponentsByFormKey.isEmpty()) {
            this.generateSearchComponentsByFormKeyMap();
        }
        return searchComponentsByFormKey;
    }

    public void setSearchComponentsByFormKey(Map<String, SearchAttributeCriteriaComponent> searchComponentsByFormKey) {
        this.searchComponentsByFormKey = searchComponentsByFormKey;
    }

    protected Map generateSearchComponentsByFormKeyMap() {
        Map criteriaMap = new HashMap();
        for (Iterator iter = getCriteria().getSearchableAttributes().iterator(); iter.hasNext();) {
            SearchAttributeCriteriaComponent component = (SearchAttributeCriteriaComponent) iter.next();
            criteriaMap.put(component.getFormKey(), component);
        }
        this.searchComponentsByFormKey = criteriaMap;
        return criteriaMap;
    }

    public abstract List<String> getSpecificSearchCriteriaFormFieldNames();

    public abstract List<String> getGeneralSearchUserRequiredFormFieldNames();

    public abstract List<String> getSearchAttributeFormFieldNamesToIgnore();

    public abstract String getErrorMessageForNonSpecialUserInvalidCriteria();

    public boolean isSpecialAccessSearchUser(WorkflowUser workflowUser) {
        String searchSpecialAccess = getSpecialAccessSearchUserWorkgroupName();
        Person currentUser = SpringContext.getBean(org.kuali.rice.kim.service.PersonService.class).getPerson(workflowUser.getWorkflowId());
        if (currentUser != null) {
            return KIMServiceLocator.getIdentityManagementService().isMemberOfGroup(currentUser.getPrincipalId(), org.kuali.kfs.sys.KFSConstants.KFS_GROUP_NAMESPACE, searchSpecialAccess);
        } else {
            String errorMessage = "Error attempting to find user with UUID '" + workflowUser.getUuId() + "'";
            LOG.error(errorMessage);
            throw new RuntimeException(errorMessage);
        }
    }

    public String getSpecialAccessSearchUserWorkgroupName() {
        return SpringContext.getBean(ParameterService.class).getParameterValue(ParameterConstants.PURCHASING_DOCUMENT.class, PurapParameterConstants.Workgroups.SEARCH_SPECIAL_ACCESS);
    }

    /**
     * @see org.kuali.rice.kew.docsearch.StandardDocumentSearchGenerator#clearSearch(org.kuali.rice.kew.docsearch.DocSearchCriteriaDTO)
     */
    @Override
    public DocSearchCriteriaDTO clearSearch(DocSearchCriteriaDTO searchCriteria) {
        DocSearchCriteriaDTO docSearchCriteriaDTO = new DocSearchCriteriaDTO();
        docSearchCriteriaDTO.setDocTypeFullName(searchCriteria.getDocTypeFullName());
        return docSearchCriteriaDTO;
    }

    /**
     * @see org.kuali.rice.kew.docsearch.StandardDocumentSearchGenerator#performPreSearchConditions(org.kuali.rice.kew.user.WorkflowUser,
     *      org.kuali.rice.kew.docsearch.DocSearchCriteriaDTO)
     */
    @Override
    public List<WorkflowServiceError> performPreSearchConditions(WorkflowUser user, DocSearchCriteriaDTO searchCriteria) {
        this.setCriteria(searchCriteria);
        this.generateSearchComponentsByFormKeyMap();
        List<WorkflowServiceError> errors = super.performPreSearchConditions(user, searchCriteria);
        if (isStandardCriteriaConsideredEmpty() && isSearchAttributeCriteriaConsideredEmpty()) {
            // error out for empty criteria
            addErrorMessageToList(errors, "The search criteria entered is not sufficient to search for documents of this type.");
        }
        /*
         * Error out if all of following are true: 1) standard search criteria are empty 2) search criteria does not contain at
         * least one 'specific criteria element' (ie: doc id or purap id) 3) user is not special-access user as defined by each
         * document 4) criteria is missing one or more required criteria elements of a non-special-access user search
         */
        else if ((isStandardCriteriaConsideredEmpty()) && (!isSpecialAccessSearchUser(user)) && (!containsAllGeneralSearchUserRequiredFields()) && (!containsOneOrMoreSpecificSearchCriteriaFields())) {
            // error out for non special user with invalid criteria
            addErrorMessageToList(errors, getErrorMessageForNonSpecialUserInvalidCriteria());
        }
        return errors;
    }

    protected boolean isSearchAttributeCriteriaConsideredEmpty() {
        Set<String> formFieldNamesToIgnore = new HashSet(getSearchAttributeFormFieldNamesToIgnore());
        List<String> formKeyFieldsToUse = new ArrayList<String>();
        for (Iterator iter = getSearchComponentsByFormKey().keySet().iterator(); iter.hasNext();) {
            String formKey = (String) iter.next();
            if (formFieldNamesToIgnore.contains(formKey)) {
                continue;
            }
            formKeyFieldsToUse.add(formKey);
        }
        // returns true if it finds a non-blank attribute with one of the given form key field names
        return !this.containsSearchCriteriaForListFormFieldNames(formKeyFieldsToUse, false);
    }

    protected boolean isStandardCriteriaConsideredEmpty() {
        return getCriteria().isStandardCriteriaConsideredEmpty(true);
    }

    protected boolean containsAllGeneralSearchUserRequiredFields() {
        return this.containsSearchCriteriaForListFormFieldNames(getGeneralSearchUserRequiredFormFieldNames(), true);
    }

    public boolean containsOneOrMoreSpecificSearchCriteriaFields() {
        return this.containsSearchCriteriaForListFormFieldNames(getSpecificSearchCriteriaFormFieldNames(), false);
    }

    private boolean containsSearchCriteriaForListFormFieldNames(List<String> listOfFormFieldNames, boolean checkForAllInList) {
        for (String formKey : listOfFormFieldNames) {
            SearchAttributeCriteriaComponent component = getSearchComponentsByFormKey().get(formKey);
            if (component == null) {
                throw new RuntimeException("Criteria Component for search using form key '" + formKey + "' cannot be found in search criteria searchable attributes");
            }
            if (component.isNonBlankValueGiven()) {
                if (!checkForAllInList) {
                    // checking for at least one element of list that is filled in and we found one
                    return true;
                }
            }
            else {
                if (checkForAllInList) {
                    // checking to make sure all list elements are filled in and at least one is not
                    return false;
                }
            }
        }
        if (checkForAllInList) {
            // checking to make sure all list elements are filled in and they are
            return true;
        }
        else {
            // checking for at least one element of list that is filled in and we found none
            return false;
        }
    }

}

