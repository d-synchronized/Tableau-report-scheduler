package com.subscription.TableauSubscription;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.util.UriComponentsBuilder;
import com.subscription.service.DashboardsConfigService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TableauSubscriptionApplicationTests {

  @Autowired
  DashboardsConfigService dashboardConfig;

  final static List<String> notTableauParams =
      new ArrayList<>(Arrays.asList("startDate", "endDate", "url", "cronVal", "subscriptionName"));

  @Test
  public void testGetDashboardConfigFile() {

    // Object obj = dashboardConfig.getConfigFromFile();
    // System.out.println(obj);

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    LocalDate date = LocalDate.parse("30/06/2018", formatter);
    System.out.println(date);

    Map<Object, Object> data = new HashMap<>();
    data.put("url", "dashboard/TestDash");
    data.put("startDate", "01/01/2018");
    data.put("height", "4500");
    data.put("endDate", "30/06/2018");
    data.put("cronVal", "25 5 3 * *");
    data.put("subscriptionName", "My nAme");

    data.put("width 11", "2000");

    UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(data.get("url").toString());


    data.forEach((key, value) -> {

      if (!notTableauParams.contains(key)) {
        builder.queryParam(key.toString(), value.toString());
      }
    });

    System.out.println(builder.build().toUri().toString());

    // dashboardConfig.writeObjectToFile(data);


    // dashboardConfig.deleteFileByFileName("dashboard_2023277001645645882 - Copy.yaml");

  }

}
