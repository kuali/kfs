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
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html:html locale="true">
<link rel="stylesheet" type="text/css"  href="<%= request.getContextPath() %>/pdp/css/pdp_styles.css">
  <head>
    <html:base />
    <title>Payment Update</title>
  </head>
  <body>
    <h1><strong>Payment Update:</strong> </h1> <br>
    <jsp:include page="${request.contextPath}/pdp/TestEnvironmentWarning.jsp" flush="true"/>
    <html:form action="/pdp/paymentupdate">
      <table width="100%" border="0" cellspacing="0" cellpadding="0">
        <tr valign="top">
          <td width="20">&nbsp</td>
          <td>
            <table width="100%" border=0 cellpadding=0 cellspacing=0>
              <tbody>
                <tr>
                  <td height="30" nowrap="true" colspan="3">
                    <br>
                    <font color="#800000">
                      <html:errors/>&nbsp;
                    </font>&nbsp;
                  </td>
                </tr>
                <tr>
                  <td height="30" nowrap="true" colspan="3">
                    <br>
                    <c:choose>
                      <c:when test="${PaymentMaintenanceForm.action eq 'Hold'}">
                        You are about to Hold the Payment below.
                      </c:when>
                      <c:when test="${PaymentMaintenanceForm.action eq 'RemoveHold'}">
                        You are about to Remove a Hold on the Payment below.
                      </c:when>
                      <c:when test="${PaymentMaintenanceForm.action eq 'Cancel'}">
                        You are about to Cancel the Payment below.  Once a payment has been cancelled it can not be re-opened for payment.
                      </c:when>
                      <c:when test="${PaymentMaintenanceForm.action eq 'DisbursementCancel'}">
                        You are about to Cancel the Disbursement below.  This action is final and cannot be undone.
                      </c:when>
                      <c:when test="${PaymentMaintenanceForm.action eq 'ReIssueCancel'}">
                        You are about to Cancel and Reissue the Disbursement below.
                      </c:when>
                      <c:when test="${PaymentMaintenanceForm.action eq 'ChangeImmediate'}">
                        You are about to Change the Print Immediate Flag on the Payment below.
                      </c:when>
                    </c:choose>
                    &nbsp;<br><br>
                  </td>
                </tr>
                <tr>
                  <td>&nbsp;</td>
            <td>
              <table width="100%" border=0 cellpadding=0 cellspacing=0 class="bord-r-t">
                <tbody>
             <!-- SUMMARY TAB -->
                  <tr>
                    <th align=right nowrap>
                      Customer
                    </th>
                    <td class="datacell">
                      <c:out value="${PaymentDetail.paymentGroup.batch.customerProfile.chartCode}"/>-
                      <c:out value="${PaymentDetail.paymentGroup.batch.customerProfile.orgCode}"/>-
                      <c:out value="${PaymentDetail.paymentGroup.batch.customerProfile.subUnitCode}"/>
                      &nbsp;
                    </td>
                    <th width="25%" align=right nowrap>
                      Payee Name:
                    </th>
                    <td width="25%" class="datacell">
                      <c:out value="${PaymentDetail.paymentGroup.payeeName}"/>
                      &nbsp;
                    </td>
                  </tr>
                  <tr>
                    <th width="25%" align=right valign=top nowrap>
                      Disbursement Type:
                    </th>
                    <td width="25%" align=left class="datacell">            
                      <c:out value="${PaymentDetail.paymentGroup.disbursementType.description}"/>
                      &nbsp;
                    </td>  
                    <th align=right valign=top nowrap>
                      Payee ID Type:
                    </th>
                    <!-- set in session -->
                    <td align=left class="datacell">
                      <c:out value="${payeeIdTypeDesc}" />
                      &nbsp;
                    </td>
                  </tr>
                  <tr>
                    <th align=right valign=top nowrap>
                      Disbursement Number: 
                    </th>
                    <td align=left class="datacell">            
                      <c:out value="${PaymentDetail.paymentGroup.disbursementNbr}"/>
                      &nbsp;
                    </td>  
                  <!-- Not to be seen by all users-->
                    <th align=right nowrap>
                      <c:if test="${(SecurityRecord.viewIdRole != true)}">
                        <font color="#800000">*&nbsp;</font>
                      </c:if>
                      Payee ID:
                    </th>
                    <td class="datacell">
                    <c:choose>
						<c:when test="${(SecurityRecord.sysAdminRole == true) or (SecurityRecord.viewAllRole == true) or (SecurityRecord.viewIdRole == true)}">
					    	<c:out value="${PaymentDetail.paymentGroup.payeeId}"/>&nbsp;
						</c:when>
						<c:otherwise>
						   <font color="#800000">
						   *************
					        &nbsp;
					       </font>
					   </c:otherwise>
					</c:choose>
                    </td>  
                  </tr>
                  <tr>
                    <th align=right valign=top nowrap>
                      Payment Date:
                    </th>
                    <td align=left class="datacell">            
                      <fmt:formatDate value="${PaymentDetail.paymentGroup.paymentDate}" pattern="MM/dd/yyyy"/>
                      &nbsp;
                    </td>  
                    <th align=right valign=top nowrap>
                      Source Payment Document Number:
                    </th>
                    <td align=left class="datacell">
                      <c:out value="${PaymentDetail.custPaymentDocNbr}" />
                      &nbsp;
                    </td>
                  </tr>
                  <tr>
                    <th align=right valign=top nowrap>
                      Disbursement Date:
                    </th>
                    <td align=left class="datacell">            
                      <fmt:formatDate value="${PaymentDetail.paymentGroup.disbursementDate}" pattern="MM/dd/yyyy"/>
                      &nbsp;
                    </td>  
                    <th align=right valign=top nowrap>
                      PO Number:
                    </th>
                    <td align=left class="datacell">            
                      <c:out value="${PaymentDetail.purchaseOrderNbr}"/>
                      &nbsp;
                    </td>  
                  </tr>
                  <tr>
                    <th align=right valign=top nowrap>
                      Payment Status:
                    </th>
                    <td align=left class="datacell">            
                      <c:out value="${PaymentDetail.paymentGroup.paymentStatus.description}"/>
                      &nbsp;
                    </td>  
                    <th align=right valign=top nowrap>
                      Invoice Number:
                    </th>
                    <td align=left class="datacell">            
                      <c:out value="${PaymentDetail.invoiceNbr}"/>
                      &nbsp;
                    </td>  
                  </tr>
                  <tr>
                    <th align=right valign=top nowrap>
                      Net Payment Amount:
                    </th>
                    <td align=left class="datacell">            
                      <fmt:formatNumber value="${PaymentDetail.netPaymentAmount}" type="currency" />
                      &nbsp;
                    </td>
                    <th align=right valign=top nowrap>
                      Requisition Number:
                    </th>
                    <td align=left class="datacell">            
                      <c:out value="${PaymentDetail.requisitionNbr}"/>
                      &nbsp;
                    </td>  
                  </tr>
                  <tr>
                    <th align=right nowrap>
                      Currently Immediate?
                    </th>
                    <td class="datacell">
                      <c:if test="${PaymentDetail.paymentGroup.processImmediate == true}" >
                        Yes
                      </c:if>
                      <c:if test="${PaymentDetail.paymentGroup.processImmediate == false}" >
                        No
                      </c:if>  
                      &nbsp;
                    </td>
                    <th align=right nowrap>
                      Customer Number for Institution:
                    </th>
                    <td class="datacell">
                      <c:out value="${PaymentDetail.paymentGroup.customerInstitutionNumber}"/>
                      &nbsp;
                    </td>
                  </tr>  
                </tbody>
              </table>
              <table width="100%">
                <tr>
                  <td>&nbsp;</td>
                  <td align="right">
                    <c:if test="${(SecurityRecord.viewIdRole != true)}">
                       <font color="#800000">* You do not have access to fields with Red Asterisk</font>
                    </c:if>
                  </td>
                  <td>&nbsp;</td>
                </tr>
              </table>
            </td>
            <td>&nbsp;</td>
          </tr>
          <tr>
            <td>&nbsp;</td>
            <td>
            
              <table width="100%" border="0" cellspacing="0" cellpadding="0">
                <tr>
                  <td width="10">&nbsp;</td>
                  <td>&nbsp;</td>
                  <td width="10">&nbsp;</td>
                </tr>  
                <tr>
                  <td width="10">&nbsp;</td>
                  <td>&nbsp;</td>
                  <td width="10">&nbsp;</td>
                </tr>  
