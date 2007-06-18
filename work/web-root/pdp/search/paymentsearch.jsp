<%--
 Copyright 2007 The Kuali Foundation.
 
 Licensed under the Educational Community License, Version 1.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
 http://www.opensource.org/licenses/ecl1.php
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
--%>
<%@ include file="/jsp/kfs/kfsTldHeader.jsp"%>
<%@ taglib uri="/WEB-INF/app.tld" prefix="app" %>
<app:getReference name="DisbursementType" />
<app:getReference name="PaymentStatus" />
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html:html locale="true">
<link rel="stylesheet" type="text/css"  href="https://docs.onestart.iu.edu/dav/MY/channels/css/styles.css"/>
<head>
  <html:base />
  <title>Payment Search</title>
  <c:if test="${param.results != null}">
    <script>
      location.hash = "results";
    </script>
  </c:if>
</head>
<body>
  <html:form action="/pdp/paymentsearch">
    <c:if test="${not empty PaymentDetailSearchFormSession}">
      <bean:define id="PaymentDetailSearchForm" name="PaymentDetailSearchFormSession" />
    </c:if>
    <h1><strong>Search for a Payment</strong></h1><br>
    <jsp:include page="${request.contextPath}/pdp/TestEnvironmentWarning.jsp" flush="true"/>
    <table width="100%" border="0" cellspacing="0" cellpadding="0">
      <tr>
        <td width="20">&nbsp;</td>
        <td>
          <br>
          <font color="#800000"><html:errors/>&nbsp;</font>&nbsp;
          <br>
        </td>
      </tr>
    </table>
    <table width="100%" border="0" cellspacing="0" cellpadding="0">
      <tr>
        <td width="20">&nbsp;</td>
        <td>
          <table width="100%" border="0" cellspacing="0" cellpadding="4">
            <tr>
              <td>
                <strong>Enter your search criteria:</strong>
              </td>
              <td></td>
            </tr>
          </table>
        </td>
        <td width="20">&nbsp;</td>
      </tr>
      <tr>
        <td width="20">&nbsp;</td>
        <td>&nbsp;</td>
        <td width="20">&nbsp;</td>
      </tr>
      <tr>
        <td>&nbsp;</td>
        <td>
          <table width="100%" border=0 cellpadding=0 cellspacing=0 class="bord-r-t">
            <tbody>
              <tr>
                <th align=right nowrap >Payee Name:</th>
                <td align=left class="datacell">
                  <html:text name="PaymentDetailSearchForm" property="payeeName" tabindex="2" maxlength="40" />
                </td>
                <th align=right nowrap >Chart:</th>
                <td align=left class="datacell">
                  <html:text name="PaymentDetailSearchForm" property="chartCode" tabindex="10" maxlength="2" />
                </td>
                <th align=right nowrap >Source Document Number:</th>
                <td align=left class="datacell">
                  <html:text name="PaymentDetailSearchForm" property="custPaymentDocNbr" tabindex="18" maxlength="9" />
                </td>
              </tr>
              <tr>
                <th align=right nowrap >Payee ID Type:</th>
                <td align=left class="datacell">
                  <html:select size="1" name="PaymentDetailSearchForm" property="payeeIdTypeCd" tabindex="3">
                    <html:option value="">&nbsp;</html:option>
                    <html:option value="E">Employee ID</html:option>
                    <html:option value="F">FEIN</html:option>
                    <html:option value="S">SSN</html:option>
                    <html:option value="V">Vendor ID</html:option>
                    <html:option value="P">Payee ID</html:option>
                    <html:option value="X">Other</html:option>
                  </html:select>
                </td>
                <th align=right nowrap >Organization:</th>
                <td align=left class="datacell">
                  <html:text name="PaymentDetailSearchForm" property="orgCode" tabindex="11" maxlength="4" />
                </td>
                <th align=right nowrap >Purchase Order Number:</th>
                <td align=left class="datacell">
                  <html:text name="PaymentDetailSearchForm" property="purchaseOrderNbr" tabindex="19" maxlength="9" />
                </td>
              </tr>
              <tr>
                <th align=right nowrap >Payee ID:</th>
                <td align=left class="datacell">
                  <html:text name="PaymentDetailSearchForm" property="payeeId" tabindex="4" maxlength="25" />
                </td>
                <th align=right nowrap >Sub-Unit:</th>
                <td align=left class="datacell">
                  <html:text name="PaymentDetailSearchForm" property="subUnitCode" tabindex="12" maxlength="4" />
                </td>
                <th align=right nowrap >Invoice Number:</th>
                <td align=left class="datacell">
                  <html:text name="PaymentDetailSearchForm" property="invoiceNbr" tabindex="20" maxlength="25" />
                </td>
              </tr>
              <tr>
                <th align=right nowrap >Disbursement Type:</th>
                <td align=left class="datacell"> 
                  <html:select size="1" name="PaymentDetailSearchForm" property="disbursementTypeCode" tabindex="5">
                    <html:option value="">&nbsp;</html:option>
                    <html:options collection="DisbursementTypeList" name="PaymentDetailSearchForm" property="code" labelProperty="description" />
                  </html:select>
                </td>
                <th align=right nowrap >Immediate:</th>
                <td align=left class="datacell">
                  <html:select size="1" name="PaymentDetailSearchForm" property="processImmediate" tabindex="13">
                    <html:option value="">&nbsp;</html:option>
                    <html:option value="N">No</html:option>
                    <html:option value="Y">Yes</html:option>
                  </html:select>
                </td>
                <th align=right nowrap >Requisition Number:</th>
                <td align=left class="datacell">
                  <html:text name="PaymentDetailSearchForm" property="requisitionNbr" tabindex="21" maxlength="8" />
                </td>
              </tr>
              <tr>
                <th align=right nowrap >Disbursement Number:</th>
                <td align=left class="datacell">
                  <html:text name="PaymentDetailSearchForm" property="disbursementNbr" tabindex="6" maxlength="9" />
                  <html:hidden name="PaymentDetailSearchForm" property="oldDisbursementNbr"/>
                </td>
                <th align=right nowrap >Special Handling:</th>
                <td align=left class="datacell">
                  <html:select size="1" name="PaymentDetailSearchForm" property="pymtSpecialHandling" tabindex="14">
                    <html:option value="">&nbsp;</html:option>
                    <html:option value="N">No</html:option>
                    <html:option value="Y">Yes</html:option>
                  </html:select>
                </td>
                <th align=right nowrap >Customer Number for IU:</th>
                <td align=left class="datacell">
                  <html:text name="PaymentDetailSearchForm" property="iuIdForCustomer" tabindex="22" maxlength="30" />
                </td>
              </tr>
              <tr>
                <th rowspan="2" align=right nowrap >Disbursement Date Range:<br><font size="1"> Ex. 11/26/2004  </font></th>
                <td rowspan="2" align=left class="datacell">
                  <html:text name="PaymentDetailSearchForm" property="beginDisbursementDate" tabindex="7" maxlength="10" />
                  <br>to<br>
                  <html:text name="PaymentDetailSearchForm" property="endDisbursementDate" tabindex="8" maxlength="10" />
                </td>
                <th align=right nowrap >Attachment:</th>
                <td align=left class="datacell">
                  <html:select size="1" name="PaymentDetailSearchForm" property="pymtAttachment" tabindex="13">
                    <html:option value="">&nbsp;</html:option>
                    <html:option value="N">No</html:option>
                    <html:option value="Y">Yes</html:option>
                  </html:select>
                </td>
                <th align=right nowrap >Process ID:</th>
                <td align=left class="datacell">
                  <html:text name="PaymentDetailSearchForm" property="processId" tabindex="23" maxlength="8" />
                </td>
              </tr>
              <tr>
                <th rowspan="2" align=right nowrap >Pay Date Range:<br><font size="1"> Ex. 01/06/2004  </font></th>
                <td rowspan="2" align=left class="datacell">
                  <html:text name="PaymentDetailSearchForm" property="beginPaymentDate" tabindex="15" maxlength="10" />
                  <br>to<br>
                  <html:text name="PaymentDetailSearchForm" property="endPaymentDate" tabindex="16" maxlength="10" />
                </td>
                <th align=right nowrap >Payment Detail ID:</th>
                <td align=left class="datacell">
                  <html:text name="PaymentDetailSearchForm" property="paymentId" tabindex="24" maxlength="8" />
                </td>
              </tr>
              <tr>
                <th align=right nowrap >Net Payment Amount:</th>
                <td align=left class="datacell">
                  <html:text name="PaymentDetailSearchForm" property="netPaymentAmount" tabindex="9" maxlength="14" />
                </td>
                <th align=right nowrap >Payment Status:</th>
                <td align=left class="datacell">
                  <html:select size="1" name="PaymentDetailSearchForm" property="paymentStatusCode" tabindex="17">
                    <html:option value="">&nbsp;</html:option>
                    <html:options collection="PaymentStatusList" name="PaymentDetailSearchForm" property="code" labelProperty="description" />
                  </html:select>
                </td>
              </tr>
              <tr valign=middle align=left>
                <th nowrap=nowrap colspan="6">
                  <div align="center">
                    <input type="image" name="btnSearch" src="<%= request.getContextPath() + "/pdp/images/button_search.gif" %>" alt="Search" align="absmiddle"></a>
                    <input type="image" name="btnClear" src="<%= request.getContextPath() + "/pdp/images/button_clearfields.gif" %>" alt="Clear" align="absmiddle"></a>
        <!-- ADVANCED BUTTON
          <input type="image" name="btnAdvanced" src="<%= request.getContextPath() + "/images/button_advanced.gif" %>" alt="advanced" align="absmiddle"></a>
        -->
                  </div>
                </th>
              </tr>
            </tbody>
          </table>
        </td>
        <td>&nbsp;</td>
      </tr>
    </table>
  </html:form>
  <br>
  <a name="results"></a>
  <logic:notEmpty name="indivSearchResults" >
    <table width="100%" border="0" cellspacing="0" cellpadding="0">
      <tr>
        <td width="20">&nbsp;</td>
        <td>
          <display-el:table sort="list" name="sessionScope.indivSearchResults" id="item" pagesize="${perPage}" cellpadding="4" cellspacing="0" class="bord-r-t">
            <display-el:column sortable="true" title="Customer" headerClass="thfont" class="datacell">
              <font size="0px"><c:out value="${item.paymentGroup.batch.customerProfile.chartCode}" />-<c:out value="${item.paymentGroup.batch.customerProfile.orgCode}" />-<c:out value="${item.paymentGroup.batch.customerProfile.subUnitCode}" /></font>&nbsp;
            </display-el:column>
            <display-el:column sortable="true" sortProperty="custPaymentDocNbr" title="Source Document Number" headerClass="thfont" class="datacell">
              <font size="0px">
                <c:if test="${(PaymentDetailSearchForm.oldDisbursementNbr != item.paymentGroup.disbursementNbr) && (not empty PaymentDetailSearchForm.oldDisbursementNbr)}">
                  <img src="<%= request.getContextPath() + "/pdp/images/bullet-red.gif" %>" alt=">>">
                </c:if>
                <a href="<%= request.getContextPath().toString() %>/pdp/paymentdetail.do?DetailId=<c:out value="${item.id}"/>"><c:out value="${item.custPaymentDocNbr}" /></a>
              </font>&nbsp;
            </display-el:column>
            <display-el:column sortable="true" sortProperty="purchaseOrderNbr" title="PO Number" headerClass="thfont" class="datacell">
              <font size="0px"><c:out value="${item.purchaseOrderNbr}" /></font>&nbsp;
            </display-el:column>
            <display-el:column sortable="true" sortProperty="invoiceNbr" title="Invoice Number" headerClass="thfont" class="datacell">
              <font size="0px"><c:out value="${item.invoiceNbr}"/>&nbsp;</font>
            </display-el:column>
            <display-el:column sortable="true" sortProperty="paymentGroup.payeeName" title="Payee Name" headerClass="thfont" class="datacell">
              <font size="0px"><c:out value="${item.paymentGroup.payeeName}" /></font>&nbsp;
            </display-el:column>
            <display-el:column sortable="true" sortProperty="paymentGroup.paymentDate" title="Pay Date" headerClass="thfont" class="datacell">
              <font size="0px"><fmt:formatDate value="${item.paymentGroup.paymentDate}" pattern="MM/dd/yyyy" /></font>&nbsp;
            </display-el:column>
            <display-el:column sortable="true" sortProperty="paymentGroup.disbursementDate" title="Disb. Date" headerClass="thfont" class="datacell">
              <font size="0px"><fmt:formatDate value="${item.paymentGroup.disbursementDate}" pattern="MM/dd/yyyy" /></font>&nbsp;
            </display-el:column>
            <display-el:column sortable="true" sortProperty="paymentGroup.paymentStatus.code" title="Status" headerClass="thfont" class="datacell">
              <font size="0px"><c:out value="${item.paymentGroup.paymentStatus.code}" /></font>&nbsp;
            </display-el:column>
            <display-el:column sortable="true" sortProperty="paymentGroup.disbursementType.description" title="Disb. Type" headerClass="thfont" class="datacell">
              <c:if test="${item.paymentGroup.disbursementType!=null}" >
                <font size="0px"><c:out value="${item.paymentGroup.disbursementType.description}" /></font>
              </c:if>&nbsp;
            </display-el:column>
            <display-el:column sortable="true" sortProperty="paymentGroup.disbursementNbr" title="Disb. Number" headerClass="thfont" class="datacell">
              <font size="0px"><c:out value="${item.paymentGroup.disbursementNbr}" /></font>&nbsp;
            </display-el:column>
            <display-el:column sortable="true" sortProperty="netPaymentAmount" title="Net Payment Amount" headerClass="thfont" class="datacell">
              <font size="0px"><fmt:formatNumber value="${item.netPaymentAmount}" type="currency" /></font>&nbsp;
            </display-el:column>
            <display-el:setProperty name="paging.banner.placement">both</display-el:setProperty>
            <display-el:setProperty name="paging.banner.group_size">15</display-el:setProperty>
            <display-el:setProperty name="paging.banner.page.separator">&nbsp;::&nbsp;</display-el:setProperty>
            <display-el:setProperty name="paging.banner.one_item_found">
                   </td>
                   <td width="20">&nbsp;</td>
                 </tr>
               </table>
               <br><br>
               <h1><div align="left">Search Results: 1 found.</div></h1>
               <br><br>
               <table width="100%" border="0" cellspacing="0" cellpadding="0">
                 <tr>
                   <td width="20">&nbsp;</td>
                   <td>
            </display-el:setProperty>
            <display-el:setProperty name="paging.banner.all_items_found">
                   </td>
                   <td width="20">&nbsp;</td>
                 </tr>
               </table>
               <br><br>
               <h1><div align="left">Search Results: {0} found.</div></h1>
               <br><br>
               <table width="100%" border="0" cellspacing="0" cellpadding="0">
                 <tr>
                   <td width="20">&nbsp;</td>
                   <td>
            </display-el:setProperty>
            <display-el:setProperty name="paging.banner.some_items_found">
                   </td>
                   <td width="20">&nbsp;</td>
                 </tr>
               </table>
               <br><br>
               <h1><div align="left">Search Results: {0} found.  Displaying {2} to {3}.</div>
            </display-el:setProperty>
            <display-el:setProperty name="paging.banner.full">
                   <div align="right">[<a href="{1}">First</a>/ <a href="{2}">Prev</a>] {0} [ <a href="{3}">Next</a>/ <a href="{4}">Last </a>]</div>
                 </h1>
                 <br><br>
                 <table width="100%" border="0" cellspacing="0" cellpadding="0">
                   <tr>
                     <td width="20">&nbsp;</td>
                     <td>
            </display-el:setProperty>
            <display-el:setProperty name="paging.banner.first">
                   <div align="right">[First/Prev] {0} [ <a href="{3}">Next</a>/ <a href="{4}">Last</a>]</div>
                 </h1>
                 <br><br>
                 <table width="100%" border="0" cellspacing="0" cellpadding="0">
                   <tr>
                     <td width="20">&nbsp;</td>
                     <td>
            </display-el:setProperty>
            <display-el:setProperty name="paging.banner.last">
                   <div align="right">[ <a href="{1}">First</a>/ <a href="{2}">Prev</a>] {0} [Next/Last]</div>
                 </h1>
                 <br><br>
                 <table width="100%" border="0" cellspacing="0" cellpadding="0">
                   <tr>
                     <td width="20">&nbsp;</td>
                     <td>
            </display-el:setProperty>
            <display-el:setProperty name="paging.banner.onepage" value=""/>
          </display-el:table>
        </td>
        <td width="20">&nbsp;</td>
      </tr>
    </table>
    <c:if test="${not empty PaymentDetailSearchForm.oldDisbursementNbr}">
      &nbsp;&nbsp;&nbsp;&nbsp;<img src="<%= request.getContextPath() + "/pdp/images/bullet-red.gif" %>" alt=">>"> Signifies a Cancelled and Re-issued Payment.  See the Payment History Tab for the matching Disbursement Number.
    </c:if>
  </logic:notEmpty>  
  <p>&nbsp;</p>
  <c:import url="/backdoor.jsp"/>
</body>
</html:html>
