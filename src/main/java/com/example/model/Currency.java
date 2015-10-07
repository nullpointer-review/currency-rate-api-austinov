package com.example.model;

import com.example.utils.CurrencyDateSerializer;
import com.example.utils.CurrencyRateSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Created by andrey on 06.10.15.
 */
public class Currency {
    private final String code;
    private final BigDecimal rate;
    private final LocalDate date;

    public Currency(String code, BigDecimal rate, LocalDate date) {
        this.code = code;
        this.rate = rate;
        this.date = date;
    }

    public String getCode() {
        return code;
    }

    @JsonSerialize(using = CurrencyRateSerializer.class, nullsUsing = CurrencyRateSerializer.class)
    public BigDecimal getRate() {
        return rate;
    }

    @JsonSerialize(using = CurrencyDateSerializer.class)
    public LocalDate getDate() {
        return date;
    }
}
