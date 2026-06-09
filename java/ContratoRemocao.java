import java.time.LocalDate;

/**
 * ContratoRemocao: o "pedido" do marketplace. Liga um ObjetoOrbital (o lixo)
 * a um OperadorMissao (a empresa que vai remover), com data da janela de
 * desorbita, valor acordado e status do contrato.
 * E a entidade associativa da relacao N-N entre objetos e operadores.
 */
public class ContratoRemocao {

    private int id;
    private ObjetoOrbital objeto;
    private OperadorMissao operador;
    private LocalDate dataJanela;
    private double valorUSD;
    private StatusContrato status;

    public ContratoRemocao() {
    }

    public ContratoRemocao(int id, ObjetoOrbital objeto, OperadorMissao operador,
                           LocalDate dataJanela, double valorUSD, StatusContrato status) {
        this.id = id;
        this.objeto = objeto;
        this.operador = operador;
        this.dataJanela = dataJanela;
        this.valorUSD = valorUSD;
        this.status = status;
    }

    // Getters e Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ObjetoOrbital getObjeto() {
        return objeto;
    }

    public void setObjeto(ObjetoOrbital objeto) {
        this.objeto = objeto;
    }

    public OperadorMissao getOperador() {
        return operador;
    }

    public void setOperador(OperadorMissao operador) {
        this.operador = operador;
    }

    public LocalDate getDataJanela() {
        return dataJanela;
    }

    public void setDataJanela(LocalDate dataJanela) {
        this.dataJanela = dataJanela;
    }

    public double getValorUSD() {
        return valorUSD;
    }

    public void setValorUSD(double valorUSD) {
        this.valorUSD = valorUSD;
    }

    public StatusContrato getStatus() {
        return status;
    }

    public void setStatus(StatusContrato status) {
        this.status = status;
    }

    @Override
    public String toString() {
        String nomeObjeto = (objeto != null) ? objeto.getNome() : "objeto removido";
        String nomeOperador = (operador != null) ? operador.getNomeEmpresa() : "operador removido";
        return "[#" + id + "] " + nomeObjeto + " <- " + nomeOperador
                + " | janela: " + dataJanela
                + " | valor: US$ " + String.format(java.util.Locale.US, "%,.2f", valorUSD)
                + " | status: " + status.getRotulo();
    }
}
