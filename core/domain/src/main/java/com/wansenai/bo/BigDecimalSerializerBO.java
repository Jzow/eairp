package com.wansenai.bo;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class BigDecimalSerializerBO extends JsonSerializer<BigDecimal> {

    @Override
    public void serialize(BigDecimal value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value != null) {
            BigDecimal convertedValue = convertToPositiveFormatAndRound(value);
            gen.writeNumber(convertedValue);
        } else {
            gen.writeNull();
        }
    }

    private BigDecimal convertToPositiveFormatAndRound(BigDecimal value) {
        BigDecimal absoluteValue = value.abs();
        BigDecimal roundedValue = absoluteValue.setScale(2, RoundingMode.HALF_UP);
        return roundedValue;
    }
}
