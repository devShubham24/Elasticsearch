package com.sb.main.uniqueDTO;

import java.util.List;

public class auditResponseDto {
	
	    private List<String> uniqueAuditTypes;

	    public auditResponseDto(List<String> uniqueAuditTypes) {
	        this.uniqueAuditTypes = uniqueAuditTypes;
	    }

	    public List<String> getUniqueAuditTypes() {
	        return uniqueAuditTypes;
	    }

	    public void setUniqueAuditTypes(List<String> uniqueAuditTypes) {
	        this.uniqueAuditTypes = uniqueAuditTypes;
	    }
	}