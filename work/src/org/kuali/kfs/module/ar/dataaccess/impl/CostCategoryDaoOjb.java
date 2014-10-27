/*
 * Copyright 2014 The Kuali Foundation.
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.ar.dataaccess.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.Query;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.kfs.coa.businessobject.ObjectCodeCurrent;
import org.kuali.kfs.coa.businessobject.ObjectLevel;
import org.kuali.kfs.gl.businessobject.Balance;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.CostCategory;
import org.kuali.kfs.module.ar.businessobject.CostCategoryDetail;
import org.kuali.kfs.module.ar.businessobject.CostCategoryObjectCode;
import org.kuali.kfs.module.ar.businessobject.CostCategoryObjectConsolidation;
import org.kuali.kfs.module.ar.businessobject.CostCategoryObjectLevel;
import org.kuali.kfs.module.ar.dataaccess.CostCategoryDao;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.util.TransactionalServiceUtils;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;

/**
 * Default OJB implementation of the CostCategoryDao
 */
public class CostCategoryDaoOjb extends PlatformAwareDaoBaseOjb implements CostCategoryDao {

    /**
     * @see org.kuali.kfs.module.ar.dataaccess.CostCategoryDao#isCostCategoryObjectConsolidationUnique(org.kuali.kfs.module.ar.businessobject.CostCategoryObjectConsolidation)
     */
    @Override
    public CostCategoryDetail retrieveMatchingCostCategoryConsolidationAmongConsolidations(CostCategoryObjectConsolidation objectConsolidation) {
        Criteria crit = new Criteria();
        if (!StringUtils.isBlank(objectConsolidation.getCategoryCode())) {
            crit.addNotEqualTo(ArPropertyConstants.CATEGORY_CODE, objectConsolidation.getCategoryCode());
        }
        crit.addEqualTo(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, objectConsolidation.getChartOfAccountsCode());
        crit.addEqualTo(KFSPropertyConstants.FIN_CONSOLIDATION_OBJECT_CODE, objectConsolidation.getFinConsolidationObjectCode());
        crit.addEqualTo(KFSPropertyConstants.ACTIVE, Boolean.TRUE);
        crit.addIn(ArPropertyConstants.CATEGORY_CODE, buildActiveCostCategorySubQuery());
        Query q = new QueryByCriteria(CostCategoryObjectConsolidation.class, crit);
        return (CostCategoryDetail)TransactionalServiceUtils.retrieveFirstAndExhaustIterator(getPersistenceBrokerTemplate().getIteratorByQuery(q));
    }

    /**
     * @see org.kuali.kfs.module.ar.dataaccess.CostCategoryDao#isCostCategoryConsolidationUniqueAmongLevels(org.kuali.kfs.module.ar.businessobject.CostCategoryObjectConsolidation)
     */
    @Override
    public CostCategoryDetail retrieveMatchingCostCategoryConsolidationAmongLevels(CostCategoryObjectConsolidation consolidation) {
        Criteria crit = new Criteria();
        if (!StringUtils.isBlank(consolidation.getCategoryCode())) {
            crit.addNotEqualTo(ArPropertyConstants.CATEGORY_CODE, consolidation.getCategoryCode());
        }
        crit.addEqualTo(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, consolidation.getChartOfAccountsCode());
        crit.addEqualTo(ArPropertyConstants.OBJECT_LEVEL+"."+KFSPropertyConstants.FINANCIAL_CONSOLIDATION_OBJECT_CODE, consolidation.getFinConsolidationObjectCode());
        crit.addEqualTo(KFSPropertyConstants.ACTIVE, Boolean.TRUE);
        crit.addIn(ArPropertyConstants.CATEGORY_CODE, buildActiveCostCategorySubQuery());
        Query q = new QueryByCriteria(CostCategoryObjectLevel.class, crit);
        return (CostCategoryDetail)TransactionalServiceUtils.retrieveFirstAndExhaustIterator(getPersistenceBrokerTemplate().getIteratorByQuery(q));
    }

