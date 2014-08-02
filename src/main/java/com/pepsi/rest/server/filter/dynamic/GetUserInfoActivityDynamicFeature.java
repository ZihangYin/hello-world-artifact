package com.pepsi.rest.server.filter.dynamic;

import javax.ws.rs.GET;
import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;

import com.pepsi.rest.server.filter.GetUserInfoActivityFilter;

import java.lang.reflect.Method;

//Comment out this dynamic feature since we do not use it at this moment.
//Without @Provider, this dynamic feature will not be registered.
//@Provider
public class GetUserInfoActivityDynamicFeature implements DynamicFeature {
    
    @Override
    public void configure(ResourceInfo resourceInfo, FeatureContext context) {
        String resourceClassName = resourceInfo.getResourceClass().getName();
        Method resourceMethod = resourceInfo.getResourceMethod();
        
        if (resourceClassName.equals("com.pepsi.rest.activity.GetUserInfoActivity") 
                && resourceMethod.getName().equals("getCustomer")
                && resourceMethod.getAnnotation(GET.class) != null) {
            context.register(GetUserInfoActivityFilter.class);
        }
    }
}
