package com.leyou.search.client;

import com.leyou.item.api.SpecParamApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("item")
public interface SpecParamClient extends SpecParamApi {

}
