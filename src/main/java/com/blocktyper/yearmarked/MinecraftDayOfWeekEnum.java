package com.blocktyper.yearmarked;

/*
 * Monsoonday: It rains all day
Earthday: Things grow faster
Snowday: It snows all day
Twilightness: It is dark all day
Lightness: It is light all day
Donnerstag: It storms with lightning all day
Worttag: Only netherwart grows, but it grows faster
 */
public enum MinecraftDayOfWeekEnum {
	MONSOONDAY(1, "MONSOONDAY", "Monsoonday"),
	EARTHDAY(2, "EARTHDAY", "Earthday"),
	DIAMONDAY(3, "DIAMONDAY", "Diamonday"),
	TWILIGHTNESS(4, "TWILIGHTNESS", "Twilightness"),
	LIGHTNESS(5, "LIGHTNESS", "Ligness"),
	DONNERSTAG(6, "DONNERSTAG", "Donnerstag"),
	WORTTAG(7, "WORTTAG", "Worttag"),
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
		return null;
	}
}
