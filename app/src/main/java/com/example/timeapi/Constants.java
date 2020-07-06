package com.example.timeapi;

public class Constants {
    public static final class DATABASE{
        public static final String DB_NAME="prayer";
        public static final int DB_VERSION=1;
        public static final String TABLE_NAME="timetable";

        public static final String ASR="Asr";
        public static final String DHUHR="Asr";
        public static final String FAJR="Asr";
        public static final String MAHGRIB="Asr";
        public static final String ISHA="Asr";


        public static final String DROP_TABLE="DROP TABLE IF EXISTS"+TABLE_NAME;

        public static final String CREATE_TABLE="CREATE TABLE"+TABLE_NAME + ""+"" +
                "(id PRIMARY KEY,"+
                ASR+ " TEXT not null,"+
                DHUHR +" TEXT not null,"+
                FAJR +" TEXT not null,"+
                ISHA +" TEXT not null,"+
                MAHGRIB +" TEXT not null)";
    }

}
