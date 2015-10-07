package com.example.api;


import com.example.dao.CbrService;
import com.example.model.Currency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.concurrent.Future;

/**
 * Created by andrey on 06.10.15.
 */
@RestController
public class Controller {

    private static final Logger log = LoggerFactory.getLogger(Controller.class);
    
    private final DateTimeFormatter INPUT_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Autowired
    CbrService cbrService;

    @RequestMapping(value = "/rate/{code}", method = RequestMethod.GET)
    public ResponseEntity<Currency> getRate(@PathVariable String code) {
        return getRateInternal(code, null);
    }
    
    @RequestMapping(value = "/rate/{code}/{date}", method = RequestMethod.GET)
    public ResponseEntity<Currency> getRate(@PathVariable String code, @PathVariable String date) {
        return getRateInternal(code, date);
    }

    private ResponseEntity<Currency> getRateInternal(String code, String date) {
        BigDecimal rate;
        LocalDate dateRate;
        try {
            code = getCodeFromRequest(code);
            dateRate = getDateFromRequest(date);
            Future<BigDecimal> future = cbrService.getRate(code, dateRate);
            rate = future.get();
        } catch (Exception ex) {
            log.error("Getting rate error", ex);
            return new ResponseEntity<Currency>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<Currency>(new Currency(code, rate, dateRate), HttpStatus.OK);
    }

    /**
     * Returns currency code in uppercase by value from request path.
     * @throws Exception if the input code is null or length of it isn't equal 3.
     */
    private String getCodeFromRequest(String code) throws Exception {
        if(code == null || code.length() != 3) {
            throw new Exception(String.format("Parameter [code=%s] is incorrect", code));
        }
        return code.toUpperCase();
    }

    /**
     * Returns date by value from request. If input value is null then returns tomorrow's date.
     * @throws Exception if the input value doesn't match the 'yyyy-MM-dd' format.
     */
    private LocalDate getDateFromRequest(String date) throws Exception {
        if (date != null) {
            try {
                return LocalDate.parse(date, INPUT_DATE_FORMAT);
            } catch (DateTimeParseException ex) {
                throw new Exception(String.format("Parameter [date=%s] is incorrect", date));
            }
        }
        // Next of current date (tomorrow)
        return LocalDate.now().plusDays(1);
    }
}