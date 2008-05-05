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

			  	<!-- PAYEE TAB -->
					<tr>
	          <td>
				      <table width="100%" border=0 cellpadding=0 cellspacing=0 class="bord-r-t">
				        <tbody>
				          <tr>
				            <th align=right valign=top nowrap>
				            	Alternate Payee ID Type:
				            </th>
				            <!-- set in session -->
				            <td align=left class="datacell">						
				              <c:out value="${alternatePayeeIdTypeDesc}"/>
				            	&nbsp;
										</td>	
				            <th rowspan="4" align=right valign=top nowrap>
											Street:
				            </th>
				            <td rowspan="4" align=left class="datacell">						
											<c:out value="${PaymentDetail.paymentGroup.line1Address}"/><br>
											<c:out value="${PaymentDetail.paymentGroup.line2Address}"/><br>
											<c:out value="${PaymentDetail.paymentGroup.line3Address}"/><br>
											<c:out value="${PaymentDetail.paymentGroup.line4Address}"/>
											&nbsp;
										</td>	
				          </tr>
				          <tr>
									<!-- Not to be seen by all users-->
				            <th align=right nowrap>
				            	<c:if test="${(SecurityRecord.viewIdRole != true)}">
						          	<font color="#800000">*&nbsp;</font>
						          </c:if>
						          Alternate Payee ID:
				            </th>
				            <td class="datacell">
						          <c:if test="${(SecurityRecord.viewIdRole == true)}">
					              <c:out value="${PaymentDetail.paymentGroup.alternatePayeeId}"/>&nbsp;
							        </c:if>
						          <c:if test="${(SecurityRecord.viewIdRole != true)}">
						          	<font color="#800000">
						          	<c:if test="${not empty PaymentDetail.paymentGroup.alternatePayeeId}">
					              	*************
					              </c:if>&nbsp;
					              </font>
							        </c:if>
										</td>	
									</tr>
				          <tr>
				            <th align=right valign=top nowrap>
				            	Payee Ownership Code:
				            </th>
				            <td align=left class="datacell">						
				              <c:out value="${PaymentDetail.paymentGroup.payeeOwnerCd}" />
				            	&nbsp;
										</td>	
				          </tr>
									<tr>
				            <th align=right valign=top nowrap>
											Is Payee an Employee? 
				            </th>
				            <td align=left class="datacell">						
											<c:if test="${PaymentDetail.paymentGroup.employeeIndicator == true}" >
												Yes
											</c:if>
											<c:if test="${PaymentDetail.paymentGroup.employeeIndicator == false}" >
												No
											</c:if>
											&nbsp;
										</td>	
				          </tr>
									<tr>
				            <th align=right valign=top nowrap>
											Is Payee a NRA? 
				            </th>
				            <td align=left class="datacell">						
											<c:if test="${PaymentDetail.paymentGroup.nraPayment == true}" >
												Yes
											</c:if>
											<c:if test="${PaymentDetail.paymentGroup.nraPayment == false}" >
												No
											</c:if>	
											&nbsp;
										</td>	
										<th align=right valign=top nowrap>
											City:
										</th>
										<td align=left class="datacell">
											<c:out value="${PaymentDetail.paymentGroup.city}"/>
				            	&nbsp;
										</td>
				          </tr>
				          <tr>
				            <th align=right valign=top nowrap>
				            	Taxable Payment?
				            </th>
				            <td align=left class="datacell">						
											<c:if test="${PaymentDetail.paymentGroup.taxablePayment == true}" >
												Yes
											</c:if>
											<c:if test="${PaymentDetail.paymentGroup.taxablePayment == false}" >
												No
											</c:if>	
											&nbsp;
										</td>	
										<th align=right valign=top nowrap>
											State:
										</th>
										<td align=left class="datacell" nowrap>
											<c:out value="${PaymentDetail.paymentGroup.state}"/>
				            	&nbsp;
										</td>
				          </tr>
				          <tr>
				            <th align=right valign=top nowrap>
                      <c:if test="${SecurityRecord.anyViewRole != true}">
                        <font color="#800000">*&nbsp;</font>
                      </c:if>
				            	ACH Advice E-mail Address:
				            </th>
				            <td align=left class="datacell">
                      <c:if test="${not empty PaymentDetail.paymentGroup.adviceEmailAddress}">
                        <c:choose>
                          <c:when test="${(SecurityRecord.sysAdminRole == true) or (SecurityRecord.viewAllRole == true) or (SecurityRecord.viewBankRole == true) or (SecurityRecord.viewIdPartialBank == true)}">
                            <c:out value="${PaymentDetail.paymentGroup.adviceEmailAddress}"/>
                          </c:when>
                          <c:otherwise>
                            **********
                          </c:otherwise>
                        </c:choose>
                      </c:if>&nbsp;
										</td>	
										<th align=right valign=top nowrap>
											Country:
										</th>
										<td align=left class="datacell">
											<c:out value="${PaymentDetail.paymentGroup.country}"/>
				            	&nbsp;
										</td>
				          </tr>
									<tr>
				            <th align=right valign=top nowrap>
											Is Address a Campus Address?
				            </th>
				            <td align=left class="datacell">
											<c:if test="${PaymentDetail.paymentGroup.campusAddress == true}" >
												Yes
											</c:if>
											<c:if test="${PaymentDetail.paymentGroup.campusAddress == false}" >
												No
											</c:if>
											&nbsp;
										</td>	
										<th align=right valign=top nowrap>
											Zip Code:
										</th>
										<td align=left class="datacell">
											<c:out value="${PaymentDetail.paymentGroup.zipCd}"/>
				            	&nbsp;
										</td>
				          </tr>
								</tbody>
							</table>
						</td>
					</tr>
