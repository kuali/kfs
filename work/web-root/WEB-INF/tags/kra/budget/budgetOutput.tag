<%@ taglib prefix="c" uri="/tlds/c.tld" %>
<%@ taglib uri="/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/tlds/fmt.tld" prefix="fmt" %>

<%@ taglib tagdir="/WEB-INF/tags" prefix="kul" %>
<%@ taglib tagdir="/WEB-INF/tags/dd" prefix="dd" %>
<%@ taglib tagdir="/WEB-INF/tags/kra" prefix="kra" %>
<%@ taglib tagdir="/WEB-INF/tags/kra/budget" prefix="kra-b" %>

<div align="right">
	<kul:help documentTypeName="${DataDictionary.KualiBudgetDocument.documentTypeName}" pageName="${KraConstants.OUTPUT_HEADER_TAB}" altText="page help"/>
</div>

<table width="100%" border="0" cellpadding="0" cellspacing="0" class="t3" summary="">

        <tbody>
          <tr>
            <td ><img src="images/pixel_clear.gif" alt="" width="12" height="12" class="tl3"></td>
            <td align="right"><img src="images/pixel_clear.gif" alt="" width="12" height="12" class="tr3"></td>
          </tr>
        </tbody>
      </table>

<div id="workarea" >

        <div class="tab-container-error">
        	<div class="left-errmsg-tab">
				<kul:errors keyMatch="currentOutputReportType" />
				<kul:errors keyMatch="currentOutputDetailLevel" />
				<kul:errors keyMatch="currentOutputAgencyType" />
				<kul:errors keyMatch="currentOutputAgencyPeriod" />
			</div>
        </div>

        <div class="tab-container"  align="center">
           <kra-b:budgetOutputSelection />
        </div>
        <table width="100%" border="0" cellpadding="0" cellspacing="0" class="b3" summary="">
          <tr>
            <td align="left" class="footer"><img src="images/pixel_clear.gif" alt="" width="12" height="14" class="bl3"></td>
            <td align="right" class="footer-right"><img src="images/pixel_clear.gif" alt="" width="12" height="14" class="br3"></td>
          </tr>
        </table>
      </div>
      <div id="globalbuttons" class="globalbuttons">
        <html:image src="images/buttonsmall_genpdf.gif" styleClass="globalbuttons" property="methodToCall.pdfOutput" alt="Copy current document" onclick="excludeSubmitRestriction=true"/>
      </div>
<div class="exportlinks">Export options: 
<a href="researchBudgetOutput.do?document.financialDocumentNumber=${KualiForm.document.financialDocumentNumber}&methodToCall=xmlOutput"><span class="export xml">XML</span></a></div>
