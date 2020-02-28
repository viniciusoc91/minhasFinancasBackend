package br.com.estudo.fullstack.model.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

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

import br.com.estudo.fullstack.model.entity.Lancamento;
import br.com.estudo.fullstack.model.enums.StatusLancamento;
import br.com.estudo.fullstack.model.enums.TipoLancamento;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ActiveProfiles("test")
public class LancamentoRepositoryTest {
	
	@Autowired
	LancamentoRepository repository;
	
	@Autowired
	TestEntityManager entityManager;
	
	@Test
	public void salvarLancamentoTest() {
		//cenário
		Lancamento lancamento = criarLancamento();
		
		lancamento = repository.save(lancamento);

		assertNotNull(lancamento.getId());
	}
	
	@Test
	public void deletarLancamentoTest() {
		//cenário
		Lancamento lancamento = criarLancamentoPersistir();
		lancamento = entityManager.find(Lancamento.class, lancamento.getId());
		
		repository.delete(lancamento);
		
		assertNull(entityManager.find(Lancamento.class, lancamento.getId()));
	}
	
	@Test
	public void atualizarLancamentoTest() {
		Lancamento lancamento = criarLancamentoPersistir();
		
		lancamento.setAno(2021);
		lancamento.setDescricao("Teste Atualizar");
		lancamento.setStatus(StatusLancamento.EFETIVADO);
		repository.save(lancamento);
		
		Lancamento lancamentoAtualizado = entityManager.find(Lancamento.class, lancamento.getId());

		assertEquals(2021, lancamentoAtualizado.getAno());
		assertEquals("Teste Atualizar", lancamentoAtualizado.getDescricao());
		assertEquals(StatusLancamento.EFETIVADO, lancamentoAtualizado.getStatus());
	}
	
	@Test
	public void buscarLancamentoPorIdTest() {
		Lancamento lancamento = criarLancamentoPersistir();
		
		Optional<Lancamento> lancamentoEncontrado = repository.findById(lancamento.getId());
		
		assertTrue(lancamentoEncontrado.isPresent());
	}
	
	private Lancamento criarLancamentoPersistir() {
		Lancamento lancamento = criarLancamento();
		entityManager.persist(lancamento);
		return lancamento;
	}
	
	public static Lancamento criarLancamento() {

		Lancamento lancamento = new Lancamento();
		lancamento.setDescricao("Lançamento teste");
		lancamento.setAno(2020);
		lancamento.setMes(2);
		lancamento.setValor(BigDecimal.valueOf(1000));
		lancamento.setDataCadastro(LocalDate.now());
		lancamento.setTipo(TipoLancamento.RECEITA);
		lancamento.setStatus(StatusLancamento.PENDENTE);
		
		return lancamento;
	}
}
