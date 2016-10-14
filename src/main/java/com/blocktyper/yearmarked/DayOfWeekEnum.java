package com.blocktyper.yearmarked;

public enum DayOfWeekEnum {
	MONSOONDAY(1, "MONSOONDAY", YearmarkedPlugin.CONFIG_KEY_MONSOONDAY),
	EARTHDAY(2, "EARTHDAY", YearmarkedPlugin.CONFIG_KEY_EARTHDAY),
	WORTAG(3, "WORTAG", YearmarkedPlugin.CONFIG_KEY_WORTAG),
	DONNERSTAG(4, "DONNERSTAG", YearmarkedPlugin.CONFIG_KEY_DONNERSTAG),
	FISHFRYDAY(5, "FISHFRYDAY", YearmarkedPlugin.CONFIG_KEY_FISHFRYDAY),
	DIAMONDAY(6, "DIAMONDAY", YearmarkedPlugin.CONFIG_KEY_DIAMONDAY),
	FEATHERSDAY(7, "FEATHERSDAY", YearmarkedPlugin.CONFIG_KEY_FEATHERSDAY),
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
