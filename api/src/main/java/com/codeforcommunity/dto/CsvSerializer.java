package com.codeforcommunity.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

public class CsvSerializer {
    protected static final CsvMapper mapper = new CsvMapper();

    private static CsvSchema getSchema(Object o){
        return mapper.schemaFor(o.getClass());
    }

    public static String getObjectHeader(Object o){
        StringBuilder builder = new StringBuilder();
        boolean first = true;
        for (CsvSchema.Column col: getSchema(o)) {
            if (!first) {
                builder.append(",");
            }
            builder.append(col.getName());
            first = false;
        }
        builder.append('\n');
        return builder.toString();
    }

    public static String toCsv(Object o){
        try {
            return mapper.writer(getSchema(o)).writeValueAsString(o);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Something went wrong with toCSV", e);
        }
    }

}
