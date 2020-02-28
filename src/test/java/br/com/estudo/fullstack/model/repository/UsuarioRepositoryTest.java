package br.com.estudo.fullstack.model.repository;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import br.com.estudo.fullstack.model.entity.Usuario;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class UsuarioRepositoryTest {

	@Autowired
	UsuarioRepository repository;
	
	@Autowired
	TestEntityManager entityManager;
	
	@Test
	public void validarEmailExistenteTest() {
		//cenário
		
		Usuario usuario = Usuario.builder()
					.nome("Amado Batista")
					.email("amado@.com")
					.senha("amado").build();
		entityManager.persist(usuario);
		
		//ação / verificação
		assertTrue(repository.existsByEmail("amado@.com"));
		
	}
	
	@Test
	public void validarEmailNaoCadastradoTest() {
		//ação / verificação
		assertFalse(repository.existsByEmail("amado@.com"));
	}
	
	@Test
	public void  cadastrarUsuarioTest() {
		//cenário
		Usuario usuario = Usuario.builder()
				.nome("Amado Batista")
				.email("amado@.com")
				.senha("amado").build();
		Usuario newUsuario = repository.save(usuario);
		
		//ação / verificação
	    assertNotNull(newUsuario.getId());
		
	}
	
	@Test 
	public void consultarUsuarioPorEmailCadastradoTest() {
		//cenário
		Usuario usuario = Usuario.builder()
				.nome("Amado Batista")
				.email("amado@.com")
				.senha("amado").build();
		entityManager.persist(usuario);
		
		//ação / verificação
	    assertNotNull(repository.findByEmail("amado@.com"));		
	}
	
	@Test 
	public void consultarUsuarioPorEmailNaoCadastradoTest() {
		//ação / verificação
	    assertNull(repository.findByEmail("amado@.com"));		
	}
}
