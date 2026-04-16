package model;

import java.time.LocalDate;
import java.math.BigDecimal;

public class Impressora {

    private Integer id;
    private String localInstalacao;
    private String modeloEquipamento;
    private BigDecimal custoPorImpressao;
    private String numeroSerie;
    private BigDecimal contadorImpressoes;
    private BigDecimal contadorAnterior;
    private LocalDate dataUltimaManutencao;
    private LocalDate dataRelatorioAnterior;
    private String secretaria;
    private String status;
    private Boolean incluirNoCalculo;

    public Impressora() {
        this.incluirNoCalculo = true;
    }

    public Impressora(Integer id) {
        this.id = id;
        this.incluirNoCalculo = true;
    }

    public Impressora(String localInstalacao, String modeloEquipamento, BigDecimal custoPorImpressao,
                     String numeroSerie, BigDecimal contadorImpressoes, BigDecimal contadorAnterior,
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
        this.incluirNoCalculo = true;
    }

    public Impressora(Integer id, String localInstalacao, String modeloEquipamento,
                     BigDecimal custoPorImpressao, String numeroSerie, BigDecimal contadorImpressoes,
                     BigDecimal contadorAnterior, LocalDate dataUltimaManutencao, String secretaria, String status) {
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
        this.incluirNoCalculo = true;
    }

    // Método para calcular impressões do mês
    public BigDecimal getImpressoesDoMes() {
        if (contadorAnterior == null || contadorAnterior.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return contadorImpressoes.subtract(contadorAnterior);
    }

    // Método para calcular custo mensal da impressora
    // Retorna ZERO se a impressora estiver marcada como "não incluir no cálculo"
    public BigDecimal getCustoMensal() {
        if (incluirNoCalculo == null || !incluirNoCalculo) {
            return BigDecimal.ZERO;
        }
        if (custoPorImpressao == null || custoPorImpressao.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal impressoes = getImpressoesDoMes();
        return custoPorImpressao.multiply(impressoes);
    }

    // Método estático para detectar custo por modelo
    public static BigDecimal detectarCustoPorModelo(String modelo) {
        if (modelo == null || modelo.trim().isEmpty()) {
            return null;
        }

        String modeloUpper = modelo.toUpperCase().replaceAll("\\s+", "").replaceAll("-", "");

        // M-3655IDN = R$ 0,12
        if (modeloUpper.contains("M3655")) {
            return new BigDecimal("0.12");
        }

        // M-2040DN/L = R$ 0,07
        if (modeloUpper.contains("M2040")) {
            return new BigDecimal("0.07");
        }

        // P3145DN/P3045DN/HLL 6202DW = R$ 0,07
        if (modeloUpper.contains("P3145") || modeloUpper.contains("P3045") ||
            modeloUpper.contains("HL6202") || modeloUpper.contains("HLL6202")) {
            return new BigDecimal("0.07");
        }

        // P-6235CDN = R$ 0,69
        if (modeloUpper.contains("P6235")) {
            return new BigDecimal("0.69");
        }

        // CANON/TM-300 = R$ 14,37
        if (modeloUpper.contains("TM300") ||
            (modeloUpper.contains("CANON") && modeloUpper.contains("300"))) {
            return new BigDecimal("14.37");
        }

        // ECOSYS MA4000x/L = R$ 0,07
        if (modeloUpper.contains("MA4000") || modeloUpper.contains("ECOSYSMA4000")) {
            return new BigDecimal("0.07");
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

    public BigDecimal getContadorImpressoes() {
        return contadorImpressoes;
    }

    public void setContadorImpressoes(BigDecimal contadorImpressoes) {
        this.contadorImpressoes = contadorImpressoes;
    }

    public BigDecimal getContadorAnterior() {
        return contadorAnterior;
    }

    public void setContadorAnterior(BigDecimal contadorAnterior) {
        this.contadorAnterior = contadorAnterior;
    }

    public LocalDate getDataUltimaManutencao() {
        return dataUltimaManutencao;
    }

    public void setDataUltimaManutencao(LocalDate dataUltimaManutencao) {
        this.dataUltimaManutencao = dataUltimaManutencao;
    }

    public LocalDate getDataRelatorioAnterior() {
        return dataRelatorioAnterior;
    }

    public void setDataRelatorioAnterior(LocalDate dataRelatorioAnterior) {
        this.dataRelatorioAnterior = dataRelatorioAnterior;
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

    public Boolean getIncluirNoCalculo() {
        return incluirNoCalculo;
    }

    public void setIncluirNoCalculo(Boolean incluirNoCalculo) {
        this.incluirNoCalculo = incluirNoCalculo;
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
                ", dataRelatorioAnterior=" + dataRelatorioAnterior +
                ", secretaria='" + secretaria + '\'' +
                ", status='" + status + '\'' +
                ", incluirNoCalculo=" + incluirNoCalculo +
                '}';
    }
}