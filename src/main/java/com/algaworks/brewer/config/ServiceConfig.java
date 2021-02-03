package com.algaworks.brewer.config;

import com.algaworks.brewer.storage.FotoStorage;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.algaworks.brewer.service.CadastroCervejaService;

@Configuration
@ComponentScan(basePackageClasses = {CadastroCervejaService.class, FotoStorage.class})
public class ServiceConfig {
}
