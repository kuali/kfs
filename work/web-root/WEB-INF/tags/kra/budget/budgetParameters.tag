<%@ taglib prefix="c" uri="/tlds/c.tld" %>
<%@ taglib uri="/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/tlds/fmt.tld" prefix="fmt" %>

<%@ taglib tagdir="/WEB-INF/tags" prefix="kul" %>
<%@ taglib tagdir="/WEB-INF/tags/kra" prefix="kra" %>
<%@ taglib tagdir="/WEB-INF/tags/kra/budget" prefix="kra-b" %>
<%@ taglib tagdir="/WEB-INF/tags/dd" prefix="dd" %>

  <c:set var="budgetAttributes" value="${DataDictionary.Budget.attributes}" />
  <c:set var="budgetPeriodAttributes" value="${DataDictionary.BudgetPeriod.attributes}" />
  <c:set var="supportsModular" value="${KualiForm.supportsModular}" />
  <c:set var="viewOnly" value="${KualiForm.editingMode['viewOnly']}"/>

       <table width="100%" border="0" cellpadding="0" cellspacing="0" class="t3">
          <tbody>
            <tr>
              <td ><img src="images/pixel_clear.gif" alt="" width="12" height="12" class="tl3"/></td>
              <td align="right"><img src="images/pixel_clear.gif" alt="" width="12" height="12" class="tr3"/></td>
            </tr>
          </tbody>
        </table>

        <div id="workarea" >
        
        <div class="tab-container-error">
        	<div class="left-errmsg-tab">
		        <kul:errors keyMatch="document.budget.parameters*,document.budget.budgetAgency*,document.budget.budgetProject*,document.budget.university*,document.budget.electronic*,document.budget.federal*,document.budget.modular*,document.budget.budgetPersonnelInflationRate,document.budget.budgetNonpersonnelInflationRate"/>
            <kra-b:auditErrors cluster="parametersAuditErrors" keyMatch="document.budget.audit.parameters.pd*" isLink="false" includesTitle="true"/>
          </div>
        </div>
        
        <div class="tab-container" align="center">
        <kra-b:budgetParametersGeneral supportsModular="${supportsModular}" />
      	
        <div class="left-errmsg-tab" style="padding-bottom: 10px;"><kul:errors keyMatch="document.budget.period*,newPeriod*"/></div>
        <kra-b:budgetPeriods />
        
        <div class="left-errmsg-tab" style="padding-bottom: 10px;">
        	<kul:errors keyMatch="document.budget.task*"/>
        	<kra-b:auditErrors cluster="parametersSoftAuditErrors" keyMatch="document.budget.audit.parameters.tasks.negativeIdc*" isLink="false" includesTitle="true"/>
        </div>
        <kra-b:budgetTasks supportsModular="${supportsModular}" />

        <div class="left-errmsg-tab" style="padding-bottom: 10px;"><kul:errors keyMatch="document.budget.fringe*"/></div>
        <kra-b:budgetFringeBenefits />
        
        <c:if test="${KualiForm.numberOfAcademicYearSubdivisions gt 0}">
	        <div class="left-errmsg-tab" style="padding-bottom: 10px;"><kul:errors keyMatch="document.budget.grad*"/></div>
	        <kra-b:budgetGradAssistantFringeRates />
        </c:if>
        
				<br>
				<div class="left-errmsg-tab" style="padding-bottom: 10px;"><kul:errors keyMatch="document.budget.budgetFringeRateDescription" /></div>
				<div class="h2-container"> <span class="subhead-left">
				  <h2>Benefit Rate Change Justification </h2>
				  </span><span class="subhead-right"> <span class="subhead"><kul:help businessObjectClassName="${DataDictionary.GraduateAssistantRate.businessObjectClass}" altText="help"/></span> </span> </div>
				<table cellpadding="0" cellspacing="0" class="datatable" summary=""> 
				  <tr align="left">
				    <td height="22" ><div align="center"> <br>
				        <kul:htmlControlAttribute property="document.budget.budgetFringeRateDescription" attributeEntry="${budgetAttributes.budgetFringeRateDescription}" readOnly="${viewOnly}"/> <br>
				        <br>
				      </div></td>
				  </tr>
				</table>        
 		</div>
 		
        <table width="100%" border="0" cellpadding="0" cellspacing="0" class="b3" summary="">
          <tr>
            <td align="left" class="footer"><img src="images/pixel_clear.gif" alt="" width="12" height="14" class="bl3"></td>
            <td align="right" class="footer-right"><img src="images/pixel_clear.gif" alt="" width="12" height="14" class="br3"></td>
          </tr>
        </table>
        </div>

  <!-- Various Hidden Fields that need to be kept around -->
  <c:if test="${supportsModular}">
	  <html:hidden property="document.budget.budgetAgency.agencyExtension.agencyNumber" />
	  <html:hidden property="document.budget.budgetAgency.agencyExtension.agencyModularIndicator" />
	  <html:hidden property="document.budget.modularBudget.documentHeaderId" />
	  <html:hidden property="document.budget.modularBudget.budgetModularIncrementAmount" />
	  <html:hidden property="document.budget.modularBudget.budgetPeriodMaximumAmount" />
	  <html:hidden property="document.budget.modularBudget.versionNumber" />
  </c:if>
  
  <logic:iterate id="budgetIndirectCostLookup" name="KualiForm" property="document.budget.budgetIndirectCostLookups" indexId="i">
      <html:hidden property="document.budget.budgetIndirectCostLookup[${i}].documentHeaderId" />
      <html:hidden property="document.budget.budgetIndirectCostLookup[${i}].budgetOnCampusIndicator" />
      <html:hidden property="document.budget.budgetIndirectCostLookup[${i}].budgetPurposeCode" />
      <html:hidden property="document.budget.budgetIndirectCostLookup[${i}].budgetIndirectCostRate" />
      <html:hidden property="document.budget.budgetIndirectCostLookup[${i}].objectId" />
      <html:hidden property="document.budget.budgetIndirectCostLookup[${i}].versionNumber" />
  </logic:iterate>