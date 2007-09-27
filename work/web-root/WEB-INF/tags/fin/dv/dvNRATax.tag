<%--
 Copyright 2006 The Kuali Foundation.
 
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

<kul:tab tabTitle="Nonresident Alien Tax" defaultOpen="false" tabErrorKey="${KFSConstants.DV_NRATAX_TAB_ERRORS}">
    <html:hidden property="document.dvNonResidentAlienTax.financialDocumentAccountingLineText"/>
    
	<c:set var="nraTaxAttributes" value="${DataDictionary.DisbursementVoucherNonResidentAlienTax.attributes}" />
    <div class="tab-container" align=center > 
    <div class="h2-container">
<h2>NRA Tax</h2>
</div>
	<table cellpadding=0 class="datatable" summary="NRA Tax Section">

            
            <tr>
              <th width="25%"><div align="right"><kul:htmlAttributeLabel attributeEntry="${nraTaxAttributes.incomeClassCode}"/></div></th>
              <td width="25%">
                <kul:htmlControlAttribute attributeEntry="${nraTaxAttributes.incomeClassCode}" property="document.dvNonResidentAlienTax.incomeClassCode" extraReadOnlyProperty="document.dvNonResidentAlienTax.incomeClassName"readOnly="${!taxEntryMode}"/>
                <c:if test="${taxEntryMode}">
                  <kul:lookup boClassName="org.kuali.module.financial.bo.TaxIncomeClassCode" fieldConversions="code:document.dvNonResidentAlienTax.incomeClassCode"/>
                </c:if>
              </td>
              <th width="25%" scope="row"><div align="right"><kul:htmlAttributeLabel attributeEntry="${nraTaxAttributes.incomeTaxTreatyExemptCode}"/></div></th>
              <td width="25%">
                <kul:htmlControlAttribute attributeEntry="${nraTaxAttributes.incomeTaxTreatyExemptCode}" property="document.dvNonResidentAlienTax.incomeTaxTreatyExemptCode" readOnly="${!taxEntryMode}"/>
              </td>
            </tr>
            
            <tr>
              <th  scope="row"><div align="right"><kul:htmlAttributeLabel attributeEntry="${nraTaxAttributes.federalIncomeTaxPercent}"/></div></th>
              <td>
                <kul:htmlControlAttribute attributeEntry="${nraTaxAttributes.federalIncomeTaxPercent}" property="document.dvNonResidentAlienTax.federalIncomeTaxPercent" readOnly="${!taxEntryMode}"/>
                <c:if test="${taxEntryMode}">
                  <kul:lookup boClassName="org.kuali.module.financial.bo.NonResidentAlienTaxPercent" fieldConversions="incomeTaxPercent:document.dvNonResidentAlienTax.federalIncomeTaxPercent" lookupParameters="document.dvNonResidentAlienTax.incomeClassCode:incomeClassCode,'F':incomeTaxTypeCode"/>
                </c:if>
              </td>
              <th  scope="row"><div align="right"><kul:htmlAttributeLabel attributeEntry="${nraTaxAttributes.foreignSourceIncomeCode}"/></div></th>
              <td>
                <kul:htmlControlAttribute attributeEntry="${nraTaxAttributes.foreignSourceIncomeCode}" property="document.dvNonResidentAlienTax.foreignSourceIncomeCode" readOnly="${!taxEntryMode}"/>
              </td>
            </tr>
            
            <tr>
              <th scope="row"><div align="right"><kul:htmlAttributeLabel attributeEntry="${nraTaxAttributes.stateIncomeTaxPercent}"/></div></th>
              <td>
                <kul:htmlControlAttribute attributeEntry="${nraTaxAttributes.stateIncomeTaxPercent}" property="document.dvNonResidentAlienTax.stateIncomeTaxPercent" readOnly="${!taxEntryMode}"/>
                <c:if test="${taxEntryMode}">
                  <kul:lookup boClassName="org.kuali.module.financial.bo.NonResidentAlienTaxPercent" fieldConversions="incomeTaxPercent:document.dvNonResidentAlienTax.stateIncomeTaxPercent" lookupParameters="document.dvNonResidentAlienTax.incomeClassCode:incomeClassCode,'S':incomeTaxTypeCode"/>
                </c:if>
              </td>
              <th scope="row"><div align="right"><kul:htmlAttributeLabel attributeEntry="${nraTaxAttributes.incomeTaxGrossUpCode}"/></div></th>
              <td>
                <kul:htmlControlAttribute attributeEntry="${nraTaxAttributes.incomeTaxGrossUpCode}" property="document.dvNonResidentAlienTax.incomeTaxGrossUpCode" readOnly="${!taxEntryMode}"/>
              </td>
            </tr>
            
            <tr>
              <th scope="row">
              	<div align="right">
              		<kul:htmlAttributeLabel attributeEntry="${nraTaxAttributes.postalCountryCode}"/>
              		<c:if test="${taxEntryMode}"><br> *required unless Income Class Code is Non Reportable</c:if>
              	</div>
              </th>
              <td>
                <kul:htmlControlAttribute attributeEntry="${nraTaxAttributes.postalCountryCode}" property="document.dvNonResidentAlienTax.postalCountryCode" readOnly="${!taxEntryMode}"/>
              </td>
              <th scope="row"><div align="right"><kul:htmlAttributeLabel attributeEntry="${nraTaxAttributes.referenceFinancialDocumentNumber}"/></div></th>
              <td>
                <kul:htmlControlAttribute attributeEntry="${nraTaxAttributes.referenceFinancialDocumentNumber}" property="document.dvNonResidentAlienTax.referenceFinancialDocumentNumber" readOnly="${!taxEntryMode}"/>
              </td>
            </tr>
            
            <c:if test="${taxEntryMode}">
              <tr>
                <td class="infoline" colspan="4">
                  <center>
                    <c:if test="${empty KualiForm.document.dvNonResidentAlienTax.financialDocumentAccountingLineText}">
                      <html:image src="${ConfigProperties.externalizable.images.url}tinybutton-genlines.gif" styleClass="tinybutton" property="methodToCall.generateNonResidentAlienTaxLines" title="Generate Non-resident Alien Tax Lines" alt="Generate Non-resident Alien Tax Lines"/>
                    </c:if>
                    <c:if test="${!empty KualiForm.document.dvNonResidentAlienTax.financialDocumentAccountingLineText}">
                      <html:image src="${ConfigProperties.externalizable.images.url}tinybutton-clearlines.gif" styleClass="tinybutton" property="methodToCall.clearNonResidentAlienTaxLines"  title="Clear Non-resident Alien Tax Lines" alt="Clear Non-resident Alien Tax Lines" />
                    </c:if>
                  </center>
                </td>
              </tr> 
            </c:if> 
          </table>
        </div>
</kul:tab>
