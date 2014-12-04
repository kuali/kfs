<%--
   - The Kuali Financial System, a comprehensive financial management system for higher education.
   - 
   - Copyright 2005-2014 The Kuali Foundation
   - 
   - This program is free software: you can redistribute it and/or modify
   - it under the terms of the GNU Affero General Public License as
   - published by the Free Software Foundation, either version 3 of the
   - License, or (at your option) any later version.
   - 
   - This program is distributed in the hope that it will be useful,
   - but WITHOUT ANY WARRANTY; without even the implied warranty of
   - MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   - GNU Affero General Public License for more details.
   - 
   - You should have received a copy of the GNU Affero General Public License
   - along with this program.  If not, see <http://www.gnu.org/licenses/>.
--%>
<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>
<%@ attribute name="hasRelatedCashControlDocument" required="true"
    description="If has related cash control document"%>
<%@ attribute name="readOnly" required="true"
    description="If document is in read only mode"%>
<%@ attribute name="isCustomerSelected" required="true"
    description="Whether or not the customer is set" %>
<c:set var="unappliedAttributes" value="${DataDictionary['NonAppliedHolding'].attributes}" />
<c:set var="customerAttributes" value="${DataDictionary['Customer'].attributes}" />

    <kul:tab tabTitle="Unapplied"
	    defaultOpen="${hasRelatedCashControlDocument}"
        tabErrorKey="${KFSConstants.PaymentApplicationTabErrorCodes.UNAPPLIED_TAB}">
        <div class="tab-container" align="center">
        	<c:choose>
            	<c:when test="${!hasRelatedCashControlDocument}">
					No Cash Control Document
	        	</c:when>
	        	<c:otherwise>
	        	    <h3>Unapplied</h3>
            		<table width="100%" cellpadding="0" cellspacing="0" class="datatable">
                		<tr>
                    		<kul:htmlAttributeHeaderCell literalLabel="Customer"/>  
                    		<td>
                        		<kul:htmlControlAttribute
                        			readOnly="${readOnly}"
                            		attributeEntry="${customerAttributes.customerNumber}"
                            		property="nonAppliedHoldingCustomerNumber"/>
								<c:if test="${readOnly ne true}">
	                        		<kul:lookup boClassName="org.kuali.kfs.module.ar.businessobject.Customer" autoSearch="true"
	                            		fieldConversions="customerNumber:nonAppliedHoldingCustomerNumber"
	                            		lookupParameters="nonAppliedHoldingCustomerNumber:customerNumber" />
                        		</c:if>
                    		</td>
                    		<kul:htmlAttributeHeaderCell literalLabel="Amount"/>  
                    		<td>
                    			<html:hidden property="oldNonAppliedHoldingAmount" value="${oldNonAppliedHoldingAmount}" />
                        		<kul:htmlControlAttribute
	                        		styleClass="amount"
                            		attributeEntry="${unappliedAttributes.financialDocumentLineAmount}"
                            		property="nonAppliedHoldingAmount"
                            		readOnly="${readOnly}" />
								<c:if test="${readOnly ne true}">
	                        		<html:image property="methodToCall.applyAllAmounts"
	                            		src="${ConfigProperties.externalizable.images.url}tinybutton-apply.gif"
	                            		alt="Commit Unapplied" title="Commit Unapplied" styleClass="tinybutton" />
	                    		</c:if>
                    		</td>
                		</tr>
            		</table>
	        	</c:otherwise>
        	</c:choose>
        </div>
    </kul:tab>
