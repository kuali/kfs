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

		     <!-- SUMMARY TAB -->
					<tr>
				    <td>
				    	<!-- This section always included -->
				      <table width="100%" border=0 cellpadding=0 cellspacing=0 class="bord-r-t">
				        <tbody>
									<tr>
				            <th width="25%" align=right valign=top nowrap>
				            	Number of Payments in this Disbursement:
				            </th>
				            <td width="25%" align=left class="datacell">						
				              <a href="<%= request.getContextPath().toString() %>/pdp/paymentlist.do?listType=disbursement"><c:out value="${disbNbrTotalPayments}"/></a>
				            	&nbsp;
										</td>	
				            <th width="25%" align=right nowrap>
				            	Number of Payments in this Payment Group:
				            </th>
				            <td width="25%" class="datacell">
				            	<a href="<%= request.getContextPath().toString() %>/pdp/paymentlist.do?listType=group"><c:out value="${size}"/></a>
											&nbsp;
										</td>
				          </tr>
									<tr>
										<th colspan="4" class="datacell">
											&nbsp; <!-- Spacer Row -->
										</th>
									</tr>
								</tbody>
							</table>
				    <!-- Included on Initial Load and after Update -->
						<c:if test="${btnPressed == 'btnSummaryTab'}" >
				      <table width="100%" border=0 cellpadding=0 cellspacing=0 class="bord-r-t">
				        <tbody>
				          <tr>
				            <th align=right nowrap>
				            	Customer:
				            </th>
				            <td class="datacell">
				              <c:out value="${PaymentDetail.paymentGroup.batch.customerProfile.chartCode}"/>-<c:out value="${PaymentDetail.paymentGroup.batch.customerProfile.orgCode}"/>-<c:out value="${PaymentDetail.paymentGroup.batch.customerProfile.subUnitCode}"/>
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
				            	Invoice Date:
				            </th>
				            <td align=left class="datacell">						
				              <fmt:formatDate value="${PaymentDetail.invoiceDate}" pattern="MM/dd/yyyy"/>
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
				            	Payment Date:
				            </th>
				            <td align=left class="datacell">						
				              <fmt:formatDate value="${PaymentDetail.paymentGroup.paymentDate}" pattern="MM/dd/yyyy"/>
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
				            	Disbursement Date:
				            </th>
				            <td align=left class="datacell">						
				              <fmt:formatDate value="${PaymentDetail.paymentGroup.disbursementDate}" pattern="MM/dd/yyyy"/>
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
				            	Payment Status:
				            </th>
				            <td align=left class="datacell">						
											<c:out value="${PaymentDetail.paymentGroup.paymentStatus.description}"/>
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
									  <th align=right valign=top nowrap>
				            	Net Payment Amount:
				            </th>
				            <td align=left class="datacell">						
				              <fmt:formatNumber value="${PaymentDetail.netPaymentAmount}" type="currency" />
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
									<tr>
										<th colspan="4" class="datacell">
											&nbsp; <!-- Spacer Row -->
										</th>
									</tr>
								</tbody>
							</table>
					</c:if>
		    	<!-- Shortened Summary info to display on all other Tabs -->
					<c:if test="${btnPressed != 'btnSummaryTab'}" >
							<table width="100%" border="0" cellspacing="0" cellpadding="0">
							  <tr>
							    <td width="20">&nbsp;</td>
							    <td>&nbsp;</td>
							    <td width="20">&nbsp;</td>
							  </tr>
							  <tr>
							    <td width="20">&nbsp;</td>
							    <td>
							      <table width="100%" border="0" cellpadding="4" cellspacing="0" class="bord-r-t">
							        <tr>
							          <th height="32" class="thfont">
							      			Customer
							      		</th>
							          <th width="10" height="32" class="thfont">
										      Source Document Number</th>
							          <th height="32"  class="thfont">
							            PO Number
							          </th>
							          <th height="32" class="thfont">
							      			Invoice Number
							      		</th>
							          <th height="32" class="thfont">
							      			Payee Name
												</th>
							          <th height="32" class="thfont">
							      			Pay Date
							      		</th>
							          <th height="32" class="thfont">
							      			Disb. Date
							      		</th>
							          <th height="32" class="thfont">
							      			Status
							      		</th>
							          <th height="32" class="thfont">
							      			Disb. Type
							      		</th>
							      		<th height="32" class="thfont">
							      			Disb. Number
							      		</th>
							          <th height="32" class="thfont">
							      			Net Payment Amount
							      		</th>
							        </tr>
							        <tbody>
													<tr>
														<td nowrap=nowrap class="datacell">
															<c:out value="${PaymentDetail.paymentGroup.batch.customerProfile.chartCode}" />-<c:out value="${PaymentDetail.paymentGroup.batch.customerProfile.orgCode}" />-<c:out value="${PaymentDetail.paymentGroup.batch.customerProfile.subUnitCode}" /></a>
														</td>
														<td nowrap=nowrap class="datacell">
															<c:out value="${PaymentDetail.custPaymentDocNbr}" />&nbsp;
														</td>
														<td nowrap=nowrap class="datacell">	
															<c:out value="${PaymentDetail.purchaseOrderNbr}" />&nbsp;
														</td>
														<td nowrap=nowrap class="datacell">
															<c:out value="${PaymentDetail.invoiceNbr}" />&nbsp;
														</td>
														<td nowrap=nowrap class="datacell">
															<c:out value="${PaymentDetail.paymentGroup.payeeName}" />&nbsp;
														</td>
														<td nowrap=nowrap class="datacell">
															<fmt:formatDate value="${PaymentDetail.paymentGroup.paymentDate}" pattern="MM/dd/yyyy" />&nbsp;
														</td>
														<td nowrap=nowrap class="datacell">
															<fmt:formatDate value="${PaymentDetail.paymentGroup.disbursementDate}" pattern="MM/dd/yyyy" />&nbsp;
														</td>
														<td nowrap=nowrap class="datacell">
															<c:out value="${PaymentDetail.paymentGroup.paymentStatus.code}" />&nbsp;
														</td>
														<td nowrap=nowrap class="datacell">
															<c:if test="${PaymentDetail.paymentGroup.disbursementType!=null}" >
																<c:out value="${PaymentDetail.paymentGroup.disbursementType.description}" />
															</c:if>
															&nbsp;
														</td>
														<td nowrap=nowrap class="datacell">
															<c:out value="${PaymentDetail.paymentGroup.disbursementNbr}" />&nbsp;
														</td>
														<td nowrap=nowrap class="datacell" align="right" >
															<fmt:formatNumber value="${PaymentDetail.netPaymentAmount}" type="currency" />&nbsp;
														</td>
													</tr>
												  <tr>
													  <th height="2" class="thfont" colspan="11">&nbsp;</td>
													</tr>
									  	</tbody>
								  	</table>
							  	</td>
								  <td width="20">&nbsp;</td>
							  </tr>
							  <tr>
								  <td width="20">&nbsp;</td>
								  <td>&nbsp;</td>
								  <td width="20">&nbsp;</td>
							  </tr>
							</table>
						</c:if>
						</td>						
					</tr>
