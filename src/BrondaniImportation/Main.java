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
