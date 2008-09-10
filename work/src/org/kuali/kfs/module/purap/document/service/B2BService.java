/*
 * Copyright 2006-2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.purap.document.service;

import java.util.Collection;
import java.util.List;

import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.util.cxml.B2BShoppingCartParser;
import org.kuali.rice.kns.bo.user.UniversalUser;

/**
 *
 * These items will allow a user to punch out to SciQuest and
 * will create requisitions from an order.  There is an additional
 * service that sends a PO to SciQuest.  That is in a different service
 * because it is called in a postprocessor in workflow.
 */
public interface B2BService {
  /**
   * Get URL to punch out to
   * 
   * @param User ID punching out
   * @return URL to punch out to
   */
  public String getPunchOutUrl(UniversalUser user);

  /**
   * Create requisition(s) from cxml and return
   * list for display
   * 
   * @param cxml cXml string from sciquest
   * @param user User doing the requisitioning
   * @return List of requisitions
   */
  public List createRequisitionsFromCxml(B2BShoppingCartParser message, UniversalUser user);

  /**
   * Send the PO to sciquest
   * 
   * @param po
   * @return Response from sciquest
   */
  public Collection sendPurchaseOrder(PurchaseOrderDocument po, UniversalUser user);
}