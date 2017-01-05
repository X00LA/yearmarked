package com.blocktyper.yearmarked;

import java.util.ArrayList;
import java.util.List;

public enum LocalizedMessageEnum {
	
	WORLD("yearmarked.world"),
	WORLDS("yearmarked.worlds"),
	WORLD_NOT_ENABLED("yearmarked.word.not.enabled"),
	ONLY_FOR_OP("yearmarked.only.for.op"),
	NO_PERMISSION("yearmarked.no.permission"),
	YM_COMMAND_NOT_A_VALID_NUMBER("yearmarked.ym.command.not.a.valid.number"),
	
	BONUS("yearmarked.bonus"),
	FALL_DAMAGE_PREVENTED("yearmarked.fall.damage.prevented"),
	DOUBLE_XP("yearmarked.double.xp"),
	FISH_HAD_DIAMOND("yearmarked.fish.had.diamond"),
	FISH_HAD_EMERALD("yearmarked.fish.had.emerald"),
	TODAY_IS("yearmarked.today.is"),

	SUPER_CREEPER_HAD_DIAMOND("yearmarked.super.creeper.had.diamond"),
	SUPER_CREEPER_HAD_EMERALD("yearmarked.super.creeper.had.emerald"),
	SUPER_CREEPER_HAD_THORDFISH("yearmarked.super.creeper.had.thordfish"),

	IT_IS_DAY_NUMBER("yearmarked.it.is.day.number"),
	OF_MONTH_NUMBER("yearmarked.of.month.number"),
	OF_YEAR_NUMBER("yearmarked.of.year.number"),
	
	SUPER_CREEPER_HAD_A_DIAMOND("yearmarked.super.creeper.had.diamond"),
	SUPER_CREEPER_HAD_AN_EMERALD("yearmarked.super.creeper.had.emerald"),
	SUPER_CREEPER_HAD_A_THORDFISH("yearmarked.super.creeper.had.thordfish"),
	
	CANT_AFFORD("yearmarked.cant.afford"),
	
	DESCRIPTION_MONSOONDAY("yearmarked.monsoonday.description"),
	DESCRIPTION_EARTHDAY("yearmarked.earthday.description"),
	DESCRIPTION_WORTAG("yearmarked.wortag.description"),
	DESCRIPTION_DONNERSTAG("yearmarked.donnerstag.description"),
	DESCRIPTION_FISHFRYDAY("yearmarked.fishfrytag.description"),
	DESCRIPTION_DIAMONDAY("yearmarked.diamonday.description"),
	DESCRIPTION_FEATHERSDAY("yearmarked.feathersday.description");


	private String key;

	private LocalizedMessageEnum(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
	
	public static List<LocalizedMessageEnum> getDayDesciptions(DayOfWeekEnum dayOfWeekEnum){
		List<LocalizedMessageEnum> returnList = new ArrayList<LocalizedMessageEnum>();
		if(dayOfWeekEnum != null){
			if(dayOfWeekEnum.equals(DayOfWeekEnum.MONSOONDAY)){
				returnList.add(DESCRIPTION_MONSOONDAY);
			}else if(dayOfWeekEnum.equals(DayOfWeekEnum.EARTHDAY)){
				returnList.add(DESCRIPTION_EARTHDAY);
			}else if(dayOfWeekEnum.equals(DayOfWeekEnum.WORTAG)){
				returnList.add(DESCRIPTION_WORTAG);
			}else if(dayOfWeekEnum.equals(DayOfWeekEnum.DONNERSTAG)){
				returnList.add(DESCRIPTION_DONNERSTAG);
			}else if(dayOfWeekEnum.equals(DayOfWeekEnum.FISHFRYDAY)){
				returnList.add(DESCRIPTION_FISHFRYDAY);
			}else if(dayOfWeekEnum.equals(DayOfWeekEnum.DIAMONDAY)){
				returnList.add(DESCRIPTION_DIAMONDAY);
			}else if(dayOfWeekEnum.equals(DayOfWeekEnum.FEATHERSDAY)){
				returnList.add(DESCRIPTION_FEATHERSDAY);
			}
		}
		
		return returnList;
	}

}
