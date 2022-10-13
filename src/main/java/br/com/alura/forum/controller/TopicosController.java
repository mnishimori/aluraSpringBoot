package br.com.alura.forum.controller;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
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
import org.springframework.web.util.UriComponentsBuilder;

import br.com.alura.forum.controller.dto.DetalhesDoTopicoDto;
import br.com.alura.forum.controller.dto.TopicoDto;
import br.com.alura.forum.controller.form.AtualizacaoTopicoForm;
import br.com.alura.forum.controller.form.TopicoForm;
import br.com.alura.forum.modelo.Topico;
import br.com.alura.forum.repository.CursoRepository;
import br.com.alura.forum.repository.TopicoRepository;

@RestController
@RequestMapping("/topicos")
public class TopicosController {
	
	@Autowired
	private TopicoRepository topicoRepository;
	
	@Autowired
	private CursoRepository cursoRepository;
	
	@GetMapping
	public List<TopicoDto> lista() {
		
		List<Topico> topicos = topicoRepository.findAll();
		
		return TopicoDto.converter(topicos);
	}
	
	@GetMapping("/por-nome")
	public Page<TopicoDto> listarTopicoPorNomeCurso(@RequestParam(required = false) String nomeCurso, 
			@RequestParam Integer page, @RequestParam Integer size, @RequestParam String ordenacao) {
		
		Pageable pageable = PageRequest.of(page, size, Direction.ASC, ordenacao);
		
		Page<Topico> topicos = null;
		
		if (nomeCurso == null || nomeCurso.isBlank()) {
			topicos = topicoRepository.findAll(pageable);
		} else {
			topicos = topicoRepository.findByCursoNome(nomeCurso, pageable);
		}
		
		return TopicoDto.converterToPage(topicos);
	}
	
	@GetMapping("/por-nome-pageable")
	@Cacheable(value = "listarTopicoPorNomeCursoPageable")
	public Page<TopicoDto> listarTopicoPorNomeCursoPageable(@RequestParam(required = false) String nomeCurso, 
			@PageableDefault(page = 0, size = 3, sort = "id", direction = Direction.DESC) Pageable pageable) {
		
		Page<Topico> topicos = null;
		
		if (nomeCurso == null || nomeCurso.isBlank()) {
			topicos = topicoRepository.findAll(pageable);
		} else {
			topicos = topicoRepository.findByCursoNome(nomeCurso, pageable);
		}
		
		return TopicoDto.converterToPage(topicos);
	}
	
	@CacheEvict(value = "listarTopicoPorNomeCursoPageable", allEntries = true)
	@PostMapping
	public ResponseEntity<TopicoDto> cadastrar(@RequestBody @Valid TopicoForm topicoForm, 
			UriComponentsBuilder uriComponentsBuilder) {
		
		Topico topico = topicoForm.converter(cursoRepository);
		
		topicoRepository.save(topico);
		
		URI uri =  uriComponentsBuilder.path("/topicos/{id}").buildAndExpand(topico.getId()).toUri();
		
		return ResponseEntity.created(uri).body(new TopicoDto(topico));
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<DetalhesDoTopicoDto> detalhar(@PathVariable Long id) {
		
		Topico topico = this.getTopico(id);
		
		return topico != null 
				? ResponseEntity.ok(new DetalhesDoTopicoDto(topico)) 
				: ResponseEntity.notFound().build();
	}

	@CacheEvict(value = "listarTopicoPorNomeCursoPageable", allEntries = true)
	@Transactional
	@PutMapping("/{id}")
	public ResponseEntity<TopicoDto> atualizar(@PathVariable Long id, 
			@RequestBody @Valid AtualizacaoTopicoForm topicoForm) {
		
		Topico topico = this.getTopico(id);
		
		if(topico != null) {
			topico = topicoForm.atualizar(id, topicoRepository);
			
			return ResponseEntity.ok(new TopicoDto(topico));
		}
		
		return ResponseEntity.notFound().build();
	}
	
	@CacheEvict(value = "listarTopicoPorNomeCursoPageable", allEntries = true)
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deletar(@PathVariable Long id) {
		Topico topico = this.getTopico(id);
		
		if(topico != null) {
			topicoRepository.deleteById(id);
			
			return ResponseEntity.ok().build();
		}
		
		return ResponseEntity.notFound().build();
	}

	private Topico getTopico(Long id) {
		
		Optional<Topico> topicoOpt = topicoRepository.findById(id);
		
		return topicoOpt.orElse(null);
	}
	
}
