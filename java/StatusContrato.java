/**
 * Estagio de um contrato de remocao (o "pedido" do marketplace).
 * PROPOSTO -> AGENDADO -> CONCLUIDO, ou CANCELADO a qualquer momento.
 */
public enum StatusContrato {

    PROPOSTO("Proposto"),
    AGENDADO("Agendado"),
    CONCLUIDO("Concluido"),
    CANCELADO("Cancelado");

    private final String rotulo;

    StatusContrato(String rotulo) {
        this.rotulo = rotulo;
    }

    public String getRotulo() {
        return rotulo;
    }
}
