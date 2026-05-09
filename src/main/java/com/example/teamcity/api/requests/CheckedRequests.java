package com.example.teamcity.api.requests;

import com.example.teamcity.api.enums.Endpoint;
import com.example.teamcity.api.models.BaseModel;
import com.example.teamcity.api.requests.checked.CheckedBase;
import io.restassured.specification.RequestSpecification;

import java.util.EnumMap;

public class CheckedRequests {
    private final EnumMap<Endpoint, CheckedBase<? extends BaseModel>> requests = new EnumMap<>(Endpoint.class);

    public CheckedRequests(RequestSpecification requestSpecification){
        for(Endpoint endpoint : Endpoint.values()){
            requests.put(endpoint, new CheckedBase<>(requestSpecification, endpoint));
        }
    }

    public <T extends BaseModel> CheckedBase<T> getRequest(Endpoint endpoint) {
        return (CheckedBase<T>) requests.get(endpoint);
    }
}