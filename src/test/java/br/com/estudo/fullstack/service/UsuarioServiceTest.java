package br.com.estudo.fullstack.service;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import br.com.estudo.fullstack.exception.ErroAutenticacaoException;
import br.com.estudo.fullstack.exception.RegraNegocioException;
import br.com.estudo.fullstack.model.entity.Usuario;
import br.com.estudo.fullstack.model.repository.UsuarioRepository;
import br.com.estudo.fullstack.service.impl.UsuarioServiceImpl;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class UsuarioServiceTest {

	@MockBean
	UsuarioRepository repository;

	@SpyBean
	UsuarioServiceImpl service;
	
	@Test
	public void validarEmailCadastradoTest() {
		//cenário
		Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(true);
		
		//ação / verificação
	    assertThrows(RegraNegocioException.class, () -> {
	    	service.validarEmail("amado@.com");
	    });
	}
	
	@Test
	public void validarEmailNaoCadastradoTest() {
		//cenário
		Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(false);
		
		//ação / verificação
	    assertDoesNotThrow(() -> {
	    	service.validarEmail("amado@.com");
	    });
	}	
	
	@Test
	public void autenticarSucessoTest(){
		//cenário
		String email = "cliqueParaAmado@.com";
		String senha = "AmadoBatista";

		Usuario usuario = Usuario.builder()
				.nome("oMeuEnderecoTaNaInternet")
				.email(email)
				.senha(senha).build();
		Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(usuario);
		
		//ação / verificação
	    assertDoesNotThrow(() -> {
	    	service.autenticar("cliqueParaAmado@.com", "AmadoBatista");
	    });
	}
	
	@Test
	public void autenticarEmailNaoCadastradoTest(){
		//cenário
		Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(null);
		
		//ação / verificação
		Exception exception = assertThrows(ErroAutenticacaoException.class, () -> {
	    	service.autenticar("cliqueparaamado@.com", "amado");
	    });
	    assertEquals(exception.getMessage(),"Não existe um usuário cadastrado com este e-mail!");
	}
	
	@Test
	public void autenticarSenhaIncorretaTest(){
		//cenário
		String email = "cliqueParaAmado@.com";
		String senha = "AmadoBatista";

		Usuario usuario = Usuario.builder()
				.nome("oMeuEnderecoTaNaInternet")
				.email(email)
				.senha(senha).build();
		Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(usuario);
		
		//ação / verificação
		
	    Exception exception = assertThrows(ErroAutenticacaoException.class, () -> {
	    	service.autenticar("amado@.com", "amado@.com");
	    });
	    assertTrue(exception.getMessage().equals("Senha incorreta!"));
	}
	
	@Test
	public void salvarUsuarioOkTest() {
		//cenário

		Usuario usuario = Usuario.builder()
				.nome("oMeuEnderecoTaNaInternet")
				.email("cliqueparaAmado@.com")
				.senha("AmadoBatista").build();
		
		Mockito.doNothing().when(service).validarEmail(Mockito.anyString());
		Mockito.when(repository.save(Mockito.any(Usuario.class))).thenReturn(usuario);
		
		//ação / verificação
		assertNotNull(service.salvarUsuario(usuario));
	}
	
	@Test
	public void salvarUsuarioEmailCadastradoTest() {
		//cenário

		Usuario usuario = Usuario.builder()
				.nome("oMeuEnderecoTaNaInternet")
				.email("cliqueparaAmado@.com")
				.senha("AmadoBatista").build();
		Mockito.doThrow(new RegraNegocioException("Já existe um usuário cadastrado com este e-mail!"))
			.when(service).validarEmail(Mockito.anyString());
		
		//ação / verificação
		assertThrows(RegraNegocioException.class, () -> {
	    	service.salvarUsuario(usuario);
	    });	
	}

}
