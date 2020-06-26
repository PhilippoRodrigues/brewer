package com.algaworks.brewer.storage.local;

import static java.nio.file.FileSystems.getDefault;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import com.algaworks.brewer.storage.FotoStorage;

public class FotoStorageLocal implements FotoStorage {
	
	private static final Logger logger = LoggerFactory.getLogger(FotoStorageLocal.class);
	
	private Path local;
	private Path localTemporario;
	
	public FotoStorageLocal() {
		this(getDefault().getPath(System.getenv("HOME"), ".brewerfotos"));
		
	}
	
	public FotoStorageLocal(Path path) {
		this.local = path;
		criarPastas();
	}

	private void criarPastas() {
		try {
			Files.createDirectories(this.local);
			this.localTemporario = getDefault().getPath(this.local.toString(), "temp");
			Files.createDirectories(this.localTemporario);
			
			if(logger.isDebugEnabled()) {
				logger.debug("Pastas para salvar fotos.");
				//Saber o path do local em que as imagens estão sendo salvas.
				logger.debug("Pasta default: " + this.local.toAbsolutePath());
				
				//Saber o path do local em que as imagens estão sendo salvas temporariamente.
				logger.debug("Pasta temporária: " + this.localTemporario.toAbsolutePath());
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Erro na criação de pasta para salvar imagem.", e);
		}
		
	}

	@Override
	public void salvarTemporariamente(MultipartFile[] files) {
		System.out.println(">>> Salvando a foto temporariamente. <<<");
		
	}
}
