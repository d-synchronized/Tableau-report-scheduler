package com.subscription.service.task;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.util.FileSystemUtils;
import com.subscription.service.ExecuteScriptService;
import com.subscription.service.TableauMailService;

public class TaskExport implements Job {

  private final Logger log = LoggerFactory.getLogger(this.getClass());

  @Autowired
  private ApplicationContext applicationContext;

  TableauMailService tableauMailService;

  ExecuteScriptService executeScriptService;


  @Override
  public void execute(JobExecutionContext context) throws JobExecutionException {

    tableauMailService = applicationContext.getBean(TableauMailService.class);
    executeScriptService = applicationContext.getBean(ExecuteScriptService.class);

    String url = context.getJobDetail().getJobDataMap().getString("url");

    String name = context.getJobDetail().getJobDataMap().getString("name");

    String emails = context.getJobDetail().getJobDataMap().getString("emails");

    String description = context.getJobDetail().getJobDataMap().getString("description");

    log.info("Running subscription: {}", url);

    try {
      // Temp directory to hold exported file
      Path exportedFileLocation = Files.createTempDirectory("exported_subscription_");

      // File to export name
      String exportedFilePath = exportedFileLocation.toAbsolutePath() + File.separator + name;

      log.info("exportedFileLocation: {}", exportedFilePath);

      // Execute tabadmin export
      executeScriptService.executeTabAdminExport(url, exportedFilePath);

      // If file has been created from tabadmin, send mail
      if (exportedFileLocation.toFile().listFiles().length > 0) {
        File exportedFile = exportedFileLocation.toFile().listFiles()[0];

        tableauMailService.send(emails, name, description, exportedFile.getAbsolutePath());
      }

      // Delete temp directory
      FileSystemUtils.deleteRecursively(exportedFileLocation);

    } catch (IOException e) {
      log.error("TaskExport.execute:", e);
    }

    log.info("Finished running subscription. ");

  }

}
