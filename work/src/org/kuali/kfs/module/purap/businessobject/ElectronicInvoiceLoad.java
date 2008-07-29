/*
 * Created on Mar 9, 2005
 *
 */
package org.kuali.kfs.module.purap.businessobject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author delyea
 *
 */
public class ElectronicInvoiceLoad {
  private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ElectronicInvoiceLoad.class);
  
  private Map invoiceLoadSummaries;
  private Map rejectFilesToMove;
  private List electronicInvoiceRejects;
  
  /**
   * 
   */
  public ElectronicInvoiceLoad() {
    super();
    invoiceLoadSummaries = new HashMap();
    rejectFilesToMove = new HashMap();
    electronicInvoiceRejects = new ArrayList();
  }
  
  public ElectronicInvoiceLoad(Map loadSummaries, Map filesMoving, List invoiceRejects) {
    super();
    this.invoiceLoadSummaries = loadSummaries;
    this.rejectFilesToMove = filesMoving;
    this.electronicInvoiceRejects = invoiceRejects;
  }
  
  public void insertInvoiceLoadSummary(ElectronicInvoiceLoadSummary eils) {
    this.invoiceLoadSummaries.put(eils.getVendorDunsNumber(), eils);
  }
  
  public void insertRejectFileToMove(File file,String directory) {
    this.rejectFilesToMove.put(file,directory);
  }
  
  public void addInvoiceReject(ElectronicInvoiceReject eir) {
    this.electronicInvoiceRejects.add(eir);
  }
  
  /**
   * @return Returns the electronicInvoiceRejects.
   */
  public List getElectronicInvoiceRejects() {
    return electronicInvoiceRejects;
  }
  /**
   * @param electronicInvoiceRejects The electronicInvoiceRejects to set.
   */
  public void setElectronicInvoiceRejects(List electronicInvoiceRejects) {
    this.electronicInvoiceRejects = electronicInvoiceRejects;
  }
  /**
   * @return Returns the invoiceLoadSummaries.
   */
  public Map getInvoiceLoadSummaries() {
    return invoiceLoadSummaries;
  }
  /**
   * @param invoiceLoadSummaries The invoiceLoadSummaries to set.
   */
  public void setInvoiceLoadSummaries(Map invoiceLoadSummaries) {
    this.invoiceLoadSummaries = invoiceLoadSummaries;
  }
  /**
   * @return Returns the rejectFilesToMove.
   */
  public Map getRejectFilesToMove() {
    return rejectFilesToMove;
}
  /**
   * @param rejectFilesToMove The rejectFilesToMove to set.
   */
  public void setRejectFilesToMove(Map rejectFilesToMove) {
    this.rejectFilesToMove = rejectFilesToMove;
  }
}
/*
Copyright (c) 2004, 2005 The National Association of College and
University Business Officers, Cornell University, Trustees of Indiana
University, Michigan State University Board of Trustees, Trustees of San
Joaquin Delta College, University of Hawai'i, The Arizona Board of
Regents on behalf of the University of Arizona, and the r*smart group.

Licensed under the Educational Community License Version 1.0 (the 
"License"); By obtaining, using and/or copying this Original Work, you
agree that you have read, understand, and will comply with the terms and
conditions of the Educational Community License.

You may obtain a copy of the License at:

http://kualiproject.org/license.html

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
TORT OR OTHERWISE, ARISING
FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
DEALINGS IN THE SOFTWARE. 
*/
