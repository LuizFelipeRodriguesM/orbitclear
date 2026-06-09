/**
 * ClienteOperador: a agencia / empresa dona de um objeto em orbita.
 * E quem contrata a remocao no marketplace (ex.: NASA, ESA, INPE).
 */
public class ClienteOperador {

    private int id;
    private String nomeAgencia;
    private String paisOrigem;
    private String contatoEmail;

    public ClienteOperador() {
    }

    public ClienteOperador(int id, String nomeAgencia, String paisOrigem, String contatoEmail) {
        this.id = id;
        this.nomeAgencia = nomeAgencia;
        this.paisOrigem = paisOrigem;
        this.contatoEmail = contatoEmail;
    }

    // Getters e Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNomeAgencia() {
        return nomeAgencia;
    }

    public void setNomeAgencia(String nomeAgencia) {
        this.nomeAgencia = nomeAgencia;
    }

    public String getPaisOrigem() {
        return paisOrigem;
    }

    public void setPaisOrigem(String paisOrigem) {
        this.paisOrigem = paisOrigem;
    }

    public String getContatoEmail() {
        return contatoEmail;
    }

    public void setContatoEmail(String contatoEmail) {
        this.contatoEmail = contatoEmail;
    }

    @Override
    public String toString() {
        return "[#" + id + "] " + nomeAgencia
                + " (" + paisOrigem + ") | contato: " + contatoEmail;
    }
}
