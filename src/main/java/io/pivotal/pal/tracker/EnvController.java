package io.pivotal.pal.tracker;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class EnvController {
    Map<String, String> envs;

    public EnvController(
            @Value("${PORT:NOT SET}") String port,
            @Value("${MEMORY_LIMIT:NOT SET}") String memoryLimit,
            @Value("${CF_INSTANCE_INDEX:NOT SET}") String CfInstanceIndex,
            @Value("${CF_INSTANCE_ADDR:NOT SET}") String CfInstanceAddr) {
        HashMap<String, String> map = new HashMap<String, String>() {
            {
                put("PORT", port);
                put("MEMORY_LIMIT", memoryLimit);
                put("CF_INSTANCE_INDEX", CfInstanceIndex);
                put("CF_INSTANCE_ADDR", CfInstanceAddr);
            }
        };

        this.envs = map;
    }

    @GetMapping("/env")
    public Map<String, String> getEnv() {
        return envs;
    }
}
