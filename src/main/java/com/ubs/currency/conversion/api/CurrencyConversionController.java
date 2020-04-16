package com.ubs.currency.conversion.api;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.ubs.currency.conversion.model.CurrencyConversion;
import com.ubs.currency.conversion.proxy.CurrencyExchangeServiceProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class CurrencyConversionController {
    private final CurrencyExchangeServiceProxy proxy;

    public CurrencyConversionController(CurrencyExchangeServiceProxy proxy){
        this.proxy = proxy;
    }

    @GetMapping("/currency-converter/from/{from}/to/{to}/quantity/{quantity}")
    public CurrencyConversion convertCurrency(@PathVariable String from, @PathVariable String to,
                                              @PathVariable BigDecimal quantity) {

        Map<String, String> uriVariables = new HashMap<>();
        uriVariables.put("from", from);
        uriVariables.put("to", to);

        ResponseEntity<CurrencyConversion> responseEntity = new RestTemplate().getForEntity(
                "http://localhost:8000/currency-exchange/from/{from}/to/{to}", CurrencyConversion.class,
                uriVariables);

        CurrencyConversion response = responseEntity.getBody();

        return new CurrencyConversion(response.getId(), from, to, response.getConversionMultiple(), quantity,
                quantity.multiply(response.getConversionMultiple()), response.getPort());
    }

    @GetMapping("/currency-converter-feign/from/{from}/to/{to}/quantity/{quantity}")
    public CurrencyConversion convertCurrencyFeign(@PathVariable String from, @PathVariable String to,
                                                       @PathVariable BigDecimal quantity) {

        CurrencyConversion response = proxy.retrieveExchangeValue(from, to);

        System.out.println(response);

        return new CurrencyConversion(response.getId(), from, to, response.getConversionMultiple(), quantity,
                quantity.multiply(response.getConversionMultiple()), response.getPort());
    }

}
