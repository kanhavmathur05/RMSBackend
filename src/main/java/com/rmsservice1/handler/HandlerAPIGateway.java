package com.rmsservice1.handler;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rmsservice1.Rmsservice1Application;
import com.rmsservice1.modal.PolicyDocuments;
import com.rmsservice1.repository.PolicyDocumentsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class HandlerAPIGateway implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static ApplicationContext applicationContext = SpringApplication.run(Rmsservice1Application.class);
    private PolicyDocumentsRepository policyDocumentsRepository;

    Logger log = LoggerFactory.getLogger(HandlerAPIGateway.class);

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        this.policyDocumentsRepository = applicationContext.getBean(PolicyDocumentsRepository.class);

        ObjectMapper objectMapper = new ObjectMapper();
        String method = input.getHttpMethod();
        String resource = input.getResource();
        Map<String, String> pathParams = input.getPathParameters();
        Map<String, String> queryParams = input.getQueryStringParameters();

        if (resource.equals("/uploadDocuments")) {
            String policyNumber = "1222";

            String policyType = "HealthInsurance";
//            String policyType = "PropertyInsurance";
//            String policyType = "VehicleInsurance";

            PolicyDocuments policyDocumentsDetails;
            if (policyDocumentsRepository.getDocumentByPolicyNumberList(policyNumber).size() != 0) {
                policyDocumentsDetails = policyDocumentsRepository.getDocumentByPolicyNumber(policyNumber);
            } else {
                policyDocumentsDetails = new PolicyDocuments();
                policyDocumentsDetails.setPolicyNumber(policyNumber);
                policyDocumentsDetails.setPolicyType(policyType);
                policyDocumentsDetails.setAddedAt(LocalDate.now().toString());
            }
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_PDF, MediaType.APPLICATION_OCTET_STREAM));
            HttpEntity<String> entity = new HttpEntity<>(headers);

            if (queryParams.get("vehicleRc") != null) {
                ResponseEntity<byte[]> response = restTemplate.exchange("http://192.168.43.80:8080/api/getvehiclerc", HttpMethod.GET, entity, byte[].class);

                String objectKey = generateObjectKey(response.getHeaders().getContentType().toString(), policyType, policyNumber);    //To add policy Type

                uploadDocument(objectKey, response);

                List<String> rcList;
                if (policyDocumentsDetails.getVehicleRc() == null) {
                    rcList = new ArrayList<>();
                } else {
                    rcList = policyDocumentsDetails.getVehicleRc();
                }

                rcList.add(objectKey);
                policyDocumentsDetails.setVehicleRc(rcList);
            }
            if (queryParams.get("healthRecord") != null) {
                ResponseEntity<byte[]> response = restTemplate.exchange("http://192.168.43.80:8080/api/gethealthrecord", HttpMethod.GET, entity, byte[].class);

                String objectKey = generateObjectKey(response.getHeaders().getContentType().toString(), policyType, policyNumber);    //To add policy Type

                uploadDocument(objectKey, response);

                List<String> healthRecordList;
                if (policyDocumentsDetails.getHealthRecord() == null) {
                    healthRecordList = new ArrayList<>();
                } else {
                    healthRecordList = policyDocumentsDetails.getHealthRecord();
                }

                healthRecordList.add(objectKey);
                policyDocumentsDetails.setHealthRecord(healthRecordList);
            }

            if (queryParams.get("propertyDocument") != null) {
                ResponseEntity<byte[]> response = restTemplate.exchange("http://192.168.43.80:8080/api/getpropertydocument", HttpMethod.GET, entity, byte[].class);

                String objectKey = generateObjectKey(response.getHeaders().getContentType().toString(), policyType, policyNumber);    //To add policy Type

                uploadDocument(objectKey, response);

                List<String> propertyDocumentsList;
                if (policyDocumentsDetails.getPropertyDocuments() == null) {
                    propertyDocumentsList = new ArrayList<>();
                } else {
                    propertyDocumentsList = policyDocumentsDetails.getPropertyDocuments();
                }

                propertyDocumentsList.add(objectKey);
                policyDocumentsDetails.setPropertyDocuments(propertyDocumentsList);
            }

            if (queryParams.get("policyDocument") != null) {
                ResponseEntity<byte[]> response = restTemplate.exchange("http://192.168.43.80:8080/api/getpolicydocument", HttpMethod.GET, entity, byte[].class);

                String objectKey = generateObjectKey(response.getHeaders().getContentType().toString(), policyType, policyNumber);    //To add policy Type

                uploadDocument(objectKey, response);

                List<String> policyDocumentList;
                if (policyDocumentsDetails.getPolicyDocument() == null) {
                    policyDocumentList = new ArrayList<>();
                } else {
                    policyDocumentList = policyDocumentsDetails.getPolicyDocument();
                }

                policyDocumentList.add(objectKey);
                policyDocumentsDetails.setPolicyDocument(policyDocumentList);
            }

            if (queryParams.get("addressProof") != null) {
                ResponseEntity<byte[]> response = restTemplate.exchange("http://192.168.43.80:8080/api/getidproof", HttpMethod.GET, entity, byte[].class);

                String objectKey = generateObjectKey(response.getHeaders().getContentType().toString(), policyType, policyNumber);    //To add policy Type

                uploadDocument(objectKey, response);

                List<String> addressProofList;
                if (policyDocumentsDetails.getAddressProof() == null) {
                    addressProofList = new ArrayList<>();
                } else {
                    addressProofList = policyDocumentsDetails.getAddressProof();
                }

                addressProofList.add(objectKey);
                policyDocumentsDetails.setAddressProof(addressProofList);
            }

            if (queryParams.get("idProof") != null) {
                ResponseEntity<byte[]> response = restTemplate.exchange("http://192.168.43.80:8080/api/getidproof", HttpMethod.GET, entity, byte[].class);

                String objectKey = generateObjectKey(response.getHeaders().getContentType().toString(), policyType, policyNumber);    //To add policy Type

                uploadDocument(objectKey, response);

                List<String> idProofList;
                if (policyDocumentsDetails.getIdProof() == null) {
                    idProofList = new ArrayList<>();
                } else {
                    idProofList = policyDocumentsDetails.getIdProof();
                }

                idProofList.add(objectKey);
                policyDocumentsDetails.setIdProof(idProofList);
            }

            policyDocumentsRepository.savePolicyDocuments(policyDocumentsDetails);
            return buildResponse(200, "Data Inserted Successfully");
        } else if (resource.equals("/getdocuments") && queryParams != null) {
            String result;
            if (queryParams.get("docsBefore") != null && queryParams.get("docsAfter") != null && queryParams.get("policyType") != null) {
                result = makeJSONStringFromObject(objectMapper, policyDocumentsRepository.getDocumentByInsurancePolicyTypeAndDateRange(queryParams.get("policyType"), queryParams.get("docsBefore"), queryParams.get("docsAfter")));
                return buildResponse(200, result);
            } else {
                result = makeJSONStringFromObject(objectMapper, policyDocumentsRepository.getDocumentByInsurancePolicyType(queryParams.get("policyType")));
                return buildResponse(200, result);
            }
        } else if (resource.equals("/getobjecturl") && queryParams != null) {
            return buildResponse(200, getObjectUrl(queryParams.get("objectKey")));
        }
        return buildResponse(404, "Not Found");
    }

    public static String generateObjectKey(String contentType, String policyType, String policyNumber) {
        String date = LocalDate.now().toString().replace("-", "");
        String time = LocalTime.now().toString().replace(".", "").replace(":", "");
        String extension = contentType.substring(contentType.indexOf("/") + 1, contentType.length());
        if (extension == "jpeg") {
            extension = extension.replace("e", "");
        }
        return policyNumber + "_" + policyType + "_" + date + time + "." + extension;
    }

    public static String uploadDocument(String objectKey, ResponseEntity<byte[]> response) {
        try {
            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                    .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials("access key", "secret key")))
                    .withRegion(Regions.AP_SOUTH_1)
                    .build();

            File file = new File("D:\\RecordManagementSystem\\rmsservice1\\src\\main\\java\\com\\rmsservice1\\mockdata\\temp");

            OutputStream os = new FileOutputStream(file);
            os.write(response.getBody());
            os.close();

            PutObjectRequest request = new PutObjectRequest("bucket name", objectKey, file);
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(response.getHeaders().getContentType().toString());
            request.setMetadata(metadata);
            s3Client.putObject(request);
            return "Document Uploaded Successfully";
        } catch (AmazonServiceException | FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Document Not Uploaded, Something went wrong!";
    }

    public static String getObjectUrl(String objectKey) {
        try {
            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                    .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials("access key", "secret key")))
                    .withRegion(Regions.AP_SOUTH_1)
                    .build();

            java.util.Date expiration = new java.util.Date();
            long expTimeMillis = Instant.now().toEpochMilli();
            expTimeMillis += 1000 * 60 * 60 * 60;
            expiration.setTime(expTimeMillis);

            GeneratePresignedUrlRequest generatePresignedUrlRequest =
                    new GeneratePresignedUrlRequest("newrmsbucket", objectKey)
                            .withMethod(com.amazonaws.HttpMethod.GET)
                            .withExpiration(expiration);
            URL url = s3Client.generatePresignedUrl(generatePresignedUrlRequest);


            return url.toString();

        } catch (AmazonServiceException e) {
            e.printStackTrace();
        } catch (SdkClientException e) {
            e.printStackTrace();
        }
        return "Something went Wrong";
    }

    public static List<Map<String, AttributeValue>> getListOfPoliciesByDate(String policyType, String docsBefore, String docsAfter) {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration("db endpoint", "region"))
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials("access key", "secret key")))
                .build();

        Map<String, AttributeValue> expressionAttributeValues = new HashMap<String, AttributeValue>();
        expressionAttributeValues.put(":val1", new AttributeValue().withS(docsAfter));
        expressionAttributeValues.put(":val2", new AttributeValue().withS(docsBefore));
        expressionAttributeValues.put(":val3", new AttributeValue().withS(policyType));

        ScanRequest scanRequest = new ScanRequest()
                .withTableName("policyDocuments")
                .withFilterExpression("addedAt >= :val1 AND addedAt <= :val2 AND policyType = :val3")
                .withExpressionAttributeValues(expressionAttributeValues);
        ScanResult result = client.scan(scanRequest);
        return result.getItems();
    }

    public static APIGatewayProxyResponseEvent buildResponse(int status, String obj) {
        APIGatewayProxyResponseEvent res = new APIGatewayProxyResponseEvent();
        res.setStatusCode(status);
        Map<String, String> map = new HashMap<String, String>();
        map.put("Content-Type", "application/json");
        map.put("Access-Control-Allow-Headers", "*");
        map.put("Access-Control-Allow-Origin", "*");
        map.put("Access-Control-Allow-Methods", "*");
        res.withHeaders(map);
        res.setBody(obj);
        return res;
    }

    public static String makeJSONStringFromObject(ObjectMapper objectMapper, Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e1) {
            e1.printStackTrace();
            return "";
        }
    }
}