/**
 * Estagio do objeto orbital dentro do fluxo de remocao do marketplace.
 * CATALOGADO -> EM_NEGOCIACAO -> AGENDADO -> REMOVIDO.
 */
public enum StatusRemocao {

    CATALOGADO("Catalogado"),
    EM_NEGOCIACAO("Em negociacao"),
    AGENDADO("Agendado"),
    REMOVIDO("Removido");

    private final String rotulo;

    StatusRemocao(String rotulo) {
        this.rotulo = rotulo;
    }

    public String getRotulo() {
        return rotulo;
    }
}
