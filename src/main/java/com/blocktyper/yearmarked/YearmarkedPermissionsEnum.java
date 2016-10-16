package com.blocktyper.yearmarked;

public enum YearmarkedPermissionsEnum {
	TIMELORD("yearmarked.timelord");
	
	
	private String name;
	
	private YearmarkedPermissionsEnum(String name){
		this.name= name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
}
