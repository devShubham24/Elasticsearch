package com.sb.main.auditDTO;

public class AuditLogDTO {
    private int auditLogId;
    private int pdmpUserId;
    private String auditType;
    private int affectedUserId;
    private int roleId;
    private int fileId;
    private int auditTypeId;
    private String auditSource;
    private String firstName;
    private String lastName;
    private String gender;
    private long dateOfBirth;
    private String roleName;
    private String userCategory;
    private String createdAt;
    private String userEmail;
	public AuditLogDTO(int auditLogId, int pdmpUserId, String auditType, int affectedUserId, int roleId, int fileId,
			int auditTypeId, String auditSource, String firstName, String lastName, String gender, long dateOfBirth,
			String roleName, String userCategory, String createdAt, String userEmail) {
		super();
		this.auditLogId = auditLogId;
		this.pdmpUserId = pdmpUserId;
		this.auditType = auditType;
		this.affectedUserId = affectedUserId;
		this.roleId = roleId;
		this.fileId = fileId;
		this.auditTypeId = auditTypeId;
		this.auditSource = auditSource;
		this.firstName = firstName;
		this.lastName = lastName;
		this.gender = gender;
		this.dateOfBirth = dateOfBirth;
		this.roleName = roleName;
		this.userCategory = userCategory;
		this.createdAt = createdAt;
		this.userEmail = userEmail;
	}
	public AuditLogDTO() {
		super();
		// TODO Auto-generated constructor stub
	}
	public int getAuditLogId() {
		return auditLogId;
	}
	public void setAuditLogId(int auditLogId) {
		this.auditLogId = auditLogId;
	}
	public int getPdmpUserId() {
		return pdmpUserId;
	}
	public void setPdmpUserId(int pdmpUserId) {
		this.pdmpUserId = pdmpUserId;
	}
	public String getAuditType() {
		return auditType;
	}
	public void setAuditType(String auditType) {
		this.auditType = auditType;
	}
	public int getAffectedUserId() {
		return affectedUserId;
	}
	public void setAffectedUserId(int affectedUserId) {
		this.affectedUserId = affectedUserId;
	}
	public int getRoleId() {
		return roleId;
	}
	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}
	public int getFileId() {
		return fileId;
	}
	public void setFileId(int fileId) {
		this.fileId = fileId;
	}
	public int getAuditTypeId() {
		return auditTypeId;
	}
	public void setAuditTypeId(int auditTypeId) {
		this.auditTypeId = auditTypeId;
	}
	public String getAuditSource() {
		return auditSource;
	}
	public void setAuditSource(String auditSource) {
		this.auditSource = auditSource;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public long getDateOfBirth() {
		return dateOfBirth;
	}
	public void setDateOfBirth(long dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}
	public String getRoleName() {
		return roleName;
	}
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
	public String getUserCategory() {
		return userCategory;
	}
	public void setUserCategory(String userCategory) {
		this.userCategory = userCategory;
	}
	public String getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}
	public String getUserEmail() {
		return userEmail;
	}
	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}
}