package br.com.alura.forum.controller.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;

import br.com.alura.forum.modelo.Topico;

public class TopicoDto {
	
	private Long id;
	private String titulo;
	private String mensagem;
	private LocalDateTime dataCriacao;
	
	public TopicoDto(Topico topico) {
		this.setId(topico.getId());
		this.setTitulo(topico.getTitulo());
		this.setMensagem(topico.getMensagem());
		this.setDataCriacao(topico.getDataCriacao());
	}
	
	public static List<TopicoDto> converter(List<Topico> topicos){
		return topicos.stream().map(TopicoDto::new).collect(Collectors.toList());
	}
	
	public static Page<TopicoDto> converterToPage(Page<Topico> topicos){
		return topicos.map(TopicoDto::new);
	}
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getTitulo() {
		return titulo;
	}
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}
	public String getMensagem() {
		return mensagem;
	}
	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}
	public LocalDateTime getDataCriacao() {
		return dataCriacao;
	}
	public void setDataCriacao(LocalDateTime dataCriacao) {
		this.dataCriacao = dataCriacao;
	}
	
}
