
<%@ page language="java"%>

<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic" prefix="logic" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-template" prefix="template" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-nested" prefix="nested" %>
<%@ taglib uri="/WEB-INF/app.tld" prefix="app" %>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt_rt" prefix="fmt" %>

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