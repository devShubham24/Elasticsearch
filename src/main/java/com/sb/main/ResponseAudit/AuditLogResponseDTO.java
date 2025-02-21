package com.sb.main.ResponseAudit;

import java.util.List;

import com.sb.main.auditDTO.AuditLogDTO;

public class AuditLogResponseDTO {
    private int totalHits;
    private List<AuditLogDTO> auditLogs;
	public int getTotalHits() {
		return totalHits;
	}
	public void setTotalHits(int totalHits) {
		this.totalHits = totalHits;
	}
	public List<AuditLogDTO> getAuditLogs() {
		return auditLogs;
	}
	public void setAuditLogs(List<AuditLogDTO> auditLogs) {
		this.auditLogs = auditLogs;
	}
	public AuditLogResponseDTO(int totalHits, List<AuditLogDTO> auditLogs) {
		super();
		this.totalHits = totalHits;
		this.auditLogs = auditLogs;
	}
	public AuditLogResponseDTO() {
		super();
		// TODO Auto-generated constructor stub
	}
}