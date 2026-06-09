/**
 * ObjetoOrbital: o lixo espacial em si (satelite morto, estagio de foguete ou fragmento).
 * E o produto principal do marketplace: o que as agencias querem ver removido.
 *
 * O metodo impactoNaTerra() e o DIFERENCIAL do projeto: traduz o nivel de risco de
 * colisao em um selo que explica o impacto daquele detrito na vida na Terra
 * (GPS, meteorologia, comunicacoes, observacao da Terra, agro por satelite).
 */
public class ObjetoOrbital {

    private int id;
    private String nome;
    private TipoObjeto tipo;
    private int altitudeKm;
    private double massaKg;
    private NivelRisco nivelRiscoColisao;
    private StatusRemocao statusRemocao;
    private ClienteOperador dono;

    public ObjetoOrbital() {
    }

    public ObjetoOrbital(int id, String nome, TipoObjeto tipo, int altitudeKm,
                         double massaKg, NivelRisco nivelRiscoColisao,
                         StatusRemocao statusRemocao, ClienteOperador dono) {
        this.id = id;
        this.nome = nome;
        this.tipo = tipo;
        this.altitudeKm = altitudeKm;
        this.massaKg = massaKg;
        this.nivelRiscoColisao = nivelRiscoColisao;
        this.statusRemocao = statusRemocao;
        this.dono = dono;
    }

    /**
     * DIFERENCIAL — Selo "Impacto na Terra".
     * Retorna o texto que conecta o risco de colisao deste objeto ao impacto
     * concreto na Terra, conforme o nivel de risco de colisao:
     *   CRITICO -> "Ameaca direta a satelites de GPS e meteorologia"
     *   ALTO    -> "Risco a comunicacoes e a observacao da Terra"
     *   MEDIO   -> interferencia em satelites de imagem e agro por satelite
     *   BAIXO   -> monitoramento de rotina, sem ameaca imediata
     * O texto vive no proprio enum NivelRisco, mantendo a regra em um unico lugar.
     */
    public String impactoNaTerra() {
        if (nivelRiscoColisao == null) {
            return "Impacto na Terra ainda nao avaliado";
        }
        return nivelRiscoColisao.getImpactoNaTerra();
    }

    // Getters e Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public TipoObjeto getTipo() {
        return tipo;
    }

    public void setTipo(TipoObjeto tipo) {
        this.tipo = tipo;
    }

    public int getAltitudeKm() {
        return altitudeKm;
    }

    public void setAltitudeKm(int altitudeKm) {
        this.altitudeKm = altitudeKm;
    }

    public double getMassaKg() {
        return massaKg;
    }

    public void setMassaKg(double massaKg) {
        this.massaKg = massaKg;
    }

    public NivelRisco getNivelRiscoColisao() {
        return nivelRiscoColisao;
    }

    public void setNivelRiscoColisao(NivelRisco nivelRiscoColisao) {
        this.nivelRiscoColisao = nivelRiscoColisao;
    }

    public StatusRemocao getStatusRemocao() {
        return statusRemocao;
    }

    public void setStatusRemocao(StatusRemocao statusRemocao) {
        this.statusRemocao = statusRemocao;
    }

    public ClienteOperador getDono() {
        return dono;
    }

    public void setDono(ClienteOperador dono) {
        this.dono = dono;
    }

    @Override
    public String toString() {
        String nomeDono = (dono != null) ? dono.getNomeAgencia() : "sem dono";
        return "[#" + id + "] " + nome
                + " | tipo: " + tipo.getRotulo()
                + " | altitude: " + altitudeKm + " km"
                + " | massa: " + String.format(java.util.Locale.US, "%,.2f", massaKg) + " kg"
                + " | risco: " + nivelRiscoColisao.getRotulo()
                + " | status: " + statusRemocao.getRotulo()
                + " | dono: " + nomeDono;
    }
}
