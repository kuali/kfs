package org.kuali.kfs.module.purap.service;

import java.util.List;

import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceItemMapping;
import org.kuali.kfs.module.purap.businessobject.ItemType;

/**
 * This service and its implementation is used by the maintenance page for Electronic Invoice Item Mapping.
 */
public interface ElectronicInvoiceItemMappingService {

    public List getAll();

    public ElectronicInvoiceItemMapping getById(String id);

    public List save(ElectronicInvoiceItemMapping ei);

    public List delete(String id);

    public List getAllItemTypes();

    public ItemType getItemTypeByCode(String code);

}
