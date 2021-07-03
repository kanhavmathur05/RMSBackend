package com.rmsservice1.handler;


import com.rmsservice1.modal.PolicyDocuments;
import com.rmsservice1.repository.PolicyDocumentsRepository;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RunWith(MockitoJUnitRunner.class)
public class PolicyDocumentsRepositoryTest {

    Logger log= LoggerFactory.getLogger(PolicyDocumentsRepositoryTest.class);

    @Mock
    PolicyDocumentsRepository policyDocumentsRepository;

    @Before
    public void setUp(){

    }

    @Test
    public void addPolicyDocumentsDetails() {
        PolicyDocuments policyDocuments=new PolicyDocuments();
        policyDocuments.setPolicyDocument(Arrays.asList("Key 1","Key 2"));
        policyDocuments.setPropertyDocuments(null);
        policyDocuments.setPolicyNumber("1234");
        policyDocuments.setPolicyType("HealthInsurance");
        policyDocuments.setIdProof(Arrays.asList("Key 1","Key 2"));
        policyDocuments.setHealthRecord(Arrays.asList("Key 1","Key 2"));
        policyDocuments.setVehicleRc(null);
        policyDocuments.setAddedAt(LocalDate.now().toString());

        Mockito.when(policyDocumentsRepository.savePolicyDocuments(policyDocuments)).thenReturn(policyDocuments);

        log.info("Policy Document Details:- ",Stream.of(policyDocumentsRepository.savePolicyDocuments(policyDocuments)));

        Assert.assertEquals(policyDocuments,policyDocumentsRepository.savePolicyDocuments(policyDocuments));

    }

    @Test
    public void getPolicyDocumentsByPolicyPolicyType() {
        String policyType="PropertyInsurance";

        List<PolicyDocuments> vehicleInsurancePolicies=Stream.of(new PolicyDocuments("1","1234",null,null,null,null,null,null,"VehicleInsurance","2021-06-18",""),
                new PolicyDocuments("2","9234",null,null,null,null,null,null,"VehicleInsurance","2021-06-18","")).collect(Collectors.toList());

        List<PolicyDocuments> healthInsurancePolicies=Stream.of(new PolicyDocuments("1","1234",null,null,null,null,null,null,"HealthInsurance","2021-06-18",""),
                new PolicyDocuments("2","2234",null,null,null,null,null,null,"HealthInsurance","2021-06-18",""),
                new PolicyDocuments("3","3234",null,null,null,null,null,null,"HealthInsurance","2021-06-18",""),
                new PolicyDocuments("4","4234",null,null,null,null,null,null,"HealthInsurance","2021-06-18","")).collect(Collectors.toList());
        List<PolicyDocuments> propertyInsurancePolicies=Stream.of(new PolicyDocuments("1","8234",null,null,null,null,null,null,"HealthInsurance","2021-06-18",""),
                new PolicyDocuments("2","6234",null,null,null,null,null,null,"HealthInsurance","2021-06-18",""),
                new PolicyDocuments("3","7234",null,null,null,null,null,null,"HealthInsurance","2021-06-18","")).collect(Collectors.toList());
        List<PolicyDocuments> generalInsurancePolicies=Stream.of(new PolicyDocuments("1","8234",null,null,null,null,null,null,"HealthInsurance","2021-06-18",""),
                new PolicyDocuments("2","6234",null,null,null,null,null,null,"HealthInsurance","2021-06-18",""),
                new PolicyDocuments("3","7234",null,null,null,null,null,null,"HealthInsurance","2021-06-18","")).collect(Collectors.toList());

        Mockito.when(policyDocumentsRepository.getDocumentByInsurancePolicyType("VehicleInsurance")).thenReturn(vehicleInsurancePolicies);
        Mockito.when(policyDocumentsRepository.getDocumentByInsurancePolicyType("HealthInsurance")).thenReturn(healthInsurancePolicies);
        Mockito.when(policyDocumentsRepository.getDocumentByInsurancePolicyType("PropertyInsurance")).thenReturn(propertyInsurancePolicies);
        Mockito.when(policyDocumentsRepository.getDocumentByInsurancePolicyType("GeneralInsurance")).thenReturn(generalInsurancePolicies);

        List<PolicyDocuments> result = policyDocumentsRepository.getDocumentByInsurancePolicyType(policyType);

//        Assert.assertEquals(vehicleInsurancePolicies,result);
//        Assert.assertEquals(healthInsurancePolicies,result);
        Assert.assertEquals(propertyInsurancePolicies,result);
//        Assert.assertEquals(generalInsurancePolicies,result);
    }



    @After
    public void tearDown() throws Exception {

    }
}