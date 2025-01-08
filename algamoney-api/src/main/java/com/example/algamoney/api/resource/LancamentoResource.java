package com.example.algamoney.api.resource;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.algamoney.api.event.RecursoCriadoEvento;
import com.example.algamoney.api.model.Lancamento;
import com.example.algamoney.api.repository.LancamentoRepository;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;



@RestController
@RequestMapping("/lancamentos")
public class LancamentoResource {
	
	@Autowired
	private LancamentoRepository lancamentoRepository;

	@Autowired
	private ApplicationEventPublisher publisher;
	
	@GetMapping
	public List<Lancamento> listar() {
		return lancamentoRepository.findAll();
	}
	
	@GetMapping("/{codigo}")
	public ResponseEntity<Lancamento> buscarPeloCodigo(@PathVariable Long codigo) {
		Lancamento lancamento = lancamentoRepository.findById(codigo).orElseThrow(
				() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lançamento não encontrada na base de dados!"));
		return ResponseEntity.ok(lancamento);
	}
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Lancamento> criarLancamento(@Valid @RequestBody Lancamento lancamento, HttpServletResponse response) {
		Lancamento lancamentoSalvo = lancamentoRepository.save(lancamento);

		publisher.publishEvent(new RecursoCriadoEvento(this, response, lancamentoSalvo.getCodigo()));

		return ResponseEntity.status(HttpStatus.CREATED).body(lancamentoSalvo);
	}
	
	
}
