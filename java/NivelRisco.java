/**
 * Nivel de risco de colisao de um objeto orbital.
 * O nivel e a base do selo "Impacto na Terra" (o diferencial do projeto):
 * cada valor guarda o texto do impacto que aquele risco representa para a vida na Terra.
 */
public enum NivelRisco {

    BAIXO("Baixo",
            "Monitoramento de rotina: sem ameaca imediata aos servicos orbitais da Terra"),
    MEDIO("Medio",
            "Pode interferir em rotas de satelites de imagem e no agro por satelite"),
    ALTO("Alto",
            "Risco a comunicacoes e a observacao da Terra"),
    CRITICO("Critico",
            "Ameaca direta a satelites de GPS e meteorologia");

    private final String rotulo;
    private final String impactoNaTerra;

    NivelRisco(String rotulo, String impactoNaTerra) {
        this.rotulo = rotulo;
        this.impactoNaTerra = impactoNaTerra;
    }

    public String getRotulo() {
        return rotulo;
    }

    /** Texto do selo "Impacto na Terra" associado a este nivel de risco. */
    public String getImpactoNaTerra() {
        return impactoNaTerra;
    }
}
