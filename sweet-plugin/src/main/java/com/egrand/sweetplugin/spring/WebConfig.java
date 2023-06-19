package com.egrand.sweetplugin.spring;

import lombok.Data;

import java.util.Set;

/**
 * 插件中对web的配置
 */
@Data
public class WebConfig {

    private boolean enable = false;
    private Set<String> resourceLocations = null;

}
