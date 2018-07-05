package com.subscription.service;

import java.time.LocalDate;

public interface MySchedulerService {

  void scheduleTask(String key, String name, String description, String emails, String url,
      String cron, LocalDate dateStart, LocalDate dateEnd);

  void unScheduleTask(String id);

}
