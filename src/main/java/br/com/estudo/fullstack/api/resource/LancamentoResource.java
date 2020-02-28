package br.com.estudo.fullstack.api.resource;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.estudo.fullstack.api.dto.AtualizaStatusDTO;
import br.com.estudo.fullstack.api.dto.LancamentoDTO;
import br.com.estudo.fullstack.exception.RegraNegocioException;
import br.com.estudo.fullstack.model.entity.Lancamento;
import br.com.estudo.fullstack.model.entity.Usuario;
import br.com.estudo.fullstack.model.enums.StatusLancamento;
import br.com.estudo.fullstack.model.enums.TipoLancamento;
import br.com.estudo.fullstack.service.LancamentoService;
import br.com.estudo.fullstack.service.UsuarioService;

@RestController
@RequestMapping("/api/lancamentos")
public class LancamentoResource {

	private LancamentoService service;

	private UsuarioService usuarioService;

	private LancamentoResource(LancamentoService service, UsuarioService usuarioService) {
		this.service = service;
		this.usuarioService = usuarioService;
	}

	@PostMapping
	public ResponseEntity salvar(@RequestBody LancamentoDTO dto) {
		try {
			Lancamento entidade = converter(dto);
			entidade = service.salvarLancamento(entidade);
			return new ResponseEntity(entidade, HttpStatus.CREATED);
		} catch (RegraNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@PutMapping("{id}")
	public ResponseEntity atualizar(@PathVariable("id") Long id, @RequestBody LancamentoDTO dto) {

		return service.buscarPorId(id).map(entidade -> {
			try {
				Lancamento lancamento = converter(dto);
				lancamento.setId(id);
				service.atualizarLancamento(lancamento);
				return new ResponseEntity(lancamento, HttpStatus.OK);
			} catch (RegraNegocioException e) {
				return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
			}
		}).orElseGet(() -> new ResponseEntity("Lançamento não encontrado na Base de Dados.", HttpStatus.BAD_REQUEST));
	}

	@PutMapping("{id}/atualiza-status")
	public ResponseEntity atualizarStatus(@PathVariable("id") Long id,@RequestBody AtualizaStatusDTO dto) {
		return service.buscarPorId(id).map(entidade -> {
			StatusLancamento statusSelecionado = StatusLancamento.valueOf(dto.getStatus());
			
			if (statusSelecionado == null) {
				return new ResponseEntity("Não foi possível atualizar o status do lançamento, envie um status válido", HttpStatus.BAD_REQUEST);
			}
			
			try {
			entidade.setStatus(statusSelecionado);
			service.atualizarLancamento(entidade);
			return new ResponseEntity(entidade, HttpStatus.OK);
			}catch(RegraNegocioException e){
				return new ResponseEntity(e.getMessage(),HttpStatus.BAD_REQUEST);
			}
		}).orElseGet(() -> new ResponseEntity("Lançamento não encontrado na Base de Dados.", HttpStatus.BAD_REQUEST));
	}

	@DeleteMapping("{id}")
	public ResponseEntity deletar(@PathVariable("id") Long id) {
		return service.buscarPorId(id).map(entidade -> {
			service.deletarLancamento(entidade);
			return new ResponseEntity(HttpStatus.NO_CONTENT);
		}).orElseGet(() -> new ResponseEntity("Lançamento não encontrado na Base de Dados.", HttpStatus.BAD_REQUEST));
	}

	@GetMapping
	public ResponseEntity buscar(@RequestParam(value = "descricao", required = false) String descricao,
			@RequestParam(value = "mes", required = false) Integer mes,
			@RequestParam(value = "ano", required = false) Integer ano,
			@RequestParam(value = "usuario", required = false) Long idUsuario) {
		Lancamento lancamentoFiltro = new Lancamento();
		lancamentoFiltro.setDescricao(descricao);
		lancamentoFiltro.setMes(mes);
		lancamentoFiltro.setAno(ano);

		Optional<Usuario> usuario = usuarioService.buscarPorId(idUsuario);
		if (!usuario.isPresent()) {
			return new ResponseEntity("Usuário não encontrado com o Id informado.", HttpStatus.BAD_REQUEST);
		} else {
			lancamentoFiltro.setUsuario(usuario.get());
		}
		List<Lancamento> lancamentos = service.buscarLancamentos(lancamentoFiltro);
		return new ResponseEntity(lancamentos, HttpStatus.OK);
	}

	public Lancamento converter(LancamentoDTO dto) {

		Lancamento lancamento = new Lancamento();
		lancamento.setId(dto.getId());
		lancamento.setDescricao(dto.getDescricao());
		lancamento.setMes(dto.getMes());
		lancamento.setAno(dto.getAno());
		lancamento.setValor(dto.getValor());

		Usuario usuario = usuarioService.buscarPorId(dto.getUsuario())
				.orElseThrow(() -> new RegraNegocioException("Usuário não encontrado com o Id informado."));
		lancamento.setUsuario(usuario);

		if (dto.getTipo() != null) {
			lancamento.setTipo(TipoLancamento.valueOf(dto.getTipo()));
		}
		if (dto.getStatus() != null) {
			lancamento.setStatus(StatusLancamento.valueOf(dto.getStatus()));
		}

		return lancamento;
	}
}
