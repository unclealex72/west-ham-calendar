package sms

/**
  * A class used to store the SMS authentication token and target phone numbers for errors.
  * Created by alex on 01/03/16.
  */
case class SmsConfiguration(authenticationToken: String, phoneNumbers: Seq[String])
