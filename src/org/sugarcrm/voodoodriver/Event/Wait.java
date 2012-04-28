/*
 * Copyright 2012 SugarCRM Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License.  You
 * may may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  Please see the License for the specific language
 * governing permissions and limitations under the License.
 */

package org.sugarcrm.voodoodriver.Event;

import org.sugarcrm.voodoodriver.VDDException;
import org.w3c.dom.Element;

/**
 * The wait event.
 *
 * @author Jon duSaint
 */

public class Wait extends Event {

   /**
    * The default timeout in seconds.  This should probably be
    * specified elsewhere.
    */

   static final int DEFAULT_TIMEOUT = 5;


   /**
    * Instantiate a wait event.
    * @param event  the event as specified in the test script
    * @throws VDDException if event instantiation fails
    */

   public Wait(Element event) throws VDDException {
      super(event);
   }


   /**
    * Run the wait event.
    *
    * @throws VDDException if execution is unsuccessful
    */

   public void execute() throws VDDException {
      int timeout = DEFAULT_TIMEOUT;
      boolean useDefault = true;

      if (this.actions.containsKey("timeout")) {
         try {
            timeout = new Integer((String)this.actions.get("timeout"));
            if (timeout < 0) {
               this.eventLoop.report.Warn("wait timeout < 0, using 0");
               timeout = 0;
            } else {
               this.eventLoop.report.Log("Setting timeout to " + timeout + "s");
            }
            useDefault = false;
         } catch (NullPointerException e) {
            this.eventLoop.report.ReportError("Null timeout value???");
         } catch (NumberFormatException e) {
            this.eventLoop.report.ReportError("Specified wait timeout '" +
                                              this.actions.get("timeout") +
                                              "' is not a valid integer");
         }
      } else if (this.actions.containsKey("condition")) {
         this.eventLoop.report.Warn("Bug 52537: condition attribute is not " +
                                    "implemented.");
      }

      if (useDefault) {
         this.eventLoop.report.Log("Using default timeout of " + timeout + "s");
      }


      while (!this.eventLoop.isStopped() && timeout-- > 0) {
         try {
            Thread.sleep(1000);
         } catch (InterruptedException e) {
            this.eventLoop.report.Log("wait interrupted");
            break;
         }
      }
   }
}