<%--
 Copyright 2005-2006 The Kuali Foundation.
 
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
<%@ include file="/jsp/core/tldHeader.jsp" %>

<kul:documentPage showDocumentInfo="true" htmlFormAction="financialAuxiliaryVoucher" documentTypeName="KualiAuxiliaryVoucherDocument" renderMultipart="true" showTabButtons="true">
		<%-- derive displayReadOnly value --%>
		<c:set var="readOnly" value="${!empty KualiForm.editingMode['viewOnly']}" />

  	   	<SCRIPT type="text/javascript">
		<!--
		    function submitForChangedType() {
        		document.forms[0].submit();
		    }
		//-->
	    </SCRIPT>

		<kul:hiddenDocumentFields />

        <kul:documentOverview editingMode="${KualiForm.editingMode}"/>
		<!-- AUXILIARY VOUCHER SPECIFIC FIELDS -->
		<kul:tab tabTitle="Auxiliary Voucher Details" defaultOpen="true" tabErrorKey="${Constants.EDIT_AUXILIARY_VOUCHER_ERRORS}" >
	    	
	    	<div class="tab-container" align="center">
		<div class="h2-container">
		<h2>Auxiliary Voucher Details</h2>
		</div>	    	
	    	<table cellpadding="0" class="datatable" summary="view/edit ad hoc recipients">
            <tbody>
              <tr>
                <th width="35%" class="bord-l-b">
                  <div align="right">
                    <kul:htmlAttributeLabel attributeEntry="${DataDictionary.KualiAuxiliaryVoucherDocument.attributes.accountingPeriod}" useShortLabel="false" />
                  </div>
                </th>
                <td class="datacell-nowrap">
                    <c:if test="${readOnly}">
                        ${KualiForm.accountingPeriod.universityFiscalPeriodName}
                        <html:hidden property="selectedAccountingPeriod" />
					</c:if>
					<c:if test="${!readOnly}">
                        <select name="selectedAccountingPeriod">
							<c:forEach items="${KualiForm.accountingPeriods}" var="accountingPeriod">
								<c:set var="accountingPeriodCompositeValue" value="${accountingPeriod.universityFiscalPeriodCode}${accountingPeriod.universityFiscalYear}" />
								<c:choose>
									<c:when test="${KualiForm.selectedAccountingPeriod==accountingPeriodCompositeValue}" >
										<option value='<c:out value="${accountingPeriodCompositeValue}"/>' selected="selected"><c:out value="${accountingPeriod.universityFiscalPeriodName}" /></option>
									</c:when>
									<c:otherwise>
										<option value='<c:out value="${accountingPeriodCompositeValue}" />'><c:out value="${accountingPeriod.universityFiscalPeriodName}" /></option>
									</c:otherwise>
								</c:choose>
							</c:forEach>
						</select>
					</c:if>
                </td>
              </tr>
              <tr>
                  <th width="35%" class="bord-l-b">
                      <div align="right">
                          <kul:htmlAttributeLabel attributeEntry="${DataDictionary.KualiAuxiliaryVoucherDocument.attributes.typeCode}" useShortLabel="false" />
                          <html:hidden property="originalVoucherType" />
                      </div>
                  </th>
                  <td class="datacell-nowrap">
         	          <kul:htmlControlAttribute
					    	attributeEntry="${DataDictionary.KualiAuxiliaryVoucherDocument.attributes.typeCode}"
                            property="document.typeCode"
                            readOnly="${readOnly}" 
                            readOnlyAlternateDisplay="${KualiForm.formattedAuxiliaryVoucherType}" 
                            onchange="submitForChangedType()"/>          
						<NOSCRIPT>
    						<html:submit value="select" alt="press this button to refresh the page after changing the voucher type." />
						</NOSCRIPT>                
                  </td>
              </tr>
              <c:choose>
                  <c:when test="${empty KualiForm.document.typeCode || KualiForm.document.typeCode == Constants.AuxiliaryVoucher.ADJUSTMENT_DOC_TYPE}">
                  </c:when>                  
                  <c:otherwise>
                      <c:set var="reversalReadOnly" value="${readOnly}"/>
                      <c:if test="${!reversalReadOnly}">  <!--  if we're already readOnly b/c of authz permissions, then we want to stay that way -->
	                      <c:if test="${KualiForm.document.typeCode == Constants.AuxiliaryVoucher.RECODE_DOC_TYPE}">
						      <c:set var="reversalReadOnly" value="true"/>
	                      </c:if>
	                  </c:if>
                      <tr>
                          <kul:htmlAttributeHeaderCell
                                  attributeEntry="${DataDictionary.KualiAuxiliaryVoucherDocument.attributes.reversalDate}"
                                  horizontal="true"
                                  width="35%"
                                  />
                          <td class="datacell-nowrap">
                              <kul:htmlControlAttribute
                                      attributeEntry="${DataDictionary.KualiAuxiliaryVoucherDocument.attributes.reversalDate}"
                                      datePicker="true"
                                      property="document.reversalDate"
                                      readOnly="${reversalReadOnly}"
                                      readOnlyAlternateDisplay="${KualiForm.formattedReversalDate}"
                                      />
                          </td>
                      </tr>
                  </c:otherwise>
              </c:choose>
            </tbody>
          </table>
	    	</div>
		</kul:tab>
        <fin:voucherAccountingLines
            isDebitCreditAmount="true" 
            editingMode="${KualiForm.editingMode}"
            editableAccounts="${KualiForm.editableAccounts}"/>
		<kul:generalLedgerPendingEntries/>

		<kul:notes/>
						
		<kul:adHocRecipients />
			
		<kul:routeLog/>

		<kul:panelFooter/>

		<kul:documentControls transactionalDocument="${documentEntry.transactionalDocument}" />

</kul:documentPage>
