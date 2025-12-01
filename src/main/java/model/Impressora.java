package model;

import java.time.LocalDate;

public class Impressora {

    private Integer id;
    private String localInstalacao;
    private String modeloEquipamento;
    private String numeroSerie;
    private Integer contadorImpressoes;
    private Integer contadorAnterior;  // NOVO
    private LocalDate dataUltimaManutencao;
    private String secretaria;
    private String status;  // NOVO

    public Impressora() {
    }
    
    public Impressora(Integer id) {
        this.id = id;
    }

    public Impressora(String localInstalacao, String modeloEquipamento, String numeroSerie, 
                     Integer contadorImpressoes, Integer contadorAnterior, 
                     LocalDate dataUltimaManutencao, String secretaria, String status) {
        this.localInstalacao = localInstalacao;
        this.modeloEquipamento = modeloEquipamento;
        this.numeroSerie = numeroSerie;
        this.contadorImpressoes = contadorImpressoes;
        this.contadorAnterior = contadorAnterior;
        this.dataUltimaManutencao = dataUltimaManutencao;
        this.secretaria = secretaria;
        this.status = status;
    }

    public Impressora(Integer id, String localInstalacao, String modeloEquipamento, 
                     String numeroSerie, Integer contadorImpressoes, Integer contadorAnterior,
                     LocalDate dataUltimaManutencao, String secretaria, String status) {
        this.id = id;
        this.localInstalacao = localInstalacao;
        this.modeloEquipamento = modeloEquipamento;
        this.numeroSerie = numeroSerie;
        this.contadorImpressoes = contadorImpressoes;
        this.contadorAnterior = contadorAnterior;
        this.dataUltimaManutencao = dataUltimaManutencao;
        this.secretaria = secretaria;
        this.status = status;
    }

    // Método para calcular impressões do mês
    public Integer getImpressoesDoMes() {
        if (contadorAnterior == null || contadorAnterior == 0) {
            return 0;
        }
        return contadorImpressoes - contadorAnterior;
    }

    // Getters e Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLocalInstalacao() {
        return localInstalacao;
    }

    public void setLocalInstalacao(String localInstalacao) {
        this.localInstalacao = localInstalacao;
    }

    public String getModeloEquipamento() {
        return modeloEquipamento;
    }

    public void setModeloEquipamento(String modeloEquipamento) {
        this.modeloEquipamento = modeloEquipamento;
    }

    public String getNumeroSerie() {
        return numeroSerie;
    }

    public void setNumeroSerie(String numeroSerie) {
        this.numeroSerie = numeroSerie;
    }

    public Integer getContadorImpressoes() {
        return contadorImpressoes;
    }

    public void setContadorImpressoes(Integer contadorImpressoes) {
        this.contadorImpressoes = contadorImpressoes;
    }

    public Integer getContadorAnterior() {
        return contadorAnterior;
    }

    public void setContadorAnterior(Integer contadorAnterior) {
        this.contadorAnterior = contadorAnterior;
    }

    public LocalDate getDataUltimaManutencao() {
        return dataUltimaManutencao;
    }

    public void setDataUltimaManutencao(LocalDate dataUltimaManutencao) {
        this.dataUltimaManutencao = dataUltimaManutencao;
    }

    public String getSecretaria() {
        return secretaria;
    }

    public void setSecretaria(String secretaria) {
        this.secretaria = secretaria;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Impressora{" +
                "id=" + id +
                ", localInstalacao='" + localInstalacao + '\'' +
                ", modeloEquipamento='" + modeloEquipamento + '\'' +
                ", numeroSerie='" + numeroSerie + '\'' +
                ", contadorImpressoes=" + contadorImpressoes +
                ", contadorAnterior=" + contadorAnterior +
                ", dataUltimaManutencao=" + dataUltimaManutencao +
                ", secretaria='" + secretaria + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}