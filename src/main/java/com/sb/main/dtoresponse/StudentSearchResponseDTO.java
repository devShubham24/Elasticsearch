package com.sb.main.dtoresponse;

import java.util.List;

import com.sb.main.dtoStudent.StudentDTO;

public class StudentSearchResponseDTO {

    private int totalHits;
    private List<StudentDTO> students;

    // Getters and Setters
    public int getTotalHits() {
        return totalHits;
    }

    public void setTotalHits(int totalHits) {
        this.totalHits = totalHits;
    }

    public List<StudentDTO> getStudents() {
        return students;
    }

    public void setStudents(List<StudentDTO> students) {
        this.students = students;
    }
}