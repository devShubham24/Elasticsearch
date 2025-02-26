package com.sb.main.ResponseAudit;

import java.util.List;

import com.sb.main.auditDTO.AuditLogDTO;

public class AuditLogResponseDTO {
    private int totalHits;
    private int totalPages;  // Add this variable
    private List<AuditLogDTO> auditLogs;

    public int getTotalHits() {
        return totalHits;
    }
    public void setTotalHits(int totalHits) {
        this.totalHits = totalHits;
    }

    public int getTotalPages() {  // Add this getter
        return totalPages;
    }
    public void setTotalPages(int totalPages) {  // Add this setter
        this.totalPages = totalPages;
    }

    public List<AuditLogDTO> getAuditLogs() {
        return auditLogs;
    }
    public void setAuditLogs(List<AuditLogDTO> auditLogs) {
        this.auditLogs = auditLogs;
    }

    public AuditLogResponseDTO(int totalHits, int totalPages, List<AuditLogDTO> auditLogs) { // Modify constructor
        super();
        this.totalHits = totalHits;
        this.totalPages = totalPages;
        this.auditLogs = auditLogs;
    }

    public AuditLogResponseDTO() {
        super();
    }
}