<c:if test="${(PaymentMaintenanceForm.action != 'DisbursementCancel') and (PaymentMaintenanceForm.action != 'ReIssueCancel')}">
                <tr>
                  <td width="10">&nbsp;</td>
                    <td>
                      <c:if test="${size > 1}">
                        Other Payments Associated with this Payment Group:
                      </c:if>
                      <c:if test="${size <= 1}">
                        There are no other payments associated with this Payment Group.
                      </c:if>
                    </td>
                  <td width="10">&nbsp;</td>
                </tr>
                <tr>
                  <td width="10">&nbsp;</td>
                  <td>&nbsp;</td>
                  <td width="10">&nbsp;</td>
                </tr>  
                <c:if test="${size > 1}">
                  <tr>
                    <td width="10">&nbsp;</td>
                    <td>
                      <div align="center">
                      <table width="80%" border="0" cellpadding="4" cellspacing="0" class="bord-r-t">
                        <tr>
                          <th height="32" class="thfont">
                            Payment Detail ID
                          </th>
                          <th height="32" class="thfont">
                            Source Payment Document Number
                          </th>
                          <th height="32"  class="thfont">
                            PO Number
                          </th>
                          <th height="32" class="thfont">
                            Invoice Number
                          </th>
                          <th height="32" class="thfont">
                            Requisition Number
                          </th>
                          <th height="32" class="thfont">
                            Customer Number for Institution:
                          </th>
                          <th height="32" class="thfont">
                            Pay Date
                          </th>
                          <th height="32" class="thfont">
                            Net Payment Amount
                          </th>
                        </tr>
                        <tbody>
                          <c:forEach items="${PaymentDetail.paymentGroup.paymentDetails}" var="item">
                            <c:if test="${item.id != PaymentDetail.id}">
                            <tr>
                              <td nowrap=nowrap class="datacell">
                                <a href="<%= request.getContextPath().toString() %>/paymentdetail.do?DetailId=<c:out value="${item.id}"/>"><c:out value="${item.id}"/></a>&nbsp;
                              </td>
                              <td nowrap=nowrap class="datacell">
                                <c:out value="${item.custPaymentDocNbr}" />&nbsp;
                              </td>
                              <td nowrap=nowrap class="datacell">  
                                <c:out value="${item.purchaseOrderNbr}" />&nbsp;
                              </td>
                              <td nowrap=nowrap class="datacell">
                                <c:out value="${item.invoiceNbr}" />&nbsp;
                              </td>
                              <td nowrap=nowrap class="datacell">
                                <c:out value="${item.requisitionNbr}" />&nbsp;
                              </td>
                              <td nowrap=nowrap class="datacell">
                                <c:out value="${PaymentDetail.paymentGroup.customerInstitutionNumber}"/>&nbsp;
                              </td>
                              <td nowrap=nowrap class="datacell">
                                <fmt:formatDate value="${item.paymentGroup.paymentDate}" pattern="MM/dd/yyyy" />&nbsp;
                              </td>
                              <td nowrap=nowrap class="datacell" align="right" >
                                <fmt:formatNumber value="${item.netPaymentAmount}" type="currency" />&nbsp;
                              </td>
                            </tr>
                            </c:if>
                          </c:forEach>
                        </tbody>
                      </table>
                      </div>
                    </td>
                  </tr>
                </c:if>
