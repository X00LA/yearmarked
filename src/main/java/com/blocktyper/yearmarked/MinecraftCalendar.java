package com.blocktyper.yearmarked;

import org.bukkit.World;

public class MinecraftCalendar {
	
	private Long day;
	private Long week;
	private Long month;
	private int year;
	private int monthOfYear;
	private int dayOfMonth;
	private int dayOfWeek;
	
	public MinecraftCalendar(){
		calc(0L);
	}
	
	public MinecraftCalendar(World world){
		this(world.getFullTime());
	}

	public MinecraftCalendar(long fullTime){
		calc(fullTime);
	}
	
	public void calc(long fullTime){
		Double preciseDay = fullTime / (24000.0);
		day = preciseDay.longValue() + 1;
		week = (day / 7) + (day % 7 > 0 ? 1 : 0);
		month = (week / 4) + (week % 4 > 0 ? 1 : 0);
		year = (month.intValue() / 12) + (month.intValue() % 12 > 0 ? 1 : 0);
		monthOfYear = month.intValue() % 12 + (month.intValue() % 12 == 0 ? 12 : 0);
		dayOfMonth = day.intValue() % 28 + (day.intValue() % 28 == 0 ? 28 : 0);
		dayOfWeek = dayOfMonth % 7 + (dayOfMonth % 7 == 0 ? 7 : 0);
	}

	public Long getDay() {
		return day;
	}

	public Long getWeek() {
		return week;
	}

	public Long getMonth() {
		return month;
	}

	public int getYear() {
		return year;
	}

	public int getMonthOfYear() {
		return monthOfYear;
	}

	public int getDayOfMonth() {
		return dayOfMonth;
	}

	public int getDayOfWeek() {
		return dayOfWeek;
	}
	
	public DayOfWeekEnum getDayOfWeekEnum(){
		return DayOfWeekEnum.findByNumber(dayOfWeek);
	}
	
	
}
