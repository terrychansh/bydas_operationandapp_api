package com.benyi.energy.controller;

import com.benyi.common.core.controller.BaseController;
import com.benyi.common.core.domain.AjaxResult;
import com.benyi.energy.domain.Cities;
import com.benyi.energy.domain.Countries;
import com.benyi.energy.domain.States;
import com.benyi.energy.service.ICitiesService;
import com.benyi.energy.service.ICountriesService;
import com.benyi.energy.service.IStatesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/energy/zone/")
public class WorldAreaZoneController extends BaseController {

    @Autowired
    private ICountriesService countriesService;

    @Autowired
    private ICitiesService citiesService;

    @Autowired
    private IStatesService statesService;


    @GetMapping("/countries")
    public AjaxResult getCountriesList(){
        AjaxResult ajaxResult=AjaxResult.success(countriesService.selectCountriesList(null));
        return ajaxResult;
    }

    @GetMapping("/states")
    public AjaxResult getStatesList(String countryName){
        States states=new States();
        Countries country=countriesService.selectCountryByCountryName(countryName);
        states.setCountryId(country.getId());
        AjaxResult ajaxResult=AjaxResult.success(statesService.selectStatesList(states));
        return ajaxResult;
    }

    @GetMapping("/cities")
    public AjaxResult getCitiesList(Integer stateId){
        Cities cities=new Cities();
        cities.setStateId(stateId);
        AjaxResult ajaxResult=AjaxResult.success(citiesService.selectCitiesList(cities));
        return ajaxResult;
    }

}
