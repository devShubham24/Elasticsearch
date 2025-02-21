package com.sb.main.Dao;

import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sb.main.ResponseAudit.AuditLogResponseDTO;
import com.sb.main.auditDTO.AuditLogDTO;
import com.sb.main.dtoStudent.StudentDTO;
import com.sb.main.dtoSubject.SubjectDTO;
import com.sb.main.dtoresponse.StudentSearchResponseDTO;
import com.sb.main.uniqueDTO.auditResponseDto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

@Repository
public class ProductDAO {
    private static final Logger logger = LoggerFactory.getLogger(ProductDAO.class);

    @Autowired
    private RestClient restClient;

    public Map<String, Object> getAggregatedPriceData() {
        Map<String, Object> result = new HashMap<>();

        try {
            // Construct Elasticsearch Aggregation Query Using JSONObject
            JSONObject jsonQuery = new JSONObject();

            // Average Price Aggregation
            JSONObject avgO = new JSONObject();
            avgO.put("field", "price");

            JSONObject avg_price1 = new JSONObject();
            avg_price1.put("avg", avgO);

            // Aggregations Main
            JSONObject aggsMain = new JSONObject();
            aggsMain.put("average_price", avg_price1);

            // Sum Price Aggregation
            JSONObject sum1 = new JSONObject();
            sum1.put("field", "price");
            JSONObject totalprice = new JSONObject();
            totalprice.put("sum", sum1);
            aggsMain.put("total_price", totalprice);

            // Final query structure
            jsonQuery.put("size", 0); // No need for document hits, only aggregations
            jsonQuery.put("aggs", aggsMain);

            System.out.println("Query is::"+jsonQuery);
            // Send Request to Elasticsearch
            Request request = new Request("POST", "/products/_search");
            request.setJsonEntity(jsonQuery.toString());
            Response response = restClient.performRequest(request);

            // Parse Response JSON
            String jsonString = EntityUtils.toString(response.getEntity());
            JSONObject jsonResponse = new JSONObject(jsonString);
            logger.info("Elasticsearch Response: {}", jsonString);

            // Extract Aggregation Results
            JSONObject aggregations = jsonResponse.getJSONObject("aggregations");

            // Handle the value for average_price
            double averagePrice = aggregations.getJSONObject("average_price").getDouble("value");
            result.put("average_price", averagePrice);

            // Handle the value for total_price, which is an object with "value"
            JSONObject totalPriceObject = aggregations.getJSONObject("total_price");
            double totalPrice = totalPriceObject.getDouble("value");
            result.put("total_price", totalPrice);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }


    public StudentSearchResponseDTO searchStudents() {
        StudentSearchResponseDTO responseDTO = new StudentSearchResponseDTO();
        List<StudentDTO> studentList = new ArrayList<>();

        try {
            // Construct the query object
            JSONObject matchCondition = new JSONObject();
            matchCondition.put("subjects.subject_name", "Science");

            JSONObject matchQuery = new JSONObject();
            matchQuery.put("match", matchCondition);

            JSONObject rangeCondition = new JSONObject();
            rangeCondition.put("gte", 80);

            JSONObject rangeQuery = new JSONObject();
            rangeQuery.put("subjects.marks", rangeCondition);

            JSONObject rangeWrapper = new JSONObject();
            rangeWrapper.put("range", rangeQuery);

            JSONArray mustConditions = new JSONArray();
            mustConditions.put(matchQuery);
            mustConditions.put(rangeWrapper);

            JSONObject boolQuery = new JSONObject();
            boolQuery.put("must", mustConditions);

            JSONObject queryObject = new JSONObject();
            queryObject.put("bool", boolQuery);

            JSONObject nestedQueryWrapper = new JSONObject();
            nestedQueryWrapper.put("path", "subjects");
            nestedQueryWrapper.put("query", queryObject);

            JSONObject nestedQuery = new JSONObject();
            nestedQuery.put("nested", nestedQueryWrapper);

            JSONObject jsonQuery = new JSONObject();
            jsonQuery.put("query", nestedQuery);

            // Send request to Elasticsearch
            Request request = new Request("POST", "/student/_search");
            request.setJsonEntity(jsonQuery.toString());
            Response response = restClient.performRequest(request);

            // Parse the response
            String jsonString = EntityUtils.toString(response.getEntity());
            JSONObject jsonResponse = new JSONObject(jsonString);

            // Extracting total hits
            JSONObject hitsObject = jsonResponse.getJSONObject("hits");
            int totalHits = hitsObject.getJSONObject("total").getInt("value");
            responseDTO.setTotalHits(totalHits);

            // Extracting actual student documents
            JSONArray hitsArray = hitsObject.getJSONArray("hits");

            // Iterate through hits and extract student data
            for (int i = 0; i < hitsArray.length(); i++) {
                JSONObject hit = hitsArray.getJSONObject(i);
                if (hit.has("_source")) {
                    JSONObject studentData = hit.getJSONObject("_source");

                    // Map student data to DTO
                    StudentDTO studentDTO = new StudentDTO();
                    studentDTO.setStudentId(studentData.getString("student_id"));
                    studentDTO.setName(studentData.getString("name"));
                    studentDTO.setAge(studentData.getInt("age"));

                    // Map subjects
                    List<SubjectDTO> subjects = new ArrayList<>();
                    JSONArray subjectArray = studentData.getJSONArray("subjects");
                    for (int j = 0; j < subjectArray.length(); j++) {
                        JSONObject subjectData = subjectArray.getJSONObject(j);
                        SubjectDTO subjectDTO = new SubjectDTO();
                        subjectDTO.setSubjectName(subjectData.getString("subject_name"));
                        subjectDTO.setMarks(subjectData.getInt("marks"));
                        subjects.add(subjectDTO);
                    }
                    studentDTO.setSubjects(subjects);

                    // Add studentDTO to list
                    studentList.add(studentDTO);
                }
            }

            // Set students in response DTO
            responseDTO.setStudents(studentList);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return responseDTO;
    }
    
    //------------------------------------------dynamic response ------------------------------------------
    
    
    
    public StudentSearchResponseDTO getStudentsWithMarksGreaterThan(int marks) {
        StudentSearchResponseDTO responseDTO = new StudentSearchResponseDTO();
        List<StudentDTO> studentList = new ArrayList<>();

        try {
            // Constructing Query using JSONObject
        	JSONObject query = new JSONObject();
            JSONObject nestedQuery = new JSONObject();
            JSONObject rangeQuery = new JSONObject();
            JSONObject marksRange = new JSONObject();

            marksRange.put("gt", marks);
            rangeQuery.put("subjects.marks", marksRange);
            nestedQuery.put("nested", new JSONObject()
                    .put("path", "subjects")
                    .put("query", new JSONObject().put("range", rangeQuery))
            );

            query.put("query", nestedQuery);

            // Sending Request to Elasticsearch
            Request request = new Request("GET", "/student/_search");
            request.setJsonEntity(query.toString());
            Response response = restClient.performRequest(request);

            // Parsing Response
            JSONObject jsonResponse = new JSONObject(new String(response.getEntity().getContent().readAllBytes()));
            JSONArray hitsArray = jsonResponse.getJSONObject("hits").getJSONArray("hits");

            // Extracting Data and Mapping to DTOs
            for (int i = 0; i < hitsArray.length(); i++) {
                JSONObject source = hitsArray.getJSONObject(i).getJSONObject("_source");

                // Extracting Student Info
                StudentDTO studentDTO = new StudentDTO();
                studentDTO.setStudentId(source.getString("student_id"));
                studentDTO.setName(source.getString("name"));
                studentDTO.setAge(source.getInt("age"));

                // Extracting Subjects
                JSONArray subjectsArray = source.getJSONArray("subjects");
                List<SubjectDTO> subjects = new ArrayList<>();
                for (int j = 0; j < subjectsArray.length(); j++) {
                    JSONObject subjectObj = subjectsArray.getJSONObject(j);
                    SubjectDTO subjectDTO = new SubjectDTO();
                    subjectDTO.setSubjectName(subjectObj.getString("subject_name"));
                    subjectDTO.setMarks(subjectObj.getInt("marks"));
                    subjects.add(subjectDTO);
                }
                studentDTO.setSubjects(subjects);

                studentList.add(studentDTO);
            }

            // Setting Response DTO
            responseDTO.setStudents(studentList);
            responseDTO.setTotalHits(studentList.size());
            logger.info("StudentList sige()"+responseDTO.getTotalHits());

        } catch (IOException e) {
            e.printStackTrace();
        }

        return responseDTO;
    }
    
    
 //-----------------------------------------------------AuditLog-------------------------------------------------------------------------------
    public AuditLogResponseDTO fetchAllAuditLogs() {
        AuditLogResponseDTO responseDTO = new AuditLogResponseDTO();
        List<AuditLogDTO> auditLogs = new ArrayList<>();

        try {
            // Construct the match-all query
            JSONObject matchAllQuery = new JSONObject();
            matchAllQuery.put("match_all", new JSONObject());

            JSONObject jsonQuery = new JSONObject();
            jsonQuery.put("query", matchAllQuery);

            // Send request to Elasticsearch
            Request request = new Request("POST", "/auditlog/_search");
            request.setJsonEntity(jsonQuery.toString());
            Response response = restClient.performRequest(request);

            // Parse the response
            String jsonString = EntityUtils.toString(response.getEntity());
            JSONObject jsonResponse = new JSONObject(jsonString);
            logger.info("Elasticsearch Response: {}"+ jsonString);

            // Extracting total hits
            JSONObject hitsObject = jsonResponse.getJSONObject("hits");
            int totalHits = hitsObject.getJSONObject("total").getInt("value");
            responseDTO.setTotalHits(totalHits);

            // Extracting actual audit log documents
            JSONArray hitsArray = hitsObject.getJSONArray("hits");

            for (int i = 0; i < hitsArray.length(); i++) {
                JSONObject hit = hitsArray.getJSONObject(i);
                if (hit.has("_source")) {
                    JSONObject logData = hit.getJSONObject("_source");

                    // Map log data to DTO
                    AuditLogDTO auditLogDTO = new AuditLogDTO();
                    auditLogDTO.setAuditLogId(logData.getInt("auditLogId"));
                    auditLogDTO.setPdmpUserId(logData.getInt("pdmpUserId"));
                    auditLogDTO.setAuditType(logData.getString("auditType"));
                    auditLogDTO.setAffectedUserId(logData.getInt("affectedUserId"));
                    auditLogDTO.setRoleId(logData.getInt("roleId"));
                    auditLogDTO.setFileId(logData.getInt("fileId"));
                    auditLogDTO.setAuditTypeId(logData.getInt("auditTypeId"));
                    auditLogDTO.setAuditSource(logData.getString("auditSource"));
                    auditLogDTO.setFirstName(logData.getString("firstName"));
                    auditLogDTO.setLastName(logData.getString("lastName"));
                    auditLogDTO.setGender(logData.getString("gender"));
                    auditLogDTO.setDateOfBirth(logData.getLong("dateOfBirth"));
                    auditLogDTO.setRoleName(logData.getString("roleName"));
                    auditLogDTO.setUserCategory(logData.getString("userCategory"));
                    auditLogDTO.setCreatedAt(logData.getString("createdAt"));
                    auditLogDTO.setUserEmail(logData.getString("userEmail"));

                    // Add to list
                    auditLogs.add(auditLogDTO);
                }
            }

            // Set audit logs in response DTO
            responseDTO.setAuditLogs(auditLogs);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return responseDTO;
    }
    //---------------------------------------------unique AuditLog------------------------------------------------------------------
    public auditResponseDto getUniqueAuditTypes() {
        List<String> auditTypes = new ArrayList<>();

        try {
            // Construct Elasticsearch Aggregation Query Using JSONObject
            JSONObject jsonQuery = new JSONObject();
            JSONObject termsAggregation = new JSONObject();
            termsAggregation.put("field", "auditType.keyword");
            termsAggregation.put("size", 1000); // Adjust size as needed

            JSONObject aggsMain = new JSONObject();
            aggsMain.put("unique_audit_types", new JSONObject().put("terms", termsAggregation));

            jsonQuery.put("size", 0); // No need for document hits, only aggregations
            jsonQuery.put("aggs", aggsMain);

            logger.info("Elasticsearch Query: {}", jsonQuery);

            // Send Request to Elasticsearch
            Request request = new Request("POST", "/auditlog/_search");
            request.setJsonEntity(jsonQuery.toString());
            Response response = restClient.performRequest(request);

            // Parse Response JSON
            String jsonString = EntityUtils.toString(response.getEntity());
            JSONObject jsonResponse = new JSONObject(jsonString);
            logger.info("Elasticsearch Response: {}", jsonString);

            // Extract Aggregation Results
            JSONObject aggregations = jsonResponse.getJSONObject("aggregations");
            JSONArray buckets = aggregations.getJSONObject("unique_audit_types").getJSONArray("buckets");

            // Iterate and extract unique auditType values
            for (int i = 0; i < buckets.length(); i++) {
                auditTypes.add(buckets.getJSONObject(i).getString("key"));
            }

        } catch (IOException e) {
            logger.error("Error fetching unique audit types from Elasticsearch", e);
        }

        return new auditResponseDto(auditTypes);
    }
  //------------------------------------------------------------------------------------Dashboard fielter----------------------------------------------------------------------------------
    public AuditLogResponseDTO fetchFilteredAuditLogs(String firstName, String lastName, String email, String auditType, String dateRange) {
        AuditLogResponseDTO responseDTO = new AuditLogResponseDTO();
        List<AuditLogDTO> auditLogs = new ArrayList<>();

        try {
            // Initialize JSON objects
            JSONObject jsonQuery = new JSONObject();
            JSONObject boolQuery = new JSONObject();
            JSONArray mustConditions = new JSONArray();

            // If no filters are provided, use match_all
            if ((firstName == null || firstName.isEmpty()) &&
                (lastName == null || lastName.isEmpty()) &&
                (email == null || email.isEmpty()) &&
                (auditType == null || auditType.isEmpty()) &&
                (dateRange == null || dateRange.isEmpty())) {

                JSONObject matchAllQuery = new JSONObject();
                jsonQuery.put("query", matchAllQuery.put("match_all", new JSONObject()));

            } else {
                // Add firstName condition
                if (firstName != null && !firstName.isEmpty()) {
                    JSONObject firstNameValue = new JSONObject();
                    firstNameValue.put("firstName", firstName);

                    JSONObject firstNameMatch = new JSONObject();
                    firstNameMatch.put("match", firstNameValue);

                    mustConditions.put(firstNameMatch);
                }

                // Add lastName condition
                if (lastName != null && !lastName.isEmpty()) {
                    JSONObject lastNameValue = new JSONObject();
                    lastNameValue.put("lastName", lastName);

                    JSONObject lastNameMatch = new JSONObject();
                    lastNameMatch.put("match", lastNameValue);

                    mustConditions.put(lastNameMatch);
                }

                // Add email condition
                if (email != null && !email.isEmpty()) {
                    JSONObject emailValue = new JSONObject();
                    emailValue.put("userEmail", email);

                    JSONObject emailMatch = new JSONObject();
                    emailMatch.put("match", emailValue);

                    mustConditions.put(emailMatch);
                }

                // Add auditType condition
                if (auditType != null && !auditType.isEmpty()) {
                    JSONObject auditTypeValue = new JSONObject();
                    auditTypeValue.put("auditType", auditType);

                    JSONObject auditTypeMatch = new JSONObject();
                    auditTypeMatch.put("match", auditTypeValue);

                    mustConditions.put(auditTypeMatch);
                }

                // Add date range condition
                if (dateRange != null && !dateRange.isEmpty()) {
                    String[] dates = dateRange.split(" - ");
                    if (dates.length == 2) {
                        SimpleDateFormat inputFormat = new SimpleDateFormat("MM/dd/yyyy");
                        SimpleDateFormat esFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
                        esFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

                        String startDate = esFormat.format(inputFormat.parse(dates[0])) + "+0000";
                        String endDate = esFormat.format(inputFormat.parse(dates[1])) + "+0000";

                        JSONObject dateRangeQuery = new JSONObject();
                        dateRangeQuery.put("gte", startDate);
                        dateRangeQuery.put("lte", endDate);

                        JSONObject rangeField = new JSONObject();
                        rangeField.put("createdAt", dateRangeQuery);

                        JSONObject rangeQuery = new JSONObject();
                        rangeQuery.put("range", rangeField);

                        mustConditions.put(rangeQuery);
                    }
                }

                // Build final query
                boolQuery.put("must", mustConditions);

                JSONObject boolContainer = new JSONObject();
                boolContainer.put("bool", boolQuery);

                jsonQuery.put("query", boolContainer);
            }

            logger.info("Elasticsearch Query: {}", jsonQuery.toString());

            Request request = new Request("POST", "/auditlog/_search");
            request.setJsonEntity(jsonQuery.toString());
            Response response = restClient.performRequest(request);

            String jsonString = EntityUtils.toString(response.getEntity());
            JSONObject jsonResponse = new JSONObject(jsonString);
            logger.info("Elasticsearch Response: {}", jsonString);

            JSONObject hitsObject = jsonResponse.getJSONObject("hits");
            int totalHits = hitsObject.getJSONObject("total").getInt("value");
            responseDTO.setTotalHits(totalHits);

            JSONArray hitsArray = hitsObject.getJSONArray("hits");
            for (int i = 0; i < hitsArray.length(); i++) {
                JSONObject hit = hitsArray.getJSONObject(i);
                if (hit.has("_source")) {
                    JSONObject logData = hit.getJSONObject("_source");

                    AuditLogDTO auditLogDTO = new AuditLogDTO();
                    auditLogDTO.setAuditLogId(logData.optInt("auditLogId", 0));
                    auditLogDTO.setPdmpUserId(logData.optInt("pdmpUserId", 0));
                    auditLogDTO.setAuditType(logData.optString("auditType", ""));
                    auditLogDTO.setAffectedUserId(logData.optInt("affectedUserId", 0));
                    auditLogDTO.setRoleId(logData.optInt("roleId", 0));
                    auditLogDTO.setFileId(logData.optInt("fileId", 0));
                    auditLogDTO.setAuditTypeId(logData.optInt("auditTypeId", 0));
                    auditLogDTO.setAuditSource(logData.optString("auditSource", ""));
                    auditLogDTO.setFirstName(logData.optString("firstName", ""));
                    auditLogDTO.setLastName(logData.optString("lastName", ""));
                    auditLogDTO.setGender(logData.optString("gender", ""));
                    auditLogDTO.setDateOfBirth(logData.optLong("dateOfBirth", 0));
                    auditLogDTO.setRoleName(logData.optString("roleName", ""));
                    auditLogDTO.setUserCategory(logData.optString("userCategory", ""));
                    auditLogDTO.setCreatedAt(logData.optString("createdAt", ""));
                    auditLogDTO.setUserEmail(logData.optString("userEmail", ""));

                    auditLogs.add(auditLogDTO);
                }
            }

            responseDTO.setAuditLogs(auditLogs);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return responseDTO;
    }

     }


