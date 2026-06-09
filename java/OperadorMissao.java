/**
 * OperadorMissao: a empresa caca-detritos que executa a remocao.
 * Anuncia no marketplace sua tecnologia, capacidade de carga e preco base
 * (ex.: ClearSpace, Astroscale, Orbital Cleaners).
 */
public class OperadorMissao {

    private int id;
    private String nomeEmpresa;
    private String pais;
    private TecnologiaCaptura tecnologiaCaptura;
    private double capacidadeKgPorMissao;
    private double precoBaseUSD;

    public OperadorMissao() {
    }

    public OperadorMissao(int id, String nomeEmpresa, String pais,
                          TecnologiaCaptura tecnologiaCaptura,
                          double capacidadeKgPorMissao, double precoBaseUSD) {
        this.id = id;
        this.nomeEmpresa = nomeEmpresa;
        this.pais = pais;
        this.tecnologiaCaptura = tecnologiaCaptura;
        this.capacidadeKgPorMissao = capacidadeKgPorMissao;
        this.precoBaseUSD = precoBaseUSD;
    }

    // Getters e Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNomeEmpresa() {
        return nomeEmpresa;
    }

    public void setNomeEmpresa(String nomeEmpresa) {
        this.nomeEmpresa = nomeEmpresa;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public TecnologiaCaptura getTecnologiaCaptura() {
        return tecnologiaCaptura;
    }

    public void setTecnologiaCaptura(TecnologiaCaptura tecnologiaCaptura) {
        this.tecnologiaCaptura = tecnologiaCaptura;
    }

    public double getCapacidadeKgPorMissao() {
        return capacidadeKgPorMissao;
    }

    public void setCapacidadeKgPorMissao(double capacidadeKgPorMissao) {
        this.capacidadeKgPorMissao = capacidadeKgPorMissao;
    }

    public double getPrecoBaseUSD() {
        return precoBaseUSD;
    }

    public void setPrecoBaseUSD(double precoBaseUSD) {
        this.precoBaseUSD = precoBaseUSD;
    }

    @Override
    public String toString() {
        return "[#" + id + "] " + nomeEmpresa + " (" + pais + ")"
                + " | tecnologia: " + tecnologiaCaptura.getRotulo()
                + " | capacidade: " + String.format(java.util.Locale.US, "%,.2f", capacidadeKgPorMissao) + " kg/missao"
                + " | preco base: US$ " + String.format(java.util.Locale.US, "%,.2f", precoBaseUSD);
    }
}
