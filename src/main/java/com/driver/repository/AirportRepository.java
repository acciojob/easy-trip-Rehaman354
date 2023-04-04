package com.driver.repository;

import com.driver.model.Airport;
import com.driver.model.City;
import com.driver.model.Flight;
import com.driver.model.Passenger;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class AirportRepository {
    //hashMaps for repo
     TreeMap<String, Airport> airportDb=new TreeMap<>();//airportName->airport
    HashMap<Integer, Flight> flightDb=new HashMap<>();//flightId-> flight
    HashMap<Integer, Passenger> passengerDb=new HashMap<>();//passengerId-> passenger
    HashMap<Integer, List<Flight>> passengerFilghtsDb=new HashMap<>();//passengerId->flights

    public void addAirport(Airport airport) {
        airportDb.put(airport.getAirportName(), airport);
    }

    public void addPassenger(Passenger passenger) {
        passengerDb.put(passenger.getPassengerId(), passenger);
    }

    public void addFlight(Flight flight) {
        flightDb.put(flight.getFlightId(), flight);
    }

    public String getLargestAirportName() {
        String airportName=null;
        int not=0;
        for(String name:airportDb.keySet())
        {
            if(airportDb.get(name).getNoOfTerminals()>not) {
                airportName = name;
                not=airportDb.get(name).getNoOfTerminals();
            }
        }
        return airportName;
    }

    public double getShortestDurationOfPossibleBetweenTwoCities(City fromCity, City toCity) {
        double getShortestDurationOfPossibleBetweenTwoCities=Double.MAX_VALUE;
        for(Flight f:flightDb.values())
        {
            if(f.getFromCity()==fromCity && f.getToCity()==toCity)
            {
                double duration=f.getDuration();
                if(duration<getShortestDurationOfPossibleBetweenTwoCities)
                {
                    getShortestDurationOfPossibleBetweenTwoCities=duration;
                }
            }
        }
        if(getShortestDurationOfPossibleBetweenTwoCities==Double.MAX_VALUE)return -1;
        return getShortestDurationOfPossibleBetweenTwoCities;
    }

    public int getNumberOfPeopleOn(Date date, String airportName) {
        int totalPeople=0;
        City airPortcity=City.KANPUR;
        for(String a:airportDb.keySet())
        {
          if(airportName.equals(a))
          {
              airPortcity=airportDb.get(a).getCity();
              break;
          }
        }
        for(Flight f:flightDb.values())
        {
            if(f.getFlightDate()==date)
            {
                if(f.getFromCity()==airPortcity||f.getToCity()==airPortcity)
                {
                    totalPeople+=f.getNoOfTicketsBoooked();
                }
            }
        }
        return totalPeople;
    }

    public int calculateFlightFare(Integer flightId) {
        int ticketsBooked=flightDb.get(flightId).getNoOfTicketsBoooked();
        return 3000+(ticketsBooked*50);
    }

    public String bookTicket(Integer flightId, Integer passengerId) {
        if(flightDb.get(flightId).getNoOfTicketsBoooked()>=flightDb.get(flightId).getMaxCapacity())
        {
            return "FAILURE";
        }
        if(passengerFilghtsDb.containsKey(passengerId)) {
            for (Flight f : passengerFilghtsDb.get(passengerId)) {
                if (f.getFlightId() == flightId)
                    return "FAILURE";
            }
            List<Flight> flights=passengerFilghtsDb.get(passengerId);
            flights.add(flightDb.get(flightId));
            passengerFilghtsDb.put(passengerId,flights);
            flightDb.get(flightId).setNoOfTicketsBoooked(flightDb.get(flightId).getNoOfTicketsBoooked()+1);
            return "SUCCESS";
        }
         List<Flight> flights=new ArrayList<>();
        flights.add(flightDb.get(flightId));
        passengerFilghtsDb.put(passengerId,flights);
        flightDb.get(flightId).setNoOfTicketsBoooked(flightDb.get(flightId).getNoOfTicketsBoooked()+1);
        return "SUCCESS";
        }

    public String cancelTicket(Integer flightId, Integer passengerId) {
        if(!passengerFilghtsDb.containsKey(passengerId))
        {
            return "FAILURE";
        }
        else {
            for (Flight f : passengerFilghtsDb.get(passengerId)) {
                if (f.getFlightId() == flightId) {
                    //cancel here
                    List<Flight> flights = passengerFilghtsDb.get(passengerId);
                    flights.remove(flightDb.get(flightId));
                    passengerFilghtsDb.put(passengerId, flights);
                    //change the attribute noOfTicketsBoooked
                    f.setNoOfTicketsBoooked(f.getNoOfTicketsBoooked()-1);
                    return "SUCCESS";
                }
            }
            return "FAILURE";
        }
    }

    public int countOfBookingsDoneByPassengerAllCombined(Integer passengerId) {
        return passengerFilghtsDb.get(passengerId).size();
    }

    public String getAirportNameFromFlightId(Integer flightId) {
        if(!flightDb.containsKey(flightId))
            return null;
        City flightStartCity=flightDb.get(flightId).getFromCity();
        for(String name:airportDb.keySet())
        {
            if(airportDb.get(name).getCity()==flightStartCity) {
                return name;
            }
        }
        return null;
    }

    public int calculateRevenueOfAFlight(Integer flightId) {
        int revenue=0;
        //sum of arithmetic series-> n/2(2a+(n-1)*d) or n/2(a+L)
        int ticketsBooked=flightDb.get(flightId).getNoOfTicketsBoooked();
        for(int i=0;i<ticketsBooked;i++)
        {
            revenue+=3000+i*50;
        }
        return revenue;

    }
}