    /**
     * @see org.kuali.kfs.module.ar.dataaccess.CostCategoryDao#isCostCategoryConsolidationUniqueAmongCodes(org.kuali.kfs.module.ar.businessobject.CostCategoryObjectConsolidation)
     */
    @Override
    public CostCategoryDetail retrieveMatchingCostCategoryConsolidationAmongCodes(CostCategoryObjectConsolidation consolidation) {
        Criteria crit = new Criteria();
        if (!StringUtils.isBlank(consolidation.getCategoryCode())) {
            crit.addNotEqualTo(ArPropertyConstants.CATEGORY_CODE, consolidation.getCategoryCode());
        }
        crit.addEqualTo(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, consolidation.getChartOfAccountsCode());
        crit.addEqualTo(KFSPropertyConstants.OBJECT_CODE_CURRENT+"."+KFSPropertyConstants.FINANCIAL_OBJECT_LEVEL+"."+KFSPropertyConstants.FINANCIAL_CONSOLIDATION_OBJECT_CODE, consolidation.getFinConsolidationObjectCode());
        crit.addEqualTo(KFSPropertyConstants.ACTIVE, Boolean.TRUE);
        crit.addIn(ArPropertyConstants.CATEGORY_CODE, buildActiveCostCategorySubQuery());
        Query q = new QueryByCriteria(CostCategoryObjectCode.class, crit);
        return (CostCategoryDetail)TransactionalServiceUtils.retrieveFirstAndExhaustIterator(getPersistenceBrokerTemplate().getIteratorByQuery(q));
    }

    /**
     * @return a newly generated subquery which guarantees that we will only check active cost categories
     */
    protected ReportQueryByCriteria buildActiveCostCategorySubQuery() {
        Criteria crit = new Criteria();
        crit.addEqualTo(KFSPropertyConstants.ACTIVE, Boolean.TRUE);
        ReportQueryByCriteria subQuery = QueryFactory.newReportQuery(CostCategory.class, crit);
        subQuery.setAttributes(new String[] { ArPropertyConstants.CATEGORY_CODE} );
        return subQuery;
    }

    /**
     * @see org.kuali.kfs.module.ar.dataaccess.CostCategoryDao#isCostCategoryObjectLevelUnique(org.kuali.kfs.module.ar.businessobject.CostCategoryObjectLevel)
     */
    @Override
    public CostCategoryDetail retrieveMatchingCostCategoryLevelAmongLevels(CostCategoryObjectLevel objectLevel) {
        Criteria crit = new Criteria();
        if (!StringUtils.isBlank(objectLevel.getCategoryCode())) {
            crit.addNotEqualTo(ArPropertyConstants.CATEGORY_CODE, objectLevel.getCategoryCode());
        }
        crit.addEqualTo(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, objectLevel.getChartOfAccountsCode());
        crit.addEqualTo(KFSPropertyConstants.FINANCIAL_OBJECT_LEVEL_CODE, objectLevel.getFinancialObjectLevelCode());
        crit.addEqualTo(KFSPropertyConstants.ACTIVE, Boolean.TRUE);
        crit.addIn(ArPropertyConstants.CATEGORY_CODE, buildActiveCostCategorySubQuery());
        Query q = new QueryByCriteria(CostCategoryObjectLevel.class, crit);
        return (CostCategoryDetail)TransactionalServiceUtils.retrieveFirstAndExhaustIterator(getPersistenceBrokerTemplate().getIteratorByQuery(q));
    }

