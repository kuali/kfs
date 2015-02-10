/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2014 The Kuali Foundation
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.ar.service.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.kuali.kfs.integration.ar.AccountsReceivableModuleBillingService;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.service.CustomerViewService;
import org.kuali.kfs.sys.service.NonTransactional;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.Row;
import org.kuali.rice.kns.web.ui.Section;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Implementation of the Customer View Service.
 */
@NonTransactional
public class CustomerViewServiceImpl implements CustomerViewService {

    protected AccountsReceivableModuleBillingService accountsReceivableModuleBillingService;

    /**
     * @see org.kuali.kfs.module.ar.service.CustomerViewService#getSections(java.util.List)
     */
    @Override
    public List getSections(List<Section> sections) {
        if (!getAccountsReceivableModuleBillingService().isContractsGrantsBillingEnhancementActive()) {
            for(Iterator<Section> it = sections.iterator(); it.hasNext(); ) {
                Section section = it.next();
                if (getSectionsToIgnore().contains(section.getSectionId())) {
                    it.remove();
                } else {
                    for (Row row : section.getRows()) {
                        row.setFields(getFieldsToDisplay(row));
                    }
                }
            }
        }

        return sections;
    }

    /**
     * Go through the fields in the row, filtering out the ones we want to ignore
     * and returning a new list of fields to display.
     *
     * @param row contains fields to process
     * @return List of fields to display
     */
    protected List<Field> getFieldsToDisplay(Row row) {
        List<Field> fieldsToDisplay = new ArrayList<Field>();

        for (Field field : row.getFields()) {
            if (field.getCONTAINER().equalsIgnoreCase(field.getFieldType())) {
                List<Row> containerRowsToDisplay = getContainerRowsToDisplay(field);
                if (CollectionUtils.isNotEmpty(containerRowsToDisplay)) {
                    field.setContainerRows(containerRowsToDisplay);
                    fieldsToDisplay.add(field);
                }
            } else if (!getFieldsToIgnore().contains(field.getPropertyName())) {
                fieldsToDisplay.add(field);
            }
        }

        return fieldsToDisplay;
    }

    /**
     * For a Container Field, go through the container rows and filter out any fields
     * we don't want to display.
     *
     * @param field container field to process
     * @return List of container rows to display
     */
    protected List<Row> getContainerRowsToDisplay(Field field) {
        List<Row> containerRowsToDisplay = new ArrayList<Row>();

        for (Row containerRow : field.getContainerRows()) {
            List<Field> updatedContainerRowFields = new ArrayList<Field>();
            for (Field containerRowfield : containerRow.getFields()) {
                if (!getFieldsToIgnore().contains(ObjectUtils.getNestedAttributePrimitive(containerRowfield.getPropertyName()))) {
                    updatedContainerRowFields.add(containerRowfield);
                }
            }
            if (CollectionUtils.isNotEmpty(updatedContainerRowFields)) {
                containerRow.setFields(updatedContainerRowFields);
                containerRowsToDisplay.add(containerRow);
            }
        }

        return containerRowsToDisplay;
    }

    /**
     * Return list of section ids to ignore if the Contracts & Grants Billing (CGB) enhancement is disabled.
     *
     * @return list of sections to ignore
     */
    protected List<String> getSectionsToIgnore() {
        List<String> sectionsToIgnore = new ArrayList<String>();

        sectionsToIgnore.add(ArPropertyConstants.SectionId.CUSTOMER_COLLECTIONS_SECTION_ID);

        return sectionsToIgnore;
    }

    /**
     * Return list of fields to ignore if the Contracts & Grants Billing (CGB) enhancement is disabled.
     *
     * @return list of fields to ignore
     */
    protected List<String> getFieldsToIgnore() {
        List<String> fieldsToIgnore = new ArrayList<String>();

        fieldsToIgnore.add(ArPropertyConstants.CustomerFields.CUSTOMER_INVOICE_TEMPLATE_CODE);
        fieldsToIgnore.add(ArPropertyConstants.INVOICE_TRANSMISSION_METHOD_CODE);
        fieldsToIgnore.add(ArPropertyConstants.CustomerFields.CUSTOMER_COPIES_TO_PRINT);
        fieldsToIgnore.add(ArPropertyConstants.CustomerFields.CUSTOMER_ENVELOPES_TO_PRINT_QUANTITY);

        return fieldsToIgnore;
    }

    public AccountsReceivableModuleBillingService getAccountsReceivableModuleBillingService() {
        return accountsReceivableModuleBillingService;
    }

    public void setAccountsReceivableModuleBillingService(AccountsReceivableModuleBillingService accountsReceivableModuleBillingService) {
        this.accountsReceivableModuleBillingService = accountsReceivableModuleBillingService;
    }

}
