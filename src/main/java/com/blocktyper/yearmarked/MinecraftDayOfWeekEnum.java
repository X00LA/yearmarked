package com.blocktyper.yearmarked;

public enum MinecraftDayOfWeekEnum {
	MONSOONDAY(1, "MONSOONDAY", "Monsoonday"),
	EARTHDAY(2, "EARTHDAY", "Earthday"),
	WORTAG(3, "WORTAG", "Wortag"),
	DONNERSTAG(4, "DONNERSTAG", "Donnerstag"),
	FISHFRYDAY(5, "FISHFRYDAY", "Fishfryday"),
	DIAMONDAY(6, "DIAMONDAY", "Diamonday"),
	FEATHERSDAY(7, "FEATHERSDAY", "Feathersday"),
	UNDEFINED(-1, "UNDEFINED", "UNDEFINED");

	private int dayOfWeek;
	private String code;
	private String displayName;

	private MinecraftDayOfWeekEnum(int dayOfWeek, String code, String displayName) {
		this.dayOfWeek = dayOfWeek;
		this.code = code;
		this.displayName = displayName;
	}

	public String getCode() {
		return code;
	}

	public String getDisplayName() {
		return displayName;
	}

	public int getDayOfWeek() {
		return dayOfWeek;
	}

	public static MinecraftDayOfWeekEnum findByNumber(int dayOfWeek) {
		for (MinecraftDayOfWeekEnum minecraftDay : MinecraftDayOfWeekEnum.values()) {
			if (minecraftDay.getDayOfWeek() == dayOfWeek)
				return minecraftDay;
		}
		return UNDEFINED;
	}

	public static MinecraftDayOfWeekEnum findByCode(String code) {
		for (MinecraftDayOfWeekEnum minecraftDay : MinecraftDayOfWeekEnum.values()) {
			if (minecraftDay.getCode().equals(code))
				return minecraftDay;
		}
		return UNDEFINED;
	}
}
