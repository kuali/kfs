<%--
 Copyright 2006-2007 The Kuali Foundation.
 
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
<%@ taglib prefix="c" uri="/tlds/c.tld"%>
<%@ taglib uri="/tlds/struts-html.tld" prefix="html"%>
<%@ taglib uri="/tlds/struts-logic.tld" prefix="logic"%>
<%@ taglib uri="/tlds/fmt.tld" prefix="fmt"%>
<%@ taglib uri="/tlds/fn.tld" prefix="fn"%>

<%@ taglib tagdir="/WEB-INF/tags" prefix="kul"%>
<%@ taglib tagdir="/WEB-INF/tags/dd" prefix="dd"%>
<%@ taglib tagdir="/WEB-INF/tags/kra" prefix="kra"%>

<%@ attribute name="editingMode" required="true" description="used to decide editability of overview fields" type="java.util.Map"%>

<c:set var="readOnly" value="${empty KualiForm.editingMode['fullEntry']}" />
<c:set var="budgetLinked" value="${KualiForm.editingMode['budgetLinked']}" />

<c:set var="institutionCostShareAttributes" value="${DataDictionary.RoutingFormInstitutionCostShare.attributes}" />
<c:set var="otherCostShareAttributes" value="${DataDictionary.RoutingFormOtherCostShare.attributes}" />

<kul:tabTop tabTitle="Cost Share" defaultOpen="false" tabErrorKey="document.routingFormInstitutionCostShare*">
  <div class="tab-container" align="center">
    <div class="h2-container">
      <h2>Cost Share</h2>
    </div>

    <table cellpadding=0 cellspacing="0" summary="">
      <tr>
        <td colspan=7 class="tab-subhead"><span class="left">Institution Cost Share</span></td>
      </tr>
      <tr>
        <th width="50">&nbsp;</th>
        <th><kul:htmlAttributeLabel attributeEntry="${institutionCostShareAttributes.chartOfAccountsCode}" skipHelpUrl="true" useShortLabel="true" noColon="true" /></th>
        <th><kul:htmlAttributeLabel attributeEntry="${institutionCostShareAttributes.organizationCode}" skipHelpUrl="true" useShortLabel="true" noColon="true" /></th>
        <th><kul:htmlAttributeLabel attributeEntry="${institutionCostShareAttributes.routingFormCostShareDescription}" skipHelpUrl="true" useShortLabel="true" noColon="true" /></th>
        <th><kul:htmlAttributeLabel attributeEntry="${institutionCostShareAttributes.accountNumber}" skipHelpUrl="true" useShortLabel="true" noColon="true" /></th>
        <th><kul:htmlAttributeLabel attributeEntry="${institutionCostShareAttributes.routingFormCostShareAmount}" skipHelpUrl="true" useShortLabel="true" noColon="true" /></th>
        <c:if test="${not readOnly and not budgetLinked}"><th>Action</th></c:if>
      </tr>

