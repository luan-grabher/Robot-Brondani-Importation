package BrondaniImportation;

import static BrondaniImportation.Main.accountsFile;
import static BrondaniImportation.Main.ano;
import static BrondaniImportation.Main.folder;
import static BrondaniImportation.Main.ini;
import static BrondaniImportation.Main.mes;
import Entity.Executavel;
import Entity.Warning;
import fileManager.CSV;
import fileManager.FileManager;
import fileManager.Selector;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Control {

    private static List<Map<String, String>> toImport = new ArrayList();
    private static final Map<String, String> accounts = new HashMap<>();
    private static final Map<String, String> accountsNotFind = new HashMap<>();
    private static final StringBuilder log = new StringBuilder();

    /**
     * Pega os arquivos definidos no ini e transforma em mapa
     */
    public static class getFiles extends Executavel {

        @Override
        public void run() {
            StringBuilder finalText = new StringBuilder(ini.get("Config", "header"));
            String[] files = ini.get("Config", "files").split(";");

            for (String fileName : files) {
                File file = Selector.getFileOnFolder(folder, fileName, "");

                if (file != null && file.exists()) {
                    finalText.append("\r\n");
                    finalText.append(FileManager.getText(file).replaceAll(",", ";").replaceAll("\"", ""));
                } else {
                    throw new Error("O arquivo " + fileName + " não foi encontrado na pasta:\n" + folder.getAbsolutePath());
                }
            }

            toImport = CSV.getMapFromText(finalText.toString(), ";");
        }

    }

    /**
     * Pega contas do arquivo de contas na escrituração mensal
     */
    public static class getAccounts extends Executavel {

        @Override
        public void run() {
            if (accountsFile.exists()) {
                List<Map<String, String>> mapAccountsFile = CSV.getMapFromText(FileManager.getText(accountsFile), ";");

                mapAccountsFile.forEach((ac) -> {
                    accounts.put(ac.get("brondani"), ac.get("moresco"));
                });
            } else {
                throw new Error("O arquivo seguinte arquivo não foi encontrado:\n" + accountsFile.getPath());
            }
        }

    }

    /**
     * Cria arquivo csv de importação com o mapa criado com os arquivos
     * anteriormente
     */
    public static class createImportationFile extends Executavel {

        @Override
        public void run() {
            String mesStr = (mes < 10 ? "0" : "") + mes;
            String anoStr = Integer.toString(ano - 2000);
            String mesano = mesStr + anoStr;

            StringBuilder importationText = new StringBuilder("#IMPORTAÇÃO ZAC");

            //Para cada importação
            toImport.forEach((map) -> {
                //empresa;ignora;data;debito;credito;valor1;valor2;ignora;historico

                String debit = accounts.get(map.get("debito"));
                String credit = accounts.get(map.get("credito"));

                String day = map.get("data").replaceAll(mesano, "");
                String date = day + "/" + mesStr + "/" + ano;

                if (debit == null) {
                    accountsNotFind.put(debit, debit);
                    log.append("A contas de Debito ").append(debit).append(" não foi encontrada no arquivo de contas.\n");
                } else if (credit == null) {
                    accountsNotFind.put(credit, credit);
                    log.append("A contas de Credito ").append(credit).append(" não foi encontrada no arquivo de contas.\n");
                } else if (log.length() == 0) {
                    importationText.append("\r\n");
                    importationText.append(
                            getImportRow(
                                    ini.get("Empresas", map.get("empresa")),
                                    date,
                                    debit,
                                    credit,
                                    map.get("historico"),
                                    map.get("valor1") + "," + map.get("valor2")
                            )
                    );
                }
            });

            //Se nao tiver log cria arquivp, se nao cria log
            if (log.length() == 0) {
                FileManager.save(folder, "importacao.csv", importationText.toString());
                throw new Warning("Arquivo 'importacao.csv' salvo na pasta:\n" + folder.getPath());
            } else {
                StringBuilder textToAdd = new StringBuilder();

                accountsNotFind.forEach((a, c) -> {
                    textToAdd.append(a).append(";;;");
                });

                FileManager.save(accountsFile, FileManager.getText(accountsFile) + textToAdd.toString());

                throw new Error(log.toString());
            }
        }

    }

    /**
     * Cria linha de importação para o csv com as informações
     *
     *
     * @param enterprise Empresa
     * @param date data no formato normal
     * @param debit debito
     * @param credit credito
     * @param history historico
     * @param value valor
     * @return
     */
    private static String getImportRow(String enterprise, String date, String debit, String credit, String history, String value) {
        StringBuilder row = new StringBuilder();

        row.append(enterprise).append(";");
        row.append("").append(";"); //part credit
        row.append("").append(";"); //part debit
        row.append(date).append(";"); //part debit
        row.append(debit).append(";");
        row.append(credit).append(";");
        row.append("").append(";"); //documento
        row.append("80").append(";"); //historico padrao
        row.append(history).append(";");
        row.append(value);

        return row.toString();
    }
}
