/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.sec.businessobject.lookup;

import static org.kuali.rice.core.api.criteria.PredicateFactory.equal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sec.SecPropertyConstants;
import org.kuali.kfs.sec.businessobject.ModelMember;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.criteria.Predicate;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.core.api.membership.MemberType;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.group.Group;
import org.kuali.rice.kim.api.group.GroupQueryResults;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.role.Role;
import org.kuali.rice.kim.api.role.RoleQueryResults;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.impl.KIMPropertyConstants;
import org.kuali.rice.kns.datadictionary.BusinessObjectEntry;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.util.FieldUtils;
import org.kuali.rice.kns.web.ui.Row;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.KRADPropertyConstants;


/**
 * Lookupable for ModelMember business object. Needs to change the lookup to search Person, Role, or Group based on the member type passed in
 */
public class ModelMemberLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {

    protected List<Row> rows;

    public ModelMemberLookupableHelperServiceImpl() {
        rows = null;
    }

    /**
     * @see org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl#getSearchResults(java.util.Map)
     */
    @Override
    public List<? extends BusinessObject> getSearchResults(Map<String, String> fieldValues) {
        List results = new ArrayList();

        Map<String, String> searchValues = new HashMap<String, String>();

        String memberTypeCode = fieldValues.get(SecPropertyConstants.MEMBER_TYPE_CODE);
        if (MemberType.ROLE.getCode().equals(memberTypeCode)) {
            List<String> roleSearchFields = getRoleLookupFields();
            roleSearchFields.remove(SecPropertyConstants.MEMBER_TYPE_CODE);
            for (String roleField : roleSearchFields) {
                if (fieldValues.containsKey(roleField) && StringUtils.isNotBlank(fieldValues.get(roleField))) {
                    searchValues.put(roleField, fieldValues.get(roleField));
                }
            }

            RoleQueryResults resultRoles = KimApiServiceLocator.getRoleService().findRoles(toQuery(searchValues));
            for (Role kimRoleInfo : resultRoles.getResults()) {
                ModelMember member = new ModelMember();
                member.setMemberId(kimRoleInfo.getId());
                member.setMemberTypeCode(MemberType.ROLE.getCode());
                member.setMemberName(kimRoleInfo.getNamespaceCode() + "-" + kimRoleInfo.getName());
                member.setActive(kimRoleInfo.isActive());

                results.add(member);
            }
        }
        else if ( MemberType.GROUP.getCode().equals(memberTypeCode)) {
            List<String> groupSearchFields = getGroupLookupFields();
            groupSearchFields.remove(SecPropertyConstants.MEMBER_TYPE_CODE);
            for (String groupField : groupSearchFields) {
                if (fieldValues.containsKey(groupField) && StringUtils.isNotBlank(fieldValues.get(groupField))) {
                    searchValues.put(groupField, fieldValues.get(groupField));
                }
            }

            GroupQueryResults resultGroups = KimApiServiceLocator.getGroupService().findGroups(toQuery(searchValues));
            for (Group group : resultGroups.getResults()) {
                ModelMember member = new ModelMember();
                member.setMemberId(group.getId());
                member.setMemberTypeCode( MemberType.GROUP.getCode() );
                member.setMemberName(group.getNamespaceCode() + "-" + group.getName());
                member.setActive(group.isActive());

                results.add(member);
            }
        }
        else {
            List<String> personSearchFields = getPersonLookupFields();
            personSearchFields.remove(SecPropertyConstants.MEMBER_TYPE_CODE);
            for (String personField : personSearchFields) {
                if (fieldValues.containsKey(personField) && StringUtils.isNotBlank(fieldValues.get(personField))) {
                    searchValues.put(personField, fieldValues.get(personField));
                }
            }

            List<? extends Person> resultPersons = KimApiServiceLocator.getPersonService().findPeople(searchValues);
            for (Person person : resultPersons) {
                ModelMember member = new ModelMember();
                member.setMemberId(person.getPrincipalId());
                member.setMemberTypeCode(MemberType.PRINCIPAL.getCode());
                member.setMemberName(person.getName());
                member.setActive(person.isActive());

                results.add(member);
            }
        }

        return results;
    }

