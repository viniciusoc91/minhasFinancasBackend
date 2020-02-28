package br.com.estudo.fullstack.service;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Example;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import br.com.estudo.fullstack.exception.RegraNegocioException;
import br.com.estudo.fullstack.model.entity.Lancamento;
import br.com.estudo.fullstack.model.entity.Usuario;
import br.com.estudo.fullstack.model.enums.StatusLancamento;
import br.com.estudo.fullstack.model.enums.TipoLancamento;
import br.com.estudo.fullstack.model.repository.LancamentoRepository;
import br.com.estudo.fullstack.model.repository.LancamentoRepositoryTest;
import br.com.estudo.fullstack.service.impl.LancamentoServiceImpl;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class LancamentoServiceTest {

	@SpyBean
	LancamentoServiceImpl service;
	@MockBean
	LancamentoRepository repository;

	@Test
	public void salvarLancamentoOKTest() {
		Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();
		Mockito.doNothing().when(service).validarLancamento(lancamentoASalvar);

		Lancamento lancamentoSalvo = LancamentoRepositoryTest.criarLancamento();
		lancamentoSalvo.setId(1l);
		Mockito.when(repository.save(lancamentoASalvar)).thenReturn(lancamentoSalvo);

		Lancamento lancamento = service.salvarLancamento(lancamentoASalvar);
		assertEquals(lancamento.getId(), lancamentoSalvo.getId());
		assertEquals(lancamento.getStatus(), StatusLancamento.PENDENTE);
	}

	@Test
	public void salvarLancamentoErroValidacaoTest() {
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		Mockito.doThrow(RegraNegocioException.class).when(service).validarLancamento(lancamento);

		assertThrows(RegraNegocioException.class, () -> {
			service.salvarLancamento(lancamento);
		});
		Mockito.verify(repository, Mockito.never()).save(lancamento);
	}

	@Test
	public void atualizarLancamentoOKTest() {
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(1l);
		lancamento.setStatus(StatusLancamento.EFETIVADO);

		Mockito.doNothing().when(service).validarLancamento(lancamento);
		Mockito.when(repository.save(lancamento)).thenReturn(lancamento);

		service.atualizarLancamento(lancamento);
		Mockito.verify(repository, Mockito.times(1)).save(lancamento);
	}

	@Test
	public void atualizarLancamentoInexistenteTest() {
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();

		assertThrows(NullPointerException.class, () -> {
			service.atualizarLancamento(lancamento);
		});
		Mockito.verify(repository, Mockito.never()).save(lancamento);
	}

	@Test
	public void deletarLancamentoOKTest() {
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(1l);

		service.deletarLancamento(lancamento);

		Mockito.verify(repository, Mockito.times(1)).delete(lancamento);
	}

	@Test
	public void deletarLancamentoInexistenteTest() {
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();

		assertThrows(NullPointerException.class, () -> {
			service.deletarLancamento(lancamento);
		});

		Mockito.verify(repository, Mockito.never()).delete(lancamento);
	}
	
	@Test
	public void filtrarLancamentoTest() {
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(1l);
		
		List<Lancamento> lista = Arrays.asList(lancamento);
		Mockito.when(repository.findAll(Mockito.any(Example.class))).thenReturn(lista);
		
		List<Lancamento> resultado = service.buscarLancamentos(lancamento);
		
		assertEquals(lista, resultado);
	}
	
	@Test
	public void atualizarStatusOKTest() {
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(1l);
		lancamento.setStatus(StatusLancamento.PENDENTE);
		
		StatusLancamento novoStatus = StatusLancamento.EFETIVADO;
		Mockito.doReturn(lancamento).when(service).atualizarLancamento(lancamento);
		
		service.atualizarStatus(lancamento, novoStatus);
		
		assertEquals(lancamento.getStatus(), novoStatus);
	}
	
	@Test
	public void buscarLancamentoPorIdTest() {
		
		Long id = 1l;
		
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(id);
		
		Mockito.when(repository.findById(id)).thenReturn(Optional.of(lancamento));
		
		Optional<Lancamento> resultado = service.buscarPorId(id);
		
		assertTrue(resultado.isPresent());
	}
	
	@Test
	public void buscarLancamentoPorIdInexistenteTest() {
		
		Long id = 1l;
		
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(id);
		
		Mockito.when(repository.findById(id)).thenReturn(Optional.empty());
		
		Optional<Lancamento> resultado = service.buscarPorId(id);
		
		assertFalse(resultado.isPresent());
	}
	
	@Test
	public void validarLancamentoOKTest() {
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		Usuario usuario = Usuario.builder()
				.nome("oMeuEnderecoTaNaInternet")
				.email("cliqueParaAmado@.com")
				.senha("AmadoBatista").build();
		usuario.setId(1l);
		lancamento.setUsuario(usuario);

		//ação / verificação
	    assertDoesNotThrow(() -> {
	    	service.validarLancamento(lancamento);
	    });
	}
	
	@Test
	public void validarLancamentoErroValidacaoTest() {
		Lancamento lancamento = new Lancamento();

		//Validar Descrição
	    Exception exception = assertThrows(RegraNegocioException.class, () ->{
	    	service.validarLancamento(lancamento);
	    });
	    assertEquals(exception.getMessage(),"Informe uma Descrição válida!");

	    lancamento.setDescricao("Teste");

		//Validar Mês
	    exception = assertThrows(RegraNegocioException.class, () ->{
	    	service.validarLancamento(lancamento);
	    });
	    assertEquals(exception.getMessage(),"Informe um Mês válido!");

	    lancamento.setMes(2);

		//Validar Ano
	    exception = assertThrows(RegraNegocioException.class, () ->{
	    	service.validarLancamento(lancamento);
	    });
	    assertEquals(exception.getMessage(),"Informe um Ano válido!");

	    lancamento.setAno(2020);

		//Validar Usuario
	    exception = assertThrows(RegraNegocioException.class, () ->{
	    	service.validarLancamento(lancamento);
	    });
	    assertEquals(exception.getMessage(),"Informe um Usuário válido!");

		Usuario usuario = Usuario.builder()
				.nome("oMeuEnderecoTaNaInternet")
				.email("cliqueParaAmado@.com")
				.senha("AmadoBatista").build();
		usuario.setId(1l);
	    lancamento.setUsuario(usuario);

		//Validar Valor
	    exception = assertThrows(RegraNegocioException.class, () ->{
	    	service.validarLancamento(lancamento);
	    });
	    assertEquals(exception.getMessage(),"Informe um Valor válido!");

	    lancamento.setValor(BigDecimal.valueOf(1000));

		//Validar Tipo Lançamento
	    exception = assertThrows(RegraNegocioException.class, () ->{
	    	service.validarLancamento(lancamento);
	    });
	    assertEquals(exception.getMessage(),"Informe um Tipo de Lançamento!");
	    
	    lancamento.setTipo(TipoLancamento.RECEITA);

	    //encerra as validações
	    assertDoesNotThrow(() -> {
	    	service.validarLancamento(lancamento);
	    });
	}
}