    /**
     * @see org.kuali.kfs.module.ar.dataaccess.CostCategoryDao#isCostCategoryLevelUniqueAmongConsolidations(org.kuali.kfs.module.ar.businessobject.CostCategoryObjectLevel)
     */
    @Override
    public CostCategoryDetail retrieveMatchingCostCategoryLevelAmongConsolidations(CostCategoryObjectLevel level) {
        Criteria consolidationCrit = new Criteria();
        if (!StringUtils.isBlank(level.getCategoryCode())) {
            consolidationCrit.addNotEqualTo(ArPropertyConstants.CATEGORY_CODE, level.getCategoryCode());
        }
        consolidationCrit.addEqualTo(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, level.getChartOfAccountsCode());
        consolidationCrit.addEqualTo(KFSPropertyConstants.FIN_CONSOLIDATION_OBJECT_CODE, buildLevelConsolidationCodeSubQuery(level));
        consolidationCrit.addEqualTo(KFSPropertyConstants.ACTIVE, Boolean.TRUE);
        consolidationCrit.addIn(ArPropertyConstants.CATEGORY_CODE, buildActiveCostCategorySubQuery());
        Query consolidationQuery = new QueryByCriteria(CostCategoryObjectConsolidation.class, consolidationCrit);
        return (CostCategoryDetail)TransactionalServiceUtils.retrieveFirstAndExhaustIterator(getPersistenceBrokerTemplate().getIteratorByQuery(consolidationQuery));
    }

    /**
     * Builds a subquery - so we can perform the whole operation as a single query - which finds the consolidation code of an object level
     * @param level the cost category object level to find the consolidation code for
     * @return a subQuery to use in a larger query
     */
    protected ReportQueryByCriteria buildLevelConsolidationCodeSubQuery(CostCategoryObjectLevel level) {
        Criteria crit = new Criteria();
        crit.addEqualTo(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, level.getChartOfAccountsCode());
        crit.addEqualTo(KFSPropertyConstants.FINANCIAL_OBJECT_LEVEL_CODE, level.getFinancialObjectLevelCode());
        crit.addEqualTo(KFSPropertyConstants.ACTIVE, Boolean.TRUE);
        ReportQueryByCriteria subQuery = QueryFactory.newReportQuery(ObjectLevel.class, crit);
        subQuery.setAttributes(new String[] { KFSPropertyConstants.FINANCIAL_CONSOLIDATION_OBJECT_CODE });
        return subQuery;
    }

    /**
     * @see org.kuali.kfs.module.ar.dataaccess.CostCategoryDao#isCostCategoryLevelUniqueAmongCodes(org.kuali.kfs.module.ar.businessobject.CostCategoryObjectLevel)
     */
    @Override
    public CostCategoryDetail retrieveMatchingCostCategoryLevelAmongCodes(CostCategoryObjectLevel level) {
        Criteria crit = new Criteria();
        if (!StringUtils.isBlank(level.getCategoryCode())) {
            crit.addNotEqualTo(ArPropertyConstants.CATEGORY_CODE, level.getCategoryCode());
        }
        crit.addEqualTo(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, level.getChartOfAccountsCode());
        crit.addEqualTo(KFSPropertyConstants.OBJECT_CODE_CURRENT+"."+KFSPropertyConstants.FINANCIAL_OBJECT_LEVEL+"."+KFSPropertyConstants.FINANCIAL_OBJECT_LEVEL_CODE, level.getFinancialObjectLevelCode());
        crit.addEqualTo(KFSPropertyConstants.ACTIVE, Boolean.TRUE);
        crit.addIn(ArPropertyConstants.CATEGORY_CODE, buildActiveCostCategorySubQuery());
        Query q = new QueryByCriteria(CostCategoryObjectCode.class, crit);
        return (CostCategoryDetail)TransactionalServiceUtils.retrieveFirstAndExhaustIterator(getPersistenceBrokerTemplate().getIteratorByQuery(q));
    }

