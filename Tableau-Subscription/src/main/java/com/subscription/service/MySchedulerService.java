package com.subscription.service;

import java.time.LocalDate;
import org.springframework.web.util.UriComponentsBuilder;

public interface MySchedulerService {

  void scheduleTask(String key, String name, String description, String emails,
      UriComponentsBuilder url, String cron, LocalDate dateStart, LocalDate dateEnd);

  void unScheduleTask(String id);

}
