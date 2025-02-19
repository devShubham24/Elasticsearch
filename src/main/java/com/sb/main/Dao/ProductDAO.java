package com.sb.main.Dao;

import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sb.main.dtoStudent.StudentDTO;
import com.sb.main.dtoSubject.SubjectDTO;
import com.sb.main.dtoresponse.StudentSearchResponseDTO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

}
