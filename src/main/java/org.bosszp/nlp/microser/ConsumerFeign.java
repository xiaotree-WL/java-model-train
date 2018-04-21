package org.bosszp.nlp.microser;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name= "producer")
public interface ConsumerFeign {
        @RequestMapping(value = "/hello")
        public String hello(@RequestParam(value = "name") String name);
}
