package edu.arizona.kfs.coa.businessobject;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.Organization;
import org.kuali.kfs.coa.service.ChartService;
import org.kuali.kfs.coa.service.OrganizationService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.mo.common.active.Inactivatable;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.krad.bo.KualiCodeBase;
//import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * UAF-120 / MOD-FP0072-01 : Budget Shell and Cross Organization Attributes for
 * Account
 * 
 * @author Jonathan Keller <keller.jonathan@gmail.com>
 * @author Adam Kost <kosta@email.arizona.edu>
 */
public class CrossOrganizationCode extends KualiCodeBase implements Inactivatable {

    private static final long serialVersionUID = 1L;
    private static transient volatile PersonService personService;
    private static transient volatile ChartService chartService;
    private static transient volatile OrganizationService organizationService;

    // Database fields
    private String chartOfAccountsCode;
    private String organizationCode;
    private String leaderPrincipalId;

    // Helper objects
    private transient volatile Chart chart;
    private transient volatile Organization organization;
    private transient volatile Person leader;
    
    public String getCrossOrganizationCode() {
        return getCode();
    }
    
    public void setCrossOrganizationCode( String crossOrganizationCode ) {
        setCode( crossOrganizationCode );
    }

    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }

    public String getOrganizationCode() {
        return organizationCode;
    }

    public void setOrganizationCode(String organizationCode) {
        this.organizationCode = organizationCode;
    }

    public String getLeaderPrincipalId() {
        return leaderPrincipalId;
    }

    public void setLeaderPrincipalId(String leaderPrincipalId) {
        this.leaderPrincipalId = leaderPrincipalId;
    }

    public Chart getChart() {
        if (chart == null || !StringUtils.equals(chart.getChartOfAccountsCode(), chartOfAccountsCode)) {
            chart = getChartService().getByPrimaryId(chartOfAccountsCode);
        }
        return chart;
    }

    public Person getLeader() {
        leader = getPersonService().updatePersonIfNecessary(leaderPrincipalId, leader);
        return leader;
    }

    public Organization getOrganization() {
        if (organization == null || !StringUtils.equals(organization.getOrganizationCode(), organizationCode)) {
            organization = getOrganizationService().getByPrimaryId(getChartOfAccountsCode(), organizationCode);
        }
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
        setOrganizationCode(organization.getOrganizationCode());
    }

    public void setChart(Chart chart) {
        this.chart = chart;
        setChartOfAccountsCode(chart.getChartOfAccountsCode());
    }

    public void setLeader(Person leader) {
        this.leader = leader;
        setLeaderPrincipalId(leader.getPrincipalId());
    }

    public static PersonService getPersonService() {
        if (personService == null) {
            personService = SpringContext.getBean(PersonService.class);
        }
        return personService;
    }

    private static ChartService getChartService() {
        if (chartService == null) {
            chartService = SpringContext.getBean(ChartService.class);
        }
        return chartService;
    }

    private static OrganizationService getOrganizationService() {
        if (organizationService == null) {
            organizationService = SpringContext.getBean(OrganizationService.class);
        }
        return organizationService;
    }
}