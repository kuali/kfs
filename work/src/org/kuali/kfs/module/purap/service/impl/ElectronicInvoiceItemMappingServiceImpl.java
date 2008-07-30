package org.kuali.kfs.module.purap.service.impl;

import java.util.List;

import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceItemMapping;
import org.kuali.kfs.module.purap.businessobject.ItemType;
import org.kuali.kfs.module.purap.dataaccess.ElectronicInvoiceItemMappingDao;
import org.kuali.kfs.module.purap.service.ElectronicInvoiceItemMappingService;

public class ElectronicInvoiceItemMappingServiceImpl implements ElectronicInvoiceItemMappingService {
  private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ElectronicInvoiceItemMappingServiceImpl.class);
  private static org.apache.log4j.Logger SERVICELOG = org.apache.log4j.Logger.getLogger("epic.service." + ElectronicInvoiceItemMappingServiceImpl.class.getName());

  private ElectronicInvoiceItemMappingDao electronicInvoiceItemMappingDao;
  
  public void setElectronicInvoiceItemMappingDao(ElectronicInvoiceItemMappingDao d) {
    this.electronicInvoiceItemMappingDao = d;
  }
  
  public List getAll() {
    SERVICELOG.debug("getAll() started");
    SERVICELOG.debug("getAll() ended");
    return electronicInvoiceItemMappingDao.getAll();
  }

  public List getAllEpicItemTypes() {
    SERVICELOG.debug("getAllEpicItemTypes() started");
    SERVICELOG.debug("getAllEpicItemTypes() ended");
    return electronicInvoiceItemMappingDao.getAllEpicItemTypes();
  }
  
  public ElectronicInvoiceItemMapping getById(String id) {
    SERVICELOG.debug("getById() started");
    SERVICELOG.debug("getById() ended");
    return electronicInvoiceItemMappingDao.getById(id);
  }

  public ItemType getItemTypeByCode(String code) {
    SERVICELOG.debug("getItemTypeByCode() started");
    SERVICELOG.debug("getItemTypeByCode() ended");
    return electronicInvoiceItemMappingDao.getItemTypeByCode(code);
  }
  
  public List save(ElectronicInvoiceItemMapping ei) {
    SERVICELOG.debug("save() started");
    //Before saving, if the id is empty, we are supposed to check whether the item mapping has existed in the database. 
    //If so, we should display an error to the user, if not, then continue with the saving.
    ElectronicInvoiceItemMapping existing = electronicInvoiceItemMappingDao.getByUniqueKeys(ei.getVendorHeaderGeneratedIdentifier(), ei.getVendorDetailAssignedIdentifier(), ei.getInvoiceItemTypeCode());
    if ((existing != null && ei.getElectronicInvoiceMapIdentifier() == null) ||	(ei.getElectronicInvoiceMapIdentifier() != null && !existing.getElectronicInvoiceMapIdentifier().equals(ei.getElectronicInvoiceMapIdentifier()))) {
        /* FIXME need to record the errors as reject reasons and put those in route log somehow
          se.setTab("error");
          se.setMessageKey("errors.einvoice.item.mapping.duplicate.rows");
         */
    } else {
      electronicInvoiceItemMappingDao.save(ei);
    }
    SERVICELOG.debug("save() ended");
    return getAll();  
  }

  public List delete(String id) {
    SERVICELOG.debug("delete() started");
    ElectronicInvoiceItemMapping ei = getById(id);
    //If both the vendor ids are null, then we set service error with appropriate tab 
    //and message key, otherwise, do the delete.
    if (ei.getVendorDetailAssignedIdentifier() == null && ei.getVendorHeaderGeneratedIdentifier() == null) {
        /* FIXME need to record the errors as reject reasons and put those in route log somehow
          se.setTab("error");
          se.setMessageKey("errors.einvoice.item.mapping.null.vendor.id.deletion");
         */
    } else {
      electronicInvoiceItemMappingDao.delete(ei);
    }
  	SERVICELOG.debug("delete() ended");
    return getAll();
  }

}
