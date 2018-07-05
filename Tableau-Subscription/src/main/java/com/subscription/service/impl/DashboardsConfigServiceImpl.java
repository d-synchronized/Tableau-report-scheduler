package com.subscription.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import com.subscription.service.DashboardsConfigService;
import com.subscription.service.MySchedulerService;

@Service
public class DashboardsConfigServiceImpl implements DashboardsConfigService {

  private final Logger log = LoggerFactory.getLogger(this.getClass());

  @Autowired
  private MySchedulerService myScheduler;

  private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
  private static final DateTimeFormatter formatterTableau =
      DateTimeFormatter.ofPattern("yyyy-MM-dd");

  public static final String START_DATE = "startScheduleDate";
  public static final String END_DATE = "endScheduleDate";
  public static final String URL = "url";
  public static final String CRON_VAL = "cronVal";
  public static final String SUBSCRIPTION_NAME = "subscriptionName";
  public static final String ACTIVATE = "activate";
  public static final String TRUE = "true";
  public static final String SITE = "site";
  public static final String EMAILS = "emails";
  public static final String DESCRIPTION = "description";

  public static final List<String> notTableauParams =
      Collections.unmodifiableList(Arrays.asList(START_DATE, END_DATE, URL, CRON_VAL,
          SUBSCRIPTION_NAME, ACTIVATE, DESCRIPTION, EMAILS, SITE));

  @Value("${dashboard.config.file.path:NONE}")
  private String filePath;

  @Override
  public List<String> getNotTableauParams() {
    return notTableauParams;
  }

  @Override
  public Map<String, Map<String, String>> getConfigFromFile() {

    Yaml yaml = new Yaml();
    File directory = new File(filePath);
    String[] extensions = new String[] {"yaml"};
    Map<String, Map<String, String>> myConfigData = new LinkedHashMap<>();

    // get dashboard config files
    List<File> fileList = (List<File>) FileUtils.listFiles(directory, extensions, true);

    // sort files by last modified date
    fileList.sort((File f1, File f2) -> Long.compare(f2.lastModified(), f1.lastModified()));

    // Add files to map with filename as key
    fileList.forEach(file -> {
      try (InputStream ios = new FileInputStream(file)) {

        myConfigData.put(file.getName(), yaml.load(ios));

      } catch (FileNotFoundException e) {
        log.error(file.getName() + " config file not found:", e);

      } catch (IOException e) {
        log.error("getConfigFromFile: ", e);
      }

    });

    return myConfigData;

  }

  @Override
  public File writeObjectToFile(Map<String, String> data) {
    File repository = new File(filePath);

    DumperOptions options = new DumperOptions();
    options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
    options.setPrettyFlow(true);
    Yaml yaml = new Yaml(options);

    try {
      File dataFile = File.createTempFile("dashboard_", ".yaml", repository);
      FileWriter writer = new FileWriter(dataFile);
      yaml.dump(data, writer);
      writer.close();

      return dataFile;
    } catch (IOException e) {
      log.error("writeObjectToFile: ", e);
      return null;
    }

  }

  @Override
  public void deleteFileByFileName(String fileName) {
    File directory = new File(filePath);
    String[] extensions = new String[] {"yaml"};
    Collection<File> fileList = FileUtils.listFiles(directory, extensions, true);

    Optional<File> value =
        fileList.stream().filter(file -> file.getName().equals(fileName)).findFirst();

    if (value.isPresent()) {
      Path path = Paths.get(value.get().getAbsolutePath());
      try {
        Files.delete(path);
      } catch (IOException e) {
        log.error("deleteFileByFileName: ", e);
      }
    }

  }

  @Override
  public void saveDashboard(Map<String, String> data, String name) {
    // If name is not null, task is an existing one
    if (name != null) {
      deleteFileByFileName(name);
      myScheduler.unScheduleTask(name);
    }

    // Save params to file
    File dataFile = writeObjectToFile(data);

    // Schedule task
    myScheduler.scheduleTask(dataFile.getName(), data.get(SUBSCRIPTION_NAME), data.get(DESCRIPTION),
        data.get(EMAILS), buildUrlFromParams(data), data.get(CRON_VAL),
        LocalDate.parse(data.get(START_DATE), formatter),
        LocalDate.parse(data.get(END_DATE), formatter));

  }

  @Override
  public void deactivateDashboard(Map<String, String> data, String name) {
    // delete file by name
    deleteFileByFileName(name);

    // Unscheduled task by name
    myScheduler.unScheduleTask(name);

    // Create new dashboard config file
    writeObjectToFile(data);

  }

  @Override
  public void deleteDashboard(String name) {
    // delete file by name
    deleteFileByFileName(name);

    // Unscheduled task by name
    myScheduler.unScheduleTask(name);

  }

  @Override
  public String buildUrlFromParams(Map<String, String> data) {

    String url = data.get(URL);
    UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url);

    data.forEach((key, value) -> {
      if (!notTableauParams.contains(key)) {
        checkIfRangeDateParam(key, value, builder);
      }
    });


    if (ExecuteScriptServiceImpl.OS.indexOf("win") >= 0) {

      return builder.build().toUriString().replaceAll(" ", "%%20");

    } else {

      return builder.build().toUri().toString();
    }

  }

  private void checkIfRangeDateParam(String key, String value, UriComponentsBuilder builder) {
    if (key.contains("[RANGE]:")) {

      String[] nameArray = key.split(":");
      String[] valueArray = value.split(":");

      String startDateName = nameArray[1];
      String endDateName = nameArray[2];

      int startDateValue = Integer.parseInt(valueArray[0]);
      String endDateValue = valueArray[1];

      LocalDate startDate = LocalDate.now();
      LocalDate endDate = LocalDate.now();

      switch (endDateValue) {
        case "Current Year":
          startDate = endDate.minusYears(startDateValue);
          break;
        case "Current Month":
          startDate = endDate.minusMonths(startDateValue);
          break;
        case "Current Week":
          startDate = endDate.minusWeeks(startDateValue);
          break;
        case "Current Date":
          startDate = endDate.minusDays(startDateValue);
          break;
        default:
          log.info("Invalid end date");
      }

      builder.queryParam(startDateName, startDate.format(formatterTableau));
      builder.queryParam(endDateName, endDate.format(formatterTableau));


    } else {
      builder.queryParam(key, value);
    }
  }

  @Override
  public void initAllDashboardSchedules() {

    // get config files
    getConfigFromFile().forEach((key, value) -> {


      if (value.get(URL) != null && value.get(CRON_VAL) != null && value.get(START_DATE) != null
          && value.get(END_DATE) != null && TRUE.equals(value.get(ACTIVATE))) {

        try {
          // Schedule task
          myScheduler.scheduleTask(key, value.get(SUBSCRIPTION_NAME), value.get(DESCRIPTION),
              value.get(EMAILS), buildUrlFromParams(value), value.get(CRON_VAL),
              LocalDate.parse(value.get(START_DATE), formatter),
              LocalDate.parse(value.get(END_DATE), formatter));

        } catch (RuntimeException e) {
          log.error("initAllDashboardSchedules:", e);
        }

      }

    });

  }


}
