package com.slido.book;

import com.slido.book.mapping.DateTimeMappingModule;
import com.slido.book.mapping.LobMappingModule;
import com.slido.validation.ValidationService;
import com.slido.validation.ValidationServiceImpl;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Singleton;

@Configuration
public class BookConfiguration {

    @Bean
    @Singleton
    @Qualifier("default")
    public ModelMapper initializeModelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.registerModule(new LobMappingModule());
        modelMapper.registerModule(new DateTimeMappingModule());
        return modelMapper;
    }

    @Bean
    @Singleton
    @Qualifier("patch")
    public ModelMapper initializePatchModelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
        modelMapper.registerModule(new LobMappingModule());
        modelMapper.registerModule(new DateTimeMappingModule());
        return modelMapper;
    }

    @Bean
    @Singleton
    public ValidationService initializeValidationService() {
        return new ValidationServiceImpl();
    }
}

