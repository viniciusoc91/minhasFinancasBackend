package br.com.estudo.fullstack.service;

import java.math.BigDecimal;
import java.util.Optional;

import br.com.estudo.fullstack.model.entity.Usuario;

public interface UsuarioService {

	Usuario autenticar(String email, String senha);
	
	Usuario salvarUsuario(Usuario usuario);
	
	void validarEmail(String email);
	
	Optional<Usuario> buscarPorId(Long id);
}
