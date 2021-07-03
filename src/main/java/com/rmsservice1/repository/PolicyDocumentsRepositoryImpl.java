package com.rmsservice1.repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.rmsservice1.modal.PolicyDocuments;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class PolicyDocumentsRepositoryImpl implements PolicyDocumentsRepository {

    @Autowired
    DynamoDBMapper mapper;

    @Override
    public PolicyDocuments savePolicyDocuments(PolicyDocuments policyDocuments) {
        mapper.save(policyDocuments);
        return policyDocuments;
    }

    @Override
    public List<PolicyDocuments> getDocumentByPolicyNumberList(String policyNumber) {
        Condition condition = new Condition();
        condition.withComparisonOperator(ComparisonOperator.EQ)
                .withAttributeValueList(new AttributeValue().withS(policyNumber));

        DynamoDBScanExpression scanExpr = new DynamoDBScanExpression();
        scanExpr.withFilterConditionEntry("policyNumber", condition);
        return mapper.scan(PolicyDocuments.class, scanExpr);
    }

    @Override
    public PolicyDocuments getDocumentByPolicyNumber(String policyNumber) {
        Condition condition = new Condition();
        condition.withComparisonOperator(ComparisonOperator.EQ)
                .withAttributeValueList(new AttributeValue().withS(policyNumber));

        DynamoDBScanExpression scanExpr = new DynamoDBScanExpression();
        scanExpr.withFilterConditionEntry("policyNumber", condition);
        return mapper.scan(PolicyDocuments.class, scanExpr).get(0);
    }

    @Override
    public List<PolicyDocuments> getDocumentByInsurancePolicyType(String policyType) {
        Condition condition = new Condition();
        condition.withComparisonOperator(ComparisonOperator.EQ)
                .withAttributeValueList(new AttributeValue().withS(policyType));

        DynamoDBScanExpression scanExpr = new DynamoDBScanExpression();
        scanExpr.withFilterConditionEntry("policyType", condition);
        return mapper.scan(PolicyDocuments.class, scanExpr);
    }

    @Override
    public List<PolicyDocuments> getDocumentByInsurancePolicyTypeAndDateRange(String policyType, String docsBefore, String docsAfter) {
        Map<String, AttributeValue> expressionAttributeValues = new HashMap<String, AttributeValue>();
        expressionAttributeValues.put(":val1", new AttributeValue().withS(docsAfter));
        expressionAttributeValues.put(":val2", new AttributeValue().withS(docsBefore));
        expressionAttributeValues.put(":val3", new AttributeValue().withS(policyType));

        List<PolicyDocuments> listOfPolicies = mapper.scanPage(PolicyDocuments.class, new DynamoDBScanExpression().withFilterExpression("addedAt >= :val1 AND addedAt <= :val2 AND policyType = :val3").withExpressionAttributeValues(expressionAttributeValues)).getResults();

        return listOfPolicies;
    }
}
