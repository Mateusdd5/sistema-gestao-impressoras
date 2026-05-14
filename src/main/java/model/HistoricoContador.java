package model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Representa uma entrada no histórico de leituras de contador de uma impressora.
 * Cada registro corresponde a uma data de relatório distinta.
 * Limite de 60 registros por impressora (5 anos mensais).
 */
public class HistoricoContador {

    private Integer id;
    private Integer impressoraId;
    private LocalDate dataRelatorio;
    private BigDecimal contadorValor;
    private LocalDateTime dataGravacao;
    private Boolean editadoManual;
    private String editadoPor;

    // Campos auxiliares populados por JOIN (não persistidos diretamente)
    private String secretaria;
    private String localInstalacao;
    private String modeloEquipamento;
    private String numeroSerie;

    public HistoricoContador() {
        this.editadoManual = false;
    }

    public HistoricoContador(Integer impressoraId, LocalDate dataRelatorio, BigDecimal contadorValor) {
        this.impressoraId = impressoraId;
        this.dataRelatorio = dataRelatorio;
        this.contadorValor = contadorValor;
        this.editadoManual = false;
    }

    // Getters e Setters

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getImpressoraId() { return impressoraId; }
    public void setImpressoraId(Integer impressoraId) { this.impressoraId = impressoraId; }

    public LocalDate getDataRelatorio() { return dataRelatorio; }
    public void setDataRelatorio(LocalDate dataRelatorio) { this.dataRelatorio = dataRelatorio; }

    public BigDecimal getContadorValor() { return contadorValor; }
    public void setContadorValor(BigDecimal contadorValor) { this.contadorValor = contadorValor; }

    public LocalDateTime getDataGravacao() { return dataGravacao; }
    public void setDataGravacao(LocalDateTime dataGravacao) { this.dataGravacao = dataGravacao; }

    public Boolean getEditadoManual() { return editadoManual; }
    public void setEditadoManual(Boolean editadoManual) { this.editadoManual = editadoManual; }

    public String getEditadoPor() { return editadoPor; }
    public void setEditadoPor(String editadoPor) { this.editadoPor = editadoPor; }

    public String getSecretaria() { return secretaria; }
    public void setSecretaria(String secretaria) { this.secretaria = secretaria; }

    public String getLocalInstalacao() { return localInstalacao; }
    public void setLocalInstalacao(String localInstalacao) { this.localInstalacao = localInstalacao; }

    public String getModeloEquipamento() { return modeloEquipamento; }
    public void setModeloEquipamento(String modeloEquipamento) { this.modeloEquipamento = modeloEquipamento; }

    public String getNumeroSerie() { return numeroSerie; }
    public void setNumeroSerie(String numeroSerie) { this.numeroSerie = numeroSerie; }

    // Métodos auxiliares

    public String getDataRelatorioFormatada() {
        if (dataRelatorio == null) return "-";
        return dataRelatorio.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    public String getDataGravacaoFormatada() {
        if (dataGravacao == null) return "-";
        return dataGravacao.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    @Override
    public String toString() {
        return "HistoricoContador{" +
                "id=" + id +
                ", impressoraId=" + impressoraId +
                ", dataRelatorio=" + dataRelatorio +
                ", contadorValor=" + contadorValor +
                ", editadoManual=" + editadoManual +
                '}';
    }
}