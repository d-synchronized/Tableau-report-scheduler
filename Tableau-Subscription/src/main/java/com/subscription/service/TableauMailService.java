package com.subscription.service;

public interface TableauMailService {

  void send(String recipitents, String subject, String bodyText, String filePath);
}
