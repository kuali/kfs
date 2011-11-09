/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.sec.businessobject.lookup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sec.SecPropertyConstants;
import org.kuali.kfs.sec.businessobject.ModelMember;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kim.api.KimApiConstants;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.group.GroupService;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.kim.api.role.Role;
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
    protected RoleService roleService;
    protected GroupService groupSevice;
    protected PersonService personService;

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
        if (KimConstants.KimUIConstants.MEMBER_TYPE_ROLE_CODE.equals(memberTypeCode)) {
            List<String> roleSearchFields = getRoleLookupFields();
            roleSearchFields.remove(SecPropertyConstants.MEMBER_TYPE_CODE);
            for (String roleField : roleSearchFields) {
                if (fieldValues.containsKey(roleField) && StringUtils.isNotBlank(fieldValues.get(roleField))) {
                    searchValues.put(roleField, fieldValues.get(roleField));
                }
            }
            
            List<Role> resultRoles = roleService.lookupRoles(searchValues);
            for (Role kimRoleInfo : resultRoles) {
                ModelMember member = new ModelMember();
                member.setMemberId(kimRoleInfo.getId());
                member.setMemberTypeCode(KimConstants.KimUIConstants.MEMBER_TYPE_ROLE_CODE);
                member.setMemberName(kimRoleInfo.getNamespaceCode() + "-" + kimRoleInfo.getName());
                member.setActive(kimRoleInfo.isActive());

                results.add(member);
            }
        }
        else if (KimConstants.KimUIConstants.MEMBER_TYPE_GROUP_CODE.equals(memberTypeCode)) {
            List<String> groupSearchFields = getGroupLookupFields();
            groupSearchFields.remove(SecPropertyConstants.MEMBER_TYPE_CODE);
            for (String groupField : groupSearchFields) {
                if (fieldValues.containsKey(groupField) && StringUtils.isNotBlank(fieldValues.get(groupField))) {
                    searchValues.put(groupField, fieldValues.get(groupField));
                }
            }
            
            List<? extends Group> resultGroups = groupSevice.lookupGroups(searchValues);
            for (Group group : resultGroups) {
                ModelMember member = new ModelMember();
                member.setMemberId(group.getGroupId());
                member.setMemberTypeCode(KimConstants.KimUIConstants.MEMBER_TYPE_GROUP_CODE);
                member.setMemberName(group.getNamespaceCode() + "-" + group.getGroupName());
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
            
            List<? extends Person> resultPersons = personService.findPeople(searchValues);
            for (Person person : resultPersons) {
                ModelMember member = new ModelMember();
                member.setMemberId(person.getPrincipalId());
                member.setMemberTypeCode(KimConstants.KimUIConstants.MEMBER_TYPE_PRINCIPAL_CODE);
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

            if (KimConstants.KimUIConstants.MEMBER_TYPE_ROLE_CODE.equals(memberTypeCode)) {
                lookupFieldAttributeList = getRoleLookupFields();
            }
            else if (KimConstants.KimUIConstants.MEMBER_TYPE_GROUP_CODE.equals(memberTypeCode)) {
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
        int numCols;
        try {
            fields = FieldUtils.createAndPopulateFieldsForLookup(lookupFieldAttributeList, getReadOnlyFieldsList(), getBusinessObjectClass());

            BusinessObjectEntry boe = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry(this.getBusinessObjectClass().getName());
            numCols = boe.getLookupDefinition().getNumOfColumns();

        }
        catch (InstantiationException e) {
            throw new RuntimeException("Unable to create instance of business object class" + e.getMessage());
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException("Unable to create instance of business object class" + e.getMessage());
        }

        if (numCols == 0)
            numCols = KRADConstants.DEFAULT_NUM_OF_COLUMNS;

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

        lookupFields.add(KIMPropertyConstants.Role.ROLE_ID);
        lookupFields.add(KIMPropertyConstants.Role.ROLE_NAME);
        lookupFields.add(KimApiConstants.UniqueKeyConstants.NAMESPACE_CODE);
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

        lookupFields.add(KIMPropertyConstants.Group.GROUP_ID);
        lookupFields.add(KimApiConstants.UniqueKeyConstants.GROUP_NAME);
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

        lookupFields.add(KIMPropertyConstants.Person.PRINCIPAL_NAME);
        lookupFields.add(KIMPropertyConstants.Person.PRINCIPAL_ID);
        lookupFields.add(KIMPropertyConstants.Person.ENTITY_ID);
        lookupFields.add(KIMPropertyConstants.Person.FIRST_NAME);
        lookupFields.add(KIMPropertyConstants.Person.MIDDLE_NAME);
        lookupFields.add(KIMPropertyConstants.Person.LAST_NAME);
        lookupFields.add(KIMPropertyConstants.Person.EMAIL_ADDRESS);
        lookupFields.add(KIMPropertyConstants.Person.EMPLOYEE_ID);
        lookupFields.add(KRADPropertyConstants.ACTIVE);
        lookupFields.add(SecPropertyConstants.MEMBER_TYPE_CODE);

        return lookupFields;
    }

    /**
     * Sets the roleService attribute value.
     * 
     * @param roleService The roleService to set.
     */
    public void setRoleService(RoleService roleService) {
        this.roleService = roleService;
    }

    /**
     * Sets the groupSevice attribute value.
     * 
     * @param groupSevice The groupSevice to set.
     */
    public void setGroupSevice(GroupService groupSevice) {
        this.groupSevice = groupSevice;
    }

    /**
     * Sets the personService attribute value.
     * 
     * @param personService The personService to set.
     */
    public void setPersonService(PersonService personService) {
        this.personService = personService;
    }

}
