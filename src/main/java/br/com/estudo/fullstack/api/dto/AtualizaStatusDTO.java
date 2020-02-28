package br.com.estudo.fullstack.api.dto;

public class AtualizaStatusDTO {

	String status;

	public AtualizaStatusDTO(String status) {
		super();
		this.status = status;
	}

	public AtualizaStatusDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
