package BrondaniImportation;

import Entity.Executavel;
import Robo.AppRobo;
import fileManager.FileManager;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;
import java.util.LinkedHashMap;
import org.ini4j.Ini;

public class Main {

    private static String nomeApp = "";
    private static Ini ini = null;

    public static String testParameters = "";

    /**
     * Pega na apsta do mes arquivos txt 'filial', 'matriz' e 'viamão'
     * <p>
     * Junta texto dos arquivos txt em uma variavel
     * <p>
     * Transforma texto dos arquivos txt em mapa com a class CSV
     * <p>
     * Pega o arquivo de contas csv, salvo na escrituração mensal.
     * Transforma o texto do arquivo de contas em um mapa com a chave igual a
     * conta contabil da brondani e o valor a conta contabil da moresco.
     * <p>
     * Percorre mapa dos arquivos txt
     * ----Cria String conforme layout de importacao no unico com as colunas do
     * txt.
     * ----Nas contas de debito e credito busca no mapa das contas, se nao
     * existir, coloca aviso no log e adiciona conta que falta dentro do arquivo
     * csv.
     * <p>
     * Se o log nao estiver vazio, retorna o aviso para completar o arquivo csv
     * e onde ele esta localizado.
     * Se não tiver faltado nenhuma conta, cria o arquivo txt com o texto do
     * layout de importação do unico na pasta dos arquivos e retorna aviso que
     * salvou o arquivo de importação.
     */
    public static void main(String[] args) {
        try {
            AppRobo robo = new AppRobo(nomeApp);
            robo.definirParametros();

            if (args.length > 0 && args[0].equals("test")) {
                robo.definirParametros(testParameters);
            }

            String iniPath = "";
            String iniName = robo.getParametro("ini");

            ini = new Ini(FileManager.getFile(iniPath + iniName + ".ini"));

            int mes = Integer.valueOf(robo.getParametro("mes"));
            mes = mes >= 1 && mes <= 12 ? mes : 1;
            int ano = Integer.valueOf(robo.getParametro("ano"));

            nomeApp = "Importação Brondani - " + ini.get("Config", "nome") + " " + mes + "/" + ano;

            robo.setNome(nomeApp);
            robo.executar(start(mes, ano));
        } catch (Exception e) {
            e.printStackTrace();
            FileManager.save(new File(System.getProperty("user.home")) + "\\Desktop\\JavaError.txt", getStackTrace(e));
            System.out.println("Ocorreu um erro na aplicação: " + e);
            System.exit(0);
        }
    }

    private static String getStackTrace(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);

        return sw.toString();
    }

    public static String start(int mes, int ano) {
        Map<String, Executavel> execs = new LinkedHashMap<>();
        execs.put("Procurando arquivo X", new Executavel());

        return AppRobo.rodarExecutaveis(nomeApp, execs);
    }

}
