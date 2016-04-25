package com.example.benjamin.tingle2.database;

import java.util.Date;

/**
 * Created by Benjamin on 3/30/2016.
 */
public class TingleDBSchema {
    public static final class ThingTable {
        public static final String NAME = "things";

        public static final class Cols {
            public  static final String  ID = "idCol";
            public  static final String  WHAT = "whatCol";
            public  static final String  WHERE = "whereCol";
        }
    }
}
