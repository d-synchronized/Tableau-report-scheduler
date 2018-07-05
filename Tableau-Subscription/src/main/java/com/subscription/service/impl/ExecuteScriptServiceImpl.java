package com.subscription.service.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.subscription.service.ExecuteScriptService;

@Service
public class ExecuteScriptServiceImpl implements ExecuteScriptService {

  private final Logger log = LoggerFactory.getLogger(this.getClass());

  public static final String OS = System.getProperty("os.name").toLowerCase();

  @Value("${tableau.location.path}")
  private String tableauInstallationPath;

  @Value("${tableau.server.url}")
  private String tableauServerUrl;

  @Value("${tableau.server.username}")
  private String tableauServerUsername;

  @Value("${tableau.server.password}")
  private String tableauServerPassword;



  @Override
  public void executeTabAdminExport(String url, String exportFile) {

    File scriptFile = generateTabAdminScriptFile(url, exportFile);

    if (scriptFile != null) {

      executeCommand(scriptFile);

      Path filePath = Paths.get(scriptFile.getAbsolutePath());

      try {
        Files.delete(filePath);
      } catch (IOException e) {
        log.error("executeTabAdminExport: ", e);
      }
    }

  }


  private File generateTabAdminScriptFile(String url, String fileToExport) {
    String scriptText = getTextFromScriptFile();

    File tempFile = createTempScriptFile();
    if (tempFile == null)
      return null;

    try {

      FileUtils.writeStringToFile(tempFile, replaceScriptParams(scriptText, url, fileToExport),
          StandardCharsets.UTF_8);

      return tempFile;


    } catch (IOException e) {
      log.error("generateTabAdminScript: ", e);
      return null;
    }
  }

  private String getTextFromScriptFile() {
    try (InputStream inputStream = this.getClass().getResourceAsStream("/script.txt")) {

      return IOUtils.toString(inputStream, StandardCharsets.UTF_8);

    } catch (IOException e) {
      log.error("getTextFromScriptFile: ", e);
      return "";
    }
  }

  private String replaceScriptParams(String originalString, String url, String filePath) {
    String newFileContext = originalString.replace("{SERVER_URL}", tableauServerUrl);
    newFileContext = newFileContext.replace("{SITE_NAME}", "PROD");
    newFileContext = newFileContext.replace("{USERNAME}", tableauServerUsername);
    newFileContext = newFileContext.replace("{PASSWORD}", tableauServerPassword);
    newFileContext = newFileContext.replace("{URL}", url);
    newFileContext = newFileContext.replace("{FILE_NAME}", filePath);
    return newFileContext;
  }

  private File createTempScriptFile() {

    try {

      String extension = "";
      if (OS.indexOf("win") >= 0) {
        extension = ".bat";
      } else {
        extension = ".sh";
      }

      return File.createTempFile("script", extension,
          new File(System.getProperty("java.io.tmpdir")));

    } catch (IOException e) {
      log.error("createTempScriptFile: ", e);
      return null;
    }
  }

  private void executeCommand(File scriptFile) {


    ProcessBuilder builder = new ProcessBuilder(scriptFile.getAbsolutePath());
    builder.directory(new File(tableauInstallationPath));

    Process p;
    try {
      p = builder.start();
      p.waitFor();

    } catch (Exception e) {
      log.error("executeCommand:", e);
    }

  }


}
