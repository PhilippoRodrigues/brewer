package com.algaworks.brewer.storage.s3;

import com.algaworks.brewer.storage.FotoStorage;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Profile("prod")
@Component
public class FotoStorageS3 implements FotoStorage {
    @Override
    public String salvar(MultipartFile[] files) {
        return null;
    }

    @Override
    public byte[] recuperar(String foto) {
        return new byte[0];
    }

    @Override
    public byte[] recuperarThumbnail(String fotoCerveja) {
        return new byte[0];
    }

    @Override
    public void excluir(String foto) {

    }
    @Override
    public String getUrl(String foto) {
        return null;
    }
}