<c:if test="${not viewOnly and not budgetLinked}">
      <tr>
        <th class="infoline">
          <div align="center">add:</div>
        </th>
        <td class="infoline">
          <div align="center">
            <kul:htmlControlAttribute property="newRoutingFormInstitutionCostShare.chartOfAccountsCode" attributeEntry="${institutionCostShareAttributes.chartOfAccountsCode}" readOnly="${viewOnly or budgetLinked}"/>
          </div>
        </td>
        <td class="infoline">
          <div align="center">
            <kul:htmlControlAttribute property="newRoutingFormInstitutionCostShare.organizationCode" attributeEntry="${institutionCostShareAttributes.organizationCode}" readOnly="${viewOnly or budgetLinked}"/>
            <c:if test="${viewOnly or budgetLinked}"> <kul:lookup boClassName="org.kuali.module.chart.bo.Org" lookupParameters="newRoutingFormInstitutionCostShare.organizationCode:organizationCode,newRoutingFormInstitutionCostShare.chartOfAccountsCode:chartOfAccountsCode" fieldConversions="organizationCode:newRoutingFormInstitutionCostShare.organizationCode,chartOfAccountsCode:newRoutingFormInstitutionCostShare.chartOfAccountsCode" tabindexOverride="5100" anchor="${currentTabIndex}" /></c:if>
          </div>
        </td>
        <td class="infoline">
          <div align="center">
            <kul:htmlControlAttribute property="newRoutingFormInstitutionCostShare.routingFormCostShareDescription" attributeEntry="${institutionCostShareAttributes.routingFormCostShareDescription}" readOnly="${viewOnly or budgetLinked}"/>
          </div>
        </td>
        <td class="infoline">
          <div align="center">
            <kul:htmlControlAttribute property="newRoutingFormInstitutionCostShare.accountNumber" attributeEntry="${institutionCostShareAttributes.accountNumber}" readOnly="${viewOnly or budgetLinked}"/>
            <kul:lookup boClassName="org.kuali.module.chart.bo.Account" lookupParameters="newRoutingFormInstitutionCostShare.accountNumber:accountNumber,newRoutingFormInstitutionCostShare.chartOfAccountsCode:chartOfAccountsCode,newRoutingFormInstitutionCostShare.organizationCode:organizationCode" fieldConversions="accountNumber:newRoutingFormInstitutionCostShare.accountNumber,chartOfAccountsCode:newRoutingFormInstitutionCostShare.chartOfAccountsCode,organizationCode:newRoutingFormInstitutionCostShare.organizationCode" tabindexOverride="5100" anchor="${currentTabIndex}" />
          </div>
        </td>
        <td class="infoline">
          <div align="right">
            <kul:htmlControlAttribute property="newRoutingFormInstitutionCostShare.routingFormCostShareAmount" attributeEntry="${institutionCostShareAttributes.routingFormCostShareAmount}" styleClass="amount" />
          </div>
        </td>
        <td class="infoline">
          <div align="center"><html:image property="methodToCall.insertRoutingFormInstitutionCostShare.anchor${currentTabIndex}" styleClass="tinybutton" src="images/tinybutton-add1.gif" alt="add institution cost share" /></div>
        </td>
      </tr>
