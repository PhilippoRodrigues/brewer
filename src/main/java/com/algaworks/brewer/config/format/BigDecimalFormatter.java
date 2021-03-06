package com.algaworks.brewer.config.format;

import org.springframework.format.Formatter;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

public class BigDecimalFormatter implements Formatter<BigDecimal> {

    private final DecimalFormat decimalFormat;

    public BigDecimalFormatter(String pattern){
        NumberFormat numberFormat = NumberFormat.getInstance(new Locale("pt", "BR"));
        decimalFormat = (DecimalFormat) numberFormat;
        decimalFormat.setParseBigDecimal(true);
        decimalFormat.applyPattern(pattern);
    }

    @Override
    public BigDecimal parse(String text, Locale locale) throws ParseException {
        return (BigDecimal) decimalFormat.parse(text);
    }

    @Override
    public String print(BigDecimal object, Locale locale) {
        return decimalFormat.format(object);
    }
}
