package controllers

import play.api._
import play.api.mvc._

object Application extends Controller {
  
  val appCfg = Play.current.configuration
  
  val gatherMessageDef = "Thank you for calling NTSYS, your IT department. " +
  		"Please press 1 to talk to our representative, " +
  		"2 to leve a message, or any other key to hangup."
  		
  val gatherMessage = appCfg.getString("twilio.gatherMessage").getOrElse(gatherMessageDef)
  
  val recordMessage = appCfg.getString("twilio.recordMessage")
              .getOrElse("Please leave a message after the tone.")

  val defaultNumber = appCfg.getString("twilio.forwardNumber").getOrElse("604-773-0275")
  
  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }
  
  def twimlG = Action { implicit request =>
    Logger.debug("httprequest query string " + request.queryString)
    val from = request.queryString.get("From").flatMap(_.headOption)
    Logger.info("Receiving call from: " + from)
    gather(gatherMessage)
  }
  
  def twimlP = Action { implicit request => 
    Logger.debug("httprequest body: " + request.body)
    val from = request.body.asFormUrlEncoded.get.get("From").getOrElse(List("No One")).head
    Logger.info("Receiving call from: " + from)
    gather(gatherMessage)
  }
  
  def handleKeyP = Action { implicit request => 
    Logger.debug("httprequest body: " + request.body)
    val form = request.body.asFormUrlEncoded.get
    val from = form.get("From").getOrElse(List("No One")).head
    val keys = form.get("Digits").getOrElse(List("No Digit")).head.trim
    Logger.info("handling key entry from: " + from + ", keys:" + keys)
    
    val response = 
      if(keys == "1") {
        Logger.info("dialing..." + defaultNumber)
        dial(defaultNumber)
      }
      else if (keys == "2") {
        Logger.info("recording a message")
        record(recordMessage)
      }
      else {
        Logger.info("hanging up")
        hangup
      }
      
    response
  }
  
  private def dial(to: String) = Ok(views.xml.dial(to)).as("application/xml")
  
  private def say(message: String) = Ok(views.xml.hello(message)).as("application/xml")
 
  private def gather(message: String) = Ok(views.xml.gather(message)).as("application/xml")
   
  private def record(message: String) = Ok(views.xml.record(message)).as("application/xml")
  
  private def hangup() = Ok(views.xml.hangup()).as("application/xml")
  
}