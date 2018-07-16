package com.subscription.service;

import java.io.File;
import java.util.List;
import java.util.Map;
import org.springframework.web.util.UriComponentsBuilder;

public interface DashboardsConfigService {

  Map<String, Map<String, String>> getConfigFromFile();

  File writeObjectToFile(Map<String, String> data);

  void deleteFileByFileName(String fileName);

  void saveDashboard(Map<String, String> data, String name);

  void deactivateDashboard(Map<String, String> data, String name);

  void deleteDashboard(String name);

  void initAllDashboardSchedules();

  UriComponentsBuilder buildUrlFromParams(Map<String, String> data);

  List<String> getNotTableauParams();


}
