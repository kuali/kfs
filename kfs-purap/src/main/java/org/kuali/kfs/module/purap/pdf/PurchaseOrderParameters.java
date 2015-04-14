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
package org.kuali.kfs.module.purap.pdf;

import org.kuali.kfs.module.purap.businessobject.PurchaseOrderVendorQuote;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;

public interface PurchaseOrderParameters {
   /**
    *  
    * This method returns the purchase Order transmit parameters require to generate
    * purchase order and purchase order quote pdf. 
    * @param po
    * @param povq
    * @return
    */
   public void setPurchaseOrderPdfParameters(PurchaseOrderDocument po) ; 
   
   /**
    *  
    * This method returns the purchase Order transmit parameters require to generate
    * purchase order and purchase order quote pdf. 
    * @param po
    * @param povq
    * @return
    */
   public void setPurchaseOrderPdfParameters(PurchaseOrderDocument po , PurchaseOrderVendorQuote povq) ;
   
   /**
    * 
    * This method returns the purchase order transmit parameters require to send
    * purchase order and purchase order quote fax.
    * @param po
    * @param povq
    * @return
    */
   public void setPurchaseOrderFaxParameters(PurchaseOrderDocument po , PurchaseOrderVendorQuote povq) ;
   
   /**
    * 
    * This method returns the purchase order transmit parameters require to 
    * generate purchase order and purchase order quote pdf and to send fax.
    * @param po
    * @param povq
    * @return
    */
   public void setPurchaseOrderPdfAndFaxParameters(PurchaseOrderDocument po ) ;
   
   /**
    * 
    * This method returns the purchase order transmit parameters require to 
    * generate purchase order and purchase order quote pdf and to send fax.
    * @param po
    * @param povq
    * @return
    */
   public void setPurchaseOrderPdfAndFaxParameters(PurchaseOrderDocument po , PurchaseOrderVendorQuote povq) ;
}
