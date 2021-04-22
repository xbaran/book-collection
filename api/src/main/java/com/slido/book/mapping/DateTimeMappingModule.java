package com.slido.book.mapping;

import org.joda.time.DateTime;
import org.modelmapper.ModelMapper;
import org.modelmapper.Module;

import java.util.Date;

public class DateTimeMappingModule implements Module {
    @Override
    public void setupModule(ModelMapper modelMapper) {
        if(modelMapper.getTypeMap(DateTime.class, Date.class) == null) {
            modelMapper.createTypeMap(DateTime.class, Date.class)
                    .setConverter(ctx -> ctx.getSource() != null ? ctx.getSource().toDate() : null);
        }
        if(modelMapper.getTypeMap(Date.class,DateTime.class) == null) {
            modelMapper.createTypeMap(Date.class,DateTime.class)
                    .setConverter(ctx -> ctx.getSource() != null ? new DateTime(ctx.getSource()) : null);
        }
    }
}