    /**
     * @see org.kuali.kfs.module.ar.dataaccess.CostCategoryDao#isCostCategoryObjectCodeUnique(org.kuali.kfs.module.ar.businessobject.CostCategoryObjectCode)
     */
    @Override
    public CostCategoryDetail retrieveMatchingCostCategoryObjectCodeAmongCodes(CostCategoryObjectCode objectCode) {
        Criteria crit = new Criteria();
        if (!StringUtils.isBlank(objectCode.getCategoryCode())) {
            crit.addNotEqualTo(ArPropertyConstants.CATEGORY_CODE, objectCode.getCategoryCode());
        }
        crit.addEqualTo(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, objectCode.getChartOfAccountsCode());
        crit.addEqualTo(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, objectCode.getFinancialObjectCode());
        crit.addEqualTo(KFSPropertyConstants.ACTIVE, Boolean.TRUE);
        crit.addIn(ArPropertyConstants.CATEGORY_CODE, buildActiveCostCategorySubQuery());
        Query uniqueQuery = new QueryByCriteria(CostCategoryObjectCode.class, crit);
        return (CostCategoryDetail)TransactionalServiceUtils.retrieveFirstAndExhaustIterator(getPersistenceBrokerTemplate().getIteratorByQuery(uniqueQuery));
    }

    /**
     * @see org.kuali.kfs.module.ar.dataaccess.CostCategoryDao#isCostCategoryObjectCodeUniqueAmongLevels(org.kuali.kfs.module.ar.businessobject.CostCategoryObjectCode)
     */
    @Override
    public CostCategoryDetail retrieveMatchingCostCategoryObjectCodeAmongLevels(CostCategoryObjectCode objectCode) {
        Criteria objectLevelCrit = new Criteria();
        if (!StringUtils.isBlank(objectCode.getCategoryCode())) {
            objectLevelCrit.addNotEqualTo(ArPropertyConstants.CATEGORY_CODE, objectCode.getCategoryCode());
        }
        objectLevelCrit.addEqualTo(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, objectCode.getChartOfAccountsCode());
        objectLevelCrit.addEqualTo(KFSPropertyConstants.FINANCIAL_OBJECT_LEVEL_CODE, buildObjectCodeLevelCodeSubQuery(objectCode));
        objectLevelCrit.addEqualTo(KFSPropertyConstants.ACTIVE, Boolean.TRUE);
        objectLevelCrit.addIn(ArPropertyConstants.CATEGORY_CODE, buildActiveCostCategorySubQuery());
        Query objectLevelQuery = new QueryByCriteria(CostCategoryObjectLevel.class, objectLevelCrit);
        return (CostCategoryDetail)TransactionalServiceUtils.retrieveFirstAndExhaustIterator(getPersistenceBrokerTemplate().getIteratorByQuery(objectLevelQuery));
    }

    /**
     * Builds a subquery - so we can perform the whole operation as a single query - which finds the object level code for an object code
     * @param objectCode the cost category object code to find the object level code for
     * @return subQuery to use in a larger query
     */
    protected ReportQueryByCriteria buildObjectCodeLevelCodeSubQuery(CostCategoryObjectCode objectCode) {
        Criteria crit = new Criteria();
        crit.addEqualTo(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, objectCode.getChartOfAccountsCode());
        crit.addEqualTo(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, objectCode.getFinancialObjectCode());
        ReportQueryByCriteria subQuery = QueryFactory.newReportQuery(ObjectCodeCurrent.class, crit);
        subQuery.setAttributes(new String[] { KFSPropertyConstants.FINANCIAL_OBJECT_LEVEL_CODE } );
        return subQuery;
    }

