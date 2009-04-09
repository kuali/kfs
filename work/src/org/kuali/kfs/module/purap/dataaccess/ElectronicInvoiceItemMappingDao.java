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
     * Save a ElectronicInvoiceItemMapping.
     * 
     * @param row ElectronicInvoiceItemMapping to save
     */
    public void save(ElectronicInvoiceItemMapping row);

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
