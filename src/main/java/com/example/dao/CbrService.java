package com.example.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;

import javax.validation.constraints.NotNull;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Future;

/**
 * Created by andrey on 06.10.15.
 */
@Service
@CacheConfig(cacheNames = {"rates"})
public class CbrService {

    private static final Logger log = LoggerFactory.getLogger(CbrService.class);
    private static final String RATES_URL = "http://www.cbr.ru/scripts/XML_daily.asp?date_req=%s";
    private static final String RATE_XPATH = "/ValCurs/Valute[CharCode='%s']/Value";
    
    private final DateTimeFormatter cbrDateFormat;
    private final DecimalFormat cbrDecimalFormat;
    private final RestTemplate restTemplate;
    private final DocumentBuilder builder;
    private final XPath xPath;

    public CbrService() throws ParserConfigurationException {
        restTemplate = new RestTemplate();
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        builder = builderFactory.newDocumentBuilder();
        xPath = XPathFactory.newInstance().newXPath();

        cbrDateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator(',');
        cbrDecimalFormat = new DecimalFormat("#.#", symbols);
        cbrDecimalFormat.setParseBigDecimal(true);
    }

    /**
     * Returns Future object for getting an exchange rate by currency code and date.
     * @param code Three character currency code.
     * @param date Date of exchange rate.
     * @return Future object for getting an exchange rate.
     */
    @Cacheable(key = "#code.concat(#date.toString())")
    public Future<BigDecimal> getRate(@NotNull String code, @NotNull LocalDate date) {
        log.info("Cache not used: code={}, Date={}", code, date);
        BigDecimal rate = null;
        String rateUrl = String.format(RATES_URL, date.format(cbrDateFormat));
        String rates = restTemplate.getForObject(rateUrl, String.class);
        InputStream is = null;
        try {
            is = new ByteArrayInputStream(rates.getBytes());
            Document xmlDocument = builder.parse(is);
            String expression = String.format(RATE_XPATH, code);
            String value = xPath.compile(expression).evaluate(xmlDocument);
            if (value != null && !value.isEmpty()) {
                rate = (BigDecimal) cbrDecimalFormat.parse(value);
            }
        } catch (Exception ex) {
            log.error("Getting rate error", ex);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ignored) {
                }
            }
        }
        return new AsyncResult<BigDecimal>(rate);
    }
    
}
