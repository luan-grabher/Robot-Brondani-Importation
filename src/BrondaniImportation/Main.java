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

    public static String folderPath;
    public static File accountsFile;
    public static File folder;
    public static Integer mes = 1;
    public static Integer ano = 2021;

    private static String nomeApp = "";
    public static Ini ini = null;

    public static String testParameters = "";

    public static void main(String[] args) {
        try {
            AppRobo robo = new AppRobo(nomeApp);
            robo.definirParametros();

            if (args.length > 0 && args[0].equals("test")) {
                robo.definirParametros(testParameters);
            }

            String iniPath = "./BrondaniImportation.ini";

            ini = new Ini(FileManager.getFile(iniPath));

            mes = Integer.valueOf(robo.getParametro("mes"));
            mes = mes >= 1 && mes <= 12 ? mes : 1;
            ano = Integer.valueOf(robo.getParametro("ano"));

            accountsFile = new File(ini.get("Pastas", "accountsFile"));

            // Arruma pasta
            folderPath = ini.get("Pastas", "folderPath").replaceAll(":ano", ano.toString());
            folderPath = folderPath.replaceAll(":mes", (mes < 10 ? "0" : "") + mes);
            folder = new File(folderPath);

            nomeApp = "Importação Brondani - " + mes + "/" + ano;

            robo.setNome(nomeApp);
            robo.executar(start(mes, ano));
        } catch (Exception e) {
            e.printStackTrace();
            FileManager.save(new File(System.getProperty("user.home")) + "\\Desktop\\JavaError.txt", getStackTrace(e));
            System.out.println("Ocorreu um erro na aplicação: " + e);
        }

        System.exit(0);
    }

    private static String getStackTrace(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);

        return sw.toString();
    }

    public static String start(int mes, int ano) {
        Map<String, Executavel> execs = new LinkedHashMap<>();
        execs.put("Pegar arquivo de contas na Escrituração Mensal", new Control.getAccounts());
        execs.put("Pegar arquivos na pasta do mes", new Control.getFiles());
        execs.put("Criar arquivo de importação", new Control.createImportationFile());

        return AppRobo.rodarExecutaveis(nomeApp, execs);
    }

}
