
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
											Is Payee an IU Employee? 
				            </th>
				            <td align=left class="datacell">						
											<c:if test="${PaymentDetail.paymentGroup.iuEmployee == true}" >
												Yes
											</c:if>
											<c:if test="${PaymentDetail.paymentGroup.iuEmployee == false}" >
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
                          <c:when test="${SecurityRecord.anyViewRole == true}">
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
