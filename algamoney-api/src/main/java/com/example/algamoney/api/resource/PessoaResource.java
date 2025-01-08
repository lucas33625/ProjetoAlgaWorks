package com.example.algamoney.api.resource;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.algamoney.api.event.RecursoCriadoEvento;
import com.example.algamoney.api.model.Pessoa;
import com.example.algamoney.api.repository.PessoaRepository;
import com.example.algamoney.api.service.PessoaService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/pessoas")
public class PessoaResource {

	@Autowired
	private PessoaService pessoaService;

	@Autowired
	private PessoaRepository pessoaRepository;

	@Autowired
	private ApplicationEventPublisher publisher;

	@GetMapping
	public List<Pessoa> listar() {
		return pessoaRepository.findAll();
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Pessoa> criarPessoa(@Valid @RequestBody Pessoa pessoa, HttpServletResponse response) {
		Pessoa pessoaSalva = pessoaRepository.save(pessoa);

		publisher.publishEvent(new RecursoCriadoEvento(this, response, pessoaSalva.getCodigo()));

		return ResponseEntity.status(HttpStatus.CREATED).body(pessoaSalva);
	}

	@GetMapping("/{codigo}")
	public ResponseEntity<Pessoa> buscarPeloCodigo(@PathVariable Long codigo) {
		Pessoa pessoa = pessoaRepository.findById(codigo).orElseThrow(
				() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pessoa não encontrada na base de dados!"));
		return ResponseEntity.ok(pessoa);
	}

	@DeleteMapping("/{codigo}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deletarPeloCodigo(@PathVariable Long codigo) {
		Pessoa pessoa = pessoaRepository.findById(codigo).orElseThrow(
				() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ID não encontrado para ser deletado!"));
		pessoaRepository.delete(pessoa);
	}

	@PutMapping("/{codigo}")
	public ResponseEntity<Pessoa> atualizar(@PathVariable Long codigo, @RequestBody Pessoa pessoa) {

		Pessoa pessoaAtualizada = pessoaService.Atualizar(codigo, pessoa);

		return ResponseEntity.ok(pessoaAtualizada);

	}

	@PutMapping("/{codigo}/ativo")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void atualizarPropriedadeAtivo(@PathVariable Long codigo, @RequestBody Boolean ativo) {

		pessoaService.atualizarPropriedadeAtivo(codigo, ativo);
	}

}