</c:if>

    <c:forEach items="${KualiForm.document.routingFormInstitutionCostShares}" var="routingFormInstitutionCostShare" varStatus="status">
      <tr>
        <th class="neutral">
          <div align="center">${status.index+1}</div>
        </th>
        <td class="neutral">
          <kul:htmlControlAttribute property="document.routingFormInstitutionCostShare[${status.index}].routingFormCostShareSequenceNumber" attributeEntry="${institutionCostShareAttributes.routingFormCostShareSequenceNumber}"/>
          <kul:htmlControlAttribute property="document.routingFormInstitutionCostShare[${status.index}].documentNumber" attributeEntry="${institutionCostShareAttributes.documentNumber}"/>
          <kul:htmlControlAttribute property="document.routingFormInstitutionCostShare[${status.index}].objectId" attributeEntry="${institutionCostShareAttributes.objectId}"/>
          <kul:htmlControlAttribute property="document.routingFormInstitutionCostShare[${status.index}].versionNumber" attributeEntry="${institutionCostShareAttributes.versionNumber}"/>
          <div align="center">
            <kul:htmlControlAttribute property="document.routingFormInstitutionCostShare[${status.index}].chartOfAccountsCode" attributeEntry="${institutionCostShareAttributes.chartOfAccountsCode}" readOnly="${viewOnly or budgetLinked}"/>
          </div>
        </td>
        <td class="neutral">
          <div align="center">
            <kul:htmlControlAttribute property="document.routingFormInstitutionCostShare[${status.index}].organizationCode" attributeEntry="${institutionCostShareAttributes.organizationCode}" readOnly="${viewOnly or budgetLinked}"/>
            <c:if test="${not viewOnly and not budgetLinked}"><kul:lookup boClassName="org.kuali.module.chart.bo.Org" lookupParameters="document.routingFormInstitutionCostShare[${status.index}].organizationCode:organizationCode,document.routingFormInstitutionCostShare[${status.index}].chartOfAccountsCode:chartOfAccountsCode" fieldConversions="organizationCode:document.routingFormInstitutionCostShare[${status.index}].organizationCode,chartOfAccountsCode:document.routingFormInstitutionCostShare[${status.index}].chartOfAccountsCode" tabindexOverride="5100" anchor="${currentTabIndex}" /></c:if>
          </div>
        </td>
        <td class="neutral">
          <div align="center">
            <kul:htmlControlAttribute property="document.routingFormInstitutionCostShare[${status.index}].routingFormCostShareDescription" attributeEntry="${institutionCostShareAttributes.routingFormCostShareDescription}" readOnly="${viewOnly or budgetLinked}"/>
          </div>
        </td>
        <td class="neutral">
          <div align="center">
            <kul:htmlControlAttribute property="document.routingFormInstitutionCostShare[${status.index}].accountNumber" attributeEntry="${institutionCostShareAttributes.accountNumber}" readOnly="${viewOnly or budgetLinked}"/>
            <c:if test="${not viewOnly and not budgetLinked}"><kul:lookup boClassName="org.kuali.module.chart.bo.Account" lookupParameters="document.routingFormInstitutionCostShare[${status.index}].accountNumber:accountNumber,document.routingFormInstitutionCostShare[${status.index}].chartOfAccountsCode:chartOfAccountsCode,document.routingFormInstitutionCostShare[${status.index}].organizationCode:organizationCode" fieldConversions="accountNumber:document.routingFormInstitutionCostShare[${status.index}].accountNumber,chartOfAccountsCode:document.routingFormInstitutionCostShare[${status.index}].chartOfAccountsCode,organizationCode:document.routingFormInstitutionCostShare[${status.index}].organizationCode" tabindexOverride="5100" anchor="${currentTabIndex}" /></c:if>
          </div>
        </td>
        <td class="neutral">
          <div align="right">
            <kul:htmlControlAttribute property="document.routingFormInstitutionCostShare[${status.index}].routingFormCostShareAmount" attributeEntry="${institutionCostShareAttributes.routingFormCostShareAmount}" styleClass="amount" readOnly="${viewOnly or budgetLinked}"/>
          </div>
        </td>
        <c:if test="${not readOnly and not budgetLinked}">
        <td class="neutral">
          <div align="center"><html:image property="methodToCall.deleteRoutingFormInstitutionCostShare.line${status.index}.anchor${currentTabIndex}" styleClass="tinybutton" src="images/tinybutton-delete1.gif" alt="delete institution cost share" /></div>
        </td>
        </c:if>
      </tr>
    </c:forEach>
    <tr>
      <td colspan="5" class="total-line" scope="row">&nbsp;</td>
      <td class="total-line"><strong> Total: <fmt:formatNumber value="${KualiForm.document.totalInstitutionCostShareAmount}" type="currency" /></strong></td>
      <c:if test="${not readOnly and not budgetLinked}"><td class="total-line">&nbsp;</td></c:if>
    </tr>
</table>


  <table cellpadding=0 cellspacing="0" summary="">
    <tr>
      <td colspan=7 class="tab-subhead"><span class="left">3rd Party Cost Share</span></td>
    </tr>
    
    <tr>
      <th>&nbsp;</th>
      <th colspan="3"><kul:htmlAttributeLabel attributeEntry="${otherCostShareAttributes.routingFormCostShareSourceName}" skipHelpUrl="true" useShortLabel="true" noColon="true" /></th>
      <th colspan="2"><kul:htmlAttributeLabel attributeEntry="${otherCostShareAttributes.routingFormCostShareAmount}" skipHelpUrl="true" useShortLabel="true" noColon="true" /></th>
      <c:if test="${not readOnly and not budgetLinked}"><th>Action</th></c:if>
    </tr>

