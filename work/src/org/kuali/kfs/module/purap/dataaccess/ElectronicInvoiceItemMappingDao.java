package org.kuali.kfs.module.purap.dataaccess;

import java.util.List;

import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceItemMapping;
import org.kuali.kfs.module.purap.businessobject.ItemType;

/**
 * 
 * @author tanc
 *
 * This dao and its implementation is used for data
 * retrieval/insertion/deletion by the ElectronicInvoiceItemMappingService
 * which is used by the maintenance page for Electronic Invoice Item
 * Mapping.
 */
public interface ElectronicInvoiceItemMappingDao {

  /**
   * Save a ElectronicInvoiceItemMapping.
   * 
   * @param row   ElectronicInvoiceItemMapping to save
   */
  public void save(ElectronicInvoiceItemMapping row);

  /**
   * Get list of all ElectronicInvoiceItemMappings
   */
  public List getAll();

  /**
   * Get an ElectronicInvoiceItemMapping by primary key.
   * 
   * @param id    the id to lookup
   */
  public ElectronicInvoiceItemMapping getById(String id);
  
/**
 *  Get an ElectronicInvoiceItemMapping based on the
 *  3 unique keys. This method is used to ensure that
 *  the user is not inserting a row that contains the
 *  same 3 keys that have already existed in the database
 * 
 * @param headerId         the vendorHeaderGeneratedId
 * @param detailId         the vendorDetailAssignedId
 * @param invoiceTypeCode  the electronicInvoiceTypeCode
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
   *  This method returns a list of all Epic Item Types
   *  from the PUR_AP_ITM_TYP_T table
   * @return
   */
  public List getAllEpicItemTypes();
  
  public ItemType getItemTypeByCode(String code);
}
/*
 * Copyright (c) 2004, 2005 The National Association of College and University
 * Business Officers, Cornell University, Trustees of Indiana University,
 * Michigan State University Board of Trustees, Trustees of San Joaquin Delta
 * College, University of Hawai'i, The Arizona Board of Regents on behalf of the
 * University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License");
 * By obtaining, using and/or copying this Original Work, you agree that you
 * have read, understand, and will comply with the terms and conditions of the
 * Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */