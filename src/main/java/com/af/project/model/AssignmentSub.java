package com.af.project.model;

import java.io.File;
import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;

public class AssignmentSub {

	@Id
	ObjectId _id;
	String assignmentId;
	String studentId;
	String courseId;
	@LastModifiedDate
    private String modified;
    private String file;
	
    
    public AssignmentSub() {
		super();
	}


	public ObjectId get_id() {
		return _id;
	}


	public void set_id(ObjectId _id) {
		this._id = _id;
	}


	public String getAssignmentId() {
		return assignmentId;
	}


	public void setAssignmentId(String assignmentId) {
		this.assignmentId = assignmentId;
	}


	public String getStudentId() {
		return studentId;
	}


	public void setStudentId(String studentId) {
		this.studentId = studentId;
	}


	public String getCourseId() {
		return courseId;
	}


	public void setCourseId(String courseId) {
		this.courseId = courseId;
	}


	public String getModified() {
		return modified;
	}


	public void setModified(String string) {
		this.modified = string;
	}


	public String getFile() {
		return file;
	}


	public void setFile(String file) {
		this.file = file;
	}


	public AssignmentSub(ObjectId _id, String assignmentId, String studentId, String courseId, String modified,
			String file) {
		super();
		this._id = _id;
		this.assignmentId = assignmentId;
		this.studentId = studentId;
		this.courseId = courseId;
		this.modified = modified;
		this.file = file;
	}


	
    
	
    
	
}
