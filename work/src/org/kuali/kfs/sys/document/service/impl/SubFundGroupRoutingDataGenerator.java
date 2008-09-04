/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.sys.document.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.service.RoutingDataGenerator;
import org.kuali.kfs.sys.document.workflow.RoutingData;
import org.kuali.kfs.sys.document.workflow.RoutingSubFundGroup;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.util.ObjectUtils;

/**
 * Creates a RoutingData object which hold routing data about various sub-funds.
 */
public class SubFundGroupRoutingDataGenerator implements RoutingDataGenerator {
    private List<String> accountingLineCollectionProperties;

    /**
     * Generates a SubFundGroupRoutingData object from the document, by iterating over the listed
     * accountingLineCollectionProperties
     * @see org.kuali.kfs.sys.document.service.RoutingDataGenerator#generateRoutingData(org.kuali.rice.kns.document.Document)
     */
    public RoutingData generateRoutingData(Document document) {
        RoutingData routingData = new RoutingData();
        
        Set<RoutingSubFundGroup> subFundGroupsForRouting = new HashSet<RoutingSubFundGroup>();
        if (accountingLineCollectionProperties != null && accountingLineCollectionProperties.size() > 0) {
            for (String accountingLineCollectionProperty : accountingLineCollectionProperties) {
                final List accountingLines = getAccountingLineCollection((AccountingDocument)document, accountingLineCollectionProperty);
                subFundGroupsForRouting.addAll(getSubFundGroupsForAccountingLineCollection(accountingLines));
            }
        }
        
        routingData.setRoutingType(getRuleAttributeName());
        routingData.setRoutingSet(subFundGroupsForRouting);
        
        return routingData;
    }
    
    /**
     * Finds the List of accounting lines on the given document and returns them
     * @param document the document the accounting line collection lives on
     * @param collectionProperty the property of the list on the document
     * @return the List of accounting lines 
     */
    protected List getAccountingLineCollection(AccountingDocument document, String collectionProperty) {
        return (List)ObjectUtils.getPropertyValue(document, collectionProperty);
    }
    
    /**
     * Builds a Set of RoutingSubFundGroup information for the given accounting line collection
     * @param accountingLineCollection a List of accountingLines
     * @return the Set of RoutingSubFundGroup information
     */
    protected Set<RoutingSubFundGroup> getSubFundGroupsForAccountingLineCollection(List accountingLineCollection) {
        Set<RoutingSubFundGroup> subFundGroupsForRouting = new HashSet<RoutingSubFundGroup>();
        if (accountingLineCollection != null && accountingLineCollection.size() > 0) {
            for (Object accountingLineAsObject : accountingLineCollection) {
                final AccountingLine line = (AccountingLine)accountingLineAsObject;
                subFundGroupsForRouting.add(getRoutingSubFundGroupForAccountingLine(line));
            }
        }
        return subFundGroupsForRouting;
    }
    
    /**
     * Builds a RoutingSubFundGroup data to encapsulate the sub-fund group key associated with the accounting line
     * @param line the accounting line to find a sub-fund group key from
     * @return RoutingSubFundGroup data encapsulating that key
     */
    protected RoutingSubFundGroup getRoutingSubFundGroupForAccountingLine(AccountingLine line) {
        line.refreshReferenceObject("account");
        return new RoutingSubFundGroup(line.getAccount().getSubFundGroupCode());
    }
    
    /**
     * @return the name of the rule attribute that will deal with this data 
     */
    protected String getRuleAttributeName() {
        return "KualiSubFundGroupAttribute";
    }

    /**
     * Gets the accountingLineCollectionProperties attribute. 
     * @return Returns the accountingLineCollectionProperties.
     */
    public List<String> getAccountingLineCollectionProperties() {
        return accountingLineCollectionProperties;
    }

    /**
     * Sets the accountingLineCollectionProperties attribute value.
     * @param accountingLineCollectionProperties The accountingLineCollectionProperties to set.
     */
    public void setAccountingLineCollectionProperties(List<String> accountingLineCollectionProperties) {
        this.accountingLineCollectionProperties = accountingLineCollectionProperties;
    }

}
