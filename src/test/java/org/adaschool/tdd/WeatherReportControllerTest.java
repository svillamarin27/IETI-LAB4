package org.adaschool.tdd;


import org.adaschool.tdd.controller.weather.dto.NearByWeatherReportsQueryDto;
import org.adaschool.tdd.controller.weather.dto.WeatherReportDto;
import org.adaschool.tdd.repository.WeatherReportRepository;
import org.adaschool.tdd.repository.document.GeoLocation;
import org.adaschool.tdd.repository.document.WeatherReport;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest( webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT )
@TestInstance( TestInstance.Lifecycle.PER_CLASS )
public class WeatherReportControllerTest {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    WeatherReportRepository repository;

    @Test
    public void greetingShouldReturnDefaultMessage()
            throws Exception
    {
        assertThat(
                this.restTemplate.getForObject( "http://localhost:" + port + "/v1/health", String.class ) ).contains(
                "API Working OK!" );
    }

    @Test
    public void weatherControllerPost() throws Exception
    {
        double lt = 4.7110;
        double ln = 74.0721;
        String urlt = "http://localhost/:";
        String path = "/v1/weather";
        GeoLocation loc = new GeoLocation( lt, ln);
        WeatherReportDto weaDto=new WeatherReportDto( loc, 35f, 22f, "tester", new Date() );
        ResponseEntity<WeatherReportDto> request = restTemplate.postForEntity(urlt + port + path,weaDto,WeatherReportDto.class);
        Assertions.assertEquals(request.getBody().getReporter(),"tester");
    }
    @Test
    public void weatherFindByReportsPostTest() throws Exception {
        List<WeatherReport> wReportsList = new ArrayList<WeatherReport>();
        String url = "http://localhost:";
        String path = "/v1/weather/nearby";
        wReportsList.add(new WeatherReport( new GeoLocation( 0, 0 ), 35f, 22f, "test1", new Date() ));
        wReportsList.add(new WeatherReport( new GeoLocation( 5, 7 ), 35f, 22f, "test2", new Date() ));
        wReportsList.add(new WeatherReport( new GeoLocation( 10, 14 ), 35f, 22f, "test3", new Date() ));
        wReportsList.add(new WeatherReport( new GeoLocation( 15, 21 ), 35f, 22f, "test4", new Date() ));
        wReportsList.add(new WeatherReport( new GeoLocation( 20, 28 ), 35f, 22f, "test5", new Date() ));
        wReportsList.add(new WeatherReport( new GeoLocation( 25, 35 ), 35f, 22f, "test6", new Date() ));
        when( repository.findAll()).thenReturn( wReportsList );
        NearByWeatherReportsQueryDto nQuery=new NearByWeatherReportsQueryDto(new GeoLocation(3,4),5);
        assertThat(this.restTemplate.postForObject(url + port + path, nQuery, List.class).size()).isEqualTo(2);
    }
    @Test
    public void findByIdGetTest() throws Exception {
        String weatherReportId="test";
        String url = "http://localhost:";
        String path = "/v1/weather/test";
        WeatherReport wReport = new WeatherReport(new GeoLocation(5, 10), 35f, 22f, "test", new Date());
        when( repository.findById( weatherReportId ) ).thenReturn( Optional.of( wReport ) );
        WeatherReport weatherReports = this.restTemplate.getForObject(url + port + path, WeatherReport.class);
        Assertions.assertEquals( wReport.getReporter(), weatherReports.getReporter() );
    }

    @Test
    public void findByReporterIdGetTest() throws Exception {
        double latitude = 4.7110;
        double length = 74.0721;
        String url = "http://localhost:";
        String path = "/v1/weather/reporter/test";
        GeoLocation loc= new GeoLocation( latitude, length );
        List<WeatherReport> wReports = new ArrayList<>();
        wReports.add(new WeatherReport( loc, 35f, 22f, "test", new Date() ));
        when( repository.findByReporter("test") ).thenReturn( wReports );
        List<WeatherReport> wReportList = this.restTemplate.getForObject(url + port + path, List.class);
        Assertions.assertEquals(wReportList.get(0).getReporter(), wReports.get(0).getReporter());
    }

}
