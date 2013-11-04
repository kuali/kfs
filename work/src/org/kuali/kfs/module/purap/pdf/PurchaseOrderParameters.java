/*
 * Copyright 2010 The Kuali Foundation.
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
