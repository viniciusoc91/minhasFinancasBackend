package br.com.estudo.fullstack.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import br.com.estudo.fullstack.model.entity.Lancamento;
import br.com.estudo.fullstack.model.enums.StatusLancamento;

public interface LancamentoService {
	
	Lancamento salvarLancamento(Lancamento lancamento);
	
	Lancamento atualizarLancamento(Lancamento lancamento);
	
	void deletarLancamento(Lancamento lancamento);
	
	List<Lancamento> buscarLancamentos(Lancamento lancamentoFiltro);
	
	void atualizarStatus(Lancamento lancamento, StatusLancamento status);
	
	void validarLancamento(Lancamento lancamento);

	Optional<Lancamento> buscarPorId(Long id);
	
	BigDecimal obterSaldoPorUsuario(Long id);
}