</c:if>
<c:if test="${(PaymentMaintenanceForm.action == 'DisbursementCancel') or (PaymentMaintenanceForm.action == 'ReIssueCancel')}">
                <tr>
                  <td width="10">&nbsp;</td>
                  <td>&nbsp;</td>
                  <td width="10">&nbsp;</td>
                </tr>  
                <tr>
                  <td width="10">&nbsp;</td>
                  <td>&nbsp;</td>
                  <td width="10">&nbsp;</td>
                </tr>  
                <tr>
                  <td width="10">&nbsp;</td>
                    <td>
                      <c:if test="${disbNbrTotalPayments > 1}">
                        Other Payments Associated with this Disbursement:
                      </c:if>
                      <c:if test="${disbNbrTotalPayments <= 1}">
                        There are no other payments associated with this Disbursement.
                      </c:if>
                    </td>
                  <td width="10">&nbsp;</td>
                </tr>
                <tr>
                  <td width="10">&nbsp;</td>
                  <td>&nbsp;</td>
                  <td width="10">&nbsp;</td>
                </tr>  
                <c:if test="${disbNbrTotalPayments > 1}">
                  <tr>
                    <td width="10">&nbsp;</td>
                    <td>
                      <div align="center">
                      <table width="80%" border="0" cellpadding="4" cellspacing="0" class="bord-r-t">
                        <tr>
                          <th height="32" class="thfont">
                            Payment Detail ID
                          </th>
                          <th height="32" class="thfont">
                            Source Payment Document Number
                          </th>
                          <th height="32"  class="thfont">
                            PO Number
                          </th>
                          <th height="32" class="thfont">
                            Invoice Number
                          </th>
                          <th height="32" class="thfont">
                            Requisition Number
                          </th>
                          <th height="32" class="thfont">
                            Customer Number for IU:
                          </th>
                          <th height="32" class="thfont">
                            Pay Date
                          </th>
                          <th height="32" class="thfont">
                            Net Payment Amount
                          </th>
                        </tr>
                        <tbody>
                          <c:forEach items="${disbursementDetailsList}" var="item">
                            <c:if test="${item.id != PaymentDetail.id}">
                            <tr>
                              <td nowrap=nowrap class="datacell">
                                <a href="<%= request.getContextPath().toString() %>/paymentdetail.do?DetailId=<c:out value="${item.id}"/>"><c:out value="${item.id}"/></a>&nbsp;
                              </td>
                              <td nowrap=nowrap class="datacell">
                                <c:out value="${item.custPaymentDocNbr}" />&nbsp;
                              </td>
                              <td nowrap=nowrap class="datacell">  
                                <c:out value="${item.purchaseOrderNbr}" />&nbsp;
                              </td>
                              <td nowrap=nowrap class="datacell">
                                <c:out value="${item.invoiceNbr}" />&nbsp;
                              </td>
                              <td nowrap=nowrap class="datacell">
                                <c:out value="${item.requisitionNbr}" />&nbsp;
                              </td>
                              <td nowrap=nowrap class="datacell">
                                <c:out value="${PaymentDetail.paymentGroup.customerInstitutionNumber}"/>&nbsp;
                              </td>
                              <td nowrap=nowrap class="datacell">
                                <fmt:formatDate value="${item.paymentGroup.paymentDate}" pattern="MM/dd/yyyy" />&nbsp;
                              </td>
                              <td nowrap=nowrap class="datacell" align="right" >
                                <fmt:formatNumber value="${item.netPaymentAmount}" type="currency" />&nbsp;
                              </td>
                            </tr>
                            </c:if>
                          </c:forEach>
                        </tbody>
                      </table>
                      </div>
                    </td>
                  </tr>
                </c:if>
