package com.example.api;

import com.example.Application;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.client.RestTemplate;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Created by andrey on 07.10.15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest({"server.port=8080"})
public class ControllerTest {

    @Value("${local.server.port}")
    private int port;

    private Map<URL, String> expectedContent;
    private Map<URL, HttpStatus> expectedStatus;
    private RestTemplate template;

    @Before
    public void setUp() throws Exception {
        String baseUrl = String.format("http://localhost:%s", port);
        
        expectedContent = new HashMap<URL, String>();
        expectedContent.put(
                new URL(baseUrl + "/rate/USD/2015-10-06"),
                "{\"code\":\"USD\",\"rate\":\"65.6248\",\"date\":\"2015-10-06\"}");
        expectedContent.put(
                new URL(baseUrl + "/rate/USD/2055-10-06"),
                "{\"code\":\"USD\",\"rate\":\"\",\"date\":\"2055-10-06\"}");

        expectedStatus = new HashMap<URL, HttpStatus>();
        expectedStatus.put(new URL(baseUrl + "/"), HttpStatus.NOT_FOUND);
        expectedStatus.put(new URL(baseUrl + "/rate"), HttpStatus.NOT_FOUND);
        expectedStatus.put(new URL(baseUrl + "/rate/x"), HttpStatus.BAD_REQUEST);
        expectedStatus.put(new URL(baseUrl + "/rate/xx"), HttpStatus.BAD_REQUEST);
        expectedStatus.put(new URL(baseUrl + "/rate/xxxx"), HttpStatus.BAD_REQUEST);
        expectedStatus.put(new URL(baseUrl + "/rate/xxx/x"), HttpStatus.BAD_REQUEST);
        expectedStatus.put(new URL(baseUrl + "/rate/xxx/2015-10-060"), HttpStatus.BAD_REQUEST);
        expectedStatus.put(new URL(baseUrl + "/rate/xxx"), HttpStatus.OK);
        expectedStatus.put(new URL(baseUrl + "/rate/xxx/2015-10-06"), HttpStatus.OK);

        template = new TestRestTemplate();
    }

    @Test
    public void testGetRate() throws Exception {
        for (Map.Entry<URL, String> entry : expectedContent.entrySet()) {
            ResponseEntity<String> response = template.getForEntity(entry.getKey().toString(), String.class);
            assertThat(response.getBody(), equalTo(entry.getValue()));
        }
    }

    @Test
    public void testRequestStatus() throws Exception {
        for (Map.Entry<URL, HttpStatus> entry : expectedStatus.entrySet()) {
            ResponseEntity<String> response = template.getForEntity(entry.getKey().toString(), String.class);
            assertThat(response.getStatusCode(), equalTo(entry.getValue()));
        }
    }
    
}
