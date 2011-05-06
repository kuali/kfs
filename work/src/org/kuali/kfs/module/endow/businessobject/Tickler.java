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
package org.kuali.kfs.module.endow.businessobject;

import java.sql.Date;
import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.rice.kim.bo.Group;
import org.kuali.rice.kns.bo.Inactivateable;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;
import org.kuali.rice.kns.util.TypedArrayList;

public class Tickler extends PersistableBusinessObjectBase implements Inactivateable {
    
    private String number;
    private String typeCode;
    private String frequencyCode;
    private Date nextDueDate;
    private String detail;
    private Date entryDate;
    private Date terminationDate;
    private boolean active;
    
    private String groupId;
    protected String assignedToGroupNamespaceForLookup;
    protected String assignedToGroupNameForLookup;
    private Group groupLookup;
    
    private TicklerTypeCode type;
    private FrequencyCode frequency;
    
    // Collections
    private List<TicklerKEMID> kemIds;
    private List<TicklerSecurity> securities;
    private List<TicklerRecipientPrincipal> recipientPrincipals;
    private List<TicklerRecipientGroup> recipientGroups;
    
    public Tickler()
    {
        super();
        kemIds = new TypedArrayList(TicklerKEMID.class);
        securities = new TypedArrayList(TicklerSecurity.class);
        recipientPrincipals = new TypedArrayList(TicklerRecipientPrincipal.class);
        recipientGroups = new TypedArrayList(TicklerRecipientGroup.class);
    }
    
    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Date getNextDueDate() {
        return nextDueDate;
    }

    public void setNextDueDate(Date nextDueDate) {
        this.nextDueDate = nextDueDate;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public Date getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(Date entryDate) {
        this.entryDate = entryDate;
    }

    public Date getTerminationDate() {
        return terminationDate;
    }

    public void setTerminationDate(Date terminationDate) {
        this.terminationDate = terminationDate;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public List<TicklerKEMID> getKemIds() {
        return kemIds;
    }

    public void setKemIds(List<TicklerKEMID> kemIds) {
        this.kemIds = kemIds;
    }

    public List<TicklerSecurity> getSecurities() {
        return securities;
    }

    public void setSecurities(List<TicklerSecurity> securities) {
        this.securities = securities;
    }

    public List<TicklerRecipientPrincipal> getRecipientPrincipals() {
        return recipientPrincipals;
    }

    public void setRecipientPrincipals(List<TicklerRecipientPrincipal> recipientPrincipals) {
        this.recipientPrincipals = recipientPrincipals;
    }

    public List<TicklerRecipientGroup> getRecipientGroups() {
        return recipientGroups;
    }

    public void setRecipientGroups(List<TicklerRecipientGroup> recipientGroups) {
        this.recipientGroups = recipientGroups;
    }

    public String getFrequencyCode() {
        return frequencyCode;
    }

    public void setFrequencyCode(String frequencyCode) {
        this.frequencyCode = frequencyCode;
    }

    public FrequencyCode getFrequency() {
        return frequency;
    }

    public void setFrequency(FrequencyCode frequency) {
        this.frequency = frequency;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public TicklerTypeCode getType() {
        return type;
    }

    public void setType(TicklerTypeCode type) {
        this.type = type;
    }

    /**
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap<String, String> toStringMapper() {
        LinkedHashMap<String, String> m = new LinkedHashMap<String, String>();
        m.put(EndowPropertyConstants.TICKLER_NUMBER, getNumber());
        return m;
    }

    public String getAssignedToGroupNamespaceForLookup() {
        return assignedToGroupNamespaceForLookup;
    }

    public void setAssignedToGroupNamespaceForLookup(String assignedToGroupNamespaceForLookup) {
        this.assignedToGroupNamespaceForLookup = assignedToGroupNamespaceForLookup;
    }

    public String getAssignedToGroupNameForLookup() {
        return assignedToGroupNameForLookup;
    }

    public void setAssignedToGroupNameForLookup(String assignedToGroupNameForLookup) {
        this.assignedToGroupNameForLookup = assignedToGroupNameForLookup;
    }

    public Group getGroupLookup() {
        return groupLookup;
    }

    public void setGroupLookup(Group groupLookup) {
        this.groupLookup = groupLookup;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
    
    
}