<c:if test="${not viewOnly and not budgetLinked}">
    <tr>
      <th scope="row">add:</th>
      <td colspan="3" class="infoline">
        <div align="center">
          <kul:htmlControlAttribute property="newRoutingFormOtherCostShare.routingFormCostShareSourceName" attributeEntry="${otherCostShareAttributes.routingFormCostShareSourceName}" readOnly="${viewOnly or budgetLinked}"/>
        </div>
      </td>
      <td colspan="2" class="infoline">
        <div align="right">
          <kul:htmlControlAttribute property="newRoutingFormOtherCostShare.routingFormCostShareAmount" attributeEntry="${otherCostShareAttributes.routingFormCostShareAmount}" styleClass="amount" readOnly="${viewOnly or budgetLinked}"/>
        </div>
      </td>
      <td class="infoline">
        <div align=center>
          <html:image property="methodToCall.insertRoutingFormOtherCostShare.anchor${currentTabIndex}" styleClass="tinybutton" src="images/tinybutton-add1.gif" alt="add 3rd party cost share" />
        </div>
      </td>
    </tr>
</c:if>

    <c:forEach items="${KualiForm.document.routingFormOtherCostShares}" var="routingFormOtherCostShare" varStatus="status">
      <tr>
        <th scope="row">
          <div align="center">${status.index+1}</div>
        </th>
        <td colspan="3">
          <kul:htmlControlAttribute property="document.routingFormOtherCostShare[${status.index}].routingFormCostShareSequenceNumber" attributeEntry="${otherCostShareAttributes.routingFormCostShareSequenceNumber}" />
          <kul:htmlControlAttribute property="document.routingFormOtherCostShare[${status.index}].documentNumber" attributeEntry="${otherCostShareAttributes.documentNumber}" />
          <kul:htmlControlAttribute property="document.routingFormOtherCostShare[${status.index}].objectId" attributeEntry="${otherCostShareAttributes.objectId}" />
          <kul:htmlControlAttribute property="document.routingFormOtherCostShare[${status.index}].versionNumber" attributeEntry="${otherCostShareAttributes.versionNumber}" />
          <div align="center">
            <kul:htmlControlAttribute property="document.routingFormOtherCostShare[${status.index}].routingFormCostShareSourceName" attributeEntry="${otherCostShareAttributes.routingFormCostShareSourceName}" readOnly="${viewOnly or budgetLinked}"/>
          </div>
        </td>
        <td colspan="2">
          <div align="right">
            <kul:htmlControlAttribute property="document.routingFormOtherCostShare[${status.index}].routingFormCostShareAmount" attributeEntry="${otherCostShareAttributes.routingFormCostShareAmount}" styleClass="amount" readOnly="${viewOnly or budgetLinked}"/>
          </div>
        </td>
        <c:if test="${not readOnly and not budgetLinked}">
        <td>
          <div align=center>
            <html:image property="methodToCall.deleteRoutingFormOtherCostShare.line${status.index}.anchor${currentTabIndex}" styleClass="tinybutton" src="images/tinybutton-delete1.gif" alt="delete 3rd party cost share" />
          </div>
        </td>
        </c:if>
      </tr>
    </c:forEach>

    <tr>
      <td colspan="4" class="total-line" scope="row">&nbsp;</td>
      <td colspan="2" class="total-line">
        <strong> Total: <fmt:formatNumber value="${KualiForm.document.totalOtherCostShareAmount}" type="currency" /></strong>
      </td>
      <c:if test="${not readOnly and not budgetLinked}"><td class="total-line">&nbsp;</td></c:if>
    </tr>
  </table>

  </div>

</kul:tabTop>
