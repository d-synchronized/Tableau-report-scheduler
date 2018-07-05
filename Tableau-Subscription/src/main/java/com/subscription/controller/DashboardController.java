package com.subscription.controller;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.subscription.service.DashboardsConfigService;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

  @Autowired
  private DashboardsConfigService dashboardsConfigService;


  @RequestMapping("/getNotTableauParams")
  public List<String> getNotTableauParams() {
    return dashboardsConfigService.getNotTableauParams();
  }

  @RequestMapping("/getDashboards")
  public Map<String, Map<String, String>> getDashboards() {
    return dashboardsConfigService.getConfigFromFile();
  }

  @RequestMapping(value = "/saveDashboard", method = RequestMethod.POST)
  public void saveDashboard(@RequestBody Map<String, String> data,
      @RequestParam(required = false) String name) {

    dashboardsConfigService.saveDashboard(data, name);

  }

  @RequestMapping(value = "/deactivateDashboard", method = RequestMethod.POST)
  public void deactivateDashboard(@RequestBody Map<String, String> data,
      @RequestParam String name) {

    dashboardsConfigService.deactivateDashboard(data, name);

  }

  @RequestMapping(value = "/deleteDashboard", method = RequestMethod.DELETE)
  public void deleteDashboard(@RequestParam String name) {

    dashboardsConfigService.deleteDashboard(name);
  }



}
