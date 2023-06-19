package com.egrand.sweetplugin.spring.boot.starter.annotations;

import com.egrand.sweetplugin.spring.boot.starter.PluginManagerStart;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import({PluginManagerStart.class})
public @interface EnableEgdPlugin {
}
