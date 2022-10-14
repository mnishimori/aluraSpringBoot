package br.com.alura.forum.controller.dto;

public class TokenDto {

	private String token;
	private String tipoToken;

	public TokenDto(String token, String string) {
		this.token = token;
		this.tipoToken = string;
	}

	public String getToken() {
		return token;
	}

	public String getTipoToken() {
		return tipoToken;
	}

}
