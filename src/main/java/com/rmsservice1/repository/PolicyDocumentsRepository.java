package com.rmsservice1.repository;

import com.rmsservice1.modal.PolicyDocuments;

import java.util.List;

public interface PolicyDocumentsRepository {

    PolicyDocuments savePolicyDocuments(PolicyDocuments policyDocuments);

    List<PolicyDocuments> getDocumentByPolicyNumberList(String policyNumber);

    PolicyDocuments getDocumentByPolicyNumber(String policyNumber);

    List<PolicyDocuments> getDocumentByInsurancePolicyType(String policyType);

    List<PolicyDocuments> getDocumentByInsurancePolicyTypeAndDateRange(String policyType,String docsBefore,String docsAfter);
}
