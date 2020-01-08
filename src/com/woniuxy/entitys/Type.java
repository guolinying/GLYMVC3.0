package com.woniuxy.entitys;

public class Type {

	private int typeId;
	private String typeCode;
	private String typeName;
	
	public Type() {
		// TODO Auto-generated constructor stub
	}
	public Type(int typeId, String typeCode, String typeName) {
		super();
		this.typeId = typeId;
		this.typeCode = typeCode;
		this.typeName = typeName;
	}
	public int getTypeId() {
		return typeId;
	}
	public void setTypeId(int typeId) {
		this.typeId = typeId;
	}
	public String getTypeCode() {
		return typeCode;
	}
	public void setTypeCode(String typeCode) {
		this.typeCode = typeCode;
	}
	public String getTypeName() {
		return typeName;
	}
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	@Override
	public String toString() {
		return "Type [typeId=" + typeId + ", typeCode=" + typeCode + ", typeName=" + typeName + "]";
	}
	
	
	
}
