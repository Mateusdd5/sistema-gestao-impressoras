package model;

import java.time.LocalDate;
import java.math.BigDecimal;

public class Impressora {

    private Integer id;
    private String localInstalacao;
    private String modeloEquipamento;
    private BigDecimal custoPorImpressao;  // NOVO
    private String numeroSerie;
    private Integer contadorImpressoes;
    private Integer contadorAnterior;
    private LocalDate dataUltimaManutencao;
    private String secretaria;
    private String status;

    public Impressora() {
    }
    
    public Impressora(Integer id) {
        this.id = id;
    }

    public Impressora(String localInstalacao, String modeloEquipamento, BigDecimal custoPorImpressao,
                     String numeroSerie, Integer contadorImpressoes, Integer contadorAnterior, 
                     LocalDate dataUltimaManutencao, String secretaria, String status) {
        this.localInstalacao = localInstalacao;
        this.modeloEquipamento = modeloEquipamento;
        this.custoPorImpressao = custoPorImpressao;
        this.numeroSerie = numeroSerie;
        this.contadorImpressoes = contadorImpressoes;
        this.contadorAnterior = contadorAnterior;
        this.dataUltimaManutencao = dataUltimaManutencao;
        this.secretaria = secretaria;
        this.status = status;
    }

    public Impressora(Integer id, String localInstalacao, String modeloEquipamento, 
                     BigDecimal custoPorImpressao, String numeroSerie, Integer contadorImpressoes, 
                     Integer contadorAnterior, LocalDate dataUltimaManutencao, String secretaria, String status) {
        this.id = id;
        this.localInstalacao = localInstalacao;
        this.modeloEquipamento = modeloEquipamento;
        this.custoPorImpressao = custoPorImpressao;
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

    // NOVO - Método para calcular custo mensal da impressora
    public BigDecimal getCustoMensal() {
        if (custoPorImpressao == null || custoPorImpressao.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        Integer impressoes = getImpressoesDoMes();
        return custoPorImpressao.multiply(new BigDecimal(impressoes));
    }

    // NOVO - Método estático para detectar custo por modelo
    public static BigDecimal detectarCustoPorModelo(String modelo) {
        if (modelo == null || modelo.trim().isEmpty()) {
            return null;
        }
        
        String modeloUpper = modelo.toUpperCase().replaceAll("\\s+", "");
        
        // M-3655IDN = R$ 0,12
        if (modeloUpper.contains("M-3655") || modeloUpper.contains("M3655")) {
            return new BigDecimal("0.12");
        }
        
        // M-2040DN/L = R$ 0,07
        if (modeloUpper.contains("M-2040") || modeloUpper.contains("M2040")) {
            return new BigDecimal("0.07");
        }
        
        // P3145DN/HLL 6202DW = R$ 0,07
        if (modeloUpper.contains("P-3145") || modeloUpper.contains("P3145") ||
            modeloUpper.contains("HL") && modeloUpper.contains("6202") ||
            modeloUpper.contains("HLL") && modeloUpper.contains("6202")) {
            return new BigDecimal("0.07");
        }
        
        // P-6235CDN = R$ 0,69
        if (modeloUpper.contains("P-6235") || modeloUpper.contains("P6235")) {
            return new BigDecimal("0.69");
        }
        
        // CANON/TM-300 = R$ 14,37
        if (modeloUpper.contains("TM-300") || modeloUpper.contains("TM300") ||
            (modeloUpper.contains("CANON") && modeloUpper.contains("300"))) {
            return new BigDecimal("14.37");
        }
        
        // Modelo não reconhecido
        return null;
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

    public BigDecimal getCustoPorImpressao() {
        return custoPorImpressao;
    }

    public void setCustoPorImpressao(BigDecimal custoPorImpressao) {
        this.custoPorImpressao = custoPorImpressao;
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
                ", custoPorImpressao=" + custoPorImpressao +
                ", numeroSerie='" + numeroSerie + '\'' +
                ", contadorImpressoes=" + contadorImpressoes +
                ", contadorAnterior=" + contadorAnterior +
                ", dataUltimaManutencao=" + dataUltimaManutencao +
                ", secretaria='" + secretaria + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}