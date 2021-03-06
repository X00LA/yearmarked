package com.blocktyper.yearmarked;

public enum DayOfWeekEnum {
	MONSOONDAY(1, "MONSOONDAY", ConfigKeyEnum.MONSOONDAY.getKey()),
	EARTHDAY(2, "EARTHDAY", ConfigKeyEnum.EARTHDAY.getKey()),
	WORTAG(3, "WORTAG", ConfigKeyEnum.WORTAG.getKey()),
	DONNERSTAG(4, "DONNERSTAG", ConfigKeyEnum.DONNERSTAG.getKey()),
	FISHFRYDAY(5, "FISHFRYDAY", ConfigKeyEnum.FISHFRYDAY.getKey()),
	DIAMONDAY(6, "DIAMONDAY", ConfigKeyEnum.DIAMONDAY.getKey()),
	FEATHERSDAY(7, "FEATHERSDAY", ConfigKeyEnum.FEATHERSDAY.getKey()),
	UNDEFINED(-1, "UNDEFINED", "UNDEFINED");

	private int dayOfWeek;
	private String code;
	private String displayKey;

	private DayOfWeekEnum(int dayOfWeek, String code, String displayKey) {
		this.dayOfWeek = dayOfWeek;
		this.code = code;
		this.displayKey = displayKey;
	}

	public String getCode() {
		return code;
	}

	public String getDisplayKey() {
		return displayKey;
	}

	public int getDayOfWeek() {
		return dayOfWeek;
	}

	public static DayOfWeekEnum findByNumber(int dayOfWeek) {
		for (DayOfWeekEnum minecraftDay : DayOfWeekEnum.values()) {
			if (minecraftDay.getDayOfWeek() == dayOfWeek)
				return minecraftDay;
		}
		return UNDEFINED;
	}

	public static DayOfWeekEnum findByCode(String code) {
		for (DayOfWeekEnum minecraftDay : DayOfWeekEnum.values()) {
			if (minecraftDay.getCode().equals(code))
				return minecraftDay;
		}
		return UNDEFINED;
	}
}
