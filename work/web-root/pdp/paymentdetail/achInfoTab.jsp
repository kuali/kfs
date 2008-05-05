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

			  	<!-- ACH INFO TAB -->
					<tr>
	          <td>
				      <table width="100%" border=0 cellpadding=0 cellspacing=0 class="bord-r-t">
				        <tbody>
				          <tr>
				            <th width="33%" align=right valign=top nowrap>
				            	Payment Bank:
				            </th>
				            <td align=left class="datacell">						
				              <c:out value="${PaymentDetail.paymentGroup.bank.description}"/>
				            	&nbsp;
										</td>	
				          </tr>				    
				          <tr>
				            <th width="33%" align=right valign=top nowrap>
                      <c:if test="${SecurityRecord.anyViewRole != true}">
                        <font color="#800000">*&nbsp;</font>
                      </c:if>
				            	ACH Bank Routing Number:
				            </th>
				            <td align=left class="datacell">
                      <c:if test="${not empty PaymentDetail.paymentGroup.achBankRoutingNbr}">
                        <c:choose>
                          <c:when test="${(SecurityRecord.sysAdminRole == true) or (SecurityRecord.viewAllRole == true) or (SecurityRecord.viewBankRole == true) or (SecurityRecord.viewIdPartialBank == true)}">
                            <c:out value="${PaymentDetail.paymentGroup.achBankRoutingNbr}"/>
                          </c:when>
                          <c:otherwise>
                            **********
                          </c:otherwise>
                        </c:choose>
                      </c:if>&nbsp;
										</td>	
				          </tr>
				          <!-- NOT FOR ALL USERS TO SEE -->
				          <tr>
				            <th width="33%" align=right valign=top nowrap>
				            	<c:if test="${(SecurityRecord.viewBankRole != true) and (SecurityRecord.viewAllRole != true)}">
						          	<font color="#800000">*&nbsp;</font>
						          </c:if>
						          ACH Bank Account Number:
				            </th>
                    <td align=left class="datacell">
                      <c:if test="${not empty PaymentDetail.paymentGroup.achAccountNumber.achBankAccountNbr}">
	                      <c:choose>
	                        <c:when test="${(SecurityRecord.sysAdminRole == true) or (SecurityRecord.viewAllRole == true) or (SecurityRecord.viewBankRole == true)}">
	                          <c:out value="${PaymentDetail.paymentGroup.achAccountNumber.achBankAccountNbr}"/>
	                        </c:when>
	                        <c:otherwise>
		                        <font color="#800000">
                            <c:choose>
                              <c:when test="${(SecurityRecord.viewIdPartialBank == true)}">
	                              <c:out value="${PaymentDetail.paymentGroup.achAccountNumber.partialMaskAchBankAccountNbr}"/>
                              </c:when>
                              <c:otherwise>
                                *************
                              </c:otherwise>
                            </c:choose>
		                        </font>
	                        </c:otherwise>
	                      </c:choose>
                      </c:if>&nbsp;
                    </td> 
				          </tr>
			          </tbody>
		          </table>
						</td>
					</tr>