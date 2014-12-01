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
package org.kuali.kfs.module.purap.dataaccess;

import java.util.List;

import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceItemMapping;
import org.kuali.kfs.module.purap.businessobject.ItemType;

/**
 * @author This dao and its implementation is used for data retrieval/insertion/deletion by the
 *         ElectronicInvoiceItemMappingService which is used by the maintenance page for Electronic Invoice Item Mapping.
 */
public interface ElectronicInvoiceItemMappingDao {

    /**
     * Get list of all ElectronicInvoiceItemMappings
     */
    public List getAll();

    /**
     * Get an ElectronicInvoiceItemMapping by primary key.
     * 
     * @param id the id to lookup
     */
    public ElectronicInvoiceItemMapping getById(String id);

    /**
     * Get an ElectronicInvoiceItemMapping based on the 3 unique keys. This method is used to ensure that the user is not inserting
     * a row that contains the same 3 keys that have already existed in the database
     * 
     * @param headerId the vendorHeaderGeneratedId
     * @param detailId the vendorDetailAssignedId
     * @param invoiceTypeCode the electronicInvoiceTypeCode
     * @return
     */
    public ElectronicInvoiceItemMapping getByUniqueKeys(Integer headerId, Integer detailId, String invoiceTypeCode);

    /**
     * Delete a ElectronicInvoiceItemMapping.
     * 
     * @param row
     */
    public void delete(ElectronicInvoiceItemMapping row);

    /**
     * This method returns a list of all Item Types from the PUR_AP_ITM_TYP_T table
     * 
     * @return
     */
    public List getAllItemTypes();

    public ItemType getItemTypeByCode(String code);
}