    /**
     * @see org.kuali.kfs.module.ar.dataaccess.CostCategoryDao#isCostCategoryObjectCodeUniqueAmongConsolidations(org.kuali.kfs.module.ar.businessobject.CostCategoryObjectCode)
     */
    @Override
    public CostCategoryDetail retrieveCostCategoryObjectCodeAmongConsolidations(CostCategoryObjectCode objectCode) {
        Criteria objectConsolidationCrit = new Criteria();
        if (!StringUtils.isBlank(objectCode.getCategoryCode())) {
            objectConsolidationCrit.addNotEqualTo(ArPropertyConstants.CATEGORY_CODE, objectCode.getCategoryCode());
        }
        objectConsolidationCrit.addEqualTo(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, objectCode.getChartOfAccountsCode());
        objectConsolidationCrit.addEqualTo(KFSPropertyConstants.FIN_CONSOLIDATION_OBJECT_CODE, buildObjectCodeConsolidationCodeSubQuery(objectCode));
        objectConsolidationCrit.addEqualTo(KFSPropertyConstants.ACTIVE, Boolean.TRUE);
        objectConsolidationCrit.addIn(ArPropertyConstants.CATEGORY_CODE, buildActiveCostCategorySubQuery());
        Query objectConsolidationQuery = new QueryByCriteria(CostCategoryObjectConsolidation.class, objectConsolidationCrit);
        return (CostCategoryDetail)TransactionalServiceUtils.retrieveFirstAndExhaustIterator((getPersistenceBrokerTemplate().getIteratorByQuery(objectConsolidationQuery)));
    }

    /**
     * Builds a subquery - so we can perform the whole operation as a single query - which finds the object consolidation code for an object code
     * @param objectCode the cost category object code to find the object level code for
     * @return subQuery to use in a larger query
     */
    protected ReportQueryByCriteria buildObjectCodeConsolidationCodeSubQuery(CostCategoryObjectCode objectCode) {
        Criteria crit = new Criteria();
        crit.addEqualTo(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, objectCode.getChartOfAccountsCode());
        crit.addEqualTo(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, objectCode.getFinancialObjectCode());
        ReportQueryByCriteria subQuery = QueryFactory.newReportQuery(ObjectCodeCurrent.class, crit);
        subQuery.setAttributes(new String[] { KFSPropertyConstants.FINANCIAL_OBJECT_LEVEL+"."+KFSPropertyConstants.FINANCIAL_CONSOLIDATION_OBJECT_CODE } );
        return subQuery;
    }

    /**
     * @see org.kuali.kfs.module.ar.dataaccess.CostCategoryDao#getBalancesForCostCategory(java.lang.Integer, java.lang.String, java.lang.String, java.lang.String, java.lang.String, org.kuali.kfs.module.ar.businessobject.CostCategory)
     */
    @Override
    public List<Balance> getBalancesForCostCategory(Integer fiscalYear, String chartOfAccountsCode, String accountNumber, String balanceType, String objectType, CostCategory costCategory) {
        if (!costCategory.isActive()) {
            return new ArrayList<Balance>(); // cost category isn't active?  then dont' find any matching balances
        }

        Criteria crit = new Criteria();
        crit.addEqualTo(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, chartOfAccountsCode);
        crit.addEqualTo(KFSPropertyConstants.ACCOUNT_NUMBER, accountNumber);
        crit.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, fiscalYear);
        crit.addEqualTo(KFSPropertyConstants.BALANCE_TYPE_CODE, balanceType);
        crit.addEqualTo(KFSPropertyConstants.OBJECT_TYPE_CODE, objectType);

        Criteria objectCodesCollectiveCriteria = new Criteria();

        Criteria objectCodeCriteria = new Criteria();
        objectCodeCriteria.addIn(KFSPropertyConstants.OBJECT_CODE, buildObjectCodesOfCostCategorySubQuery(costCategory, chartOfAccountsCode));
        objectCodesCollectiveCriteria.addOrCriteria(objectCodeCriteria);

        Criteria objectLevelCriteria = new Criteria();
        objectLevelCriteria.addIn(KFSPropertyConstants.FINANCIAL_OBJECT+"."+KFSPropertyConstants.FINANCIAL_OBJECT_LEVEL_CODE,buildObjectLevelsOfCostCategorySubQuery(costCategory, chartOfAccountsCode));
        objectCodesCollectiveCriteria.addOrCriteria(objectLevelCriteria);

