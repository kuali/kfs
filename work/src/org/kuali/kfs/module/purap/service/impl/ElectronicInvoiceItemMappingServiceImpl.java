package org.kuali.kfs.module.purap.service.impl;

import java.util.List;

import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceItemMapping;
import org.kuali.kfs.module.purap.businessobject.ItemType;
import org.kuali.kfs.module.purap.dataaccess.ElectronicInvoiceItemMappingDao;
import org.kuali.kfs.module.purap.service.ElectronicInvoiceItemMappingService;

public class ElectronicInvoiceItemMappingServiceImpl implements ElectronicInvoiceItemMappingService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ElectronicInvoiceItemMappingServiceImpl.class);

    private ElectronicInvoiceItemMappingDao electronicInvoiceItemMappingDao;

    public void setElectronicInvoiceItemMappingDao(ElectronicInvoiceItemMappingDao d) {
        this.electronicInvoiceItemMappingDao = d;
    }

    public List getAll() {
        return electronicInvoiceItemMappingDao.getAll();
    }

    public List getAllItemTypes() {
        return electronicInvoiceItemMappingDao.getAllItemTypes();
    }

    public ElectronicInvoiceItemMapping getById(String id) {
        return electronicInvoiceItemMappingDao.getById(id);
    }

    public ItemType getItemTypeByCode(String code) {
        return electronicInvoiceItemMappingDao.getItemTypeByCode(code);
    }

    public List save(ElectronicInvoiceItemMapping ei) {
        // Before saving, if the id is empty, we are supposed to check whether the item mapping has existed in the database.
        // If so, we should display an error to the user, if not, then continue with the saving.
        ElectronicInvoiceItemMapping existing = electronicInvoiceItemMappingDao.getByUniqueKeys(ei.getVendorHeaderGeneratedIdentifier(), ei.getVendorDetailAssignedIdentifier(), ei.getInvoiceItemTypeCode());
        if ((existing != null && ei.getInvoiceMapIdentifier() == null) || (ei.getInvoiceMapIdentifier() != null && !existing.getInvoiceMapIdentifier().equals(ei.getInvoiceMapIdentifier()))) {
            /*
             * FIXME need to record the errors as reject reasons and put those in route log somehow se.setTab("error");
             * se.setMessageKey("errors.einvoice.item.mapping.duplicate.rows");
             */
        }
        else {
            electronicInvoiceItemMappingDao.save(ei);
        }
        return getAll();
    }

    public List delete(String id) {
        ElectronicInvoiceItemMapping ei = getById(id);
        // If both the vendor ids are null, then we set service error with appropriate tab
        // and message key, otherwise, do the delete.
        if (ei.getVendorDetailAssignedIdentifier() == null && ei.getVendorHeaderGeneratedIdentifier() == null) {
            /*
             * FIXME need to record the errors as reject reasons and put those in route log somehow se.setTab("error");
             * se.setMessageKey("errors.einvoice.item.mapping.null.vendor.id.deletion");
             */
        }
        else {
            electronicInvoiceItemMappingDao.delete(ei);
        }
        return getAll();
    }

}