    /**
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#setRows()
     */
    @Override
    protected void setRows() {
        List<String> lookupFieldAttributeList = new ArrayList<String>();
        if (getParameters().containsKey(SecPropertyConstants.MEMBER_TYPE_CODE)) {
            String memberTypeCode = ((String[]) getParameters().get(SecPropertyConstants.MEMBER_TYPE_CODE))[0];

            if (MemberType.ROLE.getCode().equals(memberTypeCode)) {
                lookupFieldAttributeList = getRoleLookupFields();
            }
            else if (MemberType.GROUP.getCode().equals(memberTypeCode)) {
                lookupFieldAttributeList = getGroupLookupFields();
            }
            else {
                lookupFieldAttributeList = getPersonLookupFields();
            }
        }
        else {
            lookupFieldAttributeList = getPersonLookupFields();
        }

        // construct field object for each search attribute
        List fields = new ArrayList();
        int numCols = 0;
        try {
            fields = FieldUtils.createAndPopulateFieldsForLookup(lookupFieldAttributeList, getReadOnlyFieldsList(), getBusinessObjectClass());

            BusinessObjectEntry boe = (BusinessObjectEntry) SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry(this.getBusinessObjectClass().getName());
            numCols = boe.getLookupDefinition().getNumOfColumns();

        }
        catch (InstantiationException e) {
            throw new RuntimeException("Unable to create instance of business object class", e);
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException("Unable to create instance of business object class", e);
        }

        if (numCols == 0) {
            numCols = KRADConstants.DEFAULT_NUM_OF_COLUMNS;
        }

        rows = FieldUtils.wrapFields(fields, numCols);
    }

    /**
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getRows()
     *
     * KRAD Conversion: Performs retrieving the rows.
     * No use data dictionary.
     */
    @Override
    public List<Row> getRows() {
        return rows;
    }

    /**
     * Builds List of search field names for the role lookup
     *
     * @return List<String> containing lookup field names
     */
    protected List<String> getRoleLookupFields() {
        List<String> lookupFields = new ArrayList<String>();

        lookupFields.add(SecPropertyConstants.ROLE_ID);
        lookupFields.add(SecPropertyConstants.ROLE_NAME);
        lookupFields.add(KimConstants.UniqueKeyConstants.NAMESPACE_CODE);
        lookupFields.add(KRADPropertyConstants.ACTIVE);
        lookupFields.add(SecPropertyConstants.MEMBER_TYPE_CODE);

        return lookupFields;
    }

    /**
     * Builds List of search field names for the group lookup
     *
     * @return List<String> containing lookup field names
     */
    protected List<String> getGroupLookupFields() {
        List<String> lookupFields = new ArrayList<String>();

        lookupFields.add(SecPropertyConstants.GROUP_ID);
        lookupFields.add(SecPropertyConstants.GROUP_NAME);
        lookupFields.add(KRADPropertyConstants.ACTIVE);
        lookupFields.add(SecPropertyConstants.MEMBER_TYPE_CODE);

        return lookupFields;
    }

    /**
     * Builds List of search field names for the person lookup
     *
     * @return List<String> containing lookup field names
     */
    protected List<String> getPersonLookupFields() {
        List<String> lookupFields = new ArrayList<String>();

        lookupFields.add(SecPropertyConstants.PRINCIPAL_NAME);
        lookupFields.add(SecPropertyConstants.PRINCIPAL_ID);
        lookupFields.add(SecPropertyConstants.ENTITY_ID);
        lookupFields.add(SecPropertyConstants.FIRST_NAME);
        lookupFields.add(SecPropertyConstants.MIDDLE_NAME);
        lookupFields.add(SecPropertyConstants.LAST_NAME);
        lookupFields.add(SecPropertyConstants.EMAIL_ADDRESS);
        lookupFields.add(SecPropertyConstants.EMPLOYEE_ID);
        lookupFields.add(KRADPropertyConstants.ACTIVE);
        lookupFields.add(SecPropertyConstants.MEMBER_TYPE_CODE);

        return lookupFields;
    }

    private QueryByCriteria toQuery(Map<String,?> fieldValues) {
        Set<Predicate> preds = new HashSet<Predicate>();
        for (String key : fieldValues.keySet()) {
            preds.add(equal(key, fieldValues.get(key)));
        }
        Predicate[] predicates = new Predicate[0];
        predicates = preds.toArray(predicates);
        return QueryByCriteria.Builder.fromPredicates(predicates);
    }

}
