package org.adaschool.tdd.service;

import org.adaschool.tdd.controller.weather.dto.WeatherReportDto;
import org.adaschool.tdd.exception.WeatherReportNotFoundException;
import org.adaschool.tdd.repository.WeatherReportRepository;
import org.adaschool.tdd.repository.document.GeoLocation;
import org.adaschool.tdd.repository.document.WeatherReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MongoWeatherService
    implements WeatherService
{

    private final WeatherReportRepository repository;

    public MongoWeatherService( @Autowired WeatherReportRepository repository )
    {
        this.repository = repository;
    }

    @Override
    public WeatherReport report( WeatherReportDto weatherReportDto )
    {
        WeatherReport wReport = new WeatherReport(weatherReportDto);
        return repository.save(wReport);
    }
    @Override
    public WeatherReport findById( String id ) {
        Optional<WeatherReport> opt = repository.findById( id );
        if ( opt.isPresent() ){
            return opt.get();
        } else {
            throw new WeatherReportNotFoundException();
        }
    }
    @Override
    public List<WeatherReport> findNearLocation( GeoLocation geoLocation, float distanceRangeInMeters ) {
        List<WeatherReport> locationNear = new ArrayList<>();
        List<WeatherReport> reportWeather = repository.findAll();
         for(WeatherReport loc:reportWeather){
            if(calculateNearLocation(loc.getGeoLocation().getLat(),loc.getGeoLocation().getLng(),geoLocation.getLat(),
            geoLocation.getLng())<=distanceRangeInMeters){
            locationNear.add(loc);
            }
         }
         return locationNear;

    }

    @Override
    public List<WeatherReport> findWeatherReportsByName( String reporter )
    {
        return repository.findByReporter(reporter);
    }
    private double calculateNearLocation(double latitude, double length,double latitudeIni, double lengthFinal){
        return Math.sqrt((latitude - latitudeIni)*(latitude-latitudeIni)+(length-lengthFinal)*(length-lengthFinal));
    }

}
