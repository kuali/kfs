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

			  	<!-- HISTORY TAB -->
					<tr>
				    <td><br><br>
				    	<div align="center">
				    	<c:if test="${!(empty PaymentDetail.paymentGroup.paymentGroupHistory)}">
					      <table width="100%" border="0" cellpadding="4" cellspacing="0"  class="bord-r-t">
					        <tr>
					          <th height="50" class="thfont">
					      			Change Time
					          </th>
					          <th height="50" class="thfont">
								      Payment Change Code
								    </th>
					          <th height="50"  class="thfont">
					            Change User
					          </th>
					          <th height="50" class="thfont">
					      			Orig. Disb. Type
					          </th>
					          <th height="50" class="thfont">
					      			Original Disb. Number
					          </th>
					          <th height="50" class="thfont">
					      			Original Disb. Date
					          </th>
<!--
					          <th height="50" class="thfont">
					      			Original Payment Date
					          </th>
-->
					          <th height="50" class="thfont">
					      			Original Payment Status
					          </th>
					          <th height="50" class="thfont">
					      			Bank
					          </th>
					          <th height="50" class="thfont">
					      			Original ACH Bank Routing Number
					          </th>
<!--
					          <th height="50" class="thfont">
					      			Original Process Immediate Indicator
					          </th>
					          <th height="50" class="thfont">
					      			Original Special Handling Indicator
					          </th>
-->					          
					          <th height="50" class="thfont">
					      			Cancel Extract Status
					          </th>
					          <th height="50" class="thfont">
					      			Cancel Extract Date
					          </th>
					          <th height="50" class="thfont">
					      			Change Note Text
					      		</th>
<!--
					          <th height="50" class="thfont">
					      			Original Advice E-Mail
					          </th>
-->					          
	  				    	</tr>
					        <tbody>
					        	<c:forEach items="${PaymentDetail.paymentGroup.paymentGroupHistory}" var="item" >
						          <tr>
						            <td class="datacell">
						            	<fmt:formatDate value="${item.changeTime}" pattern="MM/dd/yyyy'  at  'hh:mm a"/>&nbsp;
												</td>
						            <td class="datacell">
						              <c:out value="${item.paymentChange.description}"/>&nbsp;
												</td>
											  <td class="datacell">
						              <c:out value="${item.changeUser.networkId}"/>&nbsp;
												</td>
						            <td class="datacell">
						              <c:out value="${item.disbursementType.description}"/>&nbsp;
												</td>
						            <td class="datacell">
						              <c:out value="${item.origDisburseNbr}"/>&nbsp;
												</td>
						            <td class="datacell">
						              <c:out value="${item.origDisburseDate}"/>&nbsp;
												</td>
<!--
						            <td class="datacell">
						            	<fmt:formatDate value="${item.origPaymentDate}" pattern="MM/dd/yyyy"/>&nbsp;
												</td>
-->
						            <td class="datacell">
						              <c:out value="${item.origPaymentStatus.description}"/>&nbsp;
												</td>
						            <td class="datacell">
						              <c:out value="${item.bank.description}"/>&nbsp;
												</td>
						            <td class="datacell">
						              <c:out value="${item.origAchBankRouteNbr}"/>&nbsp;
												</td>
<!--
						            <td class="datacell">
													<c:if test="${item.origProcessImmediate == true}" >
														Yes
													</c:if>
													<c:if test="${item.origProcessImmediate == false}" >
														No
													</c:if>&nbsp;
												</td>
						            <td class="datacell">
													<c:if test="${item.origPmtSpecHandling == true}" >
														Yes
													</c:if>
													<c:if test="${item.origPmtSpecHandling == false}" >
														No
													</c:if>&nbsp;
												</td>
-->												
						            <td class="datacell">
													<c:if test="${item.pmtCancelExtractStat == true}" >
														Yes
													</c:if>
													<c:if test="${item.pmtCancelExtractStat == false}" >
														No
													</c:if>&nbsp;
												</td>
						            <td class="datacell">
						            	<fmt:formatDate value="${item.pmtCancelExtractDate}" pattern="MM/dd/yyyy"/>&nbsp;
												</td>
						            <td class="datacell">
						              <c:out value="${item.changeNoteText}"/>&nbsp;
												</td>
<!--
						            <td class="datacell">
						              <c:out value="${item.origAdviceEmail}"/>&nbsp;
												</td>
-->												
											</tr>
										</c:forEach>
									</tbody>
								</table>
							</c:if>
							<c:if test="${empty PaymentDetail.paymentGroup.paymentGroupHistory}">
								<strong>There are no history records for this payment.</strong><br><br>
							</c:if>
							</div><br>
						</td>
					</tr>