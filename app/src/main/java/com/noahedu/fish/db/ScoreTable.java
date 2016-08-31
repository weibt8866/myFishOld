package com.noahedu.fish.db;

public class ScoreTable {
	/* 表名 */
	public static final String FISH_TABLE = "fishtable";
	/* ID(自增) */
	public static final String ID = "id";
	/* 分数 */
	public static final String SCORE = "score";
	/* 创建表 */
	public static final String CREATE_FISH_TABLE = "create table fishtable(id integer primary key autoincrement,score integer not null default 0);";
}
