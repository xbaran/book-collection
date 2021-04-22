package com.slido.book.mapping;

import org.modelmapper.ModelMapper;
import org.modelmapper.Module;
import org.modelmapper.TypeToken;

import static java.nio.charset.StandardCharsets.UTF_8;

public class LobMappingModule implements Module {
    private final TypeToken<byte[]> BYTE_TYPE = new TypeToken<byte[]>(){};
    @Override
    public void setupModule(ModelMapper modelMapper) {
        if(modelMapper.getTypeMap(String.class,BYTE_TYPE.getRawType()) == null) {
            modelMapper.createTypeMap(String.class, BYTE_TYPE.getRawType())
                    .setConverter(ctx -> ctx.getSource() != null ? ctx.getSource().getBytes(UTF_8) : null);
        }
        if(modelMapper.getTypeMap(BYTE_TYPE.getRawType(),String.class) == null) {
            modelMapper.createTypeMap(BYTE_TYPE.getRawType(),String.class)
                    .setConverter(ctx -> ctx.getSource() != null ? new String(ctx.getSource(), UTF_8) : null);
        }
    }
}