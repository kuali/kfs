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
package org.kuali.kfs.module.purap.service;

import java.util.Map;

import org.kuali.kfs.module.purap.businessobject.ElectronicInvoice;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceItem;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceOrder;

public interface ElectronicInvoiceMappingService {
  
  // default values
  public static String DEFAULT_BELOW_LINE_ITEM_DESCRIPTION = "Electronically entered amount";
  // ELECTRONIC INVOICE SHIPPING DESCRIPTION
  public static String E_INVOICE_SHIPPING_DESCRIPTION = "Shipping";

  
  // role id of the address we use as the Ship To address
  public static String CXML_ADDRESS_SHIP_TO_ROLE_ID = "shipTo";
  public static String CXML_ADDRESS_BILL_TO_ROLE_ID = "billTo";
  public static String CXML_ADDRESS_REMIT_TO_ROLE_ID = "remitTo";
  
  // name of our default address name we use (null for first available)
  public static String CXML_ADDRESS_SHIP_TO_NAME = null;
  public static String CXML_ADDRESS_BILL_TO_NAME = null;
  public static String CXML_ADDRESS_REMIT_TO_NAME = null;

  /*
   * These mappings link E-Invoice CXML item types to PREQ Item Types
   * 
   * If the PREQ ITEM TYPE is null then we do not accept or care
   * about these items
   */
  public static String ITEM_TYPE_RETURN_VALUE_UNACCEPTED = "";
  
  // ELECTRONIC INVOICE CXML US DOLLAR CODE
  public static String[] CXML_VALID_CURRENCY_CODES = {"USD"};

  // ELECTRONIC INVOICE CXML OPERATION CODE
  public static String[] CXML_VALID_OPERATION_CODES = {"new"};

  // ELECTRONIC INVOICE CXML PURPOSE CODE
  public static String[] CXML_VALID_PURPOSE_CODES = {"standard"};

  public Map getDefaultItemMappingMap();

  public Map getItemMappingMap(Integer vendorHeaderId, Integer vendorDetailId);

  public boolean acceptAmountType(String cxmlAmountType) ;
  
  /**
   * This method defines which field out of the ElectronicInvoiceOrder that the
   * purchase order number is coming in as
   * 
   * @param invoiceOrder  ElectronicInvoiceOrder we need to get the PO ID from
   * @return the String value of the purchase order id
   */
  public String getInvoicePurchaseOrderID(ElectronicInvoiceOrder invoiceOrder);
  
  /**
   * This method defines which field out of the ElectronicInvoiceItem that the
   * catalog number is coming in as
   * 
   * @param eii  ElectronicInvoiceItem we need to get the catalog number from
   * @return catalog number value
   */
  public String getCatalogNumber(ElectronicInvoiceItem item);
  
  /**
   * This method defines which field out of the ElectronicInvoice that the
   * customer number field is coming in as
   * 
   * @param ei  ElectronicInvoice we need to get the customer number from
   * @return customer number value
   */
  public String getInvoiceCustomerNumber(ElectronicInvoice ei);
  
  /**
   * This method contains the mapping check for valid Currency Code(s)
   */
  public String checkCodeForValidCurrency(String code);
  
  /**
   * This method contains the mapping check for valid Currency Code(s)
   */
  public boolean isCodeValidCurrency(String code);
}
