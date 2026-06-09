/**
 * Tecnologia que um operador de missao usa para capturar o lixo espacial.
 * Cada valor tem um rotulo legivel para exibicao no menu (PT-BR).
 */
public enum TecnologiaCaptura {

    ARPAO("Arpao"),
    REDE("Rede"),
    BRACO_ROBOTICO("Braco robotico"),
    LASER("Laser");

    private final String rotulo;

    TecnologiaCaptura(String rotulo) {
        this.rotulo = rotulo;
    }

    public String getRotulo() {
        return rotulo;
    }
}
