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
 * Created on Mar 2, 2006
 *
 */
package org.kuali.kfs.module.purap.dataaccess;

import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceLoadSummary;
import org.kuali.kfs.module.purap.document.ElectronicInvoiceRejectDocument;

public interface ElectronicInvoicingDao {
  public ElectronicInvoiceLoadSummary getElectronicInvoiceLoadSummary(Integer loadId,String vendorDunsNumber);
  public List getPendingElectronicInvoices();
  public Map getDefaultItemMappingMap();
  public Map getItemMappingMap(Integer vendorHeaderId, Integer vendorDetailId);
}