        Criteria consolidationCriteria = new Criteria();
        consolidationCriteria.addIn(KFSPropertyConstants.FINANCIAL_OBJECT+"."+KFSPropertyConstants.FINANCIAL_OBJECT_LEVEL+"."+KFSPropertyConstants.FINANCIAL_CONSOLIDATION_OBJECT_CODE, buildObjectConsolidationsOfCostCategorySubQuery(costCategory, chartOfAccountsCode));
        objectCodesCollectiveCriteria.addOrCriteria(consolidationCriteria);

        crit.addAndCriteria(objectCodesCollectiveCriteria);
        Query q = new QueryByCriteria(Balance.class, crit);
        return (List<Balance>)getPersistenceBrokerTemplate().getCollectionByQuery(q);
    }

    /**
     * Builds a subquery to find the object codes which match the given cost category within the given chart of accounts
     * @param costCategory the costCategory to find object codes for
     * @param chartOfAccountsCode the chart that object codes should be limited to
     * @return subQuery the subequery to find matching object codes
     */
    protected ReportQueryByCriteria buildObjectCodesOfCostCategorySubQuery(CostCategory costCategory, String chartOfAccountsCode) {
        Criteria crit = new Criteria();
        crit.addEqualTo(ArPropertyConstants.CATEGORY_CODE, costCategory.getCategoryCode());
        crit.addEqualTo(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, chartOfAccountsCode);
        crit.addEqualTo(KFSPropertyConstants.ACTIVE, Boolean.TRUE);
        ReportQueryByCriteria subQuery = QueryFactory.newReportQuery(CostCategoryObjectCode.class, crit);
        subQuery.setAttributes(new String[] { KFSPropertyConstants.FINANCIAL_OBJECT_CODE } );
        return subQuery;
    }

    /**
     * Builds a subquery to find the object levels which match the given cost category within the given chart of accounts
     * @param costCategory the costCategory to find object lvels for
     * @param chartOfAccountsCode the chart that object codes should be limited to
     * @return subQuery the subequery to find matching object levels
     */
    protected ReportQueryByCriteria buildObjectLevelsOfCostCategorySubQuery(CostCategory costCategory, String chartOfAccountsCode) {
        Criteria crit = new Criteria();
        crit.addEqualTo(ArPropertyConstants.CATEGORY_CODE, costCategory.getCategoryCode());
        crit.addEqualTo(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, chartOfAccountsCode);
        crit.addEqualTo(KFSPropertyConstants.ACTIVE, Boolean.TRUE);
        ReportQueryByCriteria subQuery = QueryFactory.newReportQuery(CostCategoryObjectLevel.class, crit);
        subQuery.setAttributes(new String[] { KFSPropertyConstants.FINANCIAL_OBJECT_LEVEL_CODE } );
        return subQuery;
    }

    /**
     * Builds a subquery to find the object consolidations which match the given cost category within the given chart of accounts
     * @param costCategory the costCategory to find object consolidations for
     * @param chartOfAccountsCode the chart that object consolidations should be limited to
     * @return subQuery the subequery to find matching object consolidations
     */
    protected ReportQueryByCriteria buildObjectConsolidationsOfCostCategorySubQuery(CostCategory costCategory, String chartOfAccountsCode) {
        Criteria crit = new Criteria();
        crit.addEqualTo(ArPropertyConstants.CATEGORY_CODE, costCategory.getCategoryCode());
        crit.addEqualTo(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, chartOfAccountsCode);
        crit.addEqualTo(KFSPropertyConstants.ACTIVE, Boolean.TRUE);
        ReportQueryByCriteria subQuery = QueryFactory.newReportQuery(CostCategoryObjectConsolidation.class, crit);
        subQuery.setAttributes(new String[] { KFSPropertyConstants.FIN_CONSOLIDATION_OBJECT_CODE } );
        return subQuery;
    }
}