package com.subscription.util;
import java.util.Date;
import org.quartz.CronScheduleBuilder;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;

public class ScheduleJob {

  private ScheduleJob() {
    super();
  }

  /**
   * @param scheduler
   * @param jobName
   * @throws SchedulerException
   */
  public static void deleteJob(Scheduler scheduler, String jobName) throws SchedulerException {
    JobKey jobKey = new JobKey(jobName);
    scheduler.deleteJob(jobKey);
  }

  /**
   * @param job
   * @param jobName
   * @param jobDescription
   * @return
   */
  public static JobDetail getJobDetail(Job job, String jobName, String jobDescription) {
    return JobBuilder.newJob().ofType(job.getClass()).storeDurably().withIdentity(jobName)
        .withDescription(jobDescription).build();
  }

  /**
   * @param jobName
   * @param jobDescription
   * @param startDate
   * @param endDate
   * @param cronExpression
   * @return
   */
  public static Trigger getTrigger(String jobName, Date startDate, Date endDate,
      String cronExpression) {
    // Create trigger
    Trigger trigger;

    // If cron expression is null, schedule job only once based on startDate
    if (cronExpression != null) {
      trigger =
          TriggerBuilder
          .newTrigger()
          .withIdentity(jobName)
          .startAt(startDate)
          .endAt(endDate)
          .withSchedule(
              CronScheduleBuilder.cronSchedule(cronExpression)
              .withMisfireHandlingInstructionDoNothing()).build();
    } else {
      trigger =
          TriggerBuilder
          .newTrigger()
          .withIdentity(jobName)
          .startAt(startDate)
          .endAt(endDate)
          .withSchedule(
              SimpleScheduleBuilder.simpleSchedule()
              .withMisfireHandlingInstructionNextWithRemainingCount()).build();
    }

    return trigger;
  }

  /**
   * @param scheduler
   * @param jobDetail
   * @param trigger
   * @throws SchedulerException
   */
  public static void scheduleJob(Scheduler scheduler, JobDetail jobDetail, Trigger trigger)
      throws SchedulerException {

    // Schedule Job
    scheduler.scheduleJob(jobDetail, trigger);
    // Start schedule
    scheduler.start();

  }


}
