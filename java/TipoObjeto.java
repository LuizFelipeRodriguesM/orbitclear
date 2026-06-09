/**
 * Tipo de lixo espacial catalogado no marketplace.
 * Cada valor carrega um rotulo legivel para exibicao no menu (PT-BR).
 */
public enum TipoObjeto {

    SATELITE_MORTO("Satelite morto"),
    ESTAGIO_FOGUETE("Estagio de foguete"),
    FRAGMENTO("Fragmento");

    private final String rotulo;

    TipoObjeto(String rotulo) {
        this.rotulo = rotulo;
    }

    public String getRotulo() {
        return rotulo;
    }
}