</c:if>
                <tr>
                  <td width="10">&nbsp;</td>
                  <td>&nbsp;</td>
                  <td width="10">&nbsp;</td>
                </tr>
                <tr>
                  <td width="10">&nbsp;</td>
                    <td>
                      Please enter a note to describe the change:&nbsp;<br>
                    </td>
                  <td width="10">&nbsp;</td>
                </tr>
              </table>
            </td>
            <td>&nbsp;</td>
          </tr>
          <tr>
            <td>&nbsp;</td>
            <td>
              <br>
              <div align="center">
              <table cellpadding=3 width="65%" cellspacing=0 border=0  class="bord-r-t">
                <tbody>
                  <tr>
                    <th width="33%" align=right nowrap>
                      Enter Change Note:
                    </th>
                    <td class="datacell">
                      <html:textarea cols="40" rows="4" property="changeText" />
                      <html:hidden property="changeId" />
                      <html:hidden property="paymentDetailId" />
                      <html:hidden property="action" />&nbsp;
                    </td>
                  </tr>
                  <tr valign=middle align=left>
                    <th nowrap=nowrap colspan="2">
                      <div align="center">
                        <input type="image" name="btnUpdateSave" src="<%= request.getContextPath().toString() %>/pdp/images/button_save.gif" alt="Save" align="absmiddle"></a>
                        <input type="image" name="btnUpdateClear" src="<%= request.getContextPath().toString() %>/pdp/images/button_clearfields.gif" alt="Clear" align="absmiddle"></a>
                        <input type="image" name="btnUpdateCancel" src="<%= request.getContextPath().toString() %>/pdp/images/button_cancel.gif" alt="Cancel" align="absmiddle"></a>
                      </div>
                    </th>
                  </tr>
                </tbody>
              </table>
              </div>
            </td>
            <td>&nbsp;</td>
          </tr>
            </td>
            <td>&nbsp;</td>
          </tr>
        </tbody>
      </table>
    </td>
    <td width=20>&nbsp;</td>
  </tr>
</table>



    </html:form>
<c:import url="/backdoor.jsp"/>
  </body>
</html:html>
