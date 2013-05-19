package test

import org.specs2.mutable._
import play.api.test._
import play.api.test.Helpers._
import play.api.http.HeaderNames

/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 * For more information, consult the wiki.
 */
class ApplicationSpec extends Specification {
  
  "Application" should {
    
    "send 404 on a bad request" in {
      running(FakeApplication()) {
        route(FakeRequest(GET, "/boum")) must beNone        
      }
    }
    
    "render the index page" in {
      running(FakeApplication()) {
        val home = route(FakeRequest(GET, "/")).get
        
        status(home) must equalTo(OK)
        contentType(home) must beSome.which(_ == "text/html")
        contentAsString(home) must contain ("Your new application is ready.")
      }
    }
    
    
    "send gather" in {
      testUrlEncoded("/twiml", gatherResponse, ("From", "123-456-7890"))
    }
      
    "send dial" in {
      testUrlEncoded("/handleKey", dialResponse, ("From", "123-456-7890"), ("Digits", "1"))
    }
    
    "send record" in {
      testUrlEncoded("/handleKey", recordResponse, ("From", "123-456-7890"), ("Digits", "2"))
    } 
    
    "send hangup" in {
      testUrlEncoded("/handleKey", hangupResponse, ("From", "123-456-7890"), ("Digits", "4"))
    } 
  }
  
  val gatherResponse = 
      """<?xml version="1.0" encoding="UTF-8"?>
<Response>
  <Gather numDigits="1" action="/handleKey" method="POST">
    <!--Play>welcome.mp3</Play-->
 	<Say voice="woman">
 	  Thank you for calling NTSYS, your IT department. Please press 1 to talk to our representative, 2 to leve a message, or any other key to hangup.
 	</Say>
  </Gather>
</Response>"""
    
    val dialResponse = 
      """<?xml version="1.0" encoding="UTF-8"?>
<Response>
  <Dial>604-773-0275</Dial>
  <Say voice="woman">Goodbye</Say>
</Response>""" 
      
    val recordResponse = 
      """<?xml version="1.0" encoding="UTF-8"?>
<Response>
  <Say voice="woman">Please leave a message after the tone.</Say>
  <Record maxLength="60" />
</Response>"""
      
    val hangupResponse = 
      """<?xml version="1.0" encoding="UTF-8"?>
<Response>
  <Hangup/>
</Response>""" 
  
  private def testUrlEncoded(url: String, response: String, body: (String, String)*): Unit =
  {
      running(FakeApplication()) {        
        val home = route(
            FakeRequest(POST, url)
            .withFormUrlEncodedBody(body:_*)
        ).get
      
        status(home) must equalTo(OK)
        contentType(home) must beSome.which(_ == "application/xml")
        contentAsString(home).trim must equalTo(response.trim)
      }     
  }
}