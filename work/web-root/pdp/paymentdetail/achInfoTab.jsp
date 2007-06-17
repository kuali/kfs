
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
                          <c:when test="${SecurityRecord.anyViewRole == true}">
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
	                        <c:when test="${(SecurityRecord.viewAllRole == true)}">
	                          <c:out value="${PaymentDetail.paymentGroup.achAccountNumber.achBankAccountNbr}"/>
	                        </c:when>
	                        <c:otherwise>
		                        <font color="#800000">
                            <c:choose>
                              <c:when test="${(SecurityRecord.viewBankRole == true)}">
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