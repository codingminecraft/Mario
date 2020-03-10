package com.util;

import com.util.enums.DataType;

public class JMath {
    private static final int FLOAT_SIZE = 4;
    private static final int INT_SIZE = 4;
    private static final int BOOL_SIZE = 4;

    public static int sizeof(DataType type) {
        switch (type) {
            case FLOAT:
                return FLOAT_SIZE;
            case INT:
                return INT_SIZE;
            case BOOL:
                return BOOL_SIZE;
            default:
                System.out.println("Size of this data type unknown in JMath");
                return 0;
        }
    }
}
