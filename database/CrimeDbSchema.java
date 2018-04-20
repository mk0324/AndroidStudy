package com.bignerdranch.andorid.deutschcrime.database;

// 스키마 클래스인 CrimeDbSchema
public class CrimeDbSchema {
    // CrimeTable 내부 클래스 정의하기
    public static final class CrimeTable{
        public static final String NAME = "crimes";
        // 테이블 열 정의하기
        public static final class Cols{
            public static final String UUID = "uuid";
            public static final String TITLE = "title";
            public static final String DATE = "date";
            public static final String SOLVED = "solved";
        }
    }
}
