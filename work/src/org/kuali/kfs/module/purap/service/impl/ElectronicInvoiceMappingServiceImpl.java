/*
 * Copyright 2006-2009 The Kuali Foundation
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
/*
 * Created on Mar 7, 2006
 *
 */
package org.kuali.kfs.module.purap.service.impl;

import java.util.Map;

import org.kuali.kfs.module.purap.businessobject.ElectronicInvoice;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceItem;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceOrder;
import org.kuali.kfs.module.purap.dataaccess.ElectronicInvoicingDao;
import org.kuali.kfs.module.purap.service.ElectronicInvoiceMappingService;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class ElectronicInvoiceMappingServiceImpl implements ElectronicInvoiceMappingService {
  private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ElectronicInvoiceMappingServiceImpl.class);
  
  private ElectronicInvoicingDao electronicInvoicingDao;

  public void setElectronicInvoicingDao(ElectronicInvoicingDao electronicInvoicingDao) {
    this.electronicInvoicingDao = electronicInvoicingDao;
  }
  
  public Map getDefaultItemMappingMap() {
    LOG.debug("getDefaultItemMappingMap() started");
    return electronicInvoicingDao.getDefaultItemMappingMap();
  }

  public Map getItemMappingMap(Integer vendorHeaderId, Integer vendorDetailId) {
      if (LOG.isDebugEnabled()) {
          LOG.debug("getItemMappingMap() started for vendor id " + vendorHeaderId + "-" + vendorDetailId);
      }
    return electronicInvoicingDao.getItemMappingMap(vendorHeaderId,vendorDetailId);
  }
  
  public boolean acceptAmountType(String cxmlAmountType) {
    return ( (cxmlAmountType != null) && (!(cxmlAmountType.equalsIgnoreCase(ITEM_TYPE_RETURN_VALUE_UNACCEPTED))) );
  }
  
  /**
   * This method defines which field out of the ElectronicInvoiceOrder that the
   * purchase order number is coming in as
   * 
   * @param invoiceOrder  ElectronicInvoiceOrder we need to get the PO ID from
   * @return the String value of the purchase order id
   */
  public String getInvoicePurchaseOrderID(ElectronicInvoiceOrder invoiceOrder) {
    return invoiceOrder.getOrderReferenceOrderID();
  }
  
  /**
   * This method defines which field out of the ElectronicInvoiceItem that the
   * catalog number is coming in as
   * 
   * @param eii  ElectronicInvoiceItem we need to get the catalog number from
   * @return catalog number value
   */
  public String getCatalogNumber(ElectronicInvoiceItem item) {
    return item.getReferenceItemIDSupplierPartID();
  }
  
  /**
   * This method defines which field out of the ElectronicInvoice that the
   * customer number field is coming in as
   * 
   * @param ei  ElectronicInvoice we need to get the customer number from
   * @return customer number value
   */
  public String getInvoiceCustomerNumber(ElectronicInvoice ei) {
    // TODO: Future Release - Enter valid location for Customer Number from E-Invoice
    return null;
  }
  
  /**
   * This method contains the mapping check for valid Currency Code(s)
   */
  public String checkCodeForValidCurrency(String code) {
    if (!(this.isCodeValidCurrency(code))) {
      return code;
    } else {
      return null;
    }
  }

  /**
   * This method contains the mapping check for valid Currency Code(s)
   */
  public boolean isCodeValidCurrency(String code) {
    if (code != null) {
      for (int i = 0; i < CXML_VALID_CURRENCY_CODES.length; i++) {
        String validCode = CXML_VALID_CURRENCY_CODES[i];
        if (code.equalsIgnoreCase(validCode)) {
          return true;
        }
      }
    }
    return false;
  }
  
  
}
