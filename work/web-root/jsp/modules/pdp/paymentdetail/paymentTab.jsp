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

	  			<!-- PAYMENT TAB -->
					<tr>
	          <td>
				      <table width="100%" border=0 cellpadding=0 cellspacing=0 class="bord-r-t">
				        <tbody>
				          <tr>
				            <th align=right valign=top nowrap>
				            	Immediate Print?
				            </th>
				            <td align=left class="datacell">						
											<c:if test="${KualiForm.paymentDetail.paymentGroup.processImmediate == true}" >
												Yes
											</c:if>
											<c:if test="${KualiForm.paymentDetail.paymentGroup.processImmediate == false}" >
												No
											</c:if>	
											&nbsp;
										</td>	
				            <th align=right valign=top nowrap>
				            	Original Invoice Amount:
				            </th>
				            <td align=left class="datacell">						
				              <fmt:formatNumber value="${KualiForm.paymentDetail.origInvoiceAmount}" type="currency" />
				            	&nbsp;
										</td>	
				          </tr>
				          <tr>
				            <th align=right valign=top nowrap>
				            	Special Handling?
				            </th>
				            <td align=left class="datacell">						
											<c:if test="${KualiForm.paymentDetail.paymentGroup.pymtSpecialHandling == true}" >
												Yes
											</c:if>
											<c:if test="${KualiForm.paymentDetail.paymentGroup.pymtSpecialHandling == false}" >
												No
											</c:if>	
											&nbsp;
										</td>	
				            <th align=right valign=top nowrap>
				            	Net Payment Amount:
				            </th>
				            <td align=left class="datacell">						
				              <fmt:formatNumber value="${KualiForm.paymentDetail.netPaymentAmount}" type="currency" />
				            	&nbsp;
										</td>	
				          </tr>
				          <tr>
				            <th align=right valign=top nowrap>
				            	Attachment?
				            </th>
				            <td align=left class="datacell">						
											<c:if test="${KualiForm.paymentDetail.paymentGroup.pymtAttachment == true}" >
												Yes
											</c:if>
											<c:if test="${KualiForm.paymentDetail.paymentGroup.pymtAttachment == false}" >
												No
											</c:if>	
											&nbsp;
										</td>	
				            <th align=right valign=top nowrap>
				            	Invoice Total Discount Amount:
				            </th>
				            <td align=left class="datacell">						
				              <fmt:formatNumber value="${KualiForm.paymentDetail.invTotDiscountAmount}" type="currency" />
				            	&nbsp;
										</td>	
				          </tr>
									<tr>
				            <th align=right valign=top nowrap>
											Payment Last Updated: 
				            </th>
				            <td align=left class="datacell">						
											<fmt:formatDate value="${KualiForm.paymentDetail.paymentGroup.lastUpdate}" pattern="MM/dd/yyyy'  at  'hh:mm a"/>
											&nbsp;
										</td>	
				            <th align=right valign=top nowrap>
				            	Invoice Total Shipping Amount:
				            </th>
				            <td align=left class="datacell">						
				              <fmt:formatNumber value="${KualiForm.paymentDetail.invTotShipAmount}" type="currency" />
				            	&nbsp;
										</td>	
				          </tr>
				          <tr>
				            <th align=right valign=top nowrap>
				            	Payment Status:
				            </th>
				            <td align=left class="datacell">						
				              <c:out value="${PaymentDetail.paymentGroup.paymentStatus.name}"/>
				            	&nbsp;
										</td>	
				            <th align=right valign=top nowrap>
					            Invoice Total Other Debits Amount:
				            </th>
				            <td align=left class="datacell">						
				              <fmt:formatNumber value="${PaymentDetail.invTotOtherDebitAmount}" type="currency" />
				            	&nbsp;
										</td>	
				          </tr>
				          <tr>
				            <th align=right valign=top nowrap>
				            	Combinable Payment Group?
				            </th>
				            <td align=left class="datacell">						
											<c:if test="${PaymentDetail.paymentGroup.combineGroups == true}" >
												Yes
											</c:if>
											<c:if test="${PaymentDetail.paymentGroup.combineGroups == false}" >
												No
											</c:if>	
											&nbsp;
										</td>	
				            <th align=right valign=top nowrap>
					            Invoice Total Other Credits Amount:
				            </th>
				            <td align=left class="datacell">						
				              <fmt:formatNumber value="${PaymentDetail.invTotOtherCreditAmount}" type="currency" />
				            	&nbsp;
										</td>	
				          </tr>
								</tbody>
							</table>
						</td>
					</tr>
