package com.subscription.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import com.subscription.service.DashboardsConfigService;

@Component
public class ApplicationStartup implements ApplicationListener<ApplicationReadyEvent> {

  @Autowired
  private DashboardsConfigService dashboardsConfigService;

  /**
   * This event is executed as late as conceivably possible to indicate that the application is
   * ready to service requests.
   */
  @Override
  public void onApplicationEvent(final ApplicationReadyEvent event) {

    dashboardsConfigService.initAllDashboardSchedules();
  }

}
