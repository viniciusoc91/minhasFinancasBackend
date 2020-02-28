package br.com.estudo.fullstack.service.impl;


import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.estudo.fullstack.exception.ErroAutenticacaoException;
import br.com.estudo.fullstack.exception.RegraNegocioException;
import br.com.estudo.fullstack.model.entity.Usuario;
import br.com.estudo.fullstack.model.repository.UsuarioRepository;
import br.com.estudo.fullstack.service.UsuarioService;

@Service
public class UsuarioServiceImpl implements UsuarioService {

	private UsuarioRepository repository;

	public UsuarioServiceImpl(UsuarioRepository repository) {
		super();
		this.repository = repository;
	}

	@Override
	public Usuario autenticar(String email, String senha) {
		Usuario usuario = repository.findByEmail(email);
		if(usuario == null) {
			throw new ErroAutenticacaoException("Não existe um usuário cadastrado com este e-mail!");
		}
		if(!usuario.getSenha().equals(senha)) {
			throw new ErroAutenticacaoException("Senha incorreta!");
		}
		return usuario;
	}

	@Override
	@Transactional
	public Usuario salvarUsuario(Usuario usuario) {

		validarEmail(usuario.getEmail());
		return repository.save(usuario);
	}

	@Override
	public void validarEmail(String email) {
		if(repository.existsByEmail(email)) {
			throw new RegraNegocioException("Já existe um usuário cadastrado com este e-mail!");
		};
		
	}

	@Override
	public Optional<Usuario> buscarPorId(Long id) {
		return repository.findById(id);
	}
	
	
}